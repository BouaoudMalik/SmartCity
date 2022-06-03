package com.event;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;

import com.event.interfaces.EventBaseI;
import com.event.interfaces.EventI;

public class EventBase implements EventBaseI {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private ArrayList<EventI> events;

	// -------------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------------

	public EventBase() {
		this.events = new ArrayList<>();
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * Method adding an event to the list of events
	 * 
	 * @param e an event
	 */
	@Override
	public void addEvent(EventI e) {
		this.events.add(e);
	}

	/**
	 * Method removing an event e from the list of events
	 * 
	 * @param e an event
	 */
	@Override
	public void removeEvent(EventI e) {
		this.events.remove(e);
	}

	/**
	 * Method getting the event at index i
	 * 
	 * @param i an index
	 * @return an event
	 */
	@Override
	public EventI getEvent(int i) {
		return this.events.get(i);
	}

	/**
	 * Method getting the number of events in the list
	 * 
	 * @return number of events
	 */
	@Override
	public int numberOfEvents() {
		return this.events.size();
	}

	/**
	 * Method that verify if an event appears in the list of events
	 *
	 * @param e an event
	 * @return boolean value
	 */
	@Override
	public boolean appearsIn(EventI e) {
		return this.events.contains(e);
	}

	/**
	 * Method to destroy all events that occurred at more time period than the
	 * current time
	 * 
	 * @param d a time
	 */
	@Override
	public void clearEvents(Duration d) {
		LocalTime currentTime, eventTime;
		currentTime = LocalTime.now();

		if (d == null) {
			this.events.removeAll(this.events);
		} else {
			for (int i = 0; i < this.events.size(); i++) {
				eventTime = this.events.get(i).getTimeStamp();
				Duration elapsedTime = Duration.between(eventTime, currentTime);

				if (elapsedTime.compareTo(d) > 0) {
					this.events.remove(i);
				}
			}
		}
	}

	/**
	 * Method getting the list of events
	 * 
	 * @return an array list of events
	 */
	public ArrayList<EventI> getEvents() {
		return this.events;
	}

}
