package customgui;

import java.util.ArrayList;
import java.util.List;

import ilog.rules.teamserver.model.IlrElementDetails;
import ilog.rules.teamserver.model.permalink.IlrPermanentLinkHelper;
import ilog.rules.teamserver.web.IlrRuntimeSessionException;
import ilog.rules.teamserver.web.beans.*;
import ilog.rules.teamserver.web.beans.internal.ActionBean;
import ilog.rules.teamserver.web.beans.internal.TableBean;
import ilog.rules.teamserver.web.util.IlrWebMessages;
import ilog.rules.webc.jsf.IlrWebUtil;
import ilog.rules.webc.jsf.components.table.IlrUITableActionHandler;
import ilog.rules.webc.jsf.components.table.IlrUITableActionHandlerAdapter;
import ilog.rules.webc.jsf.components.table.IlrUITableModel;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

// Referenced classes of package ilog.rules.teamserver.web.beans.internal:
//	            ActionBean

public class CustomTableBean extends
		ilog.rules.teamserver.web.beans.internal.TableBean {

	protected final static String WARNINGS = "warnings";

	/**
	 * Tries to retrieve the status feature from the class.
	 */
	private EStructuralFeature getFeature(EClass eClass, String property) {
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
	 * Returns the status feature for this element, or null if it does not
	 * exist.
	 */
	private EStructuralFeature getWarningFeature(
			IlrElementDetails elementDetails) {

		// Get the artifact type (Rule, Function, ...)
		if (elementDetails == null) {
			return null;
		}

		// Retrieve the value of the status property
		EClass eClass = elementDetails.eClass();
		EStructuralFeature f = getFeature(eClass, WARNINGS);
		return f;
	}

	/**
	 * Returns the status value.
	 */
	private String getWarningPropertyValue(IlrElementDetails elementDetails) {
		EStructuralFeature f = getWarningFeature(elementDetails);
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

	public IlrUITableActionHandler getTableActionHandler() {
		if (tableActionHandler == null)

			tableActionHandler = new IlrUITableActionHandlerAdapter() {

				IlrUITableActionHandler internalTableActionHandler = TableBean
						.getInstance().getTableActionHandler();

				@Override
				public String getIcon(IlrUITableModel tableModel, String s,
						int line) {

					if (s.equals("preview")) {

						IlrElementDetails elt = (IlrElementDetails) tableModel
								.getObject(line);

						String warnings = getWarningPropertyValue(elt);

						if ((warnings != null) && !warnings.equals(""))
							return (new StringBuilder())
									.append(SkinBean.getInstance()
											.getIconsPath()).append("/")
									.append("alert.png")
									.append("\" title=\"" + warnings)
									.toString();
					}

					return internalTableActionHandler.getIcon(tableModel, s,
							line);
				}

				public boolean isLinkActionEnabled(
						ilog.rules.webc.jsf.components.table.IlrUITableModel arg0,
						int arg1, int arg2) {
					return internalTableActionHandler.isLinkActionEnabled(arg0,
							arg1, arg2);
				}

				// Method descriptor #6
				// (Lilog/rules/webc/jsf/components/table/IlrUITableModel;II)Ljava/lang/String;
				public java.lang.String performLinkAction(
						ilog.rules.webc.jsf.components.table.IlrUITableModel arg0,
						int arg1, int arg2) {
					return internalTableActionHandler.performLinkAction(arg0,
							arg1, arg2);
				}

				public List getToolbarActionNames(IlrUITableModel tableModel) {
					return internalTableActionHandler
							.getToolbarActionNames(tableModel);
				}

				public boolean isIconActionActive(IlrUITableModel tableModel,
						String actionName, int i) {
					return internalTableActionHandler.isIconActionActive(
							tableModel, actionName, i);
				}

				public String getIconActionLink(IlrUITableModel tableModel,
						String actionName, int i) {
					return internalTableActionHandler.getIconActionLink(
							tableModel, actionName, i);
				}

				// Method descriptor #6
				// (Lilog/rules/webc/jsf/components/table/IlrUITableModel;II)Ljava/lang/String;
				public java.lang.String getCustomLinkActionURL(
						IlrUITableModel arg0, int arg1, int arg2) {
					return internalTableActionHandler.getCustomLinkActionURL(
							arg0, arg1, arg2);
				}

				// Method descriptor #10
				// (Lilog/rules/webc/jsf/components/table/IlrUITableModel;Ljava/lang/String;I)Ljava/lang/String;
				public java.lang.String performIconAction(IlrUITableModel arg0,
						java.lang.String arg1, int arg2) {
					return internalTableActionHandler.performIconAction(arg0,
							arg1, arg2);
				}

				// Method descriptor #13
				// (Lilog/rules/webc/jsf/components/table/IlrUITableModel;Ljava/lang/String;I)Z
				public boolean isIconActionEnabled(IlrUITableModel arg0,
						java.lang.String arg1, int arg2) {
					return internalTableActionHandler.isIconActionEnabled(arg0,
							arg1, arg2);
				}
			};

		return tableActionHandler;
	}

	public static CustomTableBean getInstance() {
		return (CustomTableBean) IlrWebUtil
				.getBeanInstance(CustomTableBean.class);
	}

	private transient IlrUITableActionHandler tableActionHandler;

}