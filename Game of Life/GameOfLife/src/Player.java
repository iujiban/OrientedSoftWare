public class Player implements java.io.Serializable{
    int id;
    int resourcesCollected = 0;
    int x = 0; int y = 0;

    boolean canSeePlayers;
    boolean canSeeResources;

    Player() {
        this.id = -1;
    }

    Player(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    int getX() { return this.x; }
    int getY() { return this.y; }
    void setX(int x) { this.x = x; }
    void setY(int y) { this.y = y; }
    void move(int dx, int dy) {
        if ((x > 0 && dx == -1) || (x < 256 * 4 && dx == 1)) {
            this.x += dx;
        }
        if ((y > 0 && dy == -1) || (y < 256 * 4 && dy == 1)){
            this.y += dy;
        }
    }

    void resourceCollected() { this.resourcesCollected += 1; }
    int getResourcesCollected() { return this.resourcesCollected; }
}