package net.minecraft.src.forge;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map.Entry;

import net.minecraft.src.NetworkManager;

public class MessageManager
{
    private Hashtable<NetworkManager, ConnectionInstance> connections = new Hashtable<NetworkManager, ConnectionInstance>();
    private static MessageManager instance;

    public static MessageManager getInstance()
    {
        if (instance == null)
        {
            instance = new MessageManager();
        }
        return instance;
    }

    public class ConnectionInstance
    {
        private NetworkManager network;
        private Hashtable<String, ArrayList<IPacketHandler>> channelToHandlers = new Hashtable<String, ArrayList<IPacketHandler>>();
        private Hashtable<IPacketHandler, ArrayList<String>> handlerToChannels = new Hashtable<IPacketHandler, ArrayList<String>>();
        private HashSet<String> activeChannels = new HashSet<String>();

        public ConnectionInstance(NetworkManager mgr)
        {
            network = mgr;
        }

        /**
         * Retrieves the associated NetworkManager
         * @return The associated NetworkManager;
         */
        public NetworkManager getNetwork()
        {
            return network;
        }

        /**
         * Removes all channels and handlers from the registration.
         *
         * @return An array of channels that were in the registration
         *   If the connection is still active, you should send UNREGISTER messages for these.
         */
        public String[] unregisterAll()
        {
            String[] ret = getRegisteredChannels();
            channelToHandlers.clear();
            handlerToChannels.clear();
            return ret;
        }

        /**
         * Registers a channel to a specific handler.
         *
         * @param handler The handler to register
         * @param channel The channel to register on
         * @return True if the channel was not previously registered to any handlers.
         *      If True, the connection is still active, and this is not the OnLogin event,
         *      you should send a REGISTER command for this channel.
         */
        public boolean registerChannel(IPacketHandler handler, String channel)
        {
            ArrayList<IPacketHandler> handlers = channelToHandlers.get(channel);
            ArrayList<String> channels = handlerToChannels.get(handler);
            boolean ret = false;

            if (handlers == null)
            {
                ret = true;
                handlers = new ArrayList<IPacketHandler>();
                channelToHandlers.put(channel, handlers);
            }

            if (channels == null)
            {
                channels = new ArrayList<String>();
                handlerToChannels.put(handler, channels);
            }

            if (!channels.contains(channel))
            {
                channels.add(channel);
            }
            if (!handlers.contains(handler))
            {
                handlers.add(handler);
            }
            return ret;
        }

        /**
         * Unregisters a channel from the specified handler.
         *
         * @param handler The handler to remove the channel from.
         * @param channel The channel to remove from the handler registration.
         * @return True if this was the last handler registered with the specified channel.
         *      If this is the case, and the network connection is still alive, you should send
         *      a UNREGISTER message for this channel.
         */
        public boolean unregisterChannel(IPacketHandler handler, String channel)
        {
            boolean ret = false;
            ArrayList<IPacketHandler> handlers = channelToHandlers.get(channel);
            ArrayList<String> channels = handlerToChannels.get(handler);

            if (handlers != null && handlers.contains(handler))
            {
                handlers.remove(handler);
                if (handlers.size() == 0)
                {
                    ret = true;
                    channelToHandlers.remove(channel);
                }
            }

            if (channels != null && channels.contains(channel))
            {
                channels.remove(channel);
                if (handlers.size() == 0)
                {
                    handlerToChannels.remove(handler);
                }
            }

            return ret;
        }

        /**
         * Unregisters a handler from all of it's associated channels.
         *
         * @param handler The handler to unregister
         * @return A list of channels that now have no handlers.
         *      If the connection is still active, you should send a UNREGISTER
         *      message for each channel in this list.
         */
        public String[] unregisterHandler(IPacketHandler handler)
        {
            ArrayList<String> tmp = handlerToChannels.get(handler);
            if (tmp != null)
            {
                String[] channels = tmp.toArray(new String[0]);
                tmp = new ArrayList<String>();

                for (String channel : channels)
                {
                    if (unregisterChannel(handler, channel))
                    {
                        tmp.add(channel);
                    }
                }
                return tmp.toArray(new String[0]);
            }
            return new String[0];
        }

