public class PlayerFactory {

    public static Player createPlayer(int id, int x, int y) {
        return new Player(id, x, y);
    }
}