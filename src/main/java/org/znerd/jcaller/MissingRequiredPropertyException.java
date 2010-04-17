// See the COPYRIGHT file for redistribution and use restrictions.
package org.znerd.jcaller;

/**
 * Exception thrown to indicate a required property has no value set for it.
 *
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 *
 * @see InvalidPropertyValueException
 */
public final class MissingRequiredPropertyException extends PropertyException {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   /**
    * Serial version UID. Used for serialization.
    */
   private static final long serialVersionUID = -300884756567456784L;


   /**
    * Detailed description of why this property is required in the current
    * context. Can be <code>null</code>.
    */
   private final String _detail;

   /**
    * Constructs a new <code>MissingRequiredPropertyException</code>, with the
    * specified detail message.
    *
    * @param propertyName
    *    the name of the required property, not <code>null</code>.
    *
    * @param detail
    *    a more detailed description of why this property is required in this
    *    context, can be <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>propertyName == null</code>.
    */
   public MissingRequiredPropertyException(String propertyName, String detail)
   throws IllegalArgumentException {

      // Construct message and call superclass constructor
      super(propertyName, createMessage(propertyName, detail), null);

      // Store data
      _detail = detail;
   }

   /**
    * Constructs a new <code>MissingRequiredPropertyException</code>.
    *
    * @param propertyName
    *    the name of the required property, not <code>null</code>.
    *
    * @throws IllegalArgumentException
    *    if <code>propertyName == null</code>.
    */
   public MissingRequiredPropertyException(String propertyName)
   throws IllegalArgumentException {
      this(propertyName, null);
   }

   /**
    * Creates message based on the specified constructor argument.
    *
    * @param propertyName
    *    the name of the property, may be <code>null</code>.
    *
    * @param detail
    *    a more detailed description of why this property is required in this
    *    context, can be <code>null</code>.
    *
    * @return
    *    the message, never <code>null</code>.
    */
   private static String createMessage(String propertyName, String detail) {

      // Construct the message
      String message = "No value is set for the required property \"" + propertyName;

      // Append the detail message, if any
      message += (detail == null)
               ? "\"."
               : "\": " + detail;

      return message;
   }

   /**
    * Returns the detail message.
    *
    * @return
    *    the detail message, can be <code>null</code>.
    */
   public String getDetail() {
      return _detail;
   }
}
