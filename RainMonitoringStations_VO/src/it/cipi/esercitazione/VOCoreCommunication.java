package it.cipi.esercitazione;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/VOCoreCommunication")
public class VOCoreCommunication {
	
	@POST
	@Path("invokeAction")
	//http://localhost:8080/RainMonitoringStations_VO/rest/VOCoreCommunication/invokeAction
	public void invokeAction() {
		System.err.println("START ALARM!!!!!!!");
	}
}
