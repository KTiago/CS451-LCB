import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParserMembership {

    private List<String> input;
    private int nmbPeers;
    private HashMap<Integer, Pair<String,Integer>> peers;


    //Init and parse all membership file
    public ParserMembership(String filename) {
        input = readFile(filename);
        nmbPeers = Integer.parseInt(input.get(0));
        peers = parsePeers();
    }

    //Getter for the peers table
    public HashMap<Integer, Pair<String, Integer>> getPeers() {
        return peers;
    }

    //Method to parse the membership file and fil the peers
    private HashMap<Integer, Pair<String, Integer>> parsePeers() {
        HashMap<Integer, Pair<String, Integer>> peersTable = new HashMap<>();
        for(int i = 1;i <= nmbPeers;++i){
            String[] splited = input.get(i).split("\\s+");
            peersTable.put(Integer.parseInt(splited[0]),Pair.of(splited[1],Integer.parseInt(splited[2])));
        }
        return peersTable;
    }

    //Read a file line by line
    //https://alvinalexander.com/blog/post/java/how-open-read-file-java-string-array-list
    private List<String> readFile(String filename)
    {
        List<String> records = new ArrayList<String>();
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
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
