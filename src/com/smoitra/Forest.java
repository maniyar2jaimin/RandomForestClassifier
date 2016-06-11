package com.smoitra;

import java.util.*;

/**
 * Created by smoitra on 8/6/16.
 */
public class Forest {

    private ArrayList<Tree> trees;
    private List<Record> allData;
    private ArrayList<Attribute> attributes;


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
        int n = (int) (records.size() * 0.66);
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
    public Forest(int noTrees, String filename, String attrib_file) {
        System.out.println("=====================================================================");
        System.out.println("Developed by : ");
        System.out.println("\t 1. Joyeeta Roy");
        System.out.println("\t 2. Neel Kusum Ekka");
        System.out.println("\t 2. Sourav Moitra");
        System.out.println("Creating Forest of " + noTrees + " trees.");
        LoadData.loadAllData(filename, attrib_file);
        trees = new ArrayList<Tree>();
        this.allData = LoadData.allData;
        this.attributes = LoadData.attributes;


        System.out.println("Creating trees by picking randomly 66% from training data");
        for(int i = 0; i < noTrees; i++) {
            Tree tree =  createTreeWithAttributes(pickNRandomRecords(allData),filename, i);
            this.trees.add(tree);
        }
    }

    /**
     * Does predictions based on trees created
     * @param filename Test dataset file path
     * @param attrib_file Attribute description file path
     * @return HashMap of predictions
     */
    public HashMap getPrediction(String filename, String attrib_file) {
        System.out.println("We are trying to do predictions");
        LoadData.loadAllData(filename, attrib_file);
        List<Record> testData = LoadData.allData;
        HashMap predictions = new HashMap<Record, String>();

        int success = 0;
        int failure = 0;

        for(Record rec : testData) {
            ArrayList<String> stringlist = new ArrayList<String>();
            for(Tree tr : trees) {
                String pre = tr.getRoot().getPrediction(alter_record(rec, tr.selected_attribs));
                stringlist.add(pre);

            }
            String result = getPopularElement(stringlist);
            String original_classification = rec.getValue(rec.getValues().size() - 1);
            if (original_classification.matches(result)) {
                predictions.put(rec, "Success");
                success++;
            } else {
                predictions.put(rec, "Failure");
                failure++;
            }

        }
        double success_rate = ((double) success / (double)(success + failure) ) * 100.00;
        System.out.println("Predictions complete");
        System.out.printf("Successfully predicted %.2f percent of the cases\n", success_rate);
        return predictions;
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
            new_records.add(alter_record(i, attribute_positions));
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

    /**
     * Selects attributes from supplied attributes array
     * @param record
     * @param selected_attribs
     * @return
     */
    public Record alter_record(Record record, int[] selected_attribs) {
        String[] str = new String[selected_attribs.length + 1];
        Record new_rec = new Record();
        for (int j = 0; j < selected_attribs.length; j++ ) {
            str[j] = record.getValue(selected_attribs[j]);
        }
        str[selected_attribs.length] = record.getValue(this.attributes.size() - 1 );
        new_rec.setAttribute(str);
        return new_rec;
    }
}
