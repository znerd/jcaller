// See the COPYRIGHT file for redistribution and use restrictions.
package org.znerd.jcaller;

import java.util.Iterator;

/**
 * Abstraction of a service caller for a TCP-based service. Service caller
 * implementations can be used to perform a call to a service, and potentially
 * fail-over to other back-ends if one is not available.
 *
 * <a name="section-descriptors"></a>
 * <h2>Descriptors</h2>
 *
 * <p>A service caller has a link to a {@link Descriptor} instance, which
 * describes which back-ends to call. A <code>Descriptor</code> describes
 * either a single back-end or a group of back-ends. A single back-end is
 * represented by a {@link TargetDescriptor} instance, while a groups of
 * back-ends is represented by a {@link GroupDescriptor} instance. Both are
 * subclasses of class <code>Descriptor</code>.
 *
 * <p>There is only one type of target descriptor, but there are
 * different types of group descriptor:
 *
 * <ul>
 * <li><em>ordered</em>: underlying descriptors are iterated over in
 *     sequential order;
 * <li><em>random</em>: underlying descriptors are iterated over in random
 *     order.
 * </ul>
 *
 * <p>Note that group descriptors may contain other group descriptors.
 *
 * <a name="section-timeouts"></a>
 * <h2>Time-outs</h2>
 *
 * <p>Target descriptors support three kinds of time-out:
 *
 * <ul>
 * <li><em>total time-out</em>: limits the duration of a call,
 *     including connection time, time used to send the request, time used to
 *     receive the response, etcetera;
 * <li><em>connection time-out</em>: limits the time for attempting to
 *     establish a connection;
 * <li><em>socket time-out</em>: limits the time for attempting to receive
 *     data on a socket.
 * </ul>
 *
 * <a name="section-lbfo"></a>
 * <h2>Load-balancing and fail-over</h2>
 *
 * Service callers can help in evenly distributing processing across
 * available resources. This load-balancing is achieved by using a group
 * descriptor which iterates over the underlying descriptors in a
 * <em>random</em> order.
 *
 * <p>Unlike load-balancing, fail-over allows the detection of a failure and
 * the migration of the processing to a similar, redundant back-end. This can
 * be achieved using any type of group descriptor (either <em>ordered</em> or
 * <em>random</em>).
 *
 * <p>Not all calls should be retried on a different back-end. For example, if
 * a call fails because the back-end indicates the request is considered
 * incorrect, then it may be considered unappropriate to try other back-ends.
 * The {@link #shouldFailOver(CallRequest,CallConfig,CallExceptionList)
 * shouldFailOver} method determines whether a failed call will be retried.
 *
 * <p>Consider the following hypothetical scenario. A company has two data
 * centers, a primary site and a secondary backup site. The primary site has
 * 3 back-ends running an <em>eshop</em> service, while the hot backup site
 * has only 2 such back-ends. The back-ends at the primary site should always
 * be preferred over the back-ends at the backup site. At each site, load
 * should be evenly distributed among the available back-ends within that
 * site.
 *
 * <p>Such a scenario can be converted to a descriptor configuration such as
 * the following:
 *
 * <ul>
 * <li>the service caller uses a group descriptor called <em>All</em> of type
 *     <em>ordered</em>. This group descriptor contains 2 other group
 *     descriptors: <em>MainSite</em> and <em>BackupSite</em>.
 * <li>the group descriptor <em>MainSite</em> is of type <em>random</em> and
 *     contains 3 target descriptors, called <em>Main1</em>,
 *     <em>Main2</em> and <em>Main3</em>.
 * <li>the group descriptor <em>BackupSite</em> is also of type
 *     <em>random</em> and contains 2 target descriptors, called
 *     <em>Backup1</em> and <em>Backup2</em>.
 * </ul>
 *
 * <p>Now if the service caller performs a call, it will first randomly select
 * one of <em>Main1</em>, <em>Main2</em> and <em>Main3</em>. If the call
 * fails and fail-over is considered allowable, it will retry the call with
 * one of the other back-ends in the <em>MainSite</em> group. If none of the
 * back-ends in the <em>MainSite</em> group succeeds, it will randomly select
 * back-ends from the <em>BackupSite</em> group until the call has succeeded
 * or until all back-ends were tried.
 *
 * <a name="section-callconfig"></a>
 * <h2>Call configuration</h2>
 *
 * <p>Some aspects of a call can be configured using a {@link CallConfig}
 * object. For example, the <code>CallConfig</code> base class indicates
 * whether fail-over is unconditionally allowed. Like this, some aspects of
 * the behaviour of the caller can be tweaked.
 *
 * <p>There are different places where a <code>CallConfig</code> can be
 * applied:
 *
 * <ul>
 *    <li>associated with a <code>Caller</code>;
 *    <li>associated with a <code>CallRequest</code>;
 *    <li>passed with the call method.
 * </ul>
 *
 * <p>First of all, each <code>Caller</code> instance will have a
 * fall-back <code>CallConfig</code>.
 *
 * <p>Secondly, a {@link CallRequest} instance may have a
 * <code>CallConfig</code> associated with it as well. If it does, then this
 * overrides the one on the <code>Caller</code> instance.
 *
 * <p>Finally, a <code>CallConfig</code> can be passed as an argument to the
 * call method. If it is, then this overrides any other settings.
 *
 * <a name="section-implementations"></a>
 * <h2>Subclass implementations</h2>
 *
 * <p>This class is abstract and is intended to be have service-specific
 * subclasses, e.g. for HTTP, FTP, JDBC, etc.
 *
 * <p>A subclass should adhere to the following rules:
 *
 * <ol>
 *    <li>There should be a constructor that accepts no parameters.
 *        This constructor should call
 *        <code>super(({@link Descriptor}) null,
 *                    ({@link CallConfig}) null)</code>.
 *        This descriptor should document no exceptions.
 *    <li>There should be a constructor that accepts only a {@link Descriptor}
 *        object. This constructor should call
 *        <code>super(descriptor, ({@link CallConfig}) null)</code>.
 *        This descriptor should document the same exceptions as the
 *        {@link #Caller(Descriptor,CallConfig)} constructor.
 *    <li>There should be a constructor that accepts both a
 *        {@link Descriptor} and a service-specific call config object
 *        (derived from {@link CallConfig}).  This constructor should call
 *        <code>super(descriptor, callConfig)</code>.
 *        This descriptor should document the same exceptions as the
 *        {@link #Caller(Descriptor,CallConfig)} constructor.
 *    <li>The method {@link #isProtocolSupportedImpl(String)} must be
 *        implemented.
 *    <li>There should be a <code>call</code> method that accepts only a
 *        service-specific request object (derived from {@link CallRequest}).
 *        It should call
 *        {@link #doCall(CallRequest,CallConfig) doCall}<code>(request, null)</code>.
 *    <li>There should be a <code>call</code> method that accepts both a
 *        service-specific request object (derived from {@link CallRequest}).
 *        and a service-specific call config object (derived from
 *        {@link CallConfig}). It should call
 *        {@link #doCall(CallRequest,CallConfig) doCall}<code>(request, callConfig)</code>.
 *    <li>The method
 *        {@link #doCallImpl(CallRequest,CallConfig,TargetDescriptor)}
 *        must be implemented as specified.
 *    <li>The method
 *        {@link #createCallResult(CallRequest,TargetDescriptor,long,CallExceptionList,Object) createCallResult}
 *        must be implemented as specified.
 *    <li>To control when fail-over is applied, the method
 *        {@link #shouldFailOver(CallRequest,CallConfig,CallExceptionList)}
 *        may also be implemented. The implementation can assume that
 *        the passed {@link CallRequest} object is an instance of the
 *        service-specific call request class and that the passed
 *        {@link CallConfig} object is an instance of the service-specific
 *        call config class.
 * </ol>
 *
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public abstract class Caller {

   /**
    * The descriptor for this service. Can be <code>null</code>.
    */
   private Descriptor _descriptor;

   /**
    * The fall-back call config object for this service caller.
    * Cannot be <code>null</code>.
    */
   private CallConfig _callConfig;

   /**
    * Constructs a new <code>Caller</code> with the specified
    * <code>CallConfig</code>.
    *
    * <p>The descriptor is not mandatory. However, no calls can be made with
    * this service caller until the descriptor is set.
    *
    * @param descriptor
    *    the descriptor of the service, or <code>null</code>.
    *
    * @param callConfig
    *    the {@link CallConfig} object, or <code>null</code> if a default
    *    call config (see {@link #getDefaultCallConfig()}) should be used.
    *
    * @throws UnsupportedProtocolException
    *    if <code>descriptor</code> is or contains a {@link TargetDescriptor}
    *    with an unsupported protocol.
    */
   protected Caller(Descriptor descriptor, CallConfig callConfig)
   throws UnsupportedProtocolException {

      // Store information
      setDescriptor(descriptor);

      // If no CallConfig is specified, then use a default one
      if (callConfig == null) {

         // Call getDefaultCallConfig() to get the default config...
         try {
            callConfig = getDefaultCallConfig();

         // ...it should not throw any exception...
         } catch (Throwable cause) {
            String message = null;
            throw Library.getContext().programmingError(Caller.class.getName(), "<init>(Descriptor,CallConfig)", getClass().getName(), "getDefaultConfig()", message, cause);
         }

         // ...and it should never return null.
         if (callConfig == null) {
            throw Library.getContext().programmingError("Method returned null, although that is disallowed by the Caller.getDefaultCallConfig() contract.");
         }
      }

      // Set call configuration
      _callConfig = callConfig;
   }

   /**
    * Asserts that the specified target descriptor is considered acceptable
    * for this service caller. If not, an exception is thrown.
    *
    * @param target
    *    the {@link TargetDescriptor} to test, should not be
    *    <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>target == null</code>.
    *
    * @throws UnsupportedProtocolException
    *    if the protocol in the target descriptor is unsupported.
    */
   public final void testTargetDescriptor(TargetDescriptor target)
   throws IllegalArgumentException, UnsupportedProtocolException {

      // Check preconditions
      MandatoryArgumentChecker.check("target", target);

      try {
         if (! isProtocolSupported(target.getProtocol())) {
            throw new UnsupportedProtocolException(target);
         }
      } catch (UnsupportedOperationException exception) {
         // ignore
      }
   }

   /**
    * Checks if the specified protocol is supported (wrapper method). The
    * protocol is the part in a URL before the string <code>"://"</code>).
    *
    * <p>For example:
    *
    * <ul>
    *    <li>in the URL <code>"http://www.google.nl"</code>, the protocol is
    *        <code>"http"</code>;
    *
    *    <li>in the URL <code>"jdbc:mysql://we.are.the.b.org/mydb/"</code>,
    *        the protocol is <code>"jdbc:mysql"</code>.
    * </ul>
    *
    * <p>This method first checks the argument. If it is <code>null</code>,
    * then an exception is thrown. Otherwise, the result of a call to
    * {@link #isProtocolSupportedImpl(String)} is returned, passing the
    * supplied protocol, but in lowercase. This method may then throw an
    * {@link UnsupportedOperationException} if it is not implemented (default
    * behavior).
    *
    * @param protocol
    *    the protocol, should not be <code>null</code>.
    *
    * @return
    *    <code>true</code> if the specified protocol is supported, or
    *    <code>false</code> if it is not.
    *
    * @throws IllegalArgumentException
    *    if <code>protocol == null</code>.
    */
   public final boolean isProtocolSupported(String protocol)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("protocol", protocol);

      return isProtocolSupportedImpl(protocol.toLowerCase());
   }

   /**
    * Checks if the specified protocol is supported (implementation method).
    * The protocol is the part in a URL before the string <code>"://"</code>).
    *
    * <p>This method should only ever be called from the
    * {@link #isProtocolSupported(String)} method.
    *
    * <p>The implementation of this method in class <code>Caller</code>
    * throws an {@link UnsupportedOperationException}.
    *
    * @param protocol
    *    the protocol, guaranteed not to be <code>null</code> and guaranteed
    *    to be in lower case.
    *
    * @return
    *    <code>true</code> if the specified protocol is supported, or
    *    <code>false</code> if it is not.
    */
   protected abstract boolean isProtocolSupportedImpl(String protocol);

   /**
    * Sets the descriptor.
    *
    * @param descriptor
    *    the descriptor for this service, or <code>null</code>.
    *
    * @throws UnsupportedProtocolException
    *    if <code>descriptor</code> is or contains a {@link TargetDescriptor}
    *    with an unsupported protocol.
    */
   public void setDescriptor(Descriptor descriptor)
   throws UnsupportedProtocolException {

      // Test the protocol for all TargetDescriptors
      if (descriptor != null) {
         for (TargetDescriptor target : descriptor.targets()) {
            testTargetDescriptor(target);
         }
      }

      // Store it
      _descriptor = descriptor;
   }

   /**
    * Returns the descriptor. If the descriptor is currently unset, then
    * <code>null</code> is returned.
    *
    * @return
    *    the descriptor for this service, or <code>null</code> if it is
    *    currently unset.
    */
   public final Descriptor getDescriptor() {
      return _descriptor;
   }

   /**
    * Sets the <code>CallConfig</code> associated with this service caller.
    *
    * @param config
    *    the new {@link CallConfig} object to be associated with this service
    *    caller, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>config == null</code>.
    */
   protected final void setCallConfig(CallConfig config)
   throws IllegalArgumentException {

      // Check argument
      MandatoryArgumentChecker.check("config", config);

      _callConfig = config;
   }

   /**
    * Returns the <code>CallConfig</code> associated with this service caller.
    *
    * @return
    *    the {@link CallConfig} object associated with this service caller,
    *    never <code>null</code>.
    */
   public final CallConfig getCallConfig() {
      return _callConfig;
   }

   /**
    * Returns a default <code>CallConfig</code> object. This method is called
    * by the <code>Caller</code> constructor if no
    * <code>CallConfig</code> object was given.
    *
    * <p>This method should never be called by subclasses.
    *
    * @return
    *    a new appropriate {@link CallConfig} instance,
    *    never <code>null</code>.
    */
   protected abstract CallConfig getDefaultCallConfig();

   /**
    * Attempts to execute the specified call request on one of the target
    * services, with the specified call configuration. During the execution,
    * {@link TargetDescriptor Target descriptors} will be picked and passed to
    * {@link #doCallImpl(CallRequest,CallConfig,TargetDescriptor)} until there
    * is one that succeeds, as long as fail-over can be done (according to
    * {@link #shouldFailOver(CallRequest,CallConfig,CallExceptionList)}).
    *
    * <p>If one of the calls succeeds, then the result is returned. If
    * none succeeds or if fail-over should not be done, then a
    * {@link CallException} is thrown.
    *
    * <p>Subclasses that want to use this method <em>must</em> implement
    * {@link #doCallImpl(CallRequest,CallConfig,TargetDescriptor)}. That
    * method is called for each call attempt to a specific service target
    * (represented by a {@link TargetDescriptor}).
    *
    * @param request
    *    the call request, not <code>null</code>.
    *
    * @param callConfig
    *    the call configuration, or <code>null</code> if the one defined for
    *    the call request should be used if specified, or otherwise the
    *    fall-back call configuration associated with this
    *    <code>Caller</code> (see {@link #getCallConfig()}).
    *
    * @return
    *    a combination of the call result and a link to the
    *    {@link TargetDescriptor target} that returned this result, if and
    *    only if one of the calls succeeded, could be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>request == null</code>.
    *
    * @throws IllegalStateException
    *    if the descriptor is currently unset.
    *
    * @throws CallException
    *    if all call attempts failed.
    */
   protected final CallResult doCall(CallRequest request,
                                     CallConfig  callConfig)
   throws IllegalArgumentException,
          IllegalStateException,
          CallException {

      // Check preconditions
      MandatoryArgumentChecker.check("request", request);

      LibraryContext context = Library.getContext();

      // Determine descriptor
      Descriptor descriptor = _descriptor;
      if (descriptor == null) {
         throw new IllegalStateException("Descriptor is currently unset.");
      }

      // Determine what config to use. The argument has priority, then the one
      // associated with the request and the fall-back is the one associated
      // with this service caller.
      if (callConfig == null) {
         callConfig = request.getCallConfig();
         if (callConfig == null) {
            callConfig = _callConfig;
         }
      }

      // Keep a reference to the most recent CallException since
      // setNext(CallException) needs to be called on it to make it link to
      // the next one (if there is one)
      CallException lastException = null;

      // Maintain the list of CallExceptions
      //
      // This is needed if a successful result (a CallResult object) is
      // returned, since it will contain references to the exceptions as well;
      //
      // Note that this object is lazily initialized because this code is
      // performance- and memory-optimized for the successful case
      CallExceptionList exceptions = null;

      // Iterate over all targets
      Iterator<TargetDescriptor> iterator = descriptor.targets().iterator();

      // There should be at least one target
      if (! iterator.hasNext()) {
         Throwable cause = null;
         throw context.programmingError(Caller.class.getName(), "doCall(CallRequest,CallConfig)", descriptor.getClass().getName(), "iterator()", "Descriptor (" + descriptor + ") returns no target descriptors.", cause);
      }

      // Loop over all TargetDescriptors
      boolean doContinue = true;
      while (doContinue) {

         // Get a reference to the next TargetDescriptor
         TargetDescriptor target = iterator.next();
         String              url = target.getURL();

         // Call using this target
         Object     result = null;
         boolean succeeded = false;
         long        start = System.currentTimeMillis();

         context.beforeTargetCall(url);
         try {

            long duration = System.currentTimeMillis() - start;

            // Attempt the call
            result = doCallImpl(request, callConfig, target);
            context.targetCallSucceeded(url, duration);
            succeeded = true;

         // If the call to the target fails,
         // then store the exception and try the next TargetDescriptor
         } catch (Throwable exception) {

            long duration = System.currentTimeMillis() - start;

            context.targetCallFailed(url, duration, exception);

            // If the caught exception is not a CallException, then
            // encapsulate it in one
            CallException currentException;
            if (exception instanceof CallException) {
               currentException = (CallException) exception;
            } else {
               currentException = new UnexpectedExceptionCallException(request, target, duration, null, exception);
            }

            // Link the previous exception (if there is one) to this one
            if (lastException != null) {
               lastException.setNext(currentException);
            }

            // Now set this exception as the most recent CallException
            lastException = currentException;

            // If this is the first exception being caught, then lazily
            // initialize the CallExceptionList and keep a reference to the
            // first exception
            if (exceptions == null) {
               exceptions = new CallExceptionList();
            }

            // Store the failure
            exceptions.add(currentException);

            // Determine whether fail-over is allowed
            // and whether we have another target to fail-over to
            boolean failOver = shouldFailOver(request, callConfig, exceptions);
            boolean haveNext = iterator.hasNext();

            // Continue processing targets if both are true
            doContinue = haveNext && failOver;

            context.failoverDecided(failOver, haveNext, doContinue);
         }

         // The call succeeded
         if (succeeded) {
            long duration = System.currentTimeMillis() - start;

            return createCallResult(request, target, duration, exceptions, result);
         }
      }

      // Loop ended, call failed completely
      context.callCompletelyFailed(exceptions);

      // Throw the first exception from the list
      throw exceptions.get(0);
   }

   /**
    * Calls the specified target using the specified subject. This method must
    * be implemented by subclasses. It is called as soon as a target is
    * selected to be called. If the call fails, then a {@link CallException}
    * should be thrown. If the call succeeds, then the call result should be
    * returned from this method.
    *
    * <p>Subclasses that want to use {@link #doCall(CallRequest,CallConfig)}
    * <em>must</em> implement this method.
    *
    * @param request
    *    the call request to be executed, never <code>null</code>.
    *
    * @param callConfig
    *    the call config to be used, never <code>null</code>; this is
    *    determined by {@link #doCall(CallRequest,CallConfig)} and is
    *    guaranteed not to be <code>null</code>.
    *
    * @param target
    *    the target to call, cannot be <code>null</code>.
    *
    * @return
    *    the result, if and only if the call succeeded, could be
    *    <code>null</code>.
    *
    * @throws ClassCastException
    *    if the specified <code>request</code> object is not <code>null</code>
    *    and not an instance of an expected subclass of class
    *    {@link CallRequest}.
    *
    * @throws IllegalArgumentException
    *    if <code>target == null || request == null</code>.
    *
    * @throws CallException
    *    if the call to the specified target failed.
    */
   public abstract Object doCallImpl(CallRequest      request,
                                     CallConfig       callConfig,
                                     TargetDescriptor target)
   throws ClassCastException, IllegalArgumentException, CallException;

   /**
    * Constructs an appropriate <code>CallResult</code> object for a
    * successful call attempt. This method is called from
    * {@link #doCall(CallRequest,CallConfig)}.
    *
    * @param request
    *    the {@link CallRequest} that was to be executed, never
    *    <code>null</code> when called from {@link #doCall(CallRequest,CallConfig)}.
    *
    * @param succeededTarget
    *    the {@link TargetDescriptor} for the service that was successfully
    *    called, never <code>null</code> when called from
    *    {@link #doCall(CallRequest,CallConfig)}.
    *
    * @param duration
    *    the call duration in milliseconds, guaranteed to be a non-negative
    *    number when called from {@link #doCall(CallRequest,CallConfig)}.
    *
    * @param exceptions
    *    the list of {@link CallException} instances, or <code>null</code> if
    *    there were no call failures.
    *
    * @param result
    *    the result from the call, which is the object returned by
    *    {@link #doCallImpl(CallRequest,CallConfig,TargetDescriptor)}, can be
    *    <code>null</code>.
    *
    * @return
    *    a {@link CallResult} instance, never <code>null</code>.
    *
    * @throws ClassCastException
    *    if <code>request</code> and/or <code>result</code> are not of the
    *    correct class.
    */
   protected abstract CallResult createCallResult(CallRequest       request,
                                                  TargetDescriptor  succeededTarget,
                                                  long              duration,
                                                  CallExceptionList exceptions,
                                                  Object            result)
   throws ClassCastException;

   /**
    * Utility function that runs a specified task, throwing an exception if it
    * does not complete within a time-out period.
    *
    * <p>If the specified descriptor defines a total time-out, then the task
    * <code>task</code> is executed on a separate thread. If the task does
    * then not finish within the total time-out period, then the thread
    * executing it is interrupted using the {@link Thread#interrupt()}
    * method and a {@link TimeOutException} is thrown.
    *
    * <p>If the specified descriptor does not define a total time-out, then
    * the task <code>task</code> is executed on the current thread.
    *
    * @param task
    *    the task to run, cannot be <code>null</code>.
    *
    * @param descriptor
    *    the descriptor for the target on which the task is executed, cannot
    *    be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>task == null || descriptor == null</code>.
    *
    * @throws IllegalThreadStateException
    *    if <code>descriptor.getTotalTimeOut() &gt; 0</code> and the task is a
    *    {@link Thread} which is already started.
    *
    * @throws SecurityException
    *    if the task did not finish within the total time-out period, but the
    *    interruption of the thread was disallowed (see
    *    {@link Thread#interrupt()}).
    *
    * @throws TimeOutException
    *    if the task did not finish within the total time-out period and was
    *    interrupted.
    */
   protected final void controlTimeOut(Runnable         task,
                                       TargetDescriptor descriptor)
   throws IllegalArgumentException,
          IllegalThreadStateException,
          SecurityException,
          TimeOutException {

      // Check preconditions
      MandatoryArgumentChecker.check("task",       task,
                                     "descriptor", descriptor);

      // Determine the total time-out
      int totalTimeOut = descriptor.getTotalTimeOut();

      // If there is no total time-out, then execute the task on this thread
      if (totalTimeOut < 1) {
         task.run();

      // Otherwise a time-out controller will be used
      } else {
         TimeOutController.execute(task, totalTimeOut);
      }
   }

   /**
    * Determines whether a call should fail-over to the next selected target
    * based on a request, call configuration and exception list.
    * This method should only be called from
    * {@link #doCall(CallRequest,CallConfig)}.
    *
    * <p>This method is typically overridden by subclasses. Usually, a
    * subclass first calls this method in the superclass, and if that returns
    * <code>false</code> it does some additional checks, otherwise
    * <code>true</code> is immediately returned.
    *
    * <p>The implementation of this method in class {@link Caller}
    * returns <code>true</code> if and only if at least one of the following
    * conditions is true:
    *
    * <ul>
    *    <li><code>callConfig.{@link CallConfig#isFailOverAllowed() isFailOverAllowed()}</code>
    *    <li><code>exception instanceof {@link ConnectionCallException}</code>
    * </ul>
    *
    * @param request
    *    the request for the call, as passed to
    *    {@link #doCall(CallRequest,CallConfig)},
    *    cannot be <code>null</code>.
    *
    * @param callConfig
    *    the call config that is currently in use,
    *    cannot be <code>null</code>.
    *
    * @param exceptions
    *    the current list of {@link CallException}s; never
    *    <code>null</code> and never empty;
    *    get the most recent one by calling
    *    <code>exceptions.</code>{@link CallExceptionList#last() last()}.
    *
    * @return
    *    <code>true</code> if the call should fail-over to the next target, or
    *    <code>false</code> if it should not.
    */
   protected boolean shouldFailOver(CallRequest       request,
                                    CallConfig        callConfig,
                                    CallExceptionList exceptions) {

      // NOTE: The IllegalArgumentException is not documented, because this
      //       method may be overloaded by subclasses that do not expose this
      //       behaviour.

      // Check preconditions
      MandatoryArgumentChecker.check("request",    request,
                                     "callConfig", callConfig,
                                     "exceptions", exceptions);
      if (exceptions.size() < 1) {
         throw new IllegalThreadStateException("exceptions.size() == " + exceptions.size());
      }

      // Determine if fail-over is applicable
      return callConfig.isFailOverAllowed() || (! exceptions.last().isCallPossiblyProcessed());
   }
}
