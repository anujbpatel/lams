<%@ include file="/taglibs.jsp"%>

<c:forEach var="user" items="${users}">
	<li role="presentation" id="<c:out value="${user.userId}"/>"><c:out value="${user.login}" /> (<c:out value="${user.firstName}" />&nbsp;<c:out value="${user.lastName}" />) - <c:out value="${user.email}" /></li>
</c:forEach>
