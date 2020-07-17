/**
 * Created by mitja on 2018-09-28.
 */

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;


public interface World {
    public void teh();
    public void draw(Graphics g);
    public void meh(MouseEvent e);
    public void keh(KeyEvent e);
    public boolean hasEnded();
    public void sayBye();
}
