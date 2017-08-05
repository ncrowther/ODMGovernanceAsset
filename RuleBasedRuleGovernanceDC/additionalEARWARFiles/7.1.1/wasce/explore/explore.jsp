<% 
// IBM Confidential     
// OCO Source Materials  
// 5724-Y00 
// © Copyrights by IBM Corp 1987, 2010. - All rights Reserved
// The source code for this program is not published or otherwise
// divested of its trade secrets, irrespective of what has
// been deposited with the U.S. Copyright Office.
%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="ISO-8859-1"%>

<%@ page import="ilog.rules.teamserver.web.beans.NavigationBean"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="/WEB-INF/components.tld" prefix="teamserverex"%>
<%@ taglib uri="http://www.ilog.com/jrules/webc" prefix="webc"%>
<%@ page import="ilog.rules.teamserver.web.beans.internal.TableBean"%>
<%@ page import="customgui.CustomTableBean"%>

<f:loadBundle basename="ilog.rules.teamserver.common.messages" var="bundle" />

<%NavigationBean.setBreadCrumbs(new String[][] { { "explore_nav",
                                                   "#{bundle.explore_key}" }, });
%>
<f:view locale="#{ManagerBean.localeStr}">
	<%@ include file="/partials/page_header.jsp" %>
	<h:form id="paneForm" enctype="multipart/form-data">
    <%@include file="/partials/header.jsp"%>

    <table>
      <tr>
        <td id="TreeMenu">
          <%@include file="/explore/leftMenu.jsp"%>
        </td>
        <td id="Content">

          <h2><h:outputText value="#{SelectionBean.activeViewName}" /></h2>
<%-- JSF version --%>


          <%@include file="/explore/rightToolbar.jsp"%>
          <div id="RuleTable">
            <div id="RuleTableHeader">
              <h:panelGroup>
                <h:outputLabel for="selectMaxRows">
                  <h:outputText value="#{bundle.display_by_key} " />
                </h:outputLabel>
                <h:selectOneMenu id="selectMaxRows"
                                 value="#{RowsPerPageBean.rulesPerPageSelectedOption}"
                                 onchange="this.form.submit();" immediate="true">
                  <f:selectItems value="#{RowsPerPageBean.rowsPerPageOptions}" />
                </h:selectOneMenu>
              </h:panelGroup>
            </div>

            <webc:table model="#{TableBean.explorerTableModel}"
                        actionHandler="#{CustomTableBean.tableActionHandler}"
                        binding="#{TableBean.currentTable}"
                        tableClass="rulelist"
                        oddRowClass="odd-row"
                        evenRowClass="even-row"
                        hyperLinkClass="bluelink"
                        id="ruleTable"/>
          </div>

          <f:subview id="quickEditView">
          <%@include file="/explore/quickEdit.jsp"%>
          </f:subview>
         	<%@ include  file="/partials/rsoediting.jsp" %>
        </td>
      </tr>
    </table>

	</h:form>
	<%@ include  file="/partials/page_footer.jsp" %>
</f:view>
