/*
 * Licensed Materials - Property of IBM
 * 5725-B69 5655-Y17 5724-Y00 5724-Y17 5655-V84
 * Copyright IBM Corp. 1987, 2017. All Rights Reserved.
 *
 * Note to U.S. Government Users Restricted Rights: 
 * Use, duplication or disclosure restricted by GSA ADP Schedule 
 * Contract with IBM Corp.
 */

package decisionServiceDeployment;

import ilog.rules.teamserver.brm.IlrBaseline;
import ilog.rules.teamserver.brm.IlrBrmPackage;
import ilog.rules.teamserver.brm.IlrPackageKind;
import ilog.rules.teamserver.brm.IlrRuleProject;
import ilog.rules.teamserver.brm.IlrServer;
import ilog.rules.teamserver.brm.IlrServerKind;
import ilog.rules.teamserver.dsm.IlrDeployment;
import ilog.rules.teamserver.model.IlrApplicationException;
import ilog.rules.teamserver.model.IlrArchiveOutput;
import ilog.rules.teamserver.model.IlrConnectException;
import ilog.rules.teamserver.model.IlrDefaultSearchCriteria;
import ilog.rules.teamserver.model.IlrElementError;
import ilog.rules.teamserver.model.IlrObjectNotFoundException;
import ilog.rules.teamserver.model.IlrSession;
import ilog.rules.teamserver.model.IlrSessionHelper;
import ilog.rules.teamserver.model.permissions.IlrPermissionException;
import ilog.rules.teamserver.model.permissions.IlrRoleRestrictedPermissionException;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;

/** */
public class DeployService {

	/* Connection details for your Decision Center Instance */
	private String uri;
	private String datasource;
	private String username;
	private String password;

	private Connection conn;
	private IlrSession session;

	public DeployService(String uri, String datasource, String username,
			String password) {
		this.uri = uri;
		this.datasource = datasource;
		this.username = username;
		this.password = password;
	}

	/**
	 * The main entry point.
	 * 
	 * -rtsurl      : The URL to connect to Decision Center [server=http://localhost:9080/teamserver]. 
	 * -rtsuser     : The user to connect to Decision Center [rtsAdmin].
	 * -rtspassword : The password to connect to Decision Center [rtsAdmin].
	 * -rtsdatasource : The Decision Center datasource [jdbc/ilogDataSource].
	 * -projectName   : The decision service name [Loan Validation Service].
	 * -branch        : The branch name [Spring Release].
	 * -deploymentName: The deployment configuration name [test deployment].
	 * -serverName    : the execution server where to deploy [Sample].
	 * -redeploy      : a boolean to tell if it is a redeployment or not [true].
	 * 
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception {

		/*
		 * Process the program arguments
		 */
		String uri = "http://localhost:9081/teamserver";
		String datasource = null;
		String username = "rtsAdmin";
		String password = "rtsAdmin";
		String projectName = "Loan Validation Service";
		String branch = "Spring Release";
		String deploymentName = "test deployment";
		String redeploy = "true";
		String serverName = null;
		String outputDirectory = "";

		for (int i = 0; i < args.length - 1; i++) {
			if (args[i].equals("-rtsurl")) {
				uri = args[++i];
			} else if (args[i].equals("-rtsdatasource")) {
				datasource = args[++i];
			} else if (args[i].equals("-rtsuser")) {
				username = args[++i];
			} else if (args[i].equals("-rtspassword")) {
				password = args[++i];
			} else if (args[i].equals("-projectName")) {
				projectName = args[++i];
			} else if (args[i].equals("-branch")) {
				branch = args[++i];
			} else if (args[i].equals("-deploymentName")) {
				deploymentName = args[++i];
			} else if (args[i].equals("-redeploy")) {
				redeploy = args[++i];
			} else if (args[i].equals("-serverName")) {
				serverName = args[++i];
			}  else if (args[i].equals("-outputDirectory")) {
				outputDirectory = args[++i];
			} 
		}
		DeployService sample = new DeployService(uri, datasource, username, password);
		/*
		 * The first step is to connect to Decision Center and start a session
		 */
		sample.startSession();

		sample.deploy(projectName, branch, deploymentName, redeploy, serverName, outputDirectory);

