package Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class parser {
    public static List<List<List<Integer>>> parseOutput(int nmbrPeers){
        List<List<List<Integer>>> output = new ArrayList<>();
        for(int i = 0; i<nmbrPeers;i++) {
            List<List<Integer>> out = new ArrayList<>();
            List<String> lines = readFile("da_proc_" + (i+1) + ".out");
            for (String str : lines) {
                out.add(parseLine(str));
            }
            output.add(out);
        }
        return output;
    }

    private static List<Integer> parseLine(String line){
        String[] split = line.split("\\s+");
        List<Integer> lineParsed = new ArrayList<>();
        for(int i = 0; i < split.length;i++){
            switch (split[i]){
                case "b":
                    lineParsed.add(Test.b);
                    break;
                case "d":
                    lineParsed.add(Test.d);
                    break;
                    default:
                        lineParsed.add(Integer.parseInt(split[i]));
                        break;
            }
        }
        return lineParsed;
    }

    //Read a file line by line
    private static List<String> readFile(String filename)
    {
        //List that store each lines of the file
        List<String> records = new ArrayList<>();
        try
        {
            //Buffer used for reading
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            //While we are not at the end of the file, read the line
            while ((line = reader.readLine()) != null)
            {
                records.add(line);
            }
            reader.close();
            return records;
        }
        catch (Exception e)
        {
            System.err.format("Exception occurred trying to read '%s'.", filename);
            e.printStackTrace();
            return null;
        }
    }
}
