package gateway;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.TimerTask;



public class Gateway extends TimerTask{
	private String[] data = new String[100];
	
	public void run() {
		data = this.sendConnection();		
		Publisher.publish(data);

	}
	
	
	
	private String[] sendConnection() {
		try {
			String myUrl = "http://dati.comune.genova.it/Web_Service_meteo/"
					+ "handle_soap_request.php?mode=no_wsdl&action=rea" + 
					"d_json";
			
			URL url = new URL(myUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/json");


			System.out.println("SENDING REQUEST");			

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.out.println("");
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			String[] array = new String[100];
			System.out.println("Output from Server .... \n");
			
			while ((output = br.readLine()) != null) {
				System.out.println(output);
				array = output.split("}");

			}

			for(int i = 0; i < array.length; i++) {
				if (i > 0) {
					array[i] = array[i].replaceFirst(",", "");
				}
				else {
					array[i] = array[i].replace("[", "");
				}
				array[i] += "}";
				System.out.println("UGO");

				System.out.println(array[i]);

			}
			
			
			conn.disconnect();
			return array;
		} catch (MalformedURLException e) {

			e.printStackTrace();
			return null;
		} catch (IOException e) {

			e.printStackTrace();
			return null;
		}
		
	}
	
	/*
	 	private void printMap(HashMap<String, Object> example) {
	 
		for (String name: example.keySet()){

            String key =name.toString();
            String value = example.get(name).toString();  
            System.out.println(key + " " + value);  
		}
		}
	*/
	
}
