package snu.poi;

import java.sql.*;
import java.text.SimpleDateFormat;

public class DBModule {

    private static final String DB_URL = "jdbc:mysql://114.108.167.117:3306/snu_poi?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true";
//    private static final String DB_URL = "jdbc:mysql://localhost:3306/snu_poi?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true";
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
                        "VALUES (%d, %d, '%s', '%s', '%s', %f, %f) ON DUPLICATE KEY UPDATE text='%s'",
                id, userId, removeEmojis(addSlashes(text)), writeTimeText, addSlashes(app), geoX, geoY, removeEmojis(addSlashes(text)));
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

    private static String removeEmojis(String string) {
        return string.replaceAll("[\\x{1F600}-\\x{1F6FF}]", "")
                .replaceAll("[\\x{203C}\\x{2049}\\x{20E3}\\x{2122}\\x{2139}\\x{2194}-\\x{2199}\\x{21A9}-\\x{21AA}\\x{231A}-\\x{231B}\\x{23E9}-\\x{23EC}\\x{23F0}\\x{23F3}\\x{24C2}\\x{25AA}-\\x{25AB}\\x{25B6}\\x{25C0}\\x{25FB}-\\x{25FE}\\x{2600}-\\x{2601}\\x{260E}\\x{2611}\\x{2614}-\\x{2615}\\x{261D}\\x{263A}\\x{2648}-\\x{2653}\\x{2660}\\x{2663}\\x{2665}-\\x{2666}\\x{2668}\\x{267B}\\x{267F}\\x{2693}\\x{26A0}-\\x{26A1}\\x{26AA}-\\x{26AB}\\x{26BD}-\\x{26BE}\\x{26C4}-\\x{26C5}\\x{26CE}\\x{26D4}\\x{26EA}\\x{26F2}-\\x{26F3}\\x{26F5}\\x{26FA}\\x{26FD}\\x{2702}\\x{2705}\\x{2708}-\\x{270C}\\x{270F}\\x{2712}\\x{2714}\\x{2716}\\x{2728}\\x{2733}-\\x{2734}\\x{2744}\\x{2747}\\x{274C}\\x{274E}\\x{2753}-\\x{2755}\\x{2757}\\x{2764}\\x{2795}-\\x{2797}\\x{27A1}\\x{27B0}\\x{2934}-\\x{2935}\\x{2B05}-\\x{2B07}\\x{2B1B}-\\x{2B1C}\\x{2B50}\\x{2B55}\\x{3030}\\x{303D}\\x{3297}\\x{3299}\\x{1F004}\\x{1F0CF}\\x{1F170}-\\x{1F171}\\x{1F17E}-\\x{1F17F}\\x{1F18E}\\x{1F191}-\\x{1F19A}\\x{1F1E7}-\\x{1F1EC}\\x{1F1EE}-\\x{1F1F0}\\x{1F1F3}\\x{1F1F5}\\x{1F1F7}-\\x{1F1FA}\\x{1F201}-\\x{1F202}\\x{1F21A}\\x{1F22F}\\x{1F232}-\\x{1F23A}\\x{1F250}-\\x{1F251}\\x{1F300}-\\x{1F320}\\x{1F330}-\\x{1F335}\\x{1F337}-\\x{1F37C}\\x{1F380}-\\x{1F393}\\x{1F3A0}-\\x{1F3C4}\\x{1F3C6}-\\x{1F3CA}\\x{1F3E0}-\\x{1F3F0}\\x{1F400}-\\x{1F43E}\\x{1F440}\\x{1F442}-\\x{1F4F7}\\x{1F4F9}-\\x{1F4FC}\\x{1F500}-\\x{1F507}\\x{1F509}-\\x{1F53D}\\x{1F550}-\\x{1F567}\\x{1F5FB}-\\x{1F640}\\x{1F645}-\\x{1F64F}\\x{1F680}-\\x{1F68A}]", "");

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