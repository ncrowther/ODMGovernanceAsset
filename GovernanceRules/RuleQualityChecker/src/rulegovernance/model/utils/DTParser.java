package rulegovernance.model.utils;

import ilog.rules.dt.IlrDTController;
import ilog.rules.dt.model.helper.IlrDTHelper;
import ilog.rules.teamserver.model.IlrApplicationException;
import ilog.rules.teamserver.model.IlrSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.lexer.Page;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.NodeVisitor;

public class DTParser {

	private static Logger log = Logger.getLogger(rulegovernance.model.utils.DTParser.class.getName());
	
	Map<String, String> columnNames = new HashMap<String, String>();
	Map<Integer, String> columnGroups = new HashMap<Integer, String>();
	Map<String, List<String>> dbCols = new HashMap<String, List<String>>();

	int currentCol = 0;

	private  final Map<String, List<String>> parseEntry(String content) {

		NodeVisitor visitor = new NodeVisitor() {

			@Override
			public void visitTag(Tag tag) {
				String name = tag.getTagName();

				/*if ("TABLE".equalsIgnoreCase(name)) {
					String classValue = tag.getAttribute("class");
					if ("DecisionTable".equals(classValue)) {
						if (log.isLoggable(Level.FINE)) {
							MessageLogger.logMessage(log, Level.FINE, "***class is DecisionTable");
						}
					}
				}

				if ("TH".equalsIgnoreCase(name)) {
					String id = tag.getAttribute("id");
					if (id != null && id.startsWith("IlrDT_row")) {
						if (log.isLoggable(Level.FINE)) {
							MessageLogger.logMessage(log, Level.FINE, "***Row: " + id);
						}
					}
				}*/

				if ("TH".equalsIgnoreCase(name)) {
					String id = tag.getAttribute("id");
					String colSpan = tag.getAttribute("colspan");
					if (id != null && id.startsWith("IlrDT_column")) {

						if (colSpan == null) {

							String columnName = tag.toPlainTextString().trim();

							boolean isSubCol = isSubColumn(id);

							if (isSubCol) {
								int col = getColumn(id);
								int subCol = getSubColumn(id);
								columnName = columnGroups.get(col) + "_" + columnName;

								String columnKey = col + "_" + subCol;
								
								if (log.isLoggable(Level.FINE)) {
									MessageLogger.logMessage(log, Level.FINE, "***Column: " + columnName);
									MessageLogger.logMessage(log, Level.FINE, "***ColumnKey: " + columnKey);
								}

								dbCols.put(columnName, new ArrayList<String>());
								columnNames.put(columnKey, columnName);

								return;
							}

							// removed trimming so actual column name used 
							//columnName = trimName(columnName);

							String columnKey = currentCol + "_0";

							if (log.isLoggable(Level.FINE)) {
								MessageLogger.logMessage(log, Level.FINE, "***Column: " + columnName);
								MessageLogger.logMessage(log, Level.FINE, "***ColumnKey: " + columnKey);
							}

							dbCols.put(columnName, new ArrayList<String>());
							columnNames.put(columnKey, columnName);

							currentCol++;

						}
						if (colSpan != null) {
							// removed trimming so actual column group name used 
							//String columnGroup = trimName(tag.toPlainTextString());
							String columnGroup = tag.toPlainTextString().trim();
							
							int colId = getSubColumn(id);

							if (log.isLoggable(Level.FINE)) {
								MessageLogger.logMessage(log, Level.FINE, "***ColumnGroup: " + columnGroup 
										+ " id: " + colId);
							}

							columnGroups.put(colId, columnGroup);

							currentCol++;

						}
					}
				}

				if ("TD".equalsIgnoreCase(name)) {

					String cs = tag.getAttribute("colspan");
					String rs = tag.getAttribute("rowspan");

					if (rs == null) {
						rs = "1";
					}
					
					int rowSpan = Integer.parseInt(rs);

					if (cs == null) {
						cs = "1";
					}

					if (log.isLoggable(Level.FINE)) {
						MessageLogger.logMessage(log, Level.FINE, "***Rowspan: " + rs);
						MessageLogger.logMessage(log, Level.FINE, "***Colspan: " + cs);
					}
					
					int colSpan = Integer.parseInt(cs);

					String id = tag.getAttribute("id");
					if (id != null && id.startsWith("IlrDT_cell")) {
						if (log.isLoggable(Level.FINE)) {
							MessageLogger.logMessage(log, Level.FINE, "***id: " + id);
						}
						String cellName = getCellValue(tag);

						addCells(dbCols, id, cellName, rowSpan, colSpan);
					}
				}
			}

			private String getCellValue(Tag tag) {

				final String NOT_EQUAL = "&#8800;";
				final String LESS_THAN_OR_EQUAL = "&#8804;";
				final String LESS_THAN = "&lt;";
				final String GREATER_TO_OR_EQUAL = "&#8805;";
				final String GREATER_THAN = "&gt;";
				final String NULL = "&nbsp;";
				final String QUOTE = "&quot;";
				final String SINGLE_QUOTE = "'";
				final String AMPHERSAND = "&amp;";
				String cellName = tag.toPlainTextString();

				cellName = cellName.replace(QUOTE, "");

				cellName = cellName.replace(SINGLE_QUOTE, "");

				cellName = cellName.replace(AMPHERSAND, "&");

				if (cellName.startsWith(NOT_EQUAL)) {
					cellName = "not " + cellName.substring(NOT_EQUAL.length());
				}

				if (cellName.startsWith(LESS_THAN_OR_EQUAL)) {
					cellName = "<="
							+ cellName.substring(LESS_THAN_OR_EQUAL.length());
				}

				if (cellName.startsWith(LESS_THAN)) {
					cellName = "<" + cellName.substring(LESS_THAN.length());
				}

				if (cellName.startsWith(GREATER_THAN)) {
					cellName = ">" + cellName.substring(GREATER_THAN.length());
				}

				if (cellName.startsWith(GREATER_TO_OR_EQUAL)) {
					cellName = ">="
							+ cellName.substring(GREATER_TO_OR_EQUAL.length());
				}

				if (cellName.equals(NULL)) {
					cellName = null;
				}

				if (log.isLoggable(Level.FINE)) {
					MessageLogger.logMessage(log, Level.FINE, "***Cell: " + cellName);
				}
				return cellName;
			}

			private void addCells(final Map<String, List<String>> dbCols,
					String id, String cellName, int rowCount, int colCount) {

				int col = getColumn(id);
				int subcol = getSubColumn(id);

				while (colCount > 0) {
					int rowCounter = rowCount;
					while (rowCounter > 0) {

						String colKey = col + "_" + subcol;

						addCell(dbCols, colKey, cellName);
						rowCounter--;
					}

					colCount--;
					subcol++;
				}
			}

			private void addCell(final Map<String, List<String>> dbCols,
					String colKey, String cellName) {

				String colName = columnNames.get(colKey);

				List<String> values = dbCols.get(colName);
				
				if (values != null) {
					values.add(cellName);
				}				

			}

			private int getColumn(String id) {
				String colStr = id.substring(0, id.length() - 2);
				int pos = colStr.lastIndexOf("_");
				colStr = colStr.substring(pos + 1);
				int col = Integer.parseInt(colStr);
				return col;
			}

			private int getSubColumn(String colStr) {

				int pos = colStr.lastIndexOf("_");
				colStr = colStr.substring(pos + 1);
				int col = Integer.parseInt(colStr);
				return col;
			}

			private boolean isSubColumn(String colStr) {
				if (colStr.matches("IlrDT_column_[0-9]_[0-9]")) {
					return true;
				} else {
					return false;
				}
			}

			/*private String trimName(String rawName) {

				Pattern p = Pattern.compile("[^A-Za-z0-9_]+");
				Matcher m = p.matcher(rawName);
				StringBuffer sb = new StringBuffer();
				boolean result = m.find();

				while (result) {
					m.appendReplacement(sb, "");
					result = m.find();
				}

				// Add the last segment of input to the new String
				m.appendTail(sb);

				return sb.toString();

			}*/

		};

		Parser parser = new Parser(new Lexer(new Page(content, "UTF-8")));
		try {
			parser.visitAllNodesWith(visitor);
		} catch (ParserException e) {

			e.printStackTrace();
		}

		return dbCols;

	}
	
