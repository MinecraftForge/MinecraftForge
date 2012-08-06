package cpw.mods.fml.common.network;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.EnumSet;

import cpw.mods.fml.common.Side;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NetworkMod
{
    /**
     * Does this mod require the client side to be present when installed on a server?
     *
     * @return
     */
    boolean clientSideRequired() default false;
    /**
     * Does this mod require the server side to be present when installed on a client?
     * @return
     */
    boolean serverSideRequired() default false;
    /**
     * A list of Packet250 network channels to register for this mod
     * @return
     */
    String[] channels() default {};
    /**
     * An optional range check for client to server communication version compatibility
     * @return
     */
    String versionBounds() default "";
    /**
     * A marker for a method that will be offered the client's version string
     * if more sophisticated version rejection handling is required:
     * The method should accept a "String", a "NetworkManager" and return a boolean true
     * if the version can be accepted.
     * @author cpw
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface VersionCheckHandler { }
}
