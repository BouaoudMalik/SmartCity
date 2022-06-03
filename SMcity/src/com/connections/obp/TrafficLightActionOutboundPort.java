package com.connections.obp;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.cps.smartcity.interfaces.TrafficLightActionCI;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfTrafficLightPriority;

public class TrafficLightActionOutboundPort extends AbstractOutboundPort implements TrafficLightActionCI {
	
	private static final long serialVersionUID = 1L;

	public TrafficLightActionOutboundPort(ComponentI owner) throws Exception {
		super(TrafficLightActionCI.class, owner);
	}

	public TrafficLightActionOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, TrafficLightActionCI.class, owner);
	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.TrafficLightActionCI#changePriority(fr.sorbonne_u.cps.smartcity.interfaces.TypeOfTrafficLightPriority)
	 */
	@Override
	public void changePriority(TypeOfTrafficLightPriority priority) throws Exception {
		((TrafficLightActionCI) this.getConnector()).changePriority(priority);
	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.TrafficLightActionCI#returnToNormalMode()
	 */
	@Override
	public void returnToNormalMode() throws Exception {
		((TrafficLightActionCI) this.getConnector()).returnToNormalMode();
	}
	
}
