package net.minecraftforge.event.world;

import net.minecraft.src.*;
import net.minecraftforge.event.*;

public class InitMapGenEvent extends Event
{
    public static class Cave extends InitMapGenEvent
    {
        public final MapGenBase originalCaveGen; 
        public MapGenBase newGen;
        
        public Cave(MapGenBase original)
        {
            originalCaveGen = original;
            newGen = original;
        }
    }

    public static class NetherCave extends InitMapGenEvent
    {
        public final MapGenBase originalNetherCaveGen; 
        public MapGenBase newGen;
        
        public NetherCave(MapGenBase original)
        {
            originalNetherCaveGen = original;
            newGen = original;
        }
    }

    public static class Ravine extends InitMapGenEvent
    {
        public final MapGenBase originalRavineGen; 
        public MapGenBase newGen;
        
        public Ravine(MapGenBase original)
        {
            originalRavineGen = original;
            newGen = original;
        }
    }

    public static class Stronghold extends InitMapGenEvent
    {
        public final MapGenStronghold originalStrongholdGen; 
        public MapGenStronghold newGen;
        
        public Stronghold(MapGenStronghold original)
        {
            originalStrongholdGen = original;
            newGen = original;
        }
    }

    public static class Village extends InitMapGenEvent
    {
        public final MapGenVillage originalVillageGen; 
        public MapGenVillage newGen;
        
        public Village(MapGenVillage original)
        {
            originalVillageGen = original;
            newGen = original;
        }
    }

    public static class Mineshaft extends InitMapGenEvent
    {
        public final MapGenMineshaft originalMineshaftGen; 
        public MapGenMineshaft newGen;
        
        public Mineshaft(MapGenMineshaft original)
        {
            originalMineshaftGen = original;
            newGen = original;
        }
    }

    public static class ScatteredFeature extends InitMapGenEvent
    {
        public final MapGenScatteredFeature originalScatteredFeatureGen; 
        public MapGenScatteredFeature newGen;
        
        public ScatteredFeature(MapGenScatteredFeature original)
        {
            originalScatteredFeatureGen = original;
            newGen = original;
        }
    }
    
   public static class NetherBridge extends InitMapGenEvent
   {
       public final MapGenNetherBridge originalNetherBridgeGen; 
       public MapGenNetherBridge newGen;
       
       public NetherBridge(MapGenNetherBridge original)
       {
           originalNetherBridgeGen = original;
           newGen = original;
       }
   }
}
