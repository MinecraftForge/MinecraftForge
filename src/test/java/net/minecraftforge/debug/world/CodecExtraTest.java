package net.minecraftforge.debug.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraftforge.common.world.CodecExtraType;
import net.minecraftforge.common.world.ICodecExtra;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(CodecExtraTest.MODID)
public class CodecExtraTest
{
    public static final String MODID = "codec_extra_test";

    private static final DeferredRegister<CodecExtraType<?>> CODEC_EXTRAS = DeferredRegister.create(ForgeRegistries.CODEC_EXTRA_TYPES, MODID);
    private static final RegistryObject<CodecExtraType<TestCodec>> TEST = CODEC_EXTRAS.register("test_0", () -> new CodecExtraType<>(TestCodec.CODEC));

    public CodecExtraTest()
    {
        CODEC_EXTRAS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static class TestCodec implements ICodecExtra
    {
        public static final MapCodec<TestCodec> CODEC = RecordCodecBuilder.mapCodec(inst ->
                inst.group(
                        Codec.BOOL.fieldOf("constant_poison").forGetter(TestCodec::getConstantPoison),
                        Codec.BOOL.optionalFieldOf("too_cold_for_lava", false).forGetter(TestCodec::getTooColdForLava)
                ).apply(inst, TestCodec::new)
        );

        private final boolean constantPoison;
        private final boolean tooColdForLava;

        public TestCodec(boolean constantPoison, boolean tooColdForLava)
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

        public CodecExtraType<TestCodec> getType()
        {
            return TEST.get();
        }
    }
}
