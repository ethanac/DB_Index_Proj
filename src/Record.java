import java.math.BigInteger;

public class Record {
	public BigInteger id;
	public int age;
	public int income;

	public String toString(){
		return (id+": " + age + ", " + income);
	}
}
