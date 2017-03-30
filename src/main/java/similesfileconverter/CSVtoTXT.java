/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package similesfileconverter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 *
 * @author user
 */
public class CSVtoTXT {

    public CSVtoTXT() {
    }
    
     //it reads the csv file and writes a txt file with the useful sentences.
    public void txtWriter(String csvFile, String txtFile) {
        BufferedReader bufferReader = null;
        try {
            File inputfile = new File(csvFile);
            bufferReader = new BufferedReader(new InputStreamReader(new FileInputStream(inputfile), "UTF8"));
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(txtFile), "UTF-8"));
            int count_csv_Lines = 0; //it counts the number of lines of csv file
            int count_txt_Lines = 0; //it counts the number of lines of txt file
            int flag = 0; //if flag is 0, it is a useful line
            String line = "";
            while ((line = bufferReader.readLine()) != null) {
                count_csv_Lines++;
                if (count_csv_Lines < 5) {
                    if (count_csv_Lines == 4) {
                        flag = 1;
                    }
                } else if (flag == 1) {
                    line = line.replaceAll("\\$", " \\$ ");
                    line = line.replaceAll("\\]", " \\] ");
                    line = line.trim().replaceAll("\\[", " \\[ ");
                    line = line.trim().replaceAll("\\[ \\* \\]", "\\[\\*\\]");
                    line = line.trim().replaceAll(",", " , ");
                    line = line.trim().replaceAll("\\)", " \\) ");
                    line = line.trim().replaceAll("\\(", " \\( ");
                    line = line.trim().replaceAll("[-]", " - ");
                    line = line.trim().replaceAll("[!]", " ! ");
                    line = line.trim().replaceAll(";", " ; ");
                    line = line.trim().replaceAll("[.]", " . ");
                    line = line.trim().replaceAll("\"", " \" ");
                    line = line.trim().replaceAll("<", " ");
                    line = line.trim().replaceAll("…", " ... ");
                    line = line.trim().replaceAll("«", " « ");
                    line = line.trim().replaceAll("»", " » ");
                    line = line.trim().replaceAll("[#]", " # ");
                    line = line.trim().replaceAll("[*]", " * ");
                    line = line.trim().replace(". . .", "...");
                    line = line.trim().replaceAll(" +", " "); //replace two or more white spaces with one
                    line = line.trim(); //it deletes the first white space for each line if it is exists.
                    //line = line.replaceFirst("[.]", "");
                    writer.write(line + "\n"); //it writes the line
                    count_txt_Lines++;
                    flag++;
                } else if (flag == 3) {
                    flag = 1;
                } else {
                    flag++;
                }
            }
            System.out.println("number of csv lines:" + count_csv_Lines);
            System.out.println("number of txt lines" + count_txt_Lines);
            writer.close();
            bufferReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferReader != null) {
                try {
                    bufferReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    
    
    
    
    
}
