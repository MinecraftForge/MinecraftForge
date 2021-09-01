package net.minecraftforge.debug.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

import javax.annotation.Nullable;

@Mod("custom_shield_test")
public class CustomShieldTest {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Item.class, "custom_shield_test");

    private static final RegistryObject<CustomShieldItem> CUSTOM_SHIELD_ITEM = ITEMS.register("custom_shield",
            () -> new CustomShieldItem((new Item.Properties()).durability(336).tab(CreativeModeTab.TAB_COMBAT)));

    public CustomShieldTest() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private static class CustomShieldItem extends Item {
        public CustomShieldItem(Properties properties) {
            super(properties);
        }

        @Override
        public UseAnim getUseAnimation(ItemStack stack) {
            return UseAnim.BLOCK;
        }

        @Override
        public int getUseDuration(ItemStack stack) {
            return 72000;
        }

        @Override
        public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
            ItemStack itemstack = player.getItemInHand(hand);
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(itemstack);
        }

        @Override
        public boolean isShield(ItemStack stack, @Nullable LivingEntity entity) {
            return true;
        }
    }
}
