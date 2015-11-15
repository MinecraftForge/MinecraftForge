package net.minecraftforge.test;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityVillager.EmeraldForItems;
import net.minecraft.entity.passive.EntityVillager.PriceInfo;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

@Mod(modid = "VillagerTest", name = "SimpleVillagerTest", version = "1.0")
public class SimpleVillagerTest {

    public static final VillagerRegistry.VillagerProfession PROFESSION = new VillagerRegistry.VillagerProfession("Derpington", "minecraft:textures/entity/villager/farmer.png");
    public static final VillagerRegistry.VillagerCareer CAREER = new VillagerRegistry.VillagerCareer(PROFESSION, "Derpington");

    @EventHandler
    public void onInit(FMLPostInitializationEvent event) {
        VillagerRegistry.instance().register(PROFESSION);
        //add a trade for the career, using an addTrade(ITradeList trade, int careerLevel) method in VillagerCareer
        CAREER.addTrade(new EmeraldForItems(Item.getItemFromBlock(Blocks.dirt), new PriceInfo(1, 1)), 0);
        CAREER.addTrade(new EmeraldForItems(Item.getItemFromBlock(Blocks.stone), new PriceInfo(1, 1)), 0);
        CAREER.addTrade(new EmeraldForItems(Item.getItemFromBlock(Blocks.dirt), new PriceInfo(5, 5)), 1);
        CAREER.addTrade(new EmeraldForItems(Item.getItemFromBlock(Blocks.stone), new PriceInfo(5, 5)), 1);
        CAREER.addTrade(new EmeraldForItems(Item.getItemFromBlock(Blocks.dirt), new PriceInfo(10, 10)), 2);
        CAREER.addTrade(new EmeraldForItems(Item.getItemFromBlock(Blocks.stone), new PriceInfo(10, 10)), 2);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void spawning(EntityInteractEvent event) {
        //Set all spawning villagers to new profession for a test, 
        //in reality, these methods will be used when the villager is constructed elsewhere for spawning.
        if (event.target instanceof EntityVillager && !event.entity.worldObj.isRemote
                && event.entityPlayer.getHeldItem() != null 
                && event.entityPlayer.getHeldItem().getItem() == Items.stick) {
            EntityVillager villager = (EntityVillager) event.target;
            
            event.entityPlayer.addChatMessage(new ChatComponentText(villager.getCareer()+" "+villager.getProfession()));
           
        }
    }
    
    @SubscribeEvent
    public void interact(PlayerInteractEvent event) {
        //Sneak right click with stick to spawn in the new villager
        if (event.entityPlayer.getHeldItem() != null && event.entityPlayer.isSneaking() && !event.world.isRemote
                && event.entityPlayer.getHeldItem().getItem() == Items.stick) {
            
            EntityVillager villager = new EntityVillager(event.world, PROFESSION.getId());
            
            /*
             * This VillagerProfession.getId() should probably check a value set while regsitering the profession
             * with VillagerRegistry.instance().register(PROFESSION);
             */
            villager.setProfession(PROFESSION.getId());
            
            /**
             * If this Method isn't called, it should randomly select a career from the list of careers for that
             * profession, every time new VillagerRegistry.VillagerCareer(PROFESSION, "Derpington"); is called,
             * it should add that new VillageCareer to a list for PROFESSION
             */
            villager.setCareer(CAREER.getId());
            
            villager.setPosition(event.entity.posX + 1, event.entity.posY, event.entity.posZ);
            event.world.spawnEntityInWorld(villager);
        }
    }
}