        /**
         * Retrieves a list of all unique channels that currently have valid handlers.
         *
         * @return The channel list
         */
        public String[] getRegisteredChannels()
        {
            int x = 0;
            String[] ret = new String[channelToHandlers.size()];

            for (String value : channelToHandlers.keySet())
            {
                ret[x++] = value;
            }
            return ret;
        }

        /**
         * Retrieves a list of all handlers currently registered to the specified channel.
         *
         * @param channel The channel to get the handlers for.
         * @return A array containing all handlers for this channel.
         */
        public IPacketHandler[] getChannelHandlers(String channel)
        {
            ArrayList<IPacketHandler> handlers = channelToHandlers.get(channel);
            if (handlers != null)
            {
                return handlers.toArray(new IPacketHandler[0]);
            }
            return new IPacketHandler[0];
        }

        /**
         * Adds a channel to the active set.
         * This is a set that the other end of the connection has registered a channel.
         *
         * @param channel The channel name
         */
        public void addActiveChannel(String channel)
        {
            if (!activeChannels.contains(channel))
            {
                activeChannels.add(channel);
            }
        }

        /**
         * Removes a channel from the active set.
         * This should be done with the other end of the connection unregisters a channel.
         *
         * @param channel
         */
        public void removeActiveChannel(String channel)
        {
            if (activeChannels.contains(channel))
            {
                activeChannels.remove(channel);
            }
        }

        /**
         * Checks if the specified channel is registered as active by the other end of the connection.
         *
         * @param channel The channel to check
         * @return True if it's active, false otherwise.
         */
        public boolean isActiveChannel(String channel)
        {
            return activeChannels.contains(channel);
        }
    }

    /**
     * Retrieves, or creates a ConnectionInstance associated with the specific NetworkManager.
     *
     * @param manager The NetworkManager to look for.
     * @return A ConnectionInstance channel manager for this NetworkManager
     */
    public ConnectionInstance getConnection(NetworkManager manager)
    {
        ConnectionInstance ret = connections.get(manager);
        if (ret == null)
        {
            ret = new ConnectionInstance(manager);
            connections.put(manager, ret);
        }
        return ret;
    }

    /**
     * Removes the associated channel manager, and unregisters all channels/handlers from it.
     *
     * @param manager The NetworkManager to look for.
     * @return An array of all channels that were still registered to this NetowrkManager.
     *      If the connection is still active, you should send a UNREGISTER request for
     *      all of these channels.
     */
    public String[] removeConnection(NetworkManager manager)
    {
        if (connections.containsKey(manager))
        {
            ConnectionInstance con = getConnection(manager);
            String[] ret = con.unregisterAll();
            connections.remove(manager);
            return ret;
        }
        return new String[0];
    }

    /**
     * Registers a channel to a specific handler.
     *
     * @param manager The manager to register to
     * @param handler The handler to register
     * @param channel The channel to register on
     * @return True if the channel was not previously registered to any handlers.
     *      If True, the connection is still active, and this is not the OnLogin event,
     *      you should send a REGISTER command for this channel.
     */
    public boolean registerChannel(NetworkManager manager, IPacketHandler handler, String channel)
    {
        ConnectionInstance con = getConnection(manager);
        return con.registerChannel(handler, channel);
    }

    /**
     * Unregisters a channel from the specified handler.
     *
     * @param manager The manager to register to
     * @param handler The handler to remove the channel from.
     * @param channel The channel to remove from the handler registration.
     * @return True if this was the last handler registered with the specified channel.
     *      If this is the case, and the network connection is still alive, you should send
     *      a UNREGISTER message for this channel.
     */
    public boolean unregisterChannel(NetworkManager manager, IPacketHandler handler, String channel)
    {
        if (connections.containsKey(manager))
        {
            ConnectionInstance con = getConnection(manager);
            return con.unregisterChannel(handler, channel);
        }
        return false;
    }

