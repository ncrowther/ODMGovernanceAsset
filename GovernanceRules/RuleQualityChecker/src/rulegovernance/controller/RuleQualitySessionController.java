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

import ilog.rules.dt.IlrDTController;
import ilog.rules.dt.model.helper.IlrDTHelper;
import ilog.rules.teamserver.brm.IlrActionRule;
import ilog.rules.teamserver.brm.IlrBaseline;
import ilog.rules.teamserver.brm.IlrDecisionTable;
import ilog.rules.teamserver.brm.IlrDefinition;
import ilog.rules.teamserver.model.IlrApplicationException;
import ilog.rules.teamserver.model.IlrCommitableObject;
import ilog.rules.teamserver.model.IlrElementDetails;
import ilog.rules.teamserver.model.IlrElementHandle;
import ilog.rules.teamserver.model.IlrObjectNotFoundException;
import ilog.rules.teamserver.model.IlrSession;
import ilog.rules.teamserver.model.IlrSessionHelper;
import ilog.rules.teamserver.model.permissions.IlrPermissionException;
import ilog.rules.teamserver.web.dt.IlrDTDiff;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import rulegovernance.controller.RBRGSessionController;
import rulegovernance.model.utils.BRBodyInfo;
import rulegovernance.model.utils.Constants;
import rulegovernance.model.utils.DTParser;
import rulegovernance.model.utils.MessageLogger;
import rulegovernance.model.utils.Utilities;


public class RuleQualitySessionController extends RBRGSessionController {

	private static Logger log = Logger
			.getLogger(rulegovernance.controller.RuleQualitySessionController.class
					.getName());

	
	private final static String NAME = "name";
	private final static String LOGIN_USER = "loginUser";
	private final static String PERSISTED_DT = "persistedDT";
	private final static String EDITED_DT = "editedDT";
	private final static String ACTION_RULE_BODY = "actionRuleBody";
	private final static String URL = "URL";
	private final static String BASELINE = "baseline";
	//private final static String DELTA_REPORT = "deltaReport";	


	/**
	 * Creates a <code>RBRGSessionController</code> instance. Adds the simple
	 * log file notifications.
	 */
	public RuleQualitySessionController() {
		super();
	}
	
/************** PROTECTED METHODS IMPLEMENTING CUSTOMISATIONS ON RULE GOVERNANCE ASSET *****************/
		
	
	@Override
	protected void addCustomGetStatusVars(Map<String,Object> vars, IlrElementDetails details) {
		
	}
	
	@Override
	protected void addCustomCheckUpdateVars(Map<String,Object> vars ,IlrElementHandle handle,IlrElementDetails details ,EStructuralFeature feature) {

	}
	
	/**
	 * Add additional parameters for check create.  These parameters will be used in the rules to check for create permissions
	 */
	protected void addCustomCheckCreateVars(java.util.Map<String, Object> vars)
			throws IlrPermissionException, IlrObjectNotFoundException {
		
	}
	
	/**
	 * Add additional parameters for check delete.  These parameters will be used in the rules to check for delete permissions
	 */
	protected  void addCustomCheckDeleteVars(java.util.Map<String, Object> vars,
			IlrElementHandle details) throws IlrPermissionException,
			IlrObjectNotFoundException {
		
	}
	
	
	/**
	 * Implement notifications here.  For example email notifications.  It is invoked at the end of the element being committed
	 */
	@Override
	protected void checkIfNotificationRequested(java.util.Map<String, Object> response) {
		
	}




		
	/************** PROTECTED METHODS IMPLEMENTING CUSTOMISATIONS ON RULE GOVERNANCE ASSET *****************/
		
		
		/**
		 * Add additional parameters for on commit.  These parameters will be used in the rules to verify that the rule can be committed
		 */
		@Override
		protected void addCustomOnCommitVars(java.util.Map<String, Object>vars, IlrCommitableObject cobject, IlrElementDetails details) throws IlrPermissionException, IlrObjectNotFoundException  {

		    // Get persisted and edited dt for comparison in rules				
			BRBodyInfo bodyInfo = getBody(cobject, details);
			vars.put(PERSISTED_DT, bodyInfo.persistedDt);
			vars.put(EDITED_DT, bodyInfo.editedDt);
			vars.put(ACTION_RULE_BODY, bodyInfo.actionRuleBody);		
		}	
		
		
		/**
		 * Add additional parameters for element committed.  These parameters will be used in the rules after the rule is committed
		 */
		@Override
		protected void addCustomElementCommittedVars(java.util.Map<String, Object> vars, 
				IlrCommitableObject cobject,
				IlrElementHandle newHandle) {
			
		}
	
