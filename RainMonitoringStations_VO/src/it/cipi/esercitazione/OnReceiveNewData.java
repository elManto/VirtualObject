package it.cipi.esercitazione;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.servlet.ServletContext;

import com.google.gson.Gson;

public class OnReceiveNewData extends Thread{
	
	private double temperature, rain;
	private static final String criticalTemp = "CRITICAL_TEMPERATURE";
	private static final String criticalRain = "CRITICAL_RAIN";
	private ServletContext sc;
	
	public OnReceiveNewData(ServletContext sc) {
		this.sc = sc;
		this.temperature = Math.random() * 10;
		this.rain = Math.random() * 10;
	}
	
	public void run() {
		processing();
	}
	
	private void processing() {
		if (temperature > 7 || temperature < 3) {
			notifyEvent(criticalTemp);
		}
		if (rain > 3) {
			notifyEvent(criticalRain);
		}
			
	}
	
	private void notifyEvent(String event) {
		if (event.equals(criticalTemp)) {
			System.out.println("CRITICAL TEMPERATURE-> SEND TO ORCHESTRATOR!");
			//this.connectToOrchestrator(criticalTemp);
		}
		else if (event.equals(criticalRain)) {
			//this.connectToOrchestrator(criticalRain);
			System.out.println("CRITICAL RAIN-> SEND TO ORCHESTRATOR!");

		}
		else
		 return;
	}
	
	private void connectToOrchestrator(String event) {
		try {
			String myUrl = (String)sc.getAttribute("URL_ORCHESTRATOR");	// inserire URL orchestratore!!!
			URL url = new URL(myUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");

			HashMap<String, String> input_values = new HashMap<String, String>();
			input_values.put("event", event);
			
			

			Gson gson = new Gson();

			String input = gson.toJson(input_values);
			System.out.println(gson.toJson(input_values));

			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.out.println("");
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}

			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
	}
	

}
