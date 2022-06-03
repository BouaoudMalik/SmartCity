package com.cvm;

import java.time.LocalTime;
import java.util.Iterator;

import com.components.CEPBus;
import com.components.Correlator;
import com.components.FireStationFacade;
import com.components.SAMUStationFacade;
import com.components.TrafficLightFacade;
import com.rule.RuleBase;
import com.rule.fire.RuleF1;
import com.rule.fire.RuleF15;
import com.rule.fire.RuleF17;
import com.rule.fire.RuleF18;
import com.rule.fire.RuleF2;
import com.rule.fire.RuleF3;
import com.rule.fire.RuleF4;
import com.rule.samu.RuleS1;
import com.rule.samu.RuleS12bis;
import com.rule.samu.RuleS15;
import com.rule.samu.RuleS16;
import com.rule.samu.RuleS17;
import com.rule.samu.RuleS2;
import com.rule.samu.RuleS3;
import com.rule.samu.RuleS4;
import com.rule.samu.RuleS5;
import com.rule.samu.RuleS6;
import com.rule.samu.RuleS7;
import com.rule.samu.RuleS8;
import com.rule.samu.RuleS9;
import com.rule.samu.RuleS18;
import com.rule.samu.RuleS19;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.cps.smartcity.AbstractSmartCityCVM;
import fr.sorbonne_u.cps.smartcity.SmartCityDescriptor;
import fr.sorbonne_u.cps.smartcity.grid.IntersectionPosition;
import fr.sorbonne_u.cps.smartcity.utils.TimeManager;

public class SmartCityCVM extends AbstractSmartCityCVM {

	public SmartCityCVM() throws Exception {
		super();
	}

	/**
	 * @see fr.sorbonne_u.components.cvm.AbstractCVM#deploy()
	 */
	@Override
	public void deploy() throws Exception {
		String cepBus_URI = AbstractPort.generatePortURI();
		String eventEmissionIBP_URI = AbstractPort.generatePortURI();
		String eventReceptionIBP_URI = AbstractPort.generatePortURI();
		String eventReceptionOBP_URI = AbstractPort.generatePortURI();

		RuleBase samuRuleBase = new RuleBase();
		RuleBase fireRuleBase = new RuleBase();
		RuleBase trafficRuleBase = new RuleBase();

		RuleS1 ruleS1 = new RuleS1();
		RuleS2 ruleS2 = new RuleS2();
		RuleS3 ruleS3 = new RuleS3();
		RuleS4 ruleS4 = new RuleS4();
		RuleS5 ruleS5 = new RuleS5();
		RuleS6 ruleS6 = new RuleS6();
		RuleS7 ruleS7 = new RuleS7();
		RuleS8 ruleS8 = new RuleS8();
		RuleS9 ruleS9 = new RuleS9();
		RuleS12bis ruleS12b = new RuleS12bis();
		RuleS15 ruleS15 = new RuleS15();
		RuleS16 ruleS16 = new RuleS16();
		RuleS17 ruleS17 = new RuleS17();
		RuleS18 ruleS18 = new RuleS18();
		RuleS19 ruleS19 = new RuleS19();

		samuRuleBase.addRule(ruleS1);
		samuRuleBase.addRule(ruleS2);
		samuRuleBase.addRule(ruleS3);
		samuRuleBase.addRule(ruleS4);
		samuRuleBase.addRule(ruleS5);
		samuRuleBase.addRule(ruleS6);
		samuRuleBase.addRule(ruleS7);
		samuRuleBase.addRule(ruleS8);
		samuRuleBase.addRule(ruleS9);
		samuRuleBase.addRule(ruleS12b);
		samuRuleBase.addRule(ruleS15);
		samuRuleBase.addRule(ruleS16);
		samuRuleBase.addRule(ruleS17);
		samuRuleBase.addRule(ruleS18);
		samuRuleBase.addRule(ruleS19);

		RuleF1 ruleF1 = new RuleF1();
		RuleF2 ruleF2 = new RuleF2();
		RuleF3 ruleF3 = new RuleF3();
		RuleF4 ruleF4 = new RuleF4();
		RuleF15 ruleF15 = new RuleF15();
		RuleF17 ruleF17 = new RuleF17();
		RuleF18 ruleF18 = new RuleF18();

		fireRuleBase.addRule(ruleF1);
		fireRuleBase.addRule(ruleF2);
		fireRuleBase.addRule(ruleF3);
		fireRuleBase.addRule(ruleF4);
		fireRuleBase.addRule(ruleF15);
		fireRuleBase.addRule(ruleF17);
		fireRuleBase.addRule(ruleF18);

		AbstractComponent.createComponent(CEPBus.class.getCanonicalName(),
				new Object[] { cepBus_URI, eventEmissionIBP_URI, eventReceptionIBP_URI, eventReceptionOBP_URI });

		Iterator<String> samuStationsIditerator = SmartCityDescriptor.createSAMUStationIdIterator();
		while (samuStationsIditerator.hasNext()) {
			String samuStationId = samuStationsIditerator.next();
			String notificationInboundPortURI = AbstractPort.generatePortURI();
			
			this.register(samuStationId, notificationInboundPortURI);
			AbstractComponent.createComponent(SAMUStationFacade.class.getCanonicalName(), new Object[] { samuStationId,
					notificationInboundPortURI, SmartCityDescriptor.getActionInboundPortURI(samuStationId) });

			AbstractComponent.createComponent(Correlator.class.getCanonicalName(),
					new Object[] { samuStationId, samuRuleBase });

		}

		Iterator<String> fireStationIdsIterator = SmartCityDescriptor.createFireStationIdIterator();
		while (fireStationIdsIterator.hasNext()) {
			String fireStationId = fireStationIdsIterator.next();
			String notificationInboundPortURI = AbstractPort.generatePortURI();

			this.register(fireStationId, notificationInboundPortURI);
			AbstractComponent.createComponent(FireStationFacade.class.getCanonicalName(), new Object[] { fireStationId,
					notificationInboundPortURI, SmartCityDescriptor.getActionInboundPortURI(fireStationId) });

			AbstractComponent.createComponent(Correlator.class.getCanonicalName(),
					new Object[] { fireStationId, fireRuleBase });
		}

		Iterator<IntersectionPosition> trafficLightsIterator = SmartCityDescriptor.createTrafficLightPositionIterator();
		while (trafficLightsIterator.hasNext()) {
			IntersectionPosition p = trafficLightsIterator.next();
			String notificationInboundPortURI = AbstractPort.generatePortURI();

			this.register(p.toString(), notificationInboundPortURI);
			AbstractComponent.createComponent(TrafficLightFacade.class.getCanonicalName(),
					new Object[] { p, notificationInboundPortURI, SmartCityDescriptor.getActionInboundPortURI(p) });

			AbstractComponent.createComponent(Correlator.class.getCanonicalName(),
					new Object[] { p.toString(), trafficRuleBase });
		}

		super.deploy();
	}

	public static void main(String[] args) {
		try {
			simulatedStartTime = LocalTime.of(12, 0);
			simulatedEndTime = LocalTime.of(12, 0).plusMinutes(30L);
			SmartCityCVM c = new SmartCityCVM();
			c.startStandardLifeCycle(TimeManager.get().computeExecutionDuration() + START_DELAY);
			Thread.sleep(10000L);
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
