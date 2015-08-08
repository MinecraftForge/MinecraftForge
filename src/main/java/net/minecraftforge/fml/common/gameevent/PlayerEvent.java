package net.minecraftforge.fml.common.gameevent;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.eventhandler.Event;

public class PlayerEvent extends Event {
    public final EntityPlayer player;
    private PlayerEvent(EntityPlayer player)
    {
        this.player = player;
    }

    public static class ItemPickupEvent extends PlayerEvent {
        public final EntityItem pickedUp;
        public ItemPickupEvent(EntityPlayer player, EntityItem pickedUp)
        {
            super(player);
            this.pickedUp = pickedUp;
        }
    }

    public static class ItemCraftedEvent extends PlayerEvent {
        public final ItemStack crafting;
        public final IInventory craftMatrix;
        public ItemCraftedEvent(EntityPlayer player, ItemStack crafting, IInventory craftMatrix)
        {
            super(player);
            this.crafting = crafting;
            this.craftMatrix = craftMatrix;
        }
    }
    public static class ItemSmeltedEvent extends PlayerEvent {
        public final ItemStack smelting;
        public ItemSmeltedEvent(EntityPlayer player, ItemStack crafting)
        {
            super(player);
            this.smelting = crafting;
        }
    }

    public static class PlayerLoggedInEvent extends PlayerEvent {

        private IChatComponent loginMessage;

        public PlayerLoggedInEvent(EntityPlayer player)
        {
            super(player);
        }

        /**
         * Gets The login message to send to all players.
         *
         * @return The login message
         */
        public IChatComponent getLoginMessage()
        {
            return this.loginMessage;
        }

        /**
         * Sets the message to send to all players when the player logs in.
         * Setting null will not send the message.
         *
         * @param loginMessage The login message, may be null
         */
        public void setLoginMessage(IChatComponent loginMessage)
        {
            this.loginMessage = loginMessage;
        }
    }

    public static class PlayerLoggedOutEvent extends PlayerEvent {

        private IChatComponent logoutMessage;

        public PlayerLoggedOutEvent(EntityPlayer player)
        {
            super(player);
        }

        /**
         * Gets the logout message to send to all players.
         *
         * @return The logout message
         */
        public IChatComponent getLogoutMessage()
        {
            return this.logoutMessage;
        }

        /**
         * Sets the message to send to all players when the player logs out.
         * Setting null will not send the message.
         *
         * @param logoutMessage The logout message, may be null
         */
        public void setLogoutMessage(IChatComponent logoutMessage)
        {
            this.logoutMessage = logoutMessage;
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
