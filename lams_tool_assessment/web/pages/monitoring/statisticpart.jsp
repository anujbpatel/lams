<%@ include file="/common/taglibs.jsp"%>
<c:set var="sessionMap" value="${sessionScope[sessionMapID]}"/>
<c:set var="sessionDtos" value="${sessionMap.sessionDtos}"/>
<c:set var="leaderDto" value="${sessionMap.leaderDto}"/>
<c:set var="activityDto" value="${sessionMap.activityDto}"/>

<script type="text/javascript">
	$(document).ready(function(){
		
		$('#activity-evaluation').on('focus', function(){
			$(this).data('previousValue', this.value);
		}).on('change', function() {
			if (!this.value && !confirm("<fmt:message key='warn.tool.output.change.none'/>")) {
				$(this).val($(this).data('previousValue'));
				return;
			}
			$(this).data('previousValue', this.value);
			$.ajax({
				url: '<c:url value="/monitoring/setActivityEvaluation.do?sessionMapID=${sessionMapID}"/>',
				data: {
					activityEvaluation: this.value,
					"<csrf:tokenname/>":"<csrf:tokenvalue/>"
				},
				dataType: 'json',
				method: 'post',
				success: function (json) {
					if (json.success == "true") {
						alert("<fmt:message key='label.tool.output.has.been.changed'/>");
					}
				},
				error: function (xhr, ajaxOptions, thrownError) {
					alert("<fmt:message key='label.operation.failed'/>");
				}
			});
			
		});
		

	   	// must display charts after screen is visible or cannot calculate widths.
 		<c:choose>
		<c:when test="${sessionMap.assessment.useSelectLeaderToolOuput}">	
			drawHistogram('chartDivLeader${sessionMap.toolContentID}', 
					'<c:url value="/monitoring/getMarkChartData.do?sessionMapID=${sessionMapID}"/>', 
					'<fmt:message key="label.marks"/>', '<fmt:message key="label.number.groups.in.mark.range"/>');
		</c:when>
		<c:otherwise>
			drawHistogram('chartDivActivity${sessionMap.toolContentID}', 
				'<c:url value="/monitoring/getMarkChartData.do?sessionMapID=${sessionMapID}"/>', 
				'<fmt:message key="label.marks"/>', '<fmt:message key="label.number.learners.in.mark.range"/>');
			
			<c:forEach var="sessionDto" items="${sessionDtos}">
			drawHistogram('chartDivSession${sessionDto.sessionId}', 
					'<c:url value="/monitoring/getMarkChartData.do?sessionMapID=${sessionMapID}&toolSessionID=${sessionDto.sessionId}"/>', 
					'<fmt:message key="label.marks"/>', '<fmt:message key="label.number.learners.in.mark.range"/>');
			</c:forEach>	
		</c:otherwise>
		</c:choose>

 
	});		
</script>

<c:choose>

<c:when test="${empty sessionDtos}">
	<lams:Alert type="warn" id="no-edit" close="false">
		<fmt:message key="message.monitoring.summary.no.session" />
	</lams:Alert>
</c:when>
	
