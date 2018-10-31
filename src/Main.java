import java.util.HashMap;

public class Main {
    public static void main(String[] args) throws Exception{
        String membership = "";
        int id_process = 0;
        //Check the number of arguments
        int numberArgs = 2;
        if(args.length != numberArgs){
            System.out.println("Not the right number of arguments");
            System.out.println("--Usage: java Main id_process membership_name");
            System.exit(0);
        }else{
            id_process = Integer.parseInt(args[0]);
            membership = args[1];
        }
        //-- Use ParserMembership class to parse the membership table --
        ParserMembership parser = new ParserMembership(membership);
        HashMap<Integer, Pair<String,Integer>> peers = parser.getPeers();
        PerfectLink link = new PerfectLink(peers.get(id_process).first, peers.get(id_process).second, peers);
        System.out.println("Running process "+id_process);
        link.start();
        if(id_process == 1) {
            Thread.sleep(15000);
            link.send("1 1", 2);
            Thread.sleep(1300);
            link.send("1 2", 2);
            Thread.sleep(2000);
            link.send("1 3", 2);
            Thread.sleep(1000);
            link.send("1 4", 2);
            Thread.sleep(2000);
            link.send("1 5", 2);
            Thread.sleep(1200);
            link.send("1 6", 2);
            Thread.sleep(1000);
            link.send("1 7", 2);
        }else if (id_process == 2){
            Thread.sleep(16000);
            link.send("2 1", 1);
            Thread.sleep(1000);
            link.send("2 2", 1);
            Thread.sleep(2000);
            link.send("2 3", 1);
            Thread.sleep(1000);
            link.send("2 4", 1);
            Thread.sleep(2000);
            link.send("2 5", 1);
            Thread.sleep(700);
            link.send("2 6", 1);
            Thread.sleep(1000);
            link.send("2 7", 1);
        }
    }
}
