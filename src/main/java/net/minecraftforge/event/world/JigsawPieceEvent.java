package net.minecraftforge.event.world;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;

/**
 * Is fired from {@link net.minecraft.world.gen.feature.jigsaw.JigsawManager.Assembler#tryPlacingChildren}
 * It is fired for each generated Jigsaw block after it stores the pool of Jigsaw pieces in can attach to.
 * The list does NOT contain the pieces contained in {@link net.minecraft.world.gen.feature.jigsaw.JigsawPattern#fallback}
 * This is to avoid infinite generation problems, for the fallbacks are the empty/end Jigsaw pieces.
 * <br><br>
 * Adding a custom villager's house can be just:
 *
 * {@code
 *    if(event.specificType.equals("houses"))
 *       event.pieces.add(new SingleJigsawPiece("namespace:path"));
 * }
 *
 * For more detailed insight see <a href=https://mcforge.readthedocs.io/en/1.15.x/events/jigsawevent/>here.</a>
 */
public class JigsawPieceEvent extends Event
{
   //These can be looked at under data/minecraft/structures/village (or /pillager_outpost). All correspond to the folder name except
   // "empty", which indicates an empty pattern, and "modded", which indicates the path was not found.
   public static final List<String> STRUCTURE_TYPES = ImmutableList.of("plains", "desert", "snowy", "taiga", "savanna", "decays", "common", "pillager_outpost", "empty", "modded");

   public List<JigsawPiece> pieces;
   public final String generalType;
   public final String specificType;
   public final boolean isZombie;
   public final int currentDepth;

   /**
    *
    * @param pieces           The list of pieces that have a probability of being placed. Any addition to big pools should be shuffled for better results.
    *                         The list does NOT contain the fallback pool of the current pool. See the appropriate
    *                         VillagePools for the biome to know what the fallback. ({@link net.minecraft.world.gen.feature.structure.PlainsVillagePools} for Plains)
    * @param currentDepth     The depth of the piece that is going to be placed. It will be 0 for the jigsaw blocks contained in the starting piece.
    * @param patternLocation  The location of the {@link net.minecraft.world.gen.feature.jigsaw.JigsawPattern}
    *                         It is decomposed into {@link #generalType}, {@link #specificType} and {@link #isZombie}.
    */
   public JigsawPieceEvent(List<JigsawPiece> pieces,  int currentDepth, ResourceLocation patternLocation)
   {
      this.pieces = pieces;
      this.currentDepth = currentDepth;
      String pool = patternLocation.getPath();
      pool = pool.replace("village/", "");
      this.generalType = fromString(pool);
      pool = pool.replace(this.generalType+"/", "");
      this.specificType = pool.replace("zombie/", "");;
      this.isZombie = pool.contains("zombie/");
   }

   private static String fromString(String name)
   {
      for(String type : STRUCTURE_TYPES)
      {
         if(name.startsWith(type))
         {
            return type;
         }
      }
      return "modded";
   }
}
