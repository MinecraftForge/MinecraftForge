package net.minecraftforge.debug;

import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;

/**
 * A test for {@link net.minecraftforge.fml.common.asm.transformers.EnumConstructorTransformer}.
 * Also tests that the JVM does <i>not</i> inline the Enum.$VALUES field.
 */
@SuppressWarnings("unused")
@Mod(modid = EnumValuesInlineTest.MOD_ID, name = "Enum Values Constructor Test", version = "0.1")
public class EnumValuesInlineTest
{
    static final String MOD_ID = "enum_values_constructor_test";
    static final boolean DISABLED = false;
    static final Class<?>[] EMPTY_CLASSES = new Class<?>[0];
    static final Object[] EMPTY_ARGS = new Object[0];
    @Nullable
    TestEnum last;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        if (DISABLED)
            return;

        try {
            TestEnum.class.getDeclaredMethod("$$newInstance", String.class, int.class);
        } catch (NoSuchMethodException e){
            throw new Error("$$newInstance method not present", e);
        }

        //10000 is big enough
        for (int i = 1; i <= 10000; i++)
        {
            EnumHelper.addEnum(TestEnum.class, "V_" + i, EMPTY_CLASSES, EMPTY_ARGS);
            TestEnum.values(); // trigger jvm inline
        }
        TestEnum[] arr = TestEnum.values();
        last = arr[arr.length - 1];

        if (!last.name().equals("V_10000"))
            throw new Error("The enum values field was incorrectly inlined!");
        LogManager.getLogger(MOD_ID).info("EnumValuesInlineTest completed successfully.");
    }

    private enum TestEnum
    {
        V_0
    }

}
