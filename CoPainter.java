import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


	
public class CoPainter extends JFrame{
	private static final long serialVersionUID = 2126290136186166746L;

	public static void main(String[] args) {
			new CoPainter();
	}

	private static String preSetIP = "127.0.0.1";
	private static String preSetPort = "28080";
	
	private JPanel panel;
	private JLabel HostLb,PortLb;
	private JTextField HostTF,PortTF;
	private JButton HostBu,PortBu;
	private String IPAddress;
	private int port;
	private boolean hosting;
	private Connections connections;
	
	public CoPainter(){
		startingUI();
	}
	public void startingUI(){
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Collaborative Painter");
		setBounds(300, 300, 280, 135);
		HostLb = new JLabel("Host:");
		HostTF = new JTextField(18);
		HostTF.setText(preSetIP);
		PortLb = new JLabel("Port:");
		PortTF = new JTextField(18);
		PortTF.setText(preSetPort);
		HostBu = new JButton("Start as a host");
		PortBu = new JButton("Connect to a host");
		HostBu.addActionListener(new HostListener());
		PortBu.addActionListener(new PortListener());
		panel = new JPanel();
		panel.add(HostLb);
		panel.add(HostTF);
		panel.add(PortLb);
		panel.add(PortTF);
		panel.add(HostBu);
		panel.add(PortBu);
		getContentPane().add(panel);
		setVisible(true);		
	}
	public class HostListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			hosting = true;
			getDataPainter();
		}
	}
	public class PortListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			hosting = false;
			getDataPainter();
		}
	}
	public void getDataPainter(){
		IPAddress = HostTF.getText();
		port = Integer.parseInt(PortTF.getText());
		dispose();
		connections = new Connections(hosting,IPAddress,port);
		if (connections.confirmed()){
			new Painter(connections);
		} else {
			startingUI();
		}
	}
	
	public boolean hosting(){
		return hosting;
	}
	public String IPAddress(){
		return IPAddress;
	}
	public int port(){
		return port;
	}		
}