// See the COPYRIGHT file for redistribution and use restrictions.
package org.znerd.jcaller;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.zip.CRC32;

import static org.znerd.jcaller.Library.isEmpty;
import static org.znerd.jcaller.Library.toHexString;

/**
 * Descriptor for a single target service. A target descriptor defines a URL
 * that identifies the location of the service. Also, it may define 3 kinds of
 * time-outs:
 *
 * <dl>
 *    <dt><em>total time-out</em> ({@link #getTotalTimeOut()})</dt>
 *    <dd>the maximum duration of a call, including connection time, time used
 *    to send the request, time used to receive the response, etc.</dd>
 *
 *    <dt><em>connection time-out</em> ({@link #getConnectionTimeOut()})</dt>
 *    <dd>the maximum time for attempting to establish a connection.</dd>
 *
 *    <dt><em>socket time-out</em> ({@link #getSocketTimeOut()})</dt>
 *    <dd>the maximum time for attempting to receive data on a socket.</dd>
 * </dl>
 *
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public final class TargetDescriptor extends Descriptor {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Serial version UID. Used for serialization.
    */
   private static final long serialVersionUID = -2945764325774744627L;

   /**
    * The number of instances of this class. Initially 0.
    */
   private static int INSTANCE_COUNT;

   /**
    * The default time-out when no time-out is specified.
    */
   private static final int DEFAULT_TIMEOUT = 5000;

   /**
    * Collection of protocols that are known to require a hostname in URLs.
    */
   private static final Set<String> PROTOCOLS_REQUIRING_HOST = new HashSet<String>(Arrays.asList(new String[] { "http", "https", "ftp", "sftp", "smtp", "smtps", "gopher" }));

   /**
    * Computes the CRC-32 checksum for the specified character string.
    *
    * @param s
    *    the string for which to compute the checksum, not <code>null</code>.
    *
    * @return
    *    the checksum for <code>s</code>.
    */
   private static int computeCRC32(String s) {

      // Compute the CRC-32 checksum
      CRC32 checksum = new CRC32();
      byte[] bytes;
      final String ENCODING = "US-ASCII";
      try {
         bytes = s.getBytes(ENCODING);

      // Unsupported encoding
      } catch (UnsupportedEncodingException exception) {
         String message = "Unsupporting encoding \"" + ENCODING + "\".";
         throw Library.getContext().programmingError(TargetDescriptor.class.getName(), "computeCRC32(String)", "java.lang.String", "getBytes(java.lang.String)", message, exception);
      }

      checksum.update(bytes, 0, bytes.length);
      return (int) (checksum.getValue() & 0x00000000ffffffffL);
   }

   /**
    * Tests the specified URL string and throws an exception if it is 
    * malformed.
    *
    * @param url
    *    the string to test to see if it is a valid URL,
    *    cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>url == null</code>.
    *
    * @throws MalformedURLException
    *    if <code>url</code> is not a valid URL according to the official 
    *    format of a URL
    *    (<a href="http://www.faqs.org/rfcs/rfc1738.html">RFC 1738</a>).
    */
   private static void testURL(String url)
   throws IllegalArgumentException, MalformedURLException {

      // TODO: Add more detail to the exception message

      // Check preconditions
      MandatoryArgumentChecker.check("url", url);

      // The approach in this method is: the URL is considered valid until 
      // proven otherwise
      boolean error = false;

      // Construct a URI object (which was introduced in Java 1.4).
      // An important difference with the java.net.URL class is that the 
      // latter only supports protocols backed by a URLStreamHandler.
      URI uri;
      try {
         uri = new URI(url);

         // The URI/URL must be absolute
         error = ! uri.isAbsolute();

         if (! error) {
            String protocol = uri.getScheme();
            String host     = uri.getHost();

            // Some protocols are known to require a host name. Check this.
            if (PROTOCOLS_REQUIRING_HOST.contains(protocol) && isEmpty(host)) {
               error = true;
            }
         }

      // The URI constructor may throw a URISyntaxException, but we catch 
      // other exceptions and errors just the same
      } catch (Throwable e) {
         error = true;
      }

      // If an error was detected, then perhaps the URL contains a 
      // subprotocol. Test without the protocol part to see if that validates. 
      // If it does, then the URL is still considered valid.
      if (error) {
         int i = url.indexOf(':');
         if (i > 0) {
            try {
               testURL(url.substring(i + 1));
               error = false;
            } catch (MalformedURLException e) {
               // empty
            }
         }
      }

      // If there was still an error at this point, then the URL is definitely 
      // considered malformed.
      if (error) {
         throw new MalformedURLException(url);
      }
   }

   /**
    * The 1-based sequence number of this instance. Since this number is
    * 1-based, the first instance of this class will have instance number 1
    * assigned to it.
    */
   private final int _instanceNumber;

   /**
    * A textual representation of this object. Lazily initialized by
    * {@link #toString()} before returning it.
    */
   private String _asString;

   /**
    * The URL for the service. Cannot be <code>null</code>.
    */
   private final String _url;

   /**
    * The total time-out for the service. Is set to a 0 if no total time-out
    * should be applied.
    */
   private final int _timeOut;

   /**
    * The connection time-out for the service. Always greater than 0 and
    * smaller than or equal to the total time-out.
    */
   private final int _connectionTimeOut;

   /**
    * The socket time-out for the service. Always greater than 0 and smaller
    * than or equal to the total time-out.
    */
   private final int _socketTimeOut;

   /**
    * The CRC-32 checksum for the URL.
    */
   private final int _crc;


   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>TargetDescriptor</code> for the specified URL.
    *
    * <p>Note: Both the connection time-out and the socket time-out will be
    * set to the default time-out: 5 seconds.
    *
    * @param url
    *    the URL of the service, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>url == null</code>.
    *
    * @throws MalformedURLException
    *    if the specified URL is malformed.
    */
   public TargetDescriptor(String url)
   throws IllegalArgumentException, MalformedURLException {
      this(url, DEFAULT_TIMEOUT, DEFAULT_TIMEOUT, DEFAULT_TIMEOUT);
   }

   /**
    * Constructs a new <code>TargetDescriptor</code> for the specified URL,
    * with the specifed total time-out.
    *
    * <p>Note: Both the connection time-out and the socket time-out will be
    * set to equal the total time-out.
    *
    * @param url
    *    the URL of the service, cannot be <code>null</code>.
    *
    * @param timeOut
    *    the total time-out for the service, in milliseconds; or a
    *    non-positive value for no total time-out.
    *
    * @throws IllegalArgumentException
    *    if <code>url == null</code>.
    *
    * @throws MalformedURLException
    *    if the specified URL is malformed.
    */
   public TargetDescriptor(String url, int timeOut)
   throws IllegalArgumentException, MalformedURLException {
      this(url, timeOut, timeOut, timeOut);
   }

   /**
    * Constructs a new <code>TargetDescriptor</code> for the specified URL,
    * with the specifed total time-out and connection time-out.
    *
    * <p>Note: If the passed connection time-out is smaller than 1 ms, or
    * greater than the total time-out, then it will be adjusted to equal the
    * total time-out.
    *
    * <p>Note: The socket time-out will be set to equal the total time-out.
    *
    * @param url
    *    the URL of the service, cannot be <code>null</code>.
    *
    * @param timeOut
    *    the total time-out for the service, in milliseconds; or a
    *    non-positive value for no total time-out.
    *
    * @param connectionTimeOut
    *    the connection time-out for the service, in milliseconds; or a
    *    non-positive value if the connection time-out should equal the total
    *    time-out.
    *
    * @throws IllegalArgumentException
    *    if <code>url == null</code>.
    *
    * @throws MalformedURLException
    *    if the specified URL is malformed.
    */
   public TargetDescriptor(String url, int timeOut, int connectionTimeOut)
   throws IllegalArgumentException, MalformedURLException {
      this(url, timeOut, connectionTimeOut, timeOut);
   }

   /**
    * Constructs a new <code>TargetDescriptor</code> for the specified URL,
    * with the specifed total time-out, connection time-out and socket
    * time-out.
    *
    * <p>Note: If the passed connection time-out is smaller than 1 ms, or
    * greater than the total time-out, then it will be adjusted to equal the
    * total time-out.
    *
    * <p>Note: If the passed socket time-out is smaller than 1 ms or greater
    * than the total time-out, then it will be adjusted to equal the total
    * time-out.
    *
    * @param url
    *    the URL of the service, cannot be <code>null</code>.
    *
    * @param timeOut
    *    the total time-out for the service, in milliseconds; or a
    *    non-positive value for no total time-out.
    *
    * @param connectionTimeOut
    *    the connection time-out for the service, in milliseconds; or a
    *    non-positive value if the connection time-out should equal the total
    *    time-out.
    *
    * @param socketTimeOut
    *    the socket time-out for the service, in milliseconds; or a
    *    non-positive value for no socket time-out.
    *
    * @throws IllegalArgumentException
    *    if <code>url == null</code>.
    *
    * @throws MalformedURLException
    *    if the specified URL is malformed.
    */
   public TargetDescriptor(String url,
                           int    timeOut,
                           int    connectionTimeOut,
                           int    socketTimeOut)
   throws IllegalArgumentException, MalformedURLException {

      // Determine instance number first
      _instanceNumber = ++INSTANCE_COUNT;

      // Check preconditions
      MandatoryArgumentChecker.check("url", url);
      testURL(url);

      // Convert negative total time-out to 0
      timeOut = (timeOut > 0) ? timeOut : 0;

      // If connection time-out or socket time-out is not set, then set it to
      // the total time-out
      connectionTimeOut = (connectionTimeOut > 0) ? connectionTimeOut : timeOut;
      socketTimeOut     = (socketTimeOut     > 0) ? socketTimeOut     : timeOut;

      // If either connection or socket time-out is greater than total
      // time-out, then limit it to the total time-out
      connectionTimeOut = (connectionTimeOut < timeOut) ? connectionTimeOut : timeOut;
      socketTimeOut     = (socketTimeOut     < timeOut) ? socketTimeOut     : timeOut;

      // Set fields
      _url               = url;
      _timeOut           = timeOut;
      _connectionTimeOut = connectionTimeOut;
      _socketTimeOut     = socketTimeOut;
      _crc               = computeCRC32(url);

      // NOTE: _asString is lazily initialized
   }

   /**
    * Checks if this descriptor denotes a group of descriptors.
    *
    * @return
    *    <code>false</code>, since this descriptor does not denote a group.
    */
   @Override
   public boolean isGroup() {
      return false;
   }

   /**
    * Returns the URL for the service.
    *
    * @return
    *    the URL for the service, not <code>null</code>.
    */
   public String getURL() {
      return _url;
   }

   /**
    * Returns the protocol in the URL for the service.
    *
    * @return
    *    the protocol in the URL, not <code>null</code>.
    */
   public String getProtocol() {
      int index = _url.indexOf("://");
      return _url.substring(0, index);
   }

   /**
    * Returns the total time-out for a call to the service. The value 0
    * is returned if there is no total time-out.
    *
    * @return
    *    the total time-out for the service, as a positive number, in
    *    milli-seconds, or 0 if there is no total time-out.
    */
   public int getTotalTimeOut() {
      return _timeOut;
   }

   /**
    * Returns the connection time-out for a call to the service.
    *
    * @return
    *    the connection time-out for the service; always greater than 0 and
    *    smaller than or equal to the total time-out.
    */
   public int getConnectionTimeOut() {
      return _connectionTimeOut;
   }

   /**
    * Returns the socket time-out for a call to the service.
    *
    * @return
    *    the socket time-out for the service; always greater than 0 and
    *    smaller than or equal to the total time-out.
    */
   public int getSocketTimeOut() {
      return _socketTimeOut;
   }

   /**
    * Returns the CRC-32 checksum for the URL of this target descriptor.
    *
    * @return
    *    the CRC-32 checksum.
    */
   public int getCRC() {
      return _crc;
   }

   @Override
   public Collection<TargetDescriptor> targets() {
      return Collections.singleton(this);
   }

   /**
    * Counts the total number of target descriptors in/under this descriptor.
    *
    * @return
    *    the total number of target descriptors, always 1.
    */
   public int getTargetCount() {
      return 1;
   }

   /**
    * Returns the <code>TargetDescriptor</code> that matches the specified
    * CRC-32 checksum.
    *
    * @param crc
    *    the CRC-32 checksum.
    *
    * @return
    *    the {@link TargetDescriptor} that matches the specified checksum, or
    *    <code>null</code>, if none could be found in this descriptor.
    */
   public TargetDescriptor getTargetByCRC(int crc) {
      return (_crc == crc) ? this : null;
   }

   /**
    * Returns a hash code value for the object.
    *
    * @return
    *    a hash code value for this object.
    *
    * @see Object#hashCode()
    * @see #equals(Object)
    */
   public int hashCode() {
       return _crc;
   }

   /**
    * Indicates whether some other object is "equal to" this one. This method
    * considers <code>obj</code> equals if and only if it matches the
    * following conditions:
    *
    * <ul>
    *    <li><code>obj instanceof TargetDescriptor</code>
    *    <li>URL is equal
    *    <li>total time-out is equal
    *    <li>connection time-out is equal
    *    <li>socket time-out is equal
    * </ul>
    *
    * @param obj
    *    the reference object with which to compare.
    *
    * @return
    *    <code>true</code> if this object is the same as the <code>obj</code>
    *    argument; <code>false</code> otherwise.
    *
    * @see #hashCode()
    */
   public boolean equals(Object obj) {

      boolean equal = false;

      if (obj instanceof TargetDescriptor) {
         TargetDescriptor that = (TargetDescriptor) obj;
         equal = (_url.equals(that._url))
              && (_timeOut           == that._timeOut)
              && (_connectionTimeOut == that._connectionTimeOut)
              && (_socketTimeOut     == that._socketTimeOut);
      }

      return equal;
   }

   /**
    * Textual description of this object. The string includes the URL and all
    * time-out values. For example:
    *
    * <blockquote><code>TargetDescriptor(url="http://api.google.com/some_api/";
    * total-time-out is 5300 ms;
    * connection time-out is 1000 ms;
    * socket time-out is disabled)</code></blockquote>
    *
    * @return
    *    this <code>TargetDescriptor</code> as a {@link String}, never
    *    <code>null</code>.
    */
   public String toString() {

      // Lazily initialize
      if (_asString == null) {
         StringBuffer buffer = new StringBuffer(233);
         buffer.append("TargetDescriptor #");
         buffer.append(_instanceNumber);
         buffer.append(" [url=\"");
         buffer.append(_url);
         buffer.append("\"; crc=\"");
         buffer.append(toHexString(_crc));
         buffer.append("\"; total time-out is ");
         if (_timeOut < 1) {
            buffer.append("disabled; connection time-out is ");
         } else {
            buffer.append(_timeOut);
            buffer.append(" ms; connection time-out is ");
         }
         if (_connectionTimeOut < 1) {
            buffer.append("disabled; socket time-out is ");
         } else {
            buffer.append(_connectionTimeOut);
            buffer.append(" ms; socket time-out is ");
         }
         if (_socketTimeOut < 1) {
            buffer.append("disabled]");
         } else {
            buffer.append(_socketTimeOut);
            buffer.append(" ms]");
         }
         _asString = buffer.toString();
      }

      return _asString;
   }
}
