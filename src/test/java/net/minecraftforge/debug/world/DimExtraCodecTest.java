package net.minecraftforge.debug.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.DimensionExtra;
import net.minecraftforge.common.world.DimensionExtraCodec;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(DimExtraCodecTest.MODID)
public class DimExtraCodecTest
{
    public static final String MODID = "test_dim_codec";

    public DimExtraCodecTest()
    {
        DIM_EXTRA_CODEC.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static DeferredRegister<DimensionExtraCodec<?>> DIM_EXTRA_CODEC = DeferredRegister.create(ForgeRegistries.DIMENSION_EXTRA_CODEC, "test_dim_codec");

    //This is pretty ugly, it would be much nicer if DimensionExtraCodec could be a functional interface,
    // but that can't be because it has to implement IForgeRegistryEntry in some way.
    public static RegistryObject<DimensionExtraCodec<TestExtra>> TEST = DIM_EXTRA_CODEC.register("test_0", () ->
            new DimensionExtraCodec<TestExtra>()
            {
                @Override
                public Codec<TestExtra> codec()
                {
                    return TestExtra.TEST_EXTRA_CODEC;
                }
            }
    );

    public static class TestExtra extends DimensionExtra
    {
        public static final Codec<TestExtra> TEST_EXTRA_CODEC = RecordCodecBuilder.create(inst ->
                inst.group(
                        Codec.BOOL.fieldOf("constant_poison").forGetter(TestExtra::getConstantPoison),
                        Codec.BOOL.optionalFieldOf("too_cold_for_lava", false).forGetter(TestExtra::getTooColdForLava)
                ).apply(inst, TestExtra::new)
        );

        private final boolean constantPoison;
        private final boolean tooColdForLava;

        public TestExtra(boolean constantPoison, boolean tooColdForLava)
        {
            this.constantPoison = constantPoison;
            this.tooColdForLava = tooColdForLava;
        }

        public boolean getConstantPoison()
        {
            return constantPoison;
        }

        public boolean getTooColdForLava()
        {
            return tooColdForLava;
        }

        @Override
        public DimensionExtraCodec<TestExtra> getCodec()
        {
            return TEST.get();
        }
    }
}
