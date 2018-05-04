package JewelMidlet32bits;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class Board {

	public byte[] boardGame;
    public byte[] toExplode;
    
	private int width, height;
	private int posPlayer;

	public static final byte LEFT = 0;
	public static final byte RIGHT = 1;
	public static final byte UP = 2;
	public static final byte DOWN = 3;
	
    public static final byte EMPTY = -1;
    public static final byte WALL = 5;
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
        this.initArray(toExplode);

        Random rnd = new Random();
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
            	byte t = (byte) (1 + (rnd.nextDouble() * 4));
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

    public void set(int x, int y, byte value) {
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
    	return ((indexJewel == posPlayer-1 && posPlayer%width - 1 > -1)
    			|| (indexJewel == posPlayer+1 && posPlayer%width+1 < width) 
    			|| (indexJewel == posPlayer-width && posPlayer-width > -1)
    			|| (indexJewel == posPlayer+width && posPlayer-width < width*height));
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
    
    // Echange deux joyaux si cela permet d'exploser au moins un groupe de joyaux
    public int switchJewels(int move){

        int offset = indexOffset(move);

        if(posPlayer+offset > 0 && posPlayer+offset < width*height) {
            byte jewelPlayer = boardGame[posPlayer];
            byte jewelSwitch = boardGame[posPlayer+offset];

            boardGame[posPlayer] = jewelSwitch;
            boardGame[posPlayer+offset] = jewelPlayer;
            

            int explosion = setExplode();
            
            if(explosion == 0) {
            	System.out.println("Deplacement refuser aucune explosion créer.");
                boardGame[posPlayer] = jewelPlayer;
                boardGame[posPlayer+offset] = jewelSwitch;
            }else {
            	makeExplode();
            	return explosion;
            }
        }
        return -1;
    }
    
    // Initialise / RAZ un tableau
    public void initArray(byte[] array){
        int x,y;

        for(y=0;y<height;y++){
            for(x=0;x<width;x++){
            	int index = index(x,y);
                array[index] = 0;
            }
        }

    }

    // Fonction permettant de remplir le tableau toExplode
    	// Chaque joyaux si il a au moins deux voisins dans le meme alignement
    	// marque avec sa couleur le tableau toExplode[]
    public int setExplode(){
        int x,y,currentJewel,sommeExplode = 0;
        initArray(toExplode);
        for(y=0;y<height;y++){
            for(x=0;x<width;x++){
                currentJewel = index(x,y);
                if(boardGame[currentJewel] > - 1) {
                	toExplode[currentJewel] = (byte) sameColor(currentJewel);
                }
                sommeExplode += toExplode[currentJewel];
            }
         }
        
        return sommeExplode;
    }

    // Permet de faire exploser les alignement référencé dans toExplode[]
    public void makeExplode() {
    	int x, y;
    	for(y=0; y<height;y++) {
    		for(x=0;x<width;x++) {
    			int currentJewel = index(x,y);
    			if(toExplode[currentJewel] != 0) {
    				boardGame[currentJewel] = EMPTY;
    			}
    		}
    	}
    }
    
    // Fonction permettant de savoir combien de joyaux de même couleur
        // sont aligné en partant du noeud node
    private int sameColor(int node){        
        int sameColorRight = 0, sameColorDown = 0, sameColorLeft = 0, sameColorUp = 0;
        
        if(node%width-1 > -1 && boardGame[node] != WALL && boardGame[node] == boardGame[node-1]) {
        	sameColorLeft++;
            if(node%width-2 > -1 && boardGame[node] != WALL && boardGame[node] == boardGame[node-2]) {
            	sameColorLeft++;
            }
        }
        
        if(node+1 < width*height && node%width + 1 < width && boardGame[node] != WALL  && boardGame[node] == boardGame[node+1]) {
        	sameColorRight++;
            if(node+2 < width*height && node%width + 2 < width && boardGame[node] != WALL && boardGame[node] == boardGame[node+2]) {
            	sameColorRight++;
            }
        }
        
        if(node-width > -1 && boardGame[node] != WALL && boardGame[node] == boardGame[node-width]) {
        	sameColorDown++;
            if(node-2*width > -1 && boardGame[node] != WALL && boardGame[node] == boardGame[node-2*width]) {
            	sameColorDown++;
            }
        }
        
        if(node+width < width*height && boardGame[node] != WALL && boardGame[node] == boardGame[node+width]) {
        	sameColorUp++;
            if(node+2*width < width*height && boardGame[node] != WALL && boardGame[node] == boardGame[node+2*width]) {
            	sameColorUp++;
            }
        }
        
        if(sameColorRight + sameColorLeft >=2
        		|| sameColorUp + sameColorDown >= 2) {
        	return boardGame[node];
        }
        else{
        	return 0;
        }
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