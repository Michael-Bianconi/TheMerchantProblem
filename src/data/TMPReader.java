package data;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.opencsv.CSVReader;

/**
 * TMPReader reads in pre-generated data from .csv files to fill
 * {@link TMPDatabase} with.
 *
 * @author Michael Bianconi
 * @since 02-03-2019
 */
public class TMPReader {

    /** Makes it a static class. */
    private TMPReader() { /*    */ }

    /**
     * Fills out a String list from a .csv file.
     *
     * @param path Path to the File to read from.
     * @return Returns a list of Strings.
     */
    public static List<String> read(String path) {

        List<String> values = new ArrayList<>();

        try {
            CSVReader reader = new CSVReader(new FileReader(path));
            reader.skip(1); // skip header
            String[] line;

            // For each line in the file
            while ((line = reader.readNext()) != null) {

                Collections.addAll(values, line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return values;
    }

}
