package snu.poi;

import kr.co.shineware.util.common.model.Pair;

import java.io.*;
import java.util.List;
import java.util.Map;

public class CreateDataset {

    public static void main(String[] ar) throws IOException, ClassNotFoundException {

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("output\\output"), "UTF8"));

        for (int i = 0; i <= 8; i++) {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("data\\" + i), "UTF8"));
            while (true) {
                String id = br.readLine();
                if (id == null)
                    break;

                bw.write(id+"\n");

                String sentence = br.readLine().replaceAll("https?://\\S+\\s?", " ");
                String[] dict = POIDictionary.getInstance().getWords();
                String[] patternList = POIPatterns.getInstance().find(sentence);
                Pair<List<String>, List<String>> pair = MorphAnalyzer.getInstance().eojeolAndMorphList(sentence);
                String[] eojeolList = pair.getFirst().toArray(new String[0]);
                String[] morphList = pair.getSecond().toArray(new String[0]);
                char[] patternTag = Tagging.match(patternList, eojeolList);
                char[] dictTag = Tagging.match(dict, eojeolList);

                for (int j = 0; j < eojeolList.length; j++) {
                    bw.write(eojeolList[j].replaceAll(" ","").replaceAll("\t","") + "\t"+morphList[j] + "\t" + patternTag[j] + "\t" + dictTag[j] + "\n");
                }
            }
            br.close();
        }

        bw.close();
    }

}
