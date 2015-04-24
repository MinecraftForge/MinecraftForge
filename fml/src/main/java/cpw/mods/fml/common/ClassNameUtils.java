package cpw.mods.fml.common;

public class ClassNameUtils
{
    public static String shortName(Class<?> clz)
    {
        String nm = clz.getName();
        return nm.indexOf('.') > -1 ? nm.substring(nm.lastIndexOf('.')+1) : nm;
    }
}
