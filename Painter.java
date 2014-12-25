
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class Painter extends JFrame{
	private static final long serialVersionUID = 3814759435114048882L;
	private static MyPanel panel;
	private int currentSize = 5;
	private Color currentColor = Color.black;
	private Connections connections;
	private boolean notClosed = true;
	
	public Painter(Connections connections){
		this();
		this.connections = connections;
		connections.setPainter(this);
	}
	public Painter(){
		setTitle("Collaborative Painter");
		setBounds(200, 200, 698, 480);
		setLayout(null);
		setMenuBar();	
		setMyPanel();
		setLowerPanel();
		setVisible(true);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				notClosed = false;
				System.exit(0);
			}
		});
	}

	public void setMenuBar(){
		JMenuBar menuBar = new JMenuBar();
		JMenu action = new JMenu("Action");
		ArrayList<JMenuItem> actionItem = new ArrayList<JMenuItem>();
		actionItem.add(new JMenuItem("Clear"));
		actionItem.add(new JMenuItem("Save"));
		actionItem.add(new JMenuItem("Load"));
		actionItem.add(new JMenuItem("Exit"));
		for (JMenuItem i: actionItem){
			action.add(i);
			i.addActionListener(new MenuListener());
		}
		menuBar.add(action);
		setJMenuBar(menuBar);		
	}
	public class MenuListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if (cmd.equals("Clear")){
				clear();
				repaint();
			}
			if (cmd.equals("Save")){
				new Save();
			}
			if (cmd.equals("Load")){
				new Load();
			}
			if (cmd.equals("Exit")){
				notClosed = false;
				System.exit(0);
			}
		}
		public void clear(){
			panel.clearObjects();
		}
		public class Save extends JFrame implements ActionListener{
			private static final long serialVersionUID = 7131351837528790717L;
			private JFileChooser fileChooser;
			private File saveFile = new File("asd.ser");
			public Save(){
				setTitle("Save");
				setBounds(300, 300, 500, 320);
				fileChooser = new JFileChooser();
				fileChooser.setBounds(0, 0, 480, 280);
				fileChooser.setApproveButtonText("Save");
				fileChooser.setSelectedFile(saveFile);
				fileChooser.addActionListener(this);
				add(fileChooser);
				setLayout(null);
				setVisible(true);				
			}
			public void actionPerformed(ActionEvent e) {
				String cmd = e.getActionCommand();
				if (cmd.equals(JFileChooser.APPROVE_SELECTION)) {
		            saveFile = fileChooser.getSelectedFile();
		            if(!saveFile.toString().toLowerCase().endsWith(".ser")){
		            	saveFile = new File(saveFile.toString() + ".ser");
		            }
		            try {
		            	FileOutputStream fos = new FileOutputStream(saveFile);
						ObjectOutputStream oos = new ObjectOutputStream(fos);
			            oos.writeObject(panel.theLine());
			            oos.close();
			            fos.close();
					} catch (Exception e1){
					}
		            
		            dispose();
		        } else if (cmd.equals(JFileChooser.CANCEL_SELECTION)) {
		            dispose();
		        }
			}
		}
		public class Load extends JFrame implements ActionListener{
			private static final long serialVersionUID = -5892860932734684152L;
			private JFileChooser fileChooser;
			private File loadFile = new File("asd.ser");
			public Load(){
				setTitle("Load");
				setBounds(300, 300, 500, 320);
				fileChooser = new JFileChooser();
				fileChooser.setBounds(0, 0, 480, 280);
				fileChooser.setApproveButtonText("Load");
				fileChooser.setSelectedFile(loadFile);
				fileChooser.addActionListener(this);
				add(fileChooser);
				setLayout(null);
				setVisible(true);				
			}
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
				String cmd = e.getActionCommand();
				if (cmd.equals(JFileChooser.APPROVE_SELECTION)) {
		            try {
		            	loadFile = fileChooser.getSelectedFile();
			            FileInputStream fis = new FileInputStream(loadFile);
						ObjectInputStream ois = new ObjectInputStream(fis);
						Object x = ois.readObject();
						if (x instanceof ArrayList<?>)
							panel.setLine((ArrayList<Line>)x);
			            ois.close();
			            fis.close();
					} catch (FileNotFoundException ex){
						System.out.println(ex);
					} catch (ClassNotFoundException ex){
						System.out.println(ex);
					} catch (InvalidClassException ex){
						System.out.println(ex);
					} catch (StreamCorruptedException ex){
						System.out.println(ex);
					} catch (OptionalDataException ex){
						System.out.println(ex);
					} catch (IOException ex) {
						System.out.println(ex);
					}
		            panel.repaint();
		            dispose();
		        } else if (cmd.equals(JFileChooser.CANCEL_SELECTION)) {
		            dispose();
		        }
			}
		}
	}

	public void setMyPanel(){
		panel = new MyPanel();
		add(panel);
	}
	public class MyPanel extends JPanel implements MouseMotionListener,MouseListener{
		private static final long serialVersionUID = -1637115445239710906L;
		private ArrayList<Line> graphObjects;
		
		int[] lastCoor;
		
		public MyPanel(){
			setPanel();
			graphObjects = new ArrayList<Line>();
		}

		public void paintComponent(Graphics g){
			try{
				g.setColor( Color.white);
				g.fillRect( 0, 0, 680,370);
				Graphics2D g2D = (Graphics2D) g;
				for (Line i: graphObjects){
					g2D.setColor(i.color());
					BasicStroke s = new BasicStroke(i.size(),BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
					g2D.setStroke(s);
					g.drawLine(i.coor1()[0],i.coor1()[1],i.coor2()[0],i.coor2()[1]);
				}
			} catch (ConcurrentModificationException e){
			}
		}
		public void setPanel(){
			setBounds(0,0,680,370);
			setLayout(null);
			setVisible(true);
			addMouseListener(this);
			addMouseMotionListener(this);
		}
		public void clearObjects(){
			graphObjects.clear();
			repaint();
		}

		public ArrayList<Line> theLine(){
			return graphObjects;
		}
		public void setLine(ArrayList<Line> x){
			graphObjects = x;
		}
		public void addLine(Line x){
			graphObjects.add(x);
		}
		
		public void mouseClicked(MouseEvent e) {
			lastCoor = new int[2];
			lastCoor[0] = e.getX();
			lastCoor[1] = e.getY();
			Line x = new Line(lastCoor,lastCoor,currentSize,currentColor);
			graphObjects.add(x);
			connections.sendLine(x);
			repaint();
		}
		public void mousePressed(MouseEvent e) {
			lastCoor = new int[2];
			lastCoor[0] = e.getX();
			lastCoor[1] = e.getY();
			Line x = new Line(lastCoor,lastCoor,currentSize,currentColor);
			graphObjects.add(x);
			repaint();
		}
		public void mouseDragged(MouseEvent e) {
			int[] newCoor = {e.getX(),e.getY()};
			Line x = new Line(lastCoor,newCoor,currentSize,currentColor);
			graphObjects.add(x);
			connections.sendLine(x);	
			lastCoor = newCoor;
			repaint();
		}
		public void mouseReleased(MouseEvent e) {	
		}
		public void mouseEntered(MouseEvent e) {
		}
		public void mouseExited(MouseEvent e) {
		}
		public void mouseMoved(MouseEvent e) {
		}
}

	public void setLowerPanel(){
		LowerPanel lowerPanel = new LowerPanel();
		lowerPanel.setBounds(0,370,680,40);
		lowerPanel.setVisible(true);
		add(lowerPanel);
	}
	public class LowerPanel extends JPanel{
		private static final long serialVersionUID = -1061005642513451596L;
		private Color manualColor;
		private	ArrayList<Color> colorList;
		private int[] sizes;
		
		public LowerPanel(){
			colorList = new ArrayList<Color>();
			colorList.add(Color.gray);
			colorList.add(Color.black);
			colorList.add(Color.red);
			colorList.add(Color.green);
			colorList.add(Color.blue);
			colorList.add(Color.white);

			sizes = new int[5];
			sizes[0] = 5;
			sizes[1] = 10;
			sizes[2] = 15;
			sizes[3] = 20;
			sizes[4] = 25;
			
			setLayout(null);
			addColorPanel();
			addSizePanel();
		}
		
		public void addColorPanel(){
			
			int j = 0;
			JButton button = new JButton();
			button.setLayout(null);
			Dimension dim = new Dimension(18,35);
			button.setSize(dim);
			button.setBounds(0, 0, 20, 40);
			button.addActionListener(new ManualColorListener());
			add(button);

			dim = new Dimension(35,35);
			
			for (Color i: colorList){
				button = new ColorButton(i);
				button.setSize(dim);
				button.setBounds(20 + 40*j++, 0, 40, 40);
				button.addActionListener((ActionListener) button);
				add(button);
			}	
		}
		public class ColorButton extends JButton implements ActionListener{
			private static final long serialVersionUID = 5319980703983135885L;
			private Color color;
			public ColorButton(Color i){
				color = i;
			}
			public void paintComponent(Graphics g){
				g.setColor(color);
				g.fillRect( 0, 0, 40, 40);
			}
			public void actionPerformed(ActionEvent e) {
				currentColor =  color;
				panel.repaint();
			}
		}
		public class ManualColorListener implements ActionListener{
			public void actionPerformed(ActionEvent e) {
				new ManualColor();
			}
		}
		public class ManualColor extends JFrame{
			private static final long serialVersionUID = 5277584698534715087L;
			JSlider slider;
			public ManualColor(){
				setTitle("Color");
				manualColor = currentColor;
				setBounds(200, 680, 220, 140);
				slider= new JSlider(JSlider.HORIZONTAL,0,255,currentColor.getRed());
				slider.setBorder(BorderFactory.createTitledBorder("Red"));
				slider.setBounds(0,0, 200, 30);
				slider.addChangeListener(new ChangeListener(){
					public void stateChanged(ChangeEvent e) {
				        JSlider source = (JSlider)e.getSource();
				        if (!source.getValueIsAdjusting()) {
				            manualColor = new Color(source.getValue(),manualColor.getGreen(),manualColor.getBlue());
				            currentColor = manualColor;
				        }
					}
				});
				add(slider);
				slider = new JSlider(JSlider.HORIZONTAL,0,255,currentColor.getGreen());
				slider.setBorder(BorderFactory.createTitledBorder("Green"));
				slider.setBounds(0,30, 200, 30);
				slider.addChangeListener(new ChangeListener(){
					public void stateChanged(ChangeEvent e) {
				        JSlider source = (JSlider)e.getSource();
				        if (!source.getValueIsAdjusting()) {
				            manualColor = new Color(manualColor.getRed(),source.getValue(),manualColor.getBlue());
				            currentColor = manualColor;
				        }
					}
				});
				add(slider);
				slider = new JSlider(JSlider.HORIZONTAL,0,255,currentColor.getBlue());
				slider.setBorder(BorderFactory.createTitledBorder("Blue"));
				slider.setBounds(0,60, 200, 30);
				slider.addChangeListener(new ChangeListener(){
					public void stateChanged(ChangeEvent e) {
				        JSlider source = (JSlider)e.getSource();
				        if (!source.getValueIsAdjusting()) {
				            manualColor = new Color(manualColor.getRed(),manualColor.getGreen(),source.getValue());
				            currentColor = manualColor;
				        }
					}
				});
				add(slider);
				setLayout(null);
				setVisible(true);
			}
		}
		
		public void addSizePanel(){
			JButton button = new JButton();
			button.setLayout(null);
			Dimension dim = new Dimension(18,35);
			button.setSize(dim);
			button.setBounds(660, 0, 20, 40);
			button.addActionListener(new ManualSizeListener());
			add(button);
			for (int i: sizes){
				button = new SizeButton(i);
				button.setLayout(null);
				dim = new Dimension(35,35);
				button.setSize(dim);
				button.setBounds(680-20 -sizes.length*40+ 40*(i/5-1), 0, 40, 40);
				button.addActionListener((ActionListener) button);
				add(button);
			}
		}
		public class SizeButton extends JButton implements ActionListener{
			private static final long serialVersionUID = -2134787822255119414L;
			private int size;
			public SizeButton(int size){
				this.size = size;
			}
			public void paintComponent(Graphics g){
				g.setColor(Color.white);
				g.fillRect( 0, 0, 40, 40);
				g.setColor(Color.black);
				g.fillOval( 20-size/2, 20-size/2, size, size);
			}
			public void actionPerformed(ActionEvent e) {
				currentSize = size;
			}
		}
		public class ManualSizeListener implements ActionListener{
			public void actionPerformed(ActionEvent e) {
				new ManualSize();
			}
		}
		public class ManualSize extends JFrame{
			private static final long serialVersionUID = 6297106774971046265L;
			public ManualSize(){
				setTitle("Size");
				setBounds(728, 680, 170, 85);
				JSlider slider = new JSlider(JSlider.HORIZONTAL,0,40,currentSize);
				slider.setBorder(BorderFactory.createTitledBorder("Size"));
				slider.setBounds(0,0, 150, 40);
				slider.addChangeListener(new ChangeListener(){
					public void stateChanged(ChangeEvent e) {
				        JSlider source = (JSlider)e.getSource();
				        if (!source.getValueIsAdjusting()) {
				            currentSize = source.getValue();
				        }
					}
				});
				add(slider);
				setLayout(null);
				setVisible(true);
			}
		}
	}

	public MyPanel panel(){
		return panel;
	}
	public boolean notClosed(){
		return notClosed;
	}
}