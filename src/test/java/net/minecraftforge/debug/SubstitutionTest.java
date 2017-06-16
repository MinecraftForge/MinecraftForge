package net.minecraftforge.debug;

import net.minecraft.block.BlockGravel;
import net.minecraft.block.SoundType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentArrowDamage;
import net.minecraft.enchantment.EnchantmentDamage;
import net.minecraft.enchantment.EnchantmentDigging;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeOcean;
import net.minecraft.world.biome.BiomePlains;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.*;
import org.apache.logging.log4j.Logger;

/**
 * TODO maybe check ExistingSubstitutionException as well as IncompatibleSubstitutionException (probably in a test test)
 * To verify this is working
 * 1. Check the log for 'Substitution tests successful' resp. 'One or more substitution tests failed'
 * 2. Check the following replacements:
 * a) '/effect <player> minecraft:slowness' -> You should get 'test_substitution' effect
 * b) Select a speed potion out of the creative tab and drink it -> You should get swiftness and absorption
 * c) Equip a pickaxe and use '/enchant <player> minecraft:efficiency' -> You should get a 'test_substitution' enchantment
 * d) Summon a cow and hit it. It should make a glass break sound
 * e) Summon a cow. It should always render a name tag
 * d) Look for an ocean biome. The water should be red
 */
@Mod(modid = "forge.testsubmod", name = "Forge Substitution Test", version = "1.0", acceptableRemoteVersions = "*")
public class SubstitutionTest
{
    public static boolean enabled=true;

    private static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent evt){
        logger=evt.getModLog();
        if(enabled){
            boolean success= testPotion() && testPotionType() && testEnchantment() && testSoundEvent() && testEntity() && testBiome() && testBlock();

            if(success){
                logger.info("Substitution tests successful");
            }
            else{
                FMLLog.bigWarning("One or more substitution tests failed");
            }
        }
    }



    private boolean testPotion(){
        return replace(GameRegistry.Type.POTION,GameData.getPotionRegistry(),new ResourceLocation("minecraft:slowness"),new TestPotion() );
    }

    private boolean testPotionType(){
        ResourceLocation typeId=new ResourceLocation("minecraft:swiftness");
        return replace(GameRegistry.Type.POTION_TYPE, GameData.getPotionTypesRegistry(),typeId,new PotionType(typeId.getResourcePath(),new PotionEffect(MobEffects.SPEED,600),new PotionEffect(MobEffects.ABSORPTION,600)));
    }

    private boolean testEnchantment(){
        return replace(GameRegistry.Type.ENCHANTMENT, GameData.getEnchantmentRegistry(),new ResourceLocation("minecraft:efficiency"),new TestEnchantment());
    }

    private boolean testSoundEvent(){
        return replace(GameRegistry.Type.SOUND_EVENT, GameData.getSoundEventRegistry(),new ResourceLocation("minecraft:entity.cow.hurt"),new SoundEvent(new ResourceLocation("minecraft:block.glass.break")));
    }

    private boolean testEntity(){
        return replace(GameRegistry.Type.ENTITY, GameData.getEntityRegistry(), new ResourceLocation("minecraft:cow"),new EntityEntry(TestCow.class,"Cow"));
    }

    private boolean testBiome(){
        return replace(GameRegistry.Type.BIOME, GameData.getBiomeRegistry(), new ResourceLocation("minecraft:ocean"),new TestBiome());
    }

    private boolean testBlock(){
        return replace(GameRegistry.Type.BLOCK, GameData.getBlockRegistry(), new ResourceLocation("minecraft:gravel"),new TestBlock());
    }


    /**
     * Try to register a substitution and verify the success. Logs any problems.
     * @return Success
     */
    private <T extends IForgeRegistryEntry<T>> boolean replace(GameRegistry.Type type, IForgeRegistry<T> registry, ResourceLocation id, T testObject){
        T oldEntry = registry.getValue(id);
        try
        {
            GameRegistry.addSubstitutionAlias(id.toString(),type,testObject);
            T newEntry = registry.getValue(id);
            if(oldEntry.equals(newEntry)){
                logger.warn("[%s]Did not replace %s(%s) with %s",type,oldEntry,id,testObject);
                return false;
            }
            else if(testObject.equals(newEntry)){
                return true;
            }
            else{
                //To make sure we are not missing anything here
                logger.warn("[%s]Something went wrong while replacing %s(%s) with %s. Got %s",type,oldEntry,id,testObject,newEntry);
                return false;
            }
        }
        catch (ExistingSubstitutionException e)
        {
            logger.error("[%s]Failed to register substitution for %s",type,id);
            logger.catching(e);
        }
        return false;
    }


    private class TestEnchantment extends EnchantmentDigging{

        protected TestEnchantment()
        {
            super(Enchantment.Rarity.COMMON, EntityEquipmentSlot.MAINHAND);
            this.setName("test_substitution");
        }

    }


    private class TestPotion extends Potion{

        protected TestPotion()
        {
            super(false, 0x000000);
            this.setPotionName("test_substitution");
        }
    }

    /**
     * Has to be public. Otherwise EntityEntry cannot find/use the constructor.
     *
     * If newly registered and a existing world is loaded, all existent cows will be substituted with this class.
     * If removed from mod code, all substituted entities will be removed TODO Test without PersistentRegistryManager
     */
    public static class TestCow extends EntityCow{

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

        @Override public boolean hasCustomName()
        {
            return true;
        }
    }

    private class TestBiome extends BiomeOcean{

        public TestBiome()
        {
            super(new Biome.BiomeProperties("Ocean").setBaseHeight(-1.0F).setHeightVariation(0.1F).setWaterColor(0xFF0000));
        }

    }

    private class TestBlock extends BlockGravel{
        public TestBlock()
        {
            setBlockUnbreakable();
            setUnlocalizedName("gravel");
        }
    }
}
