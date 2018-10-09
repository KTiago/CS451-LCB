public class Main {
    public static void main(String[] args) {
        try {
            PerfectLink link1 = new PerfectLink("127.0.0.1", 5000, "127.0.0.1", 5001);
            PerfectLink link2 = new PerfectLink("127.0.0.1", 5001, "127.0.0.1", 5000);
            link1.send("HI BOI");
            System.out.println("MESSAGE SENT");
            System.out.println(link2.receive(5));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
