package net.minecraftforge.test;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.EntitySpawnPlacementHelper;
import net.minecraftforge.common.EntitySpawnPlacementHelper.ISpawnPlacementHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;

@Mod(modid="SpawnPlacementHandlerTest", name="SpawnPlacementHandlerTest", version="0.0.0")
public class SpawnPlacementHandlerTest {

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        EntityRegistry.registerModEntity(EntityFrog.class, "Frog", 0, this, 80, 3, true);
        EntityRegistry.addSpawn(EntityFrog.class, 100, 1, 1, EnumCreatureType.WATER_CREATURE, BiomeGenBase.swampland);
        EntityRegistry.addSpawn(EntityFrog.class, 100, 1, 1, EnumCreatureType.CREATURE, BiomeGenBase.swampland);
        EntitySpawnPlacementHelper.addSpawnPlacementType(EntityFrog.class, SpawnPlacementType.ON_GROUND);
        EntitySpawnPlacementHelper.addSpawnPlacementType(EntityFrog.class, SpawnPlacementType.IN_WATER);

        EntityRegistry.registerModEntity(EntityFlyingBox.class, "FlyingBox", 0, this, 80, 3, true);
        EntityRegistry.addSpawn(EntityFlyingBox.class, 100, 1, 1, EnumCreatureType.AMBIENT, BiomeGenBase.swampland);
        EntitySpawnPlacementHelper.addSpawnPlacementHandler(EntityFlyingBox.class, EntitySpawnPlacementHelper.ALWAYS_TRUE_HANDLER);
    }
    
    public static class EntityFrog extends EntityWaterMob {

        public EntityFrog(World worldIn)
        {
            super(worldIn);
            setSize(1f, 1f);
        }
        
        @Override
        public void onEntityUpdate()
        {
            super.onEntityUpdate();
            setAir(300);
        }

        @Override
        public boolean isCreatureType(EnumCreatureType type, boolean forSpawnCount)
        {
            return type == EnumCreatureType.AMBIENT;
        }
        
    }
    
    public static class EntityFlyingBox extends EntityLiving {

        public EntityFlyingBox(World worldIn)
        {
            super(worldIn);
            setSize(1f, 1f);
        }

        @Override
        public boolean isCreatureType(EnumCreatureType type, boolean forSpawnCount)
        {
            return type == EnumCreatureType.AMBIENT;
        }

    }

}
