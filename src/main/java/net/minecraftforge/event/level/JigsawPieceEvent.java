package net.minecraftforge.event.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Is fired from {@link net.minecraft.world.level.levelgen.feature.structures.JigsawPlacement.Placer#tryPlacingChildren}
 * for each generated Jigsaw block after it stores the pool of Jigsaw pieces in can attach to.
 * The list does NOT contain the pieces contained in {@link net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool#fallback}
 * to avoid infinite generation problems.
 * <br><br>
 * Adding a custom villager's house to plains village and zombie village can be just:
 *
 * {@code
 *    String pool = event.patternLocation.getPath();
 *    if (pool.startsWith("village/plains/houses") || pool.startsWith("village/plains/zombie/houses"))
 *      event.pieces.add(StructurePoolElement.single("namespace:path"));
 * }
 *
 * For more detailed insight see <a href=https://mcforge.readthedocs.io/en/1.16.x/events/jigsawevent/>here.</a>
 */
@Cancelable
public class JigsawPieceEvent extends Event
{
   public final List<StructurePoolElement> pieces;
   public final ResourceLocation patternLocation;
   public final BlockPos jigsawBlockPosition;
   public final Map<ResourceLocation, Object> jigsawPlacerSettings;
   public final Registry<StructureTemplatePool> patternRegistry;
   public final Random random;
   public final int currentDepth;

   /**
    * @param pieces                  The list of pieces that have a probability of being placed. Any addition to big pools should be shuffled for better results.
    *                                The list does NOT contain the fallback pool of the current pool. See the appropriate pool for the structure to know what the fallback is.
    *                                ({@link net.minecraft.data.worldgen.PlainVillagePools} for Plains Village)
    * @param patternLocation         The resource location of the {@link net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool}.
    * @param jigsawBlockPosition     The block position of jigsaw block requesting the pieces.
    * @param jigsawPlacerSettings    The map of generate-time settings for specific structure. It's unused for vanilla structures but can be used
    *                                in modded ones to add specific settings or more or less randomness. If `forge:force_piece_placement` is set to `true`,
    *                                all future pieces in that structure will always be placed, even if they overwrite existing pieces.
    * @param patternRegistry         The access to Registry instance for structure pools. Can be used to get pools of other structures and re-use them.
    * @param random                  The access to Random instance of specific structure. Can be used to shuffle custom pieces after they are added.
    * @param currentDepth            The depth of the piece that is going to be placed. It will be 0 for the jigsaw blocks contained in the starting piece.
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
}
