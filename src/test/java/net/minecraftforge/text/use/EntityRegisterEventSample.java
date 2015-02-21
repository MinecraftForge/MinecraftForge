package net.minecraftforge.text.use;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityRegisterEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;

@Mod(modid = EntityRegisterEventSample.MODID, version = EntityRegisterEventSample.VERSION)
public class EntityRegisterEventSample {

    public static final String MODID = "ForgeSampleEntityRegisterEvent";
    public static final String VERSION = "1.0";
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
         MinecraftForge.EVENT_BUS.register(this);
         
         EntityRegistry.registerModEntity(TestEntity.class, "TestEntity", 0, this, 20, 10, true);
         EntityRegistry.registerGlobalEntityID(TestEntity2.class, "TestEntity2", 220);
    }
    
    @SubscribeEvent
    public void onModEntityRegistered(EntityRegisterEvent.EntityModRegisteredEvent event)
    {
        System.out.println("Mod Entity Registered with Class: " + event.entityClass + " with Name: " + event.entityName + " with ID: " + event.id + "with Mod: " + event.mod);
    }
    
    @SubscribeEvent
    public void onGlobalEntityRegistered(EntityRegisterEvent.EntityGlobalRegisteredEvent event)
    {
        System.out.println("Global Entity Registered with Class: " + event.entityClass + " with Name: " + event.entityName + " with ID: " + event.id);
    }
    
    public class TestEntity extends EntityZombie
    {
        public TestEntity(World worldIn)
        {
            super(worldIn);
        }
    }
    
    public class TestEntity2 extends EntityZombie
    {
        public TestEntity2(World worldIn)
        {
            super(worldIn);
        }
    }
}
