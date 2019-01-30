package TankWar;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.List;

public class Bullet {
	private int map_width = 600;
	private int map_height = 560; // the map where tank is
	public static int b_width = 10;
	public static int b_height = 10;
	private static int speed_x = 12;
	private static int speed_y = 12;
	private int b_location_x;
	private int b_location_y;
	private int id; // id of tank that fire the bullet
	private static int hit_commonTank_power = 3;
	private static int hit_playerTank_power = 1;
	private boolean islive = true;
	private static Toolkit tk = Toolkit.getDefaultToolkit();// 控制面板
	private static Image[] bulletImags;
	private Direction direction;
	private ServerWar sw = null;
	private ClientWar cw = null;
	static {
		bulletImags = new Image[] {
				tk.getImage(TankClient.class.getResource("/image/bulletD.gif")),
				tk.getImage(TankClient.class.getResource("/image/bulletU.gif")),
				tk.getImage(TankClient.class.getResource("/image/bulletL.gif")),
				tk.getImage(TankClient.class.getResource("/image/bulletR.gif")), };
	}
	Bullet(int b_location_x, int b_location_y, Direction direction, int id, ClientWar cw){
		this.b_location_x = b_location_x;
		this.b_location_y = b_location_y;
		this.direction = direction;
		this.id = id;
		this.cw = cw;
	}
	Bullet(int b_location_x, int b_location_y, Direction direction, int id, ServerWar sw){
		this.b_location_x = b_location_x;
		this.b_location_y = b_location_y;
		this.direction = direction;
		this.id = id;
		this.sw = sw;
	}
	public void draw(Graphics g){
		if(!islive){
			/*
			if(cw != null){
				int bullet_i = 0;
				for(int i = 0; i < cw.bullets.size(); i++){
					if(cw.bullets.get(i) == this){
						bullet_i = i;
						System.out.println("我find cw_bullet_i了");
						break;
					}
				}
				cw.tc.ctd.send_msg("<#KILL_BULLET#>" + bullet_i);
				cw.bullets.remove(this);
			}
			*/
			if(sw != null){
				int bullet_i = 0;
				for(int i = 0; i < sw.bullets.size(); i++){
					if(sw.bullets.get(i) == this){
						bullet_i = i;
						System.out.println("我find sw_bullet_i了");
						break;
					}
				}
				sw.ts.std.send_msg("<#KILL_BULLET#>" + bullet_i);
				sw.bullets.remove(this);
			}
			return;
		}
		switch (direction){
		case D:
			g.drawImage(bulletImags[0], b_location_x, b_location_y, null);
			break;
		case U:
			g.drawImage(bulletImags[1], b_location_x, b_location_y, null);
			//System.out.println("tank U draw ");
			break;
		case L:
			g.drawImage(bulletImags[2], b_location_x, b_location_y, null);
			break;
		case R:
			g.drawImage(bulletImags[3], b_location_x, b_location_y, null);
			break;
		default:
			break;
		}
		move();
	}
	public void draw_static(Graphics g){ // 画出静态bullet
		switch (direction){
		case D:
			g.drawImage(bulletImags[0], b_location_x, b_location_y, null);
			break;
		case U:
			g.drawImage(bulletImags[1], b_location_x, b_location_y, null);
			//System.out.println("tank U draw ");
			break;
		case L:
			g.drawImage(bulletImags[2], b_location_x, b_location_y, null);
			break;
		case R:
			g.drawImage(bulletImags[3], b_location_x, b_location_y, null);
			break;
		default:
			break;
		}
	}
	
	public void move(){
		switch (direction){
		case D:
			b_location_y += speed_y;
			break;
		case U:
			b_location_y -= speed_y;
			break;
		case L:
			b_location_x -= speed_x;
			break;
		case R:
			b_location_x += speed_x;
			break;
		default:
			break;
		}
	}
	public boolean inMap(){
		boolean inmap = true;
		if ( b_location_x < 0                      ||
			 b_location_x > (map_width - b_width)  ||
			 b_location_y < 0                      ||
			 b_location_y > (map_height - b_height)  )
		{
			set_live_state(false);
			inmap = false;
		}
		return inmap;
	}
	public boolean hit_tank(List<Tank> tanks){
		for(int i = 0; i < tanks.size(); i++){
			if(this.getRect().intersects(tanks.get(i).getRect())){
				set_live_state(false);
				tanks.get(i).substract_blood(hit_commonTank_power);
				System.out.println("我hit tank了" + "ID:"+ this.getID() + "TANK_BLOOD" + tanks.get(i).get_blood());		
				return true;
			}
		}
		return false;
	}
	public boolean hit_tank(Tank tank){
		if(this.getRect().intersects(tank.getRect())){
			set_live_state(false);
			tank.substract_blood(hit_playerTank_power);
			return true;
		}
		return false;
	}
	public boolean hit_home(Home home){
		if(this.getRect().intersects(home.getRect())){
			set_live_state(false);
			home.set_home_alive(false);
			return true;
		}
		return false;
	}
	public void set_live_state(boolean islive){ this.islive = islive;}
	public Rectangle getRect(){
		return new Rectangle(b_location_x, b_location_y, b_width, b_height);
	}
	public Direction get_direction(){ return this.direction;}
	public int get_location_x(){ return this.b_location_x;}
	public int get_location_y(){ return this.b_location_y;}
	public int getID(){return id;}
	public void set_direction(Direction direction){this.direction = direction;}
	public void set_location_x(int x){ this.b_location_x = x;}
	public void set_location_y(int y){ this.b_location_y = y;}
}
