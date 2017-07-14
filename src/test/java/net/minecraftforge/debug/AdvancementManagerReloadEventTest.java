package net.minecraftforge.debug;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.ImmutableMap;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.command.FunctionObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.advancements.AdvancementManagerReloadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

/**
 * A bit of a proof-of-concept for dynamic advancements.
 * @author Landmaster
 */
@Mod.EventBusSubscriber
@Mod(modid = AdvancementManagerReloadEventTest.MODID, name = "Advancement Manager reload event test", version = "1.0", acceptableRemoteVersions = "*")
public class AdvancementManagerReloadEventTest
{
    public static final String MODID = "adv_reload_test";

    static final boolean ENABLED = false;
    static final ResourceLocation ID_WOOD_UNLOCK_TNT = new ResourceLocation(MODID, "wood_unlock_tnt");

    @SubscribeEvent
    public static void onAdvReload(AdvancementManagerReloadEvent event)
    {
        if (ENABLED) {
            final Advancement.Builder woodUnlockTNT = new Advancement(
                    ID_WOOD_UNLOCK_TNT,
                    event.getAdvManager().getAdvancement(new ResourceLocation("recipes/root")),
                    null,
                    new AdvancementRewards(0, new ResourceLocation[0],
                                           new ResourceLocation[] { new ResourceLocation("tnt") }, FunctionObject.CacheableFunction.EMPTY),
                    ImmutableMap.of("has_wood", new Criterion(new InventoryChangeTrigger.Instance(
                            MinMaxBounds.UNBOUNDED, MinMaxBounds.UNBOUNDED, MinMaxBounds.UNBOUNDED,
                            new ItemPredicate[] { new ItemPredicate()
                            {
                                @Override public boolean test(ItemStack stack)
                                {
                                    return ArrayUtils.contains(OreDictionary.getOreIDs(stack), OreDictionary.getOreID("plankWood")); // test the oredict for wood planks
                                }
                            } }))),
                    new String[][] {{"has_wood"}})
                .copy();

            event.getAdvMap().put(ID_WOOD_UNLOCK_TNT, woodUnlockTNT);
        }
    }
}
