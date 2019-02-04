package test;

import data.TMPDatabase;
import data.TMPGenerator;

public class GeneratorDebug {

    private static final String DB_URL =
            "jdbc:h2:~/Dev/projects/TheMerchantProblem/generatordb";

    public static void main (String[] args) {

        TMPDatabase.reset(DB_URL);
        try (TMPDatabase db = new TMPDatabase(DB_URL)) {

            TMPGenerator generator = new TMPGenerator(db);
            generator.setGlobality(100);
            generator.setVerbose(true);
            generator.generateCommodities();
            generator.generatePorts();
            generator.generatePortInventories();
            generator.generateRoutes();
            generator.generateRouteCosts();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
