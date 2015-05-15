package snu.poi;

import twitter4j.*;
import twitter4j.auth.AccessToken;

import java.io.IOException;

public class TwitterStreaming {

    public static void main(String[] args) throws TwitterException, IOException {
        StatusListener listener = new StatusListener(){
            public void onStatus(Status status) {

                if(status.getText().matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")
                        && status.getText().length() > 20) {

                    System.out.println(status.getId());
                    System.out.println(status.getUser().getId());
                    System.out.println(status.getUser().getName());
                    System.out.println(status.getUser().getProfileImageURL());
                    if (status.getGeoLocation() == null) {
                        System.out.println(-1 + "," + -1);
                    } else {
                        System.out.println(status.getGeoLocation().getLatitude() + "," + status.getGeoLocation().getLongitude());
                    }
                    System.out.println(status.getCreatedAt().getTime());
                    if (status.getSource() == null) {
                        System.out.println("NULL");
                    } else {
                        System.out.println(status.getSource().replaceAll("\n", " "));
                    }
                    System.out.println(status.getText().replaceAll("\n", " "));
                    System.out.println();
                }
            }
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}

            public void onScrubGeo(long l, long l1) {
                // do nothing
            }

            public void onStallWarning(StallWarning stallWarning) {
                // do nothing
            }

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
    }

}
