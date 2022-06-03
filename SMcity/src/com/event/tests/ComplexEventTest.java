package com.event.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import com.event.ComplexEvent;
import com.event.interfaces.EventI;
import com.event.samu.HealthEvent;
import com.event.samu.InterventionEvent;

/**
 * JUnit test class for an ComplexEvent
 *
 */
class ComplexEventTest {

	ArrayList<EventI> events;
	HealthEvent healthEventFall;
	InterventionEvent interventionRequest;

	/**
	 * Set up the test environment with events and their properties
	 * 
	 */
	public void initEvents() {
		events = new ArrayList<>();

		healthEventFall = new HealthEvent();
		healthEventFall.putProperty("personId", "123");

		interventionRequest = new InterventionEvent();
		interventionRequest.putProperty("type", "request");
		interventionRequest.putProperty("personId", "123");

		events.add(healthEventFall);
		events.add(interventionRequest);
	}

	/**
	 * Testing if a ComplexEvent is correctly set
	 * 
	 */
	@Test
	void testForCorrelatedEvent() {
		this.initEvents();

		ComplexEvent c = new ComplexEvent(events);

		assertTrue(c.getCorrelatedEvents().size() == 2);

		EventI fall = c.getCorrelatedEvents().get(0);
		EventI signal = c.getCorrelatedEvents().get(1);

		assertTrue(fall.hasProperty("personId"));
		assertTrue(signal.hasProperty("type"));
		assertTrue(fall.getPropertyValue("personId").equals(signal.getPropertyValue("personId")));
	}

}
