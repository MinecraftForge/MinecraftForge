package cpw.mods.fml.common.modloader;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.Item;
import net.minecraft.src.TradeEntry;
import net.minecraft.village.MerchantRecipeList;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;

public class ModLoaderVillageTradeHandler implements IVillageTradeHandler
{
    private List<TradeEntry> trades = Lists.newArrayList();

    @Override
    public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipeList, Random random)
    {
        for (TradeEntry ent : trades)
        {
            if (ent.buying)
            {
                VillagerRegistry.addEmeraldBuyRecipe(villager, recipeList, random, Item.field_77698_e[ent.id], ent.chance, ent.min, ent.max);
            }
            else
            {
                VillagerRegistry.addEmeraldSellRecipe(villager, recipeList, random, Item.field_77698_e[ent.id], ent.chance, ent.min, ent.max);
            }
        }
    }

    public void addTrade(TradeEntry entry)
    {
        trades.add(entry);
    }
}
