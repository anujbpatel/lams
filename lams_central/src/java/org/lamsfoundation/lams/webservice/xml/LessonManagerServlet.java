package org.lamsfoundation.lams.webservice.xml;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.lamsfoundation.lams.integration.ExtCourseClassMap;
import org.lamsfoundation.lams.integration.ExtServerOrgMap;
import org.lamsfoundation.lams.integration.ExtUserUseridMap;
import org.lamsfoundation.lams.integration.UserInfoFetchException;
import org.lamsfoundation.lams.integration.security.AuthenticationException;
import org.lamsfoundation.lams.integration.security.Authenticator;
import org.lamsfoundation.lams.integration.service.IntegrationService;
import org.lamsfoundation.lams.integration.util.LoginRequestDispatcher;
import org.lamsfoundation.lams.learningdesign.service.IExportToolContentService;
import org.lamsfoundation.lams.lesson.LearnerProgress;
import org.lamsfoundation.lams.lesson.Lesson;
import org.lamsfoundation.lams.lesson.dto.LearnerProgressDTO;
import org.lamsfoundation.lams.lesson.service.ILessonService;
import org.lamsfoundation.lams.monitoring.service.IMonitoringService;
import org.lamsfoundation.lams.usermanagement.Organisation;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.util.CentralConstants;
import org.lamsfoundation.lams.util.DateUtil;
import org.lamsfoundation.lams.web.session.SessionManager;
import org.lamsfoundation.lams.web.util.AttributeNames;
import org.lamsfoundation.lams.workspace.service.IWorkspaceManagementService;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class LessonManagerServlet extends HttpServlet {

	private static Logger log = Logger.getLogger(LessonManagerServlet.class);

	private static IntegrationService integrationService = null;

	private static IWorkspaceManagementService service = null;

	private static IMonitoringService monitoringService = null;

	private static ILessonService lessonService = null;
	
	private static IExportToolContentService exportService = null;
	
	/**
	 * Constructor of the object.
	 */
	public LessonManagerServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		PrintWriter out = response.getWriter();
		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF8");
		
		String serverId 	= request.getParameter(CentralConstants.PARAM_SERVER_ID);
		String datetime 	= request.getParameter(CentralConstants.PARAM_DATE_TIME);
		String hashValue 	= request.getParameter(CentralConstants.PARAM_HASH_VALUE);
		String username  	= request.getParameter(CentralConstants.PARAM_USERNAME);
		String courseId 	= request.getParameter(CentralConstants.PARAM_COURSE_ID);
		String ldIdStr 		= request.getParameter(CentralConstants.PARAM_LEARNING_DESIGN_ID);
		String lsIdStr 		= request.getParameter(CentralConstants.PARAM_LESSON_ID);
		String country 		= request.getParameter(CentralConstants.PARAM_COUNTRY);
		String title 		= request.getParameter(CentralConstants.PARAM_TITLE);
		String desc 		= request.getParameter(CentralConstants.PARAM_DESC);
		String startDate 	= request.getParameter(CentralConstants.PARAM_STARTDATE);
		String lang 		= request.getParameter(CentralConstants.PARAM_LANG);
		String method 		= request.getParameter(CentralConstants.PARAM_METHOD);
		String filePath		= request.getParameter(CentralConstants.PARAM_FILEPATH);
		String progressUser = request.getParameter(CentralConstants.PARAM_PROGRESS_USER);
		String learnerIds	= request.getParameter(CentralConstants.PARAM_LEARNER_IDS);
		String monitorIds	= request.getParameter(CentralConstants.PARAM_MONITOR_IDS);


		Long ldId = null;
		Long lsId = null;
		try {
			// TODO check input parameters are valid.

			// Create xml document
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();

			Element element = null;
			
			if (hashValue==null || hashValue.equals(""))
				throw new NullPointerException("Hash value missing in parameters");

			if (method.equals(CentralConstants.METHOD_START)) {
				ldId = new Long(ldIdStr);
				Long lessonId = startLesson(serverId, datetime, hashValue,
						username, ldId, courseId, title, desc, country, lang);

				element = document.createElement(CentralConstants.ELEM_LESSON);
				element.setAttribute(CentralConstants.ATTR_LESSON_ID, lessonId.toString());

			} else if (method.equals(CentralConstants.METHOD_PREVIEW)) {
				ldId = new Long(ldIdStr);
				Long lessonId = startPreview(serverId, datetime, hashValue,
						username, ldId, courseId, title, desc, country, lang);
				
				element = document.createElement(CentralConstants.ELEM_LESSON);
				element.setAttribute(CentralConstants.ATTR_LESSON_ID, lessonId.toString());
				
			} else if (method.equals(CentralConstants.METHOD_SCHEDULE)) {
				ldId = new Long(ldIdStr);
				Long lessonId = scheduleLesson(serverId, datetime, hashValue,
						username, ldId, courseId, title, desc, startDate,
						country, lang);

				element = document.createElement(CentralConstants.ELEM_LESSON);
				element.setAttribute(CentralConstants.ATTR_LESSON_ID, lessonId.toString());

			} else if (method.equals(CentralConstants.METHOD_DELETE)) {
				lsId = new Long(lsIdStr);
				Boolean deleted = deleteLesson(serverId, datetime, hashValue,
						username, lsId);

				element = document.createElement(CentralConstants.ELEM_LESSON);
				element.setAttribute(CentralConstants.ATTR_LESSON_ID, lsId.toString());
				element.setAttribute(CentralConstants.ATTR_DELETED, deleted.toString());

			}  else if (method.equals(CentralConstants.METHOD_STUDENT_PROGRESS)) {
				lsId = new Long(lsIdStr);				
				element = getAllStudentProgress(document, serverId, datetime, hashValue, 
					username, lsId, courseId);

			}  else if (method.equals(CentralConstants.METHOD_SINGLE_STUDENT_PROGRESS)) {
				lsId = new Long(lsIdStr);				
				element = getSingleStudentProgress(document, serverId, datetime, hashValue, 
						username, lsId, courseId, progressUser);

			}  else if (method.equals(CentralConstants.METHOD_IMPORT)) {

				// ldId = new Long(ldIdStr);
				Long ldID = importLearningDesign(request, response, filePath, username, serverId);
				
				element = document.createElement(CentralConstants.ELEM_LEARNINGDESIGN);
				element.setAttribute(CentralConstants.PARAM_LEARNING_DESIGN_ID, ldID.toString());

			} else if (method.equals(CentralConstants.METHOD_JOIN_LESSON)) {
				Thread t = new Thread(new AddUsersToLessonThread(serverId, datetime, username, hashValue, 
						lsIdStr, courseId, country, lang, learnerIds, monitorIds, request));
				t.start();

				element = document.createElement(CentralConstants.ELEM_LESSON);
				element.setAttribute(CentralConstants.ATTR_LESSON_ID, lsIdStr);
				
			}  else {
				String msg = "Method :" + method + " is not recognised"; 
				log.error(msg);
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
			}

			document.appendChild(element);

			// write out the xml document.
			DOMSource domSource = new DOMSource(document);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
			
			out.write(writer.toString());			
			
		} catch (NumberFormatException nfe) {
			log.error("lsId or ldId is not an integer" + lsIdStr + ldIdStr, nfe);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "lsId or ldId is not an integer");
		} catch (TransformerConfigurationException e) {
			log.error("Can not convert XML document to string", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} catch (TransformerException e) {
			log.error("Can not convert XML document to string", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
		} catch (ParserConfigurationException e) {
			log.error("Can not build XML document", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} catch (NullPointerException e) {
			log.error("Missing parameters", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		catch (Exception e) {
			log.error("Problem loading learning manager servlet request", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		
		out.flush();
		out.close();
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		// TODO services should be implemented as POST
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.flush();
		out.close();
	}
	
	

	public Long startLesson(String serverId, String datetime, String hashValue,
			String username, long ldId, String courseId, String title,
			String desc, String countryIsoCode, String langIsoCode)
			throws RemoteException {
		try {
			ExtServerOrgMap serverMap = integrationService
					.getExtServerOrgMap(serverId);
			Authenticator
					.authenticate(serverMap, datetime, username, hashValue);
			ExtUserUseridMap userMap = integrationService.getExtUserUseridMap(
					serverMap, username);
			ExtCourseClassMap orgMap = integrationService.getExtCourseClassMap(
					serverMap, userMap, courseId, countryIsoCode, langIsoCode);
			// 1. init lesson
			Lesson lesson = monitoringService
					.initializeLesson(title, desc, Boolean.TRUE, ldId, orgMap
							.getOrganisation().getOrganisationId(), userMap
							.getUser().getUserId());
			// 2. create lessonClass for lesson
			createLessonClass(lesson, orgMap.getOrganisation(), userMap
					.getUser());
			// 3. start lesson
			monitoringService.startLesson(lesson.getLessonId(), userMap
					.getUser().getUserId());

			return lesson.getLessonId();
		} catch (Exception e) {
			throw new RemoteException(e.getMessage(), e);
		}
	}

	public Long scheduleLesson(String serverId, String datetime,
			String hashValue, String username, long ldId, String courseId,
			String title, String desc, String startDate, String countryIsoCode,
			String langIsoCode) throws RemoteException {
		try {
			ExtServerOrgMap serverMap = integrationService
					.getExtServerOrgMap(serverId);
			Authenticator
					.authenticate(serverMap, datetime, username, hashValue);
			ExtUserUseridMap userMap = integrationService.getExtUserUseridMap(
					serverMap, username);
			ExtCourseClassMap orgMap = integrationService.getExtCourseClassMap(
					serverMap, userMap, courseId, countryIsoCode, langIsoCode);
			// 1. init lesson
			Lesson lesson = monitoringService
					.initializeLesson(title, desc, Boolean.TRUE, ldId, orgMap
							.getOrganisation().getOrganisationId(), userMap
							.getUser().getUserId());
			// 2. create lessonClass for lesson
			createLessonClass(lesson, orgMap.getOrganisation(), userMap
					.getUser());
			// 3. schedule lesson
			Date date = DateUtil.convertFromLAMSFlashFormat(startDate);
			monitoringService.startLessonOnSchedule(lesson.getLessonId(), date,
					userMap.getUser().getUserId());
			return lesson.getLessonId();
		} catch (Exception e) {
			throw new RemoteException(e.getMessage(), e);
		}
	}
	
	public Element getAllStudentProgress(Document document, String serverId, String datetime,
			String hashValue, String username, long lsId, String courseID)
			throws RemoteException
	{
		try {
			ExtServerOrgMap serverMap = integrationService
					.getExtServerOrgMap(serverId);
			Authenticator
					.authenticate(serverMap, datetime, username, hashValue);
			ExtUserUseridMap userMap = integrationService.getExtUserUseridMap(
					serverMap, username);
			Lesson lesson = lessonService.getLesson(lsId);

			Element element = document.createElement(CentralConstants.ELEM_LESSON_PROGRESS);
			element.setAttribute(CentralConstants.ATTR_LESSON_ID, "" + lsId);
			
			String prefix = serverMap.getPrefix();
			if(lesson!=null){
				
				int activitiesTotal = lesson.getLearningDesign().getActivities().size();
				Iterator iterator = lesson.getLearnerProgresses().iterator();
				while(iterator.hasNext()){
	    			LearnerProgress learnProg = (LearnerProgress)iterator.next();
	    			LearnerProgressDTO learnerProgress = learnProg.getLearnerProgressData();
	    			
	    			//Iterator activities = learnProg.getLesson().getLearningDesign().getActivities().iterator();
	    			
	    			//String currActivity = "";
	    			//while (activities.hasNext())
	    			//{
	    			//	Activity act = (Activity)iterator.next();
	    			//	
	    			//	if (learnerProgress.getCurrentActivityId() == ((Activity)iterator.next()).getActivityId())
	    			//	{
	    			//		currActivity = act.getTitle();
	    			//		break;
	    			//	}
	    			//}
	    			
	    			
	    			
	    			// get the username with the integration prefix removed
	    			String userNoPrefixName = learnerProgress.getUserName().
	    				substring(prefix.length()+1);		
	    			ExtUserUseridMap learnerMap = integrationService.getExtUserUseridMap(
	    					serverMap, userNoPrefixName);
	    			
	    			Element learnerProgElem = document.createElement(CentralConstants.ELEM_LEARNER_PROGRESS);
	    			
	    			int completedActivities = learnerProgress.getCompletedActivities().length;
	    			int attemptedActivities = learnerProgress.getAttemptedActivities().length;
	    			
	    			if(learnerProgElem.getNodeType()== Node.ELEMENT_NODE)
	    			{
		    			learnerProgElem.setAttribute(CentralConstants.ATTR_LESSON_COMPLETE , "" + learnerProgress.getLessonComplete());
		    			learnerProgElem.setAttribute(CentralConstants.ATTR_ACTIVITY_COUNT , "" + activitiesTotal);
		    			learnerProgElem.setAttribute(CentralConstants.ATTR_ACTIVITIES_COMPLETED , "" + completedActivities);
		    			learnerProgElem.setAttribute(CentralConstants.ATTR_ACTIVITIES_ATTEMPTED , "" + attemptedActivities);
		    			//learnerProgElem.setAttribute(CentralConstants.ATTR_CURRENT_ACTIVITY , currActivity);
		    			learnerProgElem.setAttribute(CentralConstants.ATTR_STUDENT_ID, "" + learnerMap.getSid());
		    			learnerProgElem.setAttribute(CentralConstants.ATTR_COURSE_ID, courseID);
		    			learnerProgElem.setAttribute(CentralConstants.ATTR_USERNAME, userNoPrefixName);
		    			learnerProgElem.setAttribute(CentralConstants.ATTR_LESSON_ID, "" + lsId);	
	    			}
	    			
	    			element.appendChild(learnerProgElem);
				}
	    	}
			else{
	    		throw new Exception("Lesson with lessonID: " + lsId + " could not be found for learner progresses");
	    	}
			
			return element;
			
		} catch (Exception e) {
			throw new RemoteException(e.getMessage(), e);
		}
		
	}
	
	public Element getSingleStudentProgress(Document document, String serverId, String datetime,
			String hashValue, String username, long lsId, String courseID, String progressUser)
			throws RemoteException
	{
		try {
			ExtServerOrgMap serverMap = integrationService
					.getExtServerOrgMap(serverId);
			Authenticator
					.authenticate(serverMap, datetime, username, hashValue);
			ExtUserUseridMap userMap = integrationService.getExtUserUseridMap(
					serverMap, username);
			Lesson lesson = lessonService.getLesson(lsId);

			Element element = document.createElement(CentralConstants.ELEM_LESSON_PROGRESS);
			element.setAttribute(CentralConstants.ATTR_LESSON_ID, "" + lsId);
			
			String prefix = serverMap.getPrefix();
			

			if(lesson!=null)
			{	
				int activitiesTotal = lesson.getLearningDesign().getActivities().size();
				
				ExtUserUseridMap progressUserMap = integrationService.getExistingExtUserUseridMap(
						serverMap, progressUser);
				
				LearnerProgress learnProg = lessonService.getUserProgressForLesson(userMap.getUser().getUserId(), lsId);
				
				Element learnerProgElem = document.createElement(CentralConstants.ELEM_LEARNER_PROGRESS);
				
				// if learner progress exists, make a response, otherwise, return an empty learner progress element
				if (learnProg != null)
				{
					LearnerProgressDTO learnerProgress = learnProg.getLearnerProgressData();	
					
	    			
	    			int completedActivities = learnerProgress.getCompletedActivities().length;
	    			int attemptedActivities = learnerProgress.getAttemptedActivities().length;
	    			
	    			if(learnerProgElem.getNodeType()== Node.ELEMENT_NODE)
	    			{
		    			learnerProgElem.setAttribute(CentralConstants.ATTR_LESSON_COMPLETE , "" + learnerProgress.getLessonComplete());
		    			learnerProgElem.setAttribute(CentralConstants.ATTR_ACTIVITY_COUNT , "" + activitiesTotal);
		    			learnerProgElem.setAttribute(CentralConstants.ATTR_ACTIVITIES_COMPLETED , "" + completedActivities);
		    			learnerProgElem.setAttribute(CentralConstants.ATTR_ACTIVITIES_ATTEMPTED , "" + attemptedActivities);
		    			//learnerProgElem.setAttribute(CentralConstants.ATTR_CURRENT_ACTIVITY , currActivity);
		    			learnerProgElem.setAttribute(CentralConstants.ATTR_STUDENT_ID, "" + progressUserMap.getSid());
		    			learnerProgElem.setAttribute(CentralConstants.ATTR_COURSE_ID, courseID);
		    			learnerProgElem.setAttribute(CentralConstants.ATTR_USERNAME, progressUser);
		    			learnerProgElem.setAttribute(CentralConstants.ATTR_LESSON_ID, "" + lsId);	
	    			}
				}
				else
				{
					if(learnerProgElem.getNodeType()== Node.ELEMENT_NODE)
	    			{
		    			learnerProgElem.setAttribute(CentralConstants.ATTR_LESSON_COMPLETE , "false");
		    			learnerProgElem.setAttribute(CentralConstants.ATTR_ACTIVITY_COUNT , "" + activitiesTotal);
		    			learnerProgElem.setAttribute(CentralConstants.ATTR_ACTIVITIES_COMPLETED , "0");
		    			learnerProgElem.setAttribute(CentralConstants.ATTR_ACTIVITIES_ATTEMPTED , "0");
		    			//learnerProgElem.setAttribute(CentralConstants.ATTR_CURRENT_ACTIVITY , currActivity);
		    			learnerProgElem.setAttribute(CentralConstants.ATTR_STUDENT_ID, "" + progressUserMap.getSid());
		    			learnerProgElem.setAttribute(CentralConstants.ATTR_COURSE_ID, courseID);
		    			learnerProgElem.setAttribute(CentralConstants.ATTR_USERNAME, progressUser);
		    			learnerProgElem.setAttribute(CentralConstants.ATTR_LESSON_ID, "" + lsId);	
	    			}
				}
				
				element.appendChild(learnerProgElem);
	    	}
			else{
	    		throw new Exception("Lesson with lessonID: " + lsId + " could not be found for learner progresses");
	    	}
			
			return element;
			
		} catch (Exception e) {
			throw new RemoteException(e.getMessage(), e);
		}
		
	}

	
	
	public boolean deleteLesson(String serverId, String datetime,
			String hashValue, String username, long lsId)
			throws RemoteException {
		try {
			ExtServerOrgMap serverMap = integrationService
					.getExtServerOrgMap(serverId);
			Authenticator
					.authenticate(serverMap, datetime, username, hashValue);
			ExtUserUseridMap userMap = integrationService.getExtUserUseridMap(
					serverMap, username);
			monitoringService.removeLesson(lsId, userMap.getUser().getUserId());
			return true;
		} catch (Exception e) {
			throw new RemoteException(e.getMessage(), e);
		}
	}
	
	public Long startPreview(String serverId, String datetime, String hashValue,
			String username, Long ldId, String courseId, String title,
			String desc, String countryIsoCode, String langIsoCode)
			throws RemoteException {

		try {
			ExtServerOrgMap serverMap = integrationService
					.getExtServerOrgMap(serverId);
			Authenticator
					.authenticate(serverMap, datetime, username, hashValue);
			ExtUserUseridMap userMap = integrationService.getExtUserUseridMap(
					serverMap, username);
			ExtCourseClassMap orgMap = integrationService.getExtCourseClassMap(
					serverMap, userMap, courseId, countryIsoCode, langIsoCode);
			// 1. init lesson
			Lesson lesson = monitoringService
					.initializeLessonForPreview(title, desc, ldId, userMap.getUser().getUserId());
			// 2. create lessonClass for lesson
			monitoringService.createPreviewClassForLesson(userMap.getUser().getUserId(), lesson.getLessonId());
			
			// 3. start lesson
			monitoringService.startLesson(lesson.getLessonId(), userMap.getUser().getUserId());

			return lesson.getLessonId();
		} catch (Exception e) {
			throw new RemoteException(e.getMessage(), e);
		}

		
		
	}


	public Long importLearningDesign(HttpServletRequest request, HttpServletResponse response,
				String filePath, String username, String serverId) 
		throws RemoteException {
		
		List<String> ldErrorMsgs = new ArrayList<String>();
        List<String> toolsErrorMsgs = new ArrayList<String>();
        Long ldId = null;
        Integer workspaceFolderUid = null;
        ExtUserUseridMap userMap;
		User user = null;
    	ExtServerOrgMap serverMap = integrationService
		.getExtServerOrgMap(serverId);
    	
		try {
        
			userMap = integrationService.getExtUserUseridMap(
						serverMap, username);
	
		    user = userMap.getUser();

		    HttpSession ss = SessionManager.getSession();
		    boolean createdTemporarySession = false;
		    if ( ss == null ) {
		    	// import requires a session containing the user details, so dummy it up here.
		    	SessionManager.startSession(request, response);
		    	ss = SessionManager.getSession();
				ss.setAttribute(AttributeNames.USER, user.getUserDTO());
				createdTemporarySession = true;
		    }
		        
	     	File designFile = new File(filePath);
	     	Object[] ldResults = exportService.importLearningDesign(designFile, user, workspaceFolderUid, toolsErrorMsgs);
	     	ldId = (Long) ldResults[0];
	     	ldErrorMsgs = (List<String>) ldResults[1];
	     	toolsErrorMsgs = (List<String>) ldResults[2];
			
	     	if (  createdTemporarySession ) {
	     		SessionManager.endSession();
	     	}
	     	
			return ldId;
		} catch (Exception e) {
			throw new RemoteException(e.getMessage(), e);
		}	
		
	}
	

	private void createLessonClass(Lesson lesson, Organisation organisation,
			User creator) {
		List<User> staffList = new LinkedList<User>();
		staffList.add(creator);
		List<User> learnerList = new LinkedList<User>();
		monitoringService.createLessonClassForLesson(lesson.getLessonId(),
				organisation, organisation.getName() + "Learners", learnerList,
				organisation.getName() + "Staff", staffList, creator
						.getUserId());

	}
	

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occure
	 */
	public void init() throws ServletException {
		service = (IWorkspaceManagementService) WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext()).getBean(
						"workspaceManagementService");

		integrationService = (IntegrationService) WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext()).getBean(
						"integrationService");

		monitoringService = (IMonitoringService) WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext()).getBean(
						"monitoringService");
	
		lessonService = (ILessonService) WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext()).getBean(
						"lessonService");
		
		exportService = (IExportToolContentService) WebApplicationContextUtils
		.getRequiredWebApplicationContext(getServletContext()).getBean(
				"exportToolContentService");
	
	}
	
	private class AddUsersToLessonThread implements Runnable {
		
		private String serverId;
		private String datetime;
		private String username;
		private String hashValue;
		private String lsIdStr;
		private String courseId;
		private String country;
		private String lang;
		private String learnerIds;
		private String monitorIds;
		private HttpServletRequest request;
		
		public AddUsersToLessonThread(String serverId, 
				String datetime, String username, String hashValue, 
				String lsIdStr, String courseId, String country, String lang, 
				String learnerIds, String monitorIds, HttpServletRequest request) {
			this.serverId = serverId;
			this.datetime = datetime;
			this.username = username;
			this.hashValue = hashValue;
			this.lsIdStr = lsIdStr;
			this.courseId = courseId;
			this.country = country;
			this.lang = lang;
			this.learnerIds = learnerIds;
			this.monitorIds = monitorIds;
			this.request = request;
		}
		
		public void run() {
			addUsersToLesson(serverId, datetime, username, hashValue, 
					lsIdStr, courseId, country, lang, learnerIds, monitorIds, request);
		}
		
		/**
		 * Adds each user in learnerIds and monitorIds as learner and staff to the
		 * given lesson id; authenticates using the 3rd party server requestor's
		 * username.
		 * @param serverId
		 * @param datetime
		 * @param hashValue
		 * @param lsIdStr
		 * @param learnerIds
		 * @param monitorIds
		 * @param request
		 * @return
		 */
		public Boolean addUsersToLesson(
				String serverId, 
				String datetime, 
				String requestorUsername,
				String hashValue, 
				String lsIdStr,
				String courseId,
				String countryIsoCode,
				String langIsoCode,
				String learnerIds, 
				String monitorIds, 
				HttpServletRequest request) {
			try {
				if (learnerIds != null) {
					String[] learnerIdArray = learnerIds.split(",");
					for (String learnerId : learnerIdArray) {
						if (StringUtils.isNotBlank(learnerId)) {
							addUserToLesson(request, serverId, datetime, requestorUsername, hashValue,
								LoginRequestDispatcher.METHOD_LEARNER, lsIdStr, learnerId, courseId, countryIsoCode, langIsoCode);
					}
				}
				if (monitorIds != null) {
					String[] monitorIdArray = monitorIds.split(",");
					for (String monitorId : monitorIdArray) {
						if (StringUtils.isNotBlank(monitorId)) {
							addUserToLesson(request, serverId, datetime, requestorUsername, hashValue,
								LoginRequestDispatcher.METHOD_MONITOR, lsIdStr, monitorId, courseId, countryIsoCode, langIsoCode);
						}
					}
				}
				return true;
			} catch (UserInfoFetchException e) {
				log.error(e, e);
				return false;
			} catch (AuthenticationException e) {
				log.error(e, e);
				return false;
			}
		}
		
		private void addUserToLesson(
				HttpServletRequest request, 
				String serverId, 
				String datetime, 
				String requestorUsername, 
				String hashValue,
				String method, 
				String lsIdStr,
				String username,
				String courseId, 
				String countryIsoCode, 
				String langIsoCode) throws AuthenticationException, UserInfoFetchException {
			
			if (log.isDebugEnabled()) {
				log.debug("Adding user '" + username + "' as " + method + " to lesson with id '" + lsIdStr + "'.");
			}
			
			ExtServerOrgMap serverMap = integrationService.getExtServerOrgMap(serverId);
			Authenticator.authenticate(serverMap, datetime, requestorUsername, hashValue);
			ExtUserUseridMap userMap = integrationService.getExtUserUseridMap(serverMap, username);
			// adds user to group
			ExtCourseClassMap orgMap = integrationService.getExtCourseClassMap(serverMap, userMap, courseId, countryIsoCode, langIsoCode);
			
			if (lessonService == null) {
				lessonService = (ILessonService) WebApplicationContextUtils
						.getRequiredWebApplicationContext(request.getSession().getServletContext())
						.getBean("lessonService");
			}
			
			User user = userMap.getUser();
			if ( user == null ) {
				String error = "Unable to add user to lesson class as user is missing from the user map";
				log.error(error);
				throw new UserInfoFetchException(error);
			}
			
			if (LoginRequestDispatcher.METHOD_LEARNER.equals(method)) {
				lessonService.addLearner(Long.parseLong(lsIdStr), user.getUserId());
			} else if (LoginRequestDispatcher.METHOD_MONITOR.equals(method)) {
				lessonService.addStaffMember(Long.parseLong(lsIdStr), user.getUserId());
			}
			
		}
	}
	
}
