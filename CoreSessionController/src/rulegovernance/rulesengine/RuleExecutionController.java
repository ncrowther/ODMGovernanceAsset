/*
 * Licensed Materials - Property of IBM
 * 5724-X98 5724-Y15 5655-V82 
 * Copyright IBM Corp. 1987, 2010. All Rights Reserved.
 *
 * Note to U.S. Government Users Restricted Rights: 
 * Use, duplication or disclosure restricted by GSA ADP Schedule 
 * Contract with IBM Corp.
 */

package rulegovernance.rulesengine;

import ilog.rules.res.model.IlrFormatException;
import ilog.rules.res.model.IlrPath;
import ilog.rules.res.session.IlrPOJOSessionFactory;
import ilog.rules.res.session.IlrSessionException;
import ilog.rules.res.session.IlrSessionFactory;
import ilog.rules.res.session.IlrSessionRequest;
import ilog.rules.res.session.IlrSessionResponse;
import ilog.rules.res.session.IlrStatelessSession;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides  methods to execute Rule Governance rules using the WODM
 * rule execution api.
 */
public class RuleExecutionController implements Serializable {

	private static Logger log = Logger.getLogger(rulegovernance.rulesengine.RuleExecutionController.class.getName());
	
	private static final long serialVersionUID = -4882429548145412189L;
	
	private static final String PERMISSION = "PERMISSION";
	private static final String ON_COMMIT = "ONCOMMIT";
	private static final String ELEMENT_COMMITTED = "ELEMENTCOMMITTED";	
	private static final String GET_ROLES = "GETROLES";
	private static final String GET_STATUS_VALUES = "GETSTATUSVALUES";	
	private static final String REQUEST_TYPE = "requestType";
	private static final String RESPONSE = "response";
	private static final String REQUEST = "request";
	private static final String ROLES = "roles";
	private static final String STATUS_VALUES = "statusValues";
	private static final String PERMISSION_GRANTED = "permissionGranted";
	public static final String RULEAPPNAME = "rbrgRuleApp";
	public static final String RULESETNAME = "rbrgDecisionService";

	private static final String rulesetPath = new String("/"
			+ RULEAPPNAME + "/"
			+ RULESETNAME);

	private IlrSessionFactory pojoFactory = null;
	private IlrSessionRequest sessionRequest = null;
	private IlrStatelessSession statelessSession = null;

	/**
	 * Creates an <code>RuleExecutionController</code>.
	 */
	public RuleExecutionController() {

		try {
			pojoFactory = new IlrPOJOSessionFactory();

			sessionRequest = pojoFactory.createRequest();
			sessionRequest.setRulesetPath(IlrPath.parsePath(rulesetPath));

			// Create a stateless rule session
			statelessSession = pojoFactory.createStatelessSession();
			
		} catch (Exception e) {
			e.printStackTrace();
			log.log(Level.SEVERE, "Exception Thrown while building statelessSession: "+e.getMessage());
		}
	}
	
	/**
	 * Checks whether permission is granted.
	 * @param parameters
	 *            for the rule engine
	 * @return response
	 * @throws IlrFormatException
	 * @throws IlrSessionException
	 */
	public boolean hasPermission(Map<String, Object> parameters) {

		boolean permissionGranted = false;

		// Set the input parameters for the execution of the rules
		Map<String, Object> inputParameters = sessionRequest
				.getInputParameters();
		
	    parameters.put(REQUEST_TYPE, PERMISSION);

		inputParameters.put(REQUEST, parameters);	

		// Execute the rules and returns the response
		IlrSessionResponse sessionResponse;
		try {
			sessionResponse = statelessSession.execute(sessionRequest);

			java.util.Map<?, ?> grsResponse = (java.util.Map<?, ?>) sessionResponse
					.getOutputParameters().get(RESPONSE);

			permissionGranted = (Boolean) grsResponse.get(PERMISSION_GRANTED);
			
		} catch (IlrSessionException e) {
			e.printStackTrace();
			log.log( Level.SEVERE, "Exception Thrown while executing rules: "+e.getMessage());
		}

		return permissionGranted;
	}
	
