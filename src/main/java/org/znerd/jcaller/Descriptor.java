// See the COPYRIGHT file for redistribution and use restrictions.
package org.znerd.jcaller;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

/**
 * Descriptor for a service or group of services.
 *
 * <p>Once constructed, a <code>Descriptor</code> instance is unmodifiable, it
 * will never change anymore.
 *
 * <p>To iterate over the contained {@link TargetDescriptor} instances,
 * use the following approach:
 *
 * <blockquote id="examplecode"><pre>for ({@linkplain TargetDescriptor} target : descriptor.{@linkplain #targets()}) {
   // do something with 'target'
}</pre></blockquote>
 *
 * @author <a href="mailto:ernst@ernstdehaan.com">Ernst de Haan</a>
 */
public abstract class Descriptor
implements Serializable {

   /**
    * Constructs a new <code>Descriptor</code>.
    */
   Descriptor() {
      // empty
   }

   /**
    * Checks if this descriptor denotes a group of descriptor of descriptorss.
    *
    * @return
    *    <code>true</code> if this descriptor denotes a group,
    *    <code>false</code> otherwise.
    */
   public abstract boolean isGroup();

   /**
    * Returns all leaves, the target descriptors.
    *
    * @return
    *    all the leaves, the {@link TargetDescriptor} instances,
    *    never <code>null</code>.
    */
   public abstract Collection<TargetDescriptor> targets();

   /**
    * Counts the total number of target descriptors in/under this descriptor.
    *
    * @return
    *    the total number of target descriptors, always &gt;= 1.
    */
   public abstract int getTargetCount();

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
   public abstract TargetDescriptor getTargetByCRC(int crc);
}
