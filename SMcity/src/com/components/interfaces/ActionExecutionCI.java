package com.components.interfaces;

import java.io.Serializable;

import com.interfaces.ActionI;
import com.interfaces.ResponseI;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface ActionExecutionCI extends OfferedCI, RequiredCI {
	
	public ResponseI executeAction(ActionI a, Serializable[] params) throws Exception;
	
}
