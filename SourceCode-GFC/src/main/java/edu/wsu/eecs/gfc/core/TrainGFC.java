package edu.wsu.eecs.gfc.core;

import com.google.common.base.Stopwatch;

import weka.classifiers.Classifier;

import java.io.*;
// import java.nio.file.Files;
// import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
// import com.google.gson.Gson; 

public class TrainGFC {
    private static final int GLOBAL_HOPS = 2;
    private RuleMiner<String, String> miner ;
    private Map<String, List<OGFCRule<String, String>>> Patterns ;
    private Map<String, Classifier> Models;
    public List<String> trainingAssertions = new ArrayList<>();
    private static ParseTriple parse = new ParseTriple();

    public TrainGFC() {
        
        Patterns = new HashMap<String,List<OGFCRule<String, String>>>();
        Models = new HashMap<String, Classifier>();
    }

    public Map<String, List<OGFCRule<String, String>>> getPatterns() {
        return Patterns;
    }

    public RuleMiner<String, String> getMiner() {
        return this.miner;
    }

    public Map<String, Classifier> getModels() {
        return Models;
    }

    public void train(String inputDir,String outputDir,double minSupp,double minConf,int maxSize,int topK) throws Exception {

        new File(outputDir).mkdirs();

        System.out.println("Configurations:"
                + "\nInputDir = " + inputDir
                + "\nOutputDir = " + outputDir
                + "\nminSupp = " + minSupp
                + "\nminConf = " + minConf
                + "\nmaxSize = " + maxSize
                + "\ntop-K = " + topK);

        System.out.println("Loading the data graph....");
        Graph<String, String> graph = IO.loadStringGraph(inputDir);
        System.out.println("Graph: " + graph.toSizeString());

        System.out.println("Loading the ontology....");
        DirectedAcyclicGraph<String, String> onto = IO.loadDAGOntology(inputDir);
        System.out.println("Indexing the ontology....");
        Map<String, Map<Integer, Set<String>>> ontoIndex = Utility.indexOntology(onto, GLOBAL_HOPS);

        System.out.println("Indexing the data graph....");
        GraphDatabase<String, String> bigGraph = GraphDatabase.buildFromGraph(graph, ontoIndex);
        System.out.println("BigGraph: " + bigGraph.toSizeString());

        System.out.println("Loading the input relations....");
        List<Relation<String, String>> relationList = IO.loadRelations(inputDir,this.trainingAssertions);

        this.miner = RuleMiner.createInit(bigGraph, minSupp, minConf, maxSize, topK);

        for (Relation<String, String> r : relationList) {
            System.out.println("----------------------------------------");
            System.out.println("Training for r(x, y) = " + r);
            String rName = r.srcLabel() + "_" + r.edgeLabel() + "_" + r.dstLabel();

            FactSampler sampler = new FactSampler(inputDir);
            sampler.extract_training_asserions(inputDir,r,this.trainingAssertions);

            bigGraph.buildSimLabelsMap(0);

            Stopwatch w = Stopwatch.createStarted();
            List<OGFCRule<String, String>> patterns = miner.OGFC_stream(r, sampler.getDataTrain().get(true), sampler.getDataTrain().get(false));
            w.stop();

            System.out.println("Discovered number of patterns: |P| = " + patterns.size() + ", Time = " + w.elapsed(TimeUnit.SECONDS));

            // System.out.println("\nTraining: "
            //         + FactChecker.Train_LRModel(patterns, r, sampler.getDataTrain(), outputDir));
            Classifier model = FactChecker.Train_LRModel(patterns, r, sampler.getDataTrain(), outputDir);
            Models.put(rName,model);

        }
        System.out.println("-------------------DONE-----------------");
    }

    public void addTrainingData(String sub, String pred, String obj, String truthVal){

        // String assertion = sub +" " + pred +" " + obj +" " + truthVal ;
        PreProcessedTriple preprocessedTriple = parse.preprocessTriple(sub,obj,pred,truthVal);
        String parsedAssertion = parse.parseTriples(preprocessedTriple.subject(),preprocessedTriple.object(),preprocessedTriple.predicate(),preprocessedTriple.truthValue());
        this.trainingAssertions.add(parsedAssertion); 
    }    
}
