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
package org.lamsfoundation.lams.planner.dao.hibernate;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.lamsfoundation.lams.planner.PedagogicalPlannerSequenceNode;
import org.lamsfoundation.lams.planner.dao.PedagogicalPlannerDAO;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * 
 * @author Marcin Cieslak
 * 
 * @version $Revision$
 */
public class PedagogicalPlannerDAOHibernate extends HibernateDaoSupport implements PedagogicalPlannerDAO {
    private static final String FIND_ROOT_NODE = "FROM " + PedagogicalPlannerSequenceNode.class.getName()
	    + " AS n WHERE n.parent=NULL";
    private static final String FIND_PARENT_TITLE = "SELECT n.parent.uid, n.title FROM "
	    + PedagogicalPlannerSequenceNode.class.getName() + " AS n WHERE n.uid=?";
    private static final String FIND_MAX_ORDER_ID = "SELECT MAX(n.order) FROM "
	    + PedagogicalPlannerSequenceNode.class.getName() + " AS n WHERE n.parent.uid=?";
    private static final String FIND_NEIGHBOUR_NODE = "FROM " + PedagogicalPlannerSequenceNode.class.getName()
	    + " AS n WHERE ((? IS NULL AND n.parent=NULL) OR  n.parent.uid=?) AND n.order=?";

    public PedagogicalPlannerSequenceNode getByUid(Long uid) {
	return (PedagogicalPlannerSequenceNode) getHibernateTemplate().get(PedagogicalPlannerSequenceNode.class, uid);
    }

    public PedagogicalPlannerSequenceNode getRootNode() {
	List<PedagogicalPlannerSequenceNode> subnodeList = getHibernateTemplate().find(
		PedagogicalPlannerDAOHibernate.FIND_ROOT_NODE);
	PedagogicalPlannerSequenceNode rootNode = new PedagogicalPlannerSequenceNode();
	rootNode.setLocked(true);
	Set<PedagogicalPlannerSequenceNode> subnodeSet = new LinkedHashSet<PedagogicalPlannerSequenceNode>(subnodeList);
	rootNode.setSubnodes(subnodeSet);
	return rootNode;
    }

    public List<String[]> getTitlePath(PedagogicalPlannerSequenceNode node) {
	if (node.getParent() == null) {
	    return null;
	}
	Long currentUid = node.getUid();
	LinkedList<String[]> titlePath = new LinkedList<String[]>();
	List<Object[]> result;
	Object[] row;
	while (currentUid != null) {
	    result = getHibernateTemplate().find(PedagogicalPlannerDAOHibernate.FIND_PARENT_TITLE, currentUid);
	    if (result.size() > 0) {
		row = result.get(0);
		if (!currentUid.equals(node.getUid())) {
		    String title = (String) row[1];
		    titlePath.addFirst(new String[] { currentUid.toString(), title });
		}
		currentUid = (Long) row[0];
	    } else {
		return null;
	    }
	}

	return titlePath;
    }

    public void removeNode(PedagogicalPlannerSequenceNode node) {
	getHibernateTemplate().delete(node);
	getHibernateTemplate().flush();
    }

    public void saveOrUpdateNode(PedagogicalPlannerSequenceNode node) {
	getHibernateTemplate().saveOrUpdate(node);
	getHibernateTemplate().flush();
    }

    public Integer getNextOrderId(Long parentUid) {
	Integer maxOrderId = (Integer) getHibernateTemplate().find(PedagogicalPlannerDAOHibernate.FIND_MAX_ORDER_ID,
		parentUid).get(0);
	if (maxOrderId == null) {
	    maxOrderId = 0;
	}
	return maxOrderId + 1;
    }

    public PedagogicalPlannerSequenceNode getNeighbourNode(PedagogicalPlannerSequenceNode node, Integer orderDelta) {
	Integer order = node.getOrder() + orderDelta;
	Long parentUid = node.getParent() == null ? null : node.getParent().getUid();
	return (PedagogicalPlannerSequenceNode) getHibernateTemplate().find(
		PedagogicalPlannerDAOHibernate.FIND_NEIGHBOUR_NODE, new Object[] { parentUid, parentUid, order })
		.get(0);
    }
}