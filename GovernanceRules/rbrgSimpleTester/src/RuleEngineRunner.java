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

import java.awt.List;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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

		    // D:\ic\GovernanceAsset\github\GovernanceRules\rbrgSimpleTester\rbrgRules.jar
			
			// replace \[.*\*  with ""
			// replace / with \
			
			ArrayList values = new java.util.ArrayList();
		
			Object o = new Object();
			
			o.
			
			values.add("Paul");
			vars.put("baselineKind","Branch"); 
			vars.put("readableVersion","4"); 
			vars.put("type","brm.ActivityCommentEvent"); 
			vars.put("versionComment","null"); 
			vars.put("authors","[Paul:Working]"); 
			vars.put("groups","[]"); 
			vars.put("status","InProgress"); 
			vars.put("sourceRuleProject","RoutingAndSecurity"); 
			vars.put("parent","Jan Release"); 
			vars.put("minorVersion","0"); 
			vars.put("createdOn","Tue Aug 08 18:09:14 BST 2017"); 
			vars.put("persistedDT","null"); 
			vars.put("createdBy","Bea"); 
			vars.put("owner","Bea"); 
			vars.put("uuid","ce0fe9bf-4e07-455f-88a4-0e35d62e97b8"); 
			vars.put("maxVersionId","898"); 
			vars.put("newDesc","null"); 
			vars.put("approvers","[Paul:NotReviewed]"); 
			vars.put("oldProperty","null"); 
			vars.put("oldDate","null"); 
			vars.put("depName","null"); 
			vars.put("frozen","true"); 
			vars.put("updateType","ActivityAuthorAssign"); 
			vars.put("lastChangedOn","Tue Aug 08 18:09:14 BST 2017"); 
			vars.put("editedDT","null"); 
			vars.put("eventIdString","null"); 
			vars.put("actionRuleBody",""); 
			vars.put("sourceArtifact","0"); 
			vars.put("name","Jan Activity"); 
			vars.put("additionalDetails","null"); 
			vars.put("baseline","Jan Activity"); 
			vars.put("lastChangedBy","null"); 
			vars.put("loginUser","Bea"); 
			vars.put("forUser","null"); 
			vars.put("majorVersion","0"); 
			vars.put("sourceRuleArtifactId","0"); 
			vars.put("sourceRuleArtifact","null"); 
			vars.put("newProperty","Abu"); 
			vars.put("requestType","ONCOMMIT"); 
			vars.put("securityEnforced","false"); 
			vars.put("oldName","null"); 
			vars.put("securityInherited","true"); 
			vars.put("oldDesc","null"); 
			vars.put("sourceArtifactId","0"); 
			vars.put("newDate","null"); 
			vars.put("sourceBranch","null"); 
			vars.put("targetDate","Tue Aug 22 00:00:00 BST 2017"); 
			vars.put("documentation",""); 
			vars.put("sourceArtifactType","0");  
			/*
			Map edt = new HashMap();
			ArrayList values = new java.util.ArrayList();
			values.add("A");
			
			edt.put("Receiving Bic 8", values);
			vars.put("baselineKind","Branch"); 
			vars.put("approvers","[Bea:NotReviewed]"); 
			vars.put("readableVersion","4"); 
			vars.put("locale","en_US"); 
			vars.put("depName","null"); 
			vars.put("frozen","true"); 
			vars.put("type","brm.DecisionTable"); 
			vars.put("lastChangedOn","Tue Aug 08 10:45:04 BST 2017"); 
			vars.put("editedDT", edt);
		    vars.put("eventIdString","null"); 
			vars.put("authors","[Bea:Working]"); 
			vars.put("actionRuleBody",""); 
			vars.put("priority","null"); 
			vars.put("name","D01DEADQ - DEVICE"); 
			vars.put("baseline","Jan Activity"); 
			vars.put("effectiveDate","null"); 
			vars.put("rulePackage","D01DEADQ"); 
			vars.put("overriddenRules","null"); 
			vars.put("groups","[]"); 
			vars.put("lastChangedBy","rtsAdmin"); 
			vars.put("loginUser","Bea"); 
			vars.put("template","null"); 
			vars.put("tags", "null"); 
			vars.put("expirationDate","null"); 
			vars.put("definition","null"); 
			vars.put("status","new"); 
			vars.put("parent","Jan Release"); 
			vars.put("requestType","ONCOMMIT"); 
			vars.put("securityEnforced","false"); 
			vars.put("securityInherited","true"); 
			vars.put("createdOn","Tue Aug 08 10:45:04 BST 2017"); 
			vars.put("project","RoutingDs"); 
			vars.put("createdBy","rtsAdmin"); 
			vars.put("persistedDT","{Message Type=[103, 202, 103, 202, 103, 202, 202, 103, 202], Receiving Bic 8=[CHASCN2G, CHASCN2G, CHASCNBC , CHASCNBC , CHASCNBJ , CHASCNBJ , CHASCNBJ , CHASCNSH, CHASCNSH], End Point_Reason=[, , , , , , , , ], End Point_Routes=[CPSDQLB, CPSDQLB, CPSDQLB, CPSDQLB, CPSDQLB, CPSDQLB, CPSDQLB, CPSDQLB, CPSDQLB], End Point_Devices=[DQLB, DQLB, DQLB, DQLB, DQLB, DQLB, DQLB, DQLB, DQLB], Sending Branch=[TRO, TRO, TRO, TRO, TRO, TRO, XXX, TRO, TRO], Sending Bic 8=[CHASCN2G, CHASCN2G, CHASCNBC, CHASCNBC, CHASCNBJ, CHASCNBJ, CHASCNBJ, CHASCNSH, CHASCNSH], Receiving Branch=[XXX, XXX, XXX, XXX, XXX, XXX, XXX, XXX, XXX], End Point_Exclusivity=[false, false, false, false, false, false, false, false, false], Currency=[CNY, CNY, CNY, CNY, CNY, CNY, CNY, CNY, CNY]}"); 
			vars.put("targetDate","Tue Aug 22 00:00:00 BST 2017"); 
			vars.put("documentation","null"); 
			vars.put("owner","Bea"); 
			vars.put("active","true"); 
			vars.put("categories","[]"); 
			vars.put("maxVersionId","564"); 
			vars.put("uuid","cdd59d01-90a5-448b-a29d-7892a6cc5874"); 
			vars.put("group","null"); 
			*/


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
