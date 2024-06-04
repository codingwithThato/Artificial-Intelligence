import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GA {

    private Random rand;
    private String kpFile;
    private ArrayList<Knapsack> population;
    private int totalItems;
    private int cap;
    private int populationSize;
    private double mutationRate;
    private double crossoverRate;

    private Knapsack parent1;
    private Knapsack parent2;
    private Knapsack offspring1;
    private Knapsack offspring2;


    public GA(int populationSize, double mutationRate, double crossoverRate, String kpFile, long seed) {
        this.rand = new Random(seed);
        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.population = new ArrayList<Knapsack>(); //population of knapsacks :)
        this.totalItems = 0;
        this.cap = 0;
        this.kpFile = kpFile; //knapsack file
    }

    public ArrayList<Knapsack> initPopulation() { 
        for (int i = 0; i < populationSize; i++) {
            Knapsack knapsack = new Knapsack(kpFile); //initialise a knapsack! 
            totalItems = knapsack.getTotalItemsAvailable(); //get total items in the knapsack
            cap = knapsack.getCapacity(); //get capacity of the knapsack
            Knapsack randKp = knapsack.randomize(knapsack, rand); //randomising the knapsack.

            population.add(randKp);

            // System.out.println("Knapsack " + i + ": " + randKp.toString());
        }

        return population;
    }

    public void evaluate() { 
        //where we use fitness function
        for (Knapsack knapsack : population) {
            double fitness = knapsack.calculateFitness();
            knapsack.setFitness(fitness);
        }

        // // debugging purposes:
        // for (Knapsack knapsack : population) {
        //     System.out.println("Total Value of " + knapsack + ": " + knapsack.getTotalValue());
        // }
    }

    public void selection() { //select parents using tournament selection
        int tournamentSize = 4; 

    //FOR PARENT ONE:
        List<Knapsack> parents = new ArrayList<Knapsack>(); //list of parents we selecting for fitness.
        parent1 = null; 
        
        //randomly selecting the parents based off of tournament size
        while(parents.size() < tournamentSize){
            int randIndex = rand.nextInt(population.size());
            parents.add(population.get(randIndex));
        }

        //find the fittest out of the selected parents.
        double fittest1 = Double.MIN_VALUE; // fittest1 initialisation == minimum value possible
        for (int i = 0; i < parents.size(); i++) {
            if (parents.get(i).getTotalValue() > fittest1) {
                fittest1 = parents.get(i).getTotalValue();
                parent1 = parents.get(i);
            }
        }

    //FOR PARENT TWO:
        List<Knapsack> parents2 = new ArrayList<Knapsack>(); //list of parents we selecting for fitness.
        parent2 = null; 
            
        //randomly selecting the parents based off of tournament size
        while(parents2.size() < tournamentSize){
            int randIndex2 = rand.nextInt(population.size());
            parents2.add(population.get(randIndex2));
        }

        //find the fittest out of the selected parents.
        double fittest2 = Double.MIN_VALUE; // fittest2 initialisation == minimum value possible
        for (int i = 0; i < parents2.size(); i++) {
            if (parents2.get(i).getTotalValue() > fittest2) {
                fittest2 = parents2.get(i).getTotalValue();
                parent2 = parents2.get(i);
            }
        }

        // System.out.println("Fittest Parent for Parent 1:");
        // System.out.println(parent1.toString());

        // System.out.println("Fittest Parent for Parent 2:");
        // System.out.println(parent2.toString());

    }

    public void crossover() { //crossover parents
         
        List<Knapsack> offspring = new ArrayList<Knapsack>();
        
        Collections.shuffle(population, rand); //this is to shuffle curr population to be able to get random parents
        
        int crossPoint = rand.nextInt(5);//randomly select crossover point between 0 and 5
        
        for (int i = 0; i < population.size(); i += 2) {
            parent1 = population.get(i);
            parent2 = population.get(i + 1);

            // System.out.println("Crossover Point: " + crossPoint);
            // System.out.println("Parent 1: " + parent1);
            // System.out.println("Parent 2: " + parent2);

            if (rand.nextDouble() < crossoverRate) {
                crossoverHelp(parent1, parent2, crossPoint, offspring);
            } 
            else { // no crossover? jus add parents to offspring.
                offspring.add(parent1);
                offspring.add(parent2);
            }
        }

        // System.out.println("Offspring: " + offspring.toString());

        updatePopulation(offspring);
    }

    public void crossoverHelp(Knapsack parent1, Knapsack parent2, int crossPoint, List<Knapsack> offspring) { //helper function for crossover

        //must perform crossover until population == replaced by new knapsacks.
        offspring1 = new Knapsack(cap);
        offspring2 = new Knapsack(cap);
        
        int minLength = Math.min(parent1.getTotalItems(), parent2.getTotalItems());

        // System.out.println(crossPoint);
        for (int i = 0; i < minLength; i++) {
            if(i < crossPoint){ 
                offspring1.addItemRand(parent1.getItemsRand().get(i).getWeight(), parent1.getItemsRand().get(i).getValue());
                offspring2.addItemRand(parent2.getItemsRand().get(i).getWeight(), parent2.getItemsRand().get(i).getValue());
            } 
            else {
                offspring1.addItemRand(parent2.getItemsRand().get(i).getWeight(), parent2.getItemsRand().get(i).getValue());
                offspring2.addItemRand(parent1.getItemsRand().get(i).getWeight(), parent1.getItemsRand().get(i).getValue());
            }
        }
        
        offspring.add(offspring1);
        offspring.add(offspring2);
    }

    public void mutation() { //mutate offspring
        for (Knapsack knapsack : population) {
            mutationHelp(knapsack, rand, mutationRate);
        }
    }

    public void mutationHelp(Knapsack knapsack, Random rand, double mutationRate){
        ArrayList<Item> items = knapsack.getItemsRand(); 

        for (Item item : items) {
            if (rand.nextDouble() < mutationRate) {
                item.setTaken(!item.isTaken()); //flip the bit
            }
        }
        
    }
    
    public void updatePopulation(List<Knapsack> offspring) { //function to replace population with offspring
        population.clear(); 
        population.addAll(offspring); 
    }
    
    public boolean checkStoppingCriteria(int generationCount, int maxGenerations) { //check if we have reached max gen
        return generationCount >= maxGenerations;
    }
    
    public void runGeneticAlgorithm(int maxGenerations) { //run the GA :)

        long start = System.currentTimeMillis();
        int generationCount = 0;
    
        while (!checkStoppingCriteria(generationCount, maxGenerations)) {
            
            initPopulation();

            evaluate();
    
            selection();
    
            crossover();
            mutation();
    
            evaluate();
    
            generationCount++; //increment generation count
        }

        long end = System.currentTimeMillis();
        long runtime = end - start;
        double runtimeSeconds = runtime / 1000.0;

        //best solution:
        double bestTotalValue = Double.MIN_VALUE;
        for (Knapsack knapsack : population) {
            if (knapsack.getTotalValue() > bestTotalValue) {
                bestTotalValue = knapsack.getTotalValue();
            }
        }

        System.out.println("GA--> ");
        System.out.println("\tRuntime (seconds): " + runtimeSeconds);
        System.out.println("\tBest Solution (Total Value): " + bestTotalValue);
    }

//this class should: 
/*
1. Initialise population (random items from knapsack)
2. Evaluation of every individual in the population.
3. Selection of parents
4. Application of genetic operators. 
5. Evaluation of every individual in the population.
6. Population update (generational / steady state)
7. Check for the stopping criteria.
 */



}
