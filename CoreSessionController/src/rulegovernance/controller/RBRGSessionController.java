/*
 * Licensed Materials - Property of IBM
 * 5725-B69 5655-Y17 5724-Y00 5724-Y17 5655-V84
 * Copyright IBM Corp. 1987, 2012. All Rights Reserved.
 *
 * Note to U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
 */
package rulegovernance.controller;

import ilog.rules.teamserver.brm.IlrBusinessRule;
import ilog.rules.teamserver.brm.IlrRuleArtifactTag;
import ilog.rules.teamserver.brm.IlrTag;
import ilog.rules.teamserver.model.IlrSession;
import ilog.rules.teamserver.model.IlrApplicationException;
import ilog.rules.teamserver.model.IlrCommitableObject;
import ilog.rules.teamserver.model.IlrDefaultSearchCriteria;
import ilog.rules.teamserver.model.IlrDefaultSessionController;
import ilog.rules.teamserver.model.IlrElementDetails;
import ilog.rules.teamserver.model.IlrElementHandle;
import ilog.rules.teamserver.model.IlrElementVersion;
import ilog.rules.teamserver.model.IlrObjectNotFoundException;
import ilog.rules.teamserver.model.IlrSearchCriteria;
import ilog.rules.teamserver.model.impl.IlrElementHandleImpl;
import ilog.rules.teamserver.model.permissions.IlrPermissionConstants;
import ilog.rules.teamserver.model.permissions.IlrPermissionException;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import rulegovernance.rulesengine.RuleExecutionController;

public abstract class RBRGSessionController extends IlrDefaultSessionController {

	private static Logger log = Logger
			.getLogger(rulegovernance.controller.RBRGSessionController.class
					.getName());

	protected final static String STATUS = "status";
	protected final static String OLD_STATUS = "oldStatus";
	protected final static String ROLE = "role";
	protected final static String LOGIN_USER = "loginUser";
	protected final static String NAME = "name";
	protected final static String INITIAL_STATE = "initialState";
	protected final static String FEATURE = "feature";
	protected final static String TYPE = "type";
	protected final static String VALIDATION_ERROR = "validationError";
	private final static String EXECUTE_QUERY = "executeQuery";
	protected final static String OPERATION = "operation";
	private final static String SOURCE_RULE_ARTEFACT = "sourceRuleArtifact";
	private final static String UPDATE = "update";
	private final static String CREATE = "create";
	private final static String DELETE = "delete";
	private final static String TAGS = "tags";

	protected boolean override = false;

	/**
	 * Creates a <code>RBRGSessionController</code> instance.
	 */
	public RBRGSessionController() {
		super();
	}

