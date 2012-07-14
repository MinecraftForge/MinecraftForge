/*
 * The FML Forge Mod Loader suite.
 * Copyright (C) 2012 cpw
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package cpw.mods.fml.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The new mod style in FML 1.3
 *
 * @author cpw
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Mod
{
    /**
     * The unique mod identifier for this mod
     * @return
     */
    String modid();
    /**
     * A user friendly name for the mod
     * @return
     */
    String name() default "";
    /**
     * A version string for this mod
     * @return
     */
    String version() default "";
    /**
     * A simple dependency string for this mod
     * @return
     */
    String dependsOn() default "";
    /**
     * Whether to use the mcmod.info metadata by default for this mod.
     * If true, settings in the mcmod.info file will override settings in these annotations.
     * @return
     */
    boolean useMetadata() default false;

    /**
     * Populate the annotated field with an instance of the Block as specified
     * @author cpw
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Block {
        /**
         * The block's name
         * @return
         */
        String name();
        /**
         * The class (extending Block) that should be created.
         * @return
         */
        String typeClass();
        /**
         * The associated ItemBlock subtype for the item (can be null for an ItemBlock)
         * @return
         */
        String itemTypeClass() default "";
    }
    /**
     * Populate the annotated field with an Item
     * @author cpw
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Item {
        /**
         * The name of the item
         * @return
         */
        String name();
        /**
         * The type of the item
         * @return
         */
        String typeClass();
    }
    /**
     * Populate the annotated field with the mod instance.
     * @author cpw
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Instance {}
}
