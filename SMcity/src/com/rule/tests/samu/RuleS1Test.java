package com.rule.tests.samu;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import com.event.EventBase;
import com.event.interfaces.EventI;
import com.event.samu.HealthEvent;
import com.event.samu.InterventionEvent;

class RuleS1Test {

	EventBase events;
	HealthEvent healthEventUrgence, healthEventUrgence2;
	HealthEvent healthEvent;
	InterventionEvent interventionrequest;

	void initTest() {
		events = new EventBase();
		healthEventUrgence = new HealthEvent();
		healthEventUrgence2 = new HealthEvent();
		healthEvent = new HealthEvent();
		interventionrequest = new InterventionEvent();

		healthEventUrgence.putProperty("type", "urgence");
		healthEventUrgence2.putProperty("type", "urgence");
		healthEvent.putProperty("type", "tracage");
		interventionrequest.putProperty("type", "intervention");

		events.addEvent(healthEventUrgence);
		events.addEvent(healthEventUrgence2);
		events.addEvent(healthEvent);
		events.addEvent(interventionrequest);
	}

	@Test
	void testIfMatchCreatesAnewList() {
		initTest();
		S1 ruleS1 = new S1();
		assertTrue(events.getEvents().size() == 4);
		ArrayList<EventI> matchedEvents = ruleS1.match(events);
		assertTrue(matchedEvents.size() == 2);
	}

	@Test
	void testCorrelate() {
		initTest();
		S1 ruleS1 = new S1();
		assertTrue(ruleS1.correlate(ruleS1.match(events)));
	}

	@Test
	void testFilterEvent() {
		initTest();
		S1 ruleS1 = new S1();
		HealthCorrelatorStateMock correlator = new HealthCorrelatorStateMock();
		assertTrue(ruleS1.filter(ruleS1.match(events), correlator));
	}

	/**
	 * Test if update is correctly triggered the eventBase contains 4 events, and
	 * the matched events contains 2. So the update should remove 2 events from
	 * eventBase
	 */
	@Test
	void testUpdate() {
		initTest();
		S1 ruleS1 = new S1();
		ArrayList<EventI> matchedEvents = ruleS1.match(events);
		ruleS1.update(matchedEvents, events);
		assertEquals(events.getEvents().size(), 2);
	}

}
