/**
 * Created by mitja on 2018-09-28.
 */


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BigBang extends JComponent implements ActionListener, MouseListener, KeyListener {
    Timer timer;
    World world;
    public BigBang(World world) {
        this.world = world;
        this.addMouseListener(this);
        this.addKeyListener(this);
        this.setFocusable(true);
        this.requestFocus();
    }
    public void start(int delay, int size) {
        JFrame a = new JFrame();
        a.add( this );
        a.setVisible(true);
        a.setSize(size, size);
        a.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.timer = new Timer(delay, this);
        this.timer.start();
    }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
    public void mousePressed(MouseEvent e) { }
    public void mouseReleased(MouseEvent u) { }
    public void mouseClicked(MouseEvent e) { }
    public void keyPressed(KeyEvent e) {this.world.keh(e); }
    public void keyReleased(KeyEvent e) { }
    public void keyTyped(KeyEvent e) { }

    int count = 0;
    public void actionPerformed(ActionEvent e) {
        this.count += 1;
        //System.out.println("Ouch " + this.count);
        if (this.world.hasEnded()) {
            this.timer.stop();
            this.world.sayBye();
        } else {
            this.world.teh();
        }
        this.repaint();
    }
    public void paintComponent(Graphics g) {
        this.world.draw(g);
    }
}
