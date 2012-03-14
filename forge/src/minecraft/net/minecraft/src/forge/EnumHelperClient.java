package net.minecraft.src.forge;

import argo.jdom.JsonNodeType;
import net.minecraft.src.EnumOS2;
import net.minecraft.src.EnumOptions;
import net.minecraft.src.EnumRarity;

public class EnumHelperClient extends EnumHelper
{

    private static Class[][] ctrs =
    {
        {JsonNodeType.class},
        {EnumOptions.class, String.class, boolean.class, boolean.class},
        {EnumOS2.class},
        {EnumRarity.class, int.class, String.class}
    };

    private static boolean[] decompiled = new boolean[ctrs.length];
    private static boolean   isSetup    = false;

    public static JsonNodeType addJsonNodeType(String name)
    {
        if (!isSetup)
        {
            setup();
        }

        return addEnum(decompiled[0], JsonNodeType.class, name,
                new Class[] {},
                new Object[] {});
    }
    public static EnumOptions addOptions(String name, String langName, boolean isSlider, boolean isToggle)
    {
        if (!isSetup)
        {
            setup();
        }

        return addEnum(decompiled[1], EnumOptions.class, name,
                new Class[] {String.class, boolean.class, boolean.class},
                new Object[]{langName,     isSlider,      isToggle     });
    }
    public static EnumOS2 addOS2(String name)
    {
        if (!isSetup)
        {
            setup();
        }

        return addEnum(decompiled[2], EnumOS2.class, name,
                new Class[] {},
                new Object[]{});
    }
    public static EnumRarity addRarity(String name, int color, String displayName)
    {
        if (!isSetup)
        {
            setup();
        }

        return addEnum(decompiled[3], EnumRarity.class, name,
                new Class[] {int.class, String.class},
                new Object[]{color,     displayName });
    }

    private static void setup()
    {
        //System.out.println("Enum Helper Initalizing: ");
        for (int x = 0; x < ctrs.length; x++)
        {
            try
            {
                Class<?>[] enumHeaders = new Class[ctrs[x].length + 3];
                enumHeaders[0] = String.class;
                enumHeaders[1] = int.class;
                enumHeaders[2] = String.class;
                enumHeaders[3] = int.class;

                for (int y = 1; y < ctrs[x].length; y++)
                {
                    enumHeaders[3 + y] = ctrs[x][y];
                }

                ctrs[x][0].getDeclaredConstructor(enumHeaders);
                decompiled[x] = true;

            }
            catch (Exception e)
            {
                //Nom Nom Nom
            }
            //System.out.format("\t%-25s %s\r\n", ctrs[x][0].getName().replace("net.minecraft.src.", ""), decompiled[x]);
        }
        isSetup = true;
    }

    static
    {
        if (!isSetup)
        {
            setup();
        }
    }

}
