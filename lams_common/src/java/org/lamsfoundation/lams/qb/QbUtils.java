package org.lamsfoundation.lams.qb;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Function;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.lamsfoundation.lams.qb.form.QbQuestionForm;
import org.lamsfoundation.lams.qb.model.QbCollection;
import org.lamsfoundation.lams.qb.service.IQbService;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.web.session.SessionManager;
import org.lamsfoundation.lams.web.util.AttributeNames;

public class QbUtils {

    public static final Function<String, String> QB_MIGRATION_CKEDITOR_CLEANER = string -> string == null ? null
	    : string.replaceAll(">\\&nbsp;", ">").replaceAll("\\r|\\n", "").trim();

    public static final Function<String, String> QB_MIGRATION_TAG_CLEANER = string -> string == null ? null
	    : WebUtil.removeHTMLtags(string).replaceAll(">\\&nbsp;", " ").replaceAll("\\t", " ").trim();

    public static final Function<String, String> QB_MIGRATION_QUESTION_NAME_GENERATOR = description -> {
	String name = QB_MIGRATION_TAG_CLEANER.apply(description);
	return name == null ? null : name.substring(0, Math.min(80, name.length()));
    };

    public static final Function<String, String> QB_MIGRATION_TRIMMER = string -> StringUtils.isBlank(string) ? null
	    : string.trim();

    public static void fillFormWithUserCollections(IQbService qbService, QbQuestionForm form, Long qbQuestionUid) {
	//prepare data for displaying collections
	Integer userId = QbUtils.getUserId();
	Collection<QbCollection> userCollections = qbService.getUserCollections(userId);
	form.setUserCollections(userCollections);

	//in case request came not from the tool, collectioUid is already supplied as parameter from collections.jsp
	final boolean isRequestCameFromTool = StringUtils.isNotBlank(form.getSessionMapID());
	if (!isRequestCameFromTool) {
	    return;
	}

	Collection<QbCollection> questionCollections = qbQuestionUid == null ? new LinkedList<>()
		: qbService.getQuestionCollectionsByUid(qbQuestionUid);

	Long collectionUid = null;
	if (questionCollections.isEmpty()) {
	    //set private collection as default, if question is new or doesn't have associated collection
	    for (QbCollection collection : userCollections) {
		if (collection.isPersonal()) {
		    collectionUid = collection.getUid();
		    break;
		}
	    }
	} else {
	    collectionUid = questionCollections.iterator().next().getUid();
	}
	form.setOldCollectionUid(collectionUid);
    }

    private static Integer getUserId() {
	HttpSession ss = SessionManager.getSession();
	UserDTO user = (UserDTO) ss.getAttribute(AttributeNames.USER);
	return user != null ? user.getUserID() : null;
    }

}
