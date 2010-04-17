// See the COPYRIGHT file for copyright and license information
package org.znerd.jcaller;

import java.io.InputStream;
import java.io.IOException;
import java.net.URL;

/**
 * Class that represents the JCaller library.
 *
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public final class Library {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * The version of this library, lazily initialized.
    */
   private static final String VERSION = Library.class.getPackage().getImplementationVersion();

   /**
    * The connection with the world outside this library.
    */
   private static LibraryContext CONTEXT; // TODO: Initial value!
   
   
   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   /**
    * Returns the official human-readable name of this library.
    *
    * @return
    *    the name, for example <code>"JCaller"</code>,
    *    never <code>null</code>.
    */
   public static final String getName() {
      return "JCaller";
   }

   /**
    * Returns the version of this library.
    *
    * @return
    *    the version of this library, for example <code>"3.0"</code>,
    *    or <code>null</code> if unknown.
    */
   public static final String getVersion() {
      return VERSION;
   }
   
   /**
    * Prints the name and version of this library.
    */
   public static final void main(String[] args) {
      System.out.println(getName() + " " + getVersion());
   }

   /**
    * Sets the context for this library.
    *
    * @param context
    *    the {@link LibraryContext} to use, cannot be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>context == null</code>.
    */
   public static final void setContext(LibraryContext context)
   throws IllegalArgumentException {

      // Check preconditions
      MandatoryArgumentChecker.check("context", context);

      synchronized (Library.class) {
         CONTEXT = context;
      }
   }

   /**
    * Retrieves the context for this library.
    *
    * @return
    *    the active {@link LibraryContext}, never <code>null</code>.
    */
   public static final LibraryContext getContext() {
      synchronized (Library.class) {
         return CONTEXT;
      }
   }
   
   /**
    * Determines if the specified string is <code>null</code> or an empty
    * string.
    *
    * @param s
    *    the string, or <code>null</code>.
    *
    * @return
    *    <code>true</code> if <code>s == null || s.length() &lt; 1</code>.
    */
   static boolean isEmpty(String s) {
      return (s == null) || (s.length() == 0);
   }

   /**
    * Converts the specified <code>int</code> to an unsigned number hex
    * string. The returned string will always consist of 8 hex characters,
    * zeroes will be prepended as necessary.
    *
    * @param n
    *    the number to be converted to a hex string.
    *
    * @return
    *    the hex string, cannot be <code>null</code>, the length is always 8
    *    (i.e. <code><em>return</em>.</code>{@link String#length() length()}<code> == 8</code>).
    */
   static String toHexString(int n) {

      final char[]  DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7' , '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
      final int INT_LENGTH = 8;
      final int      RADIX = 16;
      final int   INT_MASK = RADIX - 1;

      char[] chars = new char[INT_LENGTH];
      int      pos = INT_LENGTH - 1;

      // Convert the int to a hex string until the remainder is 0
      for (; n != 0; n >>>= 4) {
         chars[pos--] = DIGITS[n & INT_MASK];
      }

      // Fill the rest with '0' characters
      for (; pos >= 0; pos--) {
         chars[pos] = '0';
      }

      return new String(chars, 0, INT_LENGTH);
   }

   
   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>Library</code> object.
    */
   private Library() {
      // empty
   }
}
