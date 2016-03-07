<%@ include file="/common/taglibs.jsp"%>

<div class="question-type">
	<fmt:message key="label.learning.short.answer.answer" />
</div>

<div class="table-responsive">
	<table class="table table-hover table-condensed">
		<tr>
			<c:if test="${finishedLock}">
				<td class="complete-item-gif">
				
					<c:if test="${assessment.allowRightAnswersAfterQuestion && question.answerBoolean}">
						<img src="<html:rewrite page='/includes/images/completeitem.gif'/>">	
					</c:if>			
					<c:if test="${assessment.allowWrongAnswersAfterQuestion && !question.answerBoolean}">
						<img src="<html:rewrite page='/includes/images/incompleteitem.gif'/>">
					</c:if>
					
				</td>
			</c:if>		
			<td>
				<input type="text" autocomplete="off" name="question${status.index}" value="<c:out value='${question.answerString}' />" class="form-control"
					<c:if test="${isEditingDisabled}">disabled="disabled"</c:if>
				/>	
			</td>
		</tr>
	</table>
</div>		

<c:if test="${finishedLock && assessment.allowQuestionFeedback && (question.questionFeedback != null)}">
	<div class="question-feedback">
		<c:out value="${question.questionFeedback}" escapeXml="false" />
	</div>
</c:if>	

<%@ include file="markandpenaltyarea.jsp"%>
