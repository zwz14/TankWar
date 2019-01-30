package TankWar;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JPanel;

public class ServerWar extends JPanel{
private static final long serialVersionUID = 1L;
	
	public ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	public ArrayList<Tank> tanks = new ArrayList<Tank>();
	public Home home = new Home(this);
	private Random r = new Random();
	TankServer ts;
	PaintThread pt;
	WarMap warmap = new WarMap();
	Tank host_tank;
	Tank client_tank;
	ServerWar(TankServer ts){
		this.ts = ts;
		ini_tank();
		pt = new PaintThread(ts); 
		pt.start();
	}
	public void ini_tank(){
		if(bullets.size() != 0){
			bullets.clear();
		}
		if(tanks.size() != 0){
			tanks.clear();
		}
		host_tank = new Tank(200, 400, 0, this);
		client_tank = new Tank(400, 400, 1, this);
		for(int i = 0; i < 6; i++){
			Tank enemy_tank = new Tank(20 + 90 * i, 20, i + 2, this);
			tanks.add(enemy_tank);
		}
		
	}
	public void update_enemyTank(int i){
		boolean generate_new_tank = false;
		while(!generate_new_tank){
			int r_location_x = r.nextInt(530) + 35;
			int r_location_y = r.nextInt(200) + 35;
			tanks.get(i).setDirection(Direction.U);
			tanks.get(i).set_x_location(r_location_x);
			tanks.get(i).set_y_location(r_location_y);
			if( !tanks.get(i).collideWithTank(host_tank) &&
				!tanks.get(i).collideWithTank(tanks,i)     &&
				!tanks.get(i).collideWithMap()             )
			{
				tanks.get(i).set_live_state(true);
				generate_new_tank = true;
			}
		}
		System.out.println("ÎÒupdate enemy tankÁË");
	}
	public void paint(Graphics g){
		if(ts.getGameStart()){
			warmap.draw(g);
			this.ts.std.send_msg(
					"<#HOST#>" + 
					this.host_tank.get_direction()  + '/' +
					this.host_tank.get_x_location() + '/' +
					this.host_tank.get_y_location() + '/' + 
					this.host_tank.get_live_state()         );
			if(home.homeIsLive()){
				home.draw(g);
			}
			else{
				this.ts.std.send_msg("<#GAME_OVER#>");
				home.gameover(g);
				pt.set_keep_paint(false);
			}
			if(host_tank.get_live_state()){
				host_tank.collideWithMap();
				host_tank.collideWithTank(tanks);
				host_tank.collideWithTank(client_tank);
				host_tank.collideWithHome(home);
				host_tank.draw(g);
			}
			if(client_tank.get_live_state()){
				client_tank.draw(g,true);
			}
			for(int i = 0; i < tanks.size(); i++){
				if(tanks.get(i).get_live_state()){
					tanks.get(i).collideWithTank(tanks, i);
					tanks.get(i).collideWithTank(host_tank);
					tanks.get(i).collideWithTank(client_tank);
					tanks.get(i).collideWithMap();
					tanks.get(i).collideWithHome(home);
					tanks.get(i).draw(g);
					this.ts.std.send_msg(
							"<#UPDATA_ENEMYTANK#>" + 
							this.tanks.get(i).get_direction()  + '/' +
							this.tanks.get(i).get_x_location() + '/' +
							this.tanks.get(i).get_y_location() + '/' + 
							this.tanks.get(i).get_live_state() + '/' + i);
				}	
				else{
					/*
					int die_tank_id = tanks.get(i).get_id();
					tanks.remove(i);
					generate_new_tank(die_tank_id);
					*/
					update_enemyTank(i);
				}
			}
			for(int i = 0; i < bullets.size(); i++){
				this.ts.std.send_msg(
					"<#UPDATE_BULLET#>" + 
					bullets.get(i).get_direction()  + '/' + 
					bullets.get(i).get_location_x() + '/' + 
					bullets.get(i).get_location_y() + '/' + i );
			}
			for(int i = 0; i < bullets.size(); i++){
				bullets.get(i).inMap();
				if(bullets.get(i).getID() < 2){
					if(bullets.get(i).hit_tank(tanks)){
						ts.updateScore(1 - bullets.get(i).getID(), bullets.get(i).getID());
					}
				}
				else{
					bullets.get(i).hit_tank(host_tank);
					bullets.get(i).hit_tank(client_tank);
				}
				bullets.get(i).hit_home(home);
			}
			for(int i = 0; i < bullets.size(); i++){
				bullets.get(i).draw(g);
			}
		}
		else{
			warmap.draw(g);
			home.draw(g);
			host_tank.drawTank(g);
			client_tank.drawTank(g);
			for(int i = 0; i < tanks.size(); i++){
				tanks.get(i).drawTank(g);
			}
		}
		
	}
	public void update_clientTank(Direction direction, int x, int y, boolean islive){
		client_tank.setDirection(direction);
		client_tank.set_x_location(x);
		client_tank.set_y_location(y);
		client_tank.set_live_state(islive);
	}
}
