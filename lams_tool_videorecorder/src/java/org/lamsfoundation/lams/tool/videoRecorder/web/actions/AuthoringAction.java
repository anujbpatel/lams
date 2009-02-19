/****************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */
/* $$Id$$ */

package org.lamsfoundation.lams.tool.videoRecorder.web.actions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;
import org.lamsfoundation.lams.authoring.web.AuthoringConstants;
import org.lamsfoundation.lams.contentrepository.client.IToolContentHandler;
import org.lamsfoundation.lams.learningdesign.TextSearchConditionComparator;
import org.lamsfoundation.lams.tool.ToolAccessMode;
import org.lamsfoundation.lams.tool.ToolSessionManager;
import org.lamsfoundation.lams.tool.videoRecorder.dto.VideoRecorderDTO;
import org.lamsfoundation.lams.tool.videoRecorder.dto.VideoRecorderRecordingDTO;
import org.lamsfoundation.lams.tool.videoRecorder.dto.VideoRecorderSessionDTO;
import org.lamsfoundation.lams.tool.videoRecorder.model.VideoRecorder;
import org.lamsfoundation.lams.tool.videoRecorder.model.VideoRecorderAttachment;
import org.lamsfoundation.lams.tool.videoRecorder.model.VideoRecorderCondition;
import org.lamsfoundation.lams.tool.videoRecorder.model.VideoRecorderSession;
import org.lamsfoundation.lams.tool.videoRecorder.model.VideoRecorderUser;
import org.lamsfoundation.lams.tool.videoRecorder.service.IVideoRecorderService;
import org.lamsfoundation.lams.tool.videoRecorder.service.VideoRecorderService;
import org.lamsfoundation.lams.tool.videoRecorder.service.VideoRecorderServiceProxy;
import org.lamsfoundation.lams.tool.videoRecorder.util.VideoRecorderConstants;
import org.lamsfoundation.lams.tool.videoRecorder.web.forms.AuthoringForm;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.util.FileValidatorUtil;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.web.action.LamsDispatchAction;
import org.lamsfoundation.lams.web.session.SessionManager;
import org.lamsfoundation.lams.web.util.AttributeNames;
import org.lamsfoundation.lams.web.util.SessionMap;

/**
 * @author
 * @version
 * 
 * @struts.action path="/authoring" name="authoringForm" parameter="dispatch" scope="request" validate="false"
 * 
 * @struts.action-forward name="success" path="tiles:/authoring/main"
 * @struts.action-forward name="message_page" path="tiles:/generic/message"
 */
public class AuthoringAction extends LamsDispatchAction {

    private static Logger logger = Logger.getLogger(AuthoringAction.class);

    public IVideoRecorderService videoRecorderService;

    // Authoring SessionMap key names
    private static final String KEY_TOOL_CONTENT_ID = "toolContentID";
    private static final String KEY_CONTENT_FOLDER_ID = "contentFolderID";
    private static final String KEY_MODE = "mode";
    private static final String KEY_ONLINE_FILES = "onlineFiles";
    private static final String KEY_OFFLINE_FILES = "offlineFiles";
    private static final String KEY_UNSAVED_ONLINE_FILES = "unsavedOnlineFiles";
    private static final String KEY_UNSAVED_OFFLINE_FILES = "unsavedOfflineFiles";
    private static final String KEY_DELETED_FILES = "deletedFiles";

    /**
     * Default method when no dispatch parameter is specified. It is expected that the parameter
     * <code>toolContentID</code> will be passed in. This will be used to retrieve content for this tool.
     * 
     */
    @Override
    protected ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {

	// Extract toolContentID from parameters.
	Long toolContentID = new Long(WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_CONTENT_ID));

	// get httpsession
	HttpSession ss = SessionManager.getSession();
	
	// get LAMS user
	UserDTO user = (UserDTO)ss.getAttribute(AttributeNames.USER);
	
