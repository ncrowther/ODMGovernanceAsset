/*
* Licensed Materials - Property of IBM
* 5725-B69 5655-Y17 5724-Y00 5724-Y17 5655-V84
* Copyright IBM Corp. 1987, 2012. All Rights Reserved.
*
* Note to U.S. Government Users Restricted Rights: 
* Use, duplication or disclosure restricted by GSA ADP Schedule 
* Contract with IBM Corp.
*/
package rulegovernance.installer;

import ilog.rules.teamserver.model.IlrSession;

import java.io.FileNotFoundException;
import java.io.IOException;

import rulegovernance.model.utils.ApplicationException;

public class SimpleQueryHelper {

	/**
	 * Creates the queries in DC.
	 *
	 * @param session The session to DC.
	 * @param projectname The project in which the query will be created.
	 * @throws ApplicationException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void generateDeploymentQuery(IlrSession session, String projectname) 
			throws ApplicationException, FileNotFoundException, IOException {
		
		String query = "Find all business rules such that the status of each business rule is inProd";
		
		try {	
			System.out.println("Query= "+query);
				
			// create a name for the query
			String queryName = "Find Deployed Rules";
			System.out.println("Query Name= "+queryName);
			// Create the queries in DC
			InstallationUtil.createQuery(session, projectname, queryName, query);
			
			System.out.println("Created the query.");
		} catch (Exception e) {
			throw new ApplicationException ("Unable to create query.",e);
		}
	}
	
	/**
	 * Creates the queries in DC.
	 *
	 * @param session The session to DC.
	 * @param projectname The project in which the query will be created.
	 * @throws ApplicationException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void defineCrToTestedQuery(IlrSession session, String projectname) 
			throws ApplicationException, FileNotFoundException, IOException {
		
	
		 String query = "Find all business rules "
			    + "such that the current change request id of each business rule is \"CR123456\" Do " 
				   + "set the status of each business rule to tested" ;
		
		try {	
			System.out.println("Query= "+query);
				
			// create a name for the query
			String queryName = "Promote CR to tested";
			System.out.println("Query Name= "+queryName);
			// Create the queries in DC
			InstallationUtil.createQuery(session, projectname, queryName, query);
			
			System.out.println("Created the query.");
		} catch (Exception e) {
			throw new ApplicationException ("Unable to create query.",e);
		}
	}	
	
	
	/**
	 * Creates the queries in DC.
	 *
	 * @param session The session to DC.
	 * @param projectname The project in which the query will be created.
	 * @throws ApplicationException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void findHistoricalCRQuery(IlrSession session, String projectname) 
			throws ApplicationException, FileNotFoundException, IOException {
		
	
		 String query = "Find all business rules "
			    + "such that the historical change request id of each business rule contains \"CR123456\"";
		
		try {	
			System.out.println("Query= "+query);
				
			// create a name for the query
			String queryName = "Find historical CR";
			System.out.println("Query Name= "+queryName);
			// Create the queries in DC
			InstallationUtil.createQuery(session, projectname, queryName, query);
			
			System.out.println("Created the query.");
		} catch (Exception e) {
			throw new ApplicationException ("Unable to create query.",e);
		}
	}		
	/**
	 * Creates the queries in DC.
	 *
	 * @param session The session to DC.
	 * @param projectname The project in which the query will be created.
	 * @throws ApplicationException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void defineCrToReviewedQuery(IlrSession session, String projectname) 
			throws ApplicationException, FileNotFoundException, IOException {
		
	
		 String query = "Find all business rules "
			    + "such that the current change request id of each business rule is \"CR123456\" Do " 
				   + "set the status of each business rule to releasedForProd" ;
		
		try {	
			System.out.println("Query= "+query);
				
			// create a name for the query
			String queryName = "Promote CR to reviewed";
			System.out.println("Query Name= "+queryName);
			// Create the queries in DC
			InstallationUtil.createQuery(session, projectname, queryName, query);
			
			System.out.println("Created the query.");
		} catch (Exception e) {
			throw new ApplicationException ("Unable to create query.",e);
		}
	}		

}
