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

package net.minecraftforge.fml.common.event;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.Mod.Instance;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import org.apache.logging.log4j.Level;

/**
 * Simple intermod communications to receive simple messages directed at you
 * from other mods
 *
 * @author cpw
 *
 */
public class FMLInterModComms {
    private static final ImmutableList<IMCMessage> emptyIMCList = ImmutableList.<IMCMessage>of();
    private static ArrayListMultimap<String, IMCMessage> modMessages = ArrayListMultimap.create();

    /**
     * Subscribe to this event to receive your messages (they are sent between
     * {@link FMLInitializationEvent} and {@link FMLPostInitializationEvent})
     *
     * @see net.minecraftforge.fml.common.Mod.EventHandler for how to subscribe to this event
     * @author cpw
     */
    public static class IMCEvent extends FMLEvent {
        private ModContainer activeContainer;

        @Override
        public void applyModContainer(ModContainer activeContainer)
        {
            this.activeContainer = activeContainer;
            this.currentList = null;
            FMLLog.finer("Attempting to deliver %d IMC messages to mod %s", modMessages.get(activeContainer.getModId()).size(), activeContainer.getModId());
        }

        private ImmutableList<IMCMessage> currentList;

        public ImmutableList<IMCMessage> getMessages()
        {
            if (currentList == null)
            {
                currentList = ImmutableList.copyOf(modMessages.removeAll(activeContainer.getModId()));
            }
            return currentList;
        }
    }

    /**
     * You will receive an instance of this for each message sent
     *
     * @author cpw
     *
     */
    public static final class IMCMessage {
        private final boolean isFunction;
        /**
         * This is the modid of the mod that sent you the message
         */
        private String sender;
        /**
         * This field, and {@link #value} are both at the mod's discretion
         */
        public final String key;
        /**
         * This field, and {@link #key} are both at the mod's discretion
         */
        private final Object value;

        private IMCMessage(String key, Object value)
        {
            this.key = key;
            this.value = value;
            this.isFunction = false;
        }

        private IMCMessage(String key, String value, boolean isFunction) {
            this.key = key;
            this.value = value;
            this.isFunction = isFunction;
        }

        @Override
        public String toString()
        {
            return sender;
        }

        /**
         * Get the sending modId of this message.
         * @return The modId of the mod that originated the message
         */
        public String getSender()
        {
            return this.sender;
        }

        void setSender(ModContainer activeModContainer)
        {
            this.sender = activeModContainer.getModId();
        }

        /**
         * Get the string value from this message.
         * @throws ClassCastException if this message doesn't contain a String value
         * @return The string value
         */
        public String getStringValue()
        {
            return (String) value;
        }

        /**
         * Get the {@link NBTTagCompound} value from this message
         * @throws ClassCastException if this message doesn't contain an NBT value
         * @return The NBT value
         */
        public NBTTagCompound getNBTValue()
        {
            return (NBTTagCompound) value;
        }

        /**
         * Get the {@link ItemStack} value from this message
         * @throws ClassCastException if this message doesn't contain an Itemstack value
         * @return The Itemstack value
         */
        public ItemStack getItemStackValue()
        {
            return (ItemStack) value;
        }

        /**
         * Get the {@link Function} value from this message. This will attempt to classload the function
         * supplied by the caller. The parameter classes are strictly to give a concrete generic function return value.
         * @param functionFrom The type of the argument to the function
         * @param functionTo The type of the result of the function
         * @param <T> The argument type
         * @param <V> The result type
         * @return The function value or Optional.absent if it wasn't readable or isn't a function call
         */
        @SuppressWarnings("unchecked")
        public <T,V> Optional<Function<T,V>> getFunctionValue(Class<T> functionFrom, Class<V> functionTo) {
            if (!isFunction) {
                return Optional.absent();
            }
            try {
                Function<T,V> f = Class.forName((String) value).asSubclass(Function.class).newInstance();
                return Optional.of(f);
            } catch (Exception e) {
                FMLLog.getLogger().log(Level.INFO, "An error occurred instantiating the IMC function. key: {} value: {}, caller: {}", key,value,sender);
                return Optional.absent();
            }
        }

        /**
         * Get the actual message class type
         * @return The type of the message
         */
        public Class<?> getMessageType()
        {
            return value.getClass();
        }

        /**
         * Is this a string type message
         * @return if this is a string type message
         */
        public boolean isStringMessage()
        {
            return String.class.isAssignableFrom(getMessageType());
        }

        /**
         * Is this an {@link ItemStack} type message
         * @return if this is an itemstack type message
         */
        public boolean isItemStackMessage()
        {
            return ItemStack.class.isAssignableFrom(getMessageType());
        }

        /**
         * Is this an {@link NBTTagCompound} type message
         * @return if this is an NBT type message
         */
        public boolean isNBTMessage()
        {
            return NBTTagCompound.class.isAssignableFrom(getMessageType());
        }

