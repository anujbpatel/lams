<%--
Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
USA

http://www.gnu.org/licenses/gpl.txt
--%>

<%@ page language="java"%>
<%@ taglib uri="tags-html" prefix="html"%>
<%@ taglib uri="tags-core" prefix="c"%>
<%@ taglib uri="tags-fmt" prefix="fmt" %>
<%@ taglib uri="tags-lams" prefix="lams" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Frameset//EN" "http://www.w3.org/TR/html4/frameset.dtd">
<html:html locale="true" xhtml="true">

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
		<link href="<lams:LAMSURL/>/css/default.css" rel="stylesheet" type="text/css"/>
		<title>Learner :: LAMS</title>
		<SCRIPT LANGUAGE="VBScript">
			<!-- 
			//  Map VB script events to the JavaScript method - Netscape will ignore this... 
			//  Since FSCommand fires a VB event under ActiveX, we respond here 
			Sub leftUI_FSCommand(ByVal command, ByVal args)
			  call leftUI_DoFSCommand(command, args)
			end sub
			-->
		</SCRIPT>
		<script language="JavaScript" type="text/JavaScript">
			<!--
			
			function setFlashVars(){
				var serverURL = parent.window.serverURL;
				window.document.leftUI.SetVariable("serverURL", serverURL);
				
				//alert('serverURL='+serverURL);
			}
			
			var thePopUp = null;
			function leftUI_DoFSCommand(command, args) {
				//alert("command:"+command+","+args);
			
				if (command == "alert") {
					 doAlert(args);
				}else if (command == "confirm"){
					doConfirm(arg);
				}else if (command == "openPopUp"){
					openPopUp(args);		
				}
			
			}
			
			function doAlert(arg){
				alert(arg);
			}
			
			function doConfirm(arg){
				var answer = confirm (arg)
				if (answer)
					alert ("Oh yeah?")
				else
					alert ("Why not?")
			}
			 
			
			function openPopUp(args){
				if(thePopUp && thePopUp.open && !thePopUp.closed){		
						thePopUp.focus();	
				}else{
					//alert('opening:'+args);
					thePopUp = window.open(args,"learnerPop","HEIGHT=400,WIDTH=550,resizable,scrollbars");
				}
			}
			
			//-->
		</script>

	</head>
	<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onLoad="setFlashVars()">

		<!-- URL's used in the movie-->
		<!-- text used in the movie-->
		<!--Flash UI component-->
		<!-- OBJECT classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
				codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,0,0" name="leftUI"
				WIDTH="100%" HEIGHT="100%" ALIGN="" id="leftUI"-->
			<!-- PARAM NAME=movie VALUE="LearnerLeftUI.swf" -->
			<!-- PARAM NAME=quality VALUE="high" -->
			<!-- PARAM NAME=scale VALUE="noscale" -->
			<!-- PARAM NAME=salign VALUE="LT" -->
			<!-- PARAM NAME=flashvars VALUE="pollInterval=10000" -->
			<!--<PARAM NAME=bgcolor VALUE=#D0D0E8 -->
			<!-- EMBED src="LearnerLeftUI.swf?pollInterval=10000"  WIDTH="100%" HEIGHT="100%" ALIGN="" quality=high scale=noscale salign=LT
					TYPE="application/x-shockwave-flash" PLUGINSPAGE="http://www.macromedia.com/go/getflashplayer" swliveconnect=true name="leftUI"-->
			<!-- /EMBED-->
		<!-- /OBJECT-->
		

		<h2>Available Lessons</H2>
		<p>[Flash component goes here]</p>
		<table width="100%" border="0" cellspacing="2" cellpadding="2" summary="This table is being used for layout purposes only">
			<TR><TD><A HREF="<lams:WebAppURL/>dummylearner.do?method=getActiveLessons">Refresh List</A></TD></TR>
		<c:forEach var="lesson" items="${lessons}">
		<c:if test="${lesson.lessonStateId<6}">
			<TR><TD>
				<A HREF="<lams:WebAppURL/>learner.do?method=joinLesson&userId=<lams:user property="userID"/>&lessonId=<c:out value="${lesson.lessonId}"/>" target="contentFrame">
				<STRONG><c:out value="${lesson.lessonName}"/></STRONG></A>
			<c:choose>
				<c:when test="${lesson.lessonStateId==1}">[Created]</c:when>
				<c:when test="${lesson.lessonStateId==2}">[Scheduled]</c:when>
				<c:when test="${lesson.lessonStateId==3}">[Started]</c:when>
				<c:when test="${lesson.lessonStateId==4}">[Suspended]</c:when>
				<c:when test="${lesson.lessonStateId==5}">[Finished]</c:when>
			</c:choose>
			<BR><c:out value="${lesson.lessonDescription}"/>
			</TD></TR>
			<TR><TD><HR></TD></TR>
		</c:if>
		</c:forEach>
		</table>

	</body>
</html:html>
