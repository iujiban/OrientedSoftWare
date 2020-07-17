public interface ModelInterface extends java.rmi.Remote {
    Cell[][] getCells(int id) throws java.rmi.RemoteException;
    // above func checks player.canSeeResources before returning

    Player[] getPlayers(int id) throws java.rmi.RemoteException;
    // above func checks player.canSeePlayers before returning

    int registerNewPlayer() throws java.rmi.RemoteException; // inits new player, returns id

    void movePlayer(int id, int dx, int dy) throws java.rmi.RemoteException;

    int playerCollectsResource(int id, int x, int y) throws java.rmi.RemoteException;

    int getPlayerResourceCount(int id) throws java.rmi.RemoteException;
}