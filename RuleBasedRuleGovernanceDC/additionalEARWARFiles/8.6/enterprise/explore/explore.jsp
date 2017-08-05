<%--
 * IBM Confidential
 * OCO Source Materials
 * 5725-B69 5655-Y31 5655-Y17
 * Copyright IBM Corp. 1987, 2012
 * The source code for this program is not published or otherwise
 * divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
--%>


<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="ISO-8859-1"%>

<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="/WEB-INF/components.tld" prefix="teamserverex"%>
<%@ taglib uri="http://www.ilog.com/jrules/webc" prefix="webc"%>

<%@ page import="ilog.rules.teamserver.web.beans.NavigationBean"%>
<%@ page import="ilog.rules.teamserver.web.beans.internal.TableBean"%>
<%@ page import="customgui.CustomTableBean"%>

<f:loadBundle basename="ilog.rules.teamserver.common.messages" var="bundle" />

<%
  NavigationBean.setBreadCrumbs(new String[][]{{"explore_nav", "#{bundle.explore_key}"},});
%>

<f:view locale="#{ManagerBean.localeStr}">
	<%@ include file="/partials/page_header.jsp"%>
	<h:form id="paneForm" enctype="multipart/form-data">
		<%@include file="/partials/header.jsp"%>

		<table id="ExploreContent">
			<tr> 
				<td id="TreeMenu"><%@include file="/explore/leftMenu.jsp"%></td>
				<td id="TreeMenuRightBorder" onmousedown="setMover(event)"><%@include file="/partials/toggle.jsp"%></td> 
				<td id="Content">
					<div id="RightHeader" class="rightheader">
						<div class="rightheaderText">
					 	  <h:outputText escape="false" value="#{SelectionBean.treeBreadcrumbs}" />					 	  
					 	</div>
					</div>
					 
					<%@include file="/explore/rightToolbar.jsp"%>
					
					<div id="quickContentAttachPointError"></div> 										
					
					<div id="RuleTable" class="tundra">
							<teamserverex:ruleTable 
								model="#{TableBean.explorerTableModel}"
								actionHandler="#{CustomTableBean.tableActionHandler}"
								binding="#{TableBean.currentTable}" tableClass="rulelist"
								hyperLinkClass="bluelink" id="ruleTableContent" 
								rendered="#{RuleExplorerBean.hasSmartViews}"/>
					</div>
					<div id="quickContentAttachPoint">
						<%@include file="/explore/quickContent.jsp" %>
					</div>
								
					<%@ include file="/partials/rsoediting.jsp"%>
				</td>
			</tr>
		</table>
	</h:form>
	
	<%@ include file="/partials/page_footer.jsp"%>
</f:view>
