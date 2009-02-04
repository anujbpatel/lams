/****************************************************************
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 * USA
 *
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */

/* $Id$ */
package org.lamsfoundation.lams.tool.mc.web;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.lamsfoundation.lams.tool.mc.McAppConstants;
import org.lamsfoundation.lams.tool.mc.McCandidateAnswersDTO;
import org.lamsfoundation.lams.tool.mc.McQuestionContentDTO;
import org.lamsfoundation.lams.tool.mc.pojos.McContent;
import org.lamsfoundation.lams.tool.mc.service.IMcService;
import org.lamsfoundation.lams.web.planner.PedagogicalPlannerForm;

public class McPedagogicalPlannerForm extends PedagogicalPlannerForm {
    private static Logger logger = Logger.getLogger(McPedagogicalPlannerForm.class);

    private List<String> question;
    private List<Integer> candidateAnswerCount;
    private String candidateAnswersString;
    private List<String> correct;
    private String contentFolderID;

    public String getContentFolderID() {
	return contentFolderID;
    }

    public void setContentFolderID(String contentFolderID) {
	this.contentFolderID = contentFolderID;
    }

    public ActionMessages validate(HttpServletRequest request) {
	ActionMessages errors = new ActionMessages();
	boolean allEmpty = true;

	if (question != null && !question.isEmpty()) {
	    int questionIndex = 1;
	    for (String item : question) {
		if (item != null || !StringUtils.isEmpty(item)) {
		    try {
			List<McCandidateAnswersDTO> candidateAnswerList = extractCandidateAnswers(request,
				questionIndex);
			if (candidateAnswerList != null) {
			    boolean answersEmpty = true;
			    ActionMessage correctAnswerBlankError = null;
			    for (McCandidateAnswersDTO answer : candidateAnswerList) {
				if (answer != null && !StringUtils.isEmpty(answer.getCandidateAnswer())) {
				    allEmpty = false;
				    answersEmpty = false;
				} else if (McAppConstants.CORRECT.equals(answer.getCorrect())) {
				    correctAnswerBlankError = new ActionMessage(
					    "error.pedagogical.planner.empty.answer.selected", questionIndex);
				}
			    }
			    if (!answersEmpty && correctAnswerBlankError != null) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, correctAnswerBlankError);
			    }
			}
		    } catch (UnsupportedEncodingException e) {
			McPedagogicalPlannerForm.logger.error(e.getMessage());
			return errors;
		    }
		    questionIndex++;
		}
	    }
	}
	if (allEmpty) {
	    ActionMessage error = new ActionMessage("questions.none.submitted");
	    errors.clear();
	    errors.add(ActionMessages.GLOBAL_MESSAGE, error);
	    question = null;
	    setCandidateAnswersString("");
	} else if (!errors.isEmpty()) {
	    StringBuilder candidateAnswersBuilder = new StringBuilder();
	    Map<String, String> paramMap = request.getParameterMap();
	    setCandidateAnswerCount(new ArrayList<Integer>(getQuestionCount()));
	    for (String key : paramMap.keySet()) {
		if (key.startsWith(McAppConstants.CANDIDATE_ANSWER_PREFIX)) {
		    Object param = paramMap.get(key);
		    String answer = ((String[]) param)[0];
		    candidateAnswersBuilder.append(key).append('=').append(answer).append('&');
		}
	    }
	    setCandidateAnswersString(candidateAnswersBuilder.toString());
	    for (int questionIndex = 1; questionIndex <= getQuestionCount(); questionIndex++) {
		Object param = paramMap.get(McAppConstants.CANDIDATE_ANSWER_COUNT + questionIndex);
		int count = NumberUtils.stringToInt(((String[]) param)[0]);
		getCandidateAnswerCount().add(count);
	    }
	}

	setValid(errors.isEmpty());
	return errors;
    }

    public void fillForm(McContent mcContent, IMcService mcService) {
	if (mcContent != null) {
	    setToolContentID(mcContent.getMcContentId());

	    AuthoringUtil authoringUtil = new AuthoringUtil();
	    List<McQuestionContentDTO> questions = authoringUtil.buildDefaultQuestionContent(mcContent, mcService);

	    StringBuilder candidateAnswersBuilder = new StringBuilder();
	    setCandidateAnswerCount(new ArrayList<Integer>(questions.size()));
	    for (int questionIndex = 1; questionIndex <= questions.size(); questionIndex++) {
		McQuestionContentDTO item = questions.get(questionIndex - 1);
		int questionDisplayOrder = Integer.parseInt(item.getDisplayOrder());
		String questionText = item.getQuestion();
		setQuestion(questionDisplayOrder - 1, questionText);
		List<McCandidateAnswersDTO> candidateAnswers = item.getListCandidateAnswersDTO();

		for (int candidateAnswerIndex = 1; candidateAnswerIndex <= candidateAnswers.size(); candidateAnswerIndex++) {

		    McCandidateAnswersDTO candidateAnswer = candidateAnswers.get(candidateAnswerIndex - 1);

		    candidateAnswersBuilder.append(McAppConstants.CANDIDATE_ANSWER_PREFIX).append(questionDisplayOrder)
			    .append('-').append(candidateAnswerIndex).append('=').append(
				    candidateAnswer.getCandidateAnswer()).append('&');
		    if (candidateAnswer.getCorrect().equals(McAppConstants.CORRECT)) {
			setCorrect(questionDisplayOrder - 1, String.valueOf(candidateAnswerIndex));
		    }
		    getCandidateAnswerCount().add(candidateAnswers.size());
		}
	    }
	    setCandidateAnswersString(candidateAnswersBuilder.toString());
	}
    }

    public void setQuestion(int number, String Questions) {
	if (question == null) {
	    question = new ArrayList<String>();
	}
	while (number >= question.size()) {
	    question.add(null);
	}
	question.set(number, Questions);
    }

    public String getQuestion(int number) {
	if (question == null || number >= question.size()) {
	    return null;
	}
	return question.get(number);
    }

    public Integer getQuestionCount() {
	return question == null ? 0 : question.size();
    }

    public boolean removeQuestion(int number) {
	if (question == null || number >= question.size()) {
	    return false;
	}
	question.remove(number);
	return true;
    }

    public String getCandidateAnswersString() {
	return candidateAnswersString;
    }

    public void setCandidateAnswersString(String candidateAnswers) {
	candidateAnswersString = candidateAnswers;
    }

    public List<McCandidateAnswersDTO> extractCandidateAnswers(HttpServletRequest request, int questionIndex)
	    throws UnsupportedEncodingException {
	Map<String, String> paramMap = request.getParameterMap();
	Object param = paramMap.get(McAppConstants.CANDIDATE_ANSWER_COUNT + questionIndex);

	int count = NumberUtils.stringToInt(((String[]) param)[0]);
	int correct = Integer.parseInt(getCorrect(questionIndex - 1));
	List<McCandidateAnswersDTO> candidateAnswerList = new ArrayList<McCandidateAnswersDTO>();
	for (int index = 1; index <= count; index++) {
	    param = paramMap.get(McAppConstants.CANDIDATE_ANSWER_PREFIX + questionIndex + "-" + index);
	    String answer = ((String[]) param)[0];
	    if (answer != null) {
		McCandidateAnswersDTO candidateAnswer = new McCandidateAnswersDTO();
		candidateAnswer.setCandidateAnswer(answer);
		if (index == correct) {
		    candidateAnswer.setCorrect(McAppConstants.CORRECT);
		}
		candidateAnswerList.add(candidateAnswer);
	    }
	}
	return candidateAnswerList;
    }

    public String getCorrect(int number) {
	if (correct == null || number >= correct.size()) {
	    return null;
	}
	return correct.get(number);
    }

    public void setCorrect(int number, String correct) {
	if (this.correct == null) {
	    this.correct = new ArrayList<String>();
	}
	while (number >= this.correct.size()) {
	    this.correct.add(null);
	}
	this.correct.set(number, correct);
    }

    public List<Integer> getCandidateAnswerCount() {

	return candidateAnswerCount;
    }

    public void setCandidateAnswerCount(List<Integer> candidateAnswerCount) {

	this.candidateAnswerCount = candidateAnswerCount;
    }

    public List<String> getQuestionList() {
	return question;
    }
}