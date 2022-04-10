/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.world;

import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraftforge.event.world.JigsawPieceEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod("jigsaw_event_test")
@Mod.EventBusSubscriber(modid = "jigsaw_event_test")
public class JigsawPieceEventTest
{
   @SubscribeEvent
   public static void jigsawEvent(JigsawPieceEvent event)
   {
      // Replace iron golem with giant in all villages
      if (event.getPatternLocation().getPath().equals("village/common/iron_golem"))
      {
         event.getPieces().clear();
         event.getPieces().add(StructurePoolElement.single("jigsaw_event_test:giant").apply(StructureTemplatePool.Projection.RIGID));
      }

      // Replace at most one house in every plains village with cube of diamond blocks
      // Force diamond house to always generate, even if this will overwrite existing piece
      boolean isGenerated = (boolean) event.getJigsawPlacerSettings().getOrDefault(new ResourceLocation("jigsaw_event_test:generated"), false);
      if (event.getPatternLocation().getPath().equals("village/plains/houses") && !isGenerated)
      {
         event.getPieces().clear();
         event.getPieces().add(StructurePoolElement.single("jigsaw_event_test:diamond_house").apply(StructureTemplatePool.Projection.RIGID));

         event.getJigsawPlacerSettings().put(new ResourceLocation("jigsaw_event_test:generated"), true);
         event.getJigsawPlacerSettings().put(new ResourceLocation("forge:force_piece_placement"), true);
      } else {
         event.getJigsawPlacerSettings().put(new ResourceLocation("forge:force_piece_placement"), false);
      }

      // After 5 villagers have been generated in taiga village, replace all future villagers in that village with zombie villagers
      int numberOfVillagers = (int) event.getJigsawPlacerSettings().getOrDefault(new ResourceLocation("jigsaw_event_test:villagers"), 0);
      if (event.getPatternLocation().getPath().equals("village/taiga/villagers") && numberOfVillagers <= 5)
      {
         event.getPieces().clear();
         event.getPieces().add(StructurePoolElement.single("village/plains/zombie/villagers/baby").apply(StructureTemplatePool.Projection.RIGID));
         event.getPieces().add(StructurePoolElement.single("village/plains/zombie/villagers/nitwit").apply(StructureTemplatePool.Projection.RIGID));
         event.getPieces().add(StructurePoolElement.single("village/plains/zombie/villagers/nitwit").apply(StructureTemplatePool.Projection.RIGID));
         event.getJigsawPlacerSettings().put(new ResourceLocation("jigsaw_event_test:villagers"), numberOfVillagers + 1);
      }

      // Replace savanna houses on y>=70 with desert ones
      if (event.getPatternLocation().getPath().equals("village/savanna/houses") && event.getJigsawBlockPosition().getY() >= 70)
      {
         Optional<StructureTemplatePool> jigsawpattern = event.getPatternRegistry().getOptional(new ResourceLocation("village/desert/houses"));
         event.getPieces().clear();
         event.getPieces().addAll(jigsawpattern.get().getShuffledTemplates(event.getRandom()));
      }
   }
}
