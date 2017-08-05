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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MessageLogger  {
	/**
	 * Build message for the object <code>obj</code>.
	 * @param obj The object to log
	 */
	public static String buildMessage(Object obj) {
		StringBuffer sb = new StringBuffer();
		
		if (obj instanceof Map<?,?>) {
			Map<?,?> m = (Map<?,?>)obj;
			Set<?> s = m.keySet();
			sb.append("[");
			int count=0;
			for (Iterator<?> iter = s.iterator(); iter.hasNext();) {
				Object key = iter.next();
				if (count!=0) sb.append(",");
				sb.append("(key=").append(key).append(", ");
				Object value = m.get(key);
				sb.append("value=").append(value).append(")");
				count++;
			}
			sb.append("]");
		} else if (obj instanceof List<?>) {
			List<?> l = (List<?>)obj;
			sb.append("[");
			int count=0;
			for (Iterator<?> iter = l.iterator(); iter.hasNext();) {
				Object value = iter.next();
				if (count!=0) sb.append(",");
				sb.append(value).append("");
				count++;
			}
			sb.append("]");
		} else {
			sb.append("[").append(obj).append("]");
		}
		
		return sb.toString();
	}
	
	
	/**
	 * Log messages.
	 */
	public static void logMessage(Logger logger, Level level, String message) {
		logger.log(level, message);		
	}
	
	/**
	 * Log messages.
	 */
	public static void logMessage(Logger logger, Level level, String message, Object obj) {	
		StringBuffer sb = new StringBuffer();
		sb.append(message);
		sb.append("\n");
		String s = buildMessage(obj);
		sb.append(s);
		logger.log(level, sb.toString());
	}
	
	/**
	 * Simple Tester for this class
	 *
	 */
	public static void main (String[] args)  {
		HashMap<String, String> m = new HashMap<String, String>();
		m.put("name", "Fred");
		m.put("age", "4");
		String s = MessageLogger.buildMessage(m);
		System.out.println("The map = "+s);
		
		String s1 = MessageLogger.buildMessage(new Boolean(true));
		System.out.println("The boolean = "+s1);
	}
	
}
