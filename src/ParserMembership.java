import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParserMembership {

    //Class Variables
    private List<String> input;
    private int nmbPeers;
    private HashMap<Integer, Pair<String,Integer>> peers;
    private List<Integer> dependencies = new ArrayList<>();

    //Init and parse all membership file
    public ParserMembership(String filename,int id) {
        input = readFile(filename);
        nmbPeers = Integer.parseInt(input.get(0));
        peers = parsePeers();
        dependencies = parseDependencies(id);
    }

    //Getter for the peers table
    public List<Integer> getDependencies() {
        return dependencies;
    }

    //Getter for the peers table
    public HashMap<Integer, Pair<String, Integer>> getPeers() {
        return peers;
    }

    //Method to parse the membership file and fil the peers
    private HashMap<Integer, Pair<String, Integer>> parsePeers() {
        HashMap<Integer, Pair<String, Integer>> peersTable = new HashMap<>();
        for(int i = 1;i <= nmbPeers;++i){
            String[] split = input.get(i).split("\\s+");
            peersTable.put(Integer.parseInt(split[0]),Pair.of(split[1],Integer.parseInt(split[2])));
        }
        return peersTable;
    }

    private List<Integer> parseDependencies(int id){
        List<Integer> dep = new ArrayList<>();
        String[] split = input.get(nmbPeers+id).split("\\s+");
        for (int i = 1;i<split.length;i++){
            dep.add(Integer.parseInt(split[i]));
        }
        return dep;
    }

    //Read a file line by line
    private List<String> readFile(String filename)
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
