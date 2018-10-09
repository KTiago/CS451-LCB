import javafx.util.Pair;

import java.util.HashMap;

//USERNAME:da-user
//PASSWORD:FIFObroadcast18

public class da_proc {

    //Class Variables
    private int id;
    private boolean isAlive = true;
    private HashMap<Integer,Pair<String,Integer>> membership;

    //Enumerations
    private enum signal {
        SIGUSR1,SIGTERM, SIGINT
    }

    //Constructor of da_proc
    public da_proc(int id) {
        this.id = id;

    }


}
