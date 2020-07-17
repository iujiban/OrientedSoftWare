import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;
import java.util.Scanner;
import java.rmi.registry.*;

public class Model extends UnicastRemoteObject implements ModelInterface, ActionListener, ChangeListener {
    private Cell[][] cells;
    private final int PLAYER_COUNT = 10;
    private Timer timer;
    private Player[] players;
    private int current = 0;
    private final int gridSize = 256;
    Rule rule;
    private int timeLeft;
    final Cell[][] emptyGrid = new Cell[256][256];

    private JFrame frame;
    private JSlider res;
    private JSlider length;
    private JCheckBox playerV;
    private JCheckBox resV;
    private JLabel fracLabel;

    private boolean playersCanSeeResources;
    private boolean playersCanSeePlayers;
    private boolean started, ended;


    public Model() throws java.rmi.RemoteException {
        super();

        rule = new ForagerRule();
        fracLabel = new JLabel("% per cluster: (50/50)");
        fracLabel.setHorizontalAlignment(SwingConstants.CENTER);
        int i = 0;

        /*
        //cells = new Cell[256][256];
        try {
            Scanner inputFile = new Scanner(new File("src/GoL.txt")); //needs to be "src/GoL.txt" in IntelliJ
            while (inputFile.hasNextLine()) { // While there is still data in the file
                String line = inputFile.nextLine(); // grab the next line
                for(int j = 0; j < line.length(); j++) {
                    cells[i][j] = new Cell(i, j, Integer.parseInt(line.substring(j, j+1)));
                }
                i++;
            }
        } catch (FileNotFoundException e) {
            System.out.println("Please create file GoL.txt");
            return;
        }*/
        players = new Player[PLAYER_COUNT + 1];
        players[PLAYER_COUNT] = PlayerFactory.createPlayer(PLAYER_COUNT, timeLeft, 1000);

        started = false;
        ended = false;

        this.timer = new Timer(1000, this);
        //this.timer.start();
        this.createAndShowGUI();
    }

    public Cell[] getNeighbors(Cell cell) {
        Cell[] neighbors = new Cell[8];

        int[] ith = { 0, 1, 1, -1, 0, -1 ,-1, 1};
        int[] jth = { 1, 0, 1, 0, -1, -1 ,1,-1};
        // All neighbors of cell
        for (int k = 0; k < 8; k++) {
            if (inBounds(cell.getX() + ith[k], cell.getY() + jth[k])) {
                neighbors[k] = cells[cell.getX() + ith[k]][cell.getY() + jth[k]];
            }
        }
        return neighbors;
    }

    private boolean inBounds(int i, int j) {
        if (i < 0 || j < 0 || i >= this.cells.length || j >= this.cells.length)
            return false;
        return true;
    }

