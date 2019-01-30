package TankWar;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Tank implements KeyListener{
	private int map_width = 600;
	private int map_height = 560; // the map where tank is
	private static int t_width = 35;
	private static int t_height = 35;
	private static int speed_x = 7;
	private static int speed_y = 7;
	private static int tank_max_blood = 3;
	private int t_location_x;
	private int t_location_y;
	private int oldt_location_x;
	private int oldt_location_y;
	private int id; // 0 host_tank 1 client_tank 3 enemy_tank
	private int blood = tank_max_blood; // blood of tank
	private int last_fire_time;
	private boolean islive = true;
	private static Random r = new Random();//generate random number
	private int step = r.nextInt(10)+5 ; // generate random enemy tank's step
	private Direction direction = Direction.STOP;  //set initial direction: STOP
	private Direction old_direction = Direction.U; //set initial old_direction: up
	private static Toolkit tk = Toolkit.getDefaultToolkit();// 控制面板
	private static Image[] tankImags = null;
	private ServerWar sw = null;
	private ClientWar cw = null;
	static {
		tankImags = new Image[] {
				tk.getImage(TankClient.class.getResource("/image/tankD.gif")),
				tk.getImage(TankClient.class.getResource("/image/tankU.gif")),
				tk.getImage(TankClient.class.getResource("/image/tankL.gif")),
				tk.getImage(TankClient.class.getResource("/image/tankR.gif")), };
	}
	Tank(int x, int y, int id, ClientWar cw){
		this.t_location_x = x;
		this.t_location_y = y;
		this.oldt_location_x = t_location_x;
		this.oldt_location_y = t_location_y;
		this.id = id;
		this.cw = cw;
		this.blood = tank_max_blood;
		last_fire_time = getTime();
	}
	Tank(int x, int y, int id, ServerWar sw){
		this.t_location_x = x;
		this.t_location_y = y;
		this.oldt_location_x = t_location_x;
		this.oldt_location_y = t_location_y;
		this.id = id;
		this.sw = sw;
		last_fire_time = getTime();
	}
	public void draw(Graphics g){
		Direction draw_direction = direction;
		if(direction == Direction.STOP){
			draw_direction = old_direction;
		}
		switch (draw_direction){
		case D:
			g.drawImage(tankImags[0], t_location_x, t_location_y, null);
			break;
		case U:
			g.drawImage(tankImags[1], t_location_x, t_location_y, null);
			//System.out.println("tank U draw ");
			break;
		case L:
			g.drawImage(tankImags[2], t_location_x, t_location_y, null);
			break;
		case R:
			g.drawImage(tankImags[3], t_location_x, t_location_y, null);
			break;
		default:
			break;
		}
		move();
	}
	public void draw(Graphics g, boolean tank_not_move){
		Direction draw_direction = direction;
		if(direction == Direction.STOP){
			draw_direction = old_direction;
		}
		switch (draw_direction){
		case D:
			g.drawImage(tankImags[0], t_location_x, t_location_y, null);
			break;
		case U:
			g.drawImage(tankImags[1], t_location_x, t_location_y, null);
			//System.out.println("tank U draw ");
			break;
		case L:
			g.drawImage(tankImags[2], t_location_x, t_location_y, null);
			break;
		case R:
			g.drawImage(tankImags[3], t_location_x, t_location_y, null);
			break;
		default:
			break;
		}
	}
	public void drawTank(Graphics g){
		g.drawImage(tankImags[1], t_location_x, t_location_y, null);
	}
	public void move(){
		oldt_location_x = t_location_x;
		oldt_location_y = t_location_y;
		switch (direction){
		case D:
			t_location_y += speed_y;
			break;
		case U:
			t_location_y -= speed_y;
			break;
		case L:
			t_location_x -= speed_x;
			break;
		case R:
			t_location_x += speed_x;
			break;
		case STOP:
			//System.out.println("x:"+t_location_x);
			//System.out.println("y:"+t_location_y);
			break;
		}
		if(id >= 2){
			Direction[] directions = Direction.values();
			if(step == 0){
				step = r.nextInt(10) + 5;
				int random_direction = r.nextInt(directions.length);
				if(direction == Direction.STOP && directions[random_direction] == Direction.STOP){
					setDirection(Direction.U);
				}
				else{
					setDirection(directions[random_direction]);
				}		
				//System.out.println("d:"+directions[random_direction]);
				//System.out.println("#ENEMY_TANK#");
				//this.show_direction();
			}
			step --;
			boolean fire = r.nextInt(50) > 48;
			if(fire){
				this.fire();
				//System.out.println("我fire了");
			}
			//System.out.println("step:"+step);
			//System.out.println("x:"+t_location_x);
			//System.out.println("y:"+t_location_y);
		}
	}
	public Bullet fire(){
		Bullet bullet = null;
		Direction b_direction;
		if(direction == Direction.STOP){ b_direction = old_direction;}
		else{ b_direction = direction;}
		int bullet_x = t_location_x + Tank.t_width / 2 - Bullet.b_width / 2;
		int bullet_y = t_location_y + Tank.t_height/2 - Bullet.b_height / 2;
		if(cw != null){
			bullet = new Bullet(bullet_x, bullet_y + 2, b_direction, id, cw);
			cw.tc.ctd.send_msg(
					"<#BULLET#>"   + 
					b_direction    + '/' + 
					bullet_x       + '/' +
					(bullet_y + 2) + '/' +
					id                    );
			cw.bullets.add(bullet);
		}
		if(sw != null){
			bullet = new Bullet(bullet_x, bullet_y + 2, b_direction, id, sw);
			sw.ts.std.send_msg(
					"<#BULLET#>"   + 
					b_direction    + '/' + 
					bullet_x       + '/' +
					(bullet_y + 2) + '/' +
					id                    );
			sw.bullets.add(bullet);
		}
		return bullet;
	}
	public boolean collideWithMap(){
		if ( t_location_x < 0                      ||
			 t_location_x > (map_width - t_width)  ||
			 t_location_y < 0                      ||
			 t_location_y > (map_height - t_height)  )
		{
			changToOldPos();
			return true;
		}
		return false;
	}
	public boolean collideWithHome(Home home){
		if (this.getRect().intersects(home.getRect()))
			{
				changToOldPos();
				return true;
			}
			return false;
	}
	public boolean collideWithTank(ArrayList<Tank> tanks){
		for(int i = 0; i < tanks.size(); i++){
			if(this.getRect().intersects(tanks.get(i).getRect())){
				changToOldPos();
				return true;
			}
		}
		return false;
	}
	public boolean collideWithTank(ArrayList<Tank> tanks, int j){
		for(int i = 0; i < tanks.size(); i++){
			if(this.getRect().intersects(tanks.get(i).getRect()) && i != j){
				changToOldPos();
				return true;
			}
		}
		return false;
	}
	public boolean collideWithTank(Tank tank){
		if(this.getRect().intersects(tank.getRect())){
			changToOldPos();
			return true;
		}
		return false;
	}
	@Override
	public void keyPressed(KeyEvent e) {
		//System.out.println("我 press了");
		if (id == 0){
			switch (e.getKeyCode()){
			case KeyEvent.VK_DOWN:
				setDirection(Direction.D);
				break;
			case KeyEvent.VK_UP:
				setDirection(Direction.U);
				break;
			case KeyEvent.VK_LEFT:
				setDirection(Direction.L);
				break;
			case KeyEvent.VK_RIGHT:
				setDirection(Direction.R);
				break;
			}
		} 
		else if (id == 1){
			switch (e.getKeyCode()){
			case KeyEvent.VK_S:
				setDirection(Direction.D);
				break;
			case KeyEvent.VK_W:
				setDirection(Direction.U);
				break;
			case KeyEvent.VK_A:
				setDirection(Direction.L);
				break;
			case KeyEvent.VK_D:
				setDirection(Direction.R);
				break;
			}
		}
		//System.out.println("#KEY_PRESS#");
		//this.show_direction();
    }
	@Override
	public void keyReleased(KeyEvent e){
		//System.out.println("我 release了");
		int key = e.getKeyCode();
		if (id == 0){
			if( key == KeyEvent.VK_DOWN ||
				key == KeyEvent.VK_UP   ||
				key == KeyEvent.VK_LEFT ||
				key == KeyEvent.VK_RIGHT  )
			{
				if(this.direction != Direction.STOP){
					setDirection(Direction.STOP);
				}
			}
			if(key == KeyEvent.VK_SPACE){
				int this_fire_time = getTime();
				if( this_fire_time > last_fire_time       || 
					(this_fire_time - last_fire_time) < 0   )
				{
					last_fire_time = this_fire_time;
					fire();
				}	
			}
		}
		else if (id == 1){
			if( key == KeyEvent.VK_S ||
				key == KeyEvent.VK_W ||
				key == KeyEvent.VK_A ||
				key == KeyEvent.VK_D   )
			{
				if(this.direction != Direction.STOP){
					setDirection(Direction.STOP);
				}			
			}
			if(key == KeyEvent.VK_F){
				int this_fire_time = getTime();
				if( this_fire_time > last_fire_time       || 
					(this_fire_time - last_fire_time) < 0   )
				{
					last_fire_time = this_fire_time;
					fire();
				}	
			}
		}
		//System.out.println("#KEY_REALSE#");
		//this.show_direction();
	}
	@Override
	public void keyTyped(KeyEvent e) {}
	
	public int getTime(){
		Calendar c = Calendar.getInstance();
		int minute = c.get(Calendar.MINUTE);
		int second = c.get(Calendar.SECOND);
		int last_fire_time = minute * 60 + second;
		return last_fire_time;
	}
	public Rectangle getRect(){
		return new Rectangle(t_location_x, t_location_y, t_width, t_height);
	}
	public void set_live_state(boolean islive){ 
		this.islive = islive;
		if(islive){ this.blood = tank_max_blood;}
		else { this.blood = 0;}
	}
	public void set_x_location(int x){ this.t_location_x = x;}
	public void set_y_location(int y){ this.t_location_y = y;}
	public void setDirection(Direction newDeriction){
		old_direction = direction;
		direction = newDeriction;
	}
	public boolean get_live_state(){ return this.islive;}
	public int get_x_location(){return this.t_location_x;}
	public int get_y_location(){return this.t_location_y;}
	public Direction get_direction(){ // 返回值一定不为STOP
		if(direction == Direction.STOP){return this.old_direction;}
		return this.direction;
	}
	public Direction get_direction(boolean show_actual_direction){ return this.direction;}//返回实际的方向
	public void substract_blood(int sub_blood){
		blood -= sub_blood;
		if(blood <= 0){
			set_live_state(false);
		}
	}
	public int get_blood(){ return this.blood;}
	public int get_id(){ return id;}
	public void changToOldPos(){
		t_location_x = oldt_location_x;
		t_location_y = oldt_location_y;
	}
	/*
	private void show_direction(){
		System.out.println("#The direction is"+direction+"#");
		System.out.println("#The old_direction is"+old_direction+"#");
	}
	*/
}
