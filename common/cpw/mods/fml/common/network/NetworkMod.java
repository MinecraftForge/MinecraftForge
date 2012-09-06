package cpw.mods.fml.common.network;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.minecraft.src.Packet131MapData;

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
     * A list of Packet250 network channels to register for this mod - these channels
     * will be universal and will require a universal packethandler to handle them
     *
     * @return
     */
    String[] channels() default {};
    /**
     * An optional range check for client to server communication version compatibility
     * @return
     */
    String versionBounds() default "";

    /**
     * A packet handler implementation for channels registered through this annotation
     * - this packet handler will be universal and handle both client and server
     * requests.
     *
     * @return
     */
    Class<? extends IPacketHandler> packetHandler() default NULL.class;

    /**
     * A tiny packet handler implementation based on {@link Packet131MapData} for "small"
     * data packet loads.
     *
     * @return
     */
    Class<? extends ITinyPacketHandler> tinyPacketHandler() default NULL.class;
    /**
     * A connection handler implementation for this network mod
     *
     * @return
     */
    Class<? extends IConnectionHandler> connectionHandler() default NULL.class;
    /**
     * A packet handler and channels to register for the client side
     *
     * @return
     */
    SidedPacketHandler clientPacketHandlerSpec() default @SidedPacketHandler(channels = {}, packetHandler = NULL.class );

    /**
     * A packet handler and channels to register for the server side
     * @return
     */
    SidedPacketHandler serverPacketHandlerSpec() default @SidedPacketHandler(channels = {}, packetHandler = NULL.class );

    /**
     * Special dummy class for handling stupid annotation default values
     * @author cpw
     *
     */
    static interface NULL extends IPacketHandler, IConnectionHandler, ITinyPacketHandler {};

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

    /**
     * Bundles together a packet handler and it's associated channels for the sided packet handlers
     * @author cpw
     *
     */
    public @interface SidedPacketHandler {
        String[] channels();
        Class<? extends IPacketHandler> packetHandler();
    }

}
