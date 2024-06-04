import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class NeuralNetwork {
    private List<double[]> trainInputs = new ArrayList<>();
    private List<double[]> trainOutputs = new ArrayList<>();
    private List<double[]> testInputs = new ArrayList<>();
    private List<double[]> testOutputs = new ArrayList<>();

    private ArrayList<ArrayList<Double>> weightsInputHidden;
    private ArrayList<ArrayList<Double>> weightsHiddenOutput;
    private ArrayList<Double> hiddenLayer;
    private ArrayList<Double> outputLayer;
    private ArrayList<Double> hiddenBias;
    private ArrayList<Double> outputBias;
    private int inputSize;
    private int hiddenLayerSize;
    private int outputSize;
    private double learningRate;

    // private double learningRate = 0.1;
    // private int hiddenLayerSize = 20; 
    private Random random;

    private BufferedWriter trainingResultsWriter;
    private BufferedWriter testingResultsWriter;
    
    public NeuralNetwork(int inputSize, int hiddenSize, int outputSize, double learningRate, long seed) {
        this.inputSize = inputSize;
        this.hiddenLayerSize = hiddenSize;
        this.outputSize = outputSize;
        this.learningRate = learningRate;
        weightsInputHidden = new ArrayList<>();
        weightsHiddenOutput = new ArrayList<>();
        hiddenLayer = new ArrayList<>();
        outputLayer = new ArrayList<>();
        hiddenBias = new ArrayList<>();
        outputBias = new ArrayList<>();

        random = new Random(seed);
        initializeWeights(inputSize, outputSize);

        //initialize writers
        try {
            trainingResultsWriter = new BufferedWriter(new FileWriter("training_results.csv"));
            testingResultsWriter = new BufferedWriter(new FileWriter("testing_results.csv"));

            //write headers to CSV files
            trainingResultsWriter.write("Epoch,Input,Target,Predicted,Error\n");
            testingResultsWriter.write("Input,Target,Predicted\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initializeWeights(int inputSize, int outputSize) {
        weightsInputHidden.clear();
        weightsHiddenOutput.clear();
        hiddenBias.clear();
        outputBias.clear();

        double variance = 2.0 / (inputSize + hiddenLayerSize);
    
        for (int i = 0; i < inputSize; i++) {
            ArrayList<Double> temp = new ArrayList<>();
            for (int j = 0; j < hiddenLayerSize; j++) {
                temp.add(random.nextGaussian() * Math.sqrt(variance)); 
            }
            weightsInputHidden.add(temp);
        }
    
        for (int i = 0; i < hiddenLayerSize; i++) {
            ArrayList<Double> temp = new ArrayList<>();
            // for (int j = 0; j < outputSize; j++) {
                temp.add(random.nextGaussian() * Math.sqrt(variance)); 
            // }
            weightsHiddenOutput.add(temp);
        }
    
        for (int i = 0; i < hiddenLayerSize; i++) {
            hiddenBias.add(random.nextDouble() * 0.1 - 0.05); 
        }
    
        for (int i = 0; i < outputSize; i++) {
            outputBias.add(random.nextDouble() * 0.1 - 0.05); 
        }
    }
    
    public void loadDataset(String filePath, boolean isTraining) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;
        br.readLine(); //skips first line

        while ((line = br.readLine()) != null) {
            String[] values = line.split(",");
            double[] input = new double[values.length - 1];
            double[] output = new double[1];
            for (int i = 0; i < values.length - 1; i++) {
                input[i] = (Double.parseDouble(values[i]) - 0.5) / 0.5; 
            }
            output[0] = Double.parseDouble(values[values.length - 1]);
            if (isTraining) {
                trainInputs.add(input);
                trainOutputs.add(output);
            } else {
                testInputs.add(input);
                testOutputs.add(output);
            }
        }
        br.close();
    }

    public ArrayList<Double> feedforward(double[] input) {

        // 1) --> calculate n1 for each node in hidden layer
        ArrayList<Double> n1 = new ArrayList<>();
        for (int i = 0; i < hiddenLayerSize; i++) {
            double sum = hiddenBias.get(i);
            for (int j = 0; j < input.length; j++) {
                sum += input[j] * weightsInputHidden.get(j).get(i);
            }
            n1.add(sum);
        }

        // 2) --> Calculate the activation f(n1) for each node in the hidden layer
        hiddenLayer.clear();
        for (double value : n1) {
            double activation = sigmoid(value);
            hiddenLayer.add(activation);
        }

        // 3) --> calculate n2 for each node in the output layer
        ArrayList<Double> n2 = new ArrayList<>();
        for (int i = 0; i < outputBias.size(); i++) {
            double sum = outputBias.get(i);
            for (int j = 0; j < hiddenLayerSize; j++) {
                sum += hiddenLayer.get(j) * weightsHiddenOutput.get(j).get(i);
            }
            n2.add(sum);
        }

        // 4) --> calculate the activation f(n2) for each node in the output layer
        outputLayer.clear();
        for (double value : n2) {
            outputLayer.add(sigmoid(value));
        }

        return outputLayer;
    }

    public double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    public double sigmoidDerivative(double x) {
        return x * (1.0 - x);
    }

    public void backpropagation(double[] input, double[] target) {
        ArrayList<Double> outputErrors = new ArrayList<>();
        ArrayList<Double> hiddenErrors = new ArrayList<>();
    
        //1) --> Calculate the error information term for each node in the output layer
        for (int k = 0; k < outputLayer.size(); k++) {
            double error = (target[k] - outputLayer.get(k)) * sigmoidDerivative(outputLayer.get(k));
            outputErrors.add(error);
        }
    
        // 2) --> Calculate the weight correction term for each node in the output layer
        ArrayList<ArrayList<Double>> deltaWeightsHiddenOutput = new ArrayList<>();
        for (int i = 0; i < hiddenLayerSize; i++) {
            ArrayList<Double> temp = new ArrayList<>();
            for (int k = 0; k < outputLayer.size(); k++) {
                double deltaWeight = learningRate * outputErrors.get(k) * hiddenLayer.get(i);
                temp.add(deltaWeight);
            }
            deltaWeightsHiddenOutput.add(temp);
        }
    
        // 3) --> calculate the bias correction term for each node in the OUTPUT layer
        ArrayList<Double> deltaOutputBias = new ArrayList<>();
        for (int k = 0; k < outputBias.size(); k++) {
            double deltaBias = learningRate * outputErrors.get(k);
            deltaOutputBias.add(deltaBias);
        }
    
        // 4) --> calculate sum of delta inputs for each node in HIDDEN layer
        for (int i = 0; i < hiddenLayerSize; i++) {
            double deltaSum = 0;
            for (int k = 0; k < outputLayer.size(); k++) {
                deltaSum += outputErrors.get(k) * weightsHiddenOutput.get(i).get(k);
            }
            hiddenErrors.add(deltaSum);
        }
    
        // 5) --> calculate error information term for each node in the hidden layer
        for (int i = 0; i < hiddenLayerSize; i++) {
            double error = hiddenErrors.get(i) * sigmoidDerivative(hiddenLayer.get(i));
            hiddenErrors.set(i, error);
        }

        //  6) --> calculate  weight correction term for each node in the hidden layer
        ArrayList<ArrayList<Double>> deltaWeightsInputHidden = new ArrayList<>();
        for (int j = 0; j < input.length; j++) {
            ArrayList<Double> temp = new ArrayList<>();
            for (int i = 0; i < hiddenLayerSize; i++) {
                double deltaWeight = learningRate * hiddenErrors.get(i) * input[j];
                temp.add(deltaWeight);
            }
            deltaWeightsInputHidden.add(temp);
        }
    
        // 7) --> calculate bias correction term for each node in hidden layer
        ArrayList<Double> deltaHiddenBias = new ArrayList<>();
        for (int i = 0; i < hiddenBias.size(); i++) {
            double deltaBias = learningRate * hiddenErrors.get(i);
            deltaHiddenBias.add(deltaBias);
        }
    
        for (int i = 0; i < hiddenLayerSize; i++) {
            for (int k = 0; k < outputLayer.size(); k++) {
                double newWeight = weightsHiddenOutput.get(i).get(k) + deltaWeightsHiddenOutput.get(i).get(k);
                weightsHiddenOutput.get(i).set(k, newWeight);
            }
        }
    
        for (int k = 0; k < outputBias.size(); k++) {
            double newBias = outputBias.get(k) + deltaOutputBias.get(k);
            outputBias.set(k, newBias);
        }
    
        for (int j = 0; j < input.length; j++) {
            for (int i = 0; i < hiddenLayerSize; i++) {
                double newWeight = weightsInputHidden.get(j).get(i) + deltaWeightsInputHidden.get(j).get(i);
                weightsInputHidden.get(j).set(i, newWeight);
            }
        }
    
        for (int i = 0; i < hiddenBias.size(); i++) {
            double newBias = hiddenBias.get(i) + deltaHiddenBias.get(i);
            hiddenBias.set(i, newBias);
        }
                
    }

    public void train(int epochs) {

        try {
            // 1 --> initialise:
            initializeWeights(trainInputs.get(0).length, trainOutputs.get(0).length); 

            for (int epoch = 0; epoch < epochs; epoch++) {
                double totalError = 0;

                //representative input-output pairs for this epoch for csv
                int sampleIndex = epoch % trainInputs.size(); // Choose different input-output pair for each epoch
                double[] representativeInput = trainInputs.get(sampleIndex);
                double[] representativeTarget = trainOutputs.get(sampleIndex);
            

                for (int i = 0; i < trainInputs.size(); i++) {
                    double[] input = trainInputs.get(i);
                    double[] target = trainOutputs.get(i);

                    // 2 --> feedforward
                    feedforward(input);
                    
                    // 3 --> backpropagation & update weights + biases
                    backpropagation(input, target); 

                    //calculate error
                    for (int j = 0; j < outputLayer.size(); j++) {
                        totalError += Math.pow(target[j] - outputLayer.get(j), 2);
                    }
                }

                totalError /= trainInputs.size();
                System.out.println("Epoch " + epoch + " - Error: " + totalError);

                trainingResultsWriter.write(epoch + ", " + Arrays.toString(representativeInput) +
                ", " + Arrays.toString(representativeTarget) + ", " + outputLayer.toString() +
                ", " + totalError + "\n");

                //stopping condition: if the total error is less than 0.01, stop training
                if (totalError < 0.1) {
                    break;
                }
            }
            trainingResultsWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void test() {

        try {
            double correct = 0;
            double truePositive = 0;
            double trueNegative = 0;
            double falsePositive = 0;
            double falseNegative = 0;

            for (int i = 0; i < testInputs.size(); i++) {
                double[] input = testInputs.get(i);
                double[] target = testOutputs.get(i);

                ArrayList<Double> output = feedforward(input);
                int predicted = output.get(0) >= 0.5 ? 1 : 0;
                int actual = target[0] >= 0.5 ? 1 : 0;

                //write testing results to file
                testingResultsWriter.write(Arrays.toString(input) + ", " + Arrays.toString(target) +
                ", " + predicted + "\n");

                if (predicted == actual) {
                    correct++;
                    if (actual == 1) {
                        truePositive++;
                    } else {
                        trueNegative++;
                    }
                } else {
                    if (predicted == 1) {
                        falsePositive++;
                    } else {
                        falseNegative++;
                    }
                }
            }

            double accuracy = correct / testInputs.size();
            double sensitivity = truePositive / (truePositive + falseNegative);
            double specificity = trueNegative / (trueNegative + falsePositive);
            double precision = truePositive / (truePositive + falsePositive);
            double fMeasure = 2 * (precision * sensitivity) / (precision + sensitivity);

            System.out.println("Accuracy: " + accuracy);
            System.out.println("Sensitivity: " + sensitivity);
            System.out.println("Specificity: " + specificity);
            System.out.println("F-measure: " + fMeasure);
        
            testingResultsWriter.close();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    //any necessary getters here:
    public List<double[]> getTrainInputs() {
        return trainInputs;
    }

    public List<double[]> getTrainOutputs() {
        return trainOutputs;
    }

    public List<double[]> getTestInputs() {
        return testInputs;
    }

    public List<double[]> getTestOutputs() {
        return testOutputs;
    }
}