<c:otherwise>
	
	<p><fmt:message key="label.graph.help"/></p>
	
	<c:choose>
	<c:when test="${sessionMap.assessment.useSelectLeaderToolOuput}">	
		<div class="panel panel-default" >
       	<div class="panel-body">
 		<table class="table table-condensed table-striped table-no-border">
			<tr>
				<td class="field-name" width="25%">
					<fmt:message key="label.number.groups.finished" />:
				</td>
 				<td>
					<c:out value="${leaderDto.count}" />
				</td>
				<td class="field-name" width="25%">
					<fmt:message key="label.monitoring.question.summary.average.mark" />:
				</td>
 				<td>
					<c:out value="${leaderDto.averageString}" />
				</td>
 			</tr>
 			<tr>
				<td class="field-name" width="25%">
					<fmt:message key="label.lowest.mark"/>
				</td>
				<td>
					<c:out value="${leaderDto.minString}" />
				</td>
				<td class="field-name" width="25%">
					<fmt:message key="label.highest.mark"/>
				</td>
				<td>
					<c:out value="${leaderDto.maxString}" />
				</td>
			
			</tr>
			 <tr>
				<td class="field-name" width="25%">
					<fmt:message key="label.median.mark"/>
				</td>
				<td>
					<c:out value="${leaderDto.medianString}" />
				</td>
				<td class="field-name" width="25%">
					<fmt:message key="label.modes.mark"/>
				</td>
				<td>
					<c:out value="${leaderDto.modesString}" />
				</td>
			</tr>
 		</table>
 		<div class="row">
			 <div class="col-xs-12"></div>
				 <div id="chartDivLeader${sessionMap.toolContentID}" class="markChartDiv"></div>
		</div>
		</div>
		</div>
	</c:when>
		
	<c:otherwise>
		<c:if test="${sessionMap.isGroupedActivity}">
			<h4 class="voffset20"><fmt:message key="label.activity.stats"/></h4>
		</c:if>
		<div class="panel panel-default" >
       		<div class="panel-body">
			<table class="table table-condensed table-striped table-no-border">
				<tr>
					<td class="field-name" width="25%">
						<fmt:message key="label.number.learners.per.activity" />:
					</td>
					<td>
						<c:out value="${activityDto.count}" />
					</td>
					<td class="field-name" width="25%">
						<fmt:message key="label.monitoring.question.summary.average.mark" />:
					</td>
					
					<td>
						<c:out value="${activityDto.averageString}" />
					</td>
				</tr>
				<tr>
					<td class="field-name" width="25%">
						<fmt:message key="label.lowest.mark"/>
					</td>
					<td>
						<c:out value="${activityDto.minString}" />
					</td>
					<td class="field-name" width="25%">
						<fmt:message key="label.highest.mark"/>
					</td>
					
					<td>
						<c:out value="${activityDto.maxString}" />
					</td>
				</tr>
				<tr>
					<td class="field-name" width="25%">
						<fmt:message key="label.median.mark"/>
					</td>
					<td>
						<c:out value="${activityDto.medianString}" />
					</td>
					<td class="field-name" width="25%">
						<fmt:message key="label.modes.mark"/>
					</td>
					<td>
						<c:out value="${activityDto.modesString}" />
					</td>
				</tr>
			</table>
			<div class="row">
				 <div class="col-xs-12"></div>
 				 <div id="chartDivActivity${sessionMap.toolContentID}" class="markChartDiv"></div>
				</div>
			</div>
		</div>
		
		<c:if test="${sessionMap.isGroupedActivity}">
			<h4 class="voffset20"><fmt:message key="label.group.stats"/></h4>
			<c:forEach var="sessionDto" items="${sessionDtos}">
			    <div class="panel panel-default" >
			        <div class="panel-heading" id="heading${sessionDto.sessionId}">
						<span class="panel-title">
							<fmt:message key="monitoring.label.group" />: <c:out value="${sessionDto.sessionName}" />
						</span>
			        </div>
		       		<div class="panel-body">
					<table class="table table-condensed table-striped table-no-border">
						<tr>
							<td class="field-name" width="25%">
								<fmt:message key="label.number.learners.per.session" />:
							</td>
							<td>
								<c:out value="${sessionDto.count}" />
							</td>
							<td class="field-name" width="25%">
								<fmt:message key="label.monitoring.question.summary.average.mark" />:
							</td>
							
							<td>
								<c:out value="${sessionDto.averageString}" />
							</td>
						</tr>
						<tr>
							<td class="field-name" width="25%">
								<fmt:message key="label.lowest.mark"/>
							</td>
							<td>
								<c:out value="${sessionDto.minString}" />
							</td>
							<td class="field-name" width="25%">
								<fmt:message key="label.highest.mark"/>
							</td>
							
							<td>
								<c:out value="${sessionDto.maxString}" />
							</td>
						</tr>
						<tr>
							<td class="field-name" width="25%">
								<fmt:message key="label.median.mark"/>
							</td>
							<td>
								<c:out value="${sessionDto.medianString}" />
							</td>
							<td class="field-name" width="25%">
								<fmt:message key="label.modes.mark"/>
							</td>
							<td>
								<c:out value="${sessionDto.modesString}" />
							</td>
						</tr>
					</table>
					<div class="row">
						 <div class="col-xs-12"></div>
		 				 <div id="chartDivSession${sessionDto.sessionId}" class="markChartDiv"></div>
						</div>
					</div>
				</div>
			</c:forEach>
		</c:if>
	</c:otherwise>
	</c:choose>		
	
	<div id="accordion-qb-stats" class="panel-group voffset20" role="tablist" aria-multiselectable="true"> 
	    <div class="panel panel-default">
	        <div class="panel-heading collapsable-icon-left" id="heading-qb-stats">
	        	<span class="panel-title">
			    	<a class="collapsed" role="button" data-toggle="collapse" href="#qb-stats" aria-expanded="true" aria-controls="qb-stats">
		          		<fmt:message key="label.qb.stats" />
		          	</a>
					<i class="fa fa-question-circle" aria-hidden="true" data-toggle="tooltip" data-placement="right" title="" data-original-title="<fmt:message key="label.qb.stats.tooltip" />"></i>
	      		</span>
	        </div>
	
			<div aria-expanded="false" id="qb-stats" class="panel-body panel-collapse collapse" role="tabpanel" aria-labelledby="heading-qb-stats">
				<table class="table table-condensed table-striped table-no-border">
					<tr>		
						<th scope="col">
							#
						</th>
						<th scope="col">
							<fmt:message key="label.monitoring.question.summary.question"/>
						</th>
						<th scope="col">
							<fmt:message key="label.qb.participant.count"/>
						</th>
						<th scope="col">
							<fmt:message key="label.qb.difficulty.index"/>&nbsp;
							<i class="fa fa-question-circle" aria-hidden="true" data-toggle="tooltip" data-placement="right" title="" data-original-title="<fmt:message key="label.qb.difficulty.index.tooltip" />"></i>
						</th>
						<th scope="col">
							<fmt:message key="label.qb.discrimination.index"/>&nbsp;
							<i class="fa fa-question-circle" aria-hidden="true" data-toggle="tooltip" data-placement="right" title="" data-original-title="<fmt:message key="label.qb.discrimination.index.tooltip" />"></i>
						</th>
						<th scope="col">
							<fmt:message key="label.qb.point.biserial"/>&nbsp;
							 <i class="fa fa-question-circle" aria-hidden="true" data-toggle="tooltip" data-placement="right" title="" data-original-title="<fmt:message key="label.qb.point.biserial.tooltip" />"></i>
						</th>
					</tr>
						
					<c:forEach var="activityDTO" items="${qbStats}" varStatus="i">
						<tr>
							<td>
								${i.index + 1}
							</td>
							<td>
								<c:out value="${activityDTO.qbQuestion.name}" escapeXml="false"/>			
							</td>
							<td>
								<c:out value="${activityDTO.participantCount}" />
							</td>
							<c:choose>
								<c:when test="${activityDTO.participantCount < 2}">
									<td>-</td>
									<td>-</td>
									<td>-</td>
								</c:when>
								<c:otherwise>
									<td><fmt:formatNumber type="number" maxFractionDigits="2" value="${activityDTO.difficultyIndex}" /></td>
									<td><fmt:formatNumber type="number" maxFractionDigits="2" value="${activityDTO.discriminationIndex}" /></td>
									<td><fmt:formatNumber type="number" maxFractionDigits="2" value="${activityDTO.pointBiserial}" /></td>
								</c:otherwise>
							</c:choose>
						</tr>
					</c:forEach>	
				</table>
			</div>
		</div>
	</div>

</c:otherwise>
</c:choose>

<%@ include file="parts/toolOutput.jsp"%>
