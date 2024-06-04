import java.util.List;
import java.util.Random;

public class Main {

    
    public static void main(String[] args) {
        long seed = 1716664409626l;
        System.out.println("Seed Value: " + seed);
        // Random rand = new Random(seed);

        String trainFilePath = "mushroom_train.csv";
        String testFilePath = "mushroom_test.csv";

        List<Mushroom> trainingData = Mushroom.readMushroomData(trainFilePath);
        List<Mushroom> testData = Mushroom.readMushroomData(testFilePath);

        //normalise dataset!!!
        Mushroom.normaliseData(trainingData);
        Mushroom.normaliseData(testData);

        // testNode(rand);
        // testMushroom();
        // testGPComponents(trainingData, testData, seed);

        runGP(trainingData, testData, seed);
    }

    public static void testNode(Random rand) {
        Node node = Node.generateRandomTree(3, rand);
        System.out.println("Generated Node: " + node.toString());
    }

    private static void testMushroom() {
        String trainFilePath = "mushroom_train.csv";
        // String testFilePath = "mushroom_test.csv";

        List<Mushroom> trainingData = Mushroom.readMushroomData(trainFilePath);
        // List<Mushroom> testData = Mushroom.readMushroomData(testFilePath);

        System.out.println("Mushroom: " + trainingData.get(2).getAttributes() + ", Class: " + trainingData.get(2).getMushroomClass());

        // List<Mushroom> mushrooms = readMushroomData("mushroom_train.csv");
        // for (Mushroom mushroom : mushrooms) {
        //     System.out.println("Attributes: " + mushroom.getAttributes() + ", Class: " + mushroom.getMushroomClass());
        // }
    }

    public static void testGPComponents(List<Mushroom> trainingData, List<Mushroom> testData, long seed) {
        int populationSize = 100;
        int numGenerations = 50;
        double mutationRate = 0.3;
        double crossoverRate = 0.8;
    
        GP gp = new GP(populationSize, numGenerations, crossoverRate, mutationRate, trainingData, testData, seed);

        System.out.println("Testing initPopulation...");
        gp.initPopulation(5);

        //MODIFY THE FOLLOWING BC THEY DO NOT BE WORKING NICELY --> F-MEASURE = 0.0 MEANING BAD BAD BAD
        System.out.println("Testing evaluate..."); 
        gp.evaluate(gp.getPopulation().get(0), testData, true);

        System.out.println("Testing selectParent...");
        gp.selectParent(trainingData);

        System.out.println("Testing crossover...");
        gp.crossover(gp.getPopulation().get(0), gp.getPopulation().get(1));

        System.out.println("Testing mutate...");
        gp.mutate(gp.getPopulation().get(0), 5); 

        System.out.println("Testing getBestProgram...");
        gp.getBestProgram(trainingData);

        System.out.println("Testing evolve...");
        gp.evolve(5);
    }
    
    public static void runGP(List<Mushroom> trainingData, List<Mushroom> testData, long seed) {
        int populationSize = 100;
        int numGenerations = 50;
        double mutationRate = 0.01; //same output if i use 0.3 or 0.01
        double crossoverRate = 0.8;
        
        GP gp = new GP(populationSize, numGenerations, crossoverRate, mutationRate, trainingData, testData, seed);
        gp.run();
    }
}
