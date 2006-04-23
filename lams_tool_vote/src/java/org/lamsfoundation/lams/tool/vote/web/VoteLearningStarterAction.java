/***************************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2.0
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ***********************************************************************/
package org.lamsfoundation.lams.tool.vote.web;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.lamsfoundation.lams.tool.exception.ToolException;
import org.lamsfoundation.lams.tool.vote.VoteAppConstants;
import org.lamsfoundation.lams.tool.vote.VoteApplicationException;
import org.lamsfoundation.lams.tool.vote.VoteComparator;
import org.lamsfoundation.lams.tool.vote.VoteUtils;
import org.lamsfoundation.lams.tool.vote.pojos.VoteContent;
import org.lamsfoundation.lams.tool.vote.pojos.VoteQueUsr;
import org.lamsfoundation.lams.tool.vote.pojos.VoteSession;
import org.lamsfoundation.lams.tool.vote.service.IVoteService;
import org.lamsfoundation.lams.tool.vote.service.VoteServiceProxy;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.web.session.SessionManager;
import org.lamsfoundation.lams.web.util.AttributeNames;


/**
 * 
 * @author Ozgur Demirtas
 *
 * <lams base path>/<tool's learner url>&userId=<learners user id>&toolSessionId=123&mode=teacher

 * Tool Session:
 *
 * A tool session is the concept by which which the tool and the LAMS core manage a set of learners interacting with the tool. 
 * The tool session id (toolSessionId) is generated by the LAMS core and given to the tool.
 * A tool session represents the use of a tool for a particulate activity for a group of learners. 
 * So if an activity is ungrouped, then one tool session exist for for a tool activity in a learning design.
 *
 * More details on the tool session id are covered under monitoring.
 * When thinking about the tool content id and the tool session id, it might be helpful to think about the tool content id 
 * relating to the definition of an activity, whereas the tool session id relates to the runtime participation in the activity.

 *  
 * Learner URL:
 * The learner url display the screen(s) that the learner uses to participate in the activity. 
 * When the learner accessed this user, it will have a tool access mode ToolAccessMode.LEARNER.
 *
 * It is the responsibility of the tool to record the progress of the user. 
 * If the tool is a multistage tool, for example asking a series of questions, the tool must keep track of what the learner has already done. 
 * If the user logs out and comes back to the tool later, then the tool should resume from where the learner stopped.
 * When the user is completed with tool, then the tool notifies the progress engine by calling 
 * org.lamsfoundation.lams.learning.service.completeToolSession(Long toolSessionId, User learner).
 *
 * If the tool's content DefineLater flag is set to true, then the learner should see a "Please wait for the teacher to define this part...." 
 * style message.
 * If the tool's content RunOffline flag is set to true, then the learner should see a "This activity is not being done on the computer. 
 * Please see your instructor for details."
 *
 * ?? Would it be better to define a run offline message in the tool? We have instructions for the teacher but not the learner. ??
 * If the tool has a LockOnFinish flag, then the tool should lock learner's entries once they have completed the activity. 
 * If they return to the activity (e.g. via the progress bar) then the entries should be read only.
 * 
 */

/**
 *
 * Note:  Because of Voting learning reporting structure, Show Learner Report is always ON even if in authoring it is set to false.
 */

public class VoteLearningStarterAction extends Action implements VoteAppConstants {
	static Logger logger = Logger.getLogger(VoteLearningStarterAction.class.getName());

