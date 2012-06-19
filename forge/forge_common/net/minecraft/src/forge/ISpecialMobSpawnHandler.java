package net.minecraft.src.forge;

import net.minecraft.src.EntityLiving;
import net.minecraft.src.World;

@Deprecated //See IEntityLivingHandler
public interface ISpecialMobSpawnHandler 
{
    /**
     * Raised when a Entity is spawned into the world from natural means, meaning 
     * not by command, MobSpawner, cheat, etc.. Just naturally throughout the world.
     * 
     * This allows the mod to create special functionality that runs on a mob natural 
     * spawn. The Vanilla minecraft mechanic of having 'Spider Jockies', the color of 
     * sheep's wool, and Ocelot's spawning with babies can be canceled by returning 
     * true from this function
     * 
     * Returning true will indicate that you have performed your special spawning, 
     * and no more handling will be done. 
     * 
     * @param entity The newly spawned entity
     * @param world The world the entity is in
     * @param x The Entitie's X Position
     * @param y The Entitie's Y Position
     * @param z The Entitie's Z Position
     * @return True to prevent any further special case handling from executing.
     */
    public boolean onSpecialEntitySpawn(EntityLiving entity, World world, float x, float y, float z);
}
