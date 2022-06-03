package com.components;

import java.io.Serializable;
import java.util.concurrent.Semaphore;

import com.components.interfaces.ActionExecutionCI;
import com.components.interfaces.CEPBusManagementCI;
import com.components.interfaces.EventEmissionCI;
import com.components.interfaces.EventReceptionCI;
import com.connections.connector.ActionExecutionConnector;
import com.connections.connector.CEPBusConnector;
import com.connections.connector.EventEmissionConnector;
import com.connections.ibp.EventReceptionInboundPort;
import com.connections.obp.ActionExecutionOutboundPort;
import com.connections.obp.CEPBusOutboundPort;
import com.connections.obp.EventEmissionOutboundPort;
import com.event.EventBase;
import com.event.interfaces.EventI;
import com.interfaces.ActionI;
import com.rule.RuleBase;
import com.rule.correlator.CorrelatorState;
import com.rule.interfaces.CorrelatorStateI;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;

@OfferedInterfaces(offered = { EventReceptionCI.class })
@RequiredInterfaces(required = { EventEmissionCI.class, ActionExecutionCI.class, CEPBusManagementCI.class })
public class Correlator extends AbstractComponent {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	protected String correlator_URI = AbstractPort.generatePortURI();
	protected String eventEmissionIBP_URI = AbstractPort.generatePortURI();
	protected String cepBusIBP_URI = AbstractPort.generatePortURI();
	protected String eventReceptionIBP_URI = AbstractPort.generatePortURI();
	protected String actionExecutionOBP_URI = AbstractPort.generatePortURI();
	protected String emitterURI;

	protected EventReceptionInboundPort eventReceptionInboundPort;
	protected EventEmissionOutboundPort eventEmissionOutboundPort;
	protected ActionExecutionOutboundPort actionExecutionOutboundPort;
	protected CEPBusOutboundPort cepBusOutboundPort;

	protected EventBase eb;
	protected RuleBase ruleBase;
	protected CorrelatorStateI correlatorState;

	protected Semaphore lock = new Semaphore(1);

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	protected Correlator(String emitterURI, RuleBase ruleBase) throws Exception {
		super(1, 0);

		this.emitterURI = emitterURI;

		this.eventReceptionInboundPort = new EventReceptionInboundPort(eventReceptionIBP_URI, this);
		this.eventReceptionInboundPort.publishPort();

		this.eventEmissionOutboundPort = new EventEmissionOutboundPort(this);
		this.eventEmissionOutboundPort.publishPort();

		this.actionExecutionOutboundPort = new ActionExecutionOutboundPort(this);
		this.actionExecutionOutboundPort.publishPort();

		this.cepBusOutboundPort = new CEPBusOutboundPort(this);
		this.cepBusOutboundPort.publishPort();

		this.eb = new EventBase();
		this.ruleBase = ruleBase;
		this.correlatorState = new CorrelatorState(this.actionExecutionOutboundPort, this.eventEmissionOutboundPort);
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	@Override
	public synchronized void start() throws ComponentStartException {
		super.start();

		try {
			this.doPortConnection(this.cepBusOutboundPort.getPortURI(), CEPBus.cepBusIBP_URI,
					CEPBusConnector.class.getCanonicalName());

		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
	}

	@Override
	public synchronized void execute() throws Exception {
		super.execute();

		String cepBusURI = this.cepBusOutboundPort.registerCorrelator(correlator_URI,
				this.eventReceptionInboundPort.getPortURI());

		this.doPortConnection(this.eventEmissionOutboundPort.getPortURI(), cepBusURI,
				EventEmissionConnector.class.getCanonicalName());

		this.cepBusOutboundPort.subscribe(this.correlator_URI, this.emitterURI);
	}

	@Override
	public synchronized void finalise() throws Exception {
		this.doPortDisconnection(this.eventEmissionOutboundPort.getPortURI());
		this.doPortDisconnection(this.cepBusOutboundPort.getPortURI());

		super.finalise();
	}

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.eventReceptionInboundPort.unpublishPort();
			this.eventEmissionOutboundPort.unpublishPort();
			this.actionExecutionOutboundPort.unpublishPort();
			this.cepBusOutboundPort.unpublishPort();

		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}

		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	/**
	 * Receive an event
	 * 
	 * @param emitterURI emitted event uri
	 * @param e          the event
	 * @throws Exception
	 */
	public void receiveEvent(String emitterURI, EventI e) throws Exception {
		this.runTask(o -> {
			try {
				this.eb.addEvent(e);
				this.traceMessage("The correlator added the event to the event base");
				boolean response;

				String actionFacadeIBP_URI = this.cepBusOutboundPort.getExecutorInboundPortURI(emitterURI);
				synchronized (this) {

					if (!(this.isPortConnected(this.actionExecutionOutboundPort.getPortURI()))) {
						this.doPortConnection(this.actionExecutionOutboundPort.getPortURI(), actionFacadeIBP_URI,
								ActionExecutionConnector.class.getCanonicalName());
					}

					this.lock.acquire();

					response = this.ruleBase.fireFirstOn(eb, this.correlatorState);

					this.lock.release();

					this.traceMessage("******-----------------------------******");
					this.traceMessage("Rules has been APPLIED");

					if (response) {
						this.traceMessage("******-----------------------------******");
						this.traceMessage("Action will be sent");
					}

					this.doPortDisconnection(this.actionExecutionOutboundPort.getPortURI());
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		});
	}

	/**
	 * Receive an array of events
	 * 
	 * @param emitterURI emitted event uri
	 * @param events     the array of events
	 * @throws Exception
	 */
	public void receiveEvents(String emitterURI, EventI[] events) throws Exception {
		for (int i = 0; i < events.length; i++) {
			this.eb.addEvent(events[i]);
			this.traceMessage("The correlator added the event to the base ");
		}

		this.runTask(o -> {
			try {
				boolean response;
				for (int i = 0; i < this.eb.numberOfEvents(); i++) {
					String actionFacadeIBP_URI = this.cepBusOutboundPort.getExecutorInboundPortURI(emitterURI);

					this.doPortConnection(this.actionExecutionOutboundPort.getPortURI(), actionFacadeIBP_URI,
							ActionExecutionConnector.class.getCanonicalName());

					this.lock.acquire();

					response = this.ruleBase.fireFirstOn(eb, this.correlatorState);

					this.lock.release();

					if (response) {
						this.traceMessage("******-----------------------------******");
						this.traceMessage("Action will be sent");
					}

					this.doPortDisconnection(this.actionExecutionOutboundPort.getPortURI());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Execute the action
	 * 
	 * @param object the action
	 * @param params the parameters of the action
	 * @throws Exception
	 */
	public void executeAction(ActionI object, Serializable[] params) throws Exception {
		this.actionExecutionOutboundPort.executeAction(object, params);
	}

}
