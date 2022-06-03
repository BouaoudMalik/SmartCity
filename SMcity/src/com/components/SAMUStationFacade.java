package com.components;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.cps.smartcity.connections.SAMUNotificationInboundPort;
import fr.sorbonne_u.cps.smartcity.connections.SAMUActionConnector;
import fr.sorbonne_u.cps.smartcity.SmartCityDescriptor;
import fr.sorbonne_u.cps.smartcity.connections.SAMUActionOutboundPort;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
import fr.sorbonne_u.cps.smartcity.grid.IntersectionPosition;
import fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationCI;
import fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationImplI;
import fr.sorbonne_u.cps.smartcity.interfaces.SAMUActionCI;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfHealthAlarm;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfSAMURessources;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfTrafficLightPriority;

import java.io.Serializable;
import java.time.LocalTime;

import com.components.interfaces.CEPBusManagementCI;
import com.connections.connector.CEPBusConnector;
import com.connections.obp.CEPBusOutboundPort;
import com.connections.obp.EventEmissionOutboundPort;
import com.event.abstracts.AtomicEvent;
import com.event.interfaces.EventI;
import com.event.samu.AllAmbulancesInIntervention;
import com.event.samu.AllDoctorsInIntervention;
import com.event.samu.AvailableAmbulances;
import com.event.samu.AvailableDoctors;
import com.event.samu.HealthEvent;
import com.event.samu.ManualSignal;
import com.interfaces.ActionI;
import com.interfaces.ResponseI;
import com.plugins.SAMUActionExecutionPlugin;
import com.rule.correlator.HealthCorrelatorState;
import com.plugins.EmitterPlugin;

@OfferedInterfaces(offered = { SAMUNotificationCI.class })
@RequiredInterfaces(required = { SAMUActionCI.class, CEPBusManagementCI.class })
public class SAMUStationFacade extends AbstractComponent implements SAMUNotificationImplI {

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** set to true if actions must be tested. */
	protected static final boolean TEST_ACTIONS = true;

	public String stationId;
	/** URI of the action inbound port. */
	protected String actionInboundPortURI;
	/** notification inbound port. */
	protected SAMUNotificationInboundPort notificationIBP;
	/** action outbound port. */
	protected SAMUActionOutboundPort actionOBP;

	protected EventEmissionOutboundPort emissionOBP;
	protected CEPBusOutboundPort cepBusOBP;

	protected EmitterPlugin emissionPlugin;

	protected String cepBusInboundPortURI = AbstractPort.generatePortURI();
	protected String actionExecutionInboundPortURI = AbstractPort.generatePortURI();

	protected SAMUActionExecutionPlugin actionExecutionPlugin;
	public String ACTIONEXECUTION_PLUGIN_URI = "action-plugin-uri-Samu";

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a SAMU station facade component.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	{@code BasicSimSmartCityDescriptor.isValidSAMUStationId(stationId)}
	 * pre	{@code notificationInboundPortURI != null && !notificationInboundPortURI.isEmpty()}
	 * pre	{@code actionInboundPortURI != null && !actionInboundPortURI.isEmpty()}
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param stationId                  identifier of the corresponding SAMU
	 *                                   station.
	 * @param notificationInboundPortURI URI of the notification inbound port to be
	 *                                   used by this facade component.
	 * @param actionInboundPortURI       URI of the action inbound port of the proxy
	 *                                   component.
	 * @throws Exception
	 */
	protected SAMUStationFacade(String stationId, String notificationInboundPortURI, String actionInboundPortURI)
			throws Exception {
		super(2, 1);

		assert SmartCityDescriptor.isValidSAMUStationId(stationId);
		assert notificationInboundPortURI != null && !notificationInboundPortURI.isEmpty();
		assert actionInboundPortURI != null && !actionInboundPortURI.isEmpty();

		this.stationId = stationId;
		this.actionInboundPortURI = actionInboundPortURI;
		this.notificationIBP = new SAMUNotificationInboundPort(notificationInboundPortURI, this);
		this.notificationIBP.publishPort();
		this.actionOBP = new SAMUActionOutboundPort(this);
		this.actionOBP.publishPort();

		this.cepBusOBP = new CEPBusOutboundPort(this);
		this.cepBusOBP.publishPort();

		// Plugin Emission
		this.emissionPlugin = new EmitterPlugin();
		this.emissionPlugin.setPluginURI(EmitterPlugin.eventEmitterPluginURI);
		this.installPlugin(this.emissionPlugin);

		// Plugin ActionExecution
		actionExecutionPlugin = new SAMUActionExecutionPlugin();
		actionExecutionPlugin.setPluginURI(ACTIONEXECUTION_PLUGIN_URI + AbstractPort.generatePortURI());
		this.installPlugin(actionExecutionPlugin);

		this.getTracer().setTitle("SAMUStationFacade " + this.stationId);
		this.getTracer().setRelativePosition(1, 0);
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
					SAMUActionConnector.class.getCanonicalName());
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
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationImplI#healthAlarm(fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition,
	 *      fr.sorbonne_u.cps.smartcity.interfaces.TypeOfHealthAlarm,
	 *      java.time.LocalTime)
	 */
	@Override
	public void healthAlarm(AbsolutePosition position, TypeOfHealthAlarm type, LocalTime occurrence) throws Exception {
		EventI healthEventUrgence = new HealthEvent();
		((AtomicEvent) healthEventUrgence).putProperty("type", type);
		((AtomicEvent) healthEventUrgence).putProperty("position", position);
		((AtomicEvent) healthEventUrgence).putProperty("id", stationId);
		((AtomicEvent) healthEventUrgence).setTime(occurrence);

		this.emissionPlugin.sendEvent(stationId, healthEventUrgence);

		this.traceMessage("Health notification of type " + type + " at position " + position + " received at "
				+ occurrence + "\n");
	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationImplI#trackingAlarm(fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition,
	 *      java.lang.String, java.time.LocalTime)
	 */
	@Override
	public void trackingAlarm(AbsolutePosition position, String personId, LocalTime occurrence) throws Exception {
		EventI healthEventUrgence = new HealthEvent();
		((AtomicEvent) healthEventUrgence).putProperty("type", TypeOfHealthAlarm.TRACKING);
		((AtomicEvent) healthEventUrgence).putProperty("position", position);
		((AtomicEvent) healthEventUrgence).putProperty("personId", personId);
		((AtomicEvent) healthEventUrgence).putProperty("id", stationId);
		((AtomicEvent) healthEventUrgence).setTime(occurrence);

		this.emissionPlugin.sendEvent(stationId, healthEventUrgence);

		this.traceMessage("Health notification of type tracking for " + personId + " at position " + position
				+ " received at " + occurrence + "\n");
	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationImplI#manualSignal(java.lang.String,
	 *      java.time.LocalTime)
	 */
	@Override
	public void manualSignal(String personId, LocalTime occurrence) throws Exception {
		ManualSignal manualSignal = new ManualSignal();
		manualSignal.putProperty("personId", personId);
		manualSignal.putProperty("id", stationId);
		manualSignal.putProperty("type", "Manual signal");
		manualSignal.setTime(occurrence);

		this.emissionPlugin.sendEvent(stationId, manualSignal);

		this.traceMessage("Manual signal emitted by " + personId + " received at " + occurrence + "\n");
	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationImplI#requestPriority(fr.sorbonne_u.cps.smartcity.grid.IntersectionPosition,
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
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationImplI#atDestination(java.lang.String,
	 *      java.time.LocalTime)
	 */
	@Override
	public void atDestination(String vehicleId, LocalTime occurrence) throws Exception {
		this.traceMessage("Vehicle " + vehicleId + " has arrived at destination at " + occurrence + "\n");
	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationImplI#atStation(java.lang.String,
	 *      java.time.LocalTime)
	 */
	@Override
	public void atStation(String vehicleId, LocalTime occurrence) throws Exception {
		this.traceMessage("Vehicle " + vehicleId + " has arrived at station at " + occurrence + "\n");
	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationImplI#notifyMedicsAvailable(java.time.LocalTime)
	 */

	@Override
	public void notifyMedicsAvailable(LocalTime occurrence) throws Exception {
		assert occurrence != null;

		AvailableDoctors availableDoctors = new AvailableDoctors();
		availableDoctors.putProperty("id", stationId);
		availableDoctors.setTime(occurrence);

		HealthCorrelatorState.doctorAvailable = true;

		this.emissionPlugin.sendEvent(stationId, availableDoctors);

		this.traceMessage("Notification that medics are available received at " + occurrence + "\n");
	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationImplI#notifyNoMedicAvailable(java.time.LocalTime)
	 */
	@Override
	public void notifyNoMedicAvailable(LocalTime occurrence) throws Exception {
		assert occurrence != null;

		AllDoctorsInIntervention noAvailableDoctors = new AllDoctorsInIntervention();
		noAvailableDoctors.putProperty("id", stationId);
		noAvailableDoctors.setTime(occurrence);

		HealthCorrelatorState.doctorAvailable = false;

		this.emissionPlugin.sendEvent(stationId, noAvailableDoctors);

		this.traceMessage("Notification that no medic are available received at " + occurrence + "\n");
	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationImplI#notifyAmbulancesAvailable(java.time.LocalTime)
	 */
	@Override
	public void notifyAmbulancesAvailable(LocalTime occurrence) throws Exception {
		assert occurrence != null;

		AvailableAmbulances availableAmbulances = new AvailableAmbulances();
		availableAmbulances.putProperty("id", stationId);
		availableAmbulances.setTime(occurrence);

		HealthCorrelatorState.ambulanceAvailable = true;

		this.emissionPlugin.sendEvent(stationId, availableAmbulances);

		this.traceMessage("Notification that ambulances are available received at " + occurrence + "\n");
	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationImplI#notifyNoAmbulanceAvailable(java.time.LocalTime)
	 */
	@Override
	public void notifyNoAmbulanceAvailable(LocalTime occurrence) throws Exception {
		assert occurrence != null;

		AllAmbulancesInIntervention noAvailableAmbulances = new AllAmbulancesInIntervention();
		noAvailableAmbulances.putProperty("id", stationId);
		noAvailableAmbulances.setTime(occurrence);

		HealthCorrelatorState.ambulanceAvailable = false;

		this.emissionPlugin.sendEvent(stationId, noAvailableAmbulances);

		this.traceMessage("Notification that no ambulance are available received at " + occurrence + "\n");
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
			this.actionOBP.triggerIntervention((AbsolutePosition) params[0], null, (TypeOfSAMURessources) params[1]);
			this.traceMessage("-- Trigger intervention\n");
		}

		if (params.length == 3) {
			this.actionOBP.triggerIntervention((AbsolutePosition) params[0], (String) params[1],
					(TypeOfSAMURessources) params[2]);
			this.traceMessage("-- Trigger intervention\n");
		}

		return null;
	}

}
