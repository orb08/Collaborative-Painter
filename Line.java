import java.awt.Color;
import java.io.Serializable;

public class Line implements Serializable{
	private static final long serialVersionUID = -4621945402478833993L;
	private int[] coor1, coor2;
	private int size;
	private Color color;
	
	public Line(int[] coor1, int[] coor2, int size, Color color){
		this.coor1 = coor1;
		this.coor2 = coor2;
		this.size = size;
		this.color = color;
	}
	public int[] coor1(){
		return coor1;
	}
	public int[] coor2(){
		return coor2;
	}
	public int size(){
		return size;
	}
	public Color color(){
		return color;
	}
	public String toString(){
		System.out.print(coor1[0]+" "+coor1[1]+" "+coor2[0]+" "+coor2[1]);
		if (coor1==coor2)
			return "\n";
		else
		return "";
	}
}