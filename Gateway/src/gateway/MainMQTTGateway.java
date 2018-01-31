package gateway;


import java.util.Timer;

public class MainMQTTGateway {

	public static void main(String[] args) {
		Timer t = new Timer();
		Gateway g = new Gateway();
		t.scheduleAtFixedRate(g, 0, 10);
		
			
	}
	

}
