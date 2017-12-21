package net.minecraftforge.debug;

import com.google.common.collect.Lists;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkGeneratorEnd;
import net.minecraft.world.gen.ChunkGeneratorHell;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraftforge.common.MapGenStructureManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.InitStructureGensEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * Test the feature to add custom MapGenStructures to the ChunkGenerators.
 * To verify the function:
 * 1) Enable this mod
 * 2) Generate a new world
 * 3) Tp to 64 * 64
 * 4) run "/locate forge-test-structure"
 * 5) Check the console for result
 * 6) Repeat in Hell and End
 *
 * You should also notice that Blaze are spawning (at least during the night and on suitable terrain)
 */
@Mod(modid = StructureGenTest.MODID, name = StructureGenTest.MODID, version = "1.0")
public class StructureGenTest
{
    public final static String MODID = "structure_gen_test";
    private final static boolean ENABLED = false;
    private static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent evt)
    {
        logger = evt.getModLog();

        if (ENABLED)
        {
            MapGenStructureIO.registerStructure(Start.class, "forge-test-structure");
            MinecraftForge.TERRAIN_GEN_BUS.register(this);

        }
    }

    @SubscribeEvent
    public void onCollectStructuresOverworld(InitStructureGensEvent<ChunkGeneratorOverworld> event)
    {
        logger.info("Collecting structures for Overworld");
        event.addStructure(new TestStructure("overworld"));
    }

    @SubscribeEvent
    public void onCollectStructuresEnd(InitStructureGensEvent<ChunkGeneratorEnd> event)
    {
        logger.info("Collecting structures for End");
        event.addStructure(new TestStructure("end"));
    }

    @SubscribeEvent
    public void onCollectStructuresHell(InitStructureGensEvent<ChunkGeneratorHell> event)
    {
        logger.info("Collecting structures for Hell");
        event.addStructure(new TestStructure("hell"));
    }

    @SubscribeEvent
    public void onCollectStructures(InitStructureGensEvent event)
    {
        logger.info("Collecting structures for {}", event.getGenericType());
    }

    /**
     * Has to be public due to the MapGenStructureIO system
     */
    public static class Start extends StructureStart
    {
        Start(int x, int z)
        {
            super(x, z);
            this.updateBoundingBox();
        }

        /**
         * Required. Don't use. Used internally
         */
        public Start()
        {
            super();
        }
    }

    private class TestStructure extends MapGenStructure implements MapGenStructureManager.ISpawningStructure
    {

        private final String generator;
        private boolean spawn = true;
        private boolean isAt = true;
        private boolean generate = true;
        private boolean generateStructure = true;
        private final List<Biome.SpawnListEntry> spawnList = Lists.newArrayList();

        private TestStructure(String generator)
        {
            this.generator = generator;
            spawnList.add(new Biome.SpawnListEntry(EntityBlaze.class, 100, 1 ,5));
        }

        @Override
        public String getStructureName()
        {
            return "forge-test-structure";
        }

        @Override
        public void generate(World worldIn, int x, int z, ChunkPrimer primer)
        {
            if (generate)
            {
                logger.info("Generating {}", generator);
                generate = false;
            }
            super.generate(worldIn, x, z, primer);
        }

        @Override
        public synchronized boolean generateStructure(World worldIn, Random randomIn, ChunkPos chunkCoord)
        {
            if (generateStructure)
            {
                logger.info("Generating structure {}", generator);
                generateStructure = false;
            }
            return super.generateStructure(worldIn, randomIn, chunkCoord);
        }

        @Nullable
        @Override
        public BlockPos getNearestStructurePos(World worldIn, BlockPos pos, boolean findUnexplored)
        {
            logger.info("Getting nearest structure {}", generator);
            if (isComplete())
            {
               logger.info("[STRUCTURE-GEN-TEST] Test in {} successful", generator);
            }
            else
            {
                logger.info("[STRUCTURE-GEN-TEST] Test not successful. Spawn {}, isAt {}, generate {}, generateStructure {}", spawn, isAt, generate,
                        generateStructure);
            }
            return new BlockPos(64, 0, 64);
        }

        @Override
        protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ)
        {
            return chunkX == 4 && chunkZ == 4;
        }

        @Override
        protected StructureStart getStructureStart(int chunkX, int chunkZ)
        {
            logger.info("Starting structure in {}", generator);
            return new Start(chunkX, chunkZ);
        }

        @Override
        public boolean isInsideStructure(BlockPos pos)
        {
            if (isAt)
            {
                logger.info("Checking for structure {}", generator);
                isAt = false;
            }
            return pos.getX() / 16 == 4 && pos.getZ() / 16 == 4;
        }

        @Override
        public List<Biome.SpawnListEntry> getSpawns(List<Biome.SpawnListEntry> defaults, World world, EnumCreatureType creatureType, BlockPos pos)
        {
            if (spawn)
            {
                logger.info("Getting spawns {}", generator);
                spawn = false;
            }
            if(creatureType==EnumCreatureType.MONSTER){
                return spawnList;
            }
            return defaults;
        }

        private boolean isComplete()
        {
            return !spawn && !generate && !generateStructure && !isAt;
        }

    }
}
