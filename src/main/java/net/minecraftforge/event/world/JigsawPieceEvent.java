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
 * To get an understanding of the JigsawPatterns pools go here: {@link net.minecraft.world.gen.feature.structure.PlainsVillagePools} or, for a more
 * readable version, <a href=https://gist.github.com/Cyborgmas/0aea49f1cc940abf06da3443c65e1678>here.</a>
 * Pools are used directly once, on creation. For villages it is the town_centers pool. Other than that exception, pools are ALWAYS called from jigsaw blocks
 * inside the jigsaw pieces. It is whent that happens that this event fires. This also means that there is NO WAY to modify the starting pool using this event.
 * The best option to modify a starting pool would be to add a new {@link net.minecraft.world.gen.feature.structure.VillageStructure}
 * to {@link net.minecraft.world.biome.PlainsBiome} with a different {@link net.minecraft.world.gen.feature.structure.VillageConfig}
 * The config is what specifies the starting pool and the maximum depth of the jigsaw creation.
 * <br><br>
 * Modifying this event can provide a LOT of unexpected consequences.
 * Jigsaw pieces will contain other jigsaw blocks with their appropriate pools and correct attachments.
 * When modifying "end jigsaws", like houses, villagers, animals and decors, the only issue is to have the custom jigsaw piece
 * have one {@link net.minecraft.block.JigsawBlock} with the correct {@link net.minecraft.tileentity.JigsawTileEntity#getAttachmentType()}.
 * Otherwise it will NEVER attach. An other thing to consider is the volume of the piece that is getting placed, a discrepancy might produce weird results.
 * <br><br>
 * More care must be had when modifying pieces that will determine the layout of the village by providing more than one jigsaw blocks that have a pool
 * of non-terminal pieces. (streets for example). When providing a custom street, jigsawblocks must be also layed out in
 * the correct direction and have an appropriate attachment type. This attachment type can be a custom one to have a completely custom pool.
 * However, this "ends" the vanilla jigsaw pattern in that direction and the village will not grow more unless the custom pool does so on its own.
 * To avoid this issue in a relatively simple manner, the {@link JigsawPieceEvent#currentDepth} is provided. This allows to only modify a pool when the village
 * has grown out enough. The max depth is usually six, it can be found in the appropriate {@link net.minecraft.world.gen.feature.structure.VillageConfig} of the biome.
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
