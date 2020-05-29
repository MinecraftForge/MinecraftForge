package net.minecraftforge.debug.village;

import net.minecraft.world.gen.feature.jigsaw.SingleJigsawPiece;
import net.minecraftforge.event.world.JigsawPieceEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("jigsaw_event_test")
@Mod.EventBusSubscriber(modid = "jigsaw_event_test")
public class JigsawEventTest {
   @SubscribeEvent
   public static void jigsawEvent(JigsawPieceEvent event){
      if(event.generalType == JigsawPieceEvent.StructureType.COMMON){
         if(event.specificType.equals("iron_golem")){
            event.pieces.clear();
            event.pieces.add(new SingleJigsawPiece("jigsaw_event_test:wither"));
         }
      }
   }
}
