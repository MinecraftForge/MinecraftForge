/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Is fired from {@link net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement.Placer#tryPlacingChildren}
 * for each generated Jigsaw block after it stores the pool of Jigsaw pieces in can attach to.
 * <br><br>
 * Adding a custom villager's house to plains village and zombie village can be just:
 *
 * {@code
 *    String pool = event.patternLocation.getPath();
 *    if (pool.startsWith("village/plains/houses") || pool.startsWith("village/plains/zombie/houses"))
 *      event.pieces.add(StructurePoolElement.single("namespace:path"));
 * }
 * <br><br>
 * For more detailed insight see <a href=https://mcforge.readthedocs.io/en/1.18.x/events/jigsawevent/>here.</a>
 */
@Cancelable
public class JigsawPieceEvent extends Event
{
   private final List<StructurePoolElement> pieces;
   private final ResourceLocation patternLocation;
   private final BlockPos jigsawBlockPosition;
   private final Map<ResourceLocation, Object> jigsawPlacerSettings;
   private final Registry<StructureTemplatePool> patternRegistry;
   private final Random random;
   private final int currentDepth;

   /**
    * @param pieces                  The list of pieces that have a probability of being placed. Any addition to the pool should be shuffled for better results.
    *                                The list does NOT contain the fallback pool pieces contained in {@link StructureTemplatePool#fallback} to avoid infinite
    *                                generation problems. See the appropriate pool for the structure to know what the fallback is.
    *                                ({@link net.minecraft.data.worldgen.PlainVillagePools} for Plains Village)
    * @param patternLocation         The resource location of the {@link StructureTemplatePool}.
    * @param jigsawBlockPosition     The block position of jigsaw block requesting the pieces.
    * @param jigsawPlacerSettings    The map of generate-time settings for specific structure. Unused for vanilla structures but can be used
    *                                in modded ones to add specific settings or more or less randomness. If  {@code forge:force_piece_placement} is set
    *                                to {@code true}, all future pieces in that structure will always be placed, even if they overwrite existing pieces.
    * @param patternRegistry         The access to {@link Registry} instance for structure pools. Can be used to get pools of other structures and re-use them.
    * @param random                  The access to {@link Random} instance of specific structure. Can be used to shuffle custom pieces after they are added.
    * @param currentDepth            The current depth of the piece that is going to be placed. It will be 0 for the jigsaw blocks contained in the starting piece.
    */
   public JigsawPieceEvent(List<StructurePoolElement> pieces, ResourceLocation patternLocation, BlockPos jigsawBlockPosition, Map<ResourceLocation, Object> jigsawPlacerSettings, Registry<StructureTemplatePool> patternRegistry, Random random, int currentDepth)
   {
      this.pieces = pieces;
      this.patternLocation = patternLocation;
      this.jigsawBlockPosition = jigsawBlockPosition;
      this.jigsawPlacerSettings = jigsawPlacerSettings;
      this.patternRegistry = patternRegistry;
      this.random = random;
      this.currentDepth = currentDepth;
   }

   /**
    * Gets the list of pieces that have a probability of being placed.
    * <br><br>
    * Any addition to the pool should be shuffled for better results.
    * <br><br>
    * The list does NOT contain the fallback pool pieces contained in {@link StructureTemplatePool#fallback} to
    * avoid infinite generation problems. See the appropriate pool for the structure to know what the fallback is.
    * ({@link net.minecraft.data.worldgen.PlainVillagePools} for Plains Village)
    */
   public List<StructurePoolElement> getPieces()
   {
      return pieces;
   }

   /**
    * Gets the resource location of the {@link StructureTemplatePool}.
    */
   public ResourceLocation getPatternLocation ()
   {
      return patternLocation;
   }

   /**
    * Gets the block position of jigsaw block requesting the pieces.
    */
   public BlockPos getJigsawBlockPosition() {
      return jigsawBlockPosition;
   }

   /**
    * Gets map of generate-time settings for specific structure.
    * <br><br>
    * Unused for vanilla structures but can be used in modded ones to add specific settings or more or less randomness.
    * <br><br>
    * If  {@code forge:force_piece_placement} is set to {@code true}, all future pieces in that structure will always be placed, even if they overwrite existing pieces.
    */
   public Map<ResourceLocation, Object> getJigsawPlacerSettings() {
      return jigsawPlacerSettings;
   }

   /**
    * Gets the {@link Registry} instance for structure pools.
    * Can be used to get pools of other structures and re-use them.
    */
   public Registry<StructureTemplatePool> getPatternRegistry() {
      return patternRegistry;
   }

   /**
    * Gets the {@link Random} instance of specific structure.
    * Can be used to shuffle custom pieces after they are added.
    */
   public Random getRandom() {
      return random;
   }

   /**
    * Gets the current depth of the piece that is going to be placed.
    * It will be 0 for the jigsaw blocks contained in the starting piece.
    */
   public int getCurrentDepth() {
      return currentDepth;
   }
}
