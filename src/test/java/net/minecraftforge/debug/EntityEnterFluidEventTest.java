package net.minecraftforge.debug;

import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(name = EntityEnterFluidEventTest.MODNAME, modid = EntityEnterFluidEventTest.MODID, version = "1.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class EntityEnterFluidEventTest
{

    public static final String MODID = "entityenterfluideventtest";
    public static final String MODNAME = "Entity Enter Fluid Event Test";
    public static final boolean ENABLED = false;

   @SubscribeEvent
   public static void onEntityEnterFluidEvent(EntityEvent.EnterFluidEvent event)
   {
       if (ENABLED)
       {
           event.setCanceled(true);
       }
   }

}
