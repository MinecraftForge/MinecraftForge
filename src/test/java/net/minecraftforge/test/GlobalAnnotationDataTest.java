package net.minecraftforge.test;

import com.google.common.collect.Maps;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.lang.annotation.*;
import java.util.Map;
import java.util.Set;

/**
 * --------------------<br>
 * A mod for debugging global annotation.<br>
 * Adding Feature :<br>
 * ---Type Annotation---<br>
 * Class Type Annotation : Success<br>
 * Field Type Annotation : Success<br>
 * Method Type Annotation : Success<br>
 * Method Parameter Type Annotation : Success<br>
 * ---Method Parameter Annotation---<br>
 * Method Parameter Annotation : Success<br>
 * --- Local Variable ---<br>
 * Local Variable : Success<br>
 * --------------------
 */
@Mod(modid = "GlobalAnnotationDataTest", name = "Global Annotation Data Test", version = "1.0")
@SuppressWarnings("unused")
// @GlobalAnnotationDataTest.AnnotationTest.TestAnnotation
public class GlobalAnnotationDataTest
{
    /*
    private static final boolean ENABLE = false;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (ENABLE)
        {
            Set<ASMDataTable.ASMData> asmDataSet = event.getAsmData().getAll(AnnotationTest.TestAnnotation.class.getName());
            String message = "\n--------------------" + AnnotationTest.TestAnnotation.class.getName() + " Debug Message--------------------\n";
            for (ASMDataTable.ASMData asmData : asmDataSet)
            {
                message += "Class Name : " + asmData.getClassName() + "---Object Name : " + asmData.getObjectName() + "\n";
            }
            System.out.println(message);
        }
    }

    public static class AnnotationTest<A, B, C, D, E, F, G, H, I, J, K, L>
    {
        public static final class TypeAnnotationTest<@TestAnnotation TEST_TYPE extends Map<AnnotationTest, AnnotationTest>, TEST2 extends Annotation, @AnnotationTest.TestAnnotation TEST3 extends Annotation>
        {
            @TestAnnotation
            public static final Class<@TestAnnotation ? extends AnnotationTest<? extends Annotation, NullPointerException, NullPointerException, NullPointerException, NullPointerException, NullPointerException, NullPointerException, NullPointerException, NullPointerException, NullPointerException, NullPointerException, @TestAnnotation NullPointerException>> fieldTypeTest = null;

            public static Class<
                    @TestAnnotation ? extends Map<
                            @TestAnnotation ? extends Annotation,
                            @TestAnnotation ? extends Annotation>>
            typeAnnotationTest
                    (@TestAnnotation Class<
                    @TestAnnotation ? extends Class<
                            @TestAnnotation ? extends Annotation>> typeTestParameter)
            {
                return null;
            }
        }

        public static final class MethodAnnotationTest extends AnnotationTest
        {
            public static Class<@TestAnnotation ? extends Annotation> methodAnnotationTest(@TestAnnotation Object object, @TestAnnotation Object object2)
            {
                @TestAnnotation
                String a = null;
                @TestAnnotation
                Class<@TestAnnotation ? extends Class<@TestAnnotation ?>> abc = null;
                Map<String, String> map = Maps.newHashMap();
                return null;
            }
        }

        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_PARAMETER, ElementType.TYPE_USE, ElementType.LOCAL_VARIABLE})
        public @interface TestAnnotation
        {

        }
    }
    */
}
