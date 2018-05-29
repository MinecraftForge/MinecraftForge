package net.minecraftforge.debug;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingEvent.*;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

@Mod(modid = "elytraflighttest", name = "Custom Elytra Flight Test", version = "0", clientSideOnly = true)
public class ElytraFlightTest
{

    static final boolean ENABLED = false;

    static final UUID UUID_ELYTRA_BOOTS = UUID.fromString("D468D2A8-A074-497C-AC24-043203938E0C");
    static final UUID UUID_ANTI_ELYTRA_POTION = UUID.fromString("5B03EF4D-4177-4BBC-ADCD-CA585543950C");

    static Item ELYTRA_BOOTS = null;

    static final Potion ANTI_ELYTRA_POTION = new AntiElytraPotion();

    public static class AntiElytraPotion extends Potion
    {
        public AntiElytraPotion()
        {
            super(true, 0xFFFFFF);
            this.setPotionName("antielytrapotion");
            this.registerPotionAttributeModifier(EntityPlayer.ELYTRA_FLIGHT, UUID_ANTI_ELYTRA_POTION.toString(), -1.0D, 2);
            this.setRegistryName(new ResourceLocation("elytraflighttest", "antielytrapotion"));
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent evt)
    {
        if (ENABLED) MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> evt)
    {
        ELYTRA_BOOTS = new ItemArmor(ArmorMaterial.LEATHER, 0, EntityEquipmentSlot.FEET)
                .setUnlocalizedName("elytra_boots").setRegistryName(new ResourceLocation("elytraflighttest", "elytra_boots"))
                .setCreativeTab(CreativeTabs.TOOLS);

        evt.getRegistry().register(ELYTRA_BOOTS);
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent evt)
    {
        if (evt.phase == Phase.START)
        {
            EntityPlayer player = evt.player;
            ItemStack boots = player.inventory.armorInventory.get(0);

            boolean hasBoots = !boots.isEmpty() && boots.getItem() == ELYTRA_BOOTS;

            IAttributeInstance elytraFlight = evt.player.getAttributeMap().getAttributeInstance(EntityPlayer.ELYTRA_FLIGHT);

            if (elytraFlight != null)
            {
                AttributeModifier mod_elytra_boots = elytraFlight.getModifier(UUID_ELYTRA_BOOTS);
                if (mod_elytra_boots != null) elytraFlight.removeModifier(mod_elytra_boots);
                if (hasBoots)
                    elytraFlight.applyModifier(new AttributeModifier(
                            UUID_ELYTRA_BOOTS, "elytra_flight_boots", 1.0D, 0));
            }
        }
    }

    @SubscribeEvent
    public void onJump(LivingJumpEvent evt)
    {
        EntityLivingBase entity = evt.getEntityLiving();
        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
            ItemStack boots = player.inventory.armorInventory.get(0);
            if (!boots.isEmpty() && boots.getItem() == ELYTRA_BOOTS) player.motionY += 5.0D;
        }
    }

    @SubscribeEvent
    public void onFall(LivingFallEvent evt)
    {
        EntityLivingBase entity = evt.getEntityLiving();
        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
            ItemStack boots = player.inventory.armorInventory.get(0);
            if (!boots.isEmpty() && boots.getItem() == ELYTRA_BOOTS)
            {
                evt.setDistance(0);
                evt.setDamageMultiplier(0);
            }
        }
    }

    @SubscribeEvent
    public void registerPotions(RegistryEvent.Register<Potion> evt)
    {
        evt.getRegistry().register(ANTI_ELYTRA_POTION);
    }

    @SubscribeEvent
    public void registerPotionTypes(RegistryEvent.Register<PotionType> evt)
    {
        evt.getRegistry().register(new PotionType(new PotionEffect(ANTI_ELYTRA_POTION, 300))
                .setRegistryName("elytraflighttest", "antielytrapotion"));
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void registerModels(ModelRegistryEvent evt)
    {
        ModelLoader.setCustomModelResourceLocation(ELYTRA_BOOTS, 0,
                new ModelResourceLocation("minecraft:leather_boots", "inventory"));
    }
}
