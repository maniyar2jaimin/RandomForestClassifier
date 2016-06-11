package com.smoitra;

import java.util.*;

public class Main {

	public static void main(String[] args) {
        String dataFile = "/home/smoitra/IdeaProjects/MyRandomForest/data/iris/train.txt";
        String testFile = "/home/smoitra/IdeaProjects/MyRandomForest/data/iris/test.txt";
        String attribFile = "/home/smoitra/IdeaProjects/MyRandomForest/data/iris/attribute.txt";
        Forest f1 = new Forest(5, dataFile, attribFile);

        HashMap predictions = f1.getPrediction(testFile, attribFile);
        Iterator<Map.Entry<Record, String>> iterator = predictions.entrySet().iterator() ;
        while(iterator.hasNext()){
            Map.Entry<Record, String> processedresult = iterator.next();
            System.out.println(processedresult.getKey() +" :: "+ processedresult.getValue());
        }

    }
}
