package snu.poi;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import kr.co.shineware.nlp.komoran.core.analyzer.Komoran;
import kr.co.shineware.util.common.model.Pair;

public class MorphAnalyzer {

    private static Komoran komoran = new Komoran(ResourcePath.path + File.separator + "models-full");
    private static MorphAnalyzer morphAnalyzer;

    private MorphAnalyzer() {
    }

    /**
     * return a singleton instance of MorphAnalyzer
     * @return
     */
    public static MorphAnalyzer getInstance() {
        if(morphAnalyzer ==null) {
            morphAnalyzer = new MorphAnalyzer();
        }
        return morphAnalyzer;
    }

    /**
     * run the morph analysis w.r.t the given sentence
     * @param sentence
     * @return eojeol list, morph list
     */
    public Pair<List<String>, List<String>> eojeolAndMorphList (String sentence) {
        return divideEojeolAndMorphs(mergeNieunLieul(flattenList(komoran.analyze(sentence))));
    }

    /**
     * flattern the list of lists
     * @param list
     * @return
     */
    private List<Pair<String, String>> flattenList (List<List<Pair<String, String>>> list) {
        List<Pair<String, String>> result = new LinkedList();
        for(List<Pair<String, String>> subList : list) {
            result.addAll(subList);
        }
        return result;
    }

    /**
     * remove nieun (ㄴ) and lieul (ㄹ) from the result
     * @param eojeolResult
     * @return
     */
    private List<Pair<String, String>> mergeNieunLieul (List<Pair<String, String>> eojeolResult) {
        for(int i = 1; i < eojeolResult.size(); i++){
            String word = eojeolResult.get(i).getFirst().replaceAll(" ", "").replaceAll("\t", "");
            char[] charArr = word.toCharArray();
            if(charArr.length==1 && ((int)charArr[0]==12596 || (int)charArr[0]==12601)){
                int addition = (int)charArr[0]==12596 ? 4 : 8;
                char[] wordArr = eojeolResult.get(i-1).getFirst().toCharArray();
                wordArr[wordArr.length-1] += addition;
                eojeolResult.get(i-1).setFirst(String.valueOf(wordArr));
                eojeolResult.remove(i);
                i--;
            }
        }
        return eojeolResult;
    }

    /**
     * divide the given list into an eojeol list and a morph list
     * @param pairList
     * @return eojeol list, morph list
     */
    private Pair<List<String>, List<String>> divideEojeolAndMorphs(List<Pair<String, String>> pairList) {
        List<String> eojeolList = new LinkedList<String>();
        List<String> morphList = new LinkedList<String>();
        for(Pair<String, String> pair : pairList) {
            eojeolList.add(pair.getFirst());
            morphList.add(pair.getSecond());
        }
        return new Pair(eojeolList, morphList);
    }

}