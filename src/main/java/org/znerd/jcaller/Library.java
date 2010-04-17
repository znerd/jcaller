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
