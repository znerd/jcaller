// See the COPYRIGHT file for redistribution and use restrictions.
package org.znerd.jcaller;

/**
 * Exception that indicates that a connection to a service could not be
 * established due to a connection time-out.
 *
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public final class ConnectionTimeOutCallException
extends ConnectionCallException {

   /**
    * Serial version UID. Used for serialization.
    */
   private static final long serialVersionUID = -1955586477316135304L;

   /**
    * Constructs a new <code>ConnectionTimeOutCallException</code>.
    *
    * @param request
    *    the original request, cannot be <code>null</code>.
    *
    * @param target
    *    descriptor for the target that was attempted to be called, cannot be
    *    <code>null</code>.
    *
    * @param duration
    *    the call duration in milliseconds, must be &gt;= 0.
    *
    * @throws IllegalArgumentException
    *    if <code>request     == null
    *          || target      == null
    *          || duration  &lt; 0</code>.
    *
    */
   public ConnectionTimeOutCallException(CallRequest      request,
                                         TargetDescriptor target,
                                         long             duration)
   throws IllegalArgumentException {
      super("Connection time-out", request, target, duration, null, null);
   }
}
