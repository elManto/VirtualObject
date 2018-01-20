package it.cipi.esercitazione;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/VOCoreCommunication")
public class VOCoreCommunication {
	
	@POST
	@Path("invokeAction")
	//@Produces(MediaType.APPLICATION_JSON)
	public void invokeAction() {
		System.err.println("START ALARM!!!!!!!");
	}
}
