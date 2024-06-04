import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            long seed = 45; 
    
            String trainFilePath = "mushroom_train.csv";
            String testFilePath = "mushroom_test.csv";

            List<List<Double>> trainingData = readData(trainFilePath);
            List<List<Double>> testData = readData(testFilePath);

            // Normalize the data
            normalizeData(trainingData);
            normalizeData(testData);

            // Define the neural network parameters
            int inputSize = trainingData.get(0).size() - 1; // Last column is the label
            int hiddenSize = 10; 
            int outputSize = 1;
            double learningRate = 0.01;

            NeuralNetwork nn = new NeuralNetwork(inputSize, hiddenSize, outputSize, learningRate, seed);
    
            //initialize weights and biases test
            // nn.initializeWeights(8, 1);
            // System.out.println("Weights and biases initialized.");
    
            //load data test
            nn.loadDataset("mushroom_train.csv", true);
            System.out.println("Training dataset loaded. Size: " + nn.getTrainInputs().size());
    

            nn.loadDataset("mushroom_test.csv", false);
            System.out.println("Testing dataset loaded. Size: " + nn.getTestInputs().size());
    

            // // testing feedforward w first training data
            // if (!nn.getTrainInputs().isEmpty()) {
            //     double[] firstInput = nn.getTrainInputs().get(0);
            //     ArrayList<Double> output = nn.feedforward(firstInput);
            //     System.out.println("Feedforward output: " + output);
            // } else {
            //     System.out.println("Training dataset is empty.");
            // }
    
            // //testing backpropagation w first training data
            // if (!nn.getTrainInputs().isEmpty() && !nn.getTrainOutputs().isEmpty()) {
            //     double[] firstInput = nn.getTrainInputs().get(0);
            //     double[] firstTarget = nn.getTrainOutputs().get(0);
            //     nn.backpropagation(firstInput, firstTarget);
            //     System.out.println("Backpropagation completed.");
            // } else {
            //     System.out.println("Training dataset or targets are empty.");
            // }
            
            System.out.println("\n\nTraining network...");
            //train epochs
            nn.train(100); 
            System.out.println("\nTesting network...");
            //test network
            nn.test();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<List<Double>> readData(String filePath) {
        List<List<Double>> data = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(filePath))) {
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }
            while (scanner.hasNextLine()) {
                String[] values = scanner.nextLine().split(",");
                List<Double> row = new ArrayList<>();
                for (String value : values) {
                    try {
                        row.add(Double.parseDouble(value));
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing value: " + value);
                    }
                }
                data.add(row);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void normalizeData(List<List<Double>> data) {
        int numAttributes = data.get(0).size() - 1; // Exclude the label
        double[] minValues = new double[numAttributes];
        double[] maxValues = new double[numAttributes];

        // Initialize min and max values
        for (int i = 0; i < numAttributes; i++) {
            minValues[i] = Double.MAX_VALUE;
            maxValues[i] = Double.MIN_VALUE;
        }

        // Find min and max values for each attribute
        for (List<Double> row : data) {
            for (int i = 0; i < numAttributes; i++) {
                if (row.get(i) < minValues[i]) {
                    minValues[i] = row.get(i);
                }
                if (row.get(i) > maxValues[i]) {
                    maxValues[i] = row.get(i);
                }
            }
        }

        // Normalize attributes
        for (List<Double> row : data) {
            for (int i = 0; i < numAttributes; i++) {
                double normalizedValue = (row.get(i) - minValues[i]) / (maxValues[i] - minValues[i]);
                row.set(i, normalizedValue);
            }
        }
    }
    
}