    public Cell[][] getCells(int id) throws RemoteException {
        System.out.println("Received call...");
        if (playersCanSeeResources){
            return cells;
        }
        else { return emptyGrid; }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("was ticked...");
        players[PLAYER_COUNT].setX(--timeLeft);
        Cell[][] newCells = new Cell[256][256];
        // Run rules
        if (timeLeft > 0) {
            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells[i].length; j++) {
                    Cell newCell = new Cell(cells[i][j]);
                    Cell[] neighbors = this.getNeighbors(cells[i][j]);
                    rule.run(newCell, neighbors);
                    newCells[i][j] = newCell;
                }
            }
        }
        this.cells = newCells;
        if (timeLeft == 0){
            timer.stop();
            this.started = false;
            this.ended = true;
        }
    }

    @Override
    public void stateChanged(ChangeEvent ce) {
        JSlider s = (JSlider) ce.getSource();
        fracLabel.setText("% per cluster: (" + s.getValue() + "/" + (100 - s.getValue()) + ")");
    }

    public static void main(String[] args) {
        // We need to set a security manager since this is a server.
        // This will allow us to customize access privileges to
        // remote clients.
        //System.setSecurityManager(new SecurityManager()); //comment out in IntelliJ

        try {
            // Create a ServerRMI object and announce its service to the
            // registry.
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT); //*un-comment in IntelliJ
            Model modelServer = new Model();
            Naming.rebind("//localhost/Team07Server", modelServer);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public Player[] getPlayers(int id) throws java.rmi.RemoteException {
        if (playersCanSeePlayers) {
            return players;
        } else {
            // Makes an array where all players except the player at the specified id are blank

            Player[] blank = new Player[PLAYER_COUNT + 1];

            blank[id] = players[id];

            blank[PLAYER_COUNT] = players[PLAYER_COUNT];

            return blank;
        }
    }

    public int registerNewPlayer() throws java.rmi.RemoteException {
        if (current < PLAYER_COUNT) {
            players[current] = PlayerFactory.createPlayer(current, 0, 0);
        }
        current++;
        System.out.println("id to be sent: " + (current - 1));
        return (current - 1);
    }

    public void movePlayer(int id, int dx, int dy) throws java.rmi.RemoteException {
        boolean canMove = started;
        for (int i = 0; i < players.length; i++){
            if ((i != id) && (players[i] != null)){
                Player pl = players[i];
                if ((pl.getX() == players[id].getX() + dx) && (pl.getY() == players[id].getY() + dy)){
                    canMove = false;
                }
            }
        }
        if (canMove){
            players[id].move(dx, dy);
            this.playerCollectsResource(id, players[id].getX(), players[id].getY());
        }
    }

    public int playerCollectsResource(int id, int x, int y) throws java.rmi.RemoteException {
        if (cells[x][y].getResourceAmount() > 0) {
            players[id].resourceCollected();
            cells[x][y].collect();
        }

        return players[id].getResourcesCollected();
    }

    public int getPlayerResourceCount(int id) throws java.rmi.RemoteException {
        return players[id].resourcesCollected;
    }

    public void start() {
        this.started = true;

        this.frame.setVisible(false);

        this.cells = createStartingGrid(res.getValue());

        this.playersCanSeeResources = resV.isSelected();
        this.playersCanSeePlayers = playerV.isSelected();

        this.timeLeft = length.getValue() * 60;

        this.timer.start();
    }

    private Cell[][] createStartingGrid(int pctClusterOne) {
        Cell[][] c = new Cell[256][256];

        int pctClusterTwo = 100 - pctClusterOne;

        Random rn = new Random();
        // Buffer of 14 on all sides, one is in the top half and the other is in the bottom half.
        int[] clusterOneLoc = {rn.nextInt(229) + 14, rn.nextInt(101) + 14};
        int[] clusterTwoLoc = {rn.nextInt(229) + 14, rn.nextInt(101) + 142};

        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 256; j++) {
                if ((i == clusterOneLoc[0] && j == clusterOneLoc[1]) || (i == clusterTwoLoc[0] && j == clusterTwoLoc[1])) {
                    // This cell is the epicenter of one of the clusters so it has max resources
                    c[i][j] = new Cell(i, j, 7);
                } else {
                    double dist1 = Math.abs(Math.sqrt(Math.pow(i - clusterOneLoc[0], 2) + Math.pow(j - clusterOneLoc[1], 2)));
                    double dist2 = Math.abs(Math.sqrt(Math.pow(i - clusterTwoLoc[0], 2) + Math.pow(j - clusterTwoLoc[1], 2)));
                    if (dist1 < pctClusterOne / 20 || dist2 < pctClusterTwo / 20) {
                        // This cell is "close enough" to the epicenter to have a resource ("close enough" determined by
                        // a comparison of the Euclidian distance and the desired size of that cluster)
                        c[i][j] = new Cell(i, j, rn.nextInt(7));
                    } else {
                        // This cell is "too far" from either epicenter so it gets no resource.
                        c[i][j] = new Cell(i, j, 0);
                    }
                }
            }
        }

        return c;
    }

    private void createAndShowGUI() {
        frame = new JFrame("Configure Game");
        frame.setSize(300, 500);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(4, 1));
        JButton go = new JButton("Start");
        go.addActionListener(e -> this.start());

        // Labels
        JLabel dist = new JLabel("Resource Distribution:", SwingConstants.CENTER);
        JLabel limit = new JLabel("Round Length:", SwingConstants.CENTER);
        JLabel visibility = new JLabel("Visibility:", SwingConstants.CENTER);

        // Controlling Resource Distribution
        res = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        res.addChangeListener(this);
        res.setMajorTickSpacing(25);
        res.setMinorTickSpacing(5);
        res.setPaintTicks(true);
        res.setPaintLabels(true);

        // Controlling Round Length
        length = new JSlider(JSlider.HORIZONTAL, 0, 20, 5);
        length.setMajorTickSpacing(5);
        length.setMinorTickSpacing(1);
        length.setPaintTicks(true);
        length.setPaintLabels(true);

        // Controlling visibility of players and resources
        playerV = new JCheckBox("Players");
        playerV.setSelected(true);
        playerV.setHorizontalAlignment(SwingConstants.CENTER);

        resV = new JCheckBox("Resrouces");
        resV.setSelected(true);
        resV.setHorizontalAlignment(SwingConstants.CENTER);

        // Boxing it up
        JPanel p1 = new JPanel(new GridLayout(3, 1));
        p1.add(dist);
        p1.add(fracLabel);
        p1.add(res);

        JPanel p2 = new JPanel(new GridLayout(2, 1));
        p2.add(limit);
        p2.add(length);

        JPanel p3 = new JPanel(new GridLayout(1, 2));
        p3.add(playerV);
        p3.add(resV);

        JPanel p4 = new JPanel(new GridLayout(2, 1));
        p4.add(visibility);
        p4.add(p3);

        // Adding to master panel
        panel.add(p1);
        panel.add(p2);
        panel.add(p4);
        panel.add(go);
        frame.add(panel);

        frame.setVisible(true);
    }
}
