import java.util.Arrays;
import java.util.Random;

class ils {
    private final String[] campusNames = {"Hatfield", "Hillcrest", "Groenkloof", "Prinsof", "Mamelodi"};

    private int[][] costMatrix;
    private int[] bestRoute;
    private int bestDistance;

    private Random rand = new Random(); 

    public ils(int[][] costMatrix) {
        this.costMatrix = costMatrix;
        this.bestRoute = generateInitialSol();
        this.bestDistance = calculateTotal(this.bestRoute);
    }

    private int[] generateInitialSol() {
        int n = costMatrix.length;
        int[] initialSolution = new int[n];
        for (int i = 0; i < n; i++) {
            initialSolution[i] = i; //initialized with: Hatfield -> Hillcrest -> Groenkloof -> Prinsof -> Mamelodi
        }

        //shuffle initial sol --> create a random route
        initialSolution = shuffleSwap(initialSolution);

        return initialSolution;
    } 

    public int[] shuffleSwap(int[] arr) {
        //generate random solution --> shuffles initial solution.
        for (int i = 0; i < arr.length; i++) {
            int index = rand.nextInt(arr.length);
            int tmp = arr[i];
            arr[i] = arr[index];
            arr[index] = tmp;
        }
        return arr;
    }

    private int calculateTotal(int[] route) { //calcualates total distance based on costMatrix
        int totalDistance = 0;
        for (int i = 0; i < route.length - 1; i++) {
            totalDistance += costMatrix[route[i]][route[i + 1]];
        }
        totalDistance += costMatrix[route[route.length - 1]][route[0]]; //return to starting point
        return totalDistance;
    }

    private int[] hillClimbing(int[] solution) { //where "solution" == route taken
        int[] currSolution = Arrays.copyOf(solution, solution.length);
        int currDist = calculateTotal(currSolution);
    
        boolean improved; //improved solution
        do {
            improved = false;
            for (int i = 0; i < currSolution.length - 1; i++) {
                for (int j = i + 1; j < currSolution.length; j++) {
                    
                    int tmp = currSolution[i];//swapping two adj. individuals
                    currSolution[i] = currSolution[j];
                    currSolution[j] = tmp;
    
                    //distance of new solution
                    int newDistance = calculateTotal(currSolution);
    
                    //new solution is better? --> accept.
                    if (newDistance < currDist) {
                        currDist = newDistance;
                        improved = true;
                    } else {
                        //new solution is worse? undo swap
                        tmp = currSolution[i];
                        currSolution[i] = currSolution[j];
                        currSolution[j] = tmp;
                    }
                }
            }
        } while (improved);
    
        return currSolution;
    }
    

    private int[] VNS(int[] solution) {
        int[] bestSolution = Arrays.copyOf(solution, solution.length);
        int bestDistance = calculateTotal(bestSolution);
    
        for (int k = 1; k <= solution.length / 2; k++) {
            int[] newSolution = shake(solution, k); //randomly swapping 2 campuses
            newSolution = hillClimbing(newSolution);
    
            int newDistance = calculateTotal(newSolution);

            if (newDistance < bestDistance) {
                bestSolution = Arrays.copyOf(newSolution, newSolution.length);
                bestDistance = newDistance;
                k = 1;  //improvement? --> search with k = 1.
            }
        }
    
        return bestSolution;
    }
    
    private int[] shake(int[] solution, int k) { //variation of PERTUBATION. used for generating random solution for VNS func above.
        // randomly swap 2 campuses
        int[] newSolution = Arrays.copyOf(solution, solution.length);
        for (int i = 0; i < k; i++) {
            int index1 = rand.nextInt(newSolution.length);
            int index2 = rand.nextInt(newSolution.length);
            int temp = newSolution[index1];
            newSolution[index1] = newSolution[index2];
            newSolution[index2] = temp;
        }
        return newSolution;
    }
    
    private int[] perturbation(int[] solution) {
        int i1 = rand.nextInt(solution.length);
        int i2 = rand.nextInt(solution.length);
        int tmp = solution[i1];
        solution[i1] = solution[i2];
        solution[i2] = tmp;
        return solution;
    }
    
    public String[] getBestRouteNames() {
        String[] bestRouteNames = new String[bestRoute.length];
        for (int i = 0; i < bestRoute.length; i++) {
            bestRouteNames[i] = campusNames[bestRoute[i]];
        }
        return bestRouteNames;
    }

    public int getBestDistance() {
        return bestDistance;
    }

    public void run() { 
        for (int i = 0; i < 1000; i++) { //1000 iterations
            bestRoute = VNS(bestRoute);
            bestDistance = calculateTotal(bestRoute);
            bestRoute = perturbation(bestRoute);
        }
    }
}
