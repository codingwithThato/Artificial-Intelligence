public class Item {
    private double weight;
    private double value;
    private boolean isTaken;

    public Item(double weight, double value) {
        this.weight = weight;
        this.value = value;
        this.isTaken = false;
    }

    //getters
    public double getWeight() {
        return weight;
    }

    public double getValue() {
        return value;
    }

    public boolean isTaken() {
        return isTaken;
    }

    //setters
    public void setTaken(boolean taken) {
        isTaken = taken;
    }

    //to string to print out what info about the item.
    @Override
    public String toString() {
        return "Item: " +
                "weight=" + weight +
                ", value=" + value +
                ", isTaken=" + isTaken;
    }
}

//this class should : 
/*
 * define what an item has (weight, value, isTaken)
 */
