package com.smoitra;

import java.lang.reflect.Array;
import java.util.*;

public class Main {

    private static List<Integer> trainingData = new ArrayList<Integer>(); //index of all training samples
    private static List<Integer> testData = new ArrayList<Integer>(); // index of all test samples
    private static List<Record> allData;
    private static ArrayList<Attribute> attributes;

    public static double crossValidation(int k, String filename, String attrib_file) {

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

        double[] myAccuracy = new double[k];
        double[] myAccuracy2 = new double[k];
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
//            System.out.println(trainingData);
//            System.out.println(testData);


            for(Integer trd : trainingData) {
                myTrainingSamples.add(allData.get(trd));
            }

            System.out.println(myTestingSamples.size());

            for(Integer ted : testData) {
                myTestingSamples.add(allData.get(ted));
            }

            Forest forest = new Forest(10, filename, myTrainingSamples, attributes);

            double training_accuracy = forest.getPrediction(myTrainingSamples);
            double testing_accuracy = forest.getPrediction(myTestingSamples);

            myAccuracy[i] =  testing_accuracy;
            myAccuracy2[i] =  training_accuracy;
        }

        for (Double myacr : myAccuracy2) {
            sum +=  myacr;
        }

//        System.out.println("Average training accuracy of 10 runs is " + sum / k);

        sum = 0;
        for (Double myacr : myAccuracy) {
            sum +=  myacr;
        }

//        System.out.println("Average testing accuracy of 10 runs is " + sum / k);

        return sum / k;
    }

    public static void runTenTimes(int k, String filename, String attrib_file) {
        double[] accuracy = new double[10];
        double sum = 0.0;
        for (int i = 0; i < 10 ; i ++) {
            double temp;
            temp = crossValidation(k, filename, attrib_file);
            accuracy[i] = temp;
            sum += temp;
        }
        Arrays.sort(accuracy);
        System.out.println("============================================================");
        System.out.printf("Maximum testing accuracy of 10 runs is:\t %.2f\n", accuracy[9]);
        System.out.printf("Average testing accuracy of 10 runs is:\t %.2f\n", sum / 10);
        System.out.printf("Minimum testing accuracy of 10 runs is:\t %.2f\n", accuracy[0]);
        System.out.println("============================================================");
    }

	public static void main(String[] args) {
        String dataFile = "/home/smoitra/IdeaProjects/MyRandomForest/data/iris/train.txt";
        String attribFile = "/home/smoitra/IdeaProjects/MyRandomForest/data/iris/attribute.txt";
        int no_of_forest = 10;

        runTenTimes(no_of_forest, dataFile, attribFile);

    }
}
