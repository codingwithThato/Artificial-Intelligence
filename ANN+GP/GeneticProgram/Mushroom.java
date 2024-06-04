import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Mushroom {
    private List<Double> attributes;
    private String mushroomClass;

    public Mushroom(List<Double> attributes, String mushroomClass) {
        this.attributes = attributes;
        this.mushroomClass = mushroomClass;
    }

    public static List<Mushroom> readMushroomData(String filePath) {
        List<Mushroom> mushrooms = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(filePath))) {
            if (scanner.hasNextLine()) {//skips first line
                scanner.nextLine();
            }
            while (scanner.hasNextLine()) {
                String[] data = scanner.nextLine().split(",");
                List<Double> attributes = new ArrayList<>();
                for (int i = 0; i < data.length - 1; i++) {
                    try {
                        attributes.add(Double.parseDouble(data[i]));
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing attribute value: " + data[i]);
                    }
                }
                String mushroomClass = data[data.length - 1];
                mushrooms.add(new Mushroom(attributes, mushroomClass));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return mushrooms;
    }

    public static void normaliseData(List<Mushroom> mushrooms) {
        int numAttributes = mushrooms.get(0).getAttributes().size();
        double[] minValues = new double[numAttributes];
        double[] maxValues = new double[numAttributes];

        //initialize min and max 
        for (int i = 0; i < numAttributes; i++) {
            minValues[i] = Double.MAX_VALUE;
            maxValues[i] = Double.MIN_VALUE;
        }

        //min and max values for each attribute
        for (Mushroom mushroom : mushrooms) {
            List<Double> attributes = mushroom.getAttributes();
            for (int i = 0; i < attributes.size(); i++) {
                if (attributes.get(i) < minValues[i]) {
                    minValues[i] = attributes.get(i);
                }
                if (attributes.get(i) > maxValues[i]) {
                    maxValues[i] = attributes.get(i);
                }
            }
        }

        //normalise attributes
        for (Mushroom mushroom : mushrooms) {
            List<Double> attributes = mushroom.getAttributes();
            for (int i = 0; i < attributes.size(); i++) {
                double normalizedValue = (attributes.get(i) - minValues[i]) / (maxValues[i] - minValues[i]);
                attributes.set(i, normalizedValue);
            }
        }
    }
    

    public List<Double> getAttributes() {
        return attributes;
    }

    public String getMushroomClass() {
        return mushroomClass;
    }


}
