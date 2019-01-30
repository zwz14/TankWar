package TankWar;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;

public class Home {
	private static int h_location_x = 280;
	private static int h_location_y = 515;
	private static int h_width = 30;
	private static int h_height = 30;
	private boolean islive = true;
	private static Image[] homeImages = null;
	private static Toolkit tk = Toolkit.getDefaultToolkit();
	private ServerWar sw = null;
	private ClientWar cw = null;
	static{
		homeImages = new Image[]{
				tk.getImage(TankClient.class.getResource("/image/home.jpg"))};
	}
	Home(ClientWar cw){
		this.cw = cw;
	}
	Home(ServerWar sw){
		this.sw = sw;
	}
	public void draw(Graphics g){
		g.drawImage(homeImages[0], h_location_x, h_location_y, null);
	}
	public void gameover(Graphics g){
		if(cw != null){
			cw.bullets.clear();
			cw.tanks.clear();
			cw.host_tank.set_live_state(false);
			cw.client_tank.set_live_state(false);
			cw.tc.set_jbPause(false);
			cw.tc.set_jbContinue(false);
		}
		if(sw != null){
			sw.bullets.clear();
			sw.tanks.clear();
			sw.host_tank.set_live_state(false);
			sw.client_tank.set_live_state(false);
			sw.ts.set_jbPause(false);
			sw.ts.set_jbContinue(false);
			sw.ts.set_jbbegin(true);
		}
		Color c = g.getColor(); 
		g.setColor(Color.red);
		Font f = g.getFont();
		g.setFont(new Font(" ", Font.PLAIN, 40));
		g.drawString("ƒ„ ‰¡À£°", 220, 250);
		g.drawString("”Œœ∑Ω· ¯£° ", 210, 300);
		g.setFont(f);
		g.setColor(c);
		
	}
	public Rectangle getRect(){
		//System.out.println(new Rectangle(h_location_x, h_location_y, h_width, h_height));
		return new Rectangle(h_location_x, h_location_y, h_width, h_height);
	}
	public boolean homeIsLive(){
		return islive;
	}
	public void set_home_alive(boolean islive){
		this.islive = islive;
	}
}
