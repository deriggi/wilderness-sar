package webdomain;

import java.util.List;
import java.util.ArrayList;
import raster.domain.agent.FSMFactory;
import java.util.Collection;

public class WebBehaviourFactory{
	
	public static List<WebBehaviour> toList(Collection<FSMFactory.MachineName> behaviours){
		List<WebBehaviour> machines = new ArrayList<WebBehaviour>();

		for(FSMFactory.MachineName machineName : behaviours){

			
			machines.add(new WebBehaviour(machineName.toString(), machineName.getDescription()));
			
		}

		return machines;
	}
}