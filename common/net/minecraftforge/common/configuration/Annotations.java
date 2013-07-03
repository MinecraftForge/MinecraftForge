package net.minecraftforge.common.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class Annotations {

    /**
     * Notes for the parser that the field is to be parsed as a Block ID this
     * will only work on a field with the type of {@link Integer} The comment on
     * this will override the {@link ConfigurationComment} annotation
     * 
     * @author Cazzar
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface BlockID {
        String comment() default "";
    }

    /**
     * Notes for the parser that all the fields inside the class is used for the
     * configuration parser.
     * 
     * @author Cazzar
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ConfigurationClass {
        String category() default "";
    }

    /**
     * Notes for the parser that this field or class should have the passed
     * comment
     * 
     * @author Cazzar
     */
    @Target({ ElementType.FIELD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ConfigurationComment {
        String value();
    }

    /**
     * Notes for the parser that this field is used in the configuration
     * 
     * @author Cazzar
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ConfigurationOption {
        String category();

        String comment() default "";

        String key();
    }

    /**
     * Notes for the parser that this field is an item ID this will only work on
     * a field with the type of {@link Integer}. The comment on this will
     * override the {@link ConfigurationComment} annotation
     * 
     * @author Cazzar
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ItemID {
        String comment() default "";
    }

    /**
     * A little more data for the field currently only used for the minimum and
     * maximum values for integer values
     * 
     * @author Cayde
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ConfigurationData {
        int min() default Integer.MIN_VALUE;

        int max() default Integer.MAX_VALUE;
    }
}
