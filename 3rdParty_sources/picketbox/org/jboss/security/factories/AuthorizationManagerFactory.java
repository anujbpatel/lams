/*
  * JBoss, Home of Professional Open Source
  * Copyright 2007, JBoss Inc., and individual contributors as indicated
  * by the @authors tag. See the copyright.txt in the distribution for a
  * full listing of individual contributors.
  *
  * This is free software; you can redistribute it and/or modify it
  * under the terms of the GNU Lesser General Public License as
  * published by the Free Software Foundation; either version 2.1 of
  * the License, or (at your option) any later version.
  *
  * This software is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  * Lesser General Public License for more details.
  *
  * You should have received a copy of the GNU Lesser General Public
  * License along with this software; if not, write to the Free
  * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */
package org.jboss.security.factories;

import java.lang.reflect.Constructor;

import org.jboss.security.AuthorizationManager;

//$Id$

/**
 *  Factory to create instances of AuthorizationManager
 *  @author Anil.Saldhana@redhat.com
 *  @since  Oct 11, 2007 
 *  @version $Revision$
 */
public class AuthorizationManagerFactory
{
   private static String fqn = "org.jboss.security.plugins.JBossAuthorizationManager";
   
   @SuppressWarnings("unchecked")
   public static AuthorizationManager getAuthorizationManager(String securityDomain)
   throws Exception
   {
      SecurityManager sm = System.getSecurityManager();
      if (sm != null) {
         sm.checkPermission(new RuntimePermission(AuthorizationManagerFactory.class.getName() + ".getAuthorizationManager"));
      }
      Class clazz = SecurityActions.loadClass(fqn);
      Constructor ctr = clazz.getConstructor(new Class[] { String.class} );
      return (AuthorizationManager) ctr.newInstance(new Object[] {securityDomain}); 
   }

   public static void setFQN(String name)
   { 
      SecurityManager sm = System.getSecurityManager();
      if (sm != null) {
         sm.checkPermission(new RuntimePermission(AuthorizationManagerFactory.class.getName() + ".setFQN"));
      }
      fqn = name;
   }
}