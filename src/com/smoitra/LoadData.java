package com.smoitra;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by smoitra on 8/6/16.
 */
public class LoadData {
    public static ArrayList<Attribute> attributes;
    public static List<Record> allData;

    public static void loadAllData(String filename, String attribute_file) {
        try {
            attributes = new ArrayList<Attribute>();
            BufferedReader in = new BufferedReader(new FileReader(filename));
            BufferedReader attribs = new BufferedReader(new FileReader(attribute_file));
            String attribs_line;
            System.out.println("Reading Data...");
            while ((attribs_line = attribs.readLine()) != null) {
                attribs_line.trim();
                String[] res = attribs_line.trim().split("\\s+");
                if (res[2].charAt(0) == '{' && res[2].charAt(res[2].length() - 1) == '}') {
                    // attributes
                    String[] values = res[2].substring(1,
                            res[2].length() - 1).split(",");
                    NomAttribute attribute = new NomAttribute(res[1]);
                    for (int i = 0; i < values.length; i++)
                        attribute.addValue(values[i]);
                    attributes.add(attribute);
                } else if (res[2].equals("real")) {
                    // real - numeric attribute
                    NumAttribute attribute = new NumAttribute(res[1]);
                    attributes.add(attribute);
                }
            }
            attribs.close();
            String line;
            allData = new ArrayList<Record>();
            while ((line = in.readLine()) != null) {
                line.trim();
                String[] values = line.split(",");
                Record rec = new Record();
                rec.setAttribute(values);
                allData.add(rec);
            }
            in.close();
            System.out.println("Data Loading completed");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
