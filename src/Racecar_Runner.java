import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class Racecar_Runner {

	static JPanel j;
	
	public static int numIntWalls;
	public static int numExtWalls;
	public static int[][] internalWalls = new int[100][2];
	public static int[][] externalWalls = new int[100][2];
	public static double simScale = 2;
	public static int xmax = (int) (simScale * 1800), ymax = (int) (simScale * 1000);
	
	public static boolean zoomed = false;
	public static int zoom = 3;
	public static int xmid = xmax/2;
	public static int ymid = ymax/2;
	public static int minDispx = 0;
	public static int minDispy = 0;
	
	public static Racecar car = new Racecar(234, 860, 0);
	public static char YAc = 'X';
	public static char XAc = 'X';
	
	public static long curTime;
	static KeyListener k = new KeyListener() {

		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()) {
			case KeyEvent.VK_Z :
				zoomed = !zoomed;
			break;
			case KeyEvent.VK_W :
				YAc = 'A';
			break;
			case KeyEvent.VK_S :
				YAc = 'B';
			break;
			case KeyEvent.VK_A :
				XAc = 'L';
			break;
			case KeyEvent.VK_D :
				XAc = 'R';
			break;
			case KeyEvent.VK_H :
				load("walls.txt");
			break;
			}
			j.repaint();
		}

		public void keyReleased(KeyEvent e) {
			
		}

		public void keyTyped(KeyEvent e) {
			
		}
			
	};
	static MouseListener m = new MouseListener() {

		@Override
		public void mouseClicked(MouseEvent e) {
			//System.out.println((e.getX() - 10) + " " + (e.getY() - 40));
			/*if (e.getButton() == MouseEvent.BUTTON1) {
				internalWalls[numIntWalls][0] = (int) ((e.getX() - 10) * simScale - minDispx);
				internalWalls[numIntWalls][1] = (int) ((e.getY() - 40) * simScale - minDispy);
				numIntWalls++;
			}else {
				externalWalls[numExtWalls][0] = (int) ((e.getX() - 10) * simScale - minDispx);
				externalWalls[numExtWalls][1] = (int) ((e.getY() - 40) * simScale - minDispy);
				numExtWalls++;
			}*/
			j.repaint();
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			
		}

			
	};
	
	public static void main(String[] args) {
		load("walls.txt");
		render();
		while(true) {
			
			switch (YAc) {
			case 'A' :
				car.speedUp();
			break;
			case 'B' :
				car.brake();
			break;
			}
			switch(XAc) {
			case 'L' :
				car.turnLeft();
			break;
			case 'R' :
				car.turnRight();
			break;
			}
			YAc = 'X';
			XAc = 'X';
			
			try {
				Thread.sleep(40);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			car.tick();
			j.repaint();
		}
	}
	
	private static void save(String filename) {
		System.out.println("SAVING TO " + filename);
		try {
			FileWriter fw = new FileWriter(filename);
			fw.write("" + numExtWalls);
			for(int i = 0;i < numExtWalls;i++) {
				fw.write("\n" + externalWalls[i][0] + " " + externalWalls[i][1]);
			}
			fw.write("\n" + numIntWalls);
			for(int i = 0;i < numIntWalls;i++) {
				fw.write("\n" + internalWalls[i][0] + " " + internalWalls[i][1]);
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void load(String filename) {
		System.out.println("LOADING FROM " + filename);
		try {
			File f = new File(filename);
			Scanner fr = new Scanner(f);
			numExtWalls = fr.nextInt();

			for(int i = 0;i < numExtWalls;i++) {
				externalWalls[i][0] = fr.nextInt();
				externalWalls[i][1] = fr.nextInt();
			}

			numIntWalls = fr.nextInt();

			for(int i = 0;i < numIntWalls;i++) {
				internalWalls[i][0] = fr.nextInt();
				internalWalls[i][1] = fr.nextInt();
			}
			
			fr.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static void render(){
		JFrame frame = new JFrame("RACECAR");
		
		j = new JPanel(){
			public void paint(Graphics p) {	
				
				int maxDispx = xmax;
				int maxDispy = ymax;
				
				double scale = 1/simScale;
				
				
				if(zoomed) {
					xmid = car.x;
					ymid = car.y;
					int xfield = (xmax / zoom);
					int yfield = (ymax / zoom);
					minDispx = xmid - xfield/2;
					minDispy = ymid - yfield/2;
					
					maxDispx = minDispx + xfield;
					maxDispy = minDispy + yfield;
					
					scale *= zoom;
					
					//System.out.println(minDispx + " " + minDispy + " " + xmid + " " + ymid + " " + maxDispx + " " + maxDispy + " " + xfield + " " + yfield + " " + zoom);
									
				} else {
					minDispx = 0;
					minDispy = 0;
				}
				
				p.setColor(new Color(50, 145, 50));
				p.fillRect(0, 0, 2000, 2000);
				boolean boop = false;

				p.setColor(new Color(120, 120, 120));
				Polygon in = new Polygon();
				for(int i = 1; i < numIntWalls; i++) {
					in.addPoint((int) ((internalWalls[i][0] - minDispx) * scale), (int) ((internalWalls[i][1] - minDispy) * scale));
					if(car.intersectsLineSegment(internalWalls[i - 1], internalWalls[i])) {
						boop = true;
					}
				}
				p.fillPolygon(in);

				p.setColor(new Color(50, 145, 50));
				Polygon ex = new Polygon();
				for(int i = 0; i < numExtWalls; i++) {
					ex.addPoint((int) ((externalWalls[i][0] - minDispx) * scale), (int) ((externalWalls[i][1] - minDispy) * scale));
					if(i > 0 && car.intersectsLineSegment(externalWalls[i - 1], externalWalls[i])) {
						boop = true;
					}
				}
				p.fillPolygon(ex);
				if(boop) {
					p.setColor(Color.RED);
				}else {
					p.setColor(Color.BLACK);
				}
				p.fillPolygon(car.getPolygon(scale, minDispx, minDispy));
				

				if(zoomed) {				
					p.setColor(Color.BLACK);	
					p.drawRect(1600, 50, 180, 100);
					p.setColor(Color.RED);
					p.drawRect(1600 + (180 * minDispx/xmax), 50 + (100 * minDispy/ymax), 180 / zoom, 100 / zoom);
				}
				
				p.setColor(Color.BLACK);
				
			}
		};
			
		frame.add(j);
		frame.addKeyListener(k);
		frame.addMouseListener(m);
		frame.setLocation(200,0);
		frame.setSize(1500, 1000);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
}
