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

        BufferedWriter bw = new BufferedWriter(new FileWriter("output\\output"));

        for(int i=0; i<=8; i++) {

            BufferedReader br = new BufferedReader(new FileReader("data\\"+i));

            while (true) {
                String id = br.readLine();
                if (id == null)
                    break;
                String sentence = br.readLine().replaceAll("https?://\\S+\\s?", " ");

                bw.write(id+"\n");
                List<List<Pair<String, String>>> result = komoran.analyze(sentence);

                for (List<Pair<String, String>> eojeolResult : result) {
                    for(int j=1; j<eojeolResult.size(); j++){
                        String word = eojeolResult.get(j).getFirst().replaceAll(" ", "").replaceAll("\t", "");
                        char[] charArr = word.toCharArray();
                        if(charArr.length==1 && ((int)charArr[0]==12596 || (int)charArr[0]==12601)){
                            int addition = (int)charArr[0]==12596 ? 4 : 8;
                            char[] wordArr = eojeolResult.get(j-1).getFirst().toCharArray();
                            wordArr[wordArr.length-1] += addition;
                            eojeolResult.get(j-1).setFirst(String.valueOf(wordArr));
                            eojeolResult.remove(j);
                            j--;
                        }
                    }
                    for (Pair<String, String> wordMorph : eojeolResult) {
                        String word = wordMorph.getFirst().replaceAll(" ", "").replaceAll("\t", "");
                        bw.write(word + "\tO\n");
                    }
                }
            }

            br.close();
        }

        bw.close();
    }
}