		/*
		 * Before the program exits, we must clean up our server connection
		 */
		sample.endSession();
	}

	/**
	 * Connects to  Decision Center and starts a session
	 * 
	 * @throws IlrObjectNotFoundException
	 * @throws IlrPermissionException
	 * @throws IlrConnectException
	 */
	private void startSession() throws IlrObjectNotFoundException,
			IlrPermissionException, IlrConnectException {

		conn = new Connection(username, password, uri, datasource);
		conn.connect();
		session = conn.getSession();
		/*
		 * Attach the session to this thread. If we don't do this, will get
		 * IllegalStateExceptions when trying to access things from the session
		 */
		session.beginUsage();
	}

	/**
	 * Ends the session and disconnects from Decision Server
	 * 
	 * @throws IlrConnectException
	 */
	private void endSession() throws IlrConnectException {

		/*
		 * It's important to release the session, otherwise memory leaks on the
		 * server can occur
		 */
		session.endUsage();
		conn.disconnect();
	}

	/**
	 * Use  IlrDeploymentFacility:deployDSRuleAppArchive API to deploy the decision service
	 * Errors are catched and printed out.
	 */
	private void deploy(String projectName, String branch, String deploymentName, String redeploy, String serverName, String outputDirectory) {
		String deployWhere = " on both RES and test directory";
		if (serverName == null || serverName.isEmpty()) {
			deployWhere = " on test directory";
			}
		System.out.println("Deploy project " + projectName + " branch " + branch + " using the deployment configuration " + deploymentName + deployWhere);
		try {
			IlrRuleProject project = IlrSessionHelper.getProjectNamed(session,
					projectName);
			IlrBaseline baseline = IlrSessionHelper.getBaselineNamed(session,
					project, branch);
			session.setWorkingBaseline(baseline);
			IlrDeployment deployment = (IlrDeployment) IlrSessionHelper
					.getElementFromPath(session, deploymentName,
							IlrPackageKind.DEPLOYMENT_LITERAL);
			List<IlrServer> ilrServers = computeServers(deployment, serverName);
			HashMap<String, String> rulesetsVersionsMap =  new HashMap<String, String>();
			IlrArchiveOutput output = session.deployDSRuleAppArchive(
					deployment, ilrServers, null,
					"true".equals(redeploy), rulesetsVersionsMap);
			Iterator<IlrElementError> it = output.getCheckingErrors().iterator();
			if (!it.hasNext()) {
				System.out.println("Deployment succeed.");
				writeArchive(output, outputDirectory);
			}
			while (it.hasNext()) {
				System.out.println("BUILD FAILED: ");
				IlrElementError error = it.next();
				System.out.println(IlrElementError.errorAsString(error, session));
			}
		} catch (IlrApplicationException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Write the archive file in a file.
	 */
	private void writeArchive(IlrArchiveOutput output, String outputDirectory) {
		if (outputDirectory != null && !"".equals(outputDirectory.trim())) {
			try {
				String filename = (String) output.getAttribute(IlrArchiveOutput.DEPLOYMENT_NAME_ATTRIBUTE)
							+ ".jar";
				File archive = new File(outputDirectory, filename).getCanonicalFile();
				FileOutputStream stream = new FileOutputStream(archive);
				try {
					stream.write(output.getBytes());
				} finally {
					stream.close();
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	/**
	 * Gets the IlrServer server instance from the server name.
	 */
	
	@SuppressWarnings("unchecked")
	private   IlrServer getExecutionServerNamed(String name) throws IlrObjectNotFoundException,IlrRoleRestrictedPermissionException {
		IlrBrmPackage brm = session.getBrmPackage();

		List<EAttribute> features = new ArrayList<EAttribute> ();
		features.add(brm.getModelElement_Name());
		features.add(brm.getServer_ServerKind());
		List<Object> values = new ArrayList<Object>();
		values.add(name);
		values.add(IlrServerKind.RES_LITERAL.getLiteral());

		List<IlrServer> results = session.findElements(new IlrDefaultSearchCriteria(brm.getServer(), features, values));
		if (results.size() == 1)
			return results.get(0);

		return null;
	}
	
	private List<IlrServer> computeServers(IlrDeployment deployment, String serverName)
			throws IlrApplicationException, IlrObjectNotFoundException {
		List<IlrServer> ilrServers = new ArrayList<IlrServer>();
		if (serverName != null && !serverName.isEmpty()) {
			serverName = serverName.trim();
			IlrServer server = getExecutionServerNamed(serverName);
			ilrServers.add(server);
			}
		return ilrServers;
	}

}
