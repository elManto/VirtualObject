package it.cipi.esercitazione;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.log4j.Logger;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;

@Path("/objectcommunication")
public class VORealObjectCommunication {

	private static Logger log;
	private static ServletContext servletContext;
	
	static void init(ServletContext sc){
		log= Logger.getRootLogger();
		servletContext=sc;
		
	}
	@GET
	@Path("getinfo")
	@Produces(MediaType.APPLICATION_JSON)
	public String getInfo () {
		String payload = "id,name,latitude,longitude,height,temperature,wind_chill,humidity";
		return payload;
	}
}
