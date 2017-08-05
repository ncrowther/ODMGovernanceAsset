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
import ilog.rules.teamserver.model.IlrSessionHelper;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EStructuralFeature;

import rulegovernance.model.utils.ApplicationException;

public class SimpleSmartViewHelper {

	/**
	 * Creates the technical smart views in DC. For each status, a smart view is created.
	 *
	 * @param session The session to DC.
	 * @param projectname The project in which the smart view will be created.
	 * @param statusValues The status values
	 * @param statusProperty The name of the status property
	 * @throws ApplicationException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void generateSmartViews(IlrSession session,
			String projectname, String statusValues, String statusProperty)
			throws ApplicationException, FileNotFoundException, IOException {

		String query = "Find all business rules";

		try {
			// create a name for the smart view
			String viewname = InstallationUtil
					.makeSmartViewName("CurrentCRs");
			
			System.out.println("Smart View Name= " + viewname);
			
			// Delete Old smart views (if any)
			InstallationUtil.removeView(session, projectname, viewname);
			
			// Create using currentChangeRequestId and status to sort the view
			EStructuralFeature[] features = new EStructuralFeature[2];
			features[0] = getFeature(session, "currentChangeRequestId");
			features[1] = getFeature(session, "status");	
			IlrSessionHelper.createSmartView(session, viewname, query, features);

			System.out.println("Created  the CR Smart View");
		} catch (Exception e) {
			throw new ApplicationException("Unable to create smart view.", e);
		}
	}

	private static EStructuralFeature getFeature(IlrSession session, String featureName) {
		EList<?> eFeatures = session.getModelInfo().getBrmPackage().getBusinessRule().getEAllStructuralFeatures();

		// Loop through all properties (features) of the element 
		for (int i = 0; i < eFeatures.size(); i++) {
			// Get the next feature
			EStructuralFeature feature = (EStructuralFeature) eFeatures.get(i);
			
			if (feature.getName().equals(featureName)) {
				System.out.println("***FOUND feature" + feature);
				return feature;
			}
		}
		
		return null;
	}

}
