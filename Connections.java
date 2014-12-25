import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class Connections{

	private boolean connected = false;
	private boolean hosting;
	private Socket socket;
	private ServerSocket serverSocket;
	private ArrayList<Socket> socketList;
	private Painter painter = null;
	private String exceptionMessage;
	
	public Connections(boolean hosting, String ipAddress, int port) {
		this.hosting = hosting;
		Thread t;
		try{
			if (hosting){
				serverSocket = new ServerSocket(port);
				connected = true;
				t = new Thread(new Server(serverSocket));
				t.start();
			} else {
				socket = new Socket(ipAddress, port);
				connected = true;
				new Client(socket).start();
			}
		} catch (IOException e){
			connected = false;
			e.printStackTrace();
			if (hosting)
				exceptionMessage = "Unable to listen to port "+port+"!";
			else
				exceptionMessage = "Unable to connect to host!";
			JOptionPane.showMessageDialog(null,
				exceptionMessage,
				"Fail to start",
				JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public class Server implements Runnable{
		private ServerSocket serverSocket;
		public Server(ServerSocket serverSocket){
			this.serverSocket = serverSocket;
		}
		public void run() {
			try {
				socketList = new ArrayList<Socket>();
				while (true){
					socket = serverSocket.accept();
					socketList.add(socket);
					Thread t = new Thread(new ClientHandler(socket));
					t.start();
				}	
			} catch (IOException e) {
			}
		}
	}
	public class ClientHandler implements Runnable{
		private Socket socket;
		private ObjectInputStream ois;
		public ClientHandler(Socket socket) {
			this.socket = socket;
		}
		public void run(){
			Line x;
			try {
				while (true){
					ois = new ObjectInputStream(socket.getInputStream());
					x = (Line) ois.readObject();
					if (x instanceof Line){
						painter.panel().addLine((Line) x);
						sendLine((Line) x);
						painter.panel().repaint();
					}
				}	
			} catch (IOException e) {
				socketList.remove(socket);
				e.printStackTrace();
			} catch (ClassNotFoundException e){
				socketList.remove(socket);
				e.printStackTrace();
			}
		}
	}

	public class Client extends Thread{
		private Socket socket;
		public Client(Socket socket) {
			this.socket = socket;
		}
		public void run() {
			ObjectInputStream ois;
			Line x;
			try{
				while (true){
					ois = new ObjectInputStream(socket.getInputStream());
					 x = (Line) ois.readObject();
					painter.panel().addLine((Line) x);
					painter.panel().repaint();
				}
			} catch (IOException e){
				connected = false;
				e.printStackTrace();
				exceptionMessage = "Unable to connect to host!";
				JOptionPane.showMessageDialog(null,
						exceptionMessage,
						"Error",
						JOptionPane.ERROR_MESSAGE);
					if (painter != null)
						painter.dispose();		
			} catch (ClassNotFoundException e){
				connected = false;
				e.printStackTrace();
				exceptionMessage = "Stream corrupted error!";
				JOptionPane.showMessageDialog(null,
						exceptionMessage,
						"Error",
						JOptionPane.ERROR_MESSAGE);
					if (painter != null)
						painter.dispose();		
			}
		}
	}
	
	public boolean confirmed() {
		return connected;
	}

	public void sendLine(Line line) {
		ObjectOutputStream oos;
		try {
			if (hosting) {
				for (Socket i: socketList){
					oos = new ObjectOutputStream(i.getOutputStream());
					oos.writeObject(line);
					oos.flush();
				}
			}
			else {
				oos = new ObjectOutputStream(socket.getOutputStream());
				oos.writeObject(line);
				oos.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public boolean hosting() {
		return hosting;
	}

	public void setPainter(Painter painter) {
		this.painter = painter;
	}


}
