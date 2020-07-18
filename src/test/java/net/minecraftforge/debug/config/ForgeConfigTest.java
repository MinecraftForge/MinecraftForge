package net.minecraftforge.debug.config;

import static net.minecraft.command.Commands.literal;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.logging.Logger;

import net.minecraft.command.CommandSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ModConfigSpec;
import net.minecraftforge.common.config.ModObjectConverter;
import net.minecraftforge.common.config.RangeType;
import net.minecraftforge.common.config.values.BooleanValue;
import net.minecraftforge.common.config.values.DoubleValue;
import net.minecraftforge.common.config.values.EnumValue;
import net.minecraftforge.common.config.values.IntValue;
import net.minecraftforge.common.config.values.ListValue;
import net.minecraftforge.common.config.values.LongValue;
import net.minecraftforge.common.config.values.ModValue;
import net.minecraftforge.common.config.values.StringValue;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import com.electronwill.nightconfig.core.Config;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.mojang.brigadier.CommandDispatcher;

@Mod(ForgeConfigTest.MODID)
public class ForgeConfigTest
{
    private static final Logger LOGGER = Logger.getLogger("ForgeConfigTest");
    public static final String MODID = "forge_config_test";


    public final ConfigSpecTest CLIENT;
    public final ConfigSpecTest SERVER;
    public ForgeConfigTest()
    {
        CLIENT = ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigSpecTest::new);
        SERVER = ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ConfigSpecTest::new);

        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event)
    {
        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
        dispatcher.register(
                literal("fct")
                .then(literal("client").executes( c -> {
                    testClientSpec();
                    return 1;
                }))
                .then(literal("server").executes(c -> {
                    testServerSpec();
                    return 1;
                })
        ));
    }

    public void testClientSpec()
    {
        print("*#*#*#*#*#*#*#* CLIENT CONFIG TESTING START *#*#*#*#*#*#*#*");
        testConfigSpec(CLIENT);
        print("*#*#*#*#*#*#*#* CLIENT CONFIG TESTING END *#*#*#*#*#*#*#*");
    }

    public void testServerSpec()
    {
        print("*#*#*#*#*#*#*#* SERVER CONFIG TESTING START *#*#*#*#*#*#*#*");
        testConfigSpec(SERVER);
        print("*#*#*#*#*#*#*#* SERVER CONFIG TESTING END *#*#*#*#*#*#*#*");
    }

    public void testConfigSpec(ConfigSpecTest spec)
    {
        print("BOOL_TEST = " + spec.BOOL_TEST.get() + " == " + spec.BOOL_TEST_VALUE);
        print("DOUBLE_TEST = " + spec.DOUBLE_TEST.get() + " == " + spec.DOUBLE_TEST_VALUE);
        print("DOUBLE_RANGE_TEST = ");
        printRangeTest(spec.DOUBLE_RANGE_TEST.get(), spec.DOUBLE_RANGE_TASTE_RANGE, spec.DOUBLE_RANGE_TEST_VALUE);
        print("ENUM_TEST = " + spec.ENUM_TEST.get() + " == " + spec.ENUM_TEST_VALUE);
        print("INT_TEST = " + spec.INT_TEST.get() + " == " + spec.INT_TEST_VALUE);
        print("INT_RANGE_TEST = ");
        printRangeTest(spec.INT_RANGE_TEST.get(), spec.INT_RANGE_TEST_RANGE, spec.INT_RANGE_TEST_VALUE);
        print("LIST_TEST = ");
        printListTest(spec.LIST_TEST.get(), spec.CONFIG_LIST);
        print("LONG_TEST = " + spec.LONG_TEST.get() + " == " + spec.LONG_TEST_VALUE);
        print("LONG_RANGE_TEST = ");
        printRangeTest(spec.LONG_RANGE_TEST.get(), spec.LONG_RANG_TEST_RANGE, spec.LONG_RANGE_TEST_VALUE);
        print("MOD_VALUE_TEST = ");
        printModObject(spec.MOD_VALUE_TEST, spec.CONFIG_MOD_OBJECT);
        print("BLOCK_POS_TEST = ");
        printModObject(spec.BLOCK_POS_TEST, spec.CONFIG_POS);
        print("STRING_TEST = " + spec.STRING_TEST.get() + " == " + spec.STRING_TEST_VALUE + " == " + spec.STRING_TEST.get().equals(spec.STRING_TEST_VALUE));
    }
    public static class ConfigSpecTest
    {
        public final BooleanValue BOOL_TEST;
        public final boolean BOOL_TEST_VALUE = true;

        public final DoubleValue DOUBLE_TEST;
        public final DoubleValue DOUBLE_RANGE_TEST;
        public final double DOUBLE_TEST_VALUE = Math.PI;
        public final double DOUBLE_RANGE_TEST_VALUE = -31.5D;
        public final Range<Double> DOUBLE_RANGE_TASTE_RANGE = RangeType.CLOSED.createRange(0.0D, 4.0D);

        public final EnumValue<Direction> ENUM_TEST;
        public final Direction ENUM_TEST_VALUE = Direction.NORTH;

        public final IntValue INT_TEST;
        public final int INT_TEST_VALUE = 817471491;
        public final IntValue INT_RANGE_TEST;
        public final int INT_RANGE_TEST_VALUE = 55;
        public final Range<Integer> INT_RANGE_TEST_RANGE = RangeType.ATLEAST.createRange(65);

        public final ListValue<String> LIST_TEST;
        private final List<String> CONFIG_LIST = Lists.newArrayList("world", "DIM1", "DIM-1");

        public final LongValue LONG_TEST;
        public final long LONG_TEST_VALUE = 3481094824908109L;
        public final LongValue LONG_RANGE_TEST;
        public final long LONG_RANGE_TEST_VALUE = 45454545454545L;
        public final Range<Long> LONG_RANG_TEST_RANGE = RangeType.ATMOST.createRange(500000L);

        public final ModValue<ConfigModObject> MOD_VALUE_TEST;
        private final ConfigModObject CONFIG_MOD_OBJECT = new ConfigModObject();
        private final ConfigModObjectConverter CONFIG_MOD_OBJ_CONVERTER = new ConfigModObjectConverter();

        public final ModValue<BlockPos> BLOCK_POS_TEST;
        private final BlockPos CONFIG_POS;
        private final BlockPosConverter BLOCK_POS_CONVERTER = new BlockPosConverter();

        public final StringValue STRING_TEST;
        public final String STRING_TEST_VALUE = "45d6s4fa51823!*!)$)#++\\uue";

        public ConfigSpecTest(ModConfigSpec builder)
        {
            int randX = ThreadLocalRandom.current().nextInt(450);
            int randY = ThreadLocalRandom.current().nextInt(450);
            int randZ = ThreadLocalRandom.current().nextInt(450);
            CONFIG_POS = new BlockPos(randX, randY, randZ);
            builder.comment("Initial test comment. Please ignore.")
                   .push("TestSection");

            BOOL_TEST = builder.comment("Test boolean entry")
                    .translation("test.bool")
                    .defineBoolean("test_bool", BOOL_TEST_VALUE);
            DOUBLE_TEST = builder.comment("Test double entry")
                    .translation("test.double")
                    .defineNumber("test.double", DOUBLE_TEST_VALUE);
            DOUBLE_RANGE_TEST = builder.comment("Range test with doubles")
                    .translation("test.range.double")
                    .defineInRange("test.range.double", DOUBLE_RANGE_TEST_VALUE, DOUBLE_RANGE_TASTE_RANGE);
            ENUM_TEST = builder.comment("Test enum test")
                    .translation("test.enum")
                    .defineEnumValue("test.enum", ENUM_TEST_VALUE);
            INT_TEST = builder.comment("Integer number test")
                    .translation("test.int")
                    .defineNumber("test.int", INT_TEST_VALUE);
            INT_RANGE_TEST = builder.comment("Integer range test")
                    .translation("test.range.int")
                    .defineInRange("test.range.int", INT_RANGE_TEST_VALUE, INT_RANGE_TEST_RANGE);
            LIST_TEST = builder.comment("List test")
                    .translation("list.test")
                    .defineList("list.test", CONFIG_LIST, Object::toString);
            LONG_TEST = builder.comment("Long test")
                    .translation("test.long")
                    .defineNumber("long.test", LONG_TEST_VALUE);
            LONG_RANGE_TEST = builder.comment("Long range test")
                    .translation("test.range.long")
                    .defineInRange("test.range.long", LONG_RANGE_TEST_VALUE, LONG_RANG_TEST_RANGE);
            MOD_VALUE_TEST = builder.comment("Custom Mod Object Test")
                    .translation("test.mod.object")
                    .defineModObject("test.mod.object", CONFIG_MOD_OBJECT, CONFIG_MOD_OBJ_CONVERTER);
            BLOCK_POS_TEST = builder.comment("Block Pos Test")
                    .translation("test.block_pos")
                    .defineModObject("test.block_pos", CONFIG_POS, BLOCK_POS_CONVERTER);
            STRING_TEST = builder.comment("String test")
                    .translation("test.string")
                    .defineString("test.string", STRING_TEST_VALUE);
            builder.pop();
        }
    }



    public void printModObject(ModValue<?> configObject, Object defaultObject)
    {
        print("CONFIG: " + configObject.get().toString());
        print("DEFAULT: " + defaultObject.toString());
    }

    public void printListTest(List<String> configList, List<String> defaultList)
    {
        print("CONFIG: " + Arrays.deepToString(configList.toArray()));
        print("DEFAULT: " + Arrays.deepToString(defaultList.toArray()));
        print("EQUAL = " + configList.equals(defaultList));
    }

    public <V extends Comparable<V>> void printRangeTest(V configValue, Range<V> range, V defaultValue)
    {
        print(configValue + " ~ " + range.contains(configValue) + " ~ " + range.toString() + " ~ " + defaultValue);
    }

    private void print(String msg) {
        LOGGER.info(msg);
    }

    public static class ConfigModObject
    {
        public final long num;
        public final UUID uuid;
        public ConfigModObject()
        {
            num = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
            uuid = java.util.UUID.randomUUID();
        }

        public ConfigModObject(long num, UUID uuid)
        {
            this.num = num;
            this.uuid = uuid;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            ConfigModObject that = (ConfigModObject) o;
            return num == that.num &&
                    uuid.equals(that.uuid);
        }

        @Override
        public String toString()
        {
            return "ConfigModObject{" +
                    "num=" + num +
                    ", uuid=" + uuid +
                    '}';
        }
    }

    public static class ConfigModObjectConverter implements ModObjectConverter<ConfigModObject>
    {

        @Override
        public ConfigModObject loadFromConfig(Config subConfig, List<String> path, Supplier<ConfigModObject> defaultSupplier)
        {
            long num = subConfig.get("long_num");
            UUID uuid = UUID.fromString(subConfig.get("uuid"));
            return new ConfigModObject(num, uuid);
        }

        @Override
        public void saveToConfig(Config subConfig, List<String> path, ConfigModObject object, Supplier<ConfigModObject> defaultSupplier)
        {
            subConfig.set("long_num", object.num);
            subConfig.set("uuid", object.uuid.toString());
        }
    }

    public static class BlockPosConverter implements ModObjectConverter<BlockPos>
    {

        @Override
        public BlockPos loadFromConfig(Config subConfig, List<String> path, Supplier<BlockPos> defaultSupplier)
        {
            int x = subConfig.get("x");
            int y = subConfig.get("y");
            int z = subConfig.get("z");
            return new BlockPos(x, y, z);
        }

        @Override
        public void saveToConfig(Config subConfig, List<String> path, BlockPos object, Supplier<BlockPos> defaultSupplier)
        {
            subConfig.set("x", object.getX());
            subConfig.set("y", object.getY());
            subConfig.set("z", object.getZ());
        }
    }
}
