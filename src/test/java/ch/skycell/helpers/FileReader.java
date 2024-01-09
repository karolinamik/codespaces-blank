package ch.skycell.helpers;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.Scanner;

public class FileReader {
    public static String readResourceFile(String path) {
        InputStream is = FileReader.class.getClassLoader().getResourceAsStream(path);
        if (is == null) throw new IllegalArgumentException("Path does not exist: " + path);

        StringBuilder stringBuilder = new StringBuilder();
        try (Scanner scanner = new Scanner(is)) {
            while (scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine());
            }
        }

        return stringBuilder.toString();
    }
}
