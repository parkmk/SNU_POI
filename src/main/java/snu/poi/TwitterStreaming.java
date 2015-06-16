package snu.poi;

import kr.co.shineware.util.common.model.Pair;
import twitter4j.*;
import twitter4j.auth.AccessToken;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class TwitterStreaming {

    public static void main(String[] args) throws TwitterException, IOException, ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {

        DBModule dbModule = new DBModule();

        StatusListener listener = new StatusListener(){
            public void onStatus(Status status) {

                if(status.getText().matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*") && status.getText().length() > 20) {

                    long tweetId = status.getId();
                    long userId = status.getUser().getId();
                    String screenName = status.getUser().getName();
                    String profileURL = status.getUser().getProfileImageURL();
                    double geoX = -1;
                    double geoY = -1;
                    if (status.getGeoLocation() != null) {
                        geoX = status.getGeoLocation().getLatitude();
                        geoY =  status.getGeoLocation().getLongitude();
                    }
                    long writeTime = status.getCreatedAt().getTime();
                    String app = "NULL";
                    if (status.getSource() != null) {
                        app = status.getSource().replaceAll("\n", " ")
                                .replaceAll("<a href=(.*) rel=\"nofollow\">", "").replaceAll("</a>","");;
                    }
                    String text = status.getText().replaceAll("\n", " ");
                    try {
                        dbModule.insertUserInfo(userId, screenName, profileURL);
                        dbModule.insertTweetInfo(tweetId, userId, text, writeTime, app, geoX, geoY);
                        List<Pair<String, Character>> result = POIFinder.getInstance().findPOI(text);
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
            }
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
            public void onScrubGeo(long l, long l1) {}
            public void onStallWarning(StallWarning stallWarning) {}
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };

        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(listener);
        twitterStream.setOAuthConsumer("ZZmTMFeKXMMEMzK9oLKvyyBzv", "2weNQ7pXUJNpheBiWIEpE7prad4bt5K9wWWuGgefSgV0hIB630");
        twitterStream.setOAuthAccessToken(new AccessToken("240062213-ig6zKyayW8ljEF5F9cOOmyqhDO1aKcJl3TOeQHor", "Pa9vcMpWAv4syHcdz7JfCYT6ydZ2hmoKWL92YVDzzV1t0"));

        // sample() method internally creates a thread which manipulates TwitterStream and calls these adequate listener methods continuously.
        FilterQuery filterQuery = new FilterQuery();
        filterQuery.locations(new double[][]{{125, 34}, {130, 39}});
//        filterQuery.track(new String[]{"Foursquare"});
//        filterQuery.language(new String[]{"ko"});
        twitterStream.filter(filterQuery);

//        dbModule.close();
    }

}
