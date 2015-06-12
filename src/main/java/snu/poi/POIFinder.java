package snu.poi;


import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.objectbank.ObjectBank;
import edu.stanford.nlp.sequences.SeqClassifierFlags;
import edu.stanford.nlp.util.Quadruple;
import kr.co.shineware.util.common.model.Pair;

import java.io.*;
import java.util.*;

public class POIFinder {

    private static POIFinder finder;
    private CRFClassifier<CoreLabel> crf;

    private POIFinder() throws IOException, ClassNotFoundException {
        String loadPath = ResourcePath.path + File.separator + "models-full" + File.separator + "kor-only-model.ser.gz";
        Properties props = new Properties();
        props.setProperty("loadClassifier", loadPath);
        SeqClassifierFlags flags = new SeqClassifierFlags(props);
        crf = new CRFClassifier<CoreLabel>(flags);
        crf.loadClassifierNoExceptions(loadPath, props);
        crf.loadTagIndex();
    }

    /**
     * return the singleton instance
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static POIFinder getInstance() throws IOException, ClassNotFoundException {
        if (finder == null) {
            finder = new POIFinder();
        }
        return finder;
    }

    /**
     * Return a list of POIs and their types from the given sentence
     * @param sentence
     * @return
     * @throws IOException
     */

    public List<Pair<String, Character>> findPOI (String sentence) throws IOException {

        sentence = sentence.replaceAll("https?://\\S+\\s?", " "); //remove url
        String[] dict = POIDictionary.getInstance().getWords();
        Map<String, Character> typeMap = POIDictionary.getInstance().getTypeMap();
        String[] patternList = POIPatterns.getInstance().find(sentence);
        Pair<List<String>, List<String>> pair = MorphAnalyzer.getInstance().eojeolAndMorphList(sentence);
        String[] eojeolList = pair.getFirst().toArray(new String[0]);
        String[] morphList = pair.getSecond().toArray(new String[0]);
        char[] patternTag = Tagging.match(patternList, eojeolList);
        char[] dictTag = Tagging.match(dict, eojeolList);

        StringBuilder buff = new StringBuilder();
        for (int i = 0; i < eojeolList.length; i++) {
            buff.append(eojeolList[i].replaceAll(" ","").replaceAll("\t","") + "\t"+morphList[i] + "\t" + patternTag[i] + "\t" + dictTag[i] + "\t" + "O" + "\n");
        }

        Character[] classifiedResult = classify(buff.toString());

        List<Pair<String, Character>> poiAndTypeList = new LinkedList();
        Set<String> poiSet = new HashSet<String>();
        StringBuffer buffer = new StringBuffer();

        char type = 'U';
        for (int i = 0; i < eojeolList.length; i++) {
            if(classifiedResult[i]=='A') {
                buffer.append(eojeolList[i]);
                if (typeMap.containsKey(buffer.toString())) {
                    type = typeMap.get(buffer.toString());
                }
            }
            else {
                if(buffer.length() > 0 ) {
                    if (!poiSet.contains(buffer.toString())) {
                        poiAndTypeList.add(new Pair<String, Character>(buffer.toString(), type));
                        poiSet.add(buffer.toString());
                    }
                    buffer.setLength(0);
                    type = 'U';
                }
            }
        }
        if(buffer.length() > 0 && !poiSet.contains(buffer.toString())) {
            poiAndTypeList.add(new Pair<String, Character>(buffer.toString(), type));
            poiSet.add(buffer.toString());
        }

        return poiAndTypeList;
    }

    /**
     * FInd positions of POIs within the given sentence
     * @param pioAndTypeList
     * @param originalSentence
     * @return
     */
    public List<Quadruple<Integer, Integer, String, Character>> findPosition(List<Pair<String, Character>> pioAndTypeList, String originalSentence) {

        List<Quadruple<Integer, Integer, String, Character>> result = new LinkedList<>();
        for(Pair<String, Character> pair : pioAndTypeList) {
            String poi = pair.getFirst();
            Character type = pair.getSecond();
            int i=0;
            while (i < originalSentence.length()) {
                int currentSetenceIndex = i;
                int currentPOIIndex = 0;
                while(true) {
                    if(currentSetenceIndex >= originalSentence.length() || currentPOIIndex >= poi.length()) {
                        break;
                    }
                    else if(originalSentence.charAt(currentSetenceIndex)==' ') {
                        currentSetenceIndex++;
                    }
                    else if(originalSentence.charAt(currentSetenceIndex) == poi.charAt(currentPOIIndex)) {
                        currentSetenceIndex++;
                        currentPOIIndex++;
                    }
                    else {
                        break;
                    }
                }
                if (currentPOIIndex==poi.length()) {
                    result.add(new Quadruple<Integer, Integer, String, Character>(i, currentSetenceIndex, poi, type));
                }
                i = currentSetenceIndex+1;
            }
        }
        return result;
    }

    /**
     * classify whether each eojeol is POI or not
     * @param input
     * @return 'A': POI, 'O' NOT
     */
    private Character[] classify (String input) {

        List<Character> result = new LinkedList<Character>();

        ObjectBank<List<CoreLabel>> documents =
                crf.makeObjectBankFromString(input, crf.defaultReaderAndWriter());

        for (List<CoreLabel> document : documents) {
            List<CoreLabel> classifiedResult = crf.classify(document);
            for(CoreLabel label : classifiedResult) {
                result.add(label.getString(CoreAnnotations.AnswerAnnotation.class).charAt(0));
            }
        }

        return result.toArray(new Character[0]);
    }

    public static void main(String[] ar) throws IOException, FileNotFoundException, ClassNotFoundException {

//        for(int i=0; i<=8; i++) {
//            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("data\\"+i), "UTF8"));
//            while (true) {
//                String id = br.readLine();
//                if (id == null)
//                    break;
//                String sentence = br.readLine().replaceAll("https?://\\S+\\s?", " ");
//                List<Pair<String, Character>> poiAndTypeList = POIFinder.getInstance().findPOI(sentence);
//                for(Pair<String, Character> poiAndType : poiAndTypeList) {
//                    System.out.println(poiAndType.getFirst() + "\t" + poiAndType.getSecond());
//                }
//                if(poiAndTypeList.size() > 0) {
//                    System.out.println(sentence);
//                    System.out.println(POIFinder.getInstance().findPosition(poiAndTypeList, sentence));
//                }
//            }
//            br.close();
//        }

        List<Pair<String, Character>> poiAndTypeList = POIFinder.getInstance().findPOI("I'm at 고속터미널역 (Express Bus Terminal Stn.) I'm at 고속터미널역 (Express Bus Terminal Stn.)");
        for(Pair<String, Character> poiAndType : poiAndTypeList) {
            System.out.println(poiAndType.getFirst() + "\t" + poiAndType.getSecond());
        }
    }

}
