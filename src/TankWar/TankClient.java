package TankWar;


import java.awt.Font;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;

import javax.swing.*;

public class TankClient extends JFrame implements ActionListener,KeyListener{
	private static final long serialVersionUID = 1L;
	
	public static int jf_location_x = 300;
	public static int jf_location_y = 50;
	public static int jf_height = 600;
	public static int jf_width = 800;
	public static int jf_op_width = 200;
	private int serverScore = 0;
	private int clientScore = 0;
	private int last_fire_time;
	private boolean gameStart = false;
	public ClientWar cw = new ClientWar(this);
	public JPanel jp_op = new JPanel();
	private JLabel jlHost = new JLabel("主机名");
	private JLabel jlPort = new JLabel("端口号");
	private JLabel jlName = new JLabel("昵称");
	private JLabel jlSeverName = new JLabel("player1");
	private JLabel jlClientName = new JLabel("player2");
	private JLabel jlTankImage1 = new JLabel(new ImageIcon(this.getClass().getResource("/image/tanklabel.gif")));
	private JLabel jlTankImage2 = new JLabel(new ImageIcon(this.getClass().getResource("/image/tanklabel.gif")));
	private JLabel jlServerScore = new JLabel("0");
	private JLabel jlClientScore = new JLabel("0");
	private JTextField jtHost = new JTextField("127.0.0.1"); 
	private JTextField jtPort = new JTextField("1234"); 
	private JTextField jtName = new JTextField("son"); 
	private JButton jbConnect = new JButton("连接");
	private JButton jbDisconnect = new JButton("断开");
	private JButton jbPause = new JButton("暂停");
	private JButton jbContinue = new JButton("继续");
	public JSplitPane jsp;
	public ClientThread ctd;
	public TankClient(){
		addComponent();
		ini_frame();
		addListener();
		cw.setFocusable(false);
		cw.requestFocus(true);//left panel get forcus
		last_fire_time = getTime();
	}
	private void addComponent(){
		
		jp_op.setLayout(null);
		
		jlHost.setBounds(20, 20, 40 , 25);
		jtHost.setBounds(65, 20, 100, 25);
		jlPort.setBounds(20, 50, 40 , 25);
		jtPort.setBounds(65, 50, 100, 25);		
		jlName.setBounds(20, 80, 40 , 25);
		jtName.setBounds(65, 80, 100, 25);
		jbConnect.setBounds(20, 115, 60, 30);
		jbDisconnect.setBounds(100, 115, 60, 30);
		jlSeverName.setBounds(20, 300, 150, 30);
		jlTankImage1.setBounds(20, 335, 37, 37);
		jlServerScore.setBounds(70, 339, 40, 30);
		jlClientName.setBounds(20, 380, 150, 30);
		jlTankImage2.setBounds(20, 415, 37, 37);
		jlClientScore.setBounds(70, 419, 40, 30);
		jbPause.setBounds(20, 470, 60, 30);
		jbContinue.setBounds(100, 470, 60, 30);
		
		jlSeverName.setFont(new Font("宋体", Font.PLAIN, 20));
		jlServerScore.setFont(new Font("宋体", Font.PLAIN, 20));
		jlClientName.setFont(new Font("宋体", Font.PLAIN, 20));
		jlClientScore.setFont(new Font("宋体", Font.PLAIN, 20));
		
		jp_op.add(jlHost);
		jp_op.add(jtHost);
		jp_op.add(jlPort);
		jp_op.add(jtPort);
		jp_op.add(jlName);
		jp_op.add(jtName);
		jp_op.add(jbConnect);
		jp_op.add(jbDisconnect);
		jp_op.add(jlSeverName);
		jp_op.add(jlTankImage1);
		jp_op.add(jlServerScore);
		jp_op.add(jlClientName);
		jp_op.add(jlTankImage2);
		jp_op.add(jlClientScore);
		//jp_op.add(jbPause);
		//jp_op.add(jbContinue);
		jsp=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,cw,jp_op);
		jsp.setDividerLocation(jf_width - jf_op_width);
		jsp.setDividerSize(4);
		this.add(jsp);
		
		set_state(true);
		
