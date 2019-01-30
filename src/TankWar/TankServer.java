package TankWar;

import java.awt.Font;
import java.awt.event.*;
import java.util.Calendar;

import javax.swing.*;

public class TankServer extends JFrame implements ActionListener, KeyListener{
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
	public ServerWar sw = new ServerWar(this);
	public JPanel jp_op = new JPanel();
	private JLabel jlPort = new JLabel("端口号");
	private JLabel jlName = new JLabel("昵称");
	private JLabel jlSeverName = new JLabel("player1");
	private JLabel jlClientName = new JLabel("player2");
	private JLabel jlTankImage1 = new JLabel(new ImageIcon(this.getClass().getResource("/image/tanklabel.gif")));
	private JLabel jlTankImage2 = new JLabel(new ImageIcon(this.getClass().getResource("/image/tanklabel.gif")));
	private JLabel jlServerScore = new JLabel("0");
	private JLabel jlClientScore = new JLabel("0"); 
	private JTextField jtPort = new JTextField("1234"); 
	private JTextField jtName = new JTextField("son"); 
	private JButton jbcreate = new JButton("建主");
	private JButton jbstop = new JButton("停止");
	private JButton jbbegin = new JButton("开始");
	private JButton jbPause = new JButton("暂停");
	private JButton jbContinue = new JButton("继续");
	public JSplitPane jsp;
	public ServerThread std;
	public TankServer(){
		addComponent();
		ini_frame();
		addListener();
		sw.setFocusable(false);
		sw.requestFocus(true);//left panel get forcus
		last_fire_time = getTime();
	}
	private void addComponent(){
		
		jp_op.setLayout(null);
		
		jlPort.setBounds(20, 20, 40 , 25);
		jtPort.setBounds(65, 20, 100, 25);		
		jlName.setBounds(20, 50, 40 , 25);
		jtName.setBounds(65, 50, 100, 25);
		jbcreate.setBounds(20, 85, 60, 30);
		jbstop.setBounds(100, 85, 60, 30);
		jbbegin.setBounds(20, 125, 140, 30);
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
		
		jp_op.add(jlPort);
		jp_op.add(jtPort);
		jp_op.add(jlName);
		jp_op.add(jtName);
		jp_op.add(jbcreate);
		jp_op.add(jbstop);
		jp_op.add(jbbegin);
		jp_op.add(jlSeverName);
		jp_op.add(jlTankImage1);
		jp_op.add(jlServerScore);
		jp_op.add(jlClientName);
		jp_op.add(jlTankImage2);
		jp_op.add(jlClientScore);
		jp_op.add(jbPause);
		jp_op.add(jbContinue);
		jsp=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,sw,jp_op);
		jsp.setDividerLocation(jf_width - jf_op_width);
		jsp.setDividerSize(4);
		this.add(jsp);
		
