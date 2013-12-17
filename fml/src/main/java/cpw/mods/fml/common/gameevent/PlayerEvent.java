package cpw.mods.fml.common.gameevent;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.eventhandler.Event;

public class PlayerEvent extends Event {
    public final EntityPlayer player;
    private PlayerEvent(EntityPlayer player)
    {
        this.player = player;
    }

    public static class PlayerLoggedInEvent extends PlayerEvent {
        public PlayerLoggedInEvent(EntityPlayer player)
        {
            super(player);
        }
    }

    public static class PlayerLoggedOutEvent extends PlayerEvent {
        public PlayerLoggedOutEvent(EntityPlayer player)
        {
            super(player);
        }
    }

    public static class PlayerRespawnEvent extends PlayerEvent {
        public PlayerRespawnEvent(EntityPlayer player)
        {
            super(player);
        }
    }

    public static class PlayerChangedDimensionEvent extends PlayerEvent {
        public final int fromDim;
        public final int toDim;
        public PlayerChangedDimensionEvent(EntityPlayer player, int fromDim, int toDim)
        {
            super(player);
            this.fromDim = fromDim;
            this.toDim = toDim;
        }
    }
}
