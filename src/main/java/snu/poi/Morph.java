package snu.poi;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

import com.sun.xml.internal.fastinfoset.util.CharArray;
import kr.co.shineware.nlp.komoran.core.analyzer.Komoran;
import kr.co.shineware.util.common.model.Pair;

public class Morph {
    public static void main(String[] args) throws IOException {

        Komoran komoran = new Komoran("models-full");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("output\\output"),"UTF8"));
        String[] dict = new Dictionary().getDict();
        PatternMatching pm = new PatternMatching();


        for(int i=0; i<=8; i++) {

            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("data\\"+i), "UTF8"));

            while (true) {
                String id = br.readLine();
                if (id == null)
                    break;
                String sentence = br.readLine().replaceAll("https?://\\S+\\s?", " ");
                String[] patternList = pm.find(sentence).toArray(new String[0]);

                bw.write(id+"\n");
//                bw.write(sentence); //debug
                Pair<List<String>, List<String>> pair = divideWordsAndMorphs(mergeNieunLieul(flattenList(komoran.analyze(sentence))));
                String[] eojeolList = pair.getFirst().toArray(new String[0]);
                String[] morphList = pair.getSecond().toArray(new String[0]);
                char[] patternTag = Tagging.run(patternList, eojeolList);
                char[] dictTag = Tagging.run(dict, eojeolList);

                for (int j = 0; j < eojeolList.length; j++) {
//                    if(dictTag[j]=='P') { //debug
//                        System.out.println(eojeolList[j]); //debug
//                        System.out.println(sentence); //debug
//                    }
                    bw.write(eojeolList[j].replaceAll(" ","").replaceAll("\t","") + "\t"+morphList[j] + "\t" + patternTag[j] + "\t" + dictTag[j] + "\n");
                }
            }
            br.close();
        }
        bw.close();
    }

    /**
     * flattern the list of lists
     * @param list
     * @return
     */
    private static List<Pair<String, String>> flattenList (List<List<Pair<String, String>>> list) {
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
    private static List<Pair<String, String>> mergeNieunLieul (List<Pair<String, String>> eojeolResult) {
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

    private static Pair<List<String>, List<String>> divideWordsAndMorphs (List<Pair<String, String>> pairList) {
        List<String> eojeolList = new LinkedList<String>();
        List<String> morphList = new LinkedList<String>();
        for(Pair<String, String> pair : pairList) {
            eojeolList.add(pair.getFirst());
            morphList.add(pair.getSecond());
        }
        return new Pair(eojeolList, morphList);
    }
}