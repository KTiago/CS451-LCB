public class Main {
    public static void main(String[] args) {
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

        //--Use ParserMembership class to parse the membership table--
    }
}
