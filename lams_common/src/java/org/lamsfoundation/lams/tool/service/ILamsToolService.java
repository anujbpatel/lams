/*
 * LamsToolService.java
 *
 * Created on 11 January 2005, 13:49
 */

package org.lamsfoundation.lams.tool.service;

import java.util.List;

import org.lamsfoundation.lams.learningdesign.Activity;
import org.lamsfoundation.lams.tool.LamsToolServiceException;
import org.lamsfoundation.lams.tool.ToolSession;
import org.lamsfoundation.lams.usermanagement.User;
/**
 * Interface defines the services LAMS provides for Tools
 * @author chris
 */
public interface ILamsToolService
{
    /**
     * Returns a list of all learners who can use a specific set of tool content.
     * Note that none/some/all of these users may not reach the associated activity
     * so they may not end up using the content.
     * The purpose of this method is to provide a way for tools to do logic based on 
     * completions against potential completions.
     * @param toolContentID a long value that identifies the tool content (in the Tool and in LAMS).
     * @return a List of all the Learners who are scheduled to use the content.
     * @exception in case of any problems.
     */
    public List getAllPotentialLearners(long toolContentID) throws LamsToolServiceException;

    /**
     * Creates a ToolSession for a learner and activity.
     * @param learner
     * @param activity
     */
    public ToolSession createToolSession(User learner, Activity activity);
    
    /**
     * Returns the previously created ToolSession for a learner and activity.
     */
    public ToolSession getToolSession(User learner, Activity activity);
    
}