	String contentFolderID = WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID);

	ToolAccessMode mode = WebUtil.readToolAccessModeParam(request, "mode", true);

	// set up videoRecorderService
	if (videoRecorderService == null) {
	    videoRecorderService = VideoRecorderServiceProxy.getVideoRecorderService(this.getServlet().getServletContext());
	}
	
	// get toolSessionManager
	ToolSessionManager toolSessionManager =VideoRecorderServiceProxy.getVideoRecorderSessionManager(this.getServlet().getServletContext());
	
	// retrieving VideoRecorder with given toolContentID
	VideoRecorder videoRecorder = videoRecorderService.getVideoRecorderByContentId(toolContentID);
	if (videoRecorder == null) {
	    videoRecorder = videoRecorderService.copyDefaultContent(toolContentID);
	    videoRecorder.setCreateDate(new Date());
	    videoRecorderService.saveOrUpdateVideoRecorder(videoRecorder);
	    // TODO NOTE: this causes DB orphans when LD not saved.
	}
	
	// transform to dto
	VideoRecorderDTO videoRecorderDT0 = new VideoRecorderDTO(videoRecorder);
	
	// get recordings
	List<VideoRecorderRecordingDTO> recordings = videoRecorderService.getRecordingsByToolContentId(toolContentID);
	
	// setup first recording
	VideoRecorderRecordingDTO firstRecording = null;
	
	// if there are recordings
	if(!recordings.isEmpty()){
		// fetch the first one
		firstRecording = recordings.get(0);
	}
	
	// add it to the request
	request.setAttribute("videoRecorderRecordingDTO", firstRecording);
	
	if (mode != null && mode.isTeacher()) {
	    // Set the defineLater flag so that learners cannot use content
	    // while we
	    // are editing. This flag is released when updateContent is called.
	    videoRecorder.setDefineLater(true);
	    videoRecorderService.saveOrUpdateVideoRecorder(videoRecorder);
	}

	// Set up the authForm.
	AuthoringForm authForm = (AuthoringForm) form;
	updateAuthForm(authForm, videoRecorder);

	// Set up sessionMap
	SessionMap<String, Object> map = createSessionMap(videoRecorder, getAccessMode(request), contentFolderID,
		toolContentID);
	authForm.setSessionMapID(map.getSessionID());

	// add the sessionMap to HTTPSession
	request.getSession().setAttribute(map.getSessionID(), map);
	request.setAttribute(VideoRecorderConstants.ATTR_SESSION_MAP, map);
	
	// add the toolContentId
	request.setAttribute("toolContentId", toolContentID);
	
	// add the videoRecorderDTO
	request.setAttribute("videoRecorderDTO", videoRecorderDT0);
	
	// set language xml
	request.setAttribute("languageXML", videoRecorderService.getLanguageXMLForFCK());

	return mapping.findForward("success");
    }

    public ActionForward updateContent(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {
	// TODO need error checking.

	// get authForm and session map.
	AuthoringForm authForm = (AuthoringForm) form;
	SessionMap<String, Object> map = getSessionMap(request, authForm);

	// get videoRecorder content.
	VideoRecorder videoRecorder = videoRecorderService.getVideoRecorderByContentId((Long) map.get(AuthoringAction.KEY_TOOL_CONTENT_ID));

	// update videoRecorder content using form inputs.
	ToolAccessMode mode = (ToolAccessMode) map.get(AuthoringAction.KEY_MODE);
	updateVideoRecorder(videoRecorder, authForm, mode);

	// remove attachments marked for deletion.
	Set<VideoRecorderAttachment> attachments = videoRecorder.getVideoRecorderAttachments();
	if (attachments == null) {
	    attachments = new HashSet<VideoRecorderAttachment>();
	}

	for (VideoRecorderAttachment att : getAttList(AuthoringAction.KEY_DELETED_FILES, map)) {
	    // remove from db, leave in repository
	    attachments.remove(att);
	}

	// add unsaved attachments
	attachments.addAll(getAttList(AuthoringAction.KEY_UNSAVED_ONLINE_FILES, map));
	attachments.addAll(getAttList(AuthoringAction.KEY_UNSAVED_OFFLINE_FILES, map));

	// set attachments in case it didn't exist
	videoRecorder.setVideoRecorderAttachments(attachments);

	videoRecorderService.releaseConditionsFromCache(videoRecorder);

	Set<VideoRecorderCondition> conditions = videoRecorder.getConditions();
	if (conditions == null) {
	    conditions = new TreeSet<VideoRecorderCondition>(new TextSearchConditionComparator());
	}
	SortedSet<VideoRecorderCondition> conditionSet = (SortedSet<VideoRecorderCondition>) map
		.get(VideoRecorderConstants.ATTR_CONDITION_SET);
	conditions.addAll(conditionSet);

	List<VideoRecorderCondition> deletedConditionList = (List<VideoRecorderCondition>) map
		.get(VideoRecorderConstants.ATTR_DELETED_CONDITION_LIST);
	if (deletedConditionList != null) {
	    for (VideoRecorderCondition condition : deletedConditionList) {
		// remove from db, leave in repository
		conditions.remove(condition);
		videoRecorderService.deleteCondition(condition);
	    }
	}

	// set attachments in case it didn't exist
	videoRecorder.setConditions(conditionSet);

	// set the update date
	videoRecorder.setUpdateDate(new Date());

	// set changed attributes
	updateVideoRecorder(videoRecorder, authForm, mode);
	
	// releasing defineLater flag so that learner can start using the tool.
	videoRecorder.setDefineLater(false);

	videoRecorderService.saveOrUpdateVideoRecorder(videoRecorder);

	request.setAttribute(AuthoringConstants.LAMS_AUTHORING_SUCCESS_FLAG, Boolean.TRUE);

	// add the sessionMapID to form
	authForm.setSessionMapID(map.getSessionID());

	request.setAttribute(VideoRecorderConstants.ATTR_SESSION_MAP, map);

	return mapping.findForward("success");
    }

    public ActionForward uploadOnline(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {
	return uploadFile(mapping, (AuthoringForm) form, IToolContentHandler.TYPE_ONLINE, request);
    }

    public ActionForward uploadOffline(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {
	return uploadFile(mapping, (AuthoringForm) form, IToolContentHandler.TYPE_OFFLINE, request);
    }

    public ActionForward deleteOnline(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {
	return deleteFile(mapping, (AuthoringForm) form, IToolContentHandler.TYPE_ONLINE, request);
    }

    public ActionForward deleteOffline(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {
	return deleteFile(mapping, (AuthoringForm) form, IToolContentHandler.TYPE_OFFLINE, request);
    }

    public ActionForward removeUnsavedOnline(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {
	return removeUnsaved(mapping, (AuthoringForm) form, IToolContentHandler.TYPE_ONLINE, request);
    }

    public ActionForward removeUnsavedOffline(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {
	return removeUnsaved(mapping, (AuthoringForm) form, IToolContentHandler.TYPE_OFFLINE, request);
    }

    /* ========== Private Methods ********** */

    private ActionForward uploadFile(ActionMapping mapping, AuthoringForm authForm, String type,
	    HttpServletRequest request) {
	SessionMap<String, Object> map = getSessionMap(request, authForm);

	FormFile file;
	List<VideoRecorderAttachment> unsavedFiles;
	List<VideoRecorderAttachment> savedFiles;
	if (StringUtils.equals(IToolContentHandler.TYPE_OFFLINE, type)) {
	    file = authForm.getOfflineFile();
	    unsavedFiles = getAttList(AuthoringAction.KEY_UNSAVED_OFFLINE_FILES, map);

	    savedFiles = getAttList(AuthoringAction.KEY_OFFLINE_FILES, map);
	} else {
	    file = authForm.getOnlineFile();
	    unsavedFiles = getAttList(AuthoringAction.KEY_UNSAVED_ONLINE_FILES, map);

	    savedFiles = getAttList(AuthoringAction.KEY_ONLINE_FILES, map);
	}

	// validate file max size
	ActionMessages errors = new ActionMessages();
	FileValidatorUtil.validateFileSize(file, true, errors);
	if (!errors.isEmpty()) {
	    request.setAttribute(VideoRecorderConstants.ATTR_SESSION_MAP, map);
	    this.saveErrors(request, errors);
	    return mapping.findForward("success");
	}

	if (file.getFileName().length() != 0) {

	    // upload file to repository
	    VideoRecorderAttachment newAtt = videoRecorderService.uploadFileToContent((Long) map
		    .get(AuthoringAction.KEY_TOOL_CONTENT_ID), file, type);

	    // Add attachment to unsavedFiles
	    // check to see if file with same name exists
	    VideoRecorderAttachment currAtt;
	    Iterator iter = savedFiles.iterator();
	    while (iter.hasNext()) {
		currAtt = (VideoRecorderAttachment) iter.next();
		if (StringUtils.equals(currAtt.getFileName(), newAtt.getFileName())
			&& StringUtils.equals(currAtt.getFileType(), newAtt.getFileType())) {
		    // move from this this list to deleted list.
		    getAttList(AuthoringAction.KEY_DELETED_FILES, map).add(currAtt);
		    iter.remove();
		    break;
		}
	    }
	    unsavedFiles.add(newAtt);

	    request.setAttribute(VideoRecorderConstants.ATTR_SESSION_MAP, map);
	    request.setAttribute("unsavedChanges", new Boolean(true));
	}
	return mapping.findForward("success");
    }

    private ActionForward deleteFile(ActionMapping mapping, AuthoringForm authForm, String type,
	    HttpServletRequest request) {
	SessionMap<String, Object> map = getSessionMap(request, authForm);

	List fileList;
	if (StringUtils.equals(IToolContentHandler.TYPE_OFFLINE, type)) {
	    fileList = getAttList(AuthoringAction.KEY_OFFLINE_FILES, map);
	} else {
	    fileList = getAttList(AuthoringAction.KEY_ONLINE_FILES, map);
	}

	Iterator iter = fileList.iterator();

	while (iter.hasNext()) {
	    VideoRecorderAttachment att = (VideoRecorderAttachment) iter.next();

	    if (att.getFileUuid().equals(authForm.getDeleteFileUuid())) {
		// move to delete file list, deleted at next updateContent
		getAttList(AuthoringAction.KEY_DELETED_FILES, map).add(att);

		// remove from this list
		iter.remove();
		break;
	    }
	}

	request.setAttribute(VideoRecorderConstants.ATTR_SESSION_MAP, map);
	request.setAttribute("unsavedChanges", new Boolean(true));

	return mapping.findForward("success");
    }

    private ActionForward removeUnsaved(ActionMapping mapping, AuthoringForm authForm, String type,
	    HttpServletRequest request) {
	SessionMap<String, Object> map = getSessionMap(request, authForm);

	List unsavedFiles;

	if (StringUtils.equals(IToolContentHandler.TYPE_OFFLINE, type)) {
	    unsavedFiles = getAttList(AuthoringAction.KEY_UNSAVED_OFFLINE_FILES, map);
	} else {
	    unsavedFiles = getAttList(AuthoringAction.KEY_UNSAVED_ONLINE_FILES, map);
	}

	Iterator iter = unsavedFiles.iterator();
	while (iter.hasNext()) {
	    VideoRecorderAttachment att = (VideoRecorderAttachment) iter.next();

	    if (att.getFileUuid().equals(authForm.getDeleteFileUuid())) {
		// delete from repository and list
		videoRecorderService.deleteFromRepository(att.getFileUuid(), att.getFileVersionId());
		iter.remove();
		break;
	    }
	}

	request.setAttribute(VideoRecorderConstants.ATTR_SESSION_MAP, map);
	request.setAttribute("unsavedChanges", new Boolean(true));

	return mapping.findForward("success");
    }

    /**
     * Updates VideoRecorder content using AuthoringForm inputs.
     * 
     * @param authForm
     * @param mode
     * @return
     */
    private void updateVideoRecorder(VideoRecorder videoRecorder, AuthoringForm authForm, ToolAccessMode mode) {
	videoRecorder.setTitle(authForm.getTitle());
	videoRecorder.setInstructions(authForm.getInstructions());
	//if (mode.isAuthor()) { // Teacher cannot modify following
	    videoRecorder.setOfflineInstructions(authForm.getOnlineInstruction());
	    videoRecorder.setOnlineInstructions(authForm.getOfflineInstruction());
	    videoRecorder.setLockOnFinished(authForm.isLockOnFinished());
	    videoRecorder.setAllowUseVoice(authForm.isAllowUseVoice());
	    videoRecorder.setAllowUseCamera(authForm.isAllowUseCamera());
	    videoRecorder.setAllowLearnerVideoVisibility(authForm.isAllowLearnerVideoVisibility());
	    videoRecorder.setAllowComments(authForm.isAllowComments());
	    videoRecorder.setAllowRatings(authForm.isAllowComments());
	    videoRecorder.setExportAll(authForm.isExportAll());
	    videoRecorder.setExportOffline(authForm.isExportOffline());
	//}
    }

    /**
     * Updates AuthoringForm using VideoRecorder content.
     * 
     * @param videoRecorder
     * @param authForm
     * @return
     */
    private void updateAuthForm(AuthoringForm authForm, VideoRecorder videoRecorder) {
	authForm.setTitle(videoRecorder.getTitle());
	authForm.setInstructions(videoRecorder.getInstructions());
	authForm.setOnlineInstruction(videoRecorder.getOnlineInstructions());
	authForm.setOfflineInstruction(videoRecorder.getOfflineInstructions());
	authForm.setLockOnFinished(videoRecorder.isLockOnFinished());
	authForm.setAllowUseVoice(videoRecorder.isAllowUseVoice());
	authForm.setAllowUseCamera(videoRecorder.isAllowUseCamera());
	authForm.setAllowLearnerVideoVisibility(videoRecorder.isAllowLearnerVideoVisibility());
	authForm.setAllowComments(videoRecorder.isAllowComments());
	authForm.setAllowRatings(videoRecorder.isAllowComments());
	authForm.setExportAll(videoRecorder.isExportAll());
	authForm.setExportOffline(videoRecorder.isExportOffline());
    }

    /**
     * Updates SessionMap using VideoRecorder content.
     * 
     * @param videoRecorder
     * @param mode
     */
    private SessionMap<String, Object> createSessionMap(VideoRecorder videoRecorder, ToolAccessMode mode, String contentFolderID,
	    Long toolContentID) {

	SessionMap<String, Object> map = new SessionMap<String, Object>();

	map.put(AuthoringAction.KEY_MODE, mode);
	map.put(AuthoringAction.KEY_CONTENT_FOLDER_ID, contentFolderID);
	map.put(AuthoringAction.KEY_TOOL_CONTENT_ID, toolContentID);
	map.put(AuthoringAction.KEY_ONLINE_FILES, new LinkedList<VideoRecorderAttachment>());
	map.put(AuthoringAction.KEY_OFFLINE_FILES, new LinkedList<VideoRecorderAttachment>());
	map.put(AuthoringAction.KEY_UNSAVED_ONLINE_FILES, new LinkedList<VideoRecorderAttachment>());
	map.put(AuthoringAction.KEY_UNSAVED_OFFLINE_FILES, new LinkedList<VideoRecorderAttachment>());
	map.put(AuthoringAction.KEY_DELETED_FILES, new LinkedList<VideoRecorderAttachment>());
	map.put(VideoRecorderConstants.ATTR_DELETED_CONDITION_LIST, new ArrayList<VideoRecorderCondition>());

	Iterator iter = videoRecorder.getVideoRecorderAttachments().iterator();
	while (iter.hasNext()) {
	    VideoRecorderAttachment attachment = (VideoRecorderAttachment) iter.next();
	    String type = attachment.getFileType();
	    if (type.equals(IToolContentHandler.TYPE_OFFLINE)) {
		getAttList(AuthoringAction.KEY_OFFLINE_FILES, map).add(attachment);
	    }
	    if (type.equals(IToolContentHandler.TYPE_ONLINE)) {
		getAttList(AuthoringAction.KEY_ONLINE_FILES, map).add(attachment);
	    }
	}

	SortedSet<VideoRecorderCondition> set = new TreeSet<VideoRecorderCondition>(new TextSearchConditionComparator());

	if (videoRecorder.getConditions() != null) {
	    set.addAll(videoRecorder.getConditions());
	}
	map.put(VideoRecorderConstants.ATTR_CONDITION_SET, set);
	return map;
    }

    /**
     * Get ToolAccessMode from HttpRequest parameters. Default value is AUTHOR mode.
     * 
     * @param request
     * @return
     */
    private ToolAccessMode getAccessMode(HttpServletRequest request) {
	ToolAccessMode mode;
	String modeStr = request.getParameter(AttributeNames.ATTR_MODE);
	if (StringUtils.equalsIgnoreCase(modeStr, ToolAccessMode.TEACHER.toString())) {
	    mode = ToolAccessMode.TEACHER;
	} else {
	    mode = ToolAccessMode.AUTHOR;
	}
	return mode;
    }

    /**
     * Retrieves a List of attachments from the map using the key.
     * 
     * @param key
     * @param map
     * @return
     */
    private List<VideoRecorderAttachment> getAttList(String key, SessionMap<String, Object> map) {
	List<VideoRecorderAttachment> list = (List<VideoRecorderAttachment>) map.get(key);
	return list;
    }

    /**
     * Retrieve the SessionMap from the HttpSession.
     * 
     * @param request
     * @param authForm
     * @return
     */
    private SessionMap<String, Object> getSessionMap(HttpServletRequest request, AuthoringForm authForm) {
	return (SessionMap<String, Object>) request.getSession().getAttribute(authForm.getSessionMapID());
    }

    /**
     * Lists deleted VideoRecorder conditions, which could be persisted or non-persisted items.
     * 
     * @param request
     * @return
     */
    private List getDeletedVideoRecorderConditionList(SessionMap sessionMap) {
	List list = (List) sessionMap.get(VideoRecorderConstants.ATTR_DELETED_CONDITION_LIST);
	if (list == null) {
	    list = new ArrayList();
	    sessionMap.put(VideoRecorderConstants.ATTR_DELETED_CONDITION_LIST, list);
	}
	return list;

    }
}
