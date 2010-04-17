// See the COPYRIGHT file for redistribution and use restrictions.
package org.znerd.jcaller;

import java.util.ArrayList;
import java.util.List;

/**
 * List of call exceptions. See class {@link CallException}.
 *
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public final class CallExceptionList {

   // TODO: Consider removing this class in favour of a Java 5+
   //       generics-based approach, e.g. List<CallException>.

   /**
    * The underlying collection to store the <code>CallException</code>
    * objects in.
    */
   private final List<CallException> _exceptions;

   /**
    * Constructs a new <code>CallExceptionList</code> object.
    */
   public CallExceptionList() {
      _exceptions = new ArrayList<CallException>();
   }

   /**
    * Adds a <code>CallException</code> to this list.
    *
    * @param exception
    *    the {@link CallException} to add, cannot be <code>null</code>.
    */
   void add(CallException exception) {
      // TODO: Check preconditions
      _exceptions.add(exception);
   }

   /**
    * Counts the number of elements.
    *
    * @return
    *    the number of {@link CallException}s, always &gt;= 0.
    */
   public int size() {
      return (_exceptions == null) ? 0 : _exceptions.size();
   }

   /**
    * Retrieves a <code>CallException</code> by index.
    *
    * @param index
    *    the element index, must be &gt;= 0 and &lt; {@link #size()}.
    *
    * @return
    *    the {@link CallException} element at the specified index, never
    *    <code>null</code>.
    *
    * @throws IndexOutOfBoundsException
    *    if <code>index &lt; 0 || index &gt;= {@link #size()}</code>.
    */
   public CallException get(int index)
   throws IndexOutOfBoundsException {
      return _exceptions.get(index);
   }

   /**
    * Retrieves the last (most recent) <code>CallException</code>.
    *
    * @return
    *    the {@link CallException} element at the highest index, or
    *    <code>null</code> if this list is empty.
    */
   public CallException last() {
      if (size() == 0) {
         return null;
      } else {
         return get(size() - 1);
      }
   }
}
