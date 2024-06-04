import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class sa_copy_readwrite {
    private static final double T0 = 10000; //Initial temperature
    private static final double coolingRate = 0.003; //Cooling rate

    private List<Integer> bestRoute; //Storing
    private int bestDistance; //Storing

    public void run() {
        long startTime = System.nanoTime();

        int[][] costMatrix = {
            {0, 15, 20, 22, 30},
            {15, 0, 10, 12, 25},
            {20, 10, 0, 8, 22},
            {22, 12, 8, 0, 18},
            {30, 25, 22, 18, 0}
        };

        String[] campus = {"Hatfield", "Hillcrest", "Groenkloof", "Prinsof", "Mamelodi"};

        // initial solution (Hatfield -> Hillcrest -> Groenkloof -> Prinsof -> Mamelodi -> Hatfield)
        List<Integer> currRoute = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 0));
        List<Integer> bestRoute = new ArrayList<>(currRoute);

        double t = T0;
        Random rand = new Random();
        
        String content = "";

        while (t > 1) {
            
            //new neighbour solution
            List<Integer> newRoute = new ArrayList<>(currRoute);
            
            //swapping 2 random campuses here
            int camp1 = rand.nextInt(4) + 1;
            int camp2 = rand.nextInt(4) + 1;
            Collections.swap(newRoute, camp1, camp2);
            
            int currCost = getCost(currRoute, costMatrix);
            int neighbourCost = getCost(newRoute, costMatrix);
            
            if (acceptanceProbability(currCost, neighbourCost, t) > Math.random()) {
                currRoute = new ArrayList<>(newRoute);
            }
            
            if (getCost(currRoute, costMatrix) < getCost(bestRoute, costMatrix)) {
                bestRoute = new ArrayList<>(currRoute);
            }
            
            content += String.valueOf(getCost(bestRoute, costMatrix)) + "\n";

            t *= 1 - coolingRate;
        }

        bestDistance = getCost(bestRoute, costMatrix);
        System.out.println("Best route: " + solutionToString(bestRoute, campus));
        System.out.println("Best total distance: " + bestDistance);

        long endTime = System.nanoTime();//end time of program --> to calculate run time
        long runtime = endTime - startTime;//runtime
        double runtimeMS = (double) runtime / 1_000_000; // run time in ms
 
        System.out.println("Run time: " + runtimeMS + " ms");

        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("sa_output.txt"))) {
            writer.write(content);
        } 
        catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public int getCost(List<Integer> solution, int[][] costMatrix) {
        int cost = 0;
        for (int i = 0; i < solution.size() - 1; i++) {
            cost += costMatrix[solution.get(i)][solution.get(i + 1)];
        }
        return cost;
    }

    private static double acceptanceProbability(int currCost, int neighbourCost, double t) {
        if(neighbourCost >= currCost) { return Math.exp((currCost - neighbourCost) / t); }
        return 1.0;
    }

    private static String solutionToString(List<Integer> solution, String[] campus) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < solution.size(); i++) {
            int index = solution.get(i);
            sb.append(campus[index]);
            if (i < solution.size() - 1) {
                sb.append(" -> ");
            }
        }
        return sb.toString();
    }

    public List<Integer> getBestRoute() {
        return bestRoute;
    }

    public int getBestDistance() {
        return bestDistance;
    }
}