    /*
     * By now, the passed tool session id MUST exist in the db through the calling of:
     * public void createToolSession(Long toolSessionId, Long toolContentId) by the container.
     *  
     * 
     * make sure this session exists in tool's session table by now.
     */
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) 
		throws IOException, ServletException, VoteApplicationException {

	
	VoteUtils.cleanUpSessionAbsolute(request);
	
	Map mapQuestionsContent= new TreeMap(new VoteComparator());
	Map mapAnswers= new TreeMap(new VoteComparator());

	IVoteService voteService = VoteUtils.getToolService(request);
	logger.debug("retrieving voteService from session: " + voteService);
	if (voteService == null)
	{
		voteService = VoteServiceProxy.getVoteService(getServlet().getServletContext());
	    logger.debug("retrieving voteService from proxy: " + voteService);
	    request.getSession().setAttribute(TOOL_SERVICE, voteService);		
	}

	VoteLearningForm voteLearningForm = (VoteLearningForm) form;
	
    /*
     * persist time zone information to session scope. 
     */
    VoteUtils.persistTimeZone(request);
    ActionForward validateParameters=validateParameters(request, mapping);
    logger.debug("validateParameters: " + validateParameters);
    if (validateParameters != null)
    {
    	return validateParameters;
    }

    Long toolSessionID=(Long) request.getSession().getAttribute(AttributeNames.PARAM_TOOL_SESSION_ID);
    logger.debug("retrieved toolSessionID: " + toolSessionID);
    
    /* API test code from here*/
    String createToolSession=request.getParameter("createToolSession");
	logger.debug("createToolSession: " + createToolSession);
	if ((createToolSession != null) && createToolSession.equals("1"))
	{	try
		{
			voteService.createToolSession(toolSessionID, "toolSessionName", new Long(9876));
			return (mapping.findForward(LEARNING_STARTER));
		}
		catch(ToolException e)
		{
			VoteUtils.cleanUpSessionAbsolute(request);
			logger.debug("tool exception"  + e);
		}
	}
	
	String removeToolSession=request.getParameter("removeToolSession");
	logger.debug("removeToolSession: " + removeToolSession);
	if ((removeToolSession != null) && removeToolSession.equals("1"))
	{	try
		{
			voteService.removeToolSession(toolSessionID);
			return (mapping.findForward(LEARNING_STARTER));
		}
		catch(ToolException e)
		{
			VoteUtils.cleanUpSessionAbsolute(request);
			logger.debug("tool exception"  + e);
		}
	}
	
	String learnerId=request.getParameter("learnerId");
	logger.debug("learnerId: " + learnerId);
	if (learnerId != null) 
	{	try
		{
			String nextUrl=voteService.leaveToolSession(toolSessionID, new Long(learnerId));
			logger.debug("nextUrl: "+ nextUrl);
			return (mapping.findForward(LEARNING_STARTER));
		}
		catch(ToolException e)
		{
			VoteUtils.cleanUpSessionAbsolute(request);
			logger.debug("tool exception"  + e);
		}
	}
	/*till here*/
    
	
	/*
	 * by now, we made sure that the passed tool session id exists in the db as a new record
	 * Make sure we can retrieve it and the relavent content
	 */
	
	VoteSession voteSession=voteService.retrieveVoteSession(toolSessionID);
    logger.debug("retrieving voteSession: " + voteSession);
    
    if (voteSession == null)
    {
    	VoteUtils.cleanUpSessionAbsolute(request);
    	logger.debug("error: The tool expects voteSession.");
    	request.getSession().setAttribute(USER_EXCEPTION_NO_TOOL_SESSIONS, new Boolean(true).toString());
    	persistError(request,"error.toolSession.notAvailable");
		return (mapping.findForward(ERROR_LIST));
    }

    /*
     * find out what content this tool session is referring to
     * get the content for this tool session 
     * Each passed tool session id points to a particular content. Many to one mapping.
     */
	VoteContent voteContent=voteSession.getVoteContent();
    logger.debug("using voteContent: " + voteContent);
    
    if (voteContent == null)
    {
    	VoteUtils.cleanUpSessionAbsolute(request);
    	logger.debug("error: The tool expects voteContent.");
    	persistError(request,"error.content.doesNotExist");
    	request.getSession().setAttribute(USER_EXCEPTION_CONTENT_DOESNOTEXIST, new Boolean(true).toString());
    	return (mapping.findForward(ERROR_LIST));
    }

    
    /*
     * The content we retrieved above must have been created before in Authoring time. 
     * And the passed tool session id already refers to it.
     */
    setupAttributes(request, voteContent, voteLearningForm);

    request.getSession().setAttribute(TOOL_CONTENT_ID, voteContent.getVoteContentId());
    logger.debug("using TOOL_CONTENT_ID: " + voteContent.getVoteContentId());
    
    request.getSession().setAttribute(TOOL_CONTENT_UID, voteContent.getUid());
    logger.debug("using TOOL_CONTENT_UID: " + voteContent.getUid());
    
	/* Is the request for a preview by the author?
	Preview The tool must be able to show the specified content as if it was running in a lesson. 
	It will be the learner url with tool access mode set to ToolAccessMode.AUTHOR 
	3 modes are:
		author
		teacher
		learner
	*/
	/* ? CHECK THIS: how do we determine whether preview is requested? Mode is not enough on its own.*/
    
    /*handle PREVIEW mode*/
    String mode=(String) request.getSession().getAttribute(LEARNING_MODE);
    logger.debug("mode: " + mode);
	if ((mode != null) && (mode.equals("author")))
	{
		logger.debug("Author requests for a preview of the content.");
		logger.debug("existing voteContent:" + voteContent);
		
		commonContentSetup(request, voteContent);
		
		/* PREVIEW_ONLY for jsp*/
    	request.getSession().setAttribute(PREVIEW_ONLY, new Boolean(true).toString());
    	
    	request.getSession().setAttribute(CURRENT_QUESTION_INDEX, "1");
		VoteLearningAction voteLearningAction= new VoteLearningAction();
    	//return voteLearningAction.redoQuestions(request, voteLearningForm, mapping);
		return null;
	}
    
	/* by now, we know that the mode is either teacher or learner
	 * check if the mode is teacher and request is for Learner Progress
	 */
	String userId=request.getParameter(USER_ID);
	logger.debug("userId: " + userId);
	if ((userId != null) && (mode.equals("teacher")))
	{
		logger.debug("request is for learner progress");
		commonContentSetup(request, voteContent);
    	
		/* LEARNER_PROGRESS for jsp*/
		request.getSession().setAttribute(LEARNER_PROGRESS_USERID, userId);
		request.getSession().setAttribute(LEARNER_PROGRESS, new Boolean(true).toString());
		VoteLearningAction voteLearningAction= new VoteLearningAction();
		/* pay attention that this userId is the learner's userId passed by the request parameter.
		 * It is differerent than USER_ID kept in the session of the current system user*/
		VoteQueUsr voteQueUsr=voteService.retrieveVoteQueUsr(new Long(userId));
	    logger.debug("voteQueUsr:" + voteQueUsr);
	    if (voteQueUsr == null)
	    {
	    	VoteUtils.cleanUpSessionAbsolute(request);
	    	persistError(request, "error.learner.required");
	    	request.getSession().setAttribute(USER_EXCEPTION_LEARNER_REQUIRED, new Boolean(true).toString());
			return (mapping.findForward(ERROR_LIST));
	    }
	    
	    /* check whether the user's session really referrs to the session id passed to the url*/
	    Long sessionUid=voteQueUsr.getVoteSessionId();
	    logger.debug("sessionUid" + sessionUid);
	    VoteSession voteSessionLocal=voteService.getVoteSessionByUID(sessionUid);
	    logger.debug("checking voteSessionLocal" + voteSessionLocal);
	    Long toolSessionId=(Long)request.getSession().getAttribute(TOOL_SESSION_ID);
	    logger.debug("toolSessionId: " + toolSessionId + " versus" + voteSessionLocal);
	    if  ((voteSessionLocal ==  null) ||
			 (voteSessionLocal.getVoteSessionId().longValue() != toolSessionId.longValue()))
	    {
	    	VoteUtils.cleanUpSessionAbsolute(request);
	    	request.getSession().setAttribute(USER_EXCEPTION_TOOLSESSIONID_INCONSISTENT, new Boolean(true).toString());
	    	persistError(request, "error.learner.sessionId.inconsistent");
			return (mapping.findForward(ERROR_LIST));
	    }
		//return voteLearningAction.viewAnswers(mapping, form, request, response);
	    return null;
	}
	
	/* by now, we know that the mode is learner*/
    
    /* find out if the content is set to run offline or online. If it is set to run offline , the learners are informed about that. */
    boolean isRunOffline=VoteUtils.isRunOffline(voteContent);
    logger.debug("isRunOffline: " + isRunOffline);
    if (isRunOffline == true)
    {
    	VoteUtils.cleanUpSessionAbsolute(request);
    	logger.debug("warning to learner: the activity is offline.");
    	request.getSession().setAttribute(USER_EXCEPTION_CONTENT_RUNOFFLINE, new Boolean(true).toString());
    	persistError(request,"label.learning.runOffline");
		return (mapping.findForward(ERROR_LIST));
    }

    /* find out if the content is being modified at the moment. */
    boolean isDefineLater=VoteUtils.isDefineLater(voteContent);
    logger.debug("isDefineLater: " + isDefineLater);
    if (isDefineLater == true)
    {
    	VoteUtils.cleanUpSessionAbsolute(request);
    	request.getSession().setAttribute(USER_EXCEPTION_CONTENT_DEFINE_LATER, new Boolean(true).toString());
    	logger.debug("warning to learner: the activity is defineLater, we interpret that the content is being modified.");
    	persistError(request,"error.defineLater");
    	return (mapping.findForward(ERROR_LIST));
    }

    /*
	 * fetch question content from content
	 */
    mapQuestionsContent=LearningUtil.buildQuestionContentMap(request,voteContent);
    logger.debug("mapQuestionsContent: " + mapQuestionsContent);
	
	request.getSession().setAttribute(MAP_QUESTION_CONTENT_LEARNER, mapQuestionsContent);
	logger.debug("MAP_QUESTION_CONTENT_LEARNER: " +  request.getSession().getAttribute(MAP_QUESTION_CONTENT_LEARNER));
	logger.debug("voteContent has : " + mapQuestionsContent.size() + " entries.");
	
	request.getSession().setAttribute(CURRENT_QUESTION_INDEX, "1");
	logger.debug("CURRENT_QUESTION_INDEX: " + request.getSession().getAttribute(CURRENT_QUESTION_INDEX));
	
	/*
     * verify that userId does not already exist in the db.
     * If it does exist, that means, that user already responded to the content and 
     * his answers must be displayed  read-only
     * 
     */
	String userID=(String) request.getSession().getAttribute(USER_ID);
	logger.debug("userID:" + userID);
    
	VoteQueUsr voteQueUsr=voteService.retrieveVoteQueUsr(new Long(userID));
    logger.debug("voteQueUsr:" + voteQueUsr);
    
    if (voteQueUsr != null)
    {
    	logger.debug("voteQueUsr is available in the db:" + voteQueUsr);
    	Long queUsrId=voteQueUsr.getUid();
		logger.debug("queUsrId: " + queUsrId);
    }
    else
    {
    	logger.debug("voteQueUsr is not available in the db:" + voteQueUsr);
    }
    
    String learningMode=(String) request.getSession().getAttribute(LEARNING_MODE);
    logger.debug("users learning mode is: " + learningMode);
    /*if the user's session id AND user id exists in the tool tables go to redo questions.*/
    if ((voteQueUsr != null) && learningMode.equals("learner"))
    {
    	Long sessionUid=voteQueUsr.getVoteSessionId();
    	logger.debug("users sessionUid: " + sessionUid);
    	VoteSession voteUserSession= voteService.getVoteSessionByUID(sessionUid);
    	logger.debug("voteUserSession: " + voteUserSession);
    	String userSessionId=voteUserSession.getVoteSessionId().toString();
    	logger.debug("userSessionId: " + userSessionId);
    	Long toolSessionId=(Long)request.getSession().getAttribute(TOOL_SESSION_ID);
    	logger.debug("current toolSessionId: " + toolSessionId);
    	if (toolSessionId.toString().equals(userSessionId))
    	{
    		logger.debug("the user's session id AND user id exists in the tool tables go to redo questions. " + toolSessionId + " voteQueUsr: " + 
    				voteQueUsr + " user id: " + voteQueUsr.getQueUsrId());
    		logger.debug("the learner has already responsed to this content, just generate a read-only report. Use redo questions for this.");
	    	return (mapping.findForward(REDO_QUESTIONS));
    	}
    }
    else if (learningMode.equals("teacher"))
    {
    	VoteLearningAction voteLearningAction= new VoteLearningAction();
    	logger.debug("present to teacher learners progress...");
    	//return voteLearningAction.viewAnswers(mapping, form, request, response);
    	return null;
    }
    return (mapping.findForward(LOAD_LEARNER));	
}