		///////////////////////
		

		private BRBodyInfo getBody(IlrCommitableObject cobject,
				IlrElementDetails eDetails) throws IlrObjectNotFoundException {

			BRBodyInfo bodyInfo = new BRBodyInfo();
			IlrElementHandle eHandle = cobject.getRootElementHandle();
			if (eHandle != null) {

				EClass eclass = eHandle.eClass();

				if (eDetails != null) {

					if (log.isLoggable(Level.INFO)) {
						MessageLogger.logMessage(log, Level.INFO,
								"Getting body info");
					}
					if (eclass.getName().equals(Constants.DECISION_TABLE)) {

						bodyInfo.editedDt = getEditedDtInfo(cobject,
								(IlrDecisionTable) eDetails, session);
									
						bodyInfo.persistedDt = getPersistedDtInfo(cobject,
								(IlrDecisionTable) eDetails, session);
						
						//String editedHTML = getEditedDtHtml(cobject,
						//		(IlrDecisionTable) eDetails, session);
						
						//String persistedHTML = getPersistedDtHtml(cobject,
						//		(IlrDecisionTable) eDetails, session);					
						
						//bodyInfo.deltaReport =  ""; //computeDiff(editedHTML, persistedHTML);
					}
				}

				bodyInfo.actionRuleBody = getActionRuleBody(cobject);
			}
			return bodyInfo;
		}

		private String getActionRuleBody(IlrCommitableObject cobject)
				throws IlrObjectNotFoundException {

			String actionRuleBody = "";
			IlrElementHandle eHandle = cobject.getRootElementHandle();
			if (eHandle != null) {

				EClass eclass = eHandle.eClass();

				IlrElementDetails eDetails = cobject.getRootDetails();

				if (eDetails != null) {

					if (eclass.getName().equals(Constants.ACTION_RULE)) {

						actionRuleBody = getActionRule(cobject, actionRuleBody,
								eDetails);
					}
				}
			}
			return actionRuleBody;
		}

		/**
		 * Get the action body, edited value if exists, otherwise the persisted
		 * value
		 * 
		 * @param cobject
		 * @param actionRuleBody
		 * @param eDetails
		 * @return
		 * @throws IlrObjectNotFoundException
		 */
		private String getActionRule(IlrCommitableObject cobject,
				String actionRuleBody, IlrElementDetails eDetails)
				throws IlrObjectNotFoundException {

			Object o = getModifiedElement(cobject, session);

			if (o != null && o instanceof IlrDefinition) {
				// Get edited value first (if exists)
				IlrDefinition def = (IlrDefinition) o;

				if (log.isLoggable(Level.INFO)) {
					MessageLogger.logMessage(log, Level.INFO,
							"Getting action rule body info (edited)");
				}
				if (def != null) {
					actionRuleBody = def.getBody();
				}
			} else {
				// Get Persisted value
				IlrActionRule actionRule = (IlrActionRule) eDetails;

				IlrDefinition def = actionRule.getDefinition();

				if (log.isLoggable(Level.INFO)) {
					MessageLogger.logMessage(log, Level.INFO,
							"Getting action rule body info (persisted)");
				}
				if (def != null) {
					actionRuleBody = def.getBody();
				}
			}
			return actionRuleBody;
		}

		private static Map<String, List<String>> getPersistedDtInfo(
				IlrCommitableObject cobject, IlrDecisionTable dTable,
				IlrSession session) throws IlrObjectNotFoundException {

			if (log.isLoggable(Level.INFO)) {
				MessageLogger.logMessage(log, Level.INFO,
						"Get the persisted DT info");
			}

			Map<String, List<String>> persistedDt = null;

			IlrDefinition persistedDef = getPersistedDt(cobject, session, dTable);

			if (persistedDef != null) {

				// begin complex job of navigating decision table
				IlrDTController dtcontroller = getDTController(session, dTable,
						persistedDef);

				if (dtcontroller != null) {
					try {
						DTParser dtp = new DTParser();
						persistedDt = dtp.parseDt(session, dtcontroller);

					} catch (IlrApplicationException e) {
						e.printStackTrace();
					}
				}
			}

			return persistedDt;
		}

