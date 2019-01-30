package TankWar;

public class PaintThread extends Thread{
	private TankServer ts;
	private boolean keep_paint = true;
	private int fps = 10;
	PaintThread(TankServer ts){
		this.ts = ts;
	}
	public void run(){
		while(keep_paint){
			try {sleep(1000/fps);} catch (InterruptedException e) {e.printStackTrace();}
			ts.repaint();
		}
	}
	public void set_keep_paint(boolean keep_paint){this.keep_paint = keep_paint;}
}