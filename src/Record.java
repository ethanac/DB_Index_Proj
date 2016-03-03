import java.math.BigInteger;

public class Record {
	public BigInteger id;
	public int SIN;
	public String firstName;
	public String lastName;
	public int age;
	public int income;
	public String address;
	
	public String toString(){
		return (id+": "+SIN +" "+firstName +" "+lastName+" "+age+" "+income+" "+address);
	}
}
