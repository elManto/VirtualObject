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
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import com.google.gson.Gson;

import it.cipi.esercitazione.TestService;
import it.cipi.esercitazione.utils.ConfigurationFileUtil;

@WebListener
public class ServiceListener implements ServletContextListener{

	private static Logger log = null;
	private VORealObjectCommunicationMQTT client;
	
	
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contextInitialized(ServletContextEvent evt) {
		// TODO Auto-generated method stub
		System.out.println("TEST SERVICE con runtime annotations STARTED!!!!");
		
		ServletContext sc = evt.getServletContext();
		String warName = sc.getContextPath().length() == 0 ? "ROOT" : sc.getContextPath().substring(1);
		ConsoleAppender console = new ConsoleAppender(); //create appender
		String PATTERN = "%d{dd MMM yyyy HH:mm:ss} %5p %l %m%n";
		console.setLayout(new PatternLayout(PATTERN)); 
		console.setThreshold(Level.DEBUG);
		console.activateOptions();
		Logger.getRootLogger().addAppender(console);
		RollingFileAppender rfa = new RollingFileAppender();
		rfa.setName(warName);
		rfa.setImmediateFlush(true);
		rfa.setFile(System.getProperty("catalina.home") + "/logs/"+warName+".log");
		rfa.setLayout(new PatternLayout("%d{dd MMM yyyy HH:mm:ss} %5p %l %m%n"));
		rfa.setThreshold(Level.DEBUG);
		rfa.setAppend(true);
		rfa.setMaxFileSize("10MB");
		rfa.setMaxBackupIndex(50);
		rfa.activateOptions();
		Logger.getRootLogger().addAppender(rfa);
		
		log = Logger.getRootLogger();
		ConfigurationFileUtil.readConfigFile(evt);
		new SSSConnection().start();
		
		this.sendRequest();
		
		it.cipi.esercitazione.TestService.init(sc);
		
	}
	
	private void sendRequest() {
		try {
			client = new VORealObjectCommunicationMQTT();
			client.doDemo();
			
			URL url = new URL("http://localhost:8080/VO_Register/rest/TestService/testparam");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");

			HashMap<String, String> input_values = new HashMap<String, String>();
			input_values.put("name", "RainMonitoringVO");
			input_values.put("ip", "http://localhost:8080/VO_Register/rest/TestService");
			input_values.put("invoke_actions", "START_ALARM");
			input_values.put("events", "CRITICAL_TEMPERATURE,CRITICAL_RAIN");
			input_values.put("properties", "test");
			

			

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
