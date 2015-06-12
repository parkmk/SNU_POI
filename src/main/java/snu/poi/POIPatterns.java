package snu.poi;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class POIPatterns {

    private List<Pair<Pattern, Integer>> patternPositionPairList = new LinkedList<Pair<Pattern, Integer>>();
    private static POIPatterns pm;

    private POIPatterns() throws IOException {
        load();
    }

    public static POIPatterns getInstance() throws IOException {
        if(pm == null) {
            pm = new POIPatterns();
        }
        return pm;
    }

    /**
     * find patterns from the given sentence
     * @param setence
     * @return matched groups
     */
    public String[] find(String setence) {
        Set<String> result = new HashSet<String>();
        for(Pair<Pattern, Integer> pair : patternPositionPairList) {
            Pattern p = pair.getKey();
            Matcher m = p.matcher(setence);
            if(m.find()) {
                String foundGroup = m.group(pair.getValue());
                if (isHanguelContained(foundGroup)) {
                    result.add(foundGroup);
                }
            }
        }
        return result.toArray(new String[0]);
    }

    private void load () throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(ResourcePath.path + File.separator + "models-full"+ File.separator+"poi.pattern"));
        while (true) {
            String line = br.readLine();
            if (line==null) {
                break;
            }
            String pattern = line.trim();
            int position = Integer.valueOf(br.readLine().trim());
            patternPositionPairList.add(new Pair<Pattern, Integer>(Pattern.compile(pattern), position));
//            System.out.println(pattern+" "+position);
        }
        br.close();
    }

    private boolean isHanguelContained (String string) {
        return string.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*");
    }

    public static void main (String[] ar) throws IOException {

        String[] lines = new String[]{
                "오늘 광화문에 갔다가 지금은 I'm at 카페 베네 봉천점 (Cafe Bene)",
                "나는 오늘 카페베네로 가는 길이야",
                "여기는 광화문 앞이다",
                "나는 어제 광화문에 오다"};

        POIPatterns pm = new POIPatterns();

        for (String line : lines) {
            System.out.println(pm.find(line));
            for(String poi : pm.find(line)) {
                System.out.println(poi);
            }
            System.out.println();
        }
    }
}
