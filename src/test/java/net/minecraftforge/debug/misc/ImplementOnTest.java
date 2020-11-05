package net.minecraftforge.debug.misc;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ImplementOn;
import net.minecraftforge.fml.common.Mod;

@Mod(ImplementOnTest.MODID)
@SuppressWarnings({"unused", "RedundantSuppression"})
public class ImplementOnTest
{
    public static final String MODID = "implement_on_test";

    public ImplementOnTest() {
        method1();
        method2();
        System.out.println("method3: result = " + method3());
        System.out.println("method4: result = " + method4(5, 10));
    }


    // TEST virtual WITH void RETURN TYPE AND no ARGUMENTS

    public void method1() {

    }

    @ImplementOn(dist = Dist.CLIENT, method = "method1")
    private void method1_client() {
        System.out.println("method1: Client implementation");
    }

    @ImplementOn(dist = Dist.DEDICATED_SERVER, method = "method1")
    private void method1_server() {
        System.out.println("method1: Server implementation");
    }



    // TEST static WITH void RETURN TYPE AND no ARGUMENTS

    public static void method2() {

    }

    @ImplementOn(dist = Dist.CLIENT, method = "method2")
    private static void method2_client() {
        System.out.println("method2: Client implementation");
    }

    @ImplementOn(dist = Dist.DEDICATED_SERVER, method = "method2")
    private static void method2_server() {
        System.out.println("method2: Server implementation");
    }



    // TEST static WITH int RETURN TYPE AND no ARGUMENTS

    public static int method3() {
        return 0;
    }

    @ImplementOn(dist = Dist.CLIENT, method = "method3")
    private static int method3_client() {
        return 3;
    }

    @ImplementOn(dist = Dist.DEDICATED_SERVER, method = "method3")
    private static int method3_server() {
        return 6;
    }



    // TEST virtual WITH int RETURN TYPE AND 2 ARGUMENTS

    public int method4(int a, int b) {
        return 0;
    }

    @ImplementOn(dist = Dist.CLIENT, method = "method4")
    private int method4_client(int a, int b) {
        return a + b;
    }

    @ImplementOn(dist = Dist.DEDICATED_SERVER, method = "method4")
    private int method4_server(int a, int b) {
        return a * b;
    }
}
