package net.minecraftforge.debug.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * This test mod allows a custom scaffolding to move down
 * while sneaking through a tag and method.
 */
@Mod(ScaffoldingTest.MODID)
public class ScaffoldingTest
{
    static final String MODID = "scaffolding_test";
    static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

    static final RegistryObject<Block> SCAFFOLDING_TAG_TEST = BLOCKS.register("scaffolding_tag_test", () -> new ScaffoldingBlock(AbstractBlock.Properties.create(Material.MISCELLANEOUS, MaterialColor.SAND).doesNotBlockMovement().sound(SoundType.SCAFFOLDING).variableOpacity()));
    static final RegistryObject<Block> SCAFFOLDING_METHOD_TEST = BLOCKS.register("scaffolding_method_test", () -> new ScaffoldingMethodTestBlock(AbstractBlock.Properties.create(Material.MISCELLANEOUS, MaterialColor.SAND).doesNotBlockMovement().sound(SoundType.SCAFFOLDING).variableOpacity()));

    public ScaffoldingTest()
    {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modBus);
    }

    static class ScaffoldingMethodTestBlock extends ScaffoldingBlock
    {

        public ScaffoldingMethodTestBlock(Properties properties)
        {
            super(properties);
        }

        @Override
        public boolean isScaffolding(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity)
        {
            return true;
        }
    }
}
