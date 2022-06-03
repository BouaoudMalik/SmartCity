package com.event.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.event.EventBase;
import com.event.samu.HealthEvent;
import com.event.samu.InterventionEvent;

/**
 * JUnit test class for an EventBase
 *
 */
class EventBaseTest {

	HealthEvent healthEventPerson, healthEventTracking, healthEventUrgent;
	InterventionEvent requestIntervention;
	HealthEvent healthEvent;
	EventBase eventBase;

	/**
	 * Set up the test environment with events and their properties
	 *
	 */
	public void initEvents() {
		healthEventPerson = new HealthEvent();
		healthEventPerson.putProperty("personId", "123");

		healthEventTracking = new HealthEvent();
		healthEventTracking.putProperty("type", "tracking");

		healthEventUrgent = new HealthEvent();
		healthEventUrgent.putProperty("type", "falling");

		requestIntervention = new InterventionEvent();
		requestIntervention.putProperty("type", "urgence");

		healthEvent = new HealthEvent();
		healthEvent.putProperty("type", "tracking");

		eventBase = new EventBase();

		eventBase.addEvent(healthEventPerson);
		eventBase.addEvent(healthEventTracking);
		eventBase.addEvent(healthEventUrgent);
		eventBase.addEvent(requestIntervention);
		eventBase.addEvent(healthEvent);
	}

	/**
	 * A method testing if the events appear in the list of eventBase
	 */
	@Test
	void testIfEventAppears() {
		this.initEvents();

		assertTrue(eventBase.appearsIn(healthEventUrgent));
		assertTrue(eventBase.appearsIn(healthEventTracking));
		assertTrue(eventBase.appearsIn(healthEventPerson));
	}

	/**
	 * A method testing if the events have been removed from the list of eventBase
	 * 
	 */
	@Test
	void testIfEventsIsRemoved() {
		this.initEvents();

		eventBase.removeEvent(healthEventUrgent);
		assertFalse(eventBase.appearsIn(healthEventUrgent));
	}

	/**
	 * A method testing if all the events have been removed when the duration of the
	 * method clearEvents is null
	 * 
	 */
	@Test
	void testIfEventsAreCleared() {
		this.initEvents();

		eventBase.clearEvents(null);
		assertEquals(eventBase.getEvents().size(), 0);
	}

}