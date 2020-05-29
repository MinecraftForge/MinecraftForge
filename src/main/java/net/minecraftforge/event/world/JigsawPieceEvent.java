package net.minecraftforge.event.world;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;

/**
 * Allows to add jigsaw pieces to
 */
public class JigsawPieceEvent extends Event
{
   public List<JigsawPiece> pieces;
   public final StructureType generalType;
   public final String specificType;
   public final boolean isZombie;

   public JigsawPieceEvent(List<JigsawPiece> pieces, ResourceLocation patternLocation)
   {
      this.pieces = pieces;
      String pool = patternLocation.getPath();
      pool = pool.replace("village/", "");
      this.generalType = fromString(pool);
      pool = pool.replace(this.generalType.name().toLowerCase()+"/", "");
      this.isZombie = pool.contains("zombie/");
      this.specificType = pool.replace("zombie/", "");;
   }

   public enum StructureType
   {
      PLAINS,
      DESERT,
      SNOWY,
      TAIGA,
      SAVANNA,
      GRASS,
      COMMON,
      PILLAGER_OUTPOST,
      MODDED //If cant find the path, it is due to a mod using the JigsawManager
   }

   private static StructureType fromString(String name)
   {
      for(StructureType type : StructureType.values())
      {
         if(name.startsWith(type.name().toLowerCase()))
         {
            return type;
         }
      }
      return StructureType.MODDED;
   }
}
