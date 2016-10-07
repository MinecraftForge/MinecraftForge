package net.minecraftforge.fml.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Populate the annotated field with a {@link net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper} instance using the modid as channel name.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NetworkWrapper
{
    /**
     * The modid to use in this field
     */
    String value() default "";
}
