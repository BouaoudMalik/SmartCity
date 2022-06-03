package com.connections.ibp;

import java.time.LocalTime;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
import fr.sorbonne_u.cps.smartcity.grid.IntersectionPosition;
import fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationCI;
import fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationImplI;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfHealthAlarm;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfTrafficLightPriority;

public class SAMUNotificationInboundPort extends AbstractInboundPort implements SAMUNotificationCI {
	
	private static final long serialVersionUID = 1L;

	public SAMUNotificationInboundPort(ComponentI owner) throws Exception {
		super(SAMUNotificationCI.class, owner);
		assert owner instanceof SAMUNotificationImplI;
	}

	public SAMUNotificationInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, SAMUNotificationCI.class, owner);
		assert owner instanceof SAMUNotificationImplI;
	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationCI#healthAlarm(fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition,
	 *      fr.sorbonne_u.cps.smartcity.interfaces.TypeOfHealthAlarm,
	 *      java.time.LocalTime)
	 */
	@Override
	public void healthAlarm(AbsolutePosition position, TypeOfHealthAlarm type, LocalTime occurrence) throws Exception {
		this.getOwner().handleRequest(o -> {
			((SAMUNotificationImplI) o).healthAlarm(position, type, occurrence);
			return null;
		});
	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationCI#trackingAlarm(fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition,
	 *      java.lang.String, java.time.LocalTime)
	 */
	@Override
	public void trackingAlarm(AbsolutePosition position, String personId, LocalTime occurrence) throws Exception {
		this.getOwner().handleRequest(o -> {
			((SAMUNotificationImplI) o).trackingAlarm(position, personId, occurrence);
			return null;
		});
	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationCI#manualSignal(java.lang.String,
	 *      java.time.LocalTime)
	 */
	@Override
	public void manualSignal(String personId, LocalTime occurrence) throws Exception {
		this.getOwner().handleRequest(o -> {
			((SAMUNotificationImplI) o).manualSignal(personId, occurrence);
			return null;
		});
	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationCI#requestPriority(fr.sorbonne_u.cps.smartcity.grid.IntersectionPosition,
	 *      fr.sorbonne_u.cps.smartcity.interfaces.TypeOfTrafficLightPriority,
	 *      java.lang.String, fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition,
	 *      java.time.LocalTime)
	 */
	@Override
	public void requestPriority(IntersectionPosition intersection, TypeOfTrafficLightPriority priority,
			String vehicleId, AbsolutePosition destination, LocalTime occurrence) throws Exception {
		this.getOwner().handleRequest(o -> {
			((SAMUNotificationImplI) o).requestPriority(intersection, priority, vehicleId, destination, occurrence);
			return null;
		});
	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationCI#atDestination(java.lang.String,
	 *      java.time.LocalTime)
	 */
	@Override
	public void atDestination(String vehicleId, LocalTime occurrence) throws Exception {
		this.getOwner().handleRequest(o -> {
			((SAMUNotificationImplI) o).atDestination(vehicleId, occurrence);
			return null;
		});
	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationCI#atStation(java.lang.String,
	 *      java.time.LocalTime)
	 */
	@Override
	public void atStation(String vehicleId, LocalTime occurrence) throws Exception {
		this.getOwner().handleRequest(o -> {
			((SAMUNotificationImplI) o).atStation(vehicleId, occurrence);
			return null;
		});
	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationCI#notifyMedicsAvailable(java.time.LocalTime)
	 */
	@Override
	public void notifyMedicsAvailable(LocalTime occurrence) throws Exception {
		this.getOwner().handleRequest(o -> {
			((SAMUNotificationImplI) o).notifyMedicsAvailable(occurrence);
			return null;
		});
	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationCI#notifyNoMedicAvailable(java.time.LocalTime)
	 */
	@Override
	public void notifyNoMedicAvailable(LocalTime occurrence) throws Exception {
		this.getOwner().handleRequest(o -> {
			((SAMUNotificationImplI) o).notifyNoMedicAvailable(occurrence);
			return null;
		});
	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationCI#notifyAmbulancesAvailable(java.time.LocalTime)
	 */
	@Override
	public void notifyAmbulancesAvailable(LocalTime occurrence) throws Exception {
		this.getOwner().handleRequest(o -> {
			((SAMUNotificationImplI) o).notifyAmbulancesAvailable(occurrence);
			return null;
		});
	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationCI#notifyNoAmbulanceAvailable(java.time.LocalTime)
	 */
	@Override
	public void notifyNoAmbulanceAvailable(LocalTime occurrence) throws Exception {
		this.getOwner().handleRequest(o -> {
			((SAMUNotificationImplI) o).notifyNoAmbulanceAvailable(occurrence);
			return null;
		});
	}

}
