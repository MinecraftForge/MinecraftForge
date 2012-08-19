package cpw.mods.fml.common.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import cpw.mods.fml.common.FMLLog;

import net.minecraft.src.EntityVillager;
import net.minecraft.src.MerchantRecipeList;
import net.minecraft.src.StructureVillagePieceWeight;

/**
 * Registry for villager trading control
 *
 * @author cpw
 *
 */
public class VillagerRegistry
{
    private static final VillagerRegistry INSTANCE = new VillagerRegistry();

    private Multimap<Integer, IVillageTradeHandler> tradeHandlers = ArrayListMultimap.create();
    private List<IVillageCreationHandler> villageCreationHandlers = Lists.newArrayList();
    private Map<Integer, String> newVillagers = Maps.newHashMap();

    public interface IVillageCreationHandler
    {
        List<StructureVillagePieceWeight> getVillagePieces(Random random, int i);
    }

    public interface IVillageTradeHandler
    {
        void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipeList, Random random);
    }

    public static VillagerRegistry instance()
    {
        return INSTANCE;
    }

    public void registerVillagerType(int villagerId, String villagerSkin)
    {
        if (newVillagers.containsKey(villagerId))
        {
            FMLLog.severe("Attempt to register duplicate villager id %d", villagerId);
            throw new RuntimeException();
        }
        newVillagers.put(villagerId, villagerSkin);
    }


    public static String getVillagerSkin(int villagerType, String defaultSkin)
    {
        if (instance().newVillagers.containsKey(villagerType))
        {
            return instance().newVillagers.get(villagerType);
        }
        return defaultSkin;
    }

    public static void manageVillagerTrades(MerchantRecipeList recipeList, EntityVillager villager, int villagerType, Random random)
    {
        for (IVillageTradeHandler handler : instance().tradeHandlers.get(villagerType))
        {
            handler.manipulateTradesForVillager(villager, recipeList, random);
        }
    }

    public static void addExtraVillageComponents(ArrayList components, Random random, int i)
    {
        List<StructureVillagePieceWeight> parts = components;
        for (IVillageCreationHandler handler : instance().villageCreationHandlers)
        {
            parts.addAll(handler.getVillagePieces(random, i));
        }
    }

}