		private static Map<String, List<String>> getEditedDtInfo(
				IlrCommitableObject cobject, IlrDecisionTable dTable,
				IlrSession session) throws IlrObjectNotFoundException {

			if (log.isLoggable(Level.INFO)) {
				MessageLogger.logMessage(log, Level.INFO, "Get the edited DT info");
			}

			Map<String, List<String>> editedDtInfo = null;

			IlrDefinition editedDef = null;

			editedDef = getCurrentlyEditedDt(cobject, session, dTable);

			if (editedDef != null) {

				// begin complex job of navigating decision table
				IlrDTController dtcontroller = getDTController(session, dTable,
						editedDef);

				if (dtcontroller != null) {
					try {
						DTParser dtp = new DTParser();
						editedDtInfo = dtp.parseDt(session, dtcontroller);

					} catch (IlrApplicationException e) {
						e.printStackTrace();
					}
				}
			}

			return editedDtInfo;
		}
		
		private static String getPersistedDtHtml(
				IlrCommitableObject cobject, IlrDecisionTable dTable,
				IlrSession session) throws IlrObjectNotFoundException {

			if (log.isLoggable(Level.INFO)) {
				MessageLogger.logMessage(log, Level.INFO,
						"Get the persisted DT HTML");
			}

			IlrDefinition persistedDef = getPersistedDt(cobject, session, dTable);

			if (persistedDef != null) {

				IlrDTController dtcontroller = getDTController(session, dTable,
						persistedDef);

				if (dtcontroller != null) {
					return IlrDTHelper.getHTMLTable(dtcontroller);
				}	
			}

			return "";
		}	
		
		private  static String getEditedDtHtml(
				IlrCommitableObject cobject, IlrDecisionTable dTable,
				IlrSession session) throws IlrObjectNotFoundException {

			if (log.isLoggable(Level.INFO)) {
				MessageLogger.logMessage(log, Level.INFO, "Get the edited DT html");
			}

			IlrDefinition editedDef = null;

			editedDef = getCurrentlyEditedDt(cobject, session, dTable);

			if (editedDef != null) {

				IlrDTController dtcontroller = getDTController(session, dTable,
						editedDef);

				if (dtcontroller != null) {
					return IlrDTHelper.getHTMLTable(dtcontroller);
				}
			}

			return "";
		}	

		private static IlrDefinition getPersistedDt(IlrCommitableObject cobject,
				IlrSession session, IlrDecisionTable dTable)
				throws IlrObjectNotFoundException {

			// Get persisted value of table
			if (log.isLoggable(Level.INFO)) {
				MessageLogger.logMessage(log, Level.INFO, "Get the persisted DT");
			}
			return dTable.getDefinition();
		}

		private static IlrDefinition getCurrentlyEditedDt(
				IlrCommitableObject cobject, IlrSession session,
				IlrDecisionTable dTable) {
			IlrDefinition def = null;

			if (log.isLoggable(Level.INFO)) {
				MessageLogger.logMessage(log, Level.INFO,
						"Get the currently edited DT");
			}

			Object o = getModifiedElement(cobject, session);

			if (o != null && o instanceof IlrDefinition) {
				def = (IlrDefinition) o;
			}
			return def;
		}

		/**
		 * Return the DT Model Controller of a decision table.
		 * 
		 * @param session
		 *            The Rule Team Server session.
		 * @param dtable
		 *            The decision table.
		 * @return The DT Model Controller.
		 * @throws IlrObjectNotFoundException
		 */
		private static IlrDTController getDTController(IlrSession session,
				IlrDecisionTable dtable, IlrDefinition def)
				throws IlrObjectNotFoundException {
			// Extract the DT controller from the given decision table

			IlrDTController controller = null;

			if (def != null && def.getBody() != null) {
				controller = IlrSessionHelper.getDTController(session,
						session.getElementDetails(dtable), def.getBody(),
						session.getUserLocale());
			}

			return controller;

		}

		/**
		 * Returns the name of the current branch
		 * 
		 * @return current branch name
		 */
		private String getBaselineName() {
			IlrBaseline branch = session.getWorkingBaseline();

			// Get the artifact type (Rule, Function, ...)
			if (branch == null) {
				return "";
			}

			// Retrieve the value of the name property
			EClass eClass = branch.eClass();
			EStructuralFeature f = getFeature(eClass, NAME);
			String blName = (String) branch.getRawValue(f);

			return blName;
		}

		private static Object getModifiedElement(IlrCommitableObject cobject,
				IlrSession session) {
			// Get non persisted value of table
			// WODM 7.5 and later
			 Object o = cobject.getMonoValuedModifiedElement(session.getBrmPackage().getRuleArtifact_Definition());
			 
			// 711 and earlier
			//Object o = cobject.getModifiedElement(session.getBrmPackage()
			//		.getRuleArtifact_Definition());

			return o;
		}

	}

