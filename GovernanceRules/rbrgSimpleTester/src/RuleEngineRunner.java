/* 
 * Licensed Materials - Property of IBM 
 * 5724-X98 
 * (c) Copyright IBM Corp. 1987, 2010. All Rights Reserved. 
 * 
 * Note to U.S. Government Users Restricted Rights: 
 * Use, duplication or disclosure restricted by GSA ADP Schedule 
 * Contract with IBM Corp. 
 */

import ilog.rules.archive.IlrJarArchiveLoader;
import ilog.rules.archive.IlrRulesetArchiveLoader;
import ilog.rules.engine.IlrBadContextException;
import ilog.rules.engine.IlrContext;
import ilog.rules.engine.IlrExceptionHandler;
import ilog.rules.engine.IlrParameterMap;
import ilog.rules.engine.IlrRuleset;
import ilog.rules.engine.IlrRulesetArchiveParser;
import ilog.rules.engine.IlrUndefinedMainTaskException;
import ilog.rules.engine.IlrUserRuntimeException;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.jar.JarInputStream;

public class RuleEngineRunner {

	private IlrRuleset ruleset;

	private IlrContext engine;

	private IlrParameterMap inputs = new IlrParameterMap();

	private IlrParameterMap outputs;

	/**
	 * Loads the ruleset from the provided file Sets a PrintWriter to send
	 * errors messages
	 * 
	 * @param the
	 *            ruleset archive path
	 * @throws Exception
	 *             when parsing detected errors
	 */
	public void loadRuleset(String rulesetPath) throws Exception {
		JarInputStream is = new JarInputStream(new FileInputStream(new File(
				rulesetPath)));

		IlrRulesetArchiveLoader rulesetloader = new IlrJarArchiveLoader(is);
		IlrRulesetArchiveParser rulesetparser = new IlrRulesetArchiveParser();

		ruleset = new IlrRuleset();
		rulesetparser.setRuleset(ruleset);

		if (rulesetparser.parseArchive(rulesetloader))
			return;

		String[] errors = rulesetparser.getErrors();

		if (errors != null) {
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < errors.length; i++) {
				buf.append(errors[i]).append('\n');
			}
			throw new Exception(buf.toString());
		}
	}

	/**
	 * Prepares the engine with the loaded ruleset, and sets exception handlers
	 * for the execution
	 * 
	 * @throws IlrBadContextException
	 *             in case of mismatch between provided (in this method) and
	 *             requested (in the ruleset) context classes
	 */
	public void buildEngine() throws IlrBadContextException {
		engine = new IlrContext(ruleset);

		IlrExceptionHandler exceptionHandler = new IlrExceptionHandler() {
			/**
		 * 
		 */
			private static final long serialVersionUID = 1L;

			public boolean handleException(IlrUserRuntimeException ex) {
				if (ex.isInConditions())
					return false;
				else
					throw ex;
			}
		};
		engine.setExceptionHandler(exceptionHandler);
		engine.setRuleflowExceptionHandler(exceptionHandler);
	}

	/**
	 * Inserts all the objects of the collection into the engine working memory
	 * 
	 * @param the
	 *            collection of objects to insert
	 * @throws IlrUserRuntimeException
	 *             when provided objects throw exceptions during their insertion
	 */
	public void declareWorkingMemoryObjects(Collection<Object> objects)
			throws IlrUserRuntimeException {
		if (objects == null)
			return;
		Iterator<Object> it = objects.iterator();
		while (it.hasNext())
			engine.insert(it.next());
	}

	/**
	 * Runs the ruleset (ruleflow or rules) on the input parameters and with the
	 * current working memory
	 * 
	 * @return the number of fired rules
	 * @throws IlrUserRuntimeException
	 *             , IlrUndefinedMainTaskException
	 */
	public int executeRules() throws IlrUserRuntimeException,
			IlrUndefinedMainTaskException {
		engine.setParameters(inputs);
		outputs = engine.execute();
		return outputs.getIntValue("ilog.rules.firedRulesCount");
	}

	/**
	 * Gets the execution output results
	 * 
	 * @return the output parameter map
	 */
	public IlrParameterMap getOutputResult() {
		return outputs;
	}

	/**
	 * Resets the engine for execution of the same ruleset on other objects
	 */
	public void reset() {
		engine.retractAll();
		engine.cleanRulesetVariables();
		engine.resetRuleflow();
	}

	/**
	 * Cleans up the engine
	 */
	public void end() {
		ruleset = null;
		if (engine != null) {
			engine.end();
			engine = null;
		}
	}

	/**
	 * Runs the engine with the provided ruleset archive
	 */
	public static void main(String[] args) {
		String rulesetPath = "rbrgRules.jar";

		RuleEngineRunner runner = new RuleEngineRunner();

		try {
			// Load the ruleset
			runner.loadRuleset(rulesetPath);

			// Prepare an engine for this ruleset
			runner.buildEngine();

			// create some test data for testing inside RS
			java.util.Map<String, Object> vars = new java.util.HashMap<String, Object>();

		
			
			vars.put("oldDate","null"); 
			vars.put("oldProperty","ReadyForApproval"); 
			vars.put("type","brm.ActivityCommentEvent"); 
			vars.put("updateType","ActivityStatusUpdate"); 
			vars.put("lastChangedOn","Fri Aug 04 09:19:21 BST 2017"); 
			vars.put("editedDT","null"); 
			vars.put("versionComment","null"); 
			vars.put("actionRuleBody",""); 
			vars.put("sourceArtifact","0"); 
			vars.put("additionalDetails","autoStatusUpdate=true"); 
			vars.put("baseline","Nov Release"); 
			vars.put("lastChangedBy","null"); 
			vars.put("loginUser","authorizedUser:4b77df43-95bc-4256-8be2-cd47af6dd82f"); 
			vars.put("majorVersion","0"); 
			vars.put("forUser","null"); 
			vars.put("sourceRuleArtifactId","0"); 
			vars.put("sourceRuleProject","MortgageSegmentationService"); 
			vars.put("sourceRuleArtifact","null"); 
			vars.put("newProperty","Complete"); 
			vars.put("minorVersion","0"); 
			vars.put("requestType","ONCOMMIT"); 
			vars.put("oldName","null"); 
			vars.put("createdOn","Fri Aug 04 09:19:21 BST 2017"); 
			vars.put("oldDesc","null"); 
			vars.put("sourceArtifactId","0"); 
			vars.put("persistedDT","null"); 
			vars.put("createdBy","authorizedUser:4b77df43-95bc-4256-8be2-cd47af6dd82f"); 
			vars.put("newDate","null"); 
			vars.put("sourceBranch","null"); 
			vars.put("sourceArtifactType","0"); 
			vars.put("newDesc","null"); 

			System.out.println("Request = " + vars);
			runner.inputs.setParameter("request", vars);

			// Execute the rules
			runner.executeRules();

			java.util.Map<?, ?> response = (java.util.Map<?, ?>) runner
					.getOutputResult().getObjectValue("response");

			System.out.println("Response = " + response);

			//java.util.List<?> errors = (java.util.List<?>) runner
			//		.getOutputResult().getObjectValue("errorList");
			//System.out.println("Errors = " + errors);

			runner.reset();

		} catch (IlrBadContextException bcException) {
			bcException.printStackTrace();
		} catch (IlrUserRuntimeException urException) {
			urException.printStackTrace();
		} catch (IlrUndefinedMainTaskException umtException) {
			umtException.printStackTrace();
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			runner.end();
		}
	}
};
