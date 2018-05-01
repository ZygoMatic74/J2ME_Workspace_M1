package JewelMidlet32bits;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class Board {

	private byte[] boardGame;
	private int width, height;
	private int posPlayer;

	public static final byte LEFT = 0;
	public static final byte RIGHT = 1;
	public static final byte UP = 2;
	public static final byte DOWN = 3;
	
    public static final byte EMPTY = -1;
	public static final byte RED = 0;
	public static final byte YELLOW = 1;
	public static final byte GREEN = 2;
	public static final byte PURPLE = 3;
	public static final byte BLUE = 4;
	
	public Board(){
		generate();
	}
	
	/**
	 * Generate a random board
	 */
    public void generate() {
        width = 9;
        height = 7;
        boardGame = new byte[width * height];
        
        Random rnd = new Random();
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
            	byte t = (byte) (rnd.nextDouble() * 5);
                set(x, y, t);
            }
        }
    }
    
    // Permet de charger le board avec un fichier
    public void read(InputStream is){
        int c;
        int x = 0, y = 0;
        try {
        	
			width = is.read() - 48;
			while((c = is.read()) != 13){
				width = width*10 + (c - 48);
			}
			is.read();
			
			height = is.read() - 48;
			while((c = is.read()) != 13){
				height = height*10 + (c - 48);
			}
			is.read();
			
            boardGame = new byte[width * height];
            
            posPlayer = index(width/2, height/2);
            
            while ((c = is.read()) != -1) {
                switch (c) {
                    case '\n':
                        y++;
                        x = 0;
                        break;

                    case '0':
                        boardGame[y * width + x++] = RED;
                        break;

                    case '1':
                        boardGame[y * width + x++] = YELLOW;
                        break;

                    case '2':
                        boardGame[y * width + x++] = GREEN;
                        break;

                    case '3':
                        boardGame[y * width + x++] = PURPLE;
                        break;
                        
                    case '4':
                        boardGame[y * width + x++] = BLUE;
                        break;
                }
            }
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        }
        
        try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public byte get(int x, int y) {
        int offset = index(x, y);
        return boardGame[offset];
    }

    private void set(int x, int y, byte value) {
        boardGame[index(x, y)] = value;
    }

    // Retourne l'index de la case 
    private int index(int x, int y) {
        if (x < 0 || x >= width ||
        y < 0 || y >= height)
            return -1;

        return y * width + x;
    }
    
    // Retourne la largeur du board
    public int getWidth() {
        return width;
    }

    // Retournes la hauteur du board
    public int getHeight() {
        return height;
    }
	
    // Renvoie true si les pos x,y sont celles du joueur
    public boolean equalPosPlayer(int x, int y){
    	return (index(x,y) == posPlayer);
    }
    
    //Renvoie true si la case(x,y) est adjacente au joueur
    public boolean proximatePlayer(int x, int y) {
    	int indexJewel = index(x,y);
    	return (indexJewel == posPlayer-1 
    			|| indexJewel == posPlayer+1 
    			|| indexJewel == posPlayer-width 
    			|| indexJewel == posPlayer+width);
    }

    // Déplace le joueur en fonction du type de mouvement UP, DOWN, RIGHT, LEFT
    public void movePlayer(int move){
    	int offset = indexOffset(move);
    	if(posPlayer+offset > -1 && posPlayer+offset < width*height) {
    		posPlayer += offset;
    	}
    	// Les conditions suivantes permettent un déplacement cyclique du joueur
    	else if(posPlayer+offset <= -1) {
    		if(move == LEFT) {
    			posPlayer = (width*height)-1;
    		}
    		else {
    			posPlayer = (posPlayer) + height * (width - 1);
    		}
    	}
    	else if(posPlayer+offset >= width*height) {
    		if(move == RIGHT) {
    			posPlayer = 0;
    		}
    		else {
    			posPlayer = posPlayer%width;
    		}
    	}
    }
    
    // Echange deux joyaux si cela permet d'exploser un groupe
    public int switchJewels(int move){

        int offset = indexOffset(move);

        if(posPlayer+offset > -1 && posPlayer+offset < width*height) {
            byte jewelPlayer = boardGame[posPlayer];
            byte jewelSwitch = boardGame[posPlayer+offset];

            boardGame[posPlayer] = jewelSwitch;
            boardGame[posPlayer+offset] = jewelPlayer;
        }

        return -1;
    }

    // Explose tous les joyaux aligné
    public int explodeJewels(){
        int x,y;

        for(x=0;x<height;x++){
            for(y=0;y<width;y++){

            }
        }
    }

    // Renvoie la valeur en offset du déplacement
    private int indexOffset(int move) {
        switch (move & 3) {
        case LEFT:
            return -1;

        case RIGHT:
            return +1;

        case UP:
            return -width;

        case DOWN:
            return +width;
        }
        return 0;
    }
}