package snu.poi;

import kr.co.shineware.util.common.model.Pair;

import java.io.*;
import java.sql.SQLException;
import java.util.List;

public class UploadCrawledData {

    public static void main(String[] ar) throws IOException, ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {

        DBModule dbModule = new DBModule();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(ar[0]), "UTF8"));
        while (true) {
            String line = br.readLine();
            if (line==null) {
                break;
            }
            else if(line.length()==0 || line.startsWith("[")) {
                continue;
            }

            long tweetId = Long.valueOf(line.trim());
            long userId = Long.valueOf(br.readLine().trim());
            String screenName = br.readLine().trim();
            String profileURL = br.readLine().trim();
            String[] tokens = br.readLine().split(",");
            while(tokens.length==1) {
                tokens = br.readLine().split(",");
            }
            double geoX = Double.valueOf(tokens[0]);
            double geoY = Double.valueOf(tokens[1]);
            long writeTime = Long.valueOf(br.readLine().trim());
            String app = br.readLine().replaceAll("<a href=(.*) rel=\"nofollow\">", "").replaceAll("</a>", "");
            String text = br.readLine().trim();

            try {
                List<Pair<String, Character>> result = POIFinder.getInstance().findPOI(text);
                if(result.size()==0) {
                    continue;
                }
                dbModule.insertUserInfo(userId, screenName, profileURL);
                dbModule.insertTweetInfo(tweetId, userId, text, writeTime, app, geoX, geoY);
                for(Pair<String, Character> pair : result) {
                    String poi = pair.getFirst();
                    Character type = pair.getSecond();
                    int poiId = dbModule.insertPOIInfo(poi, type);
                    dbModule.insertPOITweet(poiId, tweetId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println(tweetId);


        }
        br.close();


    }

}
