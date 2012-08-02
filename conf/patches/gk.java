// this is needed for the sound engine to work with deobfuscated sourcecode without crashing

public class gk
{
    public static int c(double d)
    {
        int i = (int)d;
        return d >= (double)i ? i : i - 1;
    }
}
