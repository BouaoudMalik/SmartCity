package com.components;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.cps.smartcity.SmartCityDescriptor;
import fr.sorbonne_u.cps.smartcity.connections.FireStationActionConnector;
import fr.sorbonne_u.cps.smartcity.connections.FireStationActionOutboundPort;
import fr.sorbonne_u.cps.smartcity.connections.FireStationNotificationInboundPort;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
import fr.sorbonne_u.cps.smartcity.grid.IntersectionPosition;
import fr.sorbonne_u.cps.smartcity.interfaces.FireStationNotificationCI;
import fr.sorbonne_u.cps.smartcity.interfaces.FireStationNotificationImplI;
import fr.sorbonne_u.cps.smartcity.interfaces.FireStationActionCI;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfFire;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfFirefightingResource;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfTrafficLightPriority;

import java.io.Serializable;
import java.time.LocalTime;

import com.actions.FireActions;
import com.components.interfaces.CEPBusManagementCI;
import com.connections.connector.CEPBusConnector;
import com.connections.obp.CEPBusOutboundPort;
import com.connections.obp.EventEmissionOutboundPort;
import com.event.abstracts.AtomicEvent;
import com.event.fire.AllLaddersInIntervention;
import com.event.fire.AllTrucksInIntervention;
import com.event.fire.AvailableLadders;
import com.event.fire.AvailableTrucks;
import com.event.fire.EndOfFire;
import com.event.fire.FireEvent;
import com.event.interfaces.EventI;
import com.interfaces.ActionI;
import com.interfaces.ResponseI;
import com.plugins.FireActionExecutionPlugin;
import com.rule.correlator.FireCorrelatorState;
import com.plugins.EmitterPlugin;

@OfferedInterfaces(offered = { FireStationNotificationCI.class })
@RequiredInterfaces(required = { FireStationActionCI.class, CEPBusManagementCI.class })
public class FireStationFacade extends AbstractComponent implements FireStationNotificationImplI {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** set to true if actions must be tested. */
	protected static final boolean TEST_ACTIONS = false;

	/** identifier of the corresponding fire station. */
	public String stationId;
	/** URI of the action inbound port. */
	protected String actionInboundPortURI;
	/** notification inbound port. */
	protected FireStationNotificationInboundPort notificationIBP;
	/** action outbound port. */
	protected FireStationActionOutboundPort actionOBP;

	protected EventEmissionOutboundPort emissionOBP;
	protected CEPBusOutboundPort cepBusOBP;

	protected String cepBusInboundPortURI = AbstractPort.generatePortURI();
	protected String actionExecutionInboundPortURI = AbstractPort.generatePortURI();

	protected FireActionExecutionPlugin actionExecutionPlugin;

	protected EmitterPlugin emissionPlugin;

	public String ACTIONEXECUTION_PLUGIN_URI = "action-plugin-uri-Fire";

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a fire station facade component
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	{@code BasicSimSmartCityDescriptor.isValidFireStationId(stationId)}
	 * pre	{@code notificationInboundPortURI != null && !notificationInboundPortURI.isEmpty()}
	 * pre	{@code actionInboundPortURI != null && !actionInboundPortURI.isEmpty()}
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param stationId                  identifier of the corresponding fire
	 *                                   station.
	 * @param notificationInboundPortURI URI of the notification inbound port to be
	 *                                   used by this facade component.
	 * @param actionInboundPortURI       URI of the action inbound port of the proxy
	 *                                   component.
	 * @throws Exception
	 */
	protected FireStationFacade(String stationId, String notificationInboundPortURI, String actionInboundPortURI)
			throws Exception {
		super(2, 1);

		assert SmartCityDescriptor.isValidFireStationId(stationId);
		assert notificationInboundPortURI != null && !notificationInboundPortURI.isEmpty();
		assert actionInboundPortURI != null && !actionInboundPortURI.isEmpty();

		this.stationId = stationId;
		this.actionInboundPortURI = actionInboundPortURI;
		this.notificationIBP = new FireStationNotificationInboundPort(notificationInboundPortURI, this);
		this.notificationIBP.publishPort();
		this.actionOBP = new FireStationActionOutboundPort(this);
		this.actionOBP.publishPort();

		this.cepBusOBP = new CEPBusOutboundPort(this);
		this.cepBusOBP.publishPort();

		// Plugin Emission
		this.emissionPlugin = new EmitterPlugin();
		this.emissionPlugin.setPluginURI(EmitterPlugin.eventEmitterPluginURI);
		this.installPlugin(this.emissionPlugin);

		// Plugin ActionExecution
		actionExecutionPlugin = new FireActionExecutionPlugin();
		actionExecutionPlugin.setPluginURI(ACTIONEXECUTION_PLUGIN_URI + AbstractPort.generatePortURI());
		this.installPlugin(actionExecutionPlugin);

		this.getTracer().setTitle("FireStationFacade " + this.stationId);
		this.getTracer().setRelativePosition(1, 1);
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
					FireStationActionConnector.class.getCanonicalName());
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
		this.cepBusOBP.registerExecutor(this.stationId, this.actionExecutionPlugin.getActionPluginURI());
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
			this.notificationIBP.unpublishPort();
			this.actionOBP.unpublishPort();
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
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.FireStationNotificationImplI#fireAlarm(fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition,
	 *      java.time.LocalTime, fr.sorbonne_u.cps.smartcity.interfaces.TypeOfFire)
	 */
	@Override
	public void fireAlarm(AbsolutePosition position, LocalTime occurrence, TypeOfFire type) throws Exception {
		EventI fireEvent = new FireEvent();
		((AtomicEvent) fireEvent).putProperty("type", type);
		((AtomicEvent) fireEvent).putProperty("position", position);
		((AtomicEvent) fireEvent).putProperty("id", stationId);
		((AtomicEvent) fireEvent).setTime(occurrence);

		this.emissionPlugin.sendEvent(stationId, fireEvent);

		this.traceMessage(
				"Fire alarm of type " + type + " received from position " + position + " at " + occurrence + "\n");
	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.FireStationNotificationImplI#endOfFire(fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition,
	 *      java.time.LocalTime)
	 */
	@Override
	public void endOfFire(AbsolutePosition position, LocalTime occurrence) throws Exception {
		EndOfFire end = new EndOfFire();
		end.putProperty("position", position);
		end.putProperty("id", stationId);
		end.setTime(occurrence);

		this.emissionPlugin.sendEvent(stationId, end);

		this.traceMessage("End of fire received from position " + position + " at " + occurrence + "\n");
	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.FireStationNotificationImplI#requestPriority(fr.sorbonne_u.cps.smartcity.grid.IntersectionPosition,
	 *      fr.sorbonne_u.cps.smartcity.interfaces.TypeOfTrafficLightPriority,
	 *      java.lang.String, fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition,
	 *      java.time.LocalTime)
	 */
	@Override
	public void requestPriority(IntersectionPosition intersection, TypeOfTrafficLightPriority priority,
			String vehicleId, AbsolutePosition destination, LocalTime occurrence) throws Exception {
		this.traceMessage("priority " + priority + " requested for vehicle " + vehicleId + " at intersection "
				+ intersection + " towards " + destination + " at " + occurrence + "\n");
	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.FireStationNotificationImplI#atDestination(java.lang.String,
	 *      java.time.LocalTime)
	 */
	@Override
	public void atDestination(String vehicleId, LocalTime occurrence) throws Exception {
		this.traceMessage("Vehicle " + vehicleId + " has arrived at destination\n");
	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.FireStationNotificationImplI#atStation(java.lang.String,
	 *      java.time.LocalTime)
	 */
	@Override
	public void atStation(String vehicleId, LocalTime occurrence) throws Exception {
		this.traceMessage("Vehicle " + vehicleId + " has arrived at station\n");
	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.FireStationNotificationImplI#notifyNoStandardTruckAvailable(java.time.LocalTime)
	 */
	@Override
	public void notifyNoStandardTruckAvailable(LocalTime occurrence) throws Exception {
		AllTrucksInIntervention noAvailableTrucks = new AllTrucksInIntervention();
		noAvailableTrucks.putProperty("id", stationId);
		noAvailableTrucks.setTime(occurrence);

		FireCorrelatorState.truckAvailable = false;
		
		this.emissionPlugin.sendEvent(stationId, noAvailableTrucks);

		this.traceMessage("No standard truck available received at " + occurrence + "\n");
	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.FireStationNotificationImplI#notifyStandardTrucksAvailable(java.time.LocalTime)
	 */
	@Override
	public void notifyStandardTrucksAvailable(LocalTime occurrence) throws Exception {
		AvailableTrucks availableTrucks = new AvailableTrucks();
		availableTrucks.putProperty("id", stationId);
		availableTrucks.setTime(occurrence);

		FireCorrelatorState.truckAvailable = true;
		
		this.emissionPlugin.sendEvent(stationId, availableTrucks);

		this.traceMessage("Standard trucks available received at " + occurrence + "\n");
	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.FireStationNotificationImplI#notifyNoHighLadderTruckAvailable(java.time.LocalTime)
	 */
	@Override
	public void notifyNoHighLadderTruckAvailable(LocalTime occurrence) throws Exception {
		AllLaddersInIntervention noAvailableLadders = new AllLaddersInIntervention();
		noAvailableLadders.putProperty("id", stationId);
		noAvailableLadders.setTime(occurrence);

		FireCorrelatorState.ladderAvailable = false;
		
		this.emissionPlugin.sendEvent(stationId, noAvailableLadders);

		this.traceMessage("No high ladder truck available received at " + occurrence + "\n");
	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.FireStationNotificationImplI#notifyHighLadderTrucksAvailable(java.time.LocalTime)
	 */
	@Override
	public void notifyHighLadderTrucksAvailable(LocalTime occurrence) throws Exception {
		AvailableLadders availableLadders = new AvailableLadders();
		availableLadders.putProperty("id", stationId);
		availableLadders.setTime(occurrence);

		FireCorrelatorState.ladderAvailable = true;
		
		this.emissionPlugin.sendEvent(stationId, availableLadders);

		this.traceMessage("High ladder trucks available received at " + occurrence + "\n");
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
		if (params.length == 2) {
			this.actionOBP.triggerFirstAlarm((AbsolutePosition) params[0], (TypeOfFirefightingResource) params[1]);
			this.traceMessage("-- Trigger first alarm\n");
		}

		if (params.length == 1) {
			if (a == FireActions.triggerSecondAlarm) {
				this.actionOBP.triggerSecondAlarm((AbsolutePosition) params[0]);
				this.traceMessage("-- Trigger second alarm\n");
			}

			if (a == FireActions.triggerGeneralAlarm) {
				this.actionOBP.triggerGeneralAlarm((AbsolutePosition) params[0]);
				this.traceMessage("-- Trigger general alarm\n");
			}
		}

		return null;
	}

}
