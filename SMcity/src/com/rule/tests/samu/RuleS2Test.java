package com.rule.tests.samu;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RuleS2Test extends RuleS1Test {

	@Override
	@Test
	void testFilterEvent() {
		initTest();
		S2 ruleS2 = new S2();
		HealthCorrelatorStateMock correlator = new HealthCorrelatorStateMock();
		assertFalse(ruleS2.filter(ruleS2.match(events), correlator));
	}

}
