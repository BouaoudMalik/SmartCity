package com.rule.tests.pompier;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import com.event.interfaces.EventI;

class RuleF3Test extends RuleF1Test {

	@Override
	@Test
	void testFilterEvent() {
		initTest();
		F3 ruleF3 = new F3();
		FireCorrelatorStateMock correlator = new FireCorrelatorStateMock();
		assertFalse(ruleF3.filter(ruleF3.match(events), correlator));
	}

	@Override
	@Test
	void testUpdate() {
		initTest();
		F3 ruleF3 = new F3();
		ArrayList<EventI> matchedEvents = ruleF3.match(events);
		ruleF3.update(matchedEvents, events);
		assertEquals(events.getEvents().size(), 3);
	}

}
