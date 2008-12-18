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

package org.lamsfoundation.lams.tool.videoRecorder.service;

import java.util.Collection;
import java.util.List;

import org.apache.struts.upload.FormFile;
import org.lamsfoundation.lams.notebook.model.NotebookEntry;
import org.lamsfoundation.lams.tool.videoRecorder.dto.VideoRecorderRecordingDTO;
import org.lamsfoundation.lams.tool.videoRecorder.model.VideoRecorder;
import org.lamsfoundation.lams.tool.videoRecorder.model.VideoRecorderAttachment;
import org.lamsfoundation.lams.tool.videoRecorder.model.VideoRecorderCondition;
import org.lamsfoundation.lams.tool.videoRecorder.model.VideoRecorderRecording;
import org.lamsfoundation.lams.tool.videoRecorder.model.VideoRecorderSession;
import org.lamsfoundation.lams.tool.videoRecorder.model.VideoRecorderUser;
import org.lamsfoundation.lams.tool.videoRecorder.util.VideoRecorderException;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;

/**
 * Defines the services available to the web layer from the VideoRecorder Service
 */
public interface IVideoRecorderService {
    /**
     * Makes a copy of the default content and assigns it a newContentID
     * 
     * @params newContentID
     * @return
     */
    public VideoRecorder copyDefaultContent(Long newContentID);

    /**
     * Returns an instance of the VideoRecorder tools default content.
     * 
     * @return
     */
    public VideoRecorder getDefaultContent();

    /**
     * @param toolSignature
     * @return
     */
    public Long getDefaultContentIdBySignature(String toolSignature);

    /**
     * @param toolContentID
     * @return
     */
    public VideoRecorder getVideoRecorderByContentId(Long toolContentID);

    /**
     * @param toolContentId
     * @param file
     * @param type
     * @return
     */
    public VideoRecorderAttachment uploadFileToContent(Long toolContentId, FormFile file, String type);

    /**
     * @param uuid
     * @param versionID
     */
    public void deleteFromRepository(Long uuid, Long versionID) throws VideoRecorderException;

    /**
     * @param contentID
     * @param uuid
     * @param versionID
     * @param type
     */
    public void deleteInstructionFile(Long contentID, Long uuid, Long versionID, String type);

    /**
     * @param videoRecorder
     */
    public void saveOrUpdateVideoRecorder(VideoRecorder videoRecorder);

    /**
     * @param toolSessionId
     * @return
     */
    public VideoRecorderSession getSessionBySessionId(Long toolSessionId);

    /**
     * @param videoRecorderSession
     */
    public void saveOrUpdateVideoRecorderSession(VideoRecorderSession videoRecorderSession);

    /**
     * 
     * @param userId
     * @param toolSessionId
     * @return
     */
    public VideoRecorderUser getUserByUserIdAndSessionId(Long userId, Long toolSessionId);

    /**
     * 
     * @param uid
     * @return
     */
    public VideoRecorderUser getUserByUID(Long uid);

    /**
     * 
     * @param videoRecorderUser
     */
    public void saveOrUpdateVideoRecorderUser(VideoRecorderUser videoRecorderUser);

    /**
     * 
     * @param user
     * @param videoRecorderSession
     * @return
     */
    public VideoRecorderUser createVideoRecorderUser(UserDTO user, VideoRecorderSession videoRecorderSession);
    
    /**
     * 
     * @param recordingId
     * @return
     */
    public VideoRecorderRecording getRecordingById(Long recordingId);

    /**
     * 
     * @param toolSessionId
     * @return
     */
    public List<VideoRecorderRecordingDTO> getRecordingsByToolSessionId(Long toolSessionId);
    
    /**
     * 
     * @param toolSessionId
     * @paramuserId
     * @return
     */
    public List<VideoRecorderRecordingDTO> getRecordingsByToolSessionIdAndUserId(Long toolSessionId, Long userId);

    /**
     * 
     * @param videoRecorderRecording
     */
    public void saveOrUpdateVideoRecorderRecording(VideoRecorderRecording videoRecorderRecording); 
    
    /**
     * 
     * @param id
     * @param idType
     * @param signature
     * @param userID
     * @param title
     * @param entry
     * @return
     */
    Long createNotebookEntry(Long id, Integer idType, String signature, Integer userID, String entry);

    /**
     * 
     * @param uid
     * @return
     */
    NotebookEntry getEntry(Long uid);

    /**
     * 
     * @param uid
     * @param title
     * @param entry
     */
    void updateEntry(Long uid, String entry);

    /**
     * Creates an unique name for a ChatCondition. It consists of the tool output definition name and a unique positive
     * integer number.
     * 
     * @param existingConditions
     *                existing conditions; required to check if a condition with the same name does not exist.
     * @return unique ChatCondition name
     */
    String createConditionName(Collection<VideoRecorderCondition> existingConditions);

    void releaseConditionsFromCache(VideoRecorder videoRecorder);

    void deleteCondition(VideoRecorderCondition condition);
}
