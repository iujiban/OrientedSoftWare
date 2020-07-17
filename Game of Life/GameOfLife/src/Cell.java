import java.io.Serializable;

public class Cell implements Serializable {
    private int x;
    private int y;
    private int resourceAmount;
    private final double growthParam = 0.1;

    public Cell(int x, int y, int res){
        this.x = x;
        this.y = y;
        this.resourceAmount = res;
    }

    public Cell(Cell cell) {
        this.x = cell.getX();
        this.y = cell.getY();
        this.resourceAmount = cell.getResourceAmount();
    }

    public void addResource(){
        this.resourceAmount += 1;
    }

    public void collect(){ this.resourceAmount -= 1; }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }

    public int getResourceAmount(){
        return resourceAmount;
    }

    public void setResourceAmount(int res) {this.resourceAmount = res;}

    public String toString(){
        return ("Resources in cell: " + this.getResourceAmount());
    }
}
