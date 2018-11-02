import java.util.ArrayList;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

//USERNAME:da-user
//PASSWORD:FIFObroadcast18

public class da_proc {

    public static void main(String[] args) throws Exception {
        String membership = "";
        int id_process = 0;
        int numberMessages = 0;
        //Check the number of arguments
        int numberArgs = 3;
        if (args.length != numberArgs) {
            System.out.println("Not the right number of arguments");
            System.out.println("--Usage: java Main id_process membership_name");
            System.exit(0);
        } else {
            id_process = Integer.parseInt(args[0]);
            membership = args[1];
            numberMessages = Integer.parseInt(args[2]);
        }
        //-- Use ParserMembership class to parse the membership table --
        ParserMembership parser = new ParserMembership(membership);
        HashMap<Integer, Pair<String, Integer>> peers = parser.getPeers();
        System.out.println("Initializing.\n");
        da_proc process = new da_proc(id_process,peers,numberMessages);
    }


    //Class Variables
    private CountDownLatch wait = new CountDownLatch(1);
    private int numberMessages;
    private volatile boolean isWaiting = true;
    private int id;
    private HashMap<Integer, Pair<String,Integer>> membership;
    private ArrayList<Pair<Integer,Integer>> logs = new ArrayList<>();
    private UniformReliableBroadcast URB;

    //Constructor of da_proc
    public da_proc(int id,HashMap<Integer, Pair<String,Integer>> membership, int numberMessages) {
        this.id = id;
        this.membership = membership;
        this.numberMessages = numberMessages;
        URB = new UniformReliableBroadcast(membership,id,this);
        signalHandling();
        start();
    }


    //Private Methods


    public void start(){
        //Waiting to get USR2
        wait.await();
        URB.start();
        for (int i = 0;i < numberMessages;++i){
            URB.broadcast("");
        }
    }

    public void usr2Signal(){
        wait.countDown();
    }


    //Method invoked when the signal SIGTERM or SIGINT is received
    public void stop(){
        System.out.println("Immediately stopping network packet processing.\n");
        URB.stop();
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
    public void deliver(int id, int sequenceNumber, String message){
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
                Pair<Integer, Integer> l = logs.get(i);
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
