public class Main {
            
    public static void main(String[] args) {
        testILS();
        // testSA();
    }

    public static void testILS() {
        System.out.println("\nResults for Iterated Local Search:");
        long startTime = System.nanoTime(); // start time of program --> to calculate run time

        int[][] costMatrix = {
            {0, 15, 20, 22, 30},
            {15, 0, 10, 12, 25},
            {20, 10, 0, 8, 22},
            {22, 12, 8, 0, 18},
            {30, 25, 22, 18, 0}
        };

        ils solver = new ils(costMatrix);
        solver.run();

        // best route + its total distance
        String[] bestRouteNames = solver.getBestRouteNames();
        int bestDistance = solver.getBestDistance();

        // index of Hatfield
        int hatfieldIndex = -1;
        for (int i = 0; i < bestRouteNames.length; i++) {
            if (bestRouteNames[i].equals("Hatfield")) {
                hatfieldIndex = i;
                break;
            }
        }

        // best route starting + ending at Hatfield:
        System.out.println("Best route starting from Hatfield:");
        for (int i = 0; i < bestRouteNames.length; i++) {
            System.out.print(bestRouteNames[(hatfieldIndex + i) % bestRouteNames.length] + " -> ");
        }
        System.out.println("Hatfield"); // returning to start

        // total distance of the best route
        System.out.println("Total distance: " + bestDistance);

        long endTime = System.nanoTime(); // end time of program --> to calculate run time
        long runtime = endTime - startTime; // runtime
        double runtimeMS = (double) runtime / 1_000_000; // run time in ms

        System.out.println("Run time: " + runtimeMS + " ms");
        System.out.println("");
    }

    public static void testSA() {
        System.out.println("\nResults for Simulated Annealing:");
        sa simulatedAnnealing = new sa();
        simulatedAnnealing.run();
        System.out.println("");
    }
}
