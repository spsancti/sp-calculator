import java.awt.Graphics;
import javax.swing.JTextField;

public class CDisplay extends JTextField {
	private static final long serialVersionUID = 6798372382311347984L;
	private boolean mNeedPutM = false;

    public void setNeedPutM(boolean need) {
        mNeedPutM = need;
        repaint();
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
      //  System.out.println("CDisplay.paint() called");
        
        if(mNeedPutM) { 
        	int h = getHeight();
        	g.drawString("M", 5, h - 5);
        	System.out.println("CDisplay:" + "Put M sign at: " + Integer.toString(h));
        }
    }
}
