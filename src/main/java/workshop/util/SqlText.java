package workshop.util;

// import org.apache.flink.calcite.shaded.com.google.common.io.Resources;

import java.util.Scanner;

public class SqlText {
    public static String getSQL(String resourcePath) throws  Exception {
        SqlText s = new SqlText();

        // this code expect the sql files are bundled with jar file..
        // while loading jar file, use full path, /sql/file.sql for loading from resource
        String text = new Scanner(s.getClass().getResourceAsStream(resourcePath), "UTF-8").useDelimiter("\\A").next();

        return text;
    }
//
//    public static String getSQL2(String resourcePath) throws  Exception {
//        URL url = Resources.getResource(resourcePath);
//        String text = Resources.toString(url, StandardCharsets.UTF_8);
//        return text;
//    }

}