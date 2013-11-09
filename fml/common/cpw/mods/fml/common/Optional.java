package cpw.mods.fml.common;

import java.lang.annotation.ElementType;
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
        public Interface[] value();
    }
    /**
     * Used to remove optional interfaces
     * @author cpw
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Interface {
        /**
         * The fully qualified name of the interface to be stripped
         * @return the interface name
         */
        public String iface();

        /**
         * The modid that is required to be present for stripping NOT to occur
         * @return the modid
         */
        public String modid();

        /**
         * Strip references to this interface in method declarations? (Useful to kill synthetic methods from scala f.e.)
         *
         * @return if references should be stripped
         */
        public boolean striprefs() default false;
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
        public String modid();
    }
}
