import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Master {

    private static List<Integer> pages = new ArrayList<>();

    //Guarda en un vector cada letra
    private static ArrayList<Character> characters = MasterControl.chars;
    // numero de posicion donde inicia cada palabra
    private static List<List<Integer>> wordIndexes = MasterControl.wordIndexes;
    // contador de palabras
    private static List<String> word = new ArrayList<>();
    private static List<String> word_Filter = new ArrayList<>();
    private static List<String> wordsGroup = new ArrayList<>();

    private static ArrayList<String> wordSearch = new ArrayList<>();


    public static void main(String[] args) throws IOException {
        try {
            // Primera parte
            readPDF();
            MasterControl.input(new FileReader("src/input.txt"));
            MasterControl.circularShift();
            alphabetizing();
            filter();
            group();
            output(new FileWriter(new File("src/output.txt")));

            // Segunda parte
            inputWord(new FileReader("src/input_words.txt"));
            wordSearch();
            group();
            output(new FileWriter(new File("src/output2.txt")));
        } catch (IOException e) {
            System.out.println("No se puedo escribir el archivo");
        }
    }

    private static void readPDF() throws IOException {
        Writer writer;
        String text = "";
        int contLines = 0;
        //Loading an existing document
        File file = new File("src/ejemplo.pdf");
        PDDocument document = PDDocument.load(file);

        int number_page = document.getNumberOfPages();
        PDFTextStripper pdfStripper = new PDFTextStripper();

        for (int i = 1; i <= number_page; i++) {
            pdfStripper.setStartPage(i);
            pdfStripper.setEndPage(i);

            //Retrieving text from PDF document
            text += pdfStripper.getText(document);
            //text += "-------------------------\n";

            for (int j = 0; j < text.length(); j++) {
                if (text.charAt(j) == '\n') {
                    contLines++;
                }
            }
            pages.add(contLines);
            contLines = 0;
        }

        writer = (new FileWriter(new File("src/input.txt")));
        writer.write(text);
        writer.flush();

        //Closing the document
        document.close();
    }

    private static void alphabetizing() {
        int numberPage = 1;
        for (int k = 0; k < wordIndexes.size(); k++) {
            for (int j = 0; j < wordIndexes.get(k).size() - 1; j++) {
                String palabra = "";
                int min = wordIndexes.get(k).get(j);
                int max = wordIndexes.get(k).get(j + 1) - 1;
                for (int i = min; i < max; i++) {
                    palabra += characters.get(i);
                }
                if (palabra.equals("-------------------------")) {
                    numberPage++;
                }
                word.add(palabra + "," + numberPage);
            }
        }
        java.util.Collections.sort(word);
    }

    private static void filter() {
        String word_ind;
        for (int i = 0; i < word.size(); i++) {
            word_ind = word.get(i);
            word_ind = word_ind.replaceAll("[0-9][0-9][0-9]+", "");
            word_ind = word_ind.replaceAll("[¿!¡;:\\.\\?\\-‘’=*—•®#@&$()\\“\\”\\{\\}\\\"\\\"]", " ");
            word_ind = word_ind.replaceAll("[\\s]*", "");
            word_ind = word_ind.replaceAll("[,]+", ",");
            if (!word_ind.equals("") && word_ind.length() > 6) {
                word_Filter.add(word_ind.trim());
            }
        }
    }

    private static void group() {
        String word, word_next, wordPages = "";
        String[] parts, parts_next;
        word_Filter.add(" ,1");
        boolean wordEqual = false;
        for (int i = 0; i < word_Filter.size() - 1; i++) {
            word = word_Filter.get(i);
            word_next = word_Filter.get(i + 1);
            parts = word.split(",");
            parts_next = word_next.split(",");

            //palabras iguales
            if (parts[0].equals(parts_next[0])) {
                //numeros iguales
                if (!parts[1].equals(parts_next[1])) {
                    wordPages += "," + parts[1];
                    wordEqual = true;
                }
            } else {
                if (wordEqual) {
                    wordsGroup.add(parts[0] + " " + wordPages);
                } else {
                    wordsGroup.add(parts[0] + " " + parts[1]);
                }
                wordPages = "";
                wordEqual = false;
            }
        }
    }

    private static void output(Writer writer) throws IOException {
        for (int i = 0; i < wordsGroup.size(); i++) {
            writer.write(wordsGroup.get(i));
            writer.write("\n");
        }
        writer.flush();
    }

    private static void inputWord(FileReader fileReader) throws IOException {
        String cadena;
        BufferedReader buffer = new BufferedReader(fileReader);

        while ((cadena = buffer.readLine()) != null) {
            wordSearch.add(cadena);
        }
        buffer.close();
        System.out.println();
    }

    private static void wordSearch(){
        int numberPage = 1;
        int min = 0;
        word_Filter.clear();
        wordsGroup.clear();

        for (int k = 0; k < wordIndexes.size(); k++) {
            for (int j = 0; j < wordIndexes.get(k).size() - 1; j++) {
                String palabra = "";
                int max = wordIndexes.get(k).get(wordIndexes.get(k).size() - 1);
                for (int i = min; i < max; i++) {
                    palabra += characters.get(i);
                }
                if (palabra.contains("-------------------------")) {
                    numberPage++;
                }
                //System.out.println(palabra);
                for (int l = 0; l < wordSearch.size(); l++) {
                    if (palabra.toLowerCase().contains(wordSearch.get(l).toLowerCase())) {
                        word_Filter.add(wordSearch.get(l) + " , " + numberPage);
                    }
                }
                min = max;
            }
        }
        java.util.Collections.sort(word_Filter);
    }
}