	/**
	 * Returns user roles.
	 * @return response
	 * @throws IlrFormatException
	 * @throws IlrSessionException
	 */
	public List<String> getUserRoles() {

		List<String> roles = new ArrayList<String>();
			
		// Set the input parameters for the execution of the rules
		Map<String, Object> inputParameters = sessionRequest
				.getInputParameters();
		
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    
	    parameters.put(REQUEST_TYPE, GET_ROLES);

		inputParameters.put(REQUEST, parameters);	

		// Execute the rules and returns the response
		IlrSessionResponse sessionResponse;
		try {
			sessionResponse = statelessSession.execute(sessionRequest);

			java.util.Map<?, ?> grsResponse = (java.util.Map<?, ?>) sessionResponse
					.getOutputParameters().get(RESPONSE);

			roles = (List<String>) grsResponse.get(ROLES);
			
		} catch (IlrSessionException e) {
			e.printStackTrace();
			log.log(Level.SEVERE, "Exception Thrown while executing rules: "+e.getMessage());
		}


		return roles;
	}
	
	
	/**
	 * Returns the list of next status values.
	 * @param parameters
	 *            for the rule engine
	 * @return response
	 * @throws IlrFormatException
	 * @throws IlrSessionException
	 */
	public List<String> getStatusValues(Map<String, Object> parameters) {

		List<String> statusValues = new ArrayList<String>();
		
		// Set the input parameters for the execution of the rules
		Map<String, Object> inputParameters = sessionRequest
				.getInputParameters();
		
	    parameters.put(REQUEST_TYPE, GET_STATUS_VALUES);

		inputParameters.put(REQUEST, parameters);

		// Execute the rules and returns the response
		IlrSessionResponse sessionResponse;
		try {
			sessionResponse = statelessSession.execute(sessionRequest);

			java.util.Map<?, ?> grsResponse = (java.util.Map<?, ?>) sessionResponse
					.getOutputParameters().get(RESPONSE);

			statusValues = (List<String>) grsResponse.get(STATUS_VALUES);

		} catch (IlrSessionException e) {
			e.printStackTrace();
			log.log(Level.SEVERE, "Exception Thrown while executing rules: "+e.getMessage());
		}

		return statusValues;
	}

	
	/**
	 * Returns the result of validating the metadata.
	 * @param parameters
	 *            for the rule engine
	 * @return response
	 * @throws IlrFormatException
	 * @throws IlrSessionException
	 */
	@SuppressWarnings("unchecked")
	public java.util.Map<String, Object> onCommit(Map<String, Object> parameters) {

		java.util.Map<String, Object> grsResponse = null;
		
		// Set the input parameters for the execution of the rules
		Map<String, Object> inputParameters = sessionRequest
				.getInputParameters();
		
	    parameters.put(REQUEST_TYPE, ON_COMMIT);

		inputParameters.put(REQUEST, parameters);

		// Execute the rules and returns the response
		IlrSessionResponse sessionResponse;
		try {
			//log.info("The session is "+statelessSession);
			sessionResponse = statelessSession.execute(sessionRequest);

			grsResponse = (java.util.Map<String, Object>) sessionResponse.getOutputParameters().get(RESPONSE);
			
		} catch (IlrSessionException e) {
			e.printStackTrace();
			log.log(Level.SEVERE, "Exception Thrown while executing rules: "+e.getMessage());
		}

		return grsResponse;
	}	
	
	/**
	 * @param parameters
	 *            for the rule engine
	 * @return response
	 * @throws IlrFormatException
	 * @throws IlrSessionException
	 */
	public java.util.Map<String, Object> elementCommitted(Map<String, Object> parameters) {
		
		java.util.Map<String, Object> grsResponse = new HashMap<String, Object>();
		
		// Set the input parameters for the execution of the rules
		Map<String, Object> inputParameters = sessionRequest
				.getInputParameters();
		
	    parameters.put(REQUEST_TYPE, ELEMENT_COMMITTED);
	   
		inputParameters.put(REQUEST, parameters);

		// Execute the rules and returns the response
		IlrSessionResponse sessionResponse;
		try {
			sessionResponse = statelessSession.execute(sessionRequest);

			grsResponse = (java.util.Map<String, Object>) sessionResponse
					.getOutputParameters().get(RESPONSE);
			
		} catch (IlrSessionException e) {
			e.printStackTrace();
			log.log(Level.SEVERE, "Exception Thrown while executing rules: "+e.getMessage());
		}
			
		return grsResponse;
	}	
		
}