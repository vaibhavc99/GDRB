package edu.wsu.eecs.gfc.core;

import java.util.*;

import weka.classifiers.Classifier;

public class TestGFC {

    public List<String> testingAssertions = new ArrayList<>();

    public void test(RuleMiner<String, String> miner,Map<String, Classifier> Models,String inputDir,String outputPath) 
                            throws Exception {

        List<Relation<String, String>> relationList = IO.loadRelations(inputDir);
        
        for (Relation<String, String> r : relationList) {
        // System.out.println("FactChecker: OFact_R: "
        //             + FactChecker.predictByHits(patternList, dataTest));
            // String rName = r.srcLabel() + "_" + r.edgeLabel() + "_" + r.dstLabel();
            // List<OGFCRule<String, String>> patternList = Patterns.get(rName);

            FactSampler sampler = new FactSampler(inputDir);
            sampler.extract_testing_asserions(inputDir,r);

            List<OGFCRule<String, String>> patternsTest = miner.OGFC_stream(r, sampler.getDataTest().get(true), sampler.getDataTest().get(false));

            System.out.println("\nModel Evaluation: \n"+"\nAccuracy\tPrecision\tRecall\tFMeasure\n"
            + FactChecker.Test_LRModel(r, patternsTest, Models,sampler.getDataTest(), outputPath));
        }
   }

   public void addTestingData(String sub, String pred, String obj){

    String assertion = sub +" " + pred +" " + obj +" ";
    this.testingAssertions.add(assertion);
    // TODO: Conversion of RDF triples to GFC assersion format 
}
}