	/************** PUBLIC METHODS OVERRIDE SESSION CONTROLLER METHODS *****************/

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ilog.rules.teamserver.model.IlrDefaultSessionController#checkUpdate(ilog
	 * .rules.teamserver.model.IlrElementHandle,
	 * ilog.rules.teamserver.model.IlrElementDetails,
	 * org.eclipse.emf.ecore.EStructuralFeature)
	 */
	@Override
	public void checkUpdate(IlrElementHandle handle, IlrElementDetails details,
			EStructuralFeature feature) throws IlrPermissionException,
			IlrObjectNotFoundException {
		
		// Let the default controller do its job
		super.checkUpdate(handle, details, feature);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ilog.rules.teamserver.model.IlrDefaultSessionController#checkDelete(ilog
	 * .rules.teamserver.model.IlrElementHandle)
	 */
	@Override
	public void checkDelete(IlrElementHandle details)
			throws IlrPermissionException, IlrObjectNotFoundException {
		// Let the default controller do its job
		super.checkDelete(details);
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * Check if user can commit element. Also sets metadata.
	 * 
	 * @see
	 * ilog.rules.teamserver.model.IlrDefaultSessionController#onCommitElement
	 * (ilog.rules.teamserver.model.IlrCommitableObject)
	 */
	@Override
	public void onCommitElement(IlrCommitableObject cobject)
			throws IlrApplicationException {
		// Always call the super method
		super.onCommitElement(cobject);

		// Ignore governance if change initiated through code or admin role
		if (override || isRtsAdministrator(session)) {

			if (log.isLoggable(Level.INFO)) {
				log.log(Level.INFO, "onCommitElement ignored: Override: "
						+ override + " isAdmin: " + isRtsAdministrator(session));
			}

			return;
		}
		

		IlrElementHandle eHandle = cobject.getRootElementHandle();
		if (eHandle != null) {

			// Retrieve the details
			IlrElementDetails eDetails = cobject.getRootDetails();
			if (eDetails != null) {
				invokeOnCommitRules(cobject, eDetails);
			}
		}

	}

	/*
	 */
	@Override
	public void elementCommitted(IlrCommitableObject cobject,
			IlrElementHandle newHandle) throws IlrApplicationException {
		// Always call the super method
		super.elementCommitted(cobject, newHandle);
	

		// Ignore governance if change initiated through code or admin role
		if (override || isRtsAdministrator(session)) {

			if (log.isLoggable(Level.INFO)) {
				log.log(Level.INFO, "elementCommitted ignored:  Override: "
						+ override + " isAdmin: " + isRtsAdministrator(session));
			}

			return;
		}


		Object object = session.getElementDetails(newHandle);

		String newStatus = "";
		if (object instanceof IlrElementDetails) {

			IlrElementDetails element = cobject.getRootDetails();
			newStatus = getPropertyValue(element, STATUS);

			java.util.Map<String, Object> vars = new java.util.HashMap<String, Object>();

			// Overridable
			addCustomElementCommittedVars(vars, cobject, newHandle);

			//vars.put(OLD_STATUS, getLastStatus(cobject));
			vars.put(STATUS, newStatus);

			java.util.Map<String, Object> response = invokeElementCommittedRules(
					vars, element);

			checkIfExecuteQueryRequested(response);

			checkIfNotificationRequested(response);

		}

	}

	/*
	 * Determine the list of state transitions
	 */
	@Override
	public List<?> getPossibleValues(IlrElementHandle element,
			org.eclipse.emf.ecore.EStructuralFeature feature)
			throws IlrObjectNotFoundException {

		return super.getPossibleValues(element, feature);
		
		/*
		if (isStatusPropertyCandidate(element, feature)) {
			return getStatusValues(element, feature);
		} else {
			// Its not the status property or or property so just call the
			// default
			return super.getPossibleValues(element, feature);
		}
		*/
	}


	/**************
	 * PROTECTED METHODS ALLOW CUSTOMISATIONS ON RULE GOVERNANCE ASSET IN
	 * SUBCLASSES OF THIS BASE CLASS
	 *****************/

	/**
	 * Add additional parameters for check create. These parameters will be used
	 * in the rules to check for create permissions
	 */
	protected abstract void addCustomCheckCreateVars(
			java.util.Map<String, Object> vars) throws IlrPermissionException,
			IlrObjectNotFoundException;

	/**
	 * Add additional parameters for check delete. These parameters will be used
	 * in the rules to check for delete permissions
	 */
	protected abstract void addCustomCheckDeleteVars(
			java.util.Map<String, Object> vars, IlrElementHandle details)
			throws IlrPermissionException, IlrObjectNotFoundException;

	/**
	 * Add additional parameters for check update. These parameters will be used
	 * in the rules to check for update permissions
	 */
	protected abstract void addCustomCheckUpdateVars(
			java.util.Map<String, Object> vars, IlrElementHandle handle,
			IlrElementDetails details, EStructuralFeature feature)
			throws IlrPermissionException, IlrObjectNotFoundException;

	/**
	 * Add additional parameters for on commit. These parameters will be used in
	 * the rules to verify that the rule can be committed
	 */
	protected abstract void addCustomOnCommitVars(
			java.util.Map<String, Object> vars, IlrCommitableObject cobject,
			IlrElementDetails details) throws IlrPermissionException,
			IlrObjectNotFoundException;

	/**
	 * Add additional parameters for element committed. These parameters will be
	 * used in the rules after the rule is committed
	 */
	protected abstract void addCustomElementCommittedVars(
			java.util.Map<String, Object> vars, IlrCommitableObject cobject,
			IlrElementHandle newHandle) throws IlrApplicationException;

	/**
	 * Add additional parameters for getting the status values.
	 */
	protected abstract void addCustomGetStatusVars(
			java.util.Map<String, Object> vars, IlrElementDetails details)
			throws IlrObjectNotFoundException;


	/**
	 * Iterate through all the metadata for the element and send to the rule
	 * engine
	 * 
	 * @param reference
	 *            - the element from which the metadata is to be extracted
	 * 
	 * @return a populated <code>Map</code> of meta data or <code>null</code> if
	 *         the <code>reference</code> is <code>null</code>
	 */
	protected Map<String, Object> getMetadata(IlrElementDetails reference) {
		if (reference == null) {
			return null;
		}
		Map<String, Object> vars = new HashMap<String, Object>();

		// Retrieve all properties for the class
		EClass eclass = reference.eClass();
		EList<?> features = eclass.getEAllStructuralFeatures();

		// Loop through all properties (features) of the element and send them
		// to the rule engine
		for (int i = 0; i < features.size(); i++) {
			// Get the next feature
			EStructuralFeature feature = (EStructuralFeature) features.get(i);

			// Add metadata name and value to the parameter map
			Object value = reference.getRawValue(feature);

			if (value == null) {
				value = "null";
			}

			/* This does not work on deleted elements */
			if ((value instanceof IlrElementHandleImpl)
					&& (!feature.getName().equals(SOURCE_RULE_ARTEFACT))) {
				// Retrieve the value of the name property
				IlrElementHandleImpl eHandle = (IlrElementHandleImpl) value;

				if (eHandle != null) {
					// Retrieve the details
					value = eHandle.getPropertyValue(NAME);
				}
			}

			vars.put(feature.getName(), value);
		}

		return vars;
	}

	/**
	 * Returns the property value of classname
	 */
	protected String getPropertyValue(IlrElementDetails elementDetails,
			String className) {

		Map<String, Object> vars = getMetadata(elementDetails);
		if (vars == null) {
			return null;
		}
		return (String) vars.get(className);
	}

	/**
	 * Tries to retrieve the status feature from the class.
	 */
	protected EStructuralFeature getFeature(EClass eClass, String property) {
		// Retrieve all properties for the class
		EList<?> eAllStructuralFeatures = eClass.getEAllStructuralFeatures();
		for (int j = 0; j < eAllStructuralFeatures.size(); j++) {
			EStructuralFeature f = eClass.getEStructuralFeature(j);
			// If this is the status property the it is found!
			if (property.equalsIgnoreCase(f.getName())) {
				return f;
			}
		}
		// No status property found in this class (a function, or a ruleflow ,
		// or ...)
		return null;
	}

	/**
	 * Returns the status value.
	 */
	protected String getStatusPropertyValue(IlrElementDetails elementDetails) {
		EStructuralFeature f = getStatusFeature(elementDetails);
		// Found?
		if (f != null) {
			// Get its value and return it
			Object object = elementDetails.getRawValue(f);
			if (object == null) {
				return null;
			}
			return object.toString();
		}
		return null;
	}

	protected boolean isRtsAdministrator(IlrSession session) {
		return session.isUserInRole(IlrPermissionConstants.ADMINISTRATOR_ROLE);
	}

	/************** PRIVATE METHODS *****************/


	private String getLastStatus(IlrCommitableObject cobject) {

		String lastStatus = INITIAL_STATE;
		;
		try {
			IlrElementDetails eDetails = cobject.getRootDetails();

			List<IlrElementVersion> versions = session
					.getElementVersions(eDetails);

			// get details from the last version
			if (versions != null && versions.size() > 1) {

				IlrElementVersion lastVersion = versions
						.get(versions.size() - 2);
				IlrElementDetails lastVersionDetails = session
						.getElementDetails(eDetails, lastVersion);
				Map<String, Object> lastVersionValues = getMetadata(lastVersionDetails);
				lastStatus = (String) lastVersionValues.get(STATUS);

			}
		} catch (IlrObjectNotFoundException e) {
			e.printStackTrace();
		}

		return lastStatus;
	}

	private java.util.Map<String, Object> invokeElementCommittedRules(
			java.util.Map<String, Object> vars, IlrElementDetails element) {

		java.util.Map<String, Object> response = new HashMap<String, Object>();

		// Add all metadata to input vars
		Map<String, Object> v = getMetadata(element);

		if (v != null) {
			vars.putAll(v);

			// Call business rules to determine whether the role can update
			// a feature (attribute) on a given status.
			RuleExecutionController controller = new RuleExecutionController();

			response = controller.elementCommitted(vars);
		}

		return response;
	}

	private void checkQueryPermission(IlrElementHandle handle,
			EStructuralFeature feature, IlrElementDetails elementDetails)
			throws IlrObjectNotFoundException, IlrPermissionException {

		if (feature.getName().equalsIgnoreCase(STATUS)) {
			if (handle instanceof IlrElementDetails) {

				IlrElementDetails newDetails = (IlrElementDetails) handle;
				String newStatusPropertyValue = getPropertyValue(newDetails,
						STATUS);
				if (log.isLoggable(Level.INFO)) {
					log.log(Level.INFO,
							"Governance Rules [checkQuery status ActionPermission]: "
									+ newDetails + ":" + newStatusPropertyValue);
				}

				List<?> possibleValues = getPossibleValues(handle, feature);
				if (possibleValues.contains(newStatusPropertyValue))
					return;
				// If here means a user executed a query setting status which is
				// forbidden as it breaks RGA rules
				// If exception has been set by the first test for another role
				// already, it will be overridden,
				// since this exception is "weaker".
				throw new IlrPermissionException("'" + newStatusPropertyValue
						+ "' is an invalid state for element : "
						+ elementDetails.getName());
			}
		}
	}

	private void invokeOnCommitRules(IlrCommitableObject cobject,
			IlrElementDetails eDetails) throws IlrPermissionException,
			IlrObjectNotFoundException {

		if (eDetails != null) {

			// Call business rules
			RuleExecutionController controller = new RuleExecutionController();

			// Initialize the ruleset parameters.
			java.util.Map<String, Object> vars = new java.util.HashMap<String, Object>();

			// Overridable
			addCustomOnCommitVars(vars, cobject, eDetails);

			String loginUser = session.getUserName();
			vars.put(LOGIN_USER, loginUser);

			// Add all metadata to input vars
			Map<String, Object> v = getMetadata(eDetails);
			vars.putAll(v);

			java.util.Map<String, Object> response = controller.onCommit(vars);

			if (response != null) {
				Object resp = response.get(VALIDATION_ERROR);

				if (resp != null) {
					String validationError = (String) resp;

					if (!validationError.equals("")) {

						throw new IlrPermissionException(validationError);
					}

					setTags(cobject, eDetails, response);

					setMetadata(eDetails, response);
				}
			}
		}
	}

	/**
	 * Iterate through the response and for each metadata element set its value
	 * 
	 * @param reference
	 *            - the element containing the metadata to be set
	 * @param vars
	 *            the additional parameters
	 */
	private void setMetadata(IlrElementDetails reference,
			Map<String, Object> vars) {

		if (reference == null) {
			return;
		}

		// Retrieve all properties for the class
		EClass eclass = reference.eClass();
		EList<?> features = eclass.getEAllStructuralFeatures();

		// Loop through all properties (features) of the element and set its
		// value if it has been set in the rules
		for (int i = 0; i < features.size(); i++) {

			// Get the next feature
			EStructuralFeature feature = (EStructuralFeature) features.get(i);

			// See if feature name is in returned variable map
			Object value = vars.get(feature.getName());

			if (value != null) {
				log.log(Level.INFO, "Set " + feature.getName() + "=" + value
						+ ": " + value.getClass() + ": " + feature.getClass());

				reference.setRawValue(feature, value);
			}
		}
	}

	private void checkIfExecuteQueryRequested(
			java.util.Map<String, Object> response)
			throws IlrObjectNotFoundException {

		Object executeQueryObj = response.get(EXECUTE_QUERY);

		log.log(Level.INFO, "Execute Query: ", executeQueryObj);

		if (executeQueryObj instanceof String) {
			String executeQuery = (String) executeQueryObj;

			if ((executeQuery != null) && executeQuery.length() > 0) {
				executeQuery(executeQuery);
			}
		}
	}

	private List<IlrElementDetails> executeQuery(String query)
			throws IlrObjectNotFoundException {

		IlrSearchCriteria criteria = new IlrDefaultSearchCriteria(query);

		log.log(Level.INFO, "Execute Query: " + query);

		List<IlrElementDetails> elements = null;
		try {
			override = true;
			int numObjects = session.executeQuery(criteria);

			log.log(Level.INFO, "Applied query : " + query + " to "
					+ numObjects + " Objects");

		} catch (IlrApplicationException e) {
			e.printStackTrace();
		} finally {
			override = false;
		}

		return elements;
	}

	/**
	 * Returns the status feature for this element, or null if it does not
	 * exist.
	 */
	private EStructuralFeature getStatusFeature(IlrElementDetails elementDetails) {

		// Get the artifact type (Rule, Function, ...)
		if (elementDetails == null) {
			return null;
		}

		// Retrieve the value of the status property
		EClass eClass = elementDetails.eClass();
		EStructuralFeature f = getFeature(eClass, STATUS);
		return f;
	}

	/**
	 * Iterate through the response and for each metadata element set its value
	 * 
	 * @param reference
	 *            - the element containing the metadata to be set
	 * @param vars
	 *            the additional parameters
	 * @throws IlrObjectNotFoundException
	 */
	private void setTags(IlrCommitableObject cobject,
			IlrElementDetails reference, Map<String, Object> vars)
			throws IlrObjectNotFoundException {

		if ((reference == null) || (reference.getName() == null)) {
			return;
		}

		IlrElementHandle eHandle = cobject.getRootElementHandle();
		if (eHandle != null) {

			EClass eclass = eHandle.eClass();

			IlrElementDetails eDetails = cobject.getRootDetails();

			if (eDetails != null) {

				Map<String, String> tags = (Map<String, String>) vars.get(TAGS);
				Set<String> keys = tags.keySet();

				if (keys.size() > 0) {
					IlrBusinessRule brule = (IlrBusinessRule) eDetails;

					List<IlrRuleArtifactTag> existingTags = (List<IlrRuleArtifactTag>) brule
							.getTags();
					for (int i = 0; i < existingTags.size(); i++) {
						IlrTag t = existingTags.get(i);
						cobject.addDeletedElement(session.getBrmPackage()
								.getRuleArtifact_Tags(), t);
					}

					// Add replacement tags
					Iterator keySet = keys.iterator();
					while (keySet.hasNext()) {
						String key = (String) keySet.next();
						String value = (String) tags.get(key);

						IlrElementHandle taghandle = session
								.createElement(session.getBrmPackage()
										.getRuleArtifactTag());
						IlrElementDetails tag = session
								.getElementDetails(taghandle);
						tag.setRawValue(session.getBrmPackage().getTag_Name(),
								key);
						tag.setRawValue(session.getBrmPackage().getTag_Value(),
								value);

						cobject.addModifiedElement(session.getBrmPackage()
								.getRuleArtifact_Tags(), tag);

						if (log.isLoggable(Level.INFO)) {
							log.log(Level.INFO, "**Set tag" + key + "=" + value);
						}
					}

				}
			}
		}
	}
	

	protected void checkIfNotificationRequested(java.util.Map<String, Object> response) {
		
		if (log.isLoggable(Level.INFO)) {
			log.log(Level.INFO, "checkIfNotificationRequested RBRG ");
		}
		
		boolean triggerBuild = false;
		Object triggerBuildObj = response.get("triggerBuild");

		if (triggerBuildObj instanceof Boolean) {
			triggerBuild = (Boolean) triggerBuildObj;
		}
		
		
		if (triggerBuild) {
			
			String release = "UNKNOWN";
			Object releaseObj = response.get("baseline");

			if (releaseObj instanceof String) {
				release = (String) releaseObj;
			}
			
			String buildPath = ".";
			Object buildPathObj = response.get("buildPath");

			if (buildPathObj instanceof String) {
				buildPath = (String) buildPathObj;
			}

			if (log.isLoggable(Level.INFO)) {
				log.log(Level.INFO, "Governance Rules: Trigger Build of  "
						+ release + " in build path " + buildPath);
			}
			
			Writer writer = null;

			String releaseFilename = release.replace(' ', '_') + ".txt";
			try {			
			    writer = new BufferedWriter(new OutputStreamWriter(
			          new FileOutputStream(buildPath + "/" + releaseFilename), "utf-8"));
			    writer.write(release);
			} catch (IOException ex) {
				log.log(Level.INFO, "Governance Rules: Failed to write build file  "
						+ releaseFilename);
			} finally {
			   try {writer.close();} catch (Exception ex) {/*ignore*/}
			}

			/*
			notification.setRecipient((String) response
					.get(NOTIFICATION_RECIPIENT));
			notification
					.setSubject((String) response.get(NOTIFICATION_SUBJECT));
			notification.setMessageBody((String) response
					.get(NOTIFICATION_BODY));

			notificationListener.notify(session, notification);
			*/
		}
	}

}
