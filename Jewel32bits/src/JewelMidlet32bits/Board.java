package JewelMidlet32bits;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class Board {

	private byte[] boardGame;
    private byte[] toExplode;
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

        toExplode = new byte[width * height];
        this.initToExplode();

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
            toExplode = new byte[width * height];
            this.initToExplode();
            
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

        if(posPlayer+offset > 0 && posPlayer+offset < width*height) {
            byte jewelPlayer = boardGame[posPlayer];
            byte jewelSwitch = boardGame[posPlayer+offset];

            boardGame[posPlayer] = jewelSwitch;
            boardGame[posPlayer+offset] = jewelPlayer;
            this.initToExplode();
            this.setExplode();
            this.printExplode();
        }

        return -1;
    }

    // Initialise / RAZ le tableau permettant les explosions
    public void initToExplode(){
        int x,y;

        for(y=0;y<height;y++){
            for(x=0;x<width;x++){
                toExplode[index(x,y)] = 0;
            }
        }

    }

    // Fonction permettant de set up le tableau setExplode
        // Si une case ne possède pas de voisin gauche ou sup de la meme couleur
        //      on effectue une recherche récursive
        //      Sinon on lui attribue la meme valeur que son voisin
    public void setExplode(){
        int x,y,currentJewel;

        for(y=0;y<height;y++){
            for(x=0;x<width;x++){
                currentJewel = index(x,y);
                toExplode[currentJewel] = (byte) sameColor(currentJewel,currentJewel,0);
            }
         }
    }

    // Fonction récursive permettant de savoir combien de joyaux de même couleur
        // sont aligné en partant du noeud node
    private int sameColor(int parentNode, int node, int localStep){        
        int sameColorRight = 0, sameColorDown = 0;
        
        if((node%width+1) < width && boardGame[node] == boardGame[node+1] && node+1 != parentNode ){
            sameColorRight = sameColor(node,node+1, localStep + 1);
            System.out.println(node + " " + localStep + " ," + sameColorRight);
        }  
        
        if((node+width) < width*height && boardGame[node] == boardGame[node + width] && node+width != parentNode){
            sameColorDown = sameColor(node,node+width, localStep + 1);
            System.out.println(node + " " + localStep + " ," + sameColorDown);
        }

        int sameColorLeft = 0, sameColorUp = 0;

        if((node%width-1) > 0 && boardGame[node] == boardGame[node-1] && node-1 != parentNode){
            sameColorLeft = sameColor(node,node-1, localStep + 1);
            System.out.println(node + " " + localStep + " ," + sameColorLeft);
        }  
       
        if((node-width) >= 0 && boardGame[node] == boardGame[node - width] && node-width != parentNode){
            sameColorUp = sameColor(node,node-width, localStep + 1);
            System.out.println(node + " " + localStep + " ," + sameColorUp);
        }
        
        if( sameColorRight + sameColorLeft < 2 && sameColorDown + sameColorUp < 2 && localStep == 0) {
        	return 0;
        }
        
        if(sameColorRight >= 1 || sameColorDown >= 1 || sameColorLeft >= 1 || sameColorUp >= 1){
            return sameColorDown + sameColorRight + sameColorLeft + sameColorUp + 1;
        }

        return 1;
    }
    
    // Affiche dans la console le tableau toExplode
    public void printExplode(){
        int x, y;

        for(y=0; y<width; y++){
            for(x=0;x<height;x++){
                System.out.print(toExplode[index(x,y)] + " ");
            }
            System.out.println(" ");
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