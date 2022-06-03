package com.event.abstracts;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.HashMap;

import com.event.interfaces.AtomicEventI;

import fr.sorbonne_u.cps.smartcity.utils.TimeManager;

/**
 * AtomicEvent is a class that can not be divided
 */
public abstract class AtomicEvent implements AtomicEventI {

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private LocalTime time;
	private HashMap<String, Serializable> properties;

	// -------------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------------

	public AtomicEvent() {
		time = TimeManager.get().getCurrentLocalTime();
		properties = new HashMap<>();
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * Method getting the moment at which the event was created
	 * 
	 * @return a time
	 */
	public LocalTime getTimeStamp() {
		return time;
	}

	/**
	 * Method setting a new time
	 * 
	 * @param time a time
	 */
	public void setTime(LocalTime time) {
		this.time = time;
	}

	/**
	 * Method testing the presence of a property in an event
	 * 
	 * @param name the name of the variable
	 * @return boolean value
	 */
	public boolean hasProperty(String name) {
		return properties.containsKey(name);
	}

	/**
	 * Method getting a serializable property value
	 * 
	 * @param name the name of the variable
	 * @return a serializable value
	 */
	public Serializable getPropertyValue(String name) {
		return properties.get(name);
	}

	/**
	 * A method adding a property with a name and a value
	 * 
	 * @param name  property name
	 * @param value the value of the property
	 * @return the property added
	 */
	@Override
	public Serializable putProperty(String name, Serializable value) {
		properties.put(name, value);
		return value;
	}

	/**
	 * A method removing the property from the name
	 * 
	 * @param name the name of the removed property
	 */
	@Override
	public void removeProperty(String name) {
		properties.remove(name);
	}

}
