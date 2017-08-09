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

import ilog.rules.teamserver.client.IlrRemoteSessionFactory;
import ilog.rules.teamserver.model.IlrConnectException;
import ilog.rules.teamserver.model.IlrObjectNotFoundException;
import ilog.rules.teamserver.model.IlrSession;
import ilog.rules.teamserver.model.permissions.IlrPermissionException;

/**
 * Represents a connection to Rule Team Server on an application server.
 *
 */
public class Connection   {
	/**
	 * The factory that gets the session.
	 */
	IlrRemoteSessionFactory factory ;
	/**
	 * The main entry point to Rule Team Server.
	 */
	IlrSession session = null;


	/**
	 * The user used to connect to Rule Team Server.
	 */
	String user;
	/**
	 * The password used to connect to Rule Team Server.
	 */
	String password;
	/**
	 * The URL used to connect to Rule Team Server.
	 */
	String url;
	/**
	 * The Rule Team Server datasource.
	 */
	String datasource;

	/**
	 * Whether a user is connected or not.
	 */
	boolean connected = false;



	/**
	 * @param factory The factory.
	 * @param user The user.
	 * @param password The password.
	 * @param url The URL.
	 * @param datasource The datasource.
	 */
	public Connection(String user, String password, String url, String datasource) {
		super();
		this.user = user;
		this.password = password;
		this.url = url;
		this.datasource = datasource;
		this.factory = new IlrRemoteSessionFactory();
	}

    public void finalize() {
	if (connected)
	    session.endUsage();
    }

	/**
	 * Connects to the Rule Team Server project.
	 *
	 * @param name The name of the project.
	 * @throws IlrConnectException
	 * @throws IlrObjectNotFoundException
	 * @throws IlrPermissionException
	 */
	public void connect () throws IlrConnectException, IlrObjectNotFoundException, IlrPermissionException {
		factory.connect(user, password, url, datasource) ;
		session = factory.getSession();
		session.beginUsage();
		connected = true;
	}

	/**
	 * Disconnects from Rule Team Server.
	 *
	 * @throws IlrConnectException
	 */
	public void disconnect () throws IlrConnectException {
	    session.endUsage();
		session.close();
	}

	/**
	 * @return The current session.
	 * @throws IlrConnectException
	 * @throws IlrPermissionException
	 * @throws IlrObjectNotFoundException
	 */
	public IlrSession getSession() throws IlrObjectNotFoundException, IlrPermissionException, IlrConnectException {
		if (!connected) connect ();
		return session;
	}


	/**
	 * @return The factory used to connect to Rule Team Server.
	 */
	protected IlrRemoteSessionFactory getFactory() {
		return factory;
	}

	/**
	 * Sets the factory to connect to Rule Team Server.
	 * @param factory The factory.
	 */
	protected void setFactory(IlrRemoteSessionFactory factory) {
		this.factory = factory;
	}

}