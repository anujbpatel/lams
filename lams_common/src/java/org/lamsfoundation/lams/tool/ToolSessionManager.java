/* ********************************************************************************
 *  Copyright Notice
 *  =================
 * This file contains propriety information of LAMS Foundation. 
 * Copying or reproduction with prior written permission is prohibited.
 * Copyright (c) 2004 
 * Created on 2004-12-6
 ******************************************************************************** */

package org.lamsfoundation.lams.tool;

import java.util.List;

import org.lamsfoundation.lams.lesson.LearnerProgress;



/**
 * The interface that defines the tool's contract regarding session. It must 
 * be implemented by the tool to establish the communication channel between
 * tool and lams core service.
 * 
 * @author Jacky Fang 
 * @since 2004-12-6
 * @version 1.1
 */
public interface ToolSessionManager
{
    /**
     * Create a tool session for a piece of tool content using the tool 
     * session id generated by lams. 
     * @param toolSessionId the generated tool session id.
     * @param toolContentId the tool content id specified.
     */
    public void createToolSession(Long toolSessionId, Long toolContentId);
    
    /**
     * Call the controller service to complete and leave the tool session.
     * @param toolSessionId the runtime tool session id.
     * @return the data object that wraps the progess information.
     */
    public LearnerProgress leaveToolSession(Long toolSessionId);
    
    public ToolSessionExportOutputData exportToolSession(Long toolSessionId);

    public ToolSessionExportOutputData exportToolSession(List toolSessionIds);
    
}
