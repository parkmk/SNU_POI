package snu.poi;

import kr.co.shineware.nlp.komoran.core.analyzer.Komoran;
import kr.co.shineware.util.common.model.Pair;

import java.io.*;
import java.util.List;

public class MorphAnalyzerTest {
    public static void main(String[] args) throws IOException {
        Komoran komoran = new Komoran("models-full");

        List<List<Pair<String, String>>> result = komoran.analyze("에");

        for (List<Pair<String, String>> eojeolResult : result) {
            for (Pair<String, String> wordMorph : eojeolResult) {
                System.out.println(wordMorph.getFirst());
                char[] aa = wordMorph.getFirst().toCharArray();
                for(int i=0; i<aa.length; i++){
                    System.out.println((int)aa[i]);
                }
            }
            System.out.println();
        }

    }
}