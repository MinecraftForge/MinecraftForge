package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.util.BlockPos;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;

/**
 * <p>This class did the same thing as vanilla {@link EntitySpawnPlacementRegistry}.</p>
 * 
 * <p>Entity spawn placement is checked before entity creation. And is also before a
 * {@link net.minecraft.entity.EntityLiving#getCanSpawnHere} check.<p>
 * 
 * @author Herbix
 * 
 */
public class EntitySpawnPlacementHelper {

    private static Multimap<Class<? extends Entity>, ISpawnPlacementHandler> placementRegistry = HashMultimap.create();
    
    private static ISpawnPlacementHandler[] SPAWN_PLACEMENT_TYPE_HANDLERS = new ISpawnPlacementHandler[SpawnPlacementType.values().length];

    static {
        for(SpawnPlacementType type : SpawnPlacementType.values())
        {
            SPAWN_PLACEMENT_TYPE_HANDLERS[type.ordinal()] = new SpawnPlacementTypeWarpper(type);
        }
    }

    /**
     * Use {@code EntitySpawnPlacementHelper.addSpawnPlacementHandler(xx.class,
     * EntitySpawnPlacementHelper.ALWAYS_TRUE_HANDLER)} to disable spawn placement check
     * and you could do it by overriding {@link net.minecraft.entity.EntityLiving#getCanSpawnHere}.
     */
    public static ISpawnPlacementHandler ALWAYS_TRUE_HANDLER = new ISpawnPlacementHandler() {
        @Override
        public boolean canCreatureSpawnAtLocation(Class<? extends Entity> entityClass, World worldIn, BlockPos pos)
        {
            return true;
        }
    };
    
    /**
     * This is a similar method to {@link EntitySpawnPlacementRegistry#setPlacementType},
     * but multiple placement type can be added here. If one of these type passes the
     * spawn placement check, this type of entity might spawn here.<br>
     * If you only need one placement type, {@link EntitySpawnPlacementRegistry#setPlacementType}
     * is preferred.
     * @param entityClass   Entity type
     * @param placementType Placement type
     */
    public static void addSpawnPlacementType(Class<? extends Entity> entityClass, SpawnPlacementType placementType)
    {
        addSpawnPlacementHandler(entityClass, SPAWN_PLACEMENT_TYPE_HANDLERS[placementType.ordinal()]);
    }
    
    /**
     * Handlers could be specify here to do the spawn placement check. If one of them
     * passes the check, this type of entity might spawn here.
     * @param entityClass   Entity type
     * @param handler       The handler
     * @see EntitySpawnPlacementHelper#addSpawnPlacementType
     */
    public static void addSpawnPlacementHandler(Class<? extends Entity> entityClass, ISpawnPlacementHandler handler)
    {
        placementRegistry.put(entityClass, handler);
    }
    
    /**
     * A hook called by {@link SpawnerAnimals#findChunksForSpawning}.
     * @param entityClass   Entity type
     * @param worldIn       World
     * @param pos           Spawning position
     * @return              Whether the creature could spawn here
     */
    public static boolean canCreatureSpawnAtLocation(Class<? extends Entity> entityClass, World worldIn, BlockPos pos)
    {
        if (!worldIn.getWorldBorder().contains(pos))
        {
            return false;
        }
        SpawnPlacementType type = EntitySpawnPlacementRegistry.func_180109_a(entityClass);
        if (placementRegistry.containsKey(entityClass)) {
            for (ISpawnPlacementHandler handler : placementRegistry.get(entityClass))
            {
                if (handler.canCreatureSpawnAtLocation(entityClass, worldIn, pos))
                {
                    return true;
                }
            }
            if (type == null)
            {
                return false;
            }
        }
        return SpawnerAnimals.canCreatureTypeSpawnAtLocation(type, worldIn, pos);
    }

    /**
     * @see EntitySpawnPlacementHelper#addSpawnPlacementHandler
     */
    public interface ISpawnPlacementHandler {
        /**
         * @see EntitySpawnPlacementHelper#addSpawnPlacementHandler
         */
        public boolean canCreatureSpawnAtLocation(Class<? extends Entity> entityClass, World worldIn, BlockPos pos);
    }
    
    private static class SpawnPlacementTypeWarpper implements ISpawnPlacementHandler {
        
        private SpawnPlacementType type;

        public SpawnPlacementTypeWarpper(SpawnPlacementType type)
        {
            this.type = type;
        }
        
        @Override
        public boolean canCreatureSpawnAtLocation(Class<? extends Entity> entityClass, World worldIn, BlockPos pos)
        {
            return SpawnerAnimals.canCreatureTypeSpawnAtLocation(type, worldIn, pos);
        }
        
    }
}