        /**
         * Is this a {@link Function} type message
         * @return if this is a function type message
         */
        public boolean isFunctionMessage() { return Function.class.isAssignableFrom(getMessageType()); }
    }

    /**
     * Send a startup time message
     * @param modId The modid to send it to
     * @param key The mod specific key
     * @param value An NBT type value
     * @return if the message was enqueued successfully and will be processed during startup
     */
    public static boolean sendMessage(String modId, String key, NBTTagCompound value)
    {
        return enqueueStartupMessage(modId, new IMCMessage(key, value));
    }

    /**
     * Send a startup time message
     * @param modId The modid to send it to
     * @param key The mod specific key
     * @param value An Itemstack value
     * @return if the message was enqueued successfully and will be processed during startup
     */
    public static boolean sendMessage(String modId, String key, ItemStack value)
    {
        return enqueueStartupMessage(modId, new IMCMessage(key, value));
    }

    /**
     * Send a startup time message
     * @param modId The modid to send it to
     * @param key The mod specific key
     * @param value A String value
     * @return if the message was enqueued successfully and will be processed during startup
     */
    public static boolean sendMessage(String modId, String key, String value)
    {
        return enqueueStartupMessage(modId, new IMCMessage(key, value));
    }

    /**
     * Send a startup time function message
     * @param modId The modid to send it to
     * @param key The mod specific key
     * @param functionClassName The class name of a function that will be instantiated when the
     *                          message is read. It must implement {@link Function}
     * @return if the message was enqueued successfully and will be processed during startup
     */
    public static boolean sendFunctionMessage(String modId, String key, String functionClassName)
    {
        return enqueueStartupMessage(modId, new IMCMessage(key, functionClassName, true));
    }

    /**
     * Send a post-startup message
     * @param sourceMod The mod sending the message
     * @param modId The modid to send it to
     * @param key The mod specific key
     * @param value An NBT type value
     */
    public static void sendRuntimeMessage(Object sourceMod, String modId, String key, NBTTagCompound value)
    {
        enqueueMessage(sourceMod, modId, new IMCMessage(key, value));
    }

    /**
     * Send a post-startup message
     * @param sourceMod The mod sending the message
     * @param modId The modid to send it to
     * @param key The mod specific key
     * @param value An Itemstack value
     */
    public static void sendRuntimeMessage(Object sourceMod, String modId, String key, ItemStack value)
    {
        enqueueMessage(sourceMod, modId, new IMCMessage(key, value));
    }

    /**
     * Send a post-startup message
     * @param sourceMod The mod sending the message
     * @param modId The modid to send it to
     * @param key The mod specific key
     * @param value A string value
     */
    public static void sendRuntimeMessage(Object sourceMod, String modId, String key, String value)
    {
        enqueueMessage(sourceMod, modId, new IMCMessage(key, value));
    }

    /**
     * Send a post-startup function message.
     *
     * @param sourceMod The mod originating this message
     * @param modId The modid to send it to
     * @param key The mod specific key
     * @param functionClassName The name of a class to be loaded when the caller processes this message.
     *                          The named class must extend {@link Function}
     */
    public static void sendRuntimeFunctionMessage(Object sourceMod, String modId, String key, String functionClassName)
    {
        enqueueMessage(sourceMod, modId, new IMCMessage(key, functionClassName, true));
    }

    private static boolean enqueueStartupMessage(String modTarget, IMCMessage message)
    {
        if (Loader.instance().activeModContainer() == null)
        {
            return false;
        }
        enqueueMessage(Loader.instance().activeModContainer(), modTarget, message);
        return Loader.isModLoaded(modTarget) && !Loader.instance().hasReachedState(LoaderState.POSTINITIALIZATION);

    }
    private static void enqueueMessage(Object sourceMod, String modTarget, IMCMessage message)
    {
        ModContainer mc;
        if (sourceMod instanceof ModContainer) {
            mc = (ModContainer) sourceMod;
        }
        else
        {
            mc = FMLCommonHandler.instance().findContainerFor(sourceMod);
        }
        if (mc != null && Loader.isModLoaded(modTarget))
        {
            message.setSender(mc);
            modMessages.put(modTarget, message);
        }
    }

    /**
     * Retrieve any pending runtime messages for the mod
     * @param forMod The {@link Instance} of the Mod to fetch messages for
     * @return any messages - the collection will never be null
     */
    public static ImmutableList<IMCMessage> fetchRuntimeMessages(Object forMod)
    {
        ModContainer mc = FMLCommonHandler.instance().findContainerFor(forMod);
        if (mc != null)
        {
            return ImmutableList.copyOf(modMessages.removeAll(mc.getModId()));
        }
        else
        {
            return emptyIMCList;
        }
    }
}