		set_state(false);
		//jtPort.setFocusable(false);
		//jtName.setFocusable(false);
		jbPause.setFocusable(false);
		jbContinue.setFocusable(false);
		jbPause.setEnabled(false);
		jbContinue.setEnabled(false);
		jbcreate.setFocusable(false);
		jbstop.setFocusable(false);
		jbbegin.setFocusable(false);
		jbbegin.setEnabled(false);
		sw.requestFocus(true);
		
	}
	public void set_state(boolean state){
		jtPort.setFocusable(!state);
		jtName.setFocusable(!state);
		jbcreate.setEnabled(!state);
		jbstop.setEnabled(state);
		
		//jbbegin.setEnabled(state);
	}
	private void ini_frame(){
		this.setBounds(jf_location_x, jf_location_y, jf_width, jf_height);
		this.setTitle("TankWar");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	private void addListener(){
		this.addKeyListener(this);
		//this.addKeyListener(sw.client_tank);
		jbPause.addActionListener(this);
		jbContinue.addActionListener(this);
		jbcreate.addActionListener(this);
		jbbegin.addActionListener(this);
		jbstop.addActionListener(this);
		this.addWindowListener(
				new WindowAdapter(){
					public void windowClosing(WindowEvent e){
						if(std == null || std.sk == null){//服务器线程为空，则直接退出
							System.exit(0);
							return;
						}
						std.send_msg("<#SERVER_LEAVE#>");
						std.end_thread();
						System.exit(0);
					}
				}
		);
	}
	public static void main(String[] args){
		TankServer ts = new TankServer();
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == jbPause){
			sw.pt.set_keep_paint(false);
			jbPause.setEnabled(false);
			jbContinue.setEnabled(true);
		}
		else if(e.getSource() == jbContinue){
			sw.pt = new PaintThread(this);
			sw.pt.start();
			jbContinue.setEnabled(false);
			jbPause.setEnabled(true);
		}
		else if(e.getSource() == jbcreate){
			System.out.println("我create了");
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
				std = new ServerThread(port, this);
				std.start();
				JOptionPane.showMessageDialog(this,"建主成功","提示",JOptionPane.INFORMATION_MESSAGE);
			}catch (Exception e2){
				JOptionPane.showMessageDialog(this,"服务器启动失败","错误",JOptionPane.ERROR_MESSAGE);
				return;
			}
			this.set_state(true);
			//sw.requestFocus(true);
		}
		else if(e.getSource() == jbbegin){
			this.sw.home.set_home_alive(true);
			/*
			sw.pt = new PaintThread(this);
			sw.pt.start();
			*/
			if(!sw.pt.isAlive()){
				sw.pt = new PaintThread(this);
				sw.pt.start();
			}
			this.sw.ini_tank();
			this.gameStart = true;
			jbbegin.setEnabled(false);
			jbPause.setEnabled(true);
			this.std.send_msg("<#SERVER_BEGIN#>");
			sw.requestFocus(true);
		}
		else if(e.getSource() == jbstop){
			if(std.sk != null){
				std.send_msg("<#SERVER_LEAVE#>");
			}
			handle_jbstop();
		}
		sw.requestFocus(true);
	}
	public void updateScore(int add_serverScore, int add_clientScore){
		this.serverScore += add_serverScore;
		this.clientScore += add_clientScore;
		jlServerScore.setText(Integer.toString(this.serverScore));
		jlClientScore.setText(Integer.toString(this.clientScore));
	}
	public boolean getGameStart(){return this.gameStart;}
	public void set_jbbegin(boolean state){
		this.jbbegin.setEnabled(state);
	}
	public void handle_jbstop(){
		System.out.println("我开始handle_jbstop了");
		this.gameStart = false;
		this.set_state(false);
		jbPause.setEnabled(false);
		jbContinue.setEnabled(false);
		jbbegin.setEnabled(false);
		this.sw.ini_tank();
		//System.out.println(this.sw.client_tank.get_x_location() + "||" + this.sw.client_tank.get_y_location());
		if(std != null){
			std.end_thread();
		}
		System.out.println("我已经handle_jbstop了");
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
		case KeyEvent.VK_DOWN:
			sw.host_tank.setDirection(Direction.D);
			break;
		case KeyEvent.VK_UP:
			sw.host_tank.setDirection(Direction.U);
			break;
		case KeyEvent.VK_LEFT:
			sw.host_tank.setDirection(Direction.L);
			break;
		case KeyEvent.VK_RIGHT:
			sw.host_tank.setDirection(Direction.R);
			break;
		}
	}
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		int key = e.getKeyCode();
		if( key == KeyEvent.VK_DOWN ||
				key == KeyEvent.VK_UP   ||
				key == KeyEvent.VK_LEFT ||
				key == KeyEvent.VK_RIGHT  )
			{
				if(sw.host_tank.get_direction(true) != Direction.STOP){
					sw.host_tank.setDirection(Direction.STOP);
				}
			}
			if(key == KeyEvent.VK_SPACE){
				int this_fire_time = getTime();
				if( this_fire_time > last_fire_time       || 
					(this_fire_time - last_fire_time) < 0   )
				{
					last_fire_time = this_fire_time;
					sw.host_tank.fire();
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