/**
 * sets up question and candidate answers maps
 * commonContentSetup(HttpServletRequest request, VoteContent voteContent)
 * 
 * @param request
 * @param voteContent
 */
protected void commonContentSetup(HttpServletRequest request, VoteContent voteContent)
{
	Map mapQuestionsContent= new TreeMap(new VoteComparator());
	mapQuestionsContent=LearningUtil.buildQuestionContentMap(request,voteContent);
    logger.debug("mapQuestionsContent: " + mapQuestionsContent);
	
	request.getSession().setAttribute(MAP_QUESTION_CONTENT_LEARNER, mapQuestionsContent);
	logger.debug("MAP_QUESTION_CONTENT_LEARNER: " +  request.getSession().getAttribute(MAP_QUESTION_CONTENT_LEARNER));
	logger.debug("voteContent has : " + mapQuestionsContent.size() + " entries.");
	request.getSession().setAttribute(TOTAL_QUESTION_COUNT, new Long(mapQuestionsContent.size()).toString());
	
	request.getSession().setAttribute(CURRENT_QUESTION_INDEX, "1");
	logger.debug("CURRENT_QUESTION_INDEX: " + request.getSession().getAttribute(CURRENT_QUESTION_INDEX));
	
}


	/**
	 * sets up session scope attributes based on content linked to the passed tool session id
	 * setupAttributes(HttpServletRequest request, VoteContent voteContent)
	 * 
	 * @param request
	 * @param voteContent
	 */
	protected void setupAttributes(HttpServletRequest request, VoteContent voteContent, VoteLearningForm voteLearningForm)
	{
	    
	    logger.debug("IS_RETRIES: " + new Boolean(voteContent.isRetries()).toString());
	    //request.getSession().setAttribute(IS_RETRIES, new Boolean(voteContent.isRetries()).toString());
	    
	    logger.debug("IS_CONTENT_IN_USE: " + voteContent.isContentInUse());
	    
	    //request.getSession().setAttribute(ACTIVITY_TITLE, voteContent.getTitle());
	    //request.getSession().setAttribute(ACTIVITY_INSTRUCTIONS, voteContent.getInstructions());

	    Map mapGeneralCheckedOptionsContent= new TreeMap(new VoteComparator());
	    request.getSession().setAttribute(MAP_GENERAL_CHECKED_OPTIONS_CONTENT, mapGeneralCheckedOptionsContent);
	    /*
	     * Is the tool activity been checked as Run Offline in the property inspector?
	     */
	    logger.debug("IS_TOOL_ACTIVITY_OFFLINE: " + voteContent.isRunOffline());
	    //request.getSession().setAttribute(IS_TOOL_ACTIVITY_OFFLINE, new Boolean(voteContent.isRunOffline()).toString());
	    
	    
	    logger.debug("advanced properties isRetries: " + new Boolean(voteContent.isRetries()).toString());
	    logger.debug("advanced properties maxNominationCount: " + voteContent.getMaxNominationCount());
	    logger.debug("advanced properties isAllowText(): " + new Boolean(voteContent.isAllowText()).toString());
	    logger.debug("advanced properties isVoteChangable(): " + new Boolean(voteContent.isVoteChangable()).toString());
	    
	    logger.debug("advanced properties isRunOffline(): " + new Boolean(voteContent.isRunOffline()).toString());
	    logger.debug("advanced properties isRetries(): " + new Boolean(voteContent.isRetries()).toString());
	    logger.debug("advanced properties isLockOnFinish(): " + new Boolean(voteContent.isLockOnFinish()).toString());
	    
	    
	    voteLearningForm.setActivityTitle(voteContent.getTitle());
	    voteLearningForm.setActivityInstructions(voteContent.getInstructions());
	    voteLearningForm.setActivityRetries(new Boolean(voteContent.isRetries()).toString());
	    voteLearningForm.setActivityRunOffline(new Boolean(voteContent.isRunOffline()).toString());
	    
	    voteLearningForm.setMaxNominationCount(voteContent.getMaxNominationCount());
	    voteLearningForm.setAllowTextEntry(new Boolean(voteContent.isAllowText()).toString());
	    voteLearningForm.setLockOnFinish(new Boolean(voteContent.isLockOnFinish()).toString());
	    voteLearningForm.setVoteChangable(new Boolean(voteContent.isVoteChangable()).toString());
	}
	
	
	protected ActionForward validateParameters(HttpServletRequest request, ActionMapping mapping)
	{
		/*
	     * obtain and setup the current user's data 
	     */
		
	    String userID = "";
	    HttpSession ss = SessionManager.getSession();
	    logger.debug("ss: " + ss);
	    
	    if (ss != null)
	    {
		    UserDTO user = (UserDTO) ss.getAttribute(AttributeNames.USER);
		    if ((user != null) && (user.getUserID() != null))
		    {
		    	userID = user.getUserID().toString();
			    logger.debug("retrieved userId: " + userID);
		    	request.getSession().setAttribute(USER_ID, userID);
		    }
	    }
		
	    
	    /*
	     * process incoming tool session id and later derive toolContentId from it. 
	     */
    	String strToolSessionId=request.getParameter(AttributeNames.PARAM_TOOL_SESSION_ID);
	    long toolSessionId=0;
	    if ((strToolSessionId == null) || (strToolSessionId.length() == 0)) 
	    {
	    	VoteUtils.cleanUpSessionAbsolute(request);
	    	request.getSession().setAttribute(USER_EXCEPTION_TOOLSESSIONID_REQUIRED, new Boolean(true).toString());
	    	persistError(request, "error.toolSessionId.required");
	    	return (mapping.findForward(ERROR_LIST));
	    }
	    else
	    {
	    	try
			{
	    		toolSessionId=new Long(strToolSessionId).longValue();
		    	logger.debug("passed TOOL_SESSION_ID : " + new Long(toolSessionId));
		    	request.getSession().setAttribute(TOOL_SESSION_ID,new Long(toolSessionId));	
			}
	    	catch(NumberFormatException e)
			{
	    		VoteUtils.cleanUpSessionAbsolute(request);
	    		request.getSession().setAttribute(USER_EXCEPTION_NUMBERFORMAT, new Boolean(true).toString());
	    		persistError(request, "error.sessionId.numberFormatException");
	    		logger.debug("add error.sessionId.numberFormatException to ActionMessages.");
	    		return (mapping.findForward(ERROR_LIST));
			}
	    }
	    
	    /*mode can be learner, teacher or author */
	    String mode=request.getParameter(MODE);
	    logger.debug("mode: " + mode);
	    
	    if ((mode == null) || (mode.length() == 0)) 
	    {
	    	VoteUtils.cleanUpSessionAbsolute(request);
	    	request.getSession().setAttribute(USER_EXCEPTION_MODE_REQUIRED, new Boolean(true).toString());
	    	persistError(request, "error.mode.required");
	    	return (mapping.findForward(ERROR_LIST));
	    }
	    
	    if ((!mode.equals("learner")) && (!mode.equals("teacher")) && (!mode.equals("author")))
	    {
	    	VoteUtils.cleanUpSessionAbsolute(request);
	    	request.getSession().setAttribute(USER_EXCEPTION_MODE_INVALID, new Boolean(true).toString());
	    	persistError(request, "error.mode.invalid");
			return (mapping.findForward(ERROR_LIST));
	    }
		logger.debug("session LEARNING_MODE set to:" + mode);
	    request.getSession().setAttribute(LEARNING_MODE, mode);
	    
	    return null;
	}

	
	/**
     * persists error messages to request scope
     * @param request
     * @param message
     */
	public void persistError(HttpServletRequest request, String message)
	{
		ActionMessages errors= new ActionMessages();
		errors.add(Globals.ERROR_KEY, new ActionMessage(message));
		logger.debug("add " + message +"  to ActionMessages:");
		saveErrors(request,errors);	    	    
	}
}  
