import java.awt.Polygon;

public class Racecar {
	public int x;
	public int y;
	private double face;
	private double moveDir = 0;
	private int[][] corners = new int[4][2];
	
	private double curSpeed = 0;
	
	private static int xsize = 20;
	private static int ysize = 40;
	private static double accel = 0.1;
	private static double brake = 0.3;
	private static double turnRate = Math.PI * 2 / 24;
	private static int maxSpeed = 30;
	
	public Racecar(int x, int y, double face) {
		this.x = x;
		this.y = y;
		this.face = face;
		refreshCorners();
	}
	
	private int[] getXCorners(){
		int[] tr = new int[4];
		tr[0] = (int) ((xsize/2 * Math.cos(face)) - (ysize/2 * Math.sin(face)) + x);
		tr[1] = (int) ((-xsize/2 * Math.cos(face)) - (ysize/2 * Math.sin(face)) + x);
		tr[2] = (int) ((-xsize/2 * Math.cos(face)) - (-ysize/2 * Math.sin(face)) + x);
		tr[3] = (int) ((xsize/2 * Math.cos(face)) - (-ysize/2 * Math.sin(face)) + x);
		return tr;
	}
	
	private int[] getYCorners(){
		int[] tr = new int[4];
		tr[0] = (int) ((xsize/2 * Math.sin(face)) + (ysize/2 * Math.cos(face)) + y);
		tr[1] = (int) ((-xsize/2 * Math.sin(face)) + (ysize/2 * Math.cos(face)) + y);
		tr[2] = (int) ((-xsize/2 * Math.sin(face)) + (-ysize/2 * Math.cos(face)) + y);
		tr[3] = (int) ((xsize/2 * Math.sin(face)) + (-ysize/2 * Math.cos(face)) + y);
		return tr;
	}
	
	private void refreshCorners() {
		int[] x = getXCorners();
		int[] y = getYCorners();
		for(int i = 0;i < 4;i++) {
			corners[i][0] = x[i];
			corners[i][1] = y[i];
		}
	}
	
	public Polygon getPolygon(double scale, int minDispx, int minDispy) {
		int[] x = getXCorners();
		int[] y = getYCorners();
		for(int i = 0;i < 4;i++) {
			x[i] = (int) ((x[i] - minDispx) * scale);
			y[i] = (int) ((y[i] - minDispy) * scale);
		}
		return new Polygon(x, y, 4);
	}
	
	public boolean intersectsLineSegment(int[] p1, int[] p2) {
		refreshCorners();
		if(intersectsLine(p1,p2,0,1)) {
			return true;
		}
		if(intersectsLine(p1,p2,1,2)) {
			return true;
		}
		if(intersectsLine(p1,p2,2,3)) {
			return true;
		}
		if(intersectsLine(p1,p2,3,0)) {
			return true;
		}
		return false;
	}
	
	private boolean intersectsLine(int[] p0, int[] p1, int s0, int s1) {
		// If lines are vertical
		if(corners[s0][0] == corners[s1][0] && p0[0] == p0[0]) {
			if(corners[s0][0] != p0[0]) {
				return false;
			}
			return overlap(corners[s0][1],corners[s1][1],p0[1],p1[1]);
		}
		
		double aL = (p1[1] - p0[1]) * 1.0/(p1[0] - p0[0]);
		double bL = p0[1] - (aL * p0[0]);
		
		double aS = (corners[s1][1] - corners[s0][1]) * 1.0/(corners[s1][0] - corners[s0][0]);
		double bS = corners[s0][1] - (aS * corners[s0][0]);
		// If lines are parallel
		if(eq(aL, aS, 0.001)) {
			if(!eq(bL, bS, 0.001)) {
				return false;
			}
			return overlap(corners[s1][0], corners[s0][0], p1[0], p0[0]);
		}
		// Otherwise just find the intersect x
		int x = (int) (-(bL - bS)/(aL - aS));
		
		return inRange(corners[s0][0], corners[s1][0], x) && inRange(p0[0], p1[0], x);
		
	}
	
	private boolean overlap(int a0, int a1, int b0, int b1) {
		int c0,c1,d0,d1;
		if(a0 < a1) {
			c0 = a0;
			c1 = a1;
		} else {
			c0 = a1;
			c1 = a0;
		}
		if(b0 < b1) {
			d0 = b0;
			d1 = b1;
		} else {
			d0 = b1;
			d1 = b0;
		}
		return (c0 <= d1 && d0 <= c1);	
	}
	
	private boolean inRange(int a0, int a1, int x) {
		int b0,b1;
		if(a0 < a1) {
			b0 = a0;
			b1 = a1;
		} else {
			b0 = a1;
			b1 = a0;
		}
		return (x > b0) && (x < b1);
	}
	
	private boolean eq(double a, double b, double r) {
		return (a + r > b) && (a - r < b);
	}
	
	private void shiftToward(double rate) {
		if(eq(face, moveDir, rate * turnRate * (maxSpeed - curSpeed))) {
			moveDir = face;
		}else if(face > moveDir){
            if(face - moveDir > Math.PI){
            	moveDir -= rate * turnRate * (maxSpeed - curSpeed);
            }else{
            	moveDir += rate * turnRate * (maxSpeed - curSpeed);
            }
        }else{
            if(moveDir - face >= Math.PI){
            	moveDir += rate * turnRate * (maxSpeed - curSpeed);
            }else{
            	moveDir -= rate * turnRate * (maxSpeed - curSpeed);
            }
        }
	}
	
	public void speedUp() {
		curSpeed += accel * (maxSpeed - curSpeed);
		shiftToward(0.1);
	}
	
	public void brake() {
		if(curSpeed < brake) {
			curSpeed = 0;
			moveDir = face;
		} else {
			curSpeed = curSpeed * brake;
		}
		System.out.println(curSpeed);
	}
	public void turnLeft() {
		face -= turnRate;
		curSpeed -= brake;
	}
	public void turnRight() {
		face += turnRate;
		curSpeed -= brake;
	}
	public void tick() {
		if(curSpeed < 0) {
			curSpeed = 0;
		}
		shiftToward(0.05);
		x += curSpeed * Math.sin(moveDir);
		y -= curSpeed * Math.cos(moveDir);
	}
	
	
	
}
