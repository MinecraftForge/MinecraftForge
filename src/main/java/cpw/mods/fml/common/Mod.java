/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package cpw.mods.fml.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.event.FMLEvent;
import cpw.mods.fml.common.event.FMLFingerprintViolationEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * This defines a Mod to FML.
 * Any class found with this annotation applied will be loaded as a Mod. The instance that is loaded will
 * represent the mod to other Mods in the system. It will be sent various subclasses of {@link FMLEvent}
 * at pre-defined times during the loading of the game, based on where you have applied the {@link EventHandler}
 * annotation.
 *
 * This annotation is usually paired with a second annotation {@link NetworkMod}, which declares the
 * network related properties of this mod.
 *
 * <p>This is a simple example of a Mod. It has the modId of "MyModId", the name of "My example mod", it is
 * version 1.0, and depends on FML being loaded. It has the {@link NetworkMod} annotation as well, declaring it uses channel
 * "MyModChannel" and {@link IPacketHandler} class PacketHandler.
 * <pre>{@code
 * package mymod;
 * // Declare that this is a mod with modId "MyModId", name "My example mod", version "1.0" and dependency on FML.
 * {@literal @}Mod(modId="MyModId",name="My example mod",version="1.0",dependencies="required-after:FML")
 * // Declare that this is a network mod using the {@link Packet250CustomPayload} channel "MyModChannel", required on the client if it's present
 * // on the server, with {@link IPacketHandler} class PacketHandler.
 * {@literal @}NetworkMod(channels = { "MyModChannel" }, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketHandler.class)
 * public class MyMod {
 *      // Populate this field with the instance of the mod created by FML
 *      {@literal @}Instance("MyModId")
 *      public MyMod instance;
 *
 *      // Mark this method for receiving an {@link FMLEvent} (in this case, it's the {@link FMLPreInitializationEvent})
 *      {@literal @}EventHandler public void preInit(FMLPreInitializationEvent event)
 *      {
 *          // Do stuff in pre-init phase (read config, create blocks and items, register them)
 *      }
 * }
 * }
 * </pre>
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
     */
    String modid();
    /**
     * A user friendly name for the mod
     */
    String name() default "";
    /**
     * A version string for this mod
     */
    String version() default "";
    /**
     * A simple dependency string for this mod (see modloader's "priorities" string specification)
     */
    String dependencies() default "";
    /**
     * Whether to use the mcmod.info metadata by default for this mod.
     * If true, settings in the mcmod.info file will override settings in these annotations.
     */
    boolean useMetadata() default false;

    /**
     * The acceptable range of minecraft versions that this mod will load and run in
     * The default ("empty string") indicates that only the current minecraft version is acceptable.
     * FML will refuse to run with an error if the minecraft version is not in this range across all mods.
     * @return A version range as specified by the maven version range specification or the empty string
     */
    String acceptedMinecraftVersions() default "";
    /**
     * An optional bukkit plugin that will be injected into the bukkit plugin framework if
     * this mod is loaded into the FML framework and the bukkit coremod is present.
     * Instances of the bukkit plugin can be obtained via the {@link BukkitPluginRef} annotation on fields.
     *
     * This may be implemented by a bukkit integration. It is not provided with vanilla FML or MinecraftForge.
     *
     * @return The name of the plugin to load for this mod
     */
    String bukkitPlugin() default "";
    /**
     * Specifying this field allows for a mod to expect a signed jar with a fingerprint matching this value.
     * The fingerprint should be SHA-1 encoded, lowercase with ':' removed. An empty value indicates that
     * the mod is not expecting to be signed.
     *
     * Any incorrectness of the fingerprint, be it missing or wrong, will result in the {@link FMLFingerprintViolationEvent}
     * event firing <i>prior to any other event on the mod</i>.
     *
     * @return A certificate fingerprint that is expected for this mod.
     */
    String certificateFingerprint() default "";

    /**
     * The language the mod is authored in. This will be used to control certain compatibility behaviours for this mod.
     * Valid values are currently "java", "scala"
     *
     * @return The language the mod is authored in
     */
    String modLanguage() default "java";
    /**
     * NOT YET IMPLEMENTED. </br>
     * An optional ASM hook class, that can be used to apply ASM to classes loaded from this mod. It is also given
     * the ASM tree of the class declaring {@link Mod} to do with what it will.
     *
     * @return The name of a class to be loaded and executed. Must implement {@link IASMHook}.
     */
    @Deprecated
    String asmHookClass() default "";

    /**
     * A list of custom properties for this mod. Completely up to the mod author if/when they
     * want to put anything in here.
     * @return an optional list of custom properties
     */
    CustomProperty[] customProperties() default {};

    /**
     * A custom key => value property pair for use with {@link Mod#customProperties()}
     * @author cpw
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({})
    public @interface CustomProperty
    {
        /**
         * A key. Should be unique.
         * @return A key
         */
        String k();
        /**
         * A value. Can be anything.
         * @return A value
         */
        String v();
    }
    /**
     * Marks the associated method as handling an FML lifecycle event.
     * The method must have a single parameter, one of the following types. This annotation
     * replaces the multiple different annotations that previously were used.
     *
     * Current event classes. This first section is standard lifecycle events. They are dispatched
     * at various phases as the game starts. Each event should have information useful to that
     * phase of the lifecycle. They are fired in this order.
     *
     * These suggestions are mostly just suggestions on what to do in each event.
     * <ul>
     * <li> {@link FMLPreInitializationEvent} : Run before anything else. Read your config, create blocks,
     * items, etc, and register them with the {@link GameRegistry}.</li>
     * <li> {@link FMLInitializationEvent} : Do your mod setup. Build whatever data structures you care about. Register recipes,
     * send {@link FMLInterModComms} messages to other mods.</li>
     * <li> {@link FMLPostInitializationEvent} : Handle interaction with other mods, complete your setup based on this.</li>
     * </ul>
     * <p>These are the server lifecycle events. They are fired whenever a server is running, or about to run. Each time a server
     * starts they will be fired in this sequence.
     * <ul>
     * <li> {@link FMLServerAboutToStartEvent} : Use if you need to handle something before the server has even been created.</li>
     * <li> {@link FMLServerStartingEvent} : Do stuff you need to do to set up the server. register commands, tweak the server.</li>
     * <li> {@link FMLServerStartedEvent} : Do what you need to with the running server.</li>
     * <li> {@link FMLServerStoppingEvent} : Do what you need to before the server has started it's shutdown sequence.</li>
     * <li> {@link FMLServerStoppedEvent} : Do whatever cleanup you need once the server has shutdown. Generally only useful
     * on the integrated server.</li>
     * </ul>
     * The second set of events are more specialized, for receiving notification of specific
     * information.
     * <ul>
     * <li> {@link FMLFingerprintViolationEvent} : Sent just before {@link FMLPreInitializationEvent}
     * if something is wrong with your mod signature</li>
     * <li> {@link IMCEvent} : Sent just after {@link FMLInitializationEvent} if you have IMC messages waiting
     * from other mods</li>
     * </ul>
     *
     * @author cpw
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface EventHandler{}

    /**
     * Populate the annotated field with the mod instance based on the specified ModId. This can be used
     * to retrieve instances of other mods.
     * @author cpw
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Instance {
        /**
         * The mod object to inject into this field
         */
        String value() default "";
    }
    /**
     * Populate the annotated field with the mod's metadata.
     * @author cpw
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Metadata {
        /**
         * The mod id specifying the metadata to load here
         */
        String value() default "";
    }

    /**
     * Mod instance factory method. Should return an instance of the mod. Applies only to static methods on the same class as {@link Mod}.
     * @author cpw
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface InstanceFactory {
    }
}
