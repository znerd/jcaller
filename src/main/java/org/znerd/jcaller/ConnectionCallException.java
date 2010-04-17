// See the COPYRIGHT file for redistribution and use restrictions.
package org.znerd.jcaller;

/**
 * Exception that indicates that a connection to a service could not be
 * established.
 *
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public abstract class ConnectionCallException
extends GenericCallException {

   /**
    * Serial version UID. Used for serialization.
    */
   private static final long serialVersionUID = -331358001038403428L;

   /**
    * Constructs a new <code>ConnectionCallException</code>.
    *
    * @param shortReason
    *    the short reason, cannot be <code>null</code>.
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
    * @param detail
    *    a detailed description of the problem, can be <code>null</code> if
    *    there is no more detail.
    *
    * @param cause
    *    the cause exception, can be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>shortReason == null
    *          || request     == null
    *          || target      == null
    *          || duration  &lt; 0</code>.
    *
    */
   ConnectionCallException(String           shortReason,
                           CallRequest      request,
                           TargetDescriptor target,
                           long             duration,
                           String           detail,
                           Throwable        cause)
   throws IllegalArgumentException {
      super(shortReason, request, target, duration, detail, cause);
   }

   /**
    * Determines whether the call has possibly (or definitely) been processed 
    * by the target service.
    *
    * @return
    *    <code>false</code>, since a connection failure indicates the call was 
    *    definitely not processed yet (so fail-over should be allowable).
    */
   public final boolean isCallPossiblyProcessed() {
      return false;
   }
}
