/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package similesfileconverter;

import static similesfileconverter.Main.count_entities;
import static similesfileconverter.Main.datetime;
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
import java.text.BreakIterator;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import static similesfileconverter.Main.header;
import static similesfileconverter.Main.id;

/**
 *
 * @author pkouris
 */
public class TXTtoFoliaXML {

    public TXTtoFoliaXML() {
    }

    public void foliaXML_Writer(String txtFile, String foliaXMLFile) {
        BufferedReader bufferReader = null;
        try {

            File file = new File(txtFile);
            bufferReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(foliaXMLFile), "UTF-8"));
            writeTopOfFoliaXML(writer, "1.0");
            int count_paragraphs = 0; //count of sentences
            String line = "";
            bufferReader.readLine(); //load first line with header
            while ((line = bufferReader.readLine()) != null) { //it starts from second line
                count_paragraphs++;  //count_paragraphs = number of lines
                String[] column = line.split("#");  //each line has seven columns
                writer.write("\t<p xml:id=\"" + id + ".p." + count_paragraphs + "\">\n");
                BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.ENGLISH);
                iterator.setText(column[0]); //column 0 is the text.
                int start = iterator.first();
                int count_sentences = 0;
                for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) { //for sentences
                    count_sentences++;
                    writer.write("\t\t<s xml:id=\"" + id + ".p." + count_paragraphs + ".s." + count_sentences + "\">\n");
                    String sentence = column[0].substring(start, end);
                    String[] words = sentence.split(" ");
                    int count_words = 0;
                    int first_doll = 0;
                    int first_doll_countword = 0;
                    int first_doll_flag = 0; //index for first $
                    int second_doll = 0; //index for second $
                    int first_index = 0; //index for [
                    int first_index_countword = 0;
                    int second_index = 0; //index for ]
                    int word_index = -1; //it starts from 0
                    for (String w : words) { //for words
                        //w = w.trim();
                        word_index++;
                        if (!w.trim().equals("")) {
                            if ((w.trim()).equals("$")) {
                                if (first_doll_flag == 0) {
                                    first_doll_flag = 1;
                                    first_doll = word_index;
                                    first_doll_countword = count_words;
                                } else {
                                    second_doll = word_index;
                                }
                            } else if (w.trim().equals("[")) {
                                first_index = word_index;
                                first_index_countword = count_words;
                            } else if (w.trim().equals("]")) {
                                second_index = word_index;
                            } else if ((w.trim()).equals(column[3].trim())) {
                                count_words++;
                                writer.write("\t\t\t<w xml:id=\"" + id + ".p." + count_paragraphs + ".s." + count_sentences + ".w." + count_words + "\">\n");
                                writer.write("\t\t\t\t<t>" + convertSpecialCharacters(w) + "</t>\n");
                                String lemma = column[4].trim();
                                writer.write(addLemmaAnnotation(lemma, count_paragraphs));
                                writer.write("\t\t\t</w>\n");
                            } else {
                                count_words++;
                                writer.write("\t\t\t<w xml:id=\"" + id + ".p." + count_paragraphs + ".s." + count_sentences + ".w." + count_words + "\">\n");
                                writer.write("\t\t\t\t<t>" + convertSpecialCharacters(w) + "</t>\n");
                                writer.write("\t\t\t</w>\n");
                            }
                        }
                    }
                    if (first_doll > second_doll) {
                        second_doll = word_index;
                        System.out.println("paragraph: " + count_paragraphs + " first_doll: " + first_doll + " second_doll: " + second_doll);
                    }
                    if (first_index > second_index) {
                        second_index = word_index;
                        System.out.println("paragraph: " + count_paragraphs + " first_index: " + first_index + " second_index: " + second_index);
                    }
                    String entities = addEntityAnnotations(column, words, first_doll, second_doll, first_doll_countword, first_index, second_index, first_index_countword, count_paragraphs, count_sentences);
                    writer.write(entities); //add simile entity annotations
                    writer.write("\t\t</s>\n");
                }
                writer.write("\t\t<whitespace/>\n");
                writer.write("\t</p>\n");
            }
            writeLastTextFoliaXML(writer);
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

    public String convertSpecialCharacters(String w) {
        switch (w.trim()) {
            case "\"":
                return "&quot;";
            case "<":
                return "&lt;";
            case ">":
                return "&gt;";
            case "&":
                return "&amp;";
            case "'":
                return "&apos;";
            default:
                break;
        }
        return w;
    }

    //It adds Lemma annotations
    public String addLemmaAnnotation(String lemma, int count_paragraph) {
        if (!lemma.equals("") && !lemma.equals(" ")) {
            return "\t\t\t\t<lemma class=\"" + lemma + "\"/>\n";
        } else {
            /////////////////
            System.out.println("null Lemma:" + lemma + "  paragraph:" + count_paragraph);
            return "";
        }
    }

    public String addEntityAnnotations(String[] column, String[] sentence_words, int first_doll, int second_doll, int first_doll_countword, int first_index, int second_index, int first_index_countword, int count_paragraph, int count_sentence) {
        String entityAnnotations = "";
        if (first_doll < second_doll || (first_index < second_index)) {
            entityAnnotations += "\t\t\t<entities>\n";
            entityAnnotations += addSimileEntityAnnotation(column[1], sentence_words, first_doll, second_doll, first_doll_countword, count_paragraph, count_sentence);
            entityAnnotations += addGenderEntityAnnotation(column[2], sentence_words, first_index, second_index, first_index_countword, count_paragraph, count_sentence);
            entityAnnotations += addModifiedPredSemsEntityAnnotation(column[5], sentence_words, first_index, second_index, first_index_countword, count_paragraph, count_sentence);
            entityAnnotations += addTenorGeneralizedSemEntityAnnotation(column[6], sentence_words, first_index, second_index, first_index_countword, count_paragraph, count_sentence);
            entityAnnotations += addMweTypeEntityAnnotation(column[7], sentence_words, first_doll, second_doll, first_doll_countword, count_paragraph, count_sentence);
            ////////////
            //System.out.println(column.length);
            if (column.length > 7) {
                ////////////
                //System.out.println(column.length + "  " + column[6] + "  " + column[6].replaceAll("\\s+", ""));
                entityAnnotations += addPhenomenonEntityAnnotation(column[8], sentence_words, first_doll, second_doll, first_doll_countword, count_paragraph, count_sentence);
            }
            entityAnnotations += "\t\t\t</entities>\n";
        }
        return entityAnnotations;
    }

    //Add simile entity annotations $...$
    public String addSimileEntityAnnotation(String simile_column, String[] sentence_words, int first_doll, int second_doll, int first_doll_countword, int count_paragraph, int count_sentence) {
        String entityAnnotation = "";
        if (simile_column.trim().equals("1")) { //it is a simile
            entityAnnotation += writeEntity("SIM.YES", "Simile.xml", sentence_words, first_doll, second_doll, first_doll_countword, count_paragraph, count_sentence);
        } else if (simile_column.trim().equals("0")) { //it isn't a simile
            entityAnnotation += writeEntity("SIM.NO", "Simile.xml", sentence_words, first_doll, second_doll, first_doll_countword, count_paragraph, count_sentence);
        }
        return entityAnnotation;
    }

    //It adds MWE Type entity annotations $...$
    public String addMweTypeEntityAnnotation(String column, String[] sentence_words, int first_doll, int second_doll, int first_doll_countword, int count_paragraph, int count_sentence) {
        String entityAnnotation = "";
        column = column.trim();//.replaceAll("\\s+", ""); //remove the firts and last spaces
        String[] parts = column.split(",");
        ////////////
        // System.out.println("column=" + column);
        for (String s : parts) {
            String classOfTag = s.trim();
            entityAnnotation += writeEntity(classOfTag, "MWE_Type.xml", sentence_words, first_doll, second_doll, first_doll_countword, count_paragraph, count_sentence);
        }
        return entityAnnotation;
    }

    //$...$
    public String addPhenomenonEntityAnnotation(String column, String[] sentence_words, int first_doll, int second_doll, int first_doll_countword, int count_paragraph, int count_sentence) {
        String entityAnnotation = "";
        //String[] parts_of_column = "";
        column = column.replaceAll(" - ", "-");
        column = column.trim();
        ////////////
        // System.out.println("Phenomenon column=" + column);
        String[] parts = column.split(",");
        for (String s : parts) {
            String classOfTag = s.replaceAll("\\s+", "");
            entityAnnotation += writeEntity(classOfTag, "Phenomenon.xml", sentence_words, first_doll, second_doll, first_doll_countword, count_paragraph, count_sentence);
        }
        return entityAnnotation;
    }

    //[...]
    public String addGenderEntityAnnotation(String gender_column, String[] sentence_words, int first_index, int second_index, int first_index_countword, int count_paragraph, int count_sentence) {
        String entityAnnotation = "";
        if (first_index < second_index) {
            gender_column = gender_column.trim();
            String[] parts = gender_column.split(",");
            for (String s : parts) {
                if (s.trim().equals("M") || s.trim().equals("Μ")) {
                    entityAnnotation += writeEntity("Ma", "Gender.xml", sentence_words, first_index, second_index, first_index_countword, count_paragraph, count_sentence);
                } else if (s.trim().equals("F")) {
                    writeEntity("Fe", "Gender.xml", sentence_words, first_index, second_index, first_index_countword, count_paragraph, count_sentence);
                } else if (s.trim().equals("N") || s.trim().equals("Ν")) {
                    entityAnnotation += writeEntity("Ne", "Gender.xml", sentence_words, first_index, second_index, first_index_countword, count_paragraph, count_sentence);
                }
            }
        }
        return entityAnnotation;
    }

    
    
    
    
    
    //[....]
    public String addModifiedPredSemsEntityAnnotation(String column, String[] sentence_words, int first_index, int second_index, int first_index_countword, int count_paragraph, int count_sentence) {
        String entityAnnotation = "";
        if (first_index < second_index) {
            String class_of_annotation = column.trim();
            entityAnnotation += writeEntity(class_of_annotation, "Modified_Pred_Sems.xml", sentence_words, first_index, second_index, first_index_countword, count_paragraph, count_sentence);
        }
        return entityAnnotation;
    }
    
    
    //[....]
    public String addTenorGeneralizedSemEntityAnnotation(String column, String[] sentence_words, int first_index, int second_index, int first_index_countword, int count_paragraph, int count_sentence) {
        String entityAnnotation = "";
        if (first_index < second_index) {
            String class_of_annotation = column.trim();
            entityAnnotation += writeEntity(class_of_annotation, "Tenor_Generalized_Sems.xml", sentence_words, first_index, second_index, first_index_countword, count_paragraph, count_sentence);
        }
        return entityAnnotation;
    }
    
    

    //return the entities
    public String writeEntity(String class_of_entity, String set_file, String[] sentence_words, int first_index, int second_index, int first_index_countword, int count_paragraph, int count_sentence) {
        String entity = "";
        if (!class_of_entity.equals("") && !class_of_entity.equals(" ")) {
            count_entities++;
            entity = "\t\t\t\t<entity class=\"" + class_of_entity + "\" set=\"http://users.ntua.gr/pkouris/folia_tagset/" + set_file + "\" xml:id=\"" + id + ".text.entity." + count_entities + "\">\n";
            int count_word = first_index_countword;
            for (int index_word = first_index; index_word < second_index; index_word++) {
                if (!sentence_words[index_word].equals("$") && !sentence_words[index_word].equals("[") && !sentence_words[index_word].equals("]")) {
                    count_word++;
                    entity += "\t\t\t\t\t<wref id=\"" + id + ".p." + count_paragraph + ".s." + count_sentence + ".w." + count_word + "\" t=\"" + convertSpecialCharacters(sentence_words[index_word]) + "\"/>\n";
                }
            }
            entity += "\t\t\t\t</entity>\n";
        } else {
            ///////////////
            System.out.println("null class:" + class_of_entity + ", set_file: " + set_file + ", paragraph:" + count_paragraph);
        }
        return entity;
    }

    //write header of folia xml
    public String writeHeader() {
        if (!header.equals("")) {
            return "\t<p xml:id=\"" + id + ".p.0\">\n"
                    + "\t\t<s xml:id=\"" + id + ".p.0.s.1\">\n"
                    + "\t\t\t<t>" + header + "</t>\n"
                    + "\t\t</s>\n"
                    + "\t\t<whitespace/>\n"
                    + "\t</p>\n";
        }
        return "";
    }

    //it writes the top text fo the folia xml file
    public void writeTopOfFoliaXML(Writer writer, String version) {
        try {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                    + "<FoLiA xmlns=\"http://ilk.uvt.nl/folia\"\n"
                    + " xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n"
                    + " xml:id=\"" + id + "\" version=\"" + version + "\" generator=\"auto\">\n"
                    + "\t<metadata type=\"native\">\n"
                    + "\t\t<annotations>\n"
                    + "\t\t\t<entity-annotation set=\"http://users.ntua.gr/pkouris/folia_tagset/Simile.xml\" />\n"
                    + "\t\t\t<entity-annotation set=\"http://users.ntua.gr/pkouris/folia_tagset/Gender.xml\" />\n"
                    + "\t\t\t<lemma-annotation set=\"http://users.ntua.gr/pkouris/folia_tagset/Lemma.xml\"/>\n"
                    + "\t\t\t<entity-annotation set=\"http://users.ntua.gr/pkouris/folia_tagset/Modified_Pred_Sems.xml\" />\n"
                    + "\t\t\t<entity-annotation set=\"http://users.ntua.gr/pkouris/folia_tagset/Tenor_Generalized_Sems.xml\" />\n"
                    + "\t\t\t<entity-annotation set=\"http://users.ntua.gr/pkouris/folia_tagset/MWE_Type.xml\" />\n"
                    + "\t\t\t<entity-annotation set=\"http://users.ntua.gr/pkouris/folia_tagset/Phenomenon.xml\" />\n"
                    + "\t\t\t<entity-annotation set=\"http://users.ntua.gr/pkouris/folia_tagset/Part_Of_Speech.xml\" />\n"
                    + "\t\t\t<token-annotation annotator=\"ilktok\" annotatortype=\"auto\"/>\n"
                    + "\t\t\t<sentence-annotation annotator=\"ucto\" annotatortype=\"auto\" set=\"sentences\"/>\n"
                    + "\t\t\t<paragraph-annotation annotator=\"ucto\" annotatortype=\"auto\" set=\"paragraphs\"/>\n"
                    + "\t\t\t<pos-annotation annotator=\"tadpole\" annotatortype=\"auto\" set=\"cgn-combinedtags\"/>\n"
                    + "\t\t\t<correction-annotation annotator=\"proycon\" annotatortype=\"manual\" set=\"corrections\"/>\n"
                    + "\t\t\t<errordetection-annotation annotator=\"proycon\" annotatortype=\"manual\" set=\"corrections\"/>\n"
                    + "\t\t\t<morphological-annotation annotator=\"proycon\" annotatortype=\"manual\"/>\n"
                    + "\t\t\t<gap-annotation annotator=\"proycon\" annotatortype=\"manual\"/>\n"
                    + "\t\t\t<syntax-annotation set=\"syntax-set\"/>\n"
                    + "\t\t\t<note-annotation set=\"notes\"/>\n"
                    + "\t\t\t<part-annotation set=\"parts\"/>\n"
                    + "\t\t\t<term-annotation set=\"words\"/>\n"
                    + "\t\t\t<definition-annotation set=\"definitions\"/>\n"
                    + "\t\t</annotations>\n"
                    + "\t</metadata>\n"
                    + "\t<text xml:id=\"" + id + ".text\">\n");
            writer.write(writeHeader());  ///it writes the header of the folia xml
        } catch (IOException ex) {
            Logger.getLogger(TXTtoFoliaXML.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    //it writes the last text of the folia xml file
    public static void writeLastTextFoliaXML(Writer writer) {
        try {
            writer.write("\t</text>\n</FoLiA>");
        } catch (IOException ex) {
            Logger.getLogger(TXTtoFoliaXML.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

}

/* //it returns the potition of [*] starting from 1, otherwise returns 0 
    public static int findNullGender(String[] sentence_words) {
        int index = 0;
        for (String s : sentence_words) {
            index++;
            if (s.equals("[*]")) {
                return index;
            }
        }
        return 0;
    }
 */
//[....]
/*
        //[....]
    public String addModifiedPredSemsEntityAnnotation(String column, String[] sentence_words, int first_index, int second_index, int first_index_countword, String id, int count_paragraph, int count_sentence) {
        String entityAnnotation = "";
        if (first_index > 0 && second_index > 0) {
            count_entities++;
            if (column.trim().equals("ΜΑΓΟΥΛΑ")) { //Cheeks
                entityAnnotation += "\t\t\t\t<entity class=\"cheeks\" datetime=\"" + datetime + "\" set=\"http://users.ntua.gr/pkouris/tagset/Modified_Pred_Sems.xml\" xml:id=\"" + id + ".text.entity." + count_entities + "\">\n";
                int count_word = first_index_countword;
                for (int index_word = first_index; index_word < second_index; index_word++) {
                    if (!sentence_words[index_word].equals("$") && !sentence_words[index_word].equals("[") && !sentence_words[index_word].equals("]")) {
                        count_word++;
                        entityAnnotation += "\t\t\t\t\t<wref id=\"" + id + ".p." + count_paragraph + ".s." + count_sentence + ".w." + count_word + "\" t=\"" + sentence_words[index_word] + "\"/>\n";
                    }
                }
                entityAnnotation += "\t\t\t\t</entity>\n";
            } else if (column.trim().equals("πρόσωπο") || column.trim().equals("ΜΑΠΑ") || column.trim().equals("όψη")) {  //Face
                entityAnnotation += "\t\t\t\t<entity class=\"face\" datetime=\"" + datetime + "\" set=\"http://users.ntua.gr/pkouris/tagset/Modified_Pred_Sems.xml\" xml:id=\"" + id + ".text.entity." + count_entities + "\">\n";
                int count_word = first_index_countword;
                for (int index_word = first_index; index_word < second_index; index_word++) {
                    if (!sentence_words[index_word].equals("$") && !sentence_words[index_word].equals("[") && !sentence_words[index_word].equals("]")) {
                        count_word++;
                        entityAnnotation += "\t\t\t\t\t<wref id=\"" + id + ".p." + count_paragraph + ".s." + count_sentence + ".w." + count_word + "\" t=\"" + sentence_words[index_word] + "\"/>\n";
                    }
                }
                entityAnnotation += "\t\t\t\t</entity>\n";
            } else if (column.trim().equals("ΜΑΤΙΑ")) { //Eyes
                entityAnnotation += "\t\t\t\t<entity class=\"eyes\" datetime=\"" + datetime + "\" set=\"http://users.ntua.gr/pkouris/tagset/Modified_Pred_Sems.xml\" xml:id=\"" + id + ".text.entity." + count_entities + "\">\n";
                int count_word = first_index_countword;
                for (int index_word = first_index; index_word < second_index; index_word++) {
                    if (!sentence_words[index_word].equals("$") && !sentence_words[index_word].equals("[") && !sentence_words[index_word].equals("]")) {
                        count_word++;
                        entityAnnotation += "\t\t\t\t\t<wref id=\"" + id + ".p." + count_paragraph + ".s." + count_sentence + ".w." + count_word + "\" t=\"" + sentence_words[index_word] + "\"/>\n";
                    }
                }
                entityAnnotation += "\t\t\t\t</entity>\n";
            } else if (column.trim().equals("χείλια")) {
                entityAnnotation += "\t\t\t\t<entity class=\"lips\" datetime=\"" + datetime + "\" set=\"http://users.ntua.gr/pkouris/tagset/Modified_Pred_Sems.xml\" xml:id=\"" + id + ".text.entity." + count_entities + "\">\n";
                int count_word = first_index_countword;
                for (int index_word = first_index; index_word < second_index; index_word++) {
                    if (!sentence_words[index_word].equals("$") && !sentence_words[index_word].equals("[") && !sentence_words[index_word].equals("]")) {
                        count_word++;
                        entityAnnotation += "\t\t\t\t\t<wref id=\"" + id + ".p." + count_paragraph + ".s." + count_sentence + ".w." + count_word + "\" t=\"" + sentence_words[index_word] + "\"/>\n";
                    }
                }
                entityAnnotation += "\t\t\t\t</entity>\n";

            } else if (column.trim().equals("PERSON")) { //person
                entityAnnotation += "\t\t\t\t<entity class=\"person\" datetime=\"" + datetime + "\" set=\"http://users.ntua.gr/pkouris/tagset/Modified_Pred_Sems.xml\" xml:id=\"" + id + ".text.entity." + count_entities + "\">\n";
                int count_word = first_index_countword;
                for (int index_word = first_index; index_word < second_index; index_word++) {
                    if (!sentence_words[index_word].equals("$") && !sentence_words[index_word].equals("[") && !sentence_words[index_word].equals("]")) {
                        count_word++;
                        entityAnnotation += "\t\t\t\t\t<wref id=\"" + id + ".p." + count_paragraph + ".s." + count_sentence + ".w." + count_word + "\" t=\"" + sentence_words[index_word] + "\"/>\n";
                    }
                }
                entityAnnotation += "\t\t\t\t</entity>\n";
            } else if (column.trim().equals("δόντια")) { //person
                entityAnnotation += "\t\t\t\t<entity class=\"teeth\" datetime=\"" + datetime + "\" set=\"http://users.ntua.gr/pkouris/tagset/Modified_Pred_Sems.xml\" xml:id=\"" + id + ".text.entity." + count_entities + "\">\n";
                int count_word = first_index_countword;
                for (int index_word = first_index; index_word < second_index; index_word++) {
                    if (!sentence_words[index_word].equals("$") && !sentence_words[index_word].equals("[") && !sentence_words[index_word].equals("]")) {
                        count_word++;
                        entityAnnotation += "\t\t\t\t\t<wref id=\"" + id + ".p." + count_paragraph + ".s." + count_sentence + ".w." + count_word + "\" t=\"" + sentence_words[index_word] + "\"/>\n";
                    }
                }
                entityAnnotation += "\t\t\t\t</entity>\n";
            } else {
                entityAnnotation += "\t\t\t\t<entity class=\""+ column.trim() +"\" datetime=\"" + datetime + "\" set=\"http://users.ntua.gr/pkouris/tagset/Modified_Pred_Sems.xml\" xml:id=\"" + id + ".text.entity." + count_entities + "\">\n";
                int count_word = first_index_countword;
                for (int index_word = first_index; index_word < second_index; index_word++) {
                    if (!sentence_words[index_word].equals("$") && !sentence_words[index_word].equals("[") && !sentence_words[index_word].equals("]")) {
                        count_word++;
                        entityAnnotation += "\t\t\t\t\t<wref id=\"" + id + ".p." + count_paragraph + ".s." + count_sentence + ".w." + count_word + "\" t=\"" + sentence_words[index_word] + "\"/>\n";
                    }
                }
                entityAnnotation += "\t\t\t\t</entity>\n";
            }
        }
        return entityAnnotation;
    }
 */
