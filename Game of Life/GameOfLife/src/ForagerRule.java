public class ForagerRule extends Rule {

    private final double PARAM = 0.3;

    public void performRule(Cell cell, int neighborCount){
        if (cell.getResourceAmount() < 7){
            double decide = Math.random();
            if (decide < ((PARAM * neighborCount) / 8)){
                cell.addResource();
            }

        }
    }
}
