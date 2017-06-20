package net.minecraftforge.debug;

import net.minecraft.block.BlockGravel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDigging;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeOcean;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.*;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;

/**
 * To verify this is working
 * 1. Check the log for 'Substitution tests successful' resp. 'One or more substitution tests failed'
 * 2. Check the following replacements:
 * a) '/effect <player> minecraft:slowness' -> You should get 'test_substitution' effect
 * b) Select a speed potion out of the creative tab and drink it -> You should get swiftness and absorption
 * c) Equip a pickaxe and use '/enchant <player> minecraft:efficiency' -> You should get a 'test_substitution' enchantment
 * d) Summon a cow and hit it. It should make a glass break sound
 * e) Summon a cow. It should always render a name tag
 * f) Look for an ocean biome. The water should be red
 * g) Give yourself a gravel block. (It's tooltip should say "SubstitutionTestBlock"). Place it. It should be unbreakable (in survival).
 * h) Give yourself a gravel item. It's tooltip should say "SubstitutionTestItem".
 * <p>
 * Disabling this mod and reloading the world should be now problem. All replacements should be back to normal again
 * <p>
 * Using subclasses for all substitution to make it easier to trace possible issues.
 * <p>
 * The used substitutions are in no way useful (as many of the changes can be achieved with out substituting the entire object), but are meant to be easy to identify.
 */
@Mod(modid = "forge.testsubmod", name = "Forge Substitution Test", version = "1.0", acceptableRemoteVersions = "*")
public class SubstitutionTest
{
    public static boolean enabled = true;

