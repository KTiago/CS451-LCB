import java.util.HashMap;

public class Main {
    public static void main(String[] args) throws Exception{
        String membership = "";
        int id_process = 0;
        int numberMessages = 0;
        //Check the number of arguments
        int numberArgs = 3;
        if(args.length != numberArgs){
            System.out.println("Not the right number of arguments");
            System.out.println("--Usage: java Main id_process membership_name");
            System.exit(0);
        }else{
            id_process = Integer.parseInt(args[0]);
            membership = args[1];
            numberMessages = Integer.parseInt(args[2]);
        }
        //-- Use ParserMembership class to parse the membership table --
        ParserMembership parser = new ParserMembership(membership);
        HashMap<Integer, Pair<String,Integer>> peers = parser.getPeers();
        UniformReliableBroadcast link = new UniformReliableBroadcast(peers, id_process);
        System.out.println("Running process "+id_process);
        link.start();
        if(id_process == 1) {
            Thread.sleep(500);
            link.broadcast("1 0");
            Thread.sleep(500);
            link.broadcast("1 1");
            Thread.sleep(2000);
            link.broadcast("1 2");
            Thread.sleep(1000);
            link.broadcast("1 3");
            Thread.sleep(2000);
            link.broadcast("1 4");
            Thread.sleep(1200);
            link.broadcast("1 5");
            Thread.sleep(1000);
            link.broadcast("1 6");
        }else if (id_process == 2){
            Thread.sleep(1000);
            link.broadcast("2 0");
            Thread.sleep(1000);
            link.broadcast("2 1");
            Thread.sleep(2000);
            link.broadcast("2 2");
            Thread.sleep(1000);
            link.broadcast("2 3");
            Thread.sleep(2000);
            link.broadcast("2 4");
            Thread.sleep(700);
            link.broadcast("2 5");
            Thread.sleep(1000);
            link.broadcast("2 6");
        }else if (id_process == 3){
            Thread.sleep(600);
            link.broadcast("3 0");
            Thread.sleep(600);
            link.broadcast("3 1");
            Thread.sleep(300);
            link.broadcast("3 2");
            Thread.sleep(1500);
            link.broadcast("3 3");
            Thread.sleep(2000);
            link.broadcast("3 4");
            Thread.sleep(700);
            link.broadcast("3 5");
            Thread.sleep(1000);
            link.broadcast("3 6");
        }
    }
}
