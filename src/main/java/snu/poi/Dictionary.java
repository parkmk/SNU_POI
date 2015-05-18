package snu.poi;

import javafx.util.Pair;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Kijung on 2015-05-29.
 */
public class Dictionary {

    private String[] dict;

    public Dictionary () throws IOException {
        load();
    }

    private void load () throws IOException {
        
        Set<String> excludeSet = new HashSet<String>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("models-full"+ File.separator+"notpoi.dict"), "UTF8"));
        while (true) {
            String line = br.readLine();
            if (line==null) {
                break;
            }
            String entry = line.trim().replaceAll(" ","").replaceAll("\t", "");
            excludeSet.add(entry);
        }
        br.close();
        
        br = new BufferedReader(new InputStreamReader(new FileInputStream("models-full"+ File.separator+"poi.dict"), "UTF8"));
        Set<String> dictSet = new HashSet<String> ();
        while (true) {
            String line = br.readLine();
            if (line==null) {
                break;
            }
            String entry = line.trim().replaceAll(" ","").replaceAll("\t", "");
            if(!excludeSet.contains(entry) && entry.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*") && entry.length() >= 3) {
                dictSet.add(entry);
            }
        }
        br.close();
        dict = dictSet.toArray(new String[0]);
    }

    public String[] getDict () {
        return dict;
    }
}
