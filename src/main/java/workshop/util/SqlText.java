package workshop.util;

import org.apache.flink.calcite.shaded.com.google.common.io.Resources;

import java.net.URL;
import java.nio.charset.StandardCharsets;

public class SqlText {
    public static String getSQL(String resourcePath) throws  Exception {
        URL url = Resources.getResource(resourcePath);
        String text = Resources.toString(url, StandardCharsets.UTF_8);
        return text;
    }

}