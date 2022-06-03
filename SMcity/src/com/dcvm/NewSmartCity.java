package com.dcvm;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.cps.smartcity.SmartCityDescriptor;
import fr.sorbonne_u.cps.smartcity.components.FireStationProxy;
import fr.sorbonne_u.cps.smartcity.components.SAMUStationProxy;
import fr.sorbonne_u.cps.smartcity.components.TrafficLightProxy;
import fr.sorbonne_u.cps.smartcity.grid.IntersectionPosition;
import fr.sorbonne_u.cps.smartcity.traffic.components.TrafficLightsSimulator;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

// -----------------------------------------------------------------------------
/**
 * The class <code>AbstractSmartCityCVM</code>
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>
 * <strong>Invariant</strong>
 * </p>
 * 
 * <pre>
 * invariant		true
 * </pre>
 * 
 * <p>
 * Created on : 2022-03-02
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class NewSmartCity extends AbstractCVM {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	public static final boolean DEBUG = false;
	/**
	 * delay before the beginning of the smart city simulation after launching the
	 * program.
	 */
	protected static long START_DELAY = 6000L;
	/** the start time of the simulation as a Java {@code LocalTime}. */
	protected static LocalTime simulatedStartTime;
	/** the end time of the simulation as a Java {@code LocalTime}. */
	protected static LocalTime simulatedEndTime;

	/**
	 * map that will contain the URI of the action inbound ports used in proxy
	 * components to offer their services in the smart city and the URI of
	 * notification inbound ports used by events emitter components to receive the
	 * notifications from the smart city.
	 */
	private Map<String, String> facadeNotificationInboundPortsURI;
	/**
	 * URI of the fire stations and SAMU centers inbound port used by the traffic
	 * lights simulator to notify them of events concerning them.
	 */
	protected final Map<String, String> stationsEventIBPURI;
	/**
	 * URI of the traffic lights simulator inbound port used by the fire stations
	 * and SAMU centers to execute the actions concerning them.
	 */
	protected final Map<IntersectionPosition, String> trafficLightsIBPURI;

	public NewSmartCity() throws Exception {
		// initialise the basic simulator smart city descriptor.
		SmartCityDescriptor.initialise();

		facadeNotificationInboundPortsURI = new HashMap<>();

		AbstractCVM.getThisJVMURI();

		this.stationsEventIBPURI = new HashMap<>();
		Iterator<String> iterStation = SmartCityDescriptor.createFireStationIdIterator();
		while (iterStation.hasNext()) {
			String id = iterStation.next();
			this.stationsEventIBPURI.put(id, AbstractPort.generatePortURI());
		}
		iterStation = SmartCityDescriptor.createSAMUStationIdIterator();
		while (iterStation.hasNext()) {
			stationsEventIBPURI.put(iterStation.next(), AbstractPort.generatePortURI());
		}

		this.trafficLightsIBPURI = new HashMap<>();
		Iterator<IntersectionPosition> iterTL = SmartCityDescriptor.createTrafficLightPositionIterator();
		while (iterTL.hasNext()) {
			this.trafficLightsIBPURI.put(iterTL.next(), AbstractPort.generatePortURI());
		}
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * return true if the asset has already a URI registered, false otherwise.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	{@code assetId != null && !assetId.isEmpty()}
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param assetId asset identifier as define the the smart city descriptor.
	 * @return true if the asset has already a URI registered, false otherwise.
	 */
	protected  boolean registered(String assetId) {
		assert assetId != null && !assetId.isEmpty();
		return this.facadeNotificationInboundPortsURI.containsKey(assetId);
	}

	/**
	 * register the URI if the notification inbound port used in the events emitter
	 * component associated with the asset identifier {@code assetId}.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	{@code assetId != null && !assetId.isEmpty()}
	 * pre	{@code !registered(assetId)}
	 * pre	{@code uri != null && !uri.isEmpty()}
	 * post	{@code registered(assetId)}
	 * </pre>
	 *
	 * @param assetId asset identifier as define the the smart city descriptor.
	 * @param uri     URI of the notification inbound port of the corresponding
	 *                events emitter component.
	 */
	public void register(String assetId, String uri) {
		assert assetId != null && !assetId.isEmpty();
		assert !registered(assetId);
		assert uri != null && !uri.isEmpty();
		this.facadeNotificationInboundPortsURI.put(assetId, uri);
	}

	/**
	 * @see fr.sorbonne_u.components.cvm.AbstractCVM#deploy()
	 */
	@Override
	public void deploy() throws Exception {
		AbstractComponent.createComponent(TrafficLightsSimulator.class.getCanonicalName(),
				new Object[] { this.stationsEventIBPURI, this.trafficLightsIBPURI });

		Iterator<String> iterStation = SmartCityDescriptor.createFireStationIdIterator();
		while (iterStation.hasNext()) {
			String id = iterStation.next();
			AbstractComponent.createComponent(FireStationProxy.class.getCanonicalName(),
					new Object[] { SmartCityDescriptor.getActionInboundPortURI(id),
							facadeNotificationInboundPortsURI.get(id), id, SmartCityDescriptor.getPosition(id),
							this.stationsEventIBPURI.get(id), 2, 2 });
		}

		iterStation = SmartCityDescriptor.createSAMUStationIdIterator();
		while (iterStation.hasNext()) {
			String id = iterStation.next();
			AbstractComponent.createComponent(SAMUStationProxy.class.getCanonicalName(),
					new Object[] { SmartCityDescriptor.getActionInboundPortURI(id),
							facadeNotificationInboundPortsURI.get(id), id, SmartCityDescriptor.getPosition(id),
							this.stationsEventIBPURI.get(id), 2, 2 });
		}

		Iterator<IntersectionPosition> trafficLightsIterator = SmartCityDescriptor.createTrafficLightPositionIterator();
		while (trafficLightsIterator.hasNext()) {
			IntersectionPosition p = trafficLightsIterator.next();
			AbstractComponent.createComponent(TrafficLightProxy.class.getCanonicalName(),
					new Object[] { p, SmartCityDescriptor.getActionInboundPortURI(p),
							facadeNotificationInboundPortsURI.get(p.toString()),
							this.trafficLightsIBPURI.get(p) });
		}

		super.deploy();
	}
}
