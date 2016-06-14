package com.smoitra;

import java.util.*;

public class Main {

    private static List<Integer> trainingData = new ArrayList<Integer>(); //index of all training samples
    private static List<Integer> testData = new ArrayList<Integer>(); // index of all test samples
    private static List<Record> allData;
    private static ArrayList<Attribute> attributes;

    public static void crossValidation(int k, String filename, String attrib_file) {

        System.out.println("Creating Forest of " + k + " trees.");
        LoadData.loadAllData(filename, attrib_file);
        allData = LoadData.allData;
        attributes = LoadData.attributes;

        List<Record> myTrainingSamples =  new ArrayList<Record>();
        List<Record> myTestingSamples =  new ArrayList<Record>();

        HashMap<Integer, List<Integer>> kgroup = new HashMap<Integer, List<Integer>>();
//        System.out.println(attributes);
        for (int i = 0; i < allData.size(); i++) {

            if (!kgroup.containsKey(i % k)) {
                kgroup.put(i % k, new ArrayList<Integer>());
            }
            kgroup.get((i % k)).add(i);
        }

        double[] myAccuracy = new double[10];
        double sum = 0.0;
        for (int i = 0; i < k; i++) {
            testData.clear();
            trainingData.clear();
            for (int j = 0; j < k; j++) {
                if (j == i) {
                    testData.addAll(kgroup.get(j));
                } else {
                    trainingData.addAll(kgroup.get(j));
                }
            }
            myTestingSamples.clear();
            myTrainingSamples.clear();
            System.out.println(trainingData);
            System.out.println(testData);


            for(Integer trd : trainingData) {
                myTrainingSamples.add(allData.get(trd));
            }

            System.out.println(myTestingSamples.size());

            for(Integer ted : testData) {
                myTestingSamples.add(allData.get(ted));
            }

            Forest forest = new Forest(10, filename, myTrainingSamples, attributes);

            double accuracy = forest.getPrediction(myTestingSamples);

            myAccuracy[i] =  accuracy;
        }

        for (Double myacr : myAccuracy) {
            sum +=  myacr;
        }

        System.out.println("Average acuracy of the 10 fold trees is " + sum / k);
    }

	public static void main(String[] args) {
        String dataFile = "/home/smoitra/IdeaProjects/MyRandomForest/data/iris/train.txt";
        String testFile = "/home/smoitra/IdeaProjects/MyRandomForest/data/iris/test.txt";
        String attribFile = "/home/smoitra/IdeaProjects/MyRandomForest/data/iris/attribute.txt";
        int no_of_forest = 10;

        crossValidation(10, dataFile, attribFile);

    }
}
