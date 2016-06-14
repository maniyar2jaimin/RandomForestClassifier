package com.smoitra;

import java.util.*;
import org.apache.commons.collections4.ListUtils;

/**
 * Created by smoitra on 8/6/16.
 */
public class Forest {

    private ArrayList<Tree> trees;
    private List<Record> allData;
    private ArrayList<Attribute> attributes;
    private List<Record> trainingSample;
    private List<Record> testingSample;
    private HashMap accuracy;
    private HashMap predictions;

    /**
     * Selects Attribute postions randomly
     * @param n no of attributes to be selected
     * @return array of select attribute positions
     */
    public int[] pickNRandomAttribute(int n) {
        int length = attributes.size() - 1;
        ArrayList<Integer> attributes_position = new ArrayList<Integer>();
        for (int i = 0; i < length; i++)
            attributes_position.add(i);

        Collections.shuffle(attributes_position);

        int[] answer = new int[n];
        for (int i = 0; i < n; i++)
            answer[i] = attributes_position.get(i);

        Arrays.sort(answer);
        return answer;

    }

    /**
     * Selects 66% of the records randomly
     * @param records List of records to choose from
     * @return List of choosen records
     */
    public List<Record> pickNRandomRecords(List<Record> records) {
        int n = (int) (records.size() * 0.90);
        List<Record> list = new ArrayList<Record>(records.size());
        for (Record i : records) {
            list.add(i);
        }
        Collections.shuffle(list);
        List<Record> answer = new ArrayList<Record>();
        for (int i = 0; i < n; i++)
            answer.add(list.get(i));

        return answer;

    }

    /**
     * Constructor
     * @param noTrees no of trees to be created
     * @param filename Training data set file absolute path
     * @param attrib_file Atrributes description file absolute path
     */
    public Forest(int noTrees, String filename, List<Record> allData, ArrayList<Attribute> attributes) {
        trees = new ArrayList<Tree>();

        this.attributes = attributes;
        this.trainingSample = allData;
//        this.testingSample = ListUtils.subtract(allData, trainingSample);
        System.out.println();
        accuracy = new HashMap<Tree, Double>();
        for(int i = 0; i < noTrees; i++) {
            List<Record> training_sample = trainingSample;
            Tree tree =  createTreeWithAttributes(training_sample,filename, i);
            double trainingAccuracy = tree.doValidations(training_sample);
            System.out.printf("Training accuracy of tree %d is %.2f\n", i, trainingAccuracy);
            this.trees.add(tree);
        }
        System.out.println();
    }

    /**
     * Does predictions based on trees created
     * @param filename Test dataset file path
     * @param attrib_file Attribute description file path
     * @return HashMap of predictions
     */
    public double getPrediction(List<Record> testData) {
        System.out.println("We are trying to do predisctions");
//        List<Record> testData = testData;
        HashMap predictions = new HashMap<Record, String>();


        int success = 0;
        int failure = 0;

        for(Record rec : testData) {
            ArrayList<String> stringlist = new ArrayList<String>();
//            ArrayList<Tree> bestTrees = getBestTrees();
            ArrayList<Tree> bestTrees = this.trees;
            for(Tree tr : bestTrees) {
                String pre = tr.getRoot().getPrediction(Tree.alter_record(rec, tr.selected_attribs));
                stringlist.add(pre);

            }
            String result = getPopularElement(stringlist);
            String original_classification = rec.getValue(rec.getValues().size() - 1);
            if (original_classification.matches(result)) {
                predictions.put(rec, result + " , Success");
                success++;
            } else {
                predictions.put(rec, result + " , Failure");
                failure++;
            }
        }
        this.predictions = predictions;
        double success_rate = ((double) success / (double)(success + failure) ) * 100.00;
        System.out.println("Predictions complete");
        System.out.printf("Successfully predicted %.2f percent of the cases\n", success_rate);
        System.out.println("\n\n");
        return success_rate;
    }

    /**
     * Selects Maximum occured element
     * @param classes Arraylist of classes
     * @return String most occured element
     */
    public String getPopularElement(ArrayList<String> classes) {
        int count = 1, tempCount;
        String popular = classes.get(0);
        String temp;
        for (int i = 0; i < (classes.size() - 1); i++) {
            temp = classes.get(i);
            tempCount = 0;
            for (int j = 1; j < classes.size(); j++) {
                if (temp == classes.get(j))
                    tempCount++;
            } if (tempCount > count) {
                popular = temp;
                count = tempCount;
            }
        }
        return popular;
    }

    /**
     * Creates Single tree from records
     * @param records List of Records
     * @param filename Training file path
     * @param index
     * @return Tree
     */
    public Tree createTreeWithAttributes(List<Record> records, String filename, int index) {
        ArrayList<Record> new_records = new ArrayList<Record>();
        int no_of_attribs = (int)Math.round(Math.log(this.attributes.size())/Math.log(2)+1);

        int[] attribute_positions = pickNRandomAttribute(no_of_attribs);

        for(Record i : records) {
            new_records.add(Tree.alter_record(i, attribute_positions));
        }
        ArrayList<Attribute> new_attribs_list = new ArrayList<Attribute>();
        for(int i : attribute_positions) {
            new_attribs_list.add(this.attributes.get(i));
        }
        new_attribs_list.add(this.attributes.get(this.attributes.size() - 1));
        Tree tree = new Tree();
        tree.generateBestTree(new_records, new_attribs_list, 10, filename, index, attribute_positions);

        return tree;
    }

    public ArrayList<Tree> getBestTrees() {
        ArrayList<Tree> bestTrees = new ArrayList<Tree>();

        Map<Tree, Double> treeMap = sortByComparator(accuracy);
        for(Map.Entry<Tree, Double> i: treeMap.entrySet()) {
            if(bestTrees.size() > trees.size() / 2)
                break;
            bestTrees.add(i.getKey());
//            System.out.println("Tree Key " + i.getKey() + " tree value " + i.getValue());
        }

        return bestTrees;
    }

    /**
     * Sorting a Map of accuracy
     * @param unsortMap
     * @return Map<Tree, Double>
     */
    private static Map<Tree, Double> sortByComparator(Map<Tree, Double> unsortMap) {

        // Convert Map to List
        List<Map.Entry<Tree, Double>> list = new LinkedList<Map.Entry<Tree, Double>>(unsortMap.entrySet());

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Map.Entry<Tree, Double>>() {
            public int compare(Map.Entry<Tree, Double> o1,
                               Map.Entry<Tree, Double> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        Map<Tree, Double> sortedMap = new LinkedHashMap<Tree, Double>();
        for (Iterator<Map.Entry<Tree, Double>> it = list.iterator(); it.hasNext();) {
            Map.Entry<Tree, Double> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
}
