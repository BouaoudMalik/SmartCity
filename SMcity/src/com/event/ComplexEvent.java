package com.event;

import java.util.ArrayList;

import com.event.interfaces.ComplexEventI;
import com.event.interfaces.EventI;

public class ComplexEvent implements ComplexEventI {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private ArrayList<EventI> events;

	// -------------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------------

	public ComplexEvent(ArrayList<EventI> events) {
		this.events = events;
	}

	/**
	 * Method getting the correlated events
	 * 
	 * @return an array list of events
	 */
	@Override
	public ArrayList<EventI> getCorrelatedEvents() {
		return this.events;
	}

}
