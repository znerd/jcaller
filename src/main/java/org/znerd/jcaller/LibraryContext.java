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
    * Handles a programming error (possibly logging it) and returning an
    * exception to be thrown by the caller.
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

   public RuntimeException programmingError(Throwable exception);

   public void ignoredException(Throwable exception);

   public void beforeTargetCall    (String url);                                              // TODO: Map to 1309
   public void targetCallSucceeded (String url, long duration);                               // TODO: Map to 1301
   public void targetCallFailed    (String url, long duration, Throwable exception);          // TODO: Map to 1302
   public void callCompletelyFailed(CallExceptionList exceptions);                            // TODO: Map to 1303
   public void failoverDecided     (boolean failOver, boolean haveNext, boolean doContinue);  // TODO: Map to 1304/1305/1306/1307
}