	/** Entry point into parsing of DT **/
	public  Map<String, List<String>> parseDt(IlrSession session,
			IlrDTController dtcontroller) throws IlrApplicationException {

		// begin complex job of navigating decision table and converting it to a DT Info
		
		if (log.isLoggable(Level.FINE)) {
			MessageLogger.logMessage(log, Level.FINE, "** Parse DT ");
		}

		String htmlDT = IlrDTHelper.getHTMLTable(dtcontroller);

		Map<String, List<String>> dbCols = parseEntry(htmlDT);
		
		if (log.isLoggable(Level.FINE)) {
			MessageLogger.logMessage(log, Level.FINE, "** Cols:" + dbCols);
		}

		return  dbCols;
	}
	
	/** main used for testing */
	public static void main(String args[]) {

		String content = "<table class=\"DecisionTable\" border=\"1\" width=\"100%\"><thead><tr><th class=\"TopLeftCell\"></th><th id=\"IlrDT_column_0\" style=\"\" title=\"Accountable Type: the type of the accountable of the feature is &lt;an accountable type&gt;.&#10;Error(s) on column:&#10;- Partition(s) have gap(s).&#10;- Overlapping cells.\"><div width='100%' align='center'class=\"warningRight\"><span>Accountable Type</span></div></th><th id=\"IlrDT_column_1\" style=\"\" title=\"Feature Type: the type of the feature is &lt;a feature type&gt;.&#10;Error(s) on column:&#10;- Partition(s) have gap(s).\"><div width='100%' align='center'class=\"warningRight\"><span>Feature Type</span></div></th><th id=\"IlrDT_column_2\" style=\"\" title=\"Cashflow type: the type of the cashflow of the feature is &lt;a cashflow type&gt;.&#10;Error(s) on column:&#10;- Partition(s) have gap(s).\"><div width='100%' align='center'class=\"warningRight\"><span>Cashflow type</span></div></th><th id=\"IlrDT_column_3\" style=\"\" title=\"Business Event: make a business event of type &lt;a string&gt; from the feature\"><span>Business Event</span></th></tr></thead><tr><th id=\"IlrDT_row_0\" style=\"\">1</th><td id=\"IlrDT_cell_0_0_0\" style=\"\" title=\"the type of the accountable of the feature is TRADE.&#10;Rows have gap(s).\"><div align='center' width='100%' style= 'line-height: 110%;'class=\"verticalRightWarning\"><span>TRADE</span></div></td><td id=\"IlrDT_cell_0_1_0\" style=\"\" title=\"the type of the feature is CASHFLOW\"><span>CASHFLOW</span></td><td id=\"IlrDT_cell_0_2_0\" style=\"\" title=\"the type of the cashflow of the feature is &lt;a cashflow type&gt;\"><span></span></td><td id=\"IlrDT_cell_0_3_0\" class=\"action\" style=\"\" title=\"make a business event of type &quot;cashflow&quot; from the feature\"><span class=\"action\"><span>cashflow</span></span></td></tr>";

		Map<String, List<String>> dbCols = null;
		
		DTParser dtp = new DTParser();

		dbCols = dtp.parseEntry(content);

		System.out.println(":" + dbCols);
		
		content = "<table class=\"DecisionTable\" border=\"1\" width=\"100%\"><thead><tr><th class=\"TopLeftCell\"></th><th id=\"IlrDT_column_0\" style=\"\" title=\"Munker Type: the type of the accountable of the feature is &lt;an accountable type&gt;.&#10;Error(s) on column:&#10;- Partition(s) have gap(s).&#10;- Overlapping cells.\"><div width='100%' align='center'class=\"xxx\"><span>Blue Type</span></div></th><th id=\"IlrDT_column_1\" style=\"\" title=\"Feature Type: the type of the feature is &lt;a feature type&gt;.&#10;Error(s) on column:&#10;- Partition(s) have gap(s).\"><div width='100%' align='center'class=\"warningRight\"><span>Feature Type</span></div></th><th id=\"IlrDT_column_2\" style=\"\" title=\"Cashflow type: the type of the cashflow of the feature is &lt;a cashflow type&gt;.&#10;Error(s) on column:&#10;- Partition(s) have gap(s).\"><div width='100%' align='center'class=\"warningRight\"><span>Cashflow type</span></div></th><th id=\"IlrDT_column_3\" style=\"\" title=\"Business Event: make a business event of type &lt;a string&gt; from the feature\"><span>Business Event</span></th></tr></thead><tr><th id=\"IlrDT_row_0\" style=\"\">1</th><td id=\"IlrDT_cell_0_0_0\" style=\"\" title=\"the type of the accountable of the feature is TRADE.&#10;Rows have gap(s).\"><div align='center' width='100%' style= 'line-height: 110%;'class=\"verticalRightWarning\"><span>TRADE</span></div></td><td id=\"IlrDT_cell_0_1_0\" style=\"\" title=\"the type of the feature is CASHFLOW\"><span>CASHFLOW</span></td><td id=\"IlrDT_cell_0_2_0\" style=\"\" title=\"the type of the cashflow of the feature is &lt;a cashflow type&gt;\"><span></span></td><td id=\"IlrDT_cell_0_3_0\" class=\"action\" style=\"\" title=\"make a business event of type &quot;cashflow&quot; from the feature\"><span class=\"action\"><span>mashflow</span></span></td></tr>";

		dbCols = dtp.parseEntry(content);

		System.out.println(":" + dbCols);

	}
}
