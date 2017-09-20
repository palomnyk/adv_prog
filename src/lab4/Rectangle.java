package lab4;

public class Rectangle 
{
	private double width;
	private double height;
	// constructor
	public Rectangle( double width, double height) 
	{
		this.height = height;
		this.width = width;
	}
	// returns  width * height
	public double getArea()
	{
		return height * width;
	}
	// returns 2 * width + 2 * height
	public double getPerimeter()
	{
		return width * 2 + height * 2;
	}
	public static void main(String[] args) throws Exception
	{
		Rectangle r = new Rectangle(10, 5);
		System.out.println(r.getArea());  //50
		System.out.println(r.getPerimeter());  // 30
	}
}

