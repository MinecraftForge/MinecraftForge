/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Classes annotated with this will have the named interface or method removed from the runtime definition of the class
 * if the modid specified is missing.
 *
 * @author cpw
 *
 */
public final class Optional {
    /**
     * Not constructable
     */
    private Optional() {}

    /**
     * Mark a list of interfaces as removable
     * @author cpw
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface InterfaceList {
        /**
         * Mark a list of interfaces for optional removal.
         * @return
         */
        Interface[] value();
    }
    /**
     * Used to remove optional interfaces
     * @author cpw
     *
     */
    @Repeatable(InterfaceList.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Interface {
        /**
         * The fully qualified name of the interface to be stripped
         * @return the interface name
         */
        String iface();

        /**
         * The modid that is required to be present for stripping NOT to occur
         * @return the modid
         */
        String modid();

        /**
         * Strip references to this interface in method declarations? (Useful to kill synthetic methods from scala f.e.)
         *
         * @return if references should be stripped
         */
        boolean striprefs() default false;
    }
    /**
     * Used to remove optional methods
     * @author cpw
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Method {
        /**
         * The modid that is required to be present for stripping NOT to occur
         * @return the modid
         */
        String modid();
    }
}
