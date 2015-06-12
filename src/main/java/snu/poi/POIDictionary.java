package snu.poi;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class POIDictionary {

    private static POIDictionary dict;
    private Map<String, Character> typeMap = new HashMap ();
    private String[] words;

    private  POIDictionary() throws IOException {
        load();
    }

    public static POIDictionary getInstance() throws IOException {
        if (dict==null) {
            dict = new POIDictionary();
        }
        return dict;
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
        while (true) {
            String line = br.readLine();
            if (line==null) {
                break;
            }
            String[] tokens = line.trim().replaceAll(" ","").split("\t");
            String entry = tokens[0];
            char type = tokens[1].charAt(0);
            if(!excludeSet.contains(entry) && entry.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*") && entry.length() >= 3) {
                if (typeMap.containsKey(entry)) {
                    if (type!='U') { //if type is known
                        typeMap.put(entry, type);
                    }
                }
                else {
                    typeMap.put(entry, type);
                }
            }
        }
        br.close();
        int length = typeMap.size();
        words = typeMap.keySet().toArray(new String[0]);
    }

    public String[] getWords() {
        return words;
    }

    public Map<String, Character> getTypeMap() {
        return typeMap;
    }
}
