package net.minecraftforge.common;

import net.minecraft.src.Entity;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

/**
 * Allows the mods to intercept or monitor the entity/tileentity updates(ticks).
 * These methods are called very often. make sure you don't do anything repeating, unnecessary.
 * 
 * The return values define if the entity should be updated or not. or if the ticks should happen at all.
 * 
 * Return value TRUE means to stop handling the entity
 */
public interface IEntityTickHandler
{
        public abstract boolean livingEntityTickStart(World w, Entity entity);
        public abstract void livingEntityTickEnd(World w, Entity entity);

        public abstract boolean tileEntityTickStart(World w, TileEntity entity);
        public abstract void tileEntityTickEnd(World w, TileEntity entity);
        
        public abstract boolean tickStart(World w);
        public abstract void tickEnd(World w);
}
