package com.components.interfaces;

import com.event.interfaces.EventI;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface EventEmissionCI extends OfferedCI, RequiredCI {

	public void sendEvent(String emitterURI, EventI event) throws Exception;

	public void sendEvents(String emitterURI, EventI[] events) throws Exception;

}
