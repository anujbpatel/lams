<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
		"http://www.w3.org/TR/html4/loose.dtd">

<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ taglib uri="tags-lams" prefix="lams" %>
<%@ taglib uri="tags-fmt" prefix="fmt" %>
<%@ taglib uri="tags-core" prefix="c" %>

<lams:html>
<lams:head>

<c:set var="enableFlash"><lams:LearnerFlashEnabled/></c:set>
	
<c:choose>
	<c:when test="${enableFlash}">
		<META HTTP-EQUIV="Refresh" CONTENT="0;URL=learning/mainflash.jsp?lessonID=<c:out value="${lessonID}"/>&portfolioEnabled=<c:out value="${portfolioEnabled}"/>&presenceEnabledPatch=<c:out value="${presenceEnabledPatch}"/>&presenceImEnabled=<c:out value="${presenceImAvailable}"/>&presenceUrl=<c:out value="${presenceUrl}"/>&title=<c:out value="${title}"/><c:if test="${mode != null}">&mode=<c:out value="${mode}"/></c:if>">
	</c:when>
	<c:otherwise>
		<META HTTP-EQUIV="Refresh" CONTENT="0;URL=learning/mainnoflash.jsp?lessonID=<c:out value="${lessonID}"/>&portfolioEnabled=<c:out value="${portfolioEnabled}"/>&presenceEnabledPatch=<c:out value="${presenceEnabledPatch}"/>&presenceImEnabled=<c:out value="${presenceImAvailable}"/>&presenceUrl=<c:out value="${presenceUrl}"/>&createDateTime=<c:out value="${createDateTime}"/>&title=<c:out value="${title}"/><c:if test="${mode != null}">&mode=<c:out value="${mode}"/></c:if>">
	</c:otherwise>
</c:choose>

	<TITLE><fmt:message key="title.learner.window"/></TITLE>
	<lams:css/>
</lams:head>

<body class="stripes">
	
		<div id="content">
			<p><fmt:message key="msg.loading.learner.window"/></p>
		</div>
	   
		<div id="footer">
		</div><!--closes footer-->

</BODY>
	
</lams:html>
