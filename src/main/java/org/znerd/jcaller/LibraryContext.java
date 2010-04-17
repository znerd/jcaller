// See the COPYRIGHT file for redistribution and use restrictions.
package org.znerd.jcaller;

/**
 * Interface for the context that JCaller is executing within. It contains
 * several callback methods that will be invoked from this library at certain
 * predefined moments.
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
   public static RuntimeException programmingError(String    detectingClass,
                                                   String    detectingMethod,
                                                   String    subjectClass,
                                                   String    subjectMethod,
                                                   String    detail,
                                                   Throwable cause);

   public void ignoredException(Throwable exception);

   public void beforeTargetCall    (String url);                                              // TODO: Map to 1309
   public void targetCallSucceeded (String url, long duration);                               // TODO: Map to 1301
   public void targetCallFailed    (String url, long duration, Throwable exception);          // TODO: Map to 1302
   public void callCompletelyFailed(CallExceptionList exceptions);                            // TODO: Map to 1303
   public void failoverDecided     (boolean failOver, boolean haveNext, boolean doContinue);  // TODO: Map to 1304/1305/1306/1307
}
