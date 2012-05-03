public class mod_testMod extends BaseMod {


    @MLProp
    public static byte byteptest = 5; 
    @MLProp
    public static short shortptest = 5; 
    @MLProp
    public static int intptest = 5; 
    @MLProp
    public static long longptest = 5; 
    @MLProp
    public static float floatptest = 5; 
    @MLProp
    public static double doubleptest = 5; 
    @MLProp
    public static boolean booleanptest = false; 

    @Override
    public String getVersion() {
        return "test";
    }

    @Override
    public void load() {
        System.out.println("byte : "+byteptest);
        System.out.println("short : "+shortptest);
        System.out.println("int : "+intptest);
        System.out.println("long : "+longptest);
        System.out.println("float : "+floatptest);
        System.out.println("double : "+doubleptest);
        System.out.println("bool : "+booleanptest);
    }

}