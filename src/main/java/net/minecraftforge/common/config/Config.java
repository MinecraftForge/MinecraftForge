/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.common.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Config
{
	/**
	 * The mod id that this configuration is associated with.
	 */
    String modid();
    /**
     * A user friendly name for the config file,
     * the default will be modid
     */
    String name() default "";

    /**
     * The type this is, right now the only value is INSTANCE.
     * This is intended to be expanded upon later for more Forge controlled
     * configs.
     */
    Type type() default Type.INSTANCE;

    public static enum Type
    {
    	/**
    	 * Loaded once, directly after mod construction. Before pre-init.
    	 * This class must have static fields.
    	 */
    	INSTANCE(true);


    	private boolean isStatic = true;
    	private Type(boolean isStatic) { this.isStatic = isStatic; }
    	public boolean isStatic(){ return this.isStatic; }
    }

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
    public @interface LangKey
    {
    	String value();
    }

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
    public @interface Comment
    {
    	String[] value();
    }

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
    public @interface RangeInt
    {
    	int min() default Integer.MIN_VALUE;
    	int max() default Integer.MAX_VALUE;
    }

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
    public @interface RangeDouble
    {
    	double min() default Double.MIN_VALUE;
    	double max() default Double.MAX_VALUE;
    }
}
