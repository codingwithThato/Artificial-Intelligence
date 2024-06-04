import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GP {
    private int populationSize;
    private int numGenerations;
    private double crossoverRate;
    private double mutationRate;
    private List<Node> population;
    private List<Mushroom> trainingData;
    private List<Mushroom> testData;
    private Random rand;

    public GP(int populationSize, int numGenerations, double crossoverRate, double mutationRate, List<Mushroom> trainingData, List<Mushroom> testData, long seed) {
        this.populationSize = populationSize;
        this.numGenerations = numGenerations;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.trainingData = trainingData;
        this.testData = testData;
        this.rand = new Random(seed);
        this.population = new ArrayList<>();
    }

    public void initPopulation(int maxDepth) {
        for (int i = 0; i < populationSize; i++) {
            population.add(Node.generateRandomTree(maxDepth, rand));
        }
        // System.out.println("Population initialized " + population.size() + " trees");
        // System.out.println(population.toString());
    }

    public double evaluate(Node program, List<Mushroom> data, boolean verbose) {
        int correct = 0;
        int truePositive = 0;
        int falsePositive = 0;
        int trueNegative = 0;
        int falseNegative = 0;
        
        for (Mushroom mushroom : data) {
            double result = program.evaluate(mushroom);
            boolean predictedClass = result >= 0.5;
            boolean actualClass = mushroom.getMushroomClass().equals("1"); //edible = true 
            
            if (predictedClass == actualClass) 
                correct++;
            
            if (predictedClass && actualClass) 
                truePositive++;
            else if (predictedClass && !actualClass) 
                falsePositive++;
            else if (!predictedClass && !actualClass) 
                trueNegative++;
            else if (!predictedClass && actualClass) 
                falseNegative++;
        }
        
        // accuracy , specificity , sensitivity , f-measure !!!
        double accuracy = (double) correct / data.size();
        double specificity = (double) trueNegative / (trueNegative + falsePositive);
        double sensitivity = (double) truePositive / (truePositive + falseNegative);
        double fMeasure = 2 * (sensitivity * specificity) / (sensitivity + specificity);
        
        double treeSizePenalty = 0.01 * program.size(); 
        double fitness = accuracy - treeSizePenalty;
        
        if (verbose) {
            System.out.println("Accuracy: " + accuracy);
            System.out.println("Specificity: " + specificity);
            System.out.println("Sensitivity: " + sensitivity);
            System.out.println("F-Measure: " + fMeasure);
            System.out.println("Tree Size Penalty: " + treeSizePenalty);
        }
        return fitness;
    }

    public Node selectParent(List<Mushroom> data) { //tournament selection!
        Node best = null;
        double bestFitness = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < 5; i++) {
            Node candidate = population.get(rand.nextInt(populationSize));
            double candidateFitness = evaluate(candidate, data, false);
            if (candidateFitness > bestFitness) {
                bestFitness = candidateFitness;
                best = candidate;
            }
        }
        return best;
    }

    public Node crossover(Node parent1, Node parent2) {
        Node child = parent1.copy();
        boolean crossoverOccurred = false; //flag checking - has crossover occurred for this node?

        if (rand.nextDouble() < crossoverRate) {
            if (parent2.left != null) {
                child.left = parent2.left.copy();
                crossoverOccurred = true;
            }
        } else {
            if (parent2.right != null) {
                child.right = parent2.right.copy();
                crossoverOccurred = true;
            }
        }
        //if not? revert to copying the entire parent1 tree
        if (!crossoverOccurred) {
            child = parent1.copy();
        }
        return child;
    }
    

    public void mutate(Node node, int maxDepth) {
        if (rand.nextDouble() < mutationRate) {
            if (rand.nextBoolean()) {
                node.left = Node.generateRandomTree(maxDepth - 1, rand);
            } else {
                node.right = Node.generateRandomTree(maxDepth - 1, rand);
            }
        }
    }

    public Node getBestProgram(List<Mushroom> data) {
        Node best = null;
        double bestFitness = Double.NEGATIVE_INFINITY;
        for (Node program : population) {
            double fitness = evaluate(program, data, false);
            if (fitness > bestFitness) {
                bestFitness = fitness;
                best = program;
            }
        }
        return best;
    }

    public double test(Node program) {
        int correct = 0;
        for (Mushroom mushroom : testData) {
            double result = program.evaluate(mushroom);
            boolean predictedClass = result >= 0.5;
            boolean actualClass = mushroom.getMushroomClass().equals("1"); // edible = 1 or true basically
            if (predictedClass == actualClass) correct++;
        }
        return (double) correct / testData.size();
    }

    public void evolve(int maxDepth) {
        double bestTestingAccuracy = 0;
        int earlyStopCount = 0;

        for (int gen = 0; gen < numGenerations; gen++) {
            List<Node> newPopulation = new ArrayList<>();
            for (int i = 0; i < populationSize; i++) {
                Node parent1 = selectParent(trainingData);
                Node parent2 = selectParent(trainingData);
                Node child = crossover(parent1, parent2);
                mutate(child, maxDepth);
                newPopulation.add(child);
            }
            population = newPopulation;
            
            Node bestProgram = getBestProgram(trainingData);
            double trainAccuracy = evaluate(bestProgram, trainingData, false);
            double testingAccuracy = evaluate(bestProgram, testData, false);
            System.out.printf("Generation %d - Training Accuracy: %.4f, Testing Accuracy: %.4f\n", gen, trainAccuracy, testingAccuracy);
    
            if (testingAccuracy > bestTestingAccuracy) {
                bestTestingAccuracy = testingAccuracy;
            //     earlyStopCount = 0;
            // } else {
            //     earlyStopCount++;
            //     if (earlyStopCount >= 10) {
            //         System.out.println("Early stopping due to no improvement in testing accuracy.");
            //         break;
            //     }
            }
        }
    }
    
    public void run() {
        int maxDepth = 4;
        initPopulation(maxDepth);
        evolve(maxDepth);
        Node bestProgram = getBestProgram(trainingData);
        System.out.println("\nFinal Evaluation on Test Data:");
        evaluate(bestProgram, testData, true);
    }

    //any necessary getters below:
    public int getPopulationSize() {
        return populationSize;
    }

    public int getNumGenerations() {
        return numGenerations;
    }

    public List<Node> getPopulation() {
        return population;
    }
}
