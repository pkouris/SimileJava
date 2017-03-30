/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package similesfileconverter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author user
 */
public class XLSXtoTXT {

    public XLSXtoTXT() {
    }

    //it reads the csv file and writes a txt file with the useful sentences.
    public void txtWriter(String xlsxFile, String txtFile) {
        //BufferedReader bufferReader = null;
        try {
            InputStream xlsxFileToRead = new FileInputStream(xlsxFile);
            XSSFWorkbook wb = new XSSFWorkbook(xlsxFileToRead);
            //XSSFWorkbook test = new XSSFWorkbook();
            XSSFSheet sheet = wb.getSheetAt(0);
            XSSFRow row;
            XSSFCell cell;
            Iterator rows = sheet.rowIterator();
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(txtFile), "UTF-8"));
            writer.write("TEXT # SIM # GENDER # HEAD # LEMMA # MODIFIED PRED SEMS # TENOR GENERALISED SEMANTICS # MWE TYPE # PHENOMENON # DETERMINER # ΕMPHASIS # _IWO # _IXP-CREATIVE # _IXP-EXPANSION # IXP-N, IXP-W, IXP-PUNC # _MOD # _AGR # MWO, SANP, OTHER\n");//write first line
            int count_xlsx_lines = 0; //it counts the number of lines of csv file
            int count_txt_lines = 0; //it counts the number of lines of txt file
            int flag = 0; //if flag is 0, it is a useful line
            while (rows.hasNext()) {
                String line = "";
                count_xlsx_lines++;
                row = (XSSFRow) rows.next();
                Iterator cells = row.cellIterator();
                //int count_cells = 0;
                if (count_xlsx_lines < 5) {
                    if (count_xlsx_lines == 4) {
                        flag = 1;
                    }
                } else if (flag == 1) {
                    for (int count_cells = 1; count_cells < 18; count_cells++) {
                        // while (cells.hasNext()) {//read cells of row
                        //count_cells++;
                        if (count_cells < 20) {
                            if (cells.hasNext()) {
                                cell = (XSSFCell) cells.next();
                                switch (cell.getCellType()) {
                                    case HSSFCell.CELL_TYPE_STRING:
                                        if (count_cells == 17) {
                                            line += cell.getStringCellValue() + " ";
                                        } else {
                                            line += cell.getStringCellValue() + " # ";
                                        }
                                        ///////////
                                       // System.out.println("String "+line);
                                        break;
                                    case HSSFCell.CELL_TYPE_NUMERIC:
                                        if (count_cells == 19) {
                                            line += ((int) cell.getNumericCellValue()) + " ";
                                        } else {
                                            line += ((int) cell.getNumericCellValue()) + " # ";
                                        }
                                        ///////////
                                       // System.out.println("numer" + line);
                                        break;
                                    default:
                                        if (count_cells == 19) {
                                            line += " ";
                                        } else {
                                            line += " # ";
                                        }
                                        ///////////
                                       // System.out.println("defult" + line);
                                        ////////////
                                        // System.out.println("unknown cell type --> row:" + count_xlsx_lines + " cell:" + count_cells);
                                        break;
                                }
                            } else {
                                /////////
                                ///////////
                                       // System.out.println(">8" + line);
                                // System.out.println("null cell --> row:" + count_xlsx_lines + " cell:" + count_cells);
                                line += " # ";
                            }
                        } else {
                            break;
                        }
                    }
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
                    line = line.trim().replaceAll("΄", " ΄ ");
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
                    count_txt_lines++;
                    flag++;
                } else if (flag == 3) {
                    flag = 1;
                } else {
                    flag++;
                }
                //////////////////
                // System.out.println(line);
            }

            // File inputfile = new File(csvFile);
            // bufferReader = new BufferedReader(new InputStreamReader(new FileInputStream(inputfile), "UTF8"));
            //while ((line = bufferReader.readLine()) != null) {
            
            System.out.println("number of xlsx rows:" + count_xlsx_lines);
            System.out.println("number of txt lines:" + count_txt_lines);
            wb.close();
            writer.close();
//            bufferReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }

    }

}
