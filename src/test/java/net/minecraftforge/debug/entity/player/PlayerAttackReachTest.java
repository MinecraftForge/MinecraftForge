package net.minecraftforge.debug.entity.player;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;
/**
 * Tests if the patch to PlayerEntity to make it utilize ForgeMod.ATTACK_REACH works
 * The Longsword adds a modifier to the user's attack reach
 * The goal of this patch is to allow modders to more easily add equipment or potion effects that affect the attack reach of players
 * As well as allow command users to modify this attribute for a player
 */
@Mod(PlayerAttackReachTest.MODID)
public class PlayerAttackReachTest {

    static final String MODID = "player_attack_reach_test";

    static final float ATTACK_REACH_VALUE = 2.0F; // Change this value for testing, but note that the attribute is capped at 1024.0D

    static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    static RegistryObject<Item> LONGSWORD = ITEMS.register("longsword", () ->
            new LongswordItem(ItemTier.IRON, 3, -2.4F, ATTACK_REACH_VALUE, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT))
    );

    public PlayerAttackReachTest()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);
    }

    static class LongswordItem extends SwordItem {
        private final float attackReach;
        private final Multimap<Attribute, AttributeModifier> defaultModifiers = ArrayListMultimap.create(); // initialize as empty
        protected static final UUID BASE_ATTACK_REACH_UUID = UUID.fromString("f1797f2f-f02c-44f9-951f-27c9e5446916");

        LongswordItem(IItemTier itemTier, int attackDamageIn, float attackSpeedIn, float attackReach, Properties properties) {
            super(itemTier, attackDamageIn, attackSpeedIn, properties);
            this.attackReach = attackReach;
        }

        @Override
        public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlotType equipmentSlotType) {
            if(equipmentSlotType == EquipmentSlotType.MAINHAND){
                if(this.defaultModifiers.isEmpty()){
                    Multimap<Attribute, AttributeModifier> oldAttributeModifiers = super.getDefaultAttributeModifiers(equipmentSlotType);
                    this.defaultModifiers.putAll(oldAttributeModifiers);
                    this.defaultModifiers.put(ForgeMod.ATTACK_REACH.get(), new AttributeModifier(BASE_ATTACK_REACH_UUID, "Weapon modifier", (double)this.attackReach, AttributeModifier.Operation.ADDITION));
                }
                return this.defaultModifiers;
            }
            else return super.getDefaultAttributeModifiers(equipmentSlotType);
        }
    }
}
