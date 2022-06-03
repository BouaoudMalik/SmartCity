package com.rule.tests.samu;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RuleS4Test extends RuleS3Test {

	@Override
	@Test
	void testFilterEvent() {
		initTest();
		S4 ruleS4 = new S4();
		HealthCorrelatorStateMock correlator = new HealthCorrelatorStateMock();
		assertFalse(ruleS4.filter(ruleS4.match(events), correlator));
	}

}
