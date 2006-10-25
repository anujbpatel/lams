<%@ page contentType="text/html; charset=utf-8" language="java" %>

<%@ taglib uri="tags-html-el" prefix="html-el" %>
<%@ taglib uri="tags-core" prefix="c" %>
<%@ taglib uri="tags-bean" prefix="bean" %>
<%@ taglib uri="tags-logic" prefix="logic" %>
<%@ taglib uri="tags-fmt" prefix="fmt" %>
<%@ taglib uri="tags-lams" prefix="lams" %>

<script language="javascript" type="text/JavaScript">
function toggleCheckboxes(roleIndex, object){
	<logic:iterate id="userBean" name="UserOrgRoleForm" property="userBeans" indexId="beanIndex" >
	document.UserOrgRoleForm.elements[roleIndex+1+<c:out value="${numroles}" />*(<c:out value="${beanIndex}" />+1)].checked=object.checked;
	</logic:iterate>
}
</script>

<h2>
	<a href="orgmanage.do?org=1"><fmt:message key="admin.course.manage" /></a>
    <logic:notEmpty name="pOrgId">
        : <a href="orgmanage.do?org=<bean:write name="pOrgId" />"><bean:write name="pOrgName"/></a>
    </logic:notEmpty>
    : <a href="<logic:equal name="orgType" value="3">user</logic:equal><logic:notEqual name="orgType" value="3">org</logic:notEqual>manage.do?org=<bean:write name="UserOrgRoleForm" property="orgId" />">
		<bean:write name="orgName"/></a>
	: <fmt:message key="admin.user.assign.roles" />
</h2>
<p>&nbsp;</p>

<html-el:form action="/userorgrolesave.do" method="post">
<html-el:hidden property="orgId" />

<table class="alternative-color" width=100%>
<tr>
	<th><fmt:message key="admin.user.login"/></th>
	<logic:iterate id="role" name="roles" indexId="roleIndex">
		<th><input type="checkbox" 
					name="<c:out value="${roleIndex}" />" 
					onclick="toggleCheckboxes(<c:out value="${roleIndex}" />, this);" 
					onkeyup="toggleCheckboxes(<c:out value="${roleIndex}" />, this);" />
			<fmt:message>role.<lams:role role="${role.name}" /></fmt:message></th>
	</logic:iterate>
</tr>
<logic:iterate id="userBean" name="UserOrgRoleForm" property="userBeans" indexId="beanIndex">
	<tr>
		<td>
			<c:out value="${userBean.login}" /><c:if test="${!userBean.memberOfParent}"> *<c:set var="parentFlag" value="true" /></c:if>
		</td>
		<logic:iterate id="role" name="roles">
			<td>
				<html-el:multibox property="userBeans[${beanIndex}].roleIds" value="${role.roleId}" />&nbsp;
			</td>
		</logic:iterate>
	</tr>
</logic:iterate>
<tr>
	<td></td>
	<td colspan=<c:out value="${numroles}" /> align="right">
		<html-el:submit><fmt:message key="admin.save"/></html-el:submit>
		<html-el:reset><fmt:message key="admin.reset"/></html-el:reset>
		<html-el:cancel><fmt:message key="admin.cancel"/></html-el:cancel>
	</td>
</tr>
</table>
<c:if test="${parentFlag}">
<p><fmt:message key="msg.user.add.to.parent.group" /></p>
</c:if>
</html-el:form>
