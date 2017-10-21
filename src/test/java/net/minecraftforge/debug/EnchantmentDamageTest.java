package net.minecraftforge.debug;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;

/**
 * Test for calculating enchantment damage modifier based on the entity object instead of the creature attribute.
 * <p>
 * HowTo Test:
 * <p>
 * a) Grab yourself a wooden sword and enchant it with "enchantmentdamagetest.new_enchantment"
 * Spawn a Zombie and any other animal (e.g. a cow)
 * The Zombie should be one hit with this sword, the cow should take multiple hits (as normal)
 * <p>
 * b) To verify that the old (deprecated/vanilla) way/method still works
 * enchant a wooden sword with "enchantmentdamagetest.old_enchantment"
 * and repeat the test with a undead creature (Zombie) and a non undead creature (cow).
 * It should have the same result
 */
@Mod(modid = EnchantmentDamageTest.MODID, name = "EnchantmentDamageTest", version = "0.0.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class EnchantmentDamageTest
{

    public static final boolean ENABLE = true;
    public static final String MODID = "enchantmentdamagetest";

    @SubscribeEvent
    public static void registerEnchantment(RegistryEvent.Register<Enchantment> event)
    {
        if (ENABLE)
        {
            event.getRegistry().register(new NewEnchantment());
            event.getRegistry().register(new OldEnchantment());
        }
    }

    /**
     * Only affects Zombies. Uses the new entity object based method.
     */
    public static class NewEnchantment extends Enchantment
    {

        protected NewEnchantment()
        {
            super(Rarity.RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[] { EntityEquipmentSlot.MAINHAND });
            setRegistryName(MODID, "new_enchantment");
            setName("EnchantmentDamageTest-New");
        }

        @Override
        public float calcDamageByEntity(int level, @Nullable Entity entity)
        {
            if (entity instanceof EntityZombie)
            {
                return 100f;
            }
            return 0f;
        }
    }

    /**
     * Only affects Zombies. Uses the old creature attribute based method.
     */
    public static class OldEnchantment extends Enchantment
    {

        protected OldEnchantment()
        {
            super(Rarity.RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[] { EntityEquipmentSlot.MAINHAND });
            setRegistryName(MODID, "old_enchantment");
            setName("EnchantmentDamageTest-New");
        }

        @Override
        public float calcDamageByCreature(int level, EnumCreatureAttribute creatureType)
        {
            if (creatureType == EnumCreatureAttribute.UNDEAD)
            {
                return 100f;
            }
            return 0f;
        }
    }
}
