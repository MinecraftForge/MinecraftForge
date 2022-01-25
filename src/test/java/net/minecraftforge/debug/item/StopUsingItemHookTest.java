package net.minecraftforge.debug.item;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(StopUsingItemHookTest.MOD_ID)
public class StopUsingItemHookTest
{

    public static final String MOD_ID = "stop_using_item_hook_test";

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    private static final RegistryObject<Item> THING = ITEMS.register("thing", () -> new ThingItem(new Item.Properties().tab(CreativeModeTab.TAB_MISC).stacksTo(1)));

    public StopUsingItemHookTest()
    {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private static class ThingItem extends Item
    {

        public ThingItem(Item.Properties props)
        {
            super(props);
        }

        public int getUseDuration(ItemStack stack)
        {
            return 100;
        }

        @Override
        public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
        {
            player.awardStat(Stats.ITEM_USED.get(this));
            return ItemUtils.startUsingInstantly(level, player, hand);
        }

        @Override
        public void stopUsingItem(LivingEntity e, ItemStack itemStack)
        {
            super.stopUsingItem(e, itemStack);
            if (!e.getLevel().isClientSide())
            {
                System.out.println("Item Over!");
            }
        }

    }
}
