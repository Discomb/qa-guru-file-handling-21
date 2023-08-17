package guru.qa.utils;

import com.google.common.io.Files;

public class Utils {

    public static String getFileExtension(String filename){
        return Files.getFileExtension(filename);
    }
}
