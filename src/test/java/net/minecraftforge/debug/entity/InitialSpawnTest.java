package net.minecraftforge.debug.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("initial_spawn_test")
@Mod.EventBusSubscriber()
public class InitialSpawnTest 
{
    static boolean ENABLE = false;
	
    public InitialSpawnTest()
    {
		
    }
    
    @SubscribeEvent
    public static void doStuff(LivingSpawnEvent.InitialSpawn event) 
    {
      if (event.getEntity() instanceof CowEntity && ENABLE)
      {
       CowEntity cow = (CowEntity)event.getEntity();
       SheepEntity sheep = EntityType.SHEEP.create(cow.world);
       sheep.copyLocationAndAnglesFrom(cow);
       cow.startRiding(sheep);
       event.getWorld().addEntity(sheep);
      }
    }
}
