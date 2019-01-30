package TankWar;

public class CPaintThread extends Thread{
	private TankClient tc;
	private boolean keep_paint = true;
	private int fps = 10;
	CPaintThread(TankClient tc){
		this.tc = tc;
	}
	public void run(){
		while(keep_paint){
			try {sleep(1000/fps);} catch (InterruptedException e) {e.printStackTrace();}
			tc.repaint();
		}
	}
	public void set_keep_paint(boolean keep_paint){this.keep_paint = keep_paint;}
}
