package net.minecraftforge.common;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public abstract class StackOrigin {

    /** Sets the origin stack to something different. */
    public abstract void setStack(ItemStack stack);

    /** Returns if the item is currently being held in some way. */
    public boolean isHeld() { return false; }

    /** Returns if the item is currently being used. */
    public boolean isActive() { return false; }


    public static class Entity extends StackOrigin {

        public final EntityLivingBase entity;
        private final int slot;

        public Entity(EntityLivingBase entity, int slot) {
            this.entity = entity;
            this.slot = slot;
        }

        @Override
        public void setStack(ItemStack stack)
        {
            entity.setCurrentItemOrArmor(slot, stack);
        }

        @Override
        public boolean isHeld() { return slot == 0; }

        /** Returns if the item is currently equipped as armor or similar. */
        public boolean isEquipped() { return slot > 0 && slot <= 4; }

    }


    public static class Player extends Entity {

        public final EntityPlayer player;
        private final int slot;
        private final boolean active;

        public Player(EntityPlayer player, int slot)
        {
            super(player, -1);
            this.player = player;
            this.slot = slot;
            active = player.inventory.getStackInSlot(slot) == player.getItemInUse();
        }

        @Override
        public void setStack(ItemStack stack)
        {
            player.inventory.setInventorySlotContents(slot, stack);
        }

        @Override
        public boolean isHeld() { return slot == player.inventory.currentItem; }

        @Override
        public boolean isActive() { return active; }

        @Override
        public boolean isEquipped() { return slot >= 36 && slot < 40; }

        /** Returns if the item is currently in the player's
         *  hotbar or a similar subset of the inventory */
        public boolean isInHotbar() { return slot < 9; }

    }

}
