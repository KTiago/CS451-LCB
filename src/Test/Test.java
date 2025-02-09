package Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Test {
    public final static int b = -1;
    public final static int d = -2;

    public static void main(String[] args) {
        int numberOfPeers = Integer.parseInt(args[0]);
        List<List<List<Integer>>> output = parser.parseOutput(numberOfPeers);

        HashMap<Pair<Integer, Integer>, Set<Pair<Integer, Integer>>> dep = new HashMap<>();

        //Parsing Step
        for (int p = 0; p < numberOfPeers; p++) {
            List<List<Integer>> out = output.get(p);
            HashSet<Pair<Integer, Integer>> s = new HashSet<>();
            ParserMembership parser = new ParserMembership("membershipTest", p + 1);
            List<Integer> l = parser.getDependencies();
            for (List<Integer> line : out) {
                if (line.get(0) == b) {
                    Pair<Integer, Integer> pair = Pair.of(p + 1, line.get(1));
                    dep.put(pair, new HashSet<>(s));
                    s.add(pair);
                } else if (l.contains(line.get(1))) {
                    s.add(Pair.of(line.get(1), line.get(2)));
                }
            }
        }
        //Check step
        boolean error = false;
        for (int p = 0; p < numberOfPeers; p++) {
            List<List<Integer>> out = output.get(p);
            HashSet<Pair<Integer, Integer>> delivered = new HashSet<>();
            for (List<Integer> line : out) {
                if (line.get(0) == d) {
                    Pair<Integer, Integer> pair = Pair.of(line.get(1), line.get(2));
                    if (dep.get(pair) != null && !delivered.containsAll(dep.get(pair))) {
                        if (!error) { // only print first encountered error
                            System.out.println("Peer " + (p + 1));
                            System.out.println("Error");
                            System.out.println(pair);
                            System.out.println("Should have been delivered :");
                            System.out.println(dep.get(pair));
                            System.out.println("What has been delivered :");
                            System.out.println(delivered);
                        }
                        error = true;
                    }
                    delivered.add(pair);
                }
            }
        }
        if (error) {
            System.out.println(">------------FAILURE------------<");
        } else {
            System.out.println(">------------SUCCESS------------<");
        }
    }

}
