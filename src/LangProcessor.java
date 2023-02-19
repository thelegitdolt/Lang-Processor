import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LangProcessor {
    public static final String NOT_TRANSLATED = " < NOT TRANSLATED";
    public static final String FILEPATH_US = "/Users/drew/Desktop/The Moddus Doltus/Pirate Speak Processor/files/en_us.json";
    public static final String FILEPATH_PT = "/Users/drew/Desktop/The Moddus Doltus/Pirate Speak Processor/files/en_pt.json";
    public static final String FILEPATH_USE = "/Users/drew/Desktop/The Moddus Doltus/Pirate Speak Processor/files/generated.json";
    public static final File FILE_US = new File(FILEPATH_US);
    public static final File FILE_PT = new File(FILEPATH_PT);

    private LangProcessor() {}

    public static HashMap<String, String> FileToStringHashMap(File file) throws FileNotFoundException {
        HashMap<String, String> map = new HashMap<>();
        Scanner scanner = new Scanner(file);
        scanner.nextLine();
        StringBuilder line = new StringBuilder(scanner.nextLine());
        while (scanner.hasNextLine()) {

            StringBuilder theThing = new StringBuilder(line);
            theThing.delete(0, theThing.indexOf("\"") + 1);

            StringBuilder key = new StringBuilder();
            for (char car : theThing.toString().toCharArray()) {
                if (car != '\"') {
                    key.append(car);
                }
                else break;
            }
            StringBuilder ans = new StringBuilder();
            theThing.delete(0, theThing.indexOf("\"") + 1);
            theThing.delete(0, theThing.indexOf("\"") + 1);
            for (char car : theThing.toString().toCharArray()) {
                if (car != '\"') {
                    ans.append(car);
                }
                else break;
            }

            map.put(key.toString(), ans.toString());
            line = new StringBuilder(scanner.nextLine());
        }
        return map;
    }

    public static HashMap<String, String> MatchAndEliminate(HashMap<String, String> US, HashMap<String, String> PT) {
        HashMap<String, String> map = new HashMap<>();
        for (Map.Entry<String, String> entry : US.entrySet()) {
            String key = entry.getKey();

            if (PT.containsKey(key)) {
                map.put(key, PT.get(key));
            }
            else {
                map.put(key, US.get(key) + NOT_TRANSLATED);
            }
        }
        return map;
    }

    public static String OrganizedJsonFromHashMap(HashMap<String, String> map) {
        StringBuilder code = new StringBuilder("{\n");

        ArrayList<String> Blocks = new ArrayList<>();
        ArrayList<String> Items = new ArrayList<>();
        ArrayList<String> Entities = new ArrayList<>();
        ArrayList<String> Subtitles = new ArrayList<>();
        ArrayList<String> Effect = new ArrayList<>();
        ArrayList<String> Death = new ArrayList<>();
        ArrayList<String> Advancement = new ArrayList<>();
        ArrayList<String> Gui = new ArrayList<>();
        ArrayList<String> Container = new ArrayList<>();
        ArrayList<String> Biome = new ArrayList<>();
        ArrayList<String> Config = new ArrayList<>();
        ArrayList<String> Other = new ArrayList<>();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();

            if (key.contains("block")) Blocks.add(key);
            else if (key.contains("item")) Items.add(key);
            else if (key.contains("entity")) Entities.add(key);
            else if (key.contains("subtitle")) Subtitles.add(key);
            else if (key.contains("effect")) Effect.add(key);
            else if (key.contains("death")) Death.add(key);
            else if (key.contains("advancement")) Advancement.add(key);
            else if (key.contains("gui")) Gui.add(key);
            else if (key.contains("container")) Container.add(key);
            else if (key.contains("biome")) Biome.add(key);
            else if (key.contains("config")) Config.add(key);
            else Other.add(key);
        }

        InsertArray(code, Blocks, map);
        InsertArray(code, Items, map);
        InsertArray(code, Entities, map);
        InsertArray(code, Subtitles, map);
        InsertArray(code, Effect, map);
        InsertArray(code, Death, map);
        InsertArray(code, Advancement, map);
        InsertArray(code, Gui, map);
        InsertArray(code, Container, map);
        InsertArray(code, Config, map);
        InsertArray(code, Biome, map);
        InsertArray(code, Other, map);

        code.substring(0, code.length() - 1);
        code.append("\n}");
        return code.toString();
    }

    public static void InsertArray(StringBuilder code, ArrayList<String> block, HashMap<String, String> map) {
        for (String s : block) {
            StringBuilder codeB = new StringBuilder(map.get(s));
            boolean isTrans = codeB.substring(codeB.length() < NOT_TRANSLATED.length() ? 0 : codeB.length() - NOT_TRANSLATED.length(), codeB.length()).compareTo(NOT_TRANSLATED) == 0;
            code.append("\n\t\"" + s + "\": \"");
            if (isTrans) {
                code.append(codeB.substring(0, codeB.length() - NOT_TRANSLATED.length()) + "\",");
                code.append(NOT_TRANSLATED);
            }
            else {
                code.append(codeB).append("\",");
            }
        }
        code.append("\n");
        code.append("\n");
    }

    public static void process() {
        try {
            FileWriter writer = new FileWriter(FILEPATH_USE);
            writer.write(OrganizedJsonFromHashMap(MatchAndEliminate(FileToStringHashMap(FILE_US), FileToStringHashMap(FILE_PT))));
            writer.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
