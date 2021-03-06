// See the COPYRIGHT file for redistribution and use restrictions.
package org.znerd.jcaller;

/**
 * Exception that indicates that data was not received on a socket within a
 * designated time-out period.
 *
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public final class SocketTimeOutCallException
extends GenericCallException {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Serial version UID. Used for serialization.
    */
   private static final long serialVersionUID = 3960746542315816035L;


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>SocketTimeOutCallException</code>.
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
    */
   public SocketTimeOutCallException(CallRequest      request,
                                     TargetDescriptor target,
                                     long             duration)
   throws IllegalArgumentException {
      super("Socket time-out", request, target, duration, null, null);
   }
}
