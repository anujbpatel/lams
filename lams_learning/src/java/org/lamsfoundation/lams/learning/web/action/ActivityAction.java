/*
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
*/

package org.lamsfoundation.lams.learning.web.action;

import javax.servlet.http.*;

import java.util.*;

import org.lamsfoundation.lams.learning.service.ILearnerService;
import org.lamsfoundation.lams.learning.service.LearnerService;
import org.lamsfoundation.lams.learning.service.LearnerServiceProxy;
import org.lamsfoundation.lams.learning.web.bean.SessionBean;
import org.lamsfoundation.lams.learning.web.form.ActivityForm;
import org.lamsfoundation.lams.learning.web.util.ActivityMapping;

import org.lamsfoundation.lams.usermanagement.*;
import org.lamsfoundation.lams.lesson.*;
import org.lamsfoundation.lams.learningdesign.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/** 
 * MyEclipse Struts
 * Creation date: 01-12-2005
 * 
 */
public class ActivityAction extends LearnerAction {
	
	protected static final String ACTIVITY_REQUEST_ATTRIBUTE = "activity";
	protected static final String LEARNER_PROGRESS_REQUEST_ATTRIBUTE = "learnerprogress";
	
	/**
	 * Get the learner service.
	 */
	protected ILearnerService getLearnerService() {
		ILearnerService learnerService = (LearnerService)LearnerServiceProxy.getLearnerService(this.getServlet().getServletContext());
		//learnerService.setRequest(request);
		//learnerService.setActionMappings(actionMappings);
		return learnerService;
	}
	
	/**
	 * Get the ActionMappings.
	 */
	protected ActivityMapping getActivityMapping() {
        WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(this.getServlet().getServletContext());
        return (ActivityMapping)wac.getBean("activityMapping");
	}
	
	/** 
	 * Get the current learner progress. The request attributes are checked
	 * first, if not in request then a new LearnerProgress is loaded using
	 * the LearnerService. The LearnerProgress is also stored in the
	 * session so that the Flash requests don't have to reload it.
	 */
	protected LearnerProgress getLearnerProgress(HttpServletRequest request, ActivityForm form) {
		LearnerProgress learnerProgress = (LearnerProgress)request.getAttribute(ActivityAction.LEARNER_PROGRESS_REQUEST_ATTRIBUTE);
		if (learnerProgress == null) {
			SessionBean sessionBean = getSessionBean(request);
			User learner = sessionBean.getLearner();
			Lesson lesson = sessionBean.getLesson();
			
			ILearnerService learnerService = getLearnerService();
			learnerProgress = learnerService.getProgress(learner, lesson);
			
			// Save progress in session for Flash request
			sessionBean.setLearnerProgress(learnerProgress);
			setSessionBean(sessionBean, request);
		}
		return learnerProgress;
	}
	
	/**
	 * Sets the LearnerProgress in session so that the Flash requests don't
	 * have to reload it.
	 */
	protected void setLearnerProgress(HttpServletRequest request, LearnerProgress learnerProgress) {
		request.setAttribute(ActivityAction.LEARNER_PROGRESS_REQUEST_ATTRIBUTE, learnerProgress);

		SessionBean sessionBean = getSessionBean(request);
		// Save progress in session for Flash request
		sessionBean.setLearnerProgress(learnerProgress);
		setSessionBean(sessionBean, request);
	}
	
	/**
	 * Convenience method to get the requested activity. First check the
	 * request attribute to see if it has been loaded already this request.
	 * If not in request then load from the LearnerProgress using the forms
	 * activityId. If no activityId specified then return null.
	 * @param request
	 * @param form
	 * @param learnerProgress, the current LearerProgress
	 * @return Activity in request
	 */
	protected Activity getActivity(HttpServletRequest request, ActivityForm form, LearnerProgress learnerProgress) 
	{
		Activity activity = (Activity)request.getAttribute(ActivityAction.ACTIVITY_REQUEST_ATTRIBUTE);
		if (activity == null) 
		{
			Long activityId = form.getActivityId();
			if (activityId != null) 
				activity = getActivity(activityId.longValue(), learnerProgress);
		}
		return activity;
	}
	
	/**
	 * Sets an Activity in the request attributes so that it can be used by
	 * actions forwarded to without reloading it.
	 * @param request
	 * @param activity
	 */
	protected void setActivity(HttpServletRequest request, Activity activity) 
	{
		request.setAttribute(ActivityAction.ACTIVITY_REQUEST_ATTRIBUTE, activity);
	}
	
	
	/** TODO: replace method
	 * A quick method to get an activity from within a progress. This method is
	 * temporary.
	 */
	private Activity getActivity(long activityId, LearnerProgress progress) {
		Set activities = progress.getLesson().getLearningDesign().getActivities();
		Iterator i = activities.iterator();
		while (i.hasNext()) {
			Activity activity = (Activity)i.next();
			if (activity.getActivityId().longValue() == activityId) {
				return activity;
			}
		}
		return null;
	}

}