package net.minecraftforge.fml.common.gameevent;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;

public class TickEvent extends Event {
    public enum Type {
        WORLD, PLAYER, CLIENT, SERVER, RENDER;
    }

    public enum Phase {
        START, END;
    }
    public final Type type;
    public final Side side;
    public final Phase phase;
    public TickEvent(Type type, Side side, Phase phase)
    {
        this.type = type;
        this.side = side;
        this.phase = phase;
    }

    public static class ServerTickEvent extends TickEvent
    {
        private final MinecraftServer server;

        /**
         * @deprecated use {@link #ServerTickEvent(MinecraftServer, Phase)}
         */
        @Deprecated
        public ServerTickEvent(Phase phase)
        {
            super(Type.SERVER, Side.SERVER, phase);
            this.server = FMLCommonHandler.instance().getMinecraftServerInstance();
        }

        public ServerTickEvent(MinecraftServer server, Phase phase)
        {
            super(Type.SERVER, Side.SERVER, phase);
            this.server = server;
        }

        public MinecraftServer getServer()
        {
            return this.server;
        }
    }

    public static class ClientTickEvent extends TickEvent {
        public ClientTickEvent(Phase phase)
        {
            super(Type.CLIENT, Side.CLIENT, phase);
        }
    }

    public static class WorldTickEvent extends TickEvent {
        public final World world;
        public WorldTickEvent(Side side, Phase phase, World world)
        {
            super(Type.WORLD, side, phase);
            this.world = world;
        }
    }
    public static class PlayerTickEvent extends TickEvent {
        public final EntityPlayer player;

        public PlayerTickEvent(Phase phase, EntityPlayer player)
        {
            super(Type.PLAYER, player instanceof EntityPlayerMP ? Side.SERVER : Side.CLIENT, phase);
            this.player = player;
        }
    }

    public static class RenderTickEvent extends TickEvent {
        public final float renderTickTime;
        public RenderTickEvent(Phase phase, float renderTickTime)
        {
            super(Type.RENDER, Side.CLIENT, phase);
            this.renderTickTime = renderTickTime;
        }
    }
}