		jbPause.setFocusable(false);
		jbContinue.setFocusable(false);
		jbPause.setEnabled(false);
		jbContinue.setEnabled(false);
		jbConnect.setFocusable(false);
		jbDisconnect.setFocusable(false);
		cw.requestFocus(true);
		
	}
	public void set_state(boolean isFunc){
		jtHost.setFocusable(isFunc);
		jtPort.setFocusable(isFunc);
		jtName.setFocusable(isFunc);
		jbConnect.setEnabled(isFunc);
		jbDisconnect.setEnabled(!isFunc);
	}
	private void ini_frame(){
		this.setBounds(jf_location_x, jf_location_y, jf_width, jf_height);
		this.setTitle("TankWar");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	private void addListener(){
		//this.addKeyListener(cw.host_tank);
		this.addKeyListener(this);
		jbPause.addActionListener(this);
		jbContinue.addActionListener(this);
		jbConnect.addActionListener(this);
		jbDisconnect.addActionListener(this);
		this.addWindowListener(
				new WindowAdapter(){
					public void windowClosing(WindowEvent e){
						if(ctd == null || ctd.sk == null){//服务器线程为空，则直接退出
							System.exit(0);
							return;
						}
						ctd.send_msg("<#CLIENT_LEAVE#>");
						ctd.end_thread();
						System.exit(0);
					}
				}
		);
	}
	public static void main(String[] args){
		TankClient tc = new TankClient();
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == jbPause){
			cw.cpt.set_keep_paint(false);
			jbPause.setEnabled(false);
			jbContinue.setEnabled(true);
		}
		else if(e.getSource() == jbContinue){
			cw.cpt = new CPaintThread(this);
			cw.cpt.start();
			jbContinue.setEnabled(false);
			jbPause.setEnabled(true);
		}
		else if(e.getSource() == jbConnect){
			//System.out.println("我connect了");
			int port = 0;
			try{
				port = Integer.parseInt(jtPort.getText());
			} catch (Exception e1){
				JOptionPane.showMessageDialog(this,"端口号只能是整数","错误",JOptionPane.ERROR_MESSAGE);
				return;
			} 
			if(port > 65535 || port < 0){
				JOptionPane.showMessageDialog(this,"端口号只能在0~65535之间","错误",JOptionPane.ERROR_MESSAGE);
				return;
			}
			try{
				ctd = new ClientThread(this.jtHost.getText(), port, this);
				ctd.start();
				//JOptionPane.showMessageDialog(this,"连接成功","提示",JOptionPane.INFORMATION_MESSAGE);
			}catch (Exception e2){
				//JOptionPane.showMessageDialog(this,"连接服务器失败","错误",JOptionPane.ERROR_MESSAGE);
				return;
			}
			set_state(false);
		}
		else if(e.getSource() == jbDisconnect){
			System.out.println("我disconnect了");
			ctd.send_msg("<#CLIENT_LEAVE#>");
			this.handle_jbDisconnect();
			synchronized(ctd){
				ctd.notify();
			}
		}
			
		cw.requestFocus(true);
	}
	public void updateScore(int add_serverScore, int add_clientScore){
		this.serverScore += add_serverScore;
		this.clientScore += add_clientScore;
		jlServerScore.setText(Integer.toString(this.serverScore));
		jlClientScore.setText(Integer.toString(this.clientScore));
	}
	public void setGameStart(boolean gameStart){ this.gameStart = gameStart;}
	public boolean getGameStart(){return this.gameStart;}
	public void handle_jbDisconnect(){
		this.gameStart = false;
		this.set_state(true);
		jbPause.setEnabled(false);
		jbContinue.setEnabled(false);
		this.cw.ini_tank();
		//this.addKeyListener(cw.client_tank);
		if(ctd != null){
			ctd.end_thread();
		}
		System.out.println("我handle disconnect了");
	}
	public void set_jbPause(boolean state){
		jbPause.setEnabled(state);
	}
	public void set_jbContinue(boolean state){
		jbContinue.setEnabled(state);
	}
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		switch (e.getKeyCode()){
		case KeyEvent.VK_S:
			cw.client_tank.setDirection(Direction.D);
			break;
		case KeyEvent.VK_W:
			cw.client_tank.setDirection(Direction.U);
			break;
		case KeyEvent.VK_A:
			cw.client_tank.setDirection(Direction.L);
			break;
		case KeyEvent.VK_D:
			cw.client_tank.setDirection(Direction.R);
			break;
		}
	}
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		int key = e.getKeyCode();
		if( key == KeyEvent.VK_S ||
				key == KeyEvent.VK_W ||
				key == KeyEvent.VK_A ||
				key == KeyEvent.VK_D   )
			{
				if(cw.client_tank.get_direction(true) != Direction.STOP){
					cw.client_tank.setDirection(Direction.STOP);
				}			
			}
			if(key == KeyEvent.VK_F){
				int this_fire_time = getTime();
				if( this_fire_time > last_fire_time       || 
					(this_fire_time - last_fire_time) < 0   )
				{
					last_fire_time = this_fire_time;
					cw.client_tank.fire();
				}	
			}
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
}
