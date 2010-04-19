// See the COPYRIGHT file for redistribution and use restrictions.
package org.znerd.jcaller;

/**
 * Interface for the context that JCaller is executing within. It contains
 * several callback methods that will be invoked from this library at certain
 * predefined moments.
 *
 * <p>Implementations should return control to the caller quickly, as the 
 * callback methods are typically invoked synchronously, the thread does not
 * continue until the callback method returns.
 *
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public interface LibraryContext {

   /**
    * Handles a programming error and returns an exception to be thrown by the
    * caller. Typically the issue is logged.
    *
    * @param message
    *    a detail message describing the programming error,
    *    or <code>null</code>.
    *
    * @return
    *    a {@link RuntimeException} to be thrown by the caller,
    *    never <code>null</code>.
    */
   public RuntimeException programmingError(String message);

   /**
    * Handles a programming error and returns an exception to be thrown by the
    * caller. Typically the issue is logged.
    *
    * @param detectingClass
    *    the name of the class that detected the problem, or
    *    <code>null</code> if unknown.
    *
    * @param detectingMethod
    *    the name of the method within the <code>detectingClass</code> that
    *    detected the problem, or <code>null</code> if unknown.
    *
    * @param subjectClass
    *    the name of the class which exposes the programming error, or
    *    <code>null</code> if unknown.
    *
    * @param subjectMethod
    *    the name of the method (within the <code>subjectClass</code>) which
    *    exposes the programming error, or <code>null</code> if unknown.
    *
    * @param detail
    *    the detail message, can be <code>null</code>.
    *
    * @param cause
    *    the cause exception, can be <code>null</code>.
    *
    * @return
    *    an appropriate {@link RuntimeException} that can be thrown by the
    *    calling method, never <code>null</code>.
    */
   public RuntimeException programmingError(String    detectingClass,
                                            String    detectingMethod,
                                            String    subjectClass,
                                            String    subjectMethod,
                                            String    detail,
                                            Throwable cause);

   /**
    * Callback method invoked when an exception is ignored. Typically the 
    * exception is then logged by the callee.
    *
    * @param exception
    *   the exception that is ignored, never <code>null</code>.
    */
   public void ignoredException(Throwable exception);

   /**
    * Callback method invoked right before a single target is invoked.
    * After this callback, either {@link #targetCallSucceeded(String,long)}
    * or {@link #targetCallFailed(String,long,Throwable)} will be invoked.
    *
    * @param url
    *    the URL of the target, as a text string, never <code>null</code>.
    */
   public void beforeTargetCall(String url);
   // TODO: Map to 1309

   /**
    * Callback method invoked right after a single target is invoked 
    * successfully.
    *
    * @param url
    *    the URL of the target, as a text string, never <code>null</code>.
    *
    * @param duration
    *    the duration of the call, in milliseconds.
    */
   public void targetCallSucceeded(String url, long duration);
   // TODO: Map to 1301

   /**
    * Callback method invoked right after a single target is invoked 
    * without success (resulting in a failure).
    *
    * @param url
    *    the URL of the target, as a text string, never <code>null</code>.
    *
    * @param duration
    *    the duration of the call, in milliseconds.
    *
    * @param exception
    *    the exception describing the failure, never <code>null</code>.
    */
   public void targetCallFailed(String url, long duration, Throwable exception);
   // TODO: Map to 1302

   /**
    * Callback method invoked when the caller decides if a call will fail over 
    * to a next target.
    *
    * @param failOver
    *    <code>true</code> if the failing over is allowed, in principle.
    *
    * @param haveNext
    *    <code>true</code> if there is a next target to call.
    *
    * @param doContinue
    *    <code>true</code> if indeed a next target will be called.
    */
   public void failoverDecided(boolean failOver, boolean haveNext, boolean doContinue);
   // TODO: Map to 1304/1305/1306/1307

   /**
    * Callback method invoked when a call is considered completely failed. No 
    * more targets are available and/or fail-over is not (or no longer) 
    * allowed.
    *
    * @param exceptions
    *    the list of exceptions, one for each failed target,
    *    never <code>null</code> and containing at least one
    *    {@link CallException}.
    */
   public void callCompletelyFailed(CallExceptionList exceptions);
   // TODO: Map to 1303
}
