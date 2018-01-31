package it.cipi.esercitazione;
import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.*;

@ApplicationPath("rest")
public class RESTServiceConfig extends ResourceConfig {
	public RESTServiceConfig ()
	{
		packages("esercitazione");
	}
}
