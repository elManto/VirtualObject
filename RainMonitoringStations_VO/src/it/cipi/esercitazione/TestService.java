package it.cipi.esercitazione;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.POST;

@Path("/weatherdata")
public class TestService {

	private static Logger log;
	private static ServletContext servletContext;
	
	static void init(ServletContext sc){
		log= Logger.getRootLogger();
		servletContext=sc;
		
	}
	@GET
	@Path("testget")
	@Produces(MediaType.APPLICATION_JSON)
	public String test () {
		String hello ="ciao!!!!!";
		return hello;
	}
	
	@GET
	@Path("testgetqueryparam")
	@Produces(MediaType.APPLICATION_JSON)
	public String testQuery (@QueryParam("colore") String colore) {
		String hello ="Hai passato il colore: "+colore;
		return hello;
	}
	
	@GET
	@Path("testgetpathparam/{forma}")
	@Produces(MediaType.APPLICATION_JSON)
	public String testPath (@PathParam("forma") String forma) {
		String hello ="Hai passato la forma: "+forma;
		return hello;
	}
	
	/*
	 * http://localhost:8080/WeatherStation_VO/rest/weatherdata/testpostpathparam/{"uu":"foulo"}
	 */
	@POST
	//@Path("newWeatherData")
	@Path("testpostpathparam")
	@Produces(MediaType.APPLICATION_JSON)
	public String testpost (String payload) {
		String hello ="Hai passato il messaggio: " + payload;
		System.out.println(hello);
		Gson inputValuesJson= new Gson();
		// Properties prop = (Properties) servletContext.getAttribute("properties");
		HashMap <String, Object> inputs= (HashMap <String, Object>)inputValuesJson.fromJson(payload, HashMap.class);
		log.info(inputs.toString());
		return hello;
	}
	
}
