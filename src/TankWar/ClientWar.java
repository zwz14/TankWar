package TankWar;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JPanel;

public class ClientWar extends JPanel{
	private static final long serialVersionUID = 1L;
	
	public ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	public ArrayList<Tank> tanks = new ArrayList<Tank>();
	public Home home = new Home(this);
	private Random r = new Random();
	TankClient tc;
	CPaintThread cpt;
	WarMap warmap = new WarMap();
	Tank host_tank;
	Tank client_tank;
	ClientWar(TankClient tc){
		this.tc = tc;
		ini_tank();
		cpt = new CPaintThread(tc); 
		cpt.start();
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
	public void paint(Graphics g){
		if(tc.getGameStart()){
			warmap.draw(g);
			this.tc.ctd.send_msg(
					"<#CLIENT#>" + 
					this.client_tank.get_direction()  + '/' +
					this.client_tank.get_x_location() + '/' +
					this.client_tank.get_y_location() + '/' + 
					this.client_tank.get_live_state()         );
			//打印家
			if(home.homeIsLive()){
				home.draw(g);
			}
			else{
				this.tc.ctd.send_msg("<#GAME_OVER#>");
				home.gameover(g);
				cpt.set_keep_paint(false);
			}
			//打印子弹
			for(int i = 0; i < bullets.size(); i++){
				bullets.get(i).inMap();
				if(bullets.get(i).getID() < 2){
					if(bullets.get(i).hit_tank(tanks)){
						tc.updateScore(1 - bullets.get(i).getID(), bullets.get(i).getID());
					}
				}
				else{
					bullets.get(i).hit_tank(host_tank);
					bullets.get(i).hit_tank(client_tank);
				}
				bullets.get(i).hit_home(home);
			}
			for(int i = 0; i < bullets.size(); i++){
				bullets.get(i).draw_static(g);
			}
			// 打印坦克
			if(host_tank.get_live_state()){
				host_tank.draw(g, true);
			}
			if(client_tank.get_live_state()){
				client_tank.collideWithMap();
				client_tank.collideWithTank(tanks);
				client_tank.collideWithTank(host_tank);
				client_tank.collideWithHome(home);
				client_tank.draw(g);
			}
			for(int i = 0; i < tanks.size(); i++){
				if(tanks.get(i).get_live_state()){
					tanks.get(i).draw(g, true);
				}	
				else{
					//System.out.println("我send enemy tank i了");
					this.tc.ctd.send_msg("<#HIT_ENEMYTANK#>" + i);
					System.out.println("我send enemy tank i了");
				}
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
	public void update_serverTank(Direction direction, int x, int y, boolean islive){
		host_tank.setDirection(direction);
		host_tank.set_x_location(x);
		host_tank.set_y_location(y);
		host_tank.set_live_state(islive);
	}
	public void update_enemyTank(Direction direction, int x, int y, boolean state,int tank_i){
		tanks.get(tank_i).setDirection(direction);
		tanks.get(tank_i).set_x_location(x);
		tanks.get(tank_i).set_y_location(y);
		tanks.get(tank_i).set_live_state(state);
	}
	public void update_bullet(Direction direction, int x, int y, int bullet_i){
		bullets.get(bullet_i).set_direction(direction);
		bullets.get(bullet_i).set_location_x(x);
		bullets.get(bullet_i).set_location_y(y);
	}
}
