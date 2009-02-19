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
package org.lamsfoundation.lams.webservice; 

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.lamsfoundation.lams.util.ExternalServerUtil;
import org.lamsfoundation.lams.util.WebUtil;
 
/**
 * @author pgeorges
 *
 * @web:servlet name="GetRecordingServlet"
 * @web:servlet-mapping url-pattern="/GetRecording"
 */
public class GetRecordingServlet extends HttpServlet {
	
	private static Logger logger = Logger.getLogger(GetRecordingServlet.class);
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException {

		try {
			String urlStr = WebUtil.readStrParam(request, "urlStr");
			//HashMap<String, String> params = (HashMap<String, String>)request.getAttribute("params");
			String filename = WebUtil.readStrParam(request, "filename");
			String dir = WebUtil.readStrParam(request, "dir");
			
			InputStream is = ExternalServerUtil.getResponseInputStreamFromExternalServer(urlStr, new HashMap<String,String>());
			File file = ExternalServerUtil.writeFileAndDir(is, filename, dir);
			
			logger.debug("file copy complete");
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		}		
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException {
		doPost(request, response);
	}

}
 