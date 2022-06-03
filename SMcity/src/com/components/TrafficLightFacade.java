package com.components;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.cps.smartcity.SmartCityDescriptor;
import fr.sorbonne_u.cps.smartcity.connections.TrafficLightActionConnector;
import fr.sorbonne_u.cps.smartcity.connections.TrafficLightActionOutboundPort;
import fr.sorbonne_u.cps.smartcity.connections.TrafficLightNotificationInboundPort;
import fr.sorbonne_u.cps.smartcity.grid.Direction;
import fr.sorbonne_u.cps.smartcity.grid.IntersectionPosition;
import fr.sorbonne_u.cps.smartcity.interfaces.TrafficLightActionCI;
import fr.sorbonne_u.cps.smartcity.interfaces.TrafficLightNotificationCI;
import fr.sorbonne_u.cps.smartcity.interfaces.TrafficLightNotificationImplI;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfTrafficLightPriority;
import fr.sorbonne_u.cps.smartcity.utils.TimeManager;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

import com.components.interfaces.CEPBusManagementCI;
import com.connections.connector.CEPBusConnector;
import com.connections.obp.CEPBusOutboundPort;
import com.connections.obp.EventEmissionOutboundPort;
import com.event.abstracts.AtomicEvent;
import com.event.interfaces.EventI;
import com.event.traffic.PriorityEvent;
import com.interfaces.ActionI;
import com.interfaces.ResponseI;
import com.plugins.EmitterPlugin;
import com.plugins.TrafficActionExecutionPlugin;

@OfferedInterfaces(offered = { TrafficLightNotificationCI.class })
@RequiredInterfaces(required = { TrafficLightActionCI.class, CEPBusManagementCI.class })
public class TrafficLightFacade extends AbstractComponent implements TrafficLightNotificationImplI {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** set to true if actions must be tested. */
	protected final boolean TEST_ACTIONS = false;

	/** position of the traffic light. */
	protected IntersectionPosition position;
	/** URI of the action inbound port. */
	protected String actionInboundPortURI;
	/** notification inbound port. */
	protected TrafficLightNotificationInboundPort notificationIBP;
	/** action outbound port. */
	protected TrafficLightActionOutboundPort actionOBP;

	protected EventEmissionOutboundPort emissionOBP;
	protected CEPBusOutboundPort cepBusOBP;

	protected EmitterPlugin emissionPlugin;

	protected String cepBusInboundPortURI = AbstractPort.generatePortURI();
	protected String actionExecutionInboundPortURI = AbstractPort.generatePortURI();

	protected TrafficActionExecutionPlugin actionExecutionPlugin;
	public String ACTIONEXECUTION_PLUGIN_URI = "action-plugin-uri-trafic";

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	protected TrafficLightFacade(IntersectionPosition position, String notificationInboundPortURI,
			String actionInboundPortURI) throws Exception {
		super(2, 1);

		assert SmartCityDescriptor.isValidTrafficLight(position);
		assert notificationInboundPortURI != null && !notificationInboundPortURI.isEmpty();
		assert actionInboundPortURI != null && !actionInboundPortURI.isEmpty();

		this.position = position;
		this.actionInboundPortURI = actionInboundPortURI;
		this.notificationIBP = new TrafficLightNotificationInboundPort(notificationInboundPortURI, this);
		this.notificationIBP.publishPort();
		this.actionOBP = new TrafficLightActionOutboundPort(this);
		this.actionOBP.publishPort();

		this.cepBusOBP = new CEPBusOutboundPort(this);
		this.cepBusOBP.publishPort();

		// Plugin Emission
		this.emissionPlugin = new EmitterPlugin();
		this.emissionPlugin.setPluginURI(EmitterPlugin.eventEmitterPluginURI);
		this.installPlugin(this.emissionPlugin);

		// Plugin ActionExecution
		actionExecutionPlugin = new TrafficActionExecutionPlugin();
		actionExecutionPlugin.setPluginURI(ACTIONEXECUTION_PLUGIN_URI + AbstractPort.generatePortURI());
		this.installPlugin(actionExecutionPlugin);

		this.getTracer().setTitle("TrafficLightFacade " + this.position);
		this.getTracer().setRelativePosition(1, 2);
		this.toggleTracing();
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public synchronized void start() throws ComponentStartException {
		super.start();

		try {
			this.doPortConnection(this.actionOBP.getPortURI(), this.actionInboundPortURI,
					TrafficLightActionConnector.class.getCanonicalName());
			this.doPortConnection(this.cepBusOBP.getPortURI(), CEPBus.cepBusIBP_URI,
					CEPBusConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public synchronized void execute() throws Exception {
		super.execute();

		this.cepBusOBP.registerExecutor(this.position.toString(), this.actionExecutionPlugin.getActionPluginURI());
		Thread.sleep(2000);

		if (TEST_ACTIONS) {
			if (this.position.equals(new IntersectionPosition(1.0, 1.0))) {
				LocalTime t1 = TimeManager.get().getSimulatedStartTime().plusSeconds(10);
				LocalTime t2 = TimeManager.get().getSimulatedStartTime().plusSeconds(15);
				LocalTime t3 = TimeManager.get().getSimulatedStartTime().plusSeconds(20);
				LocalTime t4 = TimeManager.get().getSimulatedStartTime().plusSeconds(25);
				LocalTime t5 = TimeManager.get().getSimulatedStartTime().plusSeconds(30);
				LocalTime t6 = TimeManager.get().getSimulatedStartTime().plusSeconds(40);

				long t1NanoDelay = TimeManager.get().localTime2nanoDelay(t1);
				long t2NanoDelay = TimeManager.get().localTime2nanoDelay(t2);
				long t3NanoDelay = TimeManager.get().localTime2nanoDelay(t3);
				long t4NanoDelay = TimeManager.get().localTime2nanoDelay(t4);
				long t5NanoDelay = TimeManager.get().localTime2nanoDelay(t5);
				long t6NanoDelay = TimeManager.get().localTime2nanoDelay(t6);

				this.scheduleTask((o -> {
					try {
						this.traceMessage("Change priority to NORTH_SOUTH at " + t1 + "\n");
						this.actionOBP.changePriority(TypeOfTrafficLightPriority.NORTH_SOUTH);
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}), t1NanoDelay, TimeUnit.NANOSECONDS);
				this.scheduleTask((o -> {
					try {
						this.traceMessage("Return to normal mode at " + t2 + "\n");
						this.actionOBP.returnToNormalMode();
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}), t2NanoDelay, TimeUnit.NANOSECONDS);
				this.scheduleTask((o -> {
					try {
						this.traceMessage("Change priority to EAST_WEST at " + t3 + "\n");
						this.actionOBP.changePriority(TypeOfTrafficLightPriority.EAST_WEST);
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}), t3NanoDelay, TimeUnit.NANOSECONDS);
				this.scheduleTask((o -> {
					try {
						this.traceMessage("Return to normal mode at " + t4 + "\n");
						this.actionOBP.returnToNormalMode();
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}), t4NanoDelay, TimeUnit.NANOSECONDS);
				this.scheduleTask((o -> {
					try {
						this.traceMessage("Change priority to EMERGENCY at " + t5 + "\n");
						this.actionOBP.changePriority(TypeOfTrafficLightPriority.EMERGENCY);
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}), t5NanoDelay, TimeUnit.NANOSECONDS);
				this.scheduleTask((o -> {
					try {
						this.traceMessage("Return to normal mode at " + t6 + "\n");
						this.actionOBP.returnToNormalMode();
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}), t6NanoDelay, TimeUnit.NANOSECONDS);
			}
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public synchronized void finalise() throws Exception {
		this.doPortDisconnection(this.actionOBP.getPortURI());
		this.doPortDisconnection(this.cepBusOBP.getPortURI());

		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.actionOBP.unpublishPort();
			this.notificationIBP.unpublishPort();
			this.cepBusOBP.unpublishPort();

		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}

		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.TrafficLightNotificationImplI#vehiclePassage(java.lang.String,
	 *      fr.sorbonne_u.cps.smartcity.grid.Direction, java.time.LocalTime)
	 */
	@Override
	public void vehiclePassage(String vehicleId, Direction d, LocalTime occurrence) throws Exception {
		EventI trafficLightEvent = new PriorityEvent();
		((AtomicEvent) trafficLightEvent).putProperty("Direction", d);
		((AtomicEvent) trafficLightEvent).putProperty("id", vehicleId);
		((AtomicEvent) trafficLightEvent).setTime(occurrence);

		// this.emissionPlugin.sendEvent(this.position.toString(), trafficLightEvent);

		this.traceMessage("Traffic light at " + this.position + " receives the notification of the passage of "
				+ vehicleId + (d != null ? " in the direction of " + d : "") + " at " + occurrence + "\n");
	}

	/**
	 * Executing the action a
	 * 
	 * @param a      the action
	 * @param params the parameters of the action
	 * @return a serializable result
	 * @throws Exception
	 */
	public ResponseI executeAction(ActionI a, Serializable[] params) throws Exception {
		if (params.length == 1) {
			this.actionOBP.changePriority((TypeOfTrafficLightPriority) params[0]);
			this.traceMessage("\n-- Change priority");
		}

		if (params.length == 0) {
			this.actionOBP.returnToNormalMode();
			this.traceMessage("\n-- Return to normal mode");
		}

		return null;
	}

}