    /**
     * Unregisters a handler from all of it's associated channels.
     *
     * @param manager The manager to register to
     * @param handler The handler to unregister
     * @return A list of channels that now have no handlers.
     *      If the connection is still active, you should send a UNREGISTER
     *      message for each channel in this list.
     */
    public String[] unregisterHandler(NetworkManager manager, IPacketHandler handler)
    {
        if (connections.containsKey(manager))
        {
            ConnectionInstance con = getConnection(manager);
            return con.unregisterHandler(handler);
        }
        return new String[0];
    }

    /**
     * Retrieves a list of all unique channels that currently have valid handlers.
     *
     * @param manager The NetworkManager to look for.
     * @return The channel list
     */
    public String[] getRegisteredChannels(NetworkManager manager)
    {
        if (connections.containsKey(manager))
        {
            ConnectionInstance con = getConnection(manager);
            return con.getRegisteredChannels();
        }
        return new String[0];
    }

    /**
     * Retrieves a list of all handlers currently registered to the specified channel.
     *
     * @param manager The NetworkManager to look for.
     * @param channel The channel to get the handlers for.
     * @return A array containing all handlers for this channel.
     */
    public IPacketHandler[] getChannelHandlers(NetworkManager manager, String channel)
    {
        if (connections.containsKey(manager))
        {
            ConnectionInstance con = getConnection(manager);
            return con.getChannelHandlers(channel);
        }
        return new IPacketHandler[0];
    }

    /**
     * Adds a channel to the active set.
     * This is a set that the other end of the connection has registered a channel.
     *
     * @param manager The NetworkManager to look for.
     * @param channel The channel name
     */
    public void addActiveChannel(NetworkManager manager, String channel)
    {
        ConnectionInstance con = getConnection(manager);
        con.addActiveChannel(channel);
    }

    /**
     * Removes a channel from the active set.
     * This should be done with the other end of the connection unregisters a channel.
     *
     * @param manager The NetworkManager to look for.
     * @param channel
     */
    public void removeActiveChannel(NetworkManager manager, String channel)
    {
        if (connections.containsKey(manager))
        {
            ConnectionInstance con = getConnection(manager);
            con.removeActiveChannel(channel);
        }
    }

    /**
     * Checks if the specified channel is registered as active by the other end of the connection.
     *
     * @param manager The NetworkManager to look for.
     * @param channel The channel to check
     * @return True if it's active, false otherwise.
     */
    public boolean isActiveChannel(NetworkManager manager, String channel)
    {
        if (connections.containsKey(manager))
        {
            ConnectionInstance con = getConnection(manager);
            return con.isActiveChannel(channel);
        }
        return false;
    }

    public void dispatchIncomingMessage(NetworkManager manager, String channel, byte[] data)
    {
        if (data == null)
        {
            data = new byte[0];
        }
        
        if (channel.equals("Forge"))
        {
            if (ForgeHooks.getPacketHandler() != null)
            {
                byte[] tmpData = new byte[data.length];
                System.arraycopy(data, 0, tmpData, 0, data.length);
                ForgeHooks.getPacketHandler().onPacketData(manager, channel, tmpData);
            }
        }

        if (connections.containsKey(manager))
        {
            ConnectionInstance con = getConnection(manager);
            IPacketHandler[] handlers = con.getChannelHandlers(channel);
            byte[] tmpData = new byte[data.length];
            for (IPacketHandler handler : handlers)
            {
                System.arraycopy(data, 0, tmpData, 0, data.length);
                handler.onPacketData(manager, channel, tmpData);
            }
        }
    }
}
