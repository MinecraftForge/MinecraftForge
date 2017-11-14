package net.minecraftforge.debug;

import java.util.UUID;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
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
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = "elytratest", name = "Custom Elytra Test", version = "0.0.0", clientSideOnly = true)
public class ElytraTest
{
    static final boolean ENABLED = true;

    static final UUID UUID_ELYTRA_BOOTS_FLIGHT = UUID.fromString("D468D2A8-A074-497C-AC24-043203938E0C");
    static final String UUID_ANTI_ELYTRA_POTION = "5B03EF4D-4177-4BBC-ADCD-CA585543950C";

    // items
    static Item ELYTRA_BOOTS = null;

    static final Potion ANTI_ELYTRA_POTION = new AntiElytraPotion();

    public static class AntiElytraPotion extends Potion
    {
        public AntiElytraPotion()
        {
            super(true, 0xFFFF0000);
            this.setPotionName("antielytrapotion");
            this.registerPotionAttributeModifier(EntityPlayer.ELYTRA_FLIGHT, UUID_ANTI_ELYTRA_POTION, -1D, 2);
            this.setRegistryName(new ResourceLocation("elytratest", "antielytrapotion"));
        }

    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event)
    {
        ELYTRA_BOOTS = new ItemArmor(ArmorMaterial.LEATHER, 0, EntityEquipmentSlot.FEET)
                .setUnlocalizedName("elytra_boots").setRegistryName(new ResourceLocation("elytratest", "elytra_boots"))
                .setCreativeTab(CreativeTabs.COMBAT);

        event.getRegistry().register(ELYTRA_BOOTS);
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event)
    {
        if (event.phase == Phase.START)
        {
            EntityPlayer player = event.player;
            ItemStack boots = player.inventory.armorInventory.get(0);

            boolean hasBoots = !boots.isEmpty() && boots.getItem() == ELYTRA_BOOTS;

            IAttributeInstance attributeElytraFlight = event.player.getAttributeMap()
                    .getAttributeInstance(EntityPlayer.ELYTRA_FLIGHT);

            if (attributeElytraFlight != null)
            {
                AttributeModifier mod_elytraboots = attributeElytraFlight.getModifier(UUID_ELYTRA_BOOTS_FLIGHT);
                if (mod_elytraboots != null)
                {
                    attributeElytraFlight.removeModifier(mod_elytraboots);
                }

                if (hasBoots)
                {
                    attributeElytraFlight.applyModifier(
                            new AttributeModifier(UUID_ELYTRA_BOOTS_FLIGHT, "ElytraFlightBoots", 1.0D, 0));
                }
            }

        }
    }

    @SubscribeEvent
    public void onJump(LivingJumpEvent event)
    {
        EntityLivingBase elb = event.getEntityLiving();
        if (elb instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) elb;
            ItemStack boots = player.inventory.armorInventory.get(0);

            boolean hasBoots = !boots.isEmpty() && boots.getItem() == ELYTRA_BOOTS;
            if (hasBoots)
            {
                player.motionY += 5.0D;
            }
        }
    }

    @SubscribeEvent
    public void onFall(LivingFallEvent event)
    {
        EntityLivingBase elb = event.getEntityLiving();
        if (elb instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) elb;
            ItemStack boots = player.inventory.armorInventory.get(0);

            boolean hasBoots = !boots.isEmpty() && boots.getItem() == ELYTRA_BOOTS;
            if (hasBoots)
            {
                event.setDistance(0f);
                event.setDamageMultiplier(0f);
            }
        }
    }

    @SubscribeEvent
    public void registerPotions(RegistryEvent.Register<Potion> event)
    {
        event.getRegistry().register(ANTI_ELYTRA_POTION);
    }

    @SubscribeEvent
    public void registerPotionTypes(RegistryEvent.Register<PotionType> event)
    {
        event.getRegistry().register(new PotionType(new PotionEffect(ANTI_ELYTRA_POTION, 300))
                .setRegistryName("elytratest", "antieyltrapotion"));
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event)
    {
        ModelLoader.setCustomModelResourceLocation(ELYTRA_BOOTS, 0,
                new ModelResourceLocation("minecraft:leather_boots", "inventory"));
    }
}
