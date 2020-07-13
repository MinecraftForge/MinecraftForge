package net.minecraftforge.event.world;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Is fired from {@link net.minecraft.world.gen.feature.jigsaw.JigsawManager.Assembler#tryPlacingChildren}
 * for each generated Jigsaw block after it stores the pool of Jigsaw pieces in can attach to.
 * The list does NOT contain the pieces contained in {@link net.minecraft.world.gen.feature.jigsaw.JigsawPattern#fallback}
 * to avoid infinite generation problems.
 * <br><br>
 * Adding a custom villager's house to plains village and zombie village can be just:
 *
 * {@code
 *    String pool = event.patternLocation.getPath();
 *    if (pool.startsWith("village/plains/houses") || pool.startsWith("village/plains/zombie/houses"))
 *      event.pieces.add(new SingleJigsawPiece("namespace:path"));
 * }
 *
 * For more detailed insight see <a href=https://mcforge.readthedocs.io/en/1.16.x/events/jigsawevent/>here.</a>
 */
@Cancelable
public class JigsawPieceEvent extends Event
{
   public final List<JigsawPiece> pieces;
   public final ResourceLocation patternLocation;
   public final BlockPos jigsawBlockPosition;
   public final Map<ResourceLocation, Object> jigsawAssemblerSettings;
   public final Registry<JigsawPattern> patternRegistry;
   public final Random random;
   public final int currentDepth;

   /**
    * @param pieces                  The list of pieces that have a probability of being placed. Any addition to big pools should be shuffled for better results.
    *                                The list does NOT contain the fallback pool of the current pool. See the appropriate pool for the structure to know what the fallback.
    *                                ({@link net.minecraft.world.gen.feature.structure.PlainsVillagePools} for Plains Village)
    * @param patternLocation         The resource location of the {@link net.minecraft.world.gen.feature.jigsaw.JigsawPattern}.
    * @param jigsawBlockPosition     The block position of jigsaw block requesting the pieces.
    * @param jigsawAssemblerSettings The map of generate-time settings for specific structure. It's unused for vanilla structures but can be used
    *                                in modded ones to add specific settings or more or less randomness. If `forge:force_piece_placement` is set to `true`,
    *                                all future pieces in that structure will always be placed, even if they overwrite existing pieces.
    * @param patternRegistry         The access to Registry instance for structure pools. Can be used to get pools of other structures and re-use them.
    * @param random                  The access to Random instance of specific structure. Can be used to shuffle custom pieces after they are added.
    * @param currentDepth            The depth of the piece that is going to be placed. It will be 0 for the jigsaw blocks contained in the starting piece.
    */
   public JigsawPieceEvent(List<JigsawPiece> pieces, ResourceLocation patternLocation, BlockPos jigsawBlockPosition, Map<ResourceLocation, Object> jigsawAssemblerSettings, Registry<JigsawPattern> patternRegistry, Random random, int currentDepth)
   {
      this.pieces = pieces;
      this.patternLocation = patternLocation;
      this.jigsawBlockPosition = jigsawBlockPosition;
      this.jigsawAssemblerSettings = jigsawAssemblerSettings;
      this.patternRegistry = patternRegistry;
      this.random = random;
      this.currentDepth = currentDepth;
   }
}
