package com.rule.tests.pompier;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import com.event.interfaces.EventI;

class RuleF4Test extends RuleF3Test {

	@Override
	@Test
	void testMatchedEvent() {
		initTest();
		F4 ruleF4 = new F4();
		assertTrue(events.getEvents().size() == 5);
		ArrayList<EventI> matchedEvents = ruleF4.match(events);
		assertTrue(matchedEvents.size() == 1);
	}

	@Override
	@Test
	void testFilterEvent() {
		initTest();
		F4 ruleF4 = new F4();
		FireCorrelatorStateMock correlator = new FireCorrelatorStateMock();
		assertFalse(ruleF4.filter(ruleF4.match(events), correlator));
	}

}
