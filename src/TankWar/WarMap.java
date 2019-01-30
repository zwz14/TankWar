package TankWar;

import java.awt.*;

public class WarMap {
	public static int map_width = 600;
	public static int map_height = 600;
	Image screenImage = null;
	public WarMap(){
		
	}
	public void draw(Graphics g){
		Color c = g.getColor();
		g.setColor(new Color(30,30,30));
		//System.out.print(gps.getColor());
		g.fillRect(0, 0, map_width, map_height);
		g.setColor(c);
		//System.out.print(gps.getColor());
		paint_map(g);
	}
	private void paint_map(Graphics g){
		
	}
}
