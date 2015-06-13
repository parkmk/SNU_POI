package snu.poi;

import java.sql.*;
import java.text.SimpleDateFormat;

public class DBModule {

//    private static final String DB_URL = "jdbc:mysql://114.108.167.117:3306/snu_poi?useUnicode=true&characterEncoding=UTF-8";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/snu_poi?useUnicode=true&characterEncoding=UTF-8";
    private static final String USER = "snu";
    private static final String PASS = "2154";
    private Connection conn = null;
    private Statement st = null;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    public DBModule() throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        conn = DriverManager.getConnection(DB_URL, USER, PASS);
        st = conn.createStatement();
        st.execute("SET names UTF8");
    }

    public int insertPOIInfo(String POIName, char category) throws SQLException {

        String selectQuery = String.format("SELECT id FROM poi_info WHERE name_nospace='%s'", addSlashes(POIName.replaceAll(" ","")));
        ResultSet rs = st.executeQuery(selectQuery);
        while(rs.next()){
            int id  = rs.getInt("id");
            return id;
        }

        String insertQuery = String.format("INSERT INTO poi_info (name, name_nospace, source, type) VALUES ('%s', '%s', 3, '%s')",
                addSlashes(POIName), addSlashes(POIName.replaceAll(" ","")), String.valueOf(category));
//        System.out.println(insertQuery);
        st.executeUpdate(insertQuery, Statement.RETURN_GENERATED_KEYS);
        rs = st.getGeneratedKeys();
        while(rs.next()){
            int id  = rs.getInt(1);
            return id;
        }

        return -1;
    }

    public int insertUserInfo(long id, String screenName, String url) throws SQLException {
        String insertQuery = String.format("INSERT IGNORE INTO user_info (id, screen_name, profile_url) VALUES (%d, '%s', '%s')",
                id, addSlashes(screenName), url);
        return st.executeUpdate(insertQuery);
    }

    public int insertTweetInfo(long id, long userId, String text, long writeTime, String app, double geoX, double geoY) throws SQLException {
        Date date = new Date(writeTime);
        String writeTimeText = dateFormat.format(date);
        String insertQuery = String.format("INSERT IGNORE INTO tweet_info (id, user_id, text, write_time, app, geo_x, geo_y) " +
                        "VALUES (%d, %d, '%s', '%s', '%s', %f, %f)",
                id, userId, addSlashes(text), writeTimeText, addSlashes(app), geoX, geoY);
//        System.out.println(insertQuery);
        return st.executeUpdate(insertQuery);
    }

    public int insertPOITweet(int poiId, long tweetId) throws SQLException {

        String insertQuery = String.format("INSERT IGNORE INTO poi_tweet (poi_id, tweet_id) " +
                        "VALUES (%d, %d)", poiId, tweetId);
//        System.out.println(insertQuery);
        return st.executeUpdate(insertQuery);
    }

    public void close() throws SQLException {
        conn.close();
    }

    public static String addSlashesSearchMode(String s) {
        return addSlashes(s, true);
    }

    public static String addSlashes(String s) {
        return addSlashes(s, false);
    }

    private static String addSlashes(String s, boolean search) {
        if (s == null) {
            return s;
        }
        String[][] chars;
        if(!search) {
            chars = new String[][ ]{
                    {"\\",  "\\\\"},
                    {"\0", "\\0"},
                    {"'", "\\'"},
                    {"\"",  "\\\""},
                    {"\b",  "\\b"},
                    {"\n",  "\\n"},
                    {"\r",  "\\r"},
                    {"\t",  "\\t"},
                    {"\\Z", "\\\\Z"}, // not sure about this one
                    {"%", "\\%"},     // used in searching
                    {"_", "\\_"}
            };
        } else {
            chars = new String[][ ]{
                    {"\\",  "\\\\"},
                    {"\0", "\\0"},
                    {"'", "\\'"},
                    {"\"",  "\\\""},
                    {"\b",  "\\b"},
                    {"\n",  "\\n"},
                    {"\r",  "\\r"},
                    {"\t",  "\\t"},
                    {"\\Z", "\\\\Z"}, // not sure about this one
            };
        }
        for (String[] c : chars) {
            s = s.replace(c[0], c[1]);
        }
        return s;
    }

    public static void main (String[] ar) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        DBModule db = new DBModule();
        System.out.println(db.insertPOIInfo("이남장", 'U'));
        System.out.println(db.insertUserInfo(11223412, "신기정", "kijungshin.com"));
        System.out.println(db.insertTweetInfo(11223412, 11223412, "신기정", 1431925279000L, "android", -1, -1));
        System.out.println(db.insertPOITweet(294, 11223412));
        db.close();
    }

}