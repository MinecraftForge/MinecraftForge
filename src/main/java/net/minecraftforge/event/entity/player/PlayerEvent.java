/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.event.entity.player;

import java.io.File;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;

/**
 * PlayerEvent is fired whenever an event involving Living entities occurs. <br>
 * If a method utilizes this {@link net.minecraftforge.eventbus.api.Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
public class PlayerEvent extends LivingEvent
{
    private final PlayerEntity entityPlayer;
    public PlayerEvent(PlayerEntity player)
    {
        super(player);
        entityPlayer = player;
    }

    /**
     * @return Player
     */
    public PlayerEntity getPlayer() { return entityPlayer; }
    /**
     * HarvestCheck is fired when a player attempts to harvest a block.<br>
     * This event is fired whenever a player attempts to harvest a block in
     * {@link EntityPlayer#canHarvestBlock(IBlockState)}.<br>
     * <br>
     * This event is fired via the {@link ForgeEventFactory#doPlayerHarvestCheck(EntityPlayer, IBlockState, boolean)}.<br>
     * <br>
     * {@link #state} contains the {@link IBlockState} that is being checked for harvesting. <br>
     * {@link #success} contains the boolean value for whether the Block will be successfully harvested. <br>
     * <br>
     * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    public static class HarvestCheck extends PlayerEvent
    {
        private final BlockState state;
        private boolean success;

        public HarvestCheck(PlayerEntity player, BlockState state, boolean success)
        {
            super(player);
            this.state = state;
            this.success = success;
        }

        public BlockState getTargetBlock() { return this.state; }
        public boolean canHarvest() { return this.success; }
        public void setCanHarvest(boolean success){ this.success = success; }
    }

    /**
     * BreakSpeed is fired when a player attempts to harvest a block.<br>
     * This event is fired whenever a player attempts to harvest a block in
     * {@link EntityPlayer#canHarvestBlock(IBlockState)}.<br>
     * <br>
     * This event is fired via the {@link ForgeEventFactory#getBreakSpeed(EntityPlayer, IBlockState, float, BlockPos)}.<br>
     * <br>
     * {@link #state} contains the block being broken. <br>
     * {@link #originalSpeed} contains the original speed at which the player broke the block. <br>
     * {@link #newSpeed} contains the newSpeed at which the player will break the block. <br>
     * {@link #pos} contains the coordinates at which this event is occurring. Y value -1 means location is unknown.<br>
     * <br>
     * This event is {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
     * If it is canceled, the player is unable to break the block.<br>
     * <br>
     * This event does not have a result. {@link HasResult}<br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    @Cancelable
    public static class BreakSpeed extends PlayerEvent
    {
        private final BlockState state;
        private final float originalSpeed;
        private float newSpeed = 0.0f;
        private final BlockPos pos; // Y position of -1 notes unknown location

        public BreakSpeed(PlayerEntity player, BlockState state, float original, BlockPos pos)
        {
            super(player);
            this.state = state;
            this.originalSpeed = original;
            this.setNewSpeed(original);
            this.pos = pos;
        }

        public BlockState getState() { return state; }
        public float getOriginalSpeed() { return originalSpeed; }
        public float getNewSpeed() { return newSpeed; }
        public void setNewSpeed(float newSpeed) { this.newSpeed = newSpeed; }
        public BlockPos getPos() { return pos; }
    }

    /**
     * NameFormat is fired when a player's display name is retrieved.<br>
     * This event is fired whenever a player's name is retrieved in
     * {@link EntityPlayer#getDisplayName()} or {@link EntityPlayer#refreshDisplayName()}.<br>
     * <br>
     * This event is fired via the {@link ForgeEventFactory#getPlayerDisplayName(EntityPlayer, String)}.<br>
     * <br>
     * {@link #username} contains the username of the player.
     * {@link #displayname} contains the display name of the player.
     * <br>
     * This event is not {@link net.minecraftforge.eventbus.api.Cancelable}.
     * <br>
     * This event does not have a result. {@link HasResult}
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    public static class NameFormat extends PlayerEvent
    {
        private final String username;
        private String displayname;

        public NameFormat(PlayerEntity player, String username) {
            super(player);
            this.username = username;
            this.setDisplayname(username);
        }

        public String getUsername()
        {
            return username;
        }

        public String getDisplayname()
        {
            return displayname;
        }

        public void setDisplayname(String displayname)
        {
            this.displayname = displayname;
        }
    }

    /**
     * Fired when the EntityPlayer is cloned, typically caused by the network sending a RESPAWN_PLAYER event.
     * Either caused by death, or by traveling from the End to the overworld.
     */
    public static class Clone extends PlayerEvent
    {
        private final PlayerEntity original;
        private final boolean wasDeath;

        public Clone(PlayerEntity _new, PlayerEntity oldPlayer, boolean wasDeath)
        {
            super(_new);
            this.original = oldPlayer;
            this.wasDeath = wasDeath;
        }

        /**
         * The old EntityPlayer that this new entity is a clone of.
         */
        public PlayerEntity getOriginal()
        {
            return original;
        }

        /**
         * True if this event was fired because the player died.
         * False if it was fired because the entity switched dimensions.
         */
        public boolean isWasDeath()
        {
            return wasDeath;
        }
    }

    /**
     * Fired when an Entity is started to be "tracked" by this player (the player receives updates about this entity, e.g. motion).
     *
     */
    public static class StartTracking extends PlayerEvent {

        private final Entity target;

        public StartTracking(PlayerEntity player, Entity target)
        {
            super(player);
            this.target = target;
        }

        /**
         * The Entity now being tracked.
         */
        public Entity getTarget()
        {
            return target;
        }
    }

    /**
     * Fired when an Entity is stopped to be "tracked" by this player (the player no longer receives updates about this entity, e.g. motion).
     *
     */
    public static class StopTracking extends PlayerEvent {

        private final Entity target;

        public StopTracking(PlayerEntity player, Entity target)
        {
            super(player);
            this.target = target;
        }

        /**
         * The Entity no longer being tracked.
         */
        public Entity getTarget()
        {
            return target;
        }
    }

    /**
     * The player is being loaded from the world save. Note that the
     * player won't have been added to the world yet. Intended to
     * allow mods to load an additional file from the players directory
     * containing additional mod related player data.
     */
    public static class LoadFromFile extends PlayerEvent {
        private final File playerDirectory;
        private final String playerUUID;

        public LoadFromFile(PlayerEntity player, File originDirectory, String playerUUID)
        {
            super(player);
            this.playerDirectory = originDirectory;
            this.playerUUID = playerUUID;
        }

        /**
         * Construct and return a recommended file for the supplied suffix
         * @param suffix The suffix to use.
         * @return
         */
        public File getPlayerFile(String suffix)
        {
            if ("dat".equals(suffix)) throw new IllegalArgumentException("The suffix 'dat' is reserved");
            return new File(this.getPlayerDirectory(), this.getPlayerUUID() +"."+suffix);
        }

        /**
         * The directory where player data is being stored. Use this
         * to locate your mod additional file.
         */
        public File getPlayerDirectory()
        {
            return playerDirectory;
        }

        /**
         * The UUID is the standard for player related file storage.
         * It is broken out here for convenience for quick file generation.
         */
        public String getPlayerUUID()
        {
            return playerUUID;
        }
    }
    /**
     * The player is being saved to the world store. Note that the
     * player may be in the process of logging out or otherwise departing
     * from the world. Don't assume it's association with the world.
     * This allows mods to load an additional file from the players directory
     * containing additional mod related player data.
     * <br>
     * Use this event to save the additional mod related player data to the world.
     *
     * <br>
     * <em>WARNING</em>: Do not overwrite the player's .dat file here. You will
     * corrupt the world state.
     */
    public static class SaveToFile extends PlayerEvent {
        private final File playerDirectory;
        private final String playerUUID;

        public SaveToFile(PlayerEntity player, File originDirectory, String playerUUID)
        {
            super(player);
            this.playerDirectory = originDirectory;
            this.playerUUID = playerUUID;
        }

        /**
         * Construct and return a recommended file for the supplied suffix
         * @param suffix The suffix to use.
         * @return
         */
        public File getPlayerFile(String suffix)
        {
            if ("dat".equals(suffix)) throw new IllegalArgumentException("The suffix 'dat' is reserved");
            return new File(this.getPlayerDirectory(), this.getPlayerUUID() +"."+suffix);
        }

        /**
         * The directory where player data is being stored. Use this
         * to locate your mod additional file.
         */
        public File getPlayerDirectory()
        {
            return playerDirectory;
        }

        /**
         * The UUID is the standard for player related file storage.
         * It is broken out here for convenience for quick file generation.
         */
        public String getPlayerUUID()
        {
            return playerUUID;
        }
    }

    /**
     * Fired when the world checks if a player is near enough to be attacked by an entity.
     * The resulting visibility modifier is multiplied by the one calculated by Minecraft (based on sneaking and more) and used to calculate the radius a player has to be in (targetDistance*modifier).
     * This can also be used to increase the visibility of a player, if it was decreased by Minecraft or other mods. But the resulting value cannot be higher than the standard target distance.
     */
    public static class Visibility extends PlayerEvent
    {

        private double visibilityModifier = 1D;

        public Visibility(PlayerEntity player)
        {
            super(player);
        }

        /**
         * @param mod Is multiplied with the current modifier
         */
        public void modifyVisibility(double mod)
        {
            visibilityModifier *= mod;
        }

        /**
         * @return The current modifier
         */
        public double getVisibilityModifier()
        {
            return visibilityModifier;
        }
    }

    public static class ItemPickupEvent extends PlayerEvent {
        /**
         * Original EntityItem with current remaining stack size
         */
        private final ItemEntity originalEntity;
        /**
         * Clone item stack, containing the item and amount picked up
         */
        private final ItemStack stack;
        public ItemPickupEvent(PlayerEntity player, ItemEntity entPickedUp, ItemStack stack)
        {
            super(player);
            this.originalEntity = entPickedUp;
            this.stack = stack;
        }

        public ItemStack getStack() {
            return stack;
        }

        public ItemEntity getOriginalEntity() {
            return originalEntity;
        }
    }

    public static class ItemCraftedEvent extends PlayerEvent {
        @Nonnull
        private final ItemStack crafting;
        private final IInventory craftMatrix;
        public ItemCraftedEvent(PlayerEntity player, @Nonnull ItemStack crafting, IInventory craftMatrix)
        {
            super(player);
            this.crafting = crafting;
            this.craftMatrix = craftMatrix;
        }

        @Nonnull
        public ItemStack getCrafting()
        {
            return this.crafting;
        }

        public IInventory getInventory()
        {
            return this.craftMatrix;
        }
    }

    public static class ItemSmeltedEvent extends PlayerEvent {
        @Nonnull
        private final ItemStack smelting;
        public ItemSmeltedEvent(PlayerEntity player, @Nonnull ItemStack crafting)
        {
            super(player);
            this.smelting = crafting;
        }

        @Nonnull
        public ItemStack getSmelting()
        {
            return this.smelting;
        }
    }

    public static class PlayerLoggedInEvent extends PlayerEvent {
        public PlayerLoggedInEvent(PlayerEntity player)
        {
            super(player);
        }
    }

    public static class PlayerLoggedOutEvent extends PlayerEvent {
        public PlayerLoggedOutEvent(PlayerEntity player)
        {
            super(player);
        }
    }

    public static class PlayerRespawnEvent extends PlayerEvent {
        private final boolean endConquered;

        public PlayerRespawnEvent(PlayerEntity player, boolean endConquered)
        {
            super(player);
            this.endConquered = endConquered;
        }

        /**
         * Did this respawn event come from the player conquering the end?
         * @return if this respawn was because the player conquered the end
         */
        public boolean isEndConquered()
        {
            return this.endConquered;
        }


    }

    public static class PlayerChangedDimensionEvent extends PlayerEvent {
        private final RegistryKey<World> fromDim;
        private final RegistryKey<World> toDim;
        public PlayerChangedDimensionEvent(PlayerEntity player, RegistryKey<World> fromDim, RegistryKey<World> toDim)
        {
            super(player);
            this.fromDim = fromDim;
            this.toDim = toDim;
        }

        public RegistryKey<World> getFrom()
        {
            return this.fromDim;
        }

        public RegistryKey<World> getTo()
        {
            return this.toDim;
        }
    }
}
