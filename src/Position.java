/**
 * Class holds a Cell position as an object. 
 * @author DirgleHurbleHerb
 *
 */
public class Position {

	private double x;
	private double y;
	/**
	 * Constructor
	 * @param x
	 * x position of Cell
	 * @param y
	 * y position of Cell.
	 */
	public Position(double x, double y) {
		this.x = x;
		this.y = y;
	}
	/**
	 * Constructor
	 * @param pos
	 * An array of double values containing only x and y positions
	 * of the Cell.
	 */
	public Position(double[] pos) {
		this.x = pos[0];
		this.y = pos[1];	
	}
	/**
	 * toArray returns x and y as an Array.
	 * @return
	 * returns x and y as an Array.
	 */
	public double [] toArray() {

		double[] posArray = new double[] {x,y};

		return posArray;
	}

	/*below methods (hashCode and equals) were automatically generated methods
	 * which were necessary for being able to check Position values (x and y) 
	 * against the hashMap values in the Game class.*/
//	
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + (int) (x ^ (x >>> 32));
//		result = prime * result + (int) (y ^ (y >>> 32));
//		return result;
//	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Position other = (Position) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}
	//Setters and Getters.
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

}
