package com.rule.tests.samu;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import com.event.EventBase;
import com.event.interfaces.EventI;
import com.event.samu.HealthEvent;
import com.event.samu.InterventionEvent;

class RuleS3Test {

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

		healthEventUrgence.putProperty("type", "médicale");
		healthEventUrgence2.putProperty("type", "médicale");
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
		S3 ruleS3 = new S3();
		assertTrue(events.getEvents().size() == 4);
		ArrayList<EventI> matchedEvents = ruleS3.match(events);
		assertTrue(matchedEvents.size() == 2);
	}

	@Test
	void testCorrelate() {
		initTest();
		S3 ruleS3 = new S3();
		assertTrue(ruleS3.correlate(ruleS3.match(events)));
	}

	@Test
	void testFilterEvent() {
		initTest();
		S3 ruleS3 = new S3();
		HealthCorrelatorStateMock correlator = new HealthCorrelatorStateMock();
		assertTrue(ruleS3.filter(ruleS3.match(events), correlator));
	}

	@Test
	void testUpdate() {
		initTest();
		S3 ruleS3 = new S3();
		ArrayList<EventI> matchedEvents = ruleS3.match(events);
		ruleS3.update(matchedEvents, events);
		assertEquals(events.getEvents().size(), 2);
	}

}
