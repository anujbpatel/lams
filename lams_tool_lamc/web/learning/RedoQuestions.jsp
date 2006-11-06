<%-- 
Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
License Information: http://lamsfoundation.org/licensing/lams/2.0/

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License version 2 as 
  published by the Free Software Foundation.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
  USA

  http://www.gnu.org/licenses/gpl.txt
--%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
        "http://www.w3.org/TR/html4/strict.dtd">

<%@ include file="/common/taglibs.jsp"%>

<c:set var="lams">
	<lams:LAMSURL />
</c:set>
<c:set var="tool">
	<lams:WebAppURL />
</c:set>

<html:html>
<head>
	<html:base />
	<meta http-equiv="content-type" content="text/html; charset=UTF-8">
	<lams:css/>
	<title><fmt:message key="activity.title" /></title>
</head>

<body class="stripes">
	
		<html:form  action="/learning?method=displayMc&validate=false" method="POST" target="_self">
			<html:hidden property="toolContentID"/>						
			<html:hidden property="toolSessionID"/>								
			<html:hidden property="httpSessionID"/>											
			<html:hidden property="userID"/>											
			<html:hidden property="userOverPassMark"/>						
			<html:hidden property="passMarkApplicable"/>										
			<html:hidden property="learnerProgress"/>										
			<html:hidden property="learnerProgressUserId"/>										
			<html:hidden property="questionListingMode"/>													

			<div id="content">		
				<h1>
					<c:out value="${mcGeneralLearnerFlowDTO.activityTitle}" escapeXml="false" />		
				</h1>
	
			
				
			 		<c:if test="${mcGeneralLearnerFlowDTO.retries == 'true'}"> 		
						 
							  	 <h3><fmt:message key="label.individual.results.withRetries"/></h3>
						   
  					</c:if> 			

					<c:if test="${mcGeneralLearnerFlowDTO.retries == 'false'}"> 							  
					 
								  <h3><fmt:message key="label.individual.results.withoutRetries"/></h3>
						   
					</c:if> 			


					<p>
						  	 <fmt:message key="label.learner.redo"/> 
					  
					  </p>
					  
					  <p>
						  	 <fmt:message key="label.learner.bestMark"/>
						  	   <c:out value="${mcGeneralLearnerFlowDTO.learnerBestMark}"/> 
						  	<fmt:message key="label.outof"/> 
						  	<c:out value="${mcGeneralLearnerFlowDTO.totalQuestionCount}"/> 
					  	</p>
					  
					  
				  <div class="small-space-top">
				  			<html:submit property="viewAnswers" styleClass="button">
								<fmt:message key="label.view.answers"/>
							</html:submit>	 		

	   						<html:submit property="redoQuestionsOk" styleClass="button">
								<fmt:message key="label.redo.questions"/>
							</html:submit>	 				 		  					
				 
					</div>
				  	 
		 		<c:if test="${mcGeneralLearnerFlowDTO.retries == 'true'}"> 					  	   

					  	  	<div align="right" class="space-bottom-top">
									<c:if test="${((McLearningForm.passMarkApplicable == 'true') && (McLearningForm.userOverPassMark == 'true'))}">
										<c:if test="${mcGeneralLearnerFlowDTO.reflection != 'true'}"> 						  			  		
											<html:submit property="learnerFinished"  styleClass="button">
												<fmt:message key="label.finished"/>
											</html:submit>	 				
									  	</c:if> 				    					
						
										<c:if test="${mcGeneralLearnerFlowDTO.reflection == 'true'}"> 						  			  		
											<html:submit property="forwardtoReflection" styleClass="button">
												<fmt:message key="label.continue"/>
											</html:submit>	 				
									  	</c:if> 				    					
							  	   </c:if>	
							</div>						  	   
					  	
					</c:if>
					
					<c:if test="${mcGeneralLearnerFlowDTO.retries != 'true'}"> 							  
 			  	   		  
	    	  						<div align="right" class="space-bottom-top">
										<c:if test="${mcGeneralLearnerFlowDTO.reflection != 'true'}"> 						  			  		
										<html:submit property="learnerFinished"  styleClass="button">
											<fmt:message key="label.finished"/>
										</html:submit>	 				
								  	</c:if> 				    					
					
									<c:if test="${mcGeneralLearnerFlowDTO.reflection == 'true'}"> 						  			  		
										<html:submit property="forwardtoReflection" styleClass="button">
											<fmt:message key="label.continue"/>
										</html:submit>	 				
								  	</c:if> 				    					
								   
								</div>
					  	 
					</c:if> 																		
				  	 
				
			
		 	</div>

	
		</html:form>	
	

</body>
</html:html>








	