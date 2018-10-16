import java.util.HashMap;

//USERNAME:da-user
//PASSWORD:FIFObroadcast18

public class da_proc {

    //Class Variables
    private int id;
    private boolean isWaiting = true;
    private HashMap<Integer, Pair<String,Integer>> membership;


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
        System.exit(0);
    }



    //Handling TERM, INT and USR2 signals
    public void signalHandling(){
        DebugSignalHandler signalhandler = new DebugSignalHandler(this);
        signalhandler.listenTo("TERM");
        signalhandler.listenTo("USR2");
        signalhandler.listenTo("INT");
    }
}
