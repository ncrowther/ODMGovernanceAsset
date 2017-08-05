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

public class SimpleRESHelper {

	/**
	 * Creates the extractor in DC.
	 *
	 * @param session The session to DC.
	 * @param projectname The project in which the query will be created.
	 * @throws ApplicationException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void generateExtractor(IlrSession session, String projectname) 
			throws ApplicationException, FileNotFoundException, IOException {
		
		String query = "Find all business rules such that the status of each business rule is deployed";
		
		try {	
			System.out.println("Query= "+query);
				
			// create a name for the extractor
			String extractorName = "extractDeployed";
			System.out.println("Query Name= "+extractorName);
			// Create the extractor in DC
			InstallationUtil.createExtractor(session, projectname, extractorName, query);
			
			System.out.println("Created the extractor.");
		} catch (Exception e) {
			throw new ApplicationException ("Unable to create extractor.",e);
		}
	}

}
