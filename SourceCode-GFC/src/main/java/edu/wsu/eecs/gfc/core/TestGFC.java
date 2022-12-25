package edu.wsu.eecs.gfc.core;

import java.util.*;
import weka.classifiers.Classifier;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Testing of facts is done here.
 * Testing data is transformed into GFC format and stored in the list.
 * Predicted classification results are stored in a hash map.
 */
public class TestGFC {

    public List<String> testingAssertions = new ArrayList<>();
    public List<JSONObject> jsonList = new ArrayList<>();
    public LinkedHashMap<String,String> results = new  LinkedHashMap<>();

    private static ParseTriple parse = new ParseTriple();

    /**
     * Test method
     * @param miner
     * @param Models
     * @param inputDir
     * @param outputPath
     * @return
     * @throws Exception
     */
    public LinkedHashMap<String,String> test(RuleMiner<String, String> miner,Map<String, Classifier> Models,String inputDir,String outputPath) 
                            throws Exception {

        List<Relation<String, String>> relationList = IO.loadRelations(inputDir,this.testingAssertions);
        
        for (Relation<String, String> r : relationList) {

            FactSampler sampler = new FactSampler(inputDir);
            sampler.extract_testing_asserions(inputDir,r,this.testingAssertions);

            List<OGFCRule<String, String>> patternsTest = miner.OGFC_stream(r, sampler.getDataTest().get(true), sampler.getDataTest().get(false));

            LinkedHashMap<String, String> predictionsR = FactChecker.Test_LRModel(r, patternsTest, Models,sampler.getDataTest(), outputPath);
            
            for(int i=0; i<testingAssertions.size(); i++){
                for(Map.Entry<String,String> e:predictionsR.entrySet()){
                    if(testingAssertions.get(i).contains(e.getKey())){
                        JSONObject AssertionJson = this.jsonList.get(i);
                        String assertion = AssertionJson.getString("subject") + " " + AssertionJson.getString("predicate") + " " + AssertionJson.getString("object");
                        String score = e.getValue();
                        this.results.put(assertion,score);
                    }
                }                
            }
        }
        return this.results;
   }

   public void addTestingData(String sub, String pred, String obj, String truthVal) throws JSONException{

    JSONObject rdf = new JSONObject();
    rdf.put("subject",sub);rdf.put("predicate",pred);rdf.put("object",obj);rdf.put("score",truthVal);
    this.jsonList.add(rdf);

    PreProcessedTriple preprocessedTriple = parse.preprocessTriple(sub,obj,pred,truthVal);
    String parsedAssertion = parse.parseTriples(preprocessedTriple.subject(),preprocessedTriple.object(),preprocessedTriple.predicate(),preprocessedTriple.truthValue());
    this.testingAssertions.add(parsedAssertion);
}
}
