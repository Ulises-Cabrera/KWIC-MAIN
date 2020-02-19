import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;
import java.util.*;

public class Master2 {

    private static char[] pdfRead;
    private static List<Integer> pages = new ArrayList<>();

    private static ArrayList<Character> characters = new ArrayList<>();
    private static ArrayList<Integer> lineIndexes = MasterControl.lineIndexes;
    private static int[][] alphabetizedIndexes;
    private static ArrayList<Character> chars;
    private static int sizeLineIndexes;

    private static Map<String, Set<Integer>> map = new LinkedHashMap<>();
    private static ArrayList<String> wordSearch = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        try {
            readPDF();
            filter();
            filter2();
            MasterControl.input(new FileReader(new File("src/input2.txt")));
            MasterControl.circularShift();
            MasterControl.alphabetizing();
            alphabetizedIndexes = MasterControl.alphabetizedIndexes;
            chars = MasterControl.chars;
            sizeLineIndexes = MasterControl.sizeLineIndexes;
            group();
            output(new FileWriter(new File("src/output.txt")));

            //Segunda parte
            inputWord(new FileReader("src/input_words.txt"));
            output2(new FileWriter(new File("src/output3.txt")));

        } catch (IOException e) {
            System.out.println("No se puedo escribir el archivo");
        }
    }

    private static void readPDF() throws IOException {
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

            for (int j = 0; j < text.length(); j++) {
                if (text.charAt(j) == '\n') {
                    contLines++;
                }
            }
            pages.add(contLines);
            contLines = 0;
        }
        pdfRead = text.toCharArray();

        //Closing the document
        document.close();
    }

    private static void filter() {
        for (Character caracter : pdfRead) {
            if (caracter.equals('(') || caracter.equals(')') || caracter.equals('$') || caracter.equals('&') || caracter.equals('@')
                    || caracter.equals('#') || caracter.equals('®') || caracter.equals('•') || caracter.equals('—') || caracter.equals('*')
                    || caracter.equals('‘') || caracter.equals('’') || caracter.equals('?') || caracter.equals('.') || caracter.equals(':')
                    || caracter.equals(';') || caracter.equals('¡') || caracter.equals('!') || caracter.equals('¿') || caracter.equals(',')
                    || caracter.equals('-') || caracter.equals('+') || caracter.equals('”') || caracter.equals('“') || caracter.equals('=')
                    || caracter.equals('"') || caracter.equals('{') || caracter.equals('}')
                    || caracter.equals('0') || caracter.equals('1') || caracter.equals('2') || caracter.equals('3') || caracter.equals('4')
                    || caracter.equals('5') || caracter.equals('6') || caracter.equals('7') || caracter.equals('8') || caracter.equals('9')) {
            } else {
                characters.add(caracter);
            }
        }
    }

    private static void filter2() throws IOException {
        Writer writer;
        writer = (new FileWriter(new File("src/input2.txt")));
        String word = "";
        int cont = 0;
        int page = 0;

        for (Character charact : characters) {
            word += charact;
            if (charact.equals(' ') || charact.equals('\n')) {
                if (word.trim().length() > 5) {

                    for (int i = 0; i < pages.size(); i++) {
                        if (cont < pages.get(i)) {
                            page = i + 1;
                            break;
                        }
                    }
                    word = word.trim();
                    word += "," + page;
                    if (!word.contains("\n")) {
                        word += '\n';
                    }
                    writer.write(word);
                    cont++;
                }
                word = "";
            }
        }
        writer.flush();
    }

    private static void group() {
        ArrayList<String> words = new ArrayList<>();
        String word = "";
        int num = 0;

        for (int index = 0; index < alphabetizedIndexes.length; ++index) {
            int wordStart = alphabetizedIndexes[index][2];
            int lineEnd = sizeLineIndexes > alphabetizedIndexes[index][0] + 1 ? lineIndexes.get(alphabetizedIndexes[index][0] + 1) : chars.size();

            // Desde la palabra hasta el final de la linea
            int wordEnd = wordStart;
            for (int charIndex = wordStart; charIndex < lineEnd; charIndex++) {
                word += chars.get(charIndex);
                wordEnd++;
            }

            if (wordEnd == chars.size()) {
                word += " ";
            }

            if (index + 1 < alphabetizedIndexes.length)
                word += '\n';

            if (word.length() > 3)
                words.add(word);
            word = "";
        }

        String[] parts;
        for (String wor : words) {
            parts = wor.split(",");
            map.putIfAbsent(parts[0], new HashSet<>());
            parts[1] = parts[1].replace("\n", "");
            num = Integer.parseInt(parts[1].trim());
            map.get(parts[0]).add(num);
        }
    }

    private static void output(Writer writer) throws IOException {
        map.forEach((k, v) -> {
            try {
                writer.write(k + " " + v + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
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

    private static void search(Writer writer) throws IOException {
        map.forEach((k, v) -> {
            try {
                for (int l = 0; l < wordSearch.size(); l++) {
                    if (wordSearch.get(l).toLowerCase().contains(k.toLowerCase())) {
                        writer.write(k + " " + v + "\n");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        writer.flush();
    }

    private static void output2(Writer writer) throws IOException {
        String[] parts;
        List<Set<Integer>> wordList = new ArrayList<>();
        List<List<Integer>> wordListFound = new ArrayList<>();
        String outputWords = "";

        for (int i = 0; i < wordSearch.size(); i++) {
            parts = wordSearch.get(i).split(" ");

            for (int j = 0; j < parts.length; j++) {
                outputWords += parts[j] + " ";
                if (map.get(parts[j]) != null) {
                    wordList.add(map.get(parts[j]));
                }
            }
            if (wordList.size() > 1) {
                Iterator<Integer> prueba3 = wordList.get(0).iterator();
                Iterator<Integer> prueba4 = wordList.get(1).iterator();

                wordListFound.add(new ArrayList<>());
                for (int k = 0; k < wordList.get(0).size(); k++) {
                    wordListFound.get(0).add(prueba3.next());
                }

                wordListFound.add(new ArrayList<>());
                for (int k = 0; k < wordList.get(1).size(); k++) {
                    wordListFound.get(1).add(prueba4.next());
                }

                outputWords += " [";
                for (int j = 0; j < wordList.get(0).size(); j++) {
                    for (int k = 0; k < wordListFound.get(1).size(); k++) {
                        if (wordListFound.get(0).get(j).equals(wordListFound.get(1).get(k))) {
                            outputWords += wordListFound.get(0).get(j) + " ";
                        }
                    }
                }
                outputWords += "]";
                writer.write(outputWords + "\n");
            }
            wordList.clear();
            wordListFound.clear();
            outputWords = "";
        }
        writer.flush();
    }
}