    private static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent evt)
    {
        logger = evt.getModLog();
        if (enabled)
        {
            boolean success =
                    testPotion() && testPotionType() && testEnchantment() && testSoundEvent() && testEntity() && testBiome() && testBlock() && testItem()
                            && testExisting() && testIncompatible();

            if (success)
            {
                logger.info("Substitution tests successful");
            }
            else
            {
                FMLLog.bigWarning("One or more substitution tests failed");
            }
        }
    }

    /**
     * Try to register a substitution and verify the success. Logs any problems.
     *
     * @return Success
     */
    private <T extends IForgeRegistryEntry<T>> boolean replace(GameRegistry.Type type, IForgeRegistry<T> registry, ResourceLocation id, T testObject)
    {
        T oldEntry = registry.getValue(id);
        try
        {
            GameRegistry.addSubstitutionAlias(id.toString(), type, testObject);
            T newEntry = registry.getValue(id);
            if (oldEntry.equals(newEntry))
            {
                logger.warn("[%s]Did not replace %s(%s) with %s", type, oldEntry, id, testObject);
                return false;
            }
            else if (testObject.equals(newEntry))
            {
                return true;
            }
            else
            {
                //To make sure we are not missing anything here
                logger.warn("[%s]Something went wrong while replacing %s(%s) with %s. Got %s", type, oldEntry, id, testObject, newEntry);
                return false;
            }
        }
        catch (ExistingSubstitutionException e)
        {
            logger.error("[%s]Failed to register substitution for %s", type, id);
            logger.catching(e);
        }
        return false;
    }

    private boolean testBiome()
    {
        return replace(GameRegistry.Type.BIOME, GameData.getBiomeRegistry(), new ResourceLocation("minecraft:ocean"), new TestBiome());
    }

    private boolean testBlock()
    {
        return replace(GameRegistry.Type.BLOCK, GameData.getBlockRegistry(), new ResourceLocation("minecraft:gravel"), new TestBlock());
    }

    private boolean testEnchantment()
    {
        return replace(GameRegistry.Type.ENCHANTMENT, GameData.getEnchantmentRegistry(), new ResourceLocation("minecraft:efficiency"), new TestEnchantment());
    }

    private boolean testEntity()
    {
        return replace(GameRegistry.Type.ENTITY, GameData.getEntityRegistry(), new ResourceLocation("minecraft:cow"), new EntityEntry(TestCow.class, "Cow"));
    }

    private boolean testExisting()
    {
        //Use same registry name as {@link SubstitutionTest#testItem}
        Item testItem = new Item().setRegistryName("minecraft:flint");
        try
        {
            logger.info("Trying to register substitution for already substituted object");
            GameRegistry.addSubstitutionAlias("minecraft:flint", GameRegistry.Type.ITEM, testItem);
            //Did not throw existing substitution
            logger.warn("Able to replace minecraft:flint twice");
            return false;
        }
        catch (ExistingSubstitutionException e)
        {
            logger.info("Rejected substitution -> Success");
            return true;
        }
    }

    private boolean testIncompatible()
    {
        Item testItem = new Item();
        try
        {
            logger.info("Trying to register incompatible substitution");
            GameRegistry.addSubstitutionAlias("minecraft:sand", GameRegistry.Type.BLOCK, testItem);
            //Did not throw Incompatible Substitution
            logger.warn("Able to replace sand block with an item");
            return false;
        }
        catch (ExistingSubstitutionException e)
        {
            logger.warn("Sand is already replaced");
            return false;
        }
        catch (IncompatibleSubstitutionException e)
        {
            logger.info("Rejected incompatible substitution -> Success.");
            return true;
        }
    }

    private boolean testItem()
    {
        //Use same registry name as {@link SubstitutionTest#testExisting}
        return replace(GameRegistry.Type.ITEM, GameData.getItemRegistry(), new ResourceLocation("minecraft:flint"), new TestItem());
    }

    private boolean testPotion()
    {
        return replace(GameRegistry.Type.POTION, GameData.getPotionRegistry(), new ResourceLocation("minecraft:slowness"), new TestPotion());
    }

    private boolean testPotionType()
    {
        return replace(GameRegistry.Type.POTION_TYPE, GameData.getPotionTypesRegistry(), new ResourceLocation("minecraft:swiftness"), new TestPotionType());
    }

    private boolean testSoundEvent()
    {
        return replace(GameRegistry.Type.SOUND_EVENT, GameData.getSoundEventRegistry(), new ResourceLocation("minecraft:entity.cow.hurt"),
                new TestSoundEvent());
    }

    /**
     * Has to be public. Otherwise EntityEntry cannot find/use the constructor.
     */
    public static class TestCow extends EntityCow
    {

        public TestCow(World worldIn)
        {
            super(worldIn);
        }

        @Override
        public boolean getAlwaysRenderNameTagForRender()
        {
            return true;
        }

        @Override
        public String getCustomNameTag()
        {
            return "Test_Cow";
        }

        @Override
        public boolean hasCustomName()
        {
            return true;
        }
    }

    private class TestSoundEvent extends SoundEvent
    {

        public TestSoundEvent()
        {
            super(new ResourceLocation("minecraft:block.glass.break"));
        }
    }

    private class TestEnchantment extends EnchantmentDigging
    {

        protected TestEnchantment()
        {
            super(Enchantment.Rarity.COMMON, EntityEquipmentSlot.MAINHAND);
            this.setName("test_substitution");
        }

    }

    private class TestPotion extends Potion
    {

        protected TestPotion()
        {
            super(false, 0x000000);
            this.setPotionName("test_substitution");
        }
    }

    private class TestPotionType extends PotionType
    {
        public TestPotionType()
        {
            super("swiftness", new PotionEffect(MobEffects.SPEED, 600), new PotionEffect(MobEffects.ABSORPTION, 600));
        }
    }

    private class TestBiome extends BiomeOcean
    {

        public TestBiome()
        {
            super(new Biome.BiomeProperties("Ocean").setBaseHeight(-1.0F).setHeightVariation(0.1F).setWaterColor(0xFF0000));
        }

    }

    private class TestBlock extends BlockGravel
    {
        public TestBlock()
        {
            setBlockUnbreakable();
            setUnlocalizedName("gravel");
            setRegistryName("minecraft:gravel");
        }

        /**
         * To make sure itemBlock references the right block
         */
        @Override
        public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced)
        {
            tooltip.add("SubstitutionTestBlock");
        }
    }

    private class TestItem extends Item
    {
        public TestItem()
        {
            setUnlocalizedName("flint").setCreativeTab(CreativeTabs.MATERIALS);
        }

        @Override
        public void addInformation(ItemStack stack, @Nullable World playerIn, List<String> tooltip, ITooltipFlag advanced)
        {
            tooltip.add("SubstitutionTestItem");
        }
    }
}
