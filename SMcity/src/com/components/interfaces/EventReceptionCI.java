package com.components.interfaces;

import com.event.interfaces.EventI;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface EventReceptionCI extends OfferedCI, RequiredCI {

	public void receiveEvent(String emitterURI, EventI e) throws Exception;

	public void receiveEvents(String emitterURI, EventI[] events) throws Exception;

}
