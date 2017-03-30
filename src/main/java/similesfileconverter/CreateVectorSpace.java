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
public class CreateVectorSpace {

    public CreateVectorSpace() {
    }

    
    
    
    public void convertTXTtoVectorTXT(String inputTxtFile, String outputTxtVectorFile) {
        BufferedReader bufferReader = null;
        try {
            File file = new File(inputTxtFile);
            bufferReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputTxtVectorFile), "UTF-8"));
            String header = "Text#Simile#M#F#N#Cheeks#Face#Eyes#Lips#Person#ID#ID-A#iwo#ixp-W#ixp-N#ixp-punc#ixp-creative#sanp#mwo#agr#det#other";
            writer.write(header + "\n");
            int count_lines = 0; //count of sentences
            String line = "";
            while ((line = bufferReader.readLine()) != null) {
                count_lines++;  //count_paragraphs = number of lines
                String[] column = line.split("#");  //each line has seven columns

               // writer.write(column[0].trim() + "#" + column[1].trim());

                writer.write(column[0].trim() + "#" + column[1].trim()); //text and sim feature
                //Gender features
                if (column[2].trim().equals("M")) //#M#F#N 
                {
                    writer.write("#1#0#0");
                } else if (column[2].trim().equals("F")) {
                    writer.write("#0#1#0");
                } else if (column[2].trim().equals("N")) {
                    writer.write("#0#0#1");
                } else {
                    writer.write("#0#0#0");
                }

                //MODIFIED PRED SEMS features
                if (column[4].trim().equals("ΜΑΓΟΥΛΑ")) {
                    writer.write("#1#0#0#0#0");
                } else if (column[4].trim().equals("ΠΡΟΣΩΠΟ") || column[4].trim().equals("ΠΡΌΣΩΠΟ")) {
                    writer.write("#0#1#0#0#0");
                } else if (column[4].trim().equals("ΜΑΠΑ")) {
                    writer.write("#0#0#1#0#0");
                } else if (column[4].trim().equals("ΧΕΙΛΙΑ")) {
                    writer.write("#0#0#0#1#0");
                } else if (column[4].trim().equals("PERSON")) {
                    writer.write("#0#0#0#0#1");
                } else {
                    writer.write("#0#0#0#0#0");
                }
                //MWE Type features
                if (column[5].replaceAll("\\s+", "").equals("ID")) {
                    writer.write("#1#0");
                } else if (column[5].replaceAll("\\s+", "").equals("ID-A")) {
                    writer.write("#0#1");
                } else {
                    writer.write("#0#0");
                }

                //Phenomenon features
                if (column.length > 6) {
                    if ((column[6].replaceAll("\\s+", "")).equals("iwo")) {
                        writer.write("#1#0#0#0#0#0#0#0#0#0");
                    } else if (column[6].replaceAll("\\s+", "").equals("ixp-w")) {
                        writer.write("#0#1#0#0#0#0#0#0#0#0");
                    } else if (column[6].replaceAll("\\s+", "").equals("ixp-n")) {
                        writer.write("#0#0#1#0#0#0#0#0#0#0");
                    } else if (column[6].replaceAll("\\s+", "").equals("ixp-punc")) {
                        writer.write("#0#0#0#1#0#0#0#0#0#0");
                    } else if ((column[6].replaceAll("\\s+", "")).equals("ixp-creative")) {
                        writer.write("#0#0#0#0#1#0#0#0#0#0");
                    } else if (column[6].replaceAll("\\s+", "").equals("sanp")) {
                        writer.write("#0#0#0#0#0#1#0#0#0#0");
                    } else if (column[6].replaceAll("\\s+", "").equals("mwo")) {
                        writer.write("#0#0#0#0#0#0#1#0#0#0");
                    } else if (column[6].replaceAll("\\s+", "").equals("agr")) {
                        writer.write("#0#0#0#0#0#0#0#1#0#0");
                    } else if (column[6].replaceAll("\\s+", "").equals("det")) {
                        writer.write("#0#0#0#0#0#0#0#0#1#0");
                    } else if (column[6].replaceAll("\\s+", "").equals("other")) {
                        writer.write("#0#0#0#0#0#0#0#0#0#1");
                    } else {
                        writer.write("#0#0#0#0#0#0#0#0#0#0");
                    }
                } else {
                    writer.write("#0#0#0#0#0#0#0#0#0#0");
                }
                writer.write("\n");
            }

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
