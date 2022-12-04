package edu.wsu.eecs.gfc.core;

import java.util.*;
import weka.classifiers.Classifier;

import org.json.JSONException;
import org.json.JSONObject;

public class TestGFC {

    public List<String> testingAssertions = new ArrayList<>();
    public List<JSONObject> jsonList = new ArrayList<>();

    private static ParseTriple parse = new ParseTriple();

    public List<JSONObject> test(RuleMiner<String, String> miner,Map<String, Classifier> Models,String inputDir,String outputPath) 
                            throws Exception {

        List<Relation<String, String>> relationList = IO.loadRelations(inputDir);
        
        for (Relation<String, String> r : relationList) {
        // System.out.println("FactChecker: OFact_R: "
        //             + FactChecker.predictByHits(patternList, dataTest));
            // String rName = r.srcLabel() + "_" + r.edgeLabel() + "_" + r.dstLabel();
            // List<OGFCRule<String, String>> patternList = Patterns.get(rName);

            FactSampler sampler = new FactSampler(inputDir);
            sampler.extract_testing_asserions(inputDir,r,this.testingAssertions);

            List<OGFCRule<String, String>> patternsTest = miner.OGFC_stream(r, sampler.getDataTest().get(true), sampler.getDataTest().get(false));

            LinkedHashMap<String, String> predictionsR = FactChecker.Test_LRModel(r, patternsTest, Models,sampler.getDataTest(), outputPath);
            
            for(int i=0; i<testingAssertions.size(); i++){
                for(Map.Entry<String,String> e:predictionsR.entrySet()){
                    if(testingAssertions.get(i).contains(e.getKey())){
                        this.jsonList.get(i).put("score",e.getValue());
                    }
                }                
            }
        }
        return this.jsonList;
        
   }

   public void addTestingData(String sub, String pred, String obj, String truthVal) throws JSONException{

    JSONObject rdf = new JSONObject();
    rdf.put("subject",sub);rdf.put("predicate",pred);rdf.put("object",obj);rdf.put("score",truthVal);
    this.jsonList.add(rdf);
    // String assertion = sub +" " + pred +" " + obj +" ";
    PreProcessedTriple preprocessedTriple = parse.preprocessTriple(sub,obj,pred,truthVal);
    String parsedAssertion = parse.parseTriples(preprocessedTriple.subject(),preprocessedTriple.object(),preprocessedTriple.predicate(),preprocessedTriple.truthValue());
    this.testingAssertions.add(parsedAssertion);
}
}
