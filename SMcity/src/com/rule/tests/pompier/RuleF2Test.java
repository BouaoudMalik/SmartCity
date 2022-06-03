package com.rule.tests.pompier;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import com.event.interfaces.EventI;

class RuleF2Test extends RuleF1Test {

	@Override
	@Test
	void testMatchedEvent() {
		initTest();
		F2 ruleF2 = new F2();
		assertTrue(events.getEvents().size() == 5);
		ArrayList<EventI> matchedEvents = ruleF2.match(events);
		assertTrue(matchedEvents.size() == 1);
	}

	@Override
	@Test
	void testFilterEvent() {
		initTest();
		F2 ruleF2 = new F2();
		FireCorrelatorStateMock fireState = new FireCorrelatorStateMock();
		assertTrue(ruleF2.filter(ruleF2.match(events), fireState));
	}
}
