<%@ include file="/common/taglibs.jsp"%>

<c:if test="${not empty param.sessionMapID}">
	<c:set var="sessionMapID" value="${param.sessionMapID}" />
</c:if>
<c:set var="sessionMap" value="${sessionScope[sessionMapID]}" />
<c:set var="mode" value="${sessionMap.mode}" />
<c:set var="toolSessionID" value="${sessionMap.toolSessionID}" />
<c:set var="kaltura" value="${sessionMap.kaltura}" />

<script type="text/javascript">
	function disableFinishButton() {
		document.getElementById("finishButton").disabled = true;
	}
         function submitForm(methodName){
                var f = document.getElementById('messageForm');
                f.submit();
        }
</script>

<div data-role="header" data-theme="b" data-nobackbtn="true">
	<h1>
		${kaltura.title}
	</h1>
</div>

<div data-role="content">
	<c:choose>
		<c:when test="${empty sessionMap.submissionDeadline}">
		<p>
			<fmt:message key="message.runOfflineSet" />
		</p>
		</c:when>
		<c:otherwise>
			<div class="warning">
				<fmt:message key="authoring.info.teacher.set.restriction" >
					<fmt:param><lams:Date value="${sessionMap.submissionDeadline}" /></fmt:param>
				</fmt:message>	
			</div>
		</c:otherwise>		
	</c:choose>
</div>


<div data-role="footer" data-theme="b" class="ui-bar">
	<span class="ui-finishbtn-right">
	
		<c:if test="${mode == 'learner' || mode == 'author'}">
			<html:form action="/learning" method="post" onsubmit="disableFinishButton();" styleId="messageForm">
				<html:hidden property="dispatch" value="finishActivity" />
				<html:hidden property="sessionMapID" value="${sessionMapID}"/>
	
				<a href="#nogo" id="finishButton" onclick="submitForm('finish')" data-role="button" data-icon="arrow-r" data-theme="b">
					<span class="nextActivity"><fmt:message>button.finish</fmt:message></span>
				</a>
			</html:form>
		</c:if>
		
	</span>
</div>


	



