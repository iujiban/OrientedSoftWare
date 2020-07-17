public abstract class Rule {
    public void run(Cell cell, Cell[] neighbors) {
        int neighborCount = 0;
        for (Cell neighbor : neighbors) {
            if (neighbor != null && (neighbor.getResourceAmount() > 0))
                neighborCount++;
        }

        this.performRule(cell, neighborCount);
    }

    public abstract void performRule(Cell cell, int neighborCount);

}
