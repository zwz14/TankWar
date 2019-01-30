package TankWar;

import java.io.*;
import java.net.*;

import javax.swing.JOptionPane;

public class ServerThread extends Thread{
	int port;
	private TankServer ts;
	private ServerSocket ssk;
	public Socket sk;
	private boolean ThreadNotEnd = true;
	public BufferedReader InFromClient;
	public DataOutputStream  OutToClient;
	ServerThread(int port, TankServer ts){
		this.port = port;
		this.ts = ts;
	}
	public void run(){
		try {
			ssk = new ServerSocket(port);
			sk = ssk.accept();
			System.out.println("我accept了");
			InFromClient = new BufferedReader(new InputStreamReader(sk.getInputStream())); 
		    OutToClient =  new DataOutputStream(sk.getOutputStream());
		} catch (IOException e) {
			System.out.println("sk连接异常");
		}
		while(ThreadNotEnd){
			try {
				String msg = InFromClient.readLine();
				if(msg.startsWith("<#CLIENT_ENTER#>")){
					JOptionPane.showMessageDialog(ts,"加入游戏","提示",JOptionPane.INFORMATION_MESSAGE);
					ts.set_jbbegin(true);
				}
				else if(msg.startsWith("<#CLIENT_LEAVE#>")){
					ts.handle_jbstop();
					JOptionPane.showMessageDialog(ts,"客户端离线","提示",JOptionPane.INFORMATION_MESSAGE);
					//this.end_thread();
					
				}
				else if(msg.startsWith("<#CLIENT#>")){
					String info = msg.substring(10);
					String[] subinfo = info.split("/");
					Direction ct_direction = str_to_direct(subinfo[0]);
					int ct_location_x = Integer.parseInt(subinfo[1]);
					int ct_location_y = Integer.parseInt(subinfo[2]);
					boolean ct_islive = Boolean.parseBoolean(subinfo[3]);
					this.ts.sw.update_clientTank(ct_direction, ct_location_x, ct_location_y, ct_islive);
				}
				else if(msg.startsWith("<#HIT_ENEMYTANK#>")){
					int tank_i = Integer.parseInt(msg.substring(17));
					this.ts.sw.tanks.get(tank_i).set_live_state(false);
					this.ts.sw.update_enemyTank(tank_i);
					System.out.println("我receive hit_tank mesg了");
				}
				else if(msg.startsWith("<#GAME_OVER#>")){
					ts.sw.home.set_home_alive(false);
				}
				else if(msg.startsWith("<#BULLET#>")){
					String info = msg.substring(10);
					String[] subinfo = info.split("/");
					Direction b_direction = str_to_direct(subinfo[0]);
					int b_location_x = Integer.parseInt(subinfo[1]);
					int b_location_y = Integer.parseInt(subinfo[2]);
					int b_id = Integer.parseInt(subinfo[3]);
					Bullet bullet = new Bullet(b_location_x, b_location_y + 2, b_direction, b_id, this.ts.sw);
					this.ts.sw.bullets.add(bullet);
					System.out.println("我 add bullet了");
				}
				/*
				else if(msg.startsWith("<#KILL_BULLET#>")){
					int bullet_i = Integer.parseInt(msg.substring(15));
					this.ts.sw.bullets.remove(bullet_i);
					System.out.println("我 kill bullet了");
				}
				*/
			} catch (IOException e) {
				e.printStackTrace();
			} 
			
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {e.printStackTrace();}
		}
	}
	public void end_thread(){
		ThreadNotEnd = false;
		try {
			Thread.sleep(100);
			//InFromClient.close();
			ssk.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void send_msg(String msg){
		try {
			OutToClient.writeBytes(msg+'\n');
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static Direction str_to_direct(String str_direction){
		Direction direction  = Direction.U;
		switch(str_direction){
		case "D":
			direction = Direction.D;
			break;
		case "U":
			direction = Direction.U;
			break;
		case "L":
			direction = Direction.L;
			break;
		case "R":
			direction = Direction.R;
			break;
		}
		return direction;
	}
}
