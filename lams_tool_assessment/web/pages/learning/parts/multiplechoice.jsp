<%@ include file="/common/taglibs.jsp"%>

<div class="question-type">
	<c:choose>
		<c:when test="${question.multipleAnswersAllowed}">
			<fmt:message key="label.learning.choose.at.least.one.answer" />
		</c:when>
		<c:otherwise>
			<fmt:message key="label.learning.choose.one.answer" />
		</c:otherwise>
	</c:choose>
</div>

<table class="question-table">
	<c:forEach var="option" items="${question.options}">
		<tr>
			<c:if test="${finishedLock}">
				<td class="complete-item-gif">
				
					<c:if test="${assessment.allowRightAnswersAfterQuestion && option.answerBoolean && (option.grade > 0)}">
						<img src="<html:rewrite page='/includes/images/completeitem.gif'/>">
					</c:if>
					<c:if test="${assessment.allowWrongAnswersAfterQuestion && option.answerBoolean && (option.grade <= 0)}">
						<img src="<html:rewrite page='/includes/images/incompleteitem.gif'/>">
					</c:if>
						
				</td>
			</c:if>
			
			<td class="has-radio-button">
				<c:choose>
					<c:when test="${question.multipleAnswersAllowed}">
						<input type="checkbox" name="question${status.index}_${option.sequenceId}" value="${true}" styleClass="noBorder"
	 						<c:if test="${option.answerBoolean}">checked="checked"</c:if>
							<c:if test="${isEditingDisabled}">disabled="disabled"</c:if>
						/>
					</c:when>
					<c:otherwise>
						<input type="radio" name="question${status.index}" value="${option.sequenceId}" styleClass="noBorder"
	 						<c:if test="${option.answerBoolean}">checked="checked"</c:if>
	 						<c:if test="${isEditingDisabled}">disabled="disabled"</c:if>
						/>
					</c:otherwise>
				</c:choose>
			</td>
			
			<td class="question-option">
				<c:out value="${option.optionString}" escapeXml="false" />
			</td>
			
			<c:if test="${finishedLock && option.answerBoolean && assessment.allowQuestionFeedback}">

				<c:choose>
                	<c:when test="${option.grade <= 0}">
                    	<c:set var="color" scope="page" value="red" />
        			</c:when>
					<c:otherwise>
                    	<c:set var="color" scope="page" value="blue" />
        			</c:otherwise>
        		</c:choose>

				<td style="padding:5px 10px 2px; font-style: italic; color:${color}; width=30%;">
					<c:out value="${option.feedback}" escapeXml="false" />
				</td>		
			</c:if>
			
		</tr>
	</c:forEach>
</table>	

<c:if test="${finishedLock && assessment.allowQuestionFeedback}">
	<div class="question-feedback">
		<c:choose>
			<c:when test="${question.mark == question.defaultGrade}">
				<c:out value="${question.feedbackOnCorrect}" escapeXml="false" />
			</c:when>
			<c:when test="${question.mark > 0}">
				<c:out value="${question.feedbackOnPartiallyCorrect}" escapeXml="false" />
			</c:when>
			<c:when test="${question.mark <= 0}">
				<c:out value="${question.feedbackOnIncorrect}" escapeXml="false" />
			</c:when>		
		</c:choose>
	</div>
</c:if>

<%@ include file="markandpenaltyarea.jsp"%>
