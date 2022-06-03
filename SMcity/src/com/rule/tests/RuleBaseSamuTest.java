package com.rule.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.event.EventBase;
import com.event.samu.HealthEvent;
import com.event.samu.InterventionEvent;
import com.rule.RuleBase;
import com.rule.tests.samu.HealthCorrelatorStateMock;
import com.rule.tests.samu.S1;
import com.rule.tests.samu.S2;
import com.rule.tests.samu.S3;
import com.rule.tests.samu.S4;

class RuleBaseSamuTest {

	RuleBase ruleBase;
	EventBase healthEventsBase;

	HealthEvent healthEventUrgence, healthEventUrgence2;
	HealthEvent healthEvent;
	InterventionEvent interventionrequest;

	void initTest() {
		ruleBase = new RuleBase();

		S1 ruleS1 = new S1();
		S2 ruleS2 = new S2();
		S3 ruleS3 = new S3();
		S4 ruleS4 = new S4();

		ruleBase.addRule(ruleS1);
		ruleBase.addRule(ruleS2);
		ruleBase.addRule(ruleS3);
		ruleBase.addRule(ruleS4);

		healthEventsBase = new EventBase();

		healthEventUrgence = new HealthEvent();
		healthEventUrgence2 = new HealthEvent();
		healthEvent = new HealthEvent();
		interventionrequest = new InterventionEvent();

		healthEventUrgence.putProperty("type", "urgence");
		healthEventUrgence2.putProperty("type", "urgence");
		healthEvent.putProperty("type", "tracage");
		interventionrequest.putProperty("type", "intervention");

		healthEventsBase.addEvent(healthEventUrgence);
		healthEventsBase.addEvent(healthEventUrgence2);
		healthEventsBase.addEvent(healthEvent);
		healthEventsBase.addEvent(interventionrequest);

	}

	@Test
	void testForFireFirstEvent() {
		initTest();
		HealthCorrelatorStateMock correlator = new HealthCorrelatorStateMock();
		assertTrue(ruleBase.fireFirstOn(healthEventsBase, correlator));
	}

	@Test
	void testForFireOnAllBase() {
		initTest();
		HealthCorrelatorStateMock correlator = new HealthCorrelatorStateMock();
		assertTrue(ruleBase.fireAllOn(healthEventsBase, correlator));
	}

}
