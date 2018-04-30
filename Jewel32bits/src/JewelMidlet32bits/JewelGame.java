package JewelMidlet32bits;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class JewelGame extends MIDlet implements CommandListener{
	
    Display display;
    private JewelCanvas canvas;
    private Command exitCommand = new Command("Exit", Command.EXIT, 60);
    private Command selectCommand = new Command("Sélectionner", Command.OK, 30);
    
    public JewelGame(){
    	display = Display.getDisplay(this);
    	canvas = new JewelCanvas(this);
    }
    
	protected void destroyApp(boolean arg0){
		// TODO Auto-generated method stub
		display.setCurrent(null);
	}

	protected void pauseApp() {
		// TODO Auto-generated method stub
		
	}

	protected void startApp() throws MIDletStateChangeException {
		// TODO Auto-generated method stub
		canvas.init();
		canvas.addCommand(exitCommand);
		canvas.addCommand(selectCommand);
		canvas.setCommandListener(this);
		
		display.setCurrent(canvas);
	}

	public void commandAction(Command c, Displayable s) {
		// TODO Auto-generated method stub
		if (c == exitCommand) {
		    destroyApp(false);
		    notifyDestroyed();
		}
		else if(c == selectCommand) {
			canvas.applySelect();
		}
	}

}
