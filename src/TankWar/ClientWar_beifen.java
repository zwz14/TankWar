package TankWar;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JPanel;

public class ClientWar_beifen extends JPanel{
	private static final long serialVersionUID = 1L;
	
	public ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	public ArrayList<Tank> tanks = new ArrayList<Tank>();
	//public Home home = new Home(this);
	private Random r = new Random();
	TankClient tc;
	CPaintThread cpt;
	WarMap warmap = new WarMap();
	Tank host_tank;
	Tank client_tank;
	/*
	ClientWar(TankClient tc){
		this.tc = tc;
		ini_tank();
		cpt = new CPaintThread(tc); 
		cpt.start();
	}
	
	private void ini_tank(){
		host_tank = new Tank(200, 400, 0, this);
		client_tank = new Tank(400, 400, 1, this);
		for(int i = 0; i < 6; i++){
			Tank enemy_tank = new Tank(20 + 90 * i, 20, i + 2, this);
			tanks.add(enemy_tank);
		}
		
	}
	private void generate_new_tank(int id){
		System.out.println("我generate了");
		boolean generate_new_tank = false;
		while(!generate_new_tank){
			int r_location_x = r.nextInt(530) + 35;
			int r_location_y = r.nextInt(490) + 35;
			Tank new_tank =  new Tank(r_location_x, r_location_y, id, this);
			if( !new_tank.collideWithTank(host_tank) &&
				!new_tank.collideWithTank(tanks)     &&
				!new_tank.collideWithMap()             )
			{
				tanks.add(new_tank);
				generate_new_tank = true;
			}
		}
		System.out.println(tanks.size());
		System.out.println("我generate成功了");
		
	}
	public void paint(Graphics g){
		warmap.draw(g);
		if(home.homeIsLive()){
			home.draw(g);
		}
		else{
			home.gameover(g);
			cpt.set_keep_paint(false);
		}
		if(host_tank.get_live_state()){
			host_tank.collideWithMap();
			host_tank.collideWithTank(tanks);
			host_tank.collideWithTank(client_tank);
			host_tank.draw(g);
		}
		if(client_tank.get_live_state()){
			client_tank.collideWithMap();
			client_tank.collideWithTank(tanks);
			client_tank.collideWithTank(host_tank);
			client_tank.draw(g);
		}
		for(int i = 0; i < tanks.size(); i++){
			if(tanks.get(i).get_live_state()){
				tanks.get(i).collideWithTank(tanks, i);
				tanks.get(i).collideWithTank(host_tank);
				tanks.get(i).collideWithTank(client_tank);
				tanks.get(i).collideWithMap();
				tanks.get(i).draw(g);
			}	
			else{
				int die_tank_id = tanks.get(i).get_id();
				tanks.remove(i);
				generate_new_tank(die_tank_id);
			}
		}
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
			
			bullets.get(i).draw(g);
		}
	}
	*/
}
