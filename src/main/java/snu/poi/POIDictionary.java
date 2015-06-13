package snu.poi;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class POIDictionary {

    private static POIDictionary dict;
    private Map<String, Character> typeMap = new HashMap ();
    private String[] words;

//    private final static Pattern parenthesisPattern = Pattern.compile("(.*)(\\s*)\\((.*)\\)");
    private final static Pattern branchPattern =  Pattern.compile("(.*)(\\s+)(.*)점$");

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
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(ResourcePath.path + File.separator + "models-full"+ File.separator+"notpoi.dict"), "UTF8"));
        while (true) {
            String line = br.readLine();
            if (line==null) {
                break;
            }
            String entry = line.trim().replaceAll(" ","").replaceAll("\t", "");
            excludeSet.add(entry);
        }
        br.close();
        
        br = new BufferedReader(new InputStreamReader(new FileInputStream(ResourcePath.path + File.separator + "models-full"+ File.separator+"poi.dict"), "UTF8"));
        while (true) {
            String line = br.readLine();
            if (line==null) {
                break;
            }
            String[] tokens = line.replaceAll("\\((.*)\\)","").trim().split("\t");
            if(tokens.length==1) {
                continue;
            }
            List<String> entryList = new LinkedList<String>();
            entryList.add(tokens[0].replaceAll(" ", ""));

//            Matcher m = parenthesisPattern.matcher(tokens[0]);
//            if(m.find()) {
//                entryList.add(m.group(1).replaceAll(" ", ""));
//                entryList.add(m.group(3).replaceAll(" ", ""));
//            }

            Matcher m = branchPattern.matcher(tokens[0]);
            if(m.find()) {
                entryList.add(m.group(1).replaceAll(" ", ""));
            }

            char type = tokens[1].charAt(0);
            for(String entry : entryList) {
                if (!excludeSet.contains(entry) && entry.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*") && entry.length() >= 3) {
                    if (typeMap.containsKey(entry)) {
                        if (type != 'U') { //if type is known
                            typeMap.put(entry, type);
                        }
                    } else {
                        typeMap.put(entry, type);
                    }
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
