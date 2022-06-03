package com.rule.tests.pompier;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import com.event.EventBase;
import com.event.fire.FireEvent;
import com.event.interfaces.EventI;

class RuleF1Test {

	FireEvent fireEventhouse, fireEventBuilding, fireEventBuilding2, fireEventForest, fireEventOffice;
	EventBase events;

	void initTest() {
		events = new EventBase();

		fireEventhouse = new FireEvent();
		fireEventBuilding = new FireEvent();
		fireEventBuilding2 = new FireEvent();
		fireEventForest = new FireEvent();
		fireEventOffice = new FireEvent();

		fireEventhouse.putProperty("type", "maison");
		fireEventBuilding.putProperty("type", "immeuble");
		fireEventBuilding2.putProperty("type", "immeuble");
		fireEventForest.putProperty("type", "forêt");
		fireEventOffice.putProperty("type", "bureaux");

		events.addEvent(fireEventhouse);
		events.addEvent(fireEventBuilding);
		events.addEvent(fireEventBuilding2);
		events.addEvent(fireEventForest);
		events.addEvent(fireEventOffice);
	}

	@Test
	void testMatchedEvent() {
		initTest();
		F1 ruleF1 = new F1();
		assertTrue(events.getEvents().size() == 5);
		ArrayList<EventI> matchedEvents = ruleF1.match(events);
		assertTrue(matchedEvents.size() == 2);
	}

	@Test
	void testCorrelate() {
		initTest();
		F1 ruleF1 = new F1();
		assertTrue(ruleF1.correlate(ruleF1.match(events)));
	}

	@Test
	void testFilterEvent() {
		initTest();
		F1 ruleF1 = new F1();
		FireCorrelatorStateMock correlator = new FireCorrelatorStateMock();
		assertTrue(ruleF1.filter(ruleF1.match(events), correlator));
	}

	@Test
	void testUpdate() {
		initTest();
		F1 ruleF1 = new F1();
		ArrayList<EventI> matchedEvents = ruleF1.match(events);
		ruleF1.update(matchedEvents, events);
		assertEquals(events.getEvents().size(), 4);
	}

}
