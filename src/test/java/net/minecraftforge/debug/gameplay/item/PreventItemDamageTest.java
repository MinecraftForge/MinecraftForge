package net.minecraftforge.debug.gameplay.item;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.GameType;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.test.BaseTestMod;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@Mod(PreventItemDamageTest.MOD_ID)
@GameTestHolder("forge." + PreventItemDamageTest.MOD_ID)
public class PreventItemDamageTest extends BaseTestMod {
    static final String MOD_ID = "prevent_item_damage";

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    private static final RegistryObject<Item> FAKE_SHIELD = ITEMS.register("fake_shield", () -> new ShieldItem(new Item.Properties().setId(ITEMS.key("fake_shield")).durability(10)) {
        @Override
        public int damageItem(ItemStack stack, int damage, ServerLevel level, @Nullable ServerPlayer player, Consumer<Item> onBroken) {
            onBroken.accept(this);
            return 0;
        }
    });

    public PreventItemDamageTest(FMLJavaModLoadingContext context) {
        super(context);
        this.testItem(lookup -> new ItemStack(FAKE_SHIELD.get()));
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void player_fake_shield_took_no_damage(GameTestHelper helper) {
        helper.makeFloor();

        // setup player
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        helper.<LivingEntityUseItemEvent.Start>addEventListener(event -> {
            // Artificially pass 5 seconds from start of the shield
            // This is because the first 5 ticks, the player is still vulnerable
            if (event.getEntity() == player) {
                event.setDuration(event.getDuration() - 100);
            }
        });

        // start using shield
        var shield = FAKE_SHIELD.get().getDefaultInstance();
        player.setItemInHand(InteractionHand.MAIN_HAND, shield);
        player.startUsingItem(InteractionHand.MAIN_HAND);
        int initialDamage = shield.getDamageValue();

        // setup enemy
        var enemy = helper.spawnWithNoFreeWill(EntityType.HUSK, new BlockPos(2, 0, 2));
        player.lookAt(EntityAnchorArgument.Anchor.EYES, enemy.position());

        // hit the player
        player.hurtServer(helper.getLevel(), enemy.damageSources().mobAttack(enemy), 5.0F);

        // shield on cooldown?
        helper.assertTrue(initialDamage == shield.getDamageValue(), "Fake shield took damage! Expected: " + initialDamage + ", Actual: " + shield.getDamageValue());
        helper.succeed();
    }
}
