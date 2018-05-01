package JewelMidlet32bits;

import java.io.InputStream;

import javax.microedition.lcdui.*;

public class JewelCanvas extends Canvas{
	
    private int cell = 1;	
    private int w, h;
    private int bwidth, bheight;
    private static final int delta = 11;
    private boolean isSelected = false;
    private double decreaseSelected = 0.8;
    
    private Board board;
    
    private static int redColor =   0xDB1702;
    private static int greenColor = 0x3A9D23;
    private static int yellowColor = 0xCC5500;
    private static int purpleColor =  0xA10684;
    private static int blueColor =  0x318CE7;
    private static int whiteColor = 0xffffff;
    private static int groundColor = 0x000000;
    
    public JewelCanvas(JewelGame _jewelgame) {
    	super();
		board = new Board();
    }
    
    // Initialise le Canvas avec le level 1
    public void init(){
    	this.w = getWidth();
    	this.h = getHeight();
    	
    	readScreen(2);
    	repaint();
    }
    
    // Permet de d�tecter que le joueur reste appuyer sur la touche
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

    // D�tecte les commandes basic de d�placement UP, DOWN, RIGHT, LEFT
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
            
            if(isSelected){
            	board.switchJewels(move);
            	isSelected = false;
            }else{
            	board.movePlayer(move);
            }

            repaint();
        } // End of synchronization on the Board.
    }
    
    // Permet de charger le board � l'aide d'un fichier contenant le niveau
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
    
    // Permet d'appliquer l'etat s�lectionner ou d�selectionner
    public void applySelect() {
    	isSelected = (isSelected == false);
    	repaint();
    }
    
    // Met � jour la couche graphique du board
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
	  
		    // Place un fond de la couleur choisi
		    g.setColor(groundColor);
		    g.fillRect(0, 0, w, h);
		    
    		int sizeCell = cell;
    		int posRedim = 0;
    		
		    for(y=0; y < y2; y++){
		    	for(x=0; x < x2; x++){
		    		
		    		// Permet de visualiser la position du joueur dans le plateau
		    		if(board.equalPosPlayer(x, y)){
		    			g.setColor(whiteColor);	
		    			g.fillArc(x*cell + delta - 1, y*cell + delta - 1, cell+2, cell+2,0,360);
		    		}
		    		
		    		// Adapte la taille du joyaux pour marquer la s�lection d'un joyau
		    		sizeCell = cell;
		    		posRedim = 0;
		    		
		    		if(isSelected && board.proximatePlayer(x,y)) {
	    				sizeCell = (int) (sizeCell * decreaseSelected);
	    				posRedim = (sizeCell-cell)/2;
		    		}
		    		
		    		byte v = board.get(x, y);
				    switch (v) {
				    
					    case Board.RED:
						g.setColor(redColor);
						g.fillArc(x*cell + delta - posRedim, y*cell + delta - posRedim, sizeCell, sizeCell, 0, 360);
						break;
	
					    case Board.GREEN:
							g.setColor(greenColor);
							g.fillArc(x*cell + delta - posRedim, y*cell + delta - posRedim, sizeCell, sizeCell, 0, 360);
						break;
						
					    case Board.YELLOW:
							g.setColor(yellowColor);
							g.fillArc(x*cell + delta - posRedim, y*cell + delta - posRedim, sizeCell, sizeCell, 0, 360);
						break;
	
					    case Board.PURPLE:
							g.setColor(purpleColor);
							g.fillArc(x*cell + delta - posRedim, y*cell + delta - posRedim, sizeCell, sizeCell, 0, 360);
						break;
						
					    case Board.BLUE:
							g.setColor(blueColor);
							g.fillArc(x*cell + delta - posRedim, y*cell + delta - posRedim, sizeCell, sizeCell, 0, 360);
						break;
						
					    default:
						break;
				    }
		    	}
		    }	    
		}
	}

}