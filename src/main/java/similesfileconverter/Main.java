/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package similesfileconverter;
/**
 *
 * @author user
*/
public class Main {
    static int count_entities = 0;
    static String datetime = "2017-03-15T20:12:44"; //annotation datetime
    static String[] ids = {"",
            "i_aspros_san_to_pani1",
            "ii_stolismenos_san_fregata",
            "iii_apalos_san_poupoulo",
            "iv_apalos_san_xadi",
            "v_elafrys_san_poupoulo",
            "vi_kokkinos_san_astakos",
            "vii_oplismenos_san_astakos",
            "viii_malakos_san_voutiro",
            "ix_geros_san_tavros",
            "x_pistos_san_skilos"};
    static String id = "";
    static String[] headers_of_foliaFile = {"",
            "ΑΣΠΡΟΣ ΣΑΝ ΤΟ ΠΑΝΙ",
            "ΣΤΟΛΙΣΜΕΝΟΣ ΣΑΝ ΦΡΕΓΑΤΑ",
            "ΑΠΑΛΟΣ ΣΑΝ ΠΟΥΠΟΥΛΟ",
            "ΑΠΑΛΟΣ ΣΑΝ ΧΑΔΙ",
            "ΕΛΑΦΡΥΣ ΣΑΝ ΠΟΥΠΟΥΛΟ",
            "ΚΟΚΚΙΝΟΣ ΣΑΝ ΑΣΤΑΚΟΣ",
            "ΟΠΛΙΣΜΕΝΟΣ ΣΑΝ ΑΣΤΑΚΟΣ",
            "ΜΑΛΑΚΟΣ ΣΑΝ ΒΟΥΤΥΡΟ",
            "ΓΕΡΟΣ ΣΑΝ ΤΑΥΡΟΣ",
            "ΠΙΣΤΟΣ ΣΑΝ ΣΚΥΛΟΣ"};
    static String header = "";
    static String[] filenames = {"",
            "1_aspros_san_to_pani",
            "2_stolismenos_san_fregata",
            "3_apalos_san_poupoulo",
            "4_apalos_san_xadi",
            "5_elafrys_san_poupoulo",
            "6_kokkinos_san_astakos",
            "7_oplismenos_san_astakos",
            "8_malakos_san_voutiro",
            "9_geros_san_tavros",
            "10_pistos_san_skilos"};

    static String linux_dataset_path = "/home/pkouris/Dropbox/EMP_DID_dropbox/NLP/SIMILES/dataset/";
    static String win_dataset_path = "E:\\Dropbox\\EMP_DID_dropbox\\NLP\\SIMILES\\dataset\\";
    static String dataset_path = "";

    public static void main(String[] args) {
        //they should be changed
        dataset_path = win_dataset_path;
        int mode = 2; //1: xlsx-->txt, 2: txt-->folia xml, 3: txt-->vector space
        int index_of_file_header_id = 9; //it is the table index of file, header and id
        int applyAllFiles = 0; //1: apply to all files, 0: apply to one file

        switch (mode) {
            case 1: //mode 1: convert csv to txt
                System.out.println("convert xlsx to txt");
                if (applyAllFiles == 1) { //convert all files
                    for (int i = 1; i < 10; i++) {
                        System.out.println("\n" + filenames[i]);
                        //String filename = filenames[i];
                        header = headers_of_foliaFile[i];
                        id = ids[i];
                        String txtfile = dataset_path + "" + filenames[i] + ".txt";
                        String xlsxfile = dataset_path + "" + filenames[i] + ".xlsx";
                        XLSXtoTXT reduceXLSXtoTXT = new XLSXtoTXT();
                        reduceXLSXtoTXT.txtWriter(xlsxfile, txtfile);
                    }
                } else { //convert one file
                    header = headers_of_foliaFile[index_of_file_header_id];
                    id = ids[index_of_file_header_id];
                    String txtFile = dataset_path + "" + filenames[index_of_file_header_id] + ".txt";
                    String xlsxFile = dataset_path + "" + filenames[index_of_file_header_id] + ".xlsx";
                    XLSXtoTXT reduceXLSXtoTXT = new XLSXtoTXT();
                    reduceXLSXtoTXT.txtWriter(xlsxFile, txtFile);
                }
                break;
            case 2: //mode 2: convert txt to folia xml
                System.out.println("convert txt to folia xml");
                if (applyAllFiles == 1) {//convert all files
                    for (int i = 1; i < 10; i++) {
                        header = headers_of_foliaFile[i];
                        id = ids[i];
                        String txtFile = dataset_path + "" + filenames[i] + ".txt";
                        String xmlFile = dataset_path + "" + filenames[i] + ".xml";
                        System.out.println(filenames[i]);
                        TXTtoFoliaXML txtToFoliaXML = new TXTtoFoliaXML();
                        txtToFoliaXML.foliaXML_Writer(txtFile, xmlFile);
                        System.out.println("");
                    }
                } else {//convert one file
                    header = headers_of_foliaFile[index_of_file_header_id];
                    id = ids[index_of_file_header_id];
                    String txtFile = dataset_path + "" + filenames[index_of_file_header_id] + ".txt";
                    String foliaXMLFile = dataset_path + "" + filenames[index_of_file_header_id] + ".xml";
                    TXTtoFoliaXML txtToFoliaXML = new TXTtoFoliaXML();
                    txtToFoliaXML.foliaXML_Writer(txtFile, foliaXMLFile);
                }
                break;
            case 3:
                //mode 3: convert txt to vector space
                // System.out.println("convert folia xml to vector space");
                // CreateVectorSpace cvs = new CreateVectorSpace();
                break;
            default:
                break;
        }
    }
}
