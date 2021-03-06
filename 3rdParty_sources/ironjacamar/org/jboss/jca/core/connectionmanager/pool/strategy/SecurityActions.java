/*
 * IronJacamar, a Java EE Connector Architecture implementation
 * Copyright 2010, Red Hat Inc, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
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

package org.jboss.jca.core.connectionmanager.pool.strategy;

import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.security.auth.Subject;

/**
 * Privileged Blocks
 * @author <a href="mailto:jesper.pedersen@ironjacamar.org">Jesper Pedersen</a>
 */
class SecurityActions
{ 
   /**
    * Constructor
    */
   private SecurityActions()
   {
   }

   /**
    * Get the hash code for a Subject
    * @param subject The Subject
    * @return The hash code
    */
   static int hashCode(final Subject subject)
   {
      if (System.getSecurityManager() == null)
         return subject.hashCode();

      Integer hashCode = AccessController.doPrivileged(new PrivilegedAction<Integer>() 
      {
         public Integer run()
         {
            return subject.hashCode();
         }
      });

      return hashCode.intValue();
   }

   /**
    * Verify if two Subject's are equal
    * @param s1 The first Subject
    * @param s2 The second Subject
    * @return True if equal; otherwise false
    */
   static boolean equals(final Subject s1, final Subject s2)
   {
      if (System.getSecurityManager() == null)
         return s1 != null ? s1.equals(s2) : s2 == null;

      Boolean equals = AccessController.doPrivileged(new PrivilegedAction<Boolean>() 
      {
         public Boolean run()
         {
            return s1 != null ? s1.equals(s2) : s2 == null;
         }
      });

      return equals.booleanValue();
   }
}
