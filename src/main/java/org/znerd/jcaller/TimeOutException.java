// See the COPYRIGHT file for redistribution and use restrictions.
package org.znerd.jcaller;

/**
 * Exception that indicates the total time-out for a service call was reached.
 *
 * @version $Revision: 1.10 $ $Date: 2007/03/12 10:40:57 $
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public final class TimeOutException extends Exception {

   /**
    * Constructs a new <code>TimeOutException</code>.
    */
   public TimeOutException() {
      // empty
   }
}
