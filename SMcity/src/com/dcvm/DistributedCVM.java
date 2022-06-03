package com.dcvm;

import java.time.LocalTime;
import java.util.Iterator;

import com.components.CEPBus;
import com.components.Correlator;
import com.components.FireStationFacade;
import com.components.SAMUStationFacade;
import com.components.TrafficLightFacade;
import com.connections.connector.EventReceptionConnector;
import com.rule.RuleBase;
import com.rule.fire.RuleF1;
import com.rule.fire.RuleF2;
import com.rule.fire.RuleF3;
import com.rule.samu.RuleS1;
import com.rule.samu.RuleS2;
import com.rule.samu.RuleS3;
import com.rule.samu.RuleS5;
import com.rule.samu.RuleS6;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;
import fr.sorbonne_u.components.helpers.CVMDebugModes;
import fr.sorbonne_u.cps.smartcity.SmartCityDescriptor;
import fr.sorbonne_u.cps.smartcity.grid.IntersectionPosition;

public class DistributedCVM extends AbstractDistributedCVM {

	protected static long START_DELAY = 6000L;
	protected static LocalTime simulatedStartTime;
	protected static LocalTime simulatedEndTime;

	public String JVMURI1 = "JVM1";
	public String JVMURI2 = "JVM2";

	/**
	 * Uris des bus
	 */
	String cepBUS_URI1 ="BUS1";
	String cepBUS_URI2 = "BUS2";

	/**
	 * Uris des port entrant
	 */
	String eventEmissionIBP_URI1 = "EM1";
	String eventEmissionIBP_URI2 = "EM2";

	/**
	 * Uris pour les interconnexions des bus
	 * 
	 */
	String eventReceptionIBP_URI1 = "ibpr1";
	String eventReceptionOBP_URI1 = "obpr1";

	String eventReceptionIBP_URI2 = "ibpr2";
	String eventReceptionOBP_URI2 = "obpr2";
	
	String bus;



	private NewSmartCity nsc;

	DistributedCVM(String[] args, int xLayout, int yLayout) throws Exception {
		super(args, xLayout, yLayout);
		nsc = new NewSmartCity();

	}

	@Override
	public void initialise() throws Exception {
		String[] jvmURIs = this.configurationParameters.getJvmURIs();
		boolean JVMURI1_OK = false;
		boolean JVMURI2_OK = false;
		for (int i = 0; i < jvmURIs.length && (!JVMURI1_OK || !JVMURI2_OK); i++) {
			if (jvmURIs[i].equals(JVMURI1)) {
				JVMURI1_OK = true;
			} else if (jvmURIs[i].equals(JVMURI2)) {
				JVMURI2_OK = true;
			}
		}
		assert JVMURI1_OK && JVMURI2_OK;
		super.initialise();

	}

	// création des composants

