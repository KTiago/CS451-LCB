import java.util.ArrayList;
import java.io.FileWriter;
import java.util.HashMap;

//USERNAME:da-user
//PASSWORD:FIFObroadcast18

public class da_proc {

    //Class Variables
    private int id;
    private boolean isWaiting = true;
    private HashMap<Integer, Pair<String,Integer>> membership;
    private boolean wait_for_start = true;
    private ArrayList<Pair<Integer,Integer>> logs = new ArrayList<>();

    //Constructor of da_proc
    public da_proc(int id,HashMap<Integer, Pair<String,Integer>> membership) {
        this.id = id;
        this.membership = membership;
        signalHandling();
    }


    //Private Methods

    //Method invoked when the signal SIGUSR2 is received
    public void start(){
        //TODO
    }

    //Method invoked when the signal SIGTERM or SIGINT is received
    public void stop(){
        //TODO
        System.out.println("Immediately stopping network packet processing.\n");


        System.out.println("Writing output.");
        printLogs();
        System.exit(0);
    }



    //Handling TERM, INT and USR2 signals
    public void signalHandling(){
        DebugSignalHandler signalhandler = new DebugSignalHandler(this);
        signalhandler.listenTo("TERM");
        signalhandler.listenTo("USR2");
        signalhandler.listenTo("INT");
    }

    //Callback method for URB
    private void deliver(int id, int sequenceNumber, String message){
        //TODO Do something with message?
        logs.add(Pair.of(id,sequenceNumber));
    }

    //Print the log file in a output file.
    private void printLogs() {
        try {
            String namefile = "da_proc"+id+".out";
            int size = logs.size();
            FileWriter writer = new FileWriter(namefile);
            for (int i = 0; i < size;i++) {
                Pair<Character, Integer> l = logs.get(i);
                if(l.first == id){
                    writer.write("b " + l.second);
                }else {
                    writer.write("d " +l.first+" "+l.second);
                }

                if(i < size-1){
                    writer.write("\n");
                }
            }
            writer.close();
        } catch (Exception e) {
            System.err.format("Exception occurred trying to write output file for process "+id);
            e.printStackTrace();
        }
    }
}
