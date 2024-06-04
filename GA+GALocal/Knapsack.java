import java.io.FileNotFoundException;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Knapsack {
 
    private int capacity;
    private double totalWeight;
    private double totalValue;
    private int totalItems;
    private int totalItemsRand;
    private ArrayList<Item> items; //array of all the items
    private ArrayList<Item> itemRand; //array of random items

    public Knapsack(String fileName){

        if(fileName.length() == 0){ //empty file
            this.capacity = 0;
            this.totalWeight = 0;
            this.totalValue = 0;
            this.totalItems = 0;
            items = new ArrayList<Item>();
        } 
        
        try(Scanner reader = new Scanner(new File(fileName))){
            this.totalItems = reader.nextInt(); 
            
            this.capacity = reader.nextInt();
            
            reader.nextLine();
            
            items = new ArrayList<Item>();

            while(reader.hasNextLine()){
                double weight = reader.nextDouble(); //read 1st val
                double value = reader.nextDouble(); //read 2nd val
                addItem(weight, value); //attach those vals to item obj in items array

                if (reader.hasNextLine()) reader.nextLine();
            }
            
            reader.close();
        } 
        catch (FileNotFoundException e) {
            System.out.println("File not found!");
        }
    }

    public Knapsack(int cap) { //THIS IS THE CONSTRUCTOR FOR NEW EMPTY KNAPSACK
        this.capacity = cap;
        this.totalWeight = 0;
        this.totalValue = 0;
        this.totalItemsRand = 0;
        this.itemRand = new ArrayList<Item>();
    }

    public void addItem(double w, double val) {
        items.add(new Item(w, val));
    }

    public void addItemRand(double w, double val) { //for the new empty knapsack
        Item newItem = new Item(w, val);
        newItem.setTaken(true);
        itemRand.add(newItem);
        totalWeight += w;
        totalItemsRand++;
    }

    private void addItemNotTaken() {
        Item newItem = new Item(0, 0);
        newItem.setTaken(false);
        itemRand.add(newItem);
        totalItemsRand++;
    }

    public Knapsack randomize(Knapsack kp, Random rand) { //randomises which items we want in kp
        Knapsack randKp = new Knapsack(kp.capacity);
        for (Item item : items) {
            if (rand.nextBoolean() && (randKp.totalWeight + item.getWeight() <= kp.capacity)) {
                randKp.addItemRand(item.getWeight(), item.getValue()); //adds existing item to the rand item array list
                totalValue += item.getValue(); 
                randKp.totalValue += item.getValue();
            }
            else if(!rand.nextBoolean()){
                randKp.addItemNotTaken(); //stores data of item not taken in the array
            }
        }

        randKp.totalItemsRand = randKp.getItemsRand().size();

        return randKp; 
    }



    //getters
    public double getTotalWeight() {
        return totalWeight;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public int getTotalItems() { 
        return totalItemsRand;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public ArrayList<Item> getItemsRand() {
        return itemRand;
    }

    public double calculateFitness() {
        if(totalWeight >= capacity) this.totalValue = 0; //if weight is bigger than cap then fitness is ZERO
        return totalValue;
    }

    public void setFitness(double fitness) {
        this.totalValue = fitness;
    }
    
    public int getTotalItemsAvailable() {
        return totalItems;
    }

    public int getCapacity() {
       return capacity;
    }

    
    //to be used for local search :
    public int[] convertKnapsack() {
        int[] solution = new int[totalItems];
        for (int i = 0; i < totalItems; i++) {
            solution[i] = itemRand.get(i).isTaken() ? 1 : 0;
        }
        return solution;
    }

    public void setItemsFromSolution(int[] solution) {
        for (int i = 0; i < totalItems; i++) {
            itemRand.get(i).setTaken(solution[i] == 1);
        }
    }

    //review this toString method
    @Override
    public String toString() {
        return "Knapsack: " +
                "\ncapacity=" + capacity +
                ", \ntotalWeight=" + totalWeight +
                ", \ntotalValue=" + totalValue +
                ", \ntotalItemsRand=" + totalItemsRand +
                // ", \nitems array=" + items.toString() +
                ", \nrandom items array=" + itemRand.toString();
    }


}