	@Override
	public void instantiateAndPublish() throws Exception {

		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.LIFE_CYCLE);
		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.INTERFACES);
		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.PORTS);
		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.CONNECTING);
		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.CALLING);
		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.EXECUTOR_SERVICES);

		RuleBase samuRuleBase = new RuleBase();
		RuleBase fireRuleBase = new RuleBase();
		RuleBase trafficRuleBase = new RuleBase();

		RuleS1 ruleS1 = new RuleS1();
		RuleS2 ruleS2 = new RuleS2();
		RuleS3 ruleS3 = new RuleS3();
		RuleS5 ruleS5 = new RuleS5();
		RuleS6 ruleS6 = new RuleS6();

		samuRuleBase.addRule(ruleS1);
		samuRuleBase.addRule(ruleS2);
		samuRuleBase.addRule(ruleS3);
		samuRuleBase.addRule(ruleS5);
		samuRuleBase.addRule(ruleS6);

		RuleF1 ruleF1 = new RuleF1();
		RuleF2 ruleF2 = new RuleF2();
		RuleF3 ruleF3 = new RuleF3();

		fireRuleBase.addRule(ruleF1);
		fireRuleBase.addRule(ruleF2);
		fireRuleBase.addRule(ruleF3);

		if (AbstractCVM.getThisJVMURI().equals(JVMURI1)) {
			nsc.deploy();
			AbstractComponent.createComponent(CEPBus.class.getCanonicalName(),
					new Object[] { eventEmissionIBP_URI1, eventReceptionIBP_URI1, eventReceptionOBP_URI1 });

			Iterator<String> samuStationsIditerator = SmartCityDescriptor.createSAMUStationIdIterator();
			while (samuStationsIditerator.hasNext()) {
				String samuStationId = samuStationsIditerator.next();
				String notificationInboundPortURI = AbstractPort.generatePortURI();
				nsc.register(samuStationId, notificationInboundPortURI);
				AbstractComponent.createComponent(SAMUStationFacade.class.getCanonicalName(),
						new Object[] { samuStationId, notificationInboundPortURI,
								SmartCityDescriptor.getActionInboundPortURI(samuStationId) });

				AbstractComponent.createComponent(Correlator.class.getCanonicalName(),
						new Object[] { samuStationId, samuRuleBase });

			}

			Iterator<String> fireStationIdsIterator = SmartCityDescriptor.createFireStationIdIterator();
			while (fireStationIdsIterator.hasNext()) {
				String fireStationId = fireStationIdsIterator.next();
				String notificationInboundPortURI = AbstractPort.generatePortURI();
				nsc.register(fireStationId, notificationInboundPortURI);
				AbstractComponent.createComponent(FireStationFacade.class.getCanonicalName(),
						new Object[] { fireStationId, notificationInboundPortURI,
								SmartCityDescriptor.getActionInboundPortURI(fireStationId) });


			}

			Iterator<IntersectionPosition> trafficLightsIterator = SmartCityDescriptor
					.createTrafficLightPositionIterator();
			while (trafficLightsIterator.hasNext()) {
				IntersectionPosition p = trafficLightsIterator.next();
				String notificationInboundPortURI = AbstractPort.generatePortURI();

				nsc.register(p.toString(), notificationInboundPortURI);
				AbstractComponent.createComponent(TrafficLightFacade.class.getCanonicalName(),
						new Object[] { p, notificationInboundPortURI, SmartCityDescriptor.getActionInboundPortURI(p) });

			}

		} else if (AbstractCVM.getThisJVMURI().equals(JVMURI2)) {
			AbstractComponent.createComponent(CEPBus.class.getCanonicalName(),
					new Object[] { eventEmissionIBP_URI2, eventReceptionIBP_URI2, eventReceptionOBP_URI2 });

			Iterator<String> fireStationIdsIterator = SmartCityDescriptor.createFireStationIdIterator();
			while (fireStationIdsIterator.hasNext()) {
				String fireStationId = fireStationIdsIterator.next();
				AbstractComponent.createComponent(Correlator.class.getCanonicalName(),
						new Object[] { fireStationId, fireRuleBase });
			}
			Iterator<IntersectionPosition> trafficLightsIterator = SmartCityDescriptor
					.createTrafficLightPositionIterator();
			while (trafficLightsIterator.hasNext()) {
				IntersectionPosition p = trafficLightsIterator.next();
				AbstractComponent.createComponent(Correlator.class.getCanonicalName(),
						new Object[] { p.toString(), trafficRuleBase });
			}

		}
	}

	
	@Override
	public void interconnect() throws Exception {
		if (AbstractCVM.getThisJVMURI().equals(JVMURI1)) {
			this.doPortConnection(cepBUS_URI1, this.eventReceptionOBP_URI1, this.eventEmissionIBP_URI2, EventReceptionConnector.class.getCanonicalName());
		}else if (AbstractCVM.getThisJVMURI().equals(JVMURI1)) {
			this.doPortConnection(cepBUS_URI2, this.eventReceptionOBP_URI2, this.eventEmissionIBP_URI1, EventReceptionConnector.class.getCanonicalName());

		}

	}

	@Override
	public void shutdown() throws Exception {
		super.shutdown();
	}

	public static void main(String[] args) {
		try {
			simulatedStartTime = LocalTime.of(12, 0);
			simulatedEndTime = LocalTime.of(12, 0).plusMinutes(30L);
			DistributedCVM d = new DistributedCVM(args, 2, 5);
			d.startStandardLifeCycle(180001 + START_DELAY);
			Thread.sleep(10000L);
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
