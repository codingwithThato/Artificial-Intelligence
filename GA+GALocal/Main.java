public class Main {
    public static void main(String[] args) {
        long seed = System.currentTimeMillis();
        System.out.println("Seed␣Value: " + seed);   
        // testItem();
        // testKnapsack();
        // testGA(seed);

        runGALS(seed);
        runGA(seed);
    }

    public static void testItem(){
        Item item = new Item(1, 2);
        System.out.println(item.toString());
    }

    public static void testKnapsack() { 
        Knapsack kp = new Knapsack("f2_l-d_kp_20_878");
        // System.out.println(kp.toString());//this has been modified to test itemRand array not the original items array, so toString here specifically wont work.
    }

    public static void testGA(long seed) {

        int populationSize = 50;
        double mutationRate = 0.1;
        double crossoverRate = 0.8;
        String kpFile = "f2_l-d_kp_20_878";

        GA ga = new GA(populationSize, mutationRate, crossoverRate, kpFile, seed);

        ga.initPopulation();
        ga.evaluate();  
        ga.selection();  
        ga.crossover();
        ga.mutation();
    }

    public static void runGA(long seed) {
        int populationSize = 200;
        double mutationRate = 0.1;
        double crossoverRate = 0.9;
        String kpFile = "f8_l-d_kp_23_10000";
        int maxGenerations = 50;

        GA ga = new GA(populationSize, mutationRate, crossoverRate, kpFile, seed);

        ga.runGeneticAlgorithm(maxGenerations);
    }    
    
    public static void runGALS(long seed) {
        int populationSize = 200;
        double mutationRate = 0.1;
        double crossoverRate = 0.9;
        String kpFile = "f8_l-d_kp_23_10000";
        int maxGenerations = 50;

        GALocal ga = new GALocal(populationSize, mutationRate, crossoverRate, kpFile, seed);

        ga.runGeneticAlgorithm(maxGenerations);
    }


}

