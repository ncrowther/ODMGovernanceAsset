/*
* Licensed Materials - Property of IBM
* 5725-B69 5655-Y17 5724-Y00 5724-Y17 5655-V84
* Copyright IBM Corp. 1987, 2012. All Rights Reserved.
*
* Note to U.S. Government Users Restricted Rights: 
* Use, duplication or disclosure restricted by GSA ADP Schedule 
* Contract with IBM Corp.
*/
package rulegovernance.model.utils;

/**
 * A generic exception class for the sample.
 *
 */
public class ApplicationException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates an <code>ApplicationException</code> instance.
	 */
	public ApplicationException (String msg) {
		super (msg);
	}
	/**
	 * Creates an <code>ApplicationException</code> instance.
	 */
	public ApplicationException (String msg,Exception ex) {
		super (msg,ex);
	}
}
