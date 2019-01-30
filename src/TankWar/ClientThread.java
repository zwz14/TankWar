package TankWar;

import java.io.*;
import java.net.*;

import javax.swing.JOptionPane;

public class ClientThread extends Thread{
	String IP;
	int port;
	private TankClient tc;
	public Socket sk;
	private boolean ThreadNotEnd = true;
	public BufferedReader InFromServer;
	public DataOutputStream  OutToServer;
	ClientThread(String IP, int port, TankClient tc){
		this.IP = IP;
		this.port = port;
		this.tc = tc;
	}
	public void run(){
		try {
			sk = new Socket(IP, port);
			JOptionPane.showMessageDialog(tc,"连接成功","提示",JOptionPane.INFORMATION_MESSAGE);
			System.out.println("我connect了");
			InFromServer = new BufferedReader(new InputStreamReader(sk.getInputStream())); 
			OutToServer =  new DataOutputStream(sk.getOutputStream());
			//OutToServer.writeBytes("<#CLIENT_ENTER#>"+'\n');
			send_msg("<#CLIENT_ENTER#>"+'\n');
		} catch (IOException e) {
			JOptionPane.showMessageDialog(tc,"连接服务器失败","错误",JOptionPane.ERROR_MESSAGE);
			tc.handle_jbDisconnect();
			return;
		}
		while(ThreadNotEnd){
			try {
				String msg = InFromServer.readLine();
				if(msg.startsWith("<#SERVER_LEAVE#>")){
					tc.handle_jbDisconnect();
					JOptionPane.showMessageDialog(tc,"主机离线","提示",JOptionPane.INFORMATION_MESSAGE);
					//this.end_thread();
					
					synchronized(this){
						this.wait();
					}	
				}
				else if(msg.startsWith("<#SERVER_BEGIN#>")){
					this.tc.cw.home.set_home_alive(true);
					if(!tc.cw.cpt.isAlive()){
						tc.cw.cpt = new CPaintThread(tc);
						tc.cw.cpt.start();
					}
					this.tc.cw.ini_tank();
					tc.set_jbPause(true);
					tc.setGameStart(true);
				}
				else if(msg.startsWith("<#HOST#>")){
					String info = msg.substring(8);
					String[] subinfo = info.split("/");
					Direction ht_direction = ServerThread.str_to_direct(subinfo[0]);
					int ht_location_x = Integer.parseInt(subinfo[1]);
					int ht_location_y = Integer.parseInt(subinfo[2]);
					boolean ht_islive = Boolean.parseBoolean(subinfo[3]);
					this.tc.cw.update_serverTank(ht_direction, ht_location_x, ht_location_y, ht_islive);
				}
				else if(msg.startsWith("<#UPDATA_ENEMYTANK#>")){
					String info = msg.substring(20);
					String[] subinfo = info.split("/");
					Direction et_direction = ServerThread.str_to_direct(subinfo[0]);
					int et_location_x = Integer.parseInt(subinfo[1]);
					int et_location_y = Integer.parseInt(subinfo[2]);
					boolean et_state = Boolean.parseBoolean(subinfo[3]);
					int et_tank_i = Integer.parseInt(subinfo[4]);
					this.tc.cw.update_enemyTank(et_direction, et_location_x, et_location_y, et_state, et_tank_i);
				}
				else if(msg.startsWith("<#GAME_OVER#>")){
					tc.cw.home.set_home_alive(false);
				}
				else if(msg.startsWith("<#BULLET#>")){
					String info = msg.substring(10);
					String[] subinfo = info.split("/");
					Direction b_direction = ServerThread.str_to_direct(subinfo[0]);
					int b_location_x = Integer.parseInt(subinfo[1]);
					int b_location_y = Integer.parseInt(subinfo[2]);
					int b_id = Integer.parseInt(subinfo[3]);
					Bullet bullet = new Bullet(b_location_x, b_location_y + 2, b_direction, b_id, this.tc.cw);
					this.tc.cw.bullets.add(bullet);
				}
				else if(msg.startsWith("<#UPDATE_BULLET#>")){
					String info = msg.substring(17);
					String[] subinfo = info.split("/");
					Direction b_direction = ServerThread.str_to_direct(subinfo[0]);
					int b_location_x = Integer.parseInt(subinfo[1]);
					int b_location_y = Integer.parseInt(subinfo[2]);
					int bullet_i = Integer.parseInt(subinfo[3]);
					this.tc.cw.update_bullet(b_direction, b_location_x, b_location_y, bullet_i);
				}
				else if(msg.startsWith("<#KILL_BULLET#>")){
					int bullet_i = Integer.parseInt(msg.substring(15));
					this.tc.cw.bullets.remove(bullet_i);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {e.printStackTrace();}
		}
	}
	public void end_thread(){
		System.out.println("我end thread 中");
		ThreadNotEnd = false;
		try {
			//Thread.sleep(100);
			//InFromServer.close();
			System.out.println("InFromServer close 了");
			sk.close();
			System.out.println("sk close 了");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("我end thread了");
	}
	public void send_msg(String msg){
		try {
			OutToServer.writeBytes(msg+'\n');
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
