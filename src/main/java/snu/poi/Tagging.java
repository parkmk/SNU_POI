package snu.poi;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Kijung on 2015-05-29.
 */
public class Tagging {

    /**
     * decide whether each eojel is a part of the given words
     * @param wordList
     * @param eojeolList
     * @return 'P': part of words, 'X': not a part of words
     */
    public static char[] match(String[] wordList, String[] eojeolList) {

        //remove space
        String[] trimmedWordList = new String[wordList.length];
        for (int i=0; i<wordList.length; i++) {
            trimmedWordList[i] = wordList[i].replaceAll(" ","").replaceAll("\t", "");
        }

        //concat morphs
        String sentence = "";
        for (String eojeol : eojeolList) {
            sentence += eojeol.replaceAll(" ","").replaceAll("\t", "");
        }

        List<int[]> positionList = new LinkedList<int[]>();
        for(String word : trimmedWordList) {
            int fromIndex = 0;
            while (true) {
                int startIndex = sentence.indexOf(word, fromIndex);
                if (startIndex == -1) {
                    break;
                }
                int endIndx = startIndex+word.length();
                positionList.add(new int[]{startIndex,endIndx});
                fromIndex = endIndx;
            }
        }

        char[] tags = new char[eojeolList.length];
        int currentIndex = 0;
        for (int i = 0; i < eojeolList.length; i++) {
            String eojeol = eojeolList[i].replaceAll(" ","").replaceAll("\t", "");
            int endIndex = currentIndex + eojeol.length();
            for(int[] position : positionList) {
                if(currentIndex == position[0] && endIndex == position[1]) {
                    tags[i] = 'P'; //exact matching
                    break;
                }
                else if(currentIndex == position[0] && endIndex < position[1]) {
                    tags[i] = 'S'; //start
                    break;
                }
                else if(currentIndex > position[0] && endIndex == position[1]) {
                    tags[i] = 'E'; //end
                    break;
                }
                else if(currentIndex > position[0] && endIndex < position[1]) {
                    tags[i] = 'M'; //mid
                    break;
                }
            }
            if (tags[i] == 0) {
                tags[i] = 'X';
            }
            currentIndex = endIndex;
        }

        char[] newTags = new char[eojeolList.length];
        int startIndex = -1;
        for (int i = 0; i < eojeolList.length; i++) {
            if(tags[i] == 'P' || tags[i] == 'X') {
                newTags[i] = tags[i];
                startIndex = -1;
            }
            else if(tags[i] == 'M') {
                newTags[i] = 'X';
            }
            else if(tags[i] == 'S') {
                newTags[i] = 'X';
                startIndex = i;
            }
            else if(tags[i] == 'E') {
                newTags[i] = 'X';
                if (startIndex > 0) {
                    for(int j = startIndex; j<=i; j++) {
                        newTags[j] = 'P';
                    }
                }
                startIndex = -1;
            }
        }

        return newTags;
    }

    public static void main (String[] ar) {
        String[] wordList =new String[]{"카페 베네 봉천점", "광화문"};
        String[] morphList = new String[]{"나는", "오늘", "카페", "베네", "봉천","점", "에", "갔다가", "지금은", "광화문"};
        char[] tags = match(wordList, morphList);
        for(int i = 0; i < morphList.length; i++) {
            System.out.println(morphList[i]+" "+tags[i]);
        }
    }

}
