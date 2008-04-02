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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */

/* $$Id$$ */	
	
package org.lams.lams.tool.wiki.persistence;

import java.util.Date;

import org.apache.log4j.Logger;

/**
 * @hibernate.class table="tl_lawiki10_tool_session"
 * @author Steve.Ni
 * 
 * @version $Revision$
 */
public class WikiToolSession implements Cloneable{
	
	private static Logger log = Logger.getLogger(WikiToolSession.class);
	
	private Long uid;
	private Long sessionId;
	private String sessionName;
	private Wiki wiki;
	private Date sessionStartDate;
	private Date sessionEndDate;
	private boolean markReleased;
	//content topics copyed (1) or not (0)
	private int status;
	
	//optimistic lock 
	private int version;
//  **********************************************************
  	//		Function method for WikiToolSession
//  **********************************************************
  	public Object clone(){
  		
  		WikiToolSession session = null;
  		try{
  			session = (WikiToolSession) super.clone();
  		
		} catch (CloneNotSupportedException e) {
			log.error("When clone " + WikiToolSession.class + " failed");
		}
  		return session;
  	}

  	
//  **********************************************************
  	//		Get/Set methods
//  **********************************************************
	/**
	 * @hibernate.id generator-class="native" type="java.lang.Long" column="uid"
	 * @return Returns the learnerID.
	 */
	public Long getUid() {
		return uid;
	}
	public void setUid(Long uuid) {
		this.uid = uuid;
	}
	
	/**
	 * @hibernate.property column="session_end_date"
	 * @return
	 */
	public Date getSessionEndDate() {
		return sessionEndDate;
	}
	public void setSessionEndDate(Date sessionEndDate) {
		this.sessionEndDate = sessionEndDate;
	}
	/**
	 * @hibernate.property column="session_start_date"
	 * 
	 * @return
	 */
	public Date getSessionStartDate() {
		return sessionStartDate;
	}
	public void setSessionStartDate(Date sessionStartDate) {
		this.sessionStartDate = sessionStartDate;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

	/**
 	 * @hibernate.many-to-one  column="wiki_uid"
 	 * cascade="none"
	 * @return
	 */
	public Wiki getWiki() {
		return wiki;
	}
	public void setWiki(Wiki wiki) {
		this.wiki = wiki;
	}
	/**
	 * @hibernate.property column="session_id"
	 * @return
	 */
	public Long getSessionId() {
		return sessionId;
	}
	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
	}

	/**
	 * @hibernate.property column="session_name" length="250"
	 * @return Returns the session name
	 */
	public String getSessionName() {
		return sessionName;
	}

	/**
	 * 
	 * @param sessionName The session name to set.
	 */
	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}

	/**
	 * @hibernate.property column="mark_released" 
	 * @return Returns the mark released flag
	 */
	public boolean isMarkReleased() {
		return markReleased;
	}
	public void setMarkReleased(boolean markReleased) {
		this.markReleased = markReleased;
	}

	/**
	 * @hibernate.version column="version" 
	 */
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}
