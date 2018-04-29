package JewelMidlet32bits;

import java.io.InputStream;

import javax.microedition.lcdui.*;

public class JewelCanvas extends Canvas{
	
    private int cell = 1;	
    private int w, h;
    private int bwidth, bheight;
    private static final int delta = 11;

    private Board board;
    
    private static int redColor =   0xDB1702;
    private static int greenColor = 0x3A9D23;
    private static int yellowColor = 0xC2F732;
    private static int purpleColor =  0xA10684;
    private static int blueColor =  0x318CE7;
    private static int whiteColor = 0xffffff;
    private static int groundColor = 0x000000;
    
    public JewelCanvas(JewelGame _jewelgame) {
    	super();
		board = new Board();
    }
    
    public void init(){
    	this.w = getWidth();
    	this.h = getHeight();
    	
    	readScreen(1);
    	repaint();
    }
    
    protected void keyRepeated(int keyCode) {
        int action = getGameAction(keyCode);
        switch (action) {
        case Canvas.LEFT:
        case Canvas.RIGHT:
        case Canvas.UP:
        case Canvas.DOWN:
            keyPressed(keyCode);
            break;
        default:
            break;
        }
    }

    protected void keyPressed(int keyCode) {

        // Protect the data from changing during painting.
        synchronized (board) {

            int action = getGameAction(keyCode);
            int move;

            switch (action) {
            case Canvas.LEFT:
                move = Board.LEFT;

                break;

            case Canvas.RIGHT:
                move = Board.RIGHT;

                break;

            case Canvas.DOWN:
                move = Board.DOWN;

                break;

            case Canvas.UP:
                move = Board.UP;

                break;

            default:
                return;
            }
            
            board.movePlayer(move);
            repaint();
        } // End of synchronization on the Board.
    }
    
    private boolean readScreen(int _level) {
    	
    	if (_level <= 0) {
    		board.generate();
    	} else {
		    InputStream is = null;
		    try {
				is = getClass().getResourceAsStream(
							"/JewelResources32bits/Levels/level."
							+ _level);
				if (is != null) {
				    board.read(is);
				    is.close();
				} else {
				    System.out.println(
						   "Could not find the game board for level "
						   + _level);
				    return false;
				}
		    } 
		    catch (java.io.IOException ex) {
		    	return false;
		    }
    	}
    	
		bwidth = board.getWidth();
		bheight = board.getHeight();
	
		cell = ((h-14) / bheight < w / bwidth) ? ((h-14) - delta) / bheight : (w - delta) / bwidth;
		return true;
    }
    
	protected void paint(Graphics g) {
		// TODO Auto-generated method stub
		synchronized (board) {

		    int x = 0, y =  0, x2 = bwidth, y2 = bheight;

		    // Figure what part needs to be repainted.
		    int clipx = g.getClipX();
		    int clipy = g.getClipY();
		    int clipw = g.getClipWidth();
		    int cliph = g.getClipHeight();
		    x = clipx / cell;
		    y = clipy / cell;
		    x2 = (clipx + clipw + cell-1) / cell;
		    y2 = (clipy + cliph + cell-1) / cell;
		    if (x2 > bwidth)
			x2 = bwidth;
		    if (y2 > bheight)
			y2 = bheight;
	  
		    // Fill entire area with ground color
		    g.setColor(groundColor);
		    g.fillRect(0, 0, w, h);
		    
		    for(y=0; y < y2; y++){
		    	for(x=0; x < x2; x++){
		    		
		    		if(board.equalPosPlayer(x, y)){
		    			g.setColor(whiteColor);
		    			g.fillRect(x*cell + delta, y*cell + delta, cell, cell);
		    		}
		    		
		    		byte v = board.get(x, y);
				    switch (v) {
				    
					    case Board.RED:
						g.setColor(redColor);
						g.fillArc(x*cell + delta, y*cell + delta, cell, cell, 0, 360);
						break;
	
					    case Board.GREEN:
							g.setColor(greenColor);
							g.fillArc(x*cell + delta, y*cell + delta, cell, cell, 0, 360);
						break;
						
					    case Board.YELLOW:
							g.setColor(yellowColor);
							g.fillArc(x*cell + delta, y*cell + delta, cell, cell, 0, 360);
						break;
	
					    case Board.PURPLE:
							g.setColor(purpleColor);
							g.fillArc(x*cell + delta, y*cell + delta, cell, cell, 0, 360);
						break;
						
					    case Board.BLUE:
							g.setColor(blueColor);
							g.fillArc(x*cell + delta, y*cell + delta, cell, cell, 0, 360);
						break;
						
					    default:
						break;
				    }
		    	}
		    }	    
		}
	}

}