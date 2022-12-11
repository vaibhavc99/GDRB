package edu.wsu.eecs.gfc.exps;
import edu.wsu.eecs.gfc.core.*;

import java.io.*;
import java.util.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * The caller to test GFC mining.
 * @author Peng Lin penglin03@gmail.com
 */
public class GFC {

    private static final Logger log = LoggerFactory.getLogger(GFC.class);
    private static TrainGFC training = new TrainGFC();
    private static TestGFC testing = new TestGFC();
    public static LinkedHashMap<String,String> results = new  LinkedHashMap<>();

    public static void listen(int port,String inputDir,String outputDir,double minSupp,double minConf,int maxSize,int topK) throws Exception {

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while(true) {
                log.info("Waiting for connection on port ",port);
                Socket client = serverSocket.accept();
                log.info("Accepted connection");

                try{
                    InputStream input = client.getInputStream();
                    DataOutputStream outputStream  = new DataOutputStream(client.getOutputStream());
                    DataInputStream dis = new DataInputStream(input);
                    byte[] buffer = new byte[1024];
                    while(true) {
                        log.info("Waiting for a request");
                        dis.read(buffer);
                        String request = new String(buffer, StandardCharsets.UTF_8).trim();

                        if(request.equals(null)){
                            log.info("Connection closed");
                            client.close();
                            dis.close();
                            return;
                        }
                        log.info("Request received: " + request);

                        JSONObject requestJSON = new JSONObject(request);

                        if ((requestJSON.getString("type").equals("call")) && (requestJSON.getString("content").equals("type"))) {
                            JSONObject response = new JSONObject();
                            response.put("type","type_response");
                            response.put("content","supervised");
                            outputStream.write(response.toString().getBytes(StandardCharsets.UTF_8)); 
                        }

                        if ((requestJSON.getString("type").equals("call")) && (requestJSON.getString("content").equals("training_start"))) {
                            JSONObject response = new JSONObject();
                            response.put("type","ack");
                            response.put("content","training_start_ack");
                            outputStream.write(response.toString().getBytes(StandardCharsets.UTF_8));
                        }

                        if ((requestJSON.getString("type").equals("train"))) {
                            training.addTrainingData(requestJSON.getString("subject"),requestJSON.getString("predicate"),requestJSON.getString("object"),requestJSON.getString("score"));
                            JSONObject response = new JSONObject();
                            response.put("type","ack");
                            response.put("content","train_ack");
                            outputStream.write(response.toString().getBytes(StandardCharsets.UTF_8));
                            log.info("Request answered");
                            continue;
                        }
                        
                        if ((requestJSON.getString("type").equals("call")) && (requestJSON.getString("content").equals("training_complete"))) {
                            training.train(inputDir,outputDir,minSupp,minConf,maxSize,topK);
                            JSONObject response = new JSONObject();
                            response.put("type","ack");
                            response.put("content","training_complete_ack");
                            outputStream.write(response.toString().getBytes(StandardCharsets.UTF_8)); 
                        }

                        if ((requestJSON.getString("type").equals("call")) && (requestJSON.getString("content").equals("testing_start"))) {
                            JSONObject response = new JSONObject();
                            response.put("type","ack");
                            response.put("content","testing_start_ack");
                            outputStream.write(response.toString().getBytes(StandardCharsets.UTF_8));
                        }

                        if ((requestJSON.getString("type").equals("testGFC"))) {
                            testing.addTestingData(requestJSON.getString("subject"),requestJSON.getString("predicate"),requestJSON.getString("object"),requestJSON.getString("score"));
                            JSONObject response = new JSONObject();
                            response.put("type","ack");
                            response.put("content","testGFC_ack");
                            outputStream.write(response.toString().getBytes(StandardCharsets.UTF_8));
                            log.info("Request answered");
                            continue;
                        }

                        if ((requestJSON.getString("type").equals("call")) && (requestJSON.getString("content").equals("testing_complete"))) {
                            results = testing.test(training.getMiner(),training.getModels(),inputDir,outputDir);
                            JSONObject response = new JSONObject();
                            response.put("type","ack");
                            response.put("content","testing_complete_ack");
                            outputStream.write(response.toString().getBytes(StandardCharsets.UTF_8));
                            log.info("Request answered");
                        }

                        if ((requestJSON.getString("type").equals("test"))) {
                            String requestAssertion = requestJSON.getString("subject") + " " + requestJSON.getString("predicate") + " " +
                                                    requestJSON.getString("object");
                            String score = results.get(requestAssertion);
                            JSONObject response = new JSONObject();
                            response.put("type","test_result");
                            response.put("score",score);
                            outputStream.write(response.toString().getBytes(StandardCharsets.UTF_8));
                            log.info("Request answered");
                            continue;
                        }
                        log.info("Request answered");
                    }
                }        
                catch(IOException | JSONException e){
                    e.printStackTrace();
                }
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        String inputDir = args[0];
        String outputDir = args[1];
        new File(outputDir).mkdirs();

        double minSupp = Double.parseDouble(args[2]);
        double minConf = Double.parseDouble(args[3]);
        int maxSize = Integer.parseInt(args[4]);
        int topK = Integer.parseInt(args[5]);

        listen(4011,inputDir,outputDir,minSupp,minConf,maxSize,topK);
        // TrainGFC training = new TrainGFC();
        // training.train(inputDir,outputDir,minSupp,minConf,maxSize,topK);
        // TestGFC.test(training.getMiner(),training.getModels(),inputDir,outputDir);
    }
}
