package net.minecraftforge.oredict;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;


public class SensitiveOreDict
{
    private static final List<IOreRecognizer> recognizers = new ArrayList<IOreRecognizer>();

    public static void registerOreRecognizer(IOreRecognizer oreRecognizer)
    {
        recognizers.add(oreRecognizer);
    }

    /**
     * Returns if the item stack has the name according to this class and {@linkplain net.minecraftforge.oredict.OreDictionary}.
     *
     * @param itemStack
     * @param oreName
     * @return if the item stack has the name
     */
    public static boolean hasName(ItemStack itemStack, String oreName)
    {
        for (IOreRecognizer r: recognizers)
        {
            for (String name: r.getOreNames(itemStack))
            {
                if (oreName.equals(name)) return true;
            }
        }

        for (int id: OreDictionary.getOreIDs(itemStack))
        {
            if (oreName.equals(OreDictionary.getOreName(id))) return true;
        }
        return false;
    }

    /**
     * Returns all the ore names according this class and {@linkplain net.minecraftforge.oredict.OreDictionary}.
     * If nothing is found, returns empty list.
     * @param itemStack
     * @return All the names. Empty list if nothing found.
     */
    public static List<String> getNames(ItemStack itemStack)
    {
        List<String> names = new ArrayList<String>();
        for (IOreRecognizer r: recognizers)
        {
            for (String name: r.getOreNames(itemStack)) {
                if (!names.contains(name)) {
                    names.add(name);
                }
            }
        }
        for (int id: OreDictionary.getOreIDs(itemStack))
        {
            String name = OreDictionary.getOreName(id);
            if (!names.contains(name)) {
                names.add(name);
            }
        }
        return names;
    }
}
