/**
 * Created by mitja on 2018-09-28.
 * Edited by kjyohler on 2018-09-28.
 */

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.rmi.Naming;
import java.rmi.Remote;

public class Life extends Player implements World {
	Cell[][] cells;
	ModelInterface modelServer;
	Player[] players;
	int id;

	public Life() throws java.rmi.RemoteException {
		try {
			Remote remoteObject = Naming.lookup("//localhost/Team07Server");
			this.modelServer = (ModelInterface)remoteObject;
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		System.out.println("registering...");
		this.id = modelServer.registerNewPlayer();
		System.out.println("registered. getting cell array");
		this.cells = modelServer.getCells(id);
		System.out.println("cell array acquired. Getting players");
		this.players = modelServer.getPlayers(id);
		System.out.println("got players. initialized connection");
	}

	public void teh() {
		try {
			this.cells = modelServer.getCells(id);
			players = modelServer.getPlayers(id);

		} catch (Exception e) {
			System.out.println("Error: ");
			System.out.println(e.getMessage());
		}
	}
	public void meh(MouseEvent e) {

	}
	public void keh(KeyEvent e) {
		int keyCode = e.getKeyCode();
		try {
			switch (keyCode) {
				case KeyEvent.VK_UP:
					modelServer.movePlayer(id, 0, -1);
					break;
				case KeyEvent.VK_DOWN:
					modelServer.movePlayer(id, 0, 1);
					break;
				case KeyEvent.VK_LEFT:
					modelServer.movePlayer(id, -1, 0);
					break;
				case KeyEvent.VK_RIGHT:
					modelServer.movePlayer(id, 1, 0);
					break;
			}
			/*Player p = modelServer.getPlayers(id)[id];
			this.x = p.x;
			this.y = p.y;
			modelServer.playerCollectsResource(id, x, y);*/
		} catch (java.rmi.RemoteException re) {
			re.printStackTrace();
		}
	}

	public void draw(Graphics g) {
		for (int r = 0; r < cells.length; r++) {
			for (int c = 0; c < cells[r].length; c++) {
				if (cells[r][c] != null) {
					switch (cells[r][c].getResourceAmount()) {
                        case 0:
                            g.setColor(Color.WHITE);
                            break;
                        case 1:
                            g.setColor(new Color(0, 255, 0));
                            break;
                        case 2:
                            g.setColor(new Color(0, 235, 0));
                            break;
                        case 3:
                            g.setColor(new Color(0, 215, 0));
                            break;
                        case 4:
                            g.setColor(new Color(0, 195, 0));
                            break;
                        case 5:
                            g.setColor(new Color(0, 175, 0));
                            break;
                        case 6:
                            g.setColor(new Color(0, 155, 0));
                            break;
                        case 7:
                            g.setColor(new Color(0, 135, 0));
                            break;
                        default:
                            System.out.println("invalid color");
                            g.setColor(new Color(255, 0, 0));

					}
					g.fillRect(cells[r][c].getX() * 4, cells[r][c].getY() * 4, 4, 4);
				}
				/*if(cells[r][c].getResourceAmount() == 0) {
					g.setColor(Color.WHITE);
				} else {
					g.setColor(Color.GREEN);
				}
				*/
			}
		}

		for (Player pl : players) {
			if (pl != null) {
				if (pl.id == id) {
					g.setColor(Color.BLUE);
				} else {
					g.setColor(Color.BLACK);
				}
				g.fillRect(pl.x * 4, pl.y * 4, 4, 4);
			}
		}
		g.setFont(new Font("Timer", Font.PLAIN, 14));
		g.drawString("Time Left: " + players[players.length - 1].getX(), 0, 15);
		g.drawString("Score: " + players[id].getResourcesCollected(), 0, 30);
	}

	public boolean hasEnded() {
		return false;
	}

	public void sayBye() {
		System.out.println("Bye Bye Blackbird, I mean Life.");
	}

}
