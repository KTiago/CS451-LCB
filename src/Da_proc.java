import java.util.ArrayList;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

//Main class of the project which represents a process
public class Da_proc {

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
        ParserMembership parser = new ParserMembership(membership,id_process);
        HashMap<Integer, Pair<String, Integer>> peers = parser.getPeers();
        List<Integer> dependencies = parser.getDependencies();
        Da_proc process = new Da_proc(id_process,peers,numberMessages, dependencies);
        process.start();
        terminate.await();
    }


    //Class Variables
    private CountDownLatch wait = new CountDownLatch(1);
    private static CountDownLatch terminate = new CountDownLatch(1);
    private int numberMessages;
    private int id;
    private ArrayList<Pair<Integer,Integer>> logs = new ArrayList<>();
    private Broadcast BC;

    //Constructor of Da_proc
    public Da_proc(int id,HashMap<Integer, Pair<String,Integer>> membership, int numberMessages, List<Integer> dependencies) throws Exception{
        this.id = id;
        this.numberMessages = numberMessages;
        // We instantiate a Localized Causal Broadcast
        BC = new LCBroadcast(membership, dependencies, id,this);
        signalHandling();
    }


    //Start the process and wait until the USR2 signal is received, when USR2 is received, it will start broadcast
    public void start() throws Exception{
        BC.start();
        //Waiting to get USR2
        wait.await();
        for (int i = 0;i < numberMessages;++i){
            BC.broadcast(" ");
            logs.add(Pair.of(-1,i + 1));
        }
    }

    //Method invoked when the signal USR2 is received, it unlocks the "wait" CountDownLatch
    public void usr2Signal(){
        wait.countDown();
    }


    //Method invoked when the signal SIGTERM or SIGINT is received
    public void stop(){
        wait.countDown();
        BC.stop();
        printLogs();
        terminate.countDown();

    }

    //Handling TERM, INT and USR2 signals
    public void signalHandling(){
        DebugSignalHandler signalhandler = new DebugSignalHandler(this);
        signalhandler.listenTo("TERM");
        signalhandler.listenTo("USR2");
        signalhandler.listenTo("INT");
    }

    //Callback method for FIFO
    public void deliver(int id, int sequenceNumber, String message){
        //In this part of the project, the message is empty
        logs.add(Pair.of(id,sequenceNumber));
    }

    //Print the log file in a output file.
    private void printLogs() {
        try {
            synchronized (logs) {
                String namefile = "da_proc_" + id + ".out";
                int size = logs.size();
                FileWriter writer = new FileWriter(namefile);
                for (int i = 0; i < size; i++) {
                    Pair<Integer, Integer> l = logs.get(i);
                    if (l.first == -1) {
                        writer.write("b " + l.second + "\n");
                    } else {
                        writer.write("d " + l.first + " " + l.second + "\n");
                    }
                }
            writer.close();
            }
        } catch (Exception e) {
            System.err.format("Exception occurred trying to write output file for process "+id);
            e.printStackTrace();
        }
    }
}
