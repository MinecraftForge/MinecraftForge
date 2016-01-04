package net.minecraftforge.test;

import java.util.Set;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityVillager.EmeraldForItems;
import net.minecraft.entity.passive.EntityVillager.PriceInfo;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

@Mod(modid = "villagertest", name = "SimpleVillagerTest", version = "1.0")
public class SimpleVillagerTest {

	public VillagerRegistry.VillagerProfession PROFESSION;
	public VillagerRegistry.VillagerProfession PROFESSION2;
	public VillagerRegistry.VillagerCareer CAREER;
	public VillagerRegistry.VillagerCareer CAREER2;
	public VillagerRegistry.VillagerCareer CAREER3;
	public VillagerRegistry.VillagerProfession PROFESSION3;

	@EventHandler
	public void onInit(FMLPostInitializationEvent event) {
		PROFESSION = new VillagerRegistry.VillagerProfession("derpington",
				"villagertest:textures/entity/villager/derpington.png");
		PROFESSION2 = new VillagerRegistry.VillagerProfession("herpington",
				"villagertest:textures/entity/villager/herpington.png");
		PROFESSION3 = new VillagerRegistry.VillagerProfession("lerpington",
				"villagertest:textures/entity/villager/lerpington.png");
		CAREER = new VillagerRegistry.VillagerCareer(PROFESSION, "Derpington");
		CAREER2 = new VillagerRegistry.VillagerCareer(PROFESSION2, "Herpington");
		CAREER3 = new VillagerRegistry.VillagerCareer(PROFESSION3, "Lerpington");

		// Comment these in and out for testing removing/adding professions from
		// existing saves
		VillagerRegistry.instance().register(PROFESSION2);
		VillagerRegistry.instance().register(PROFESSION3);
		VillagerRegistry.instance().register(PROFESSION);

		// add a trade for the career, using an addTrade(ITradeList trade, int
		// careerLevel) method in VillagerCareer
		CAREER.addTrade(new EmeraldForItems(Item.getItemFromBlock(Blocks.dirt), new PriceInfo(1, 1)), 0);
		CAREER.addTrade(new EmeraldForItems(Item.getItemFromBlock(Blocks.stone), new PriceInfo(1, 1)), 0);
		CAREER.addTrade(new EmeraldForItems(Item.getItemFromBlock(Blocks.dirt), new PriceInfo(5, 5)), 1);
		CAREER.addTrade(new EmeraldForItems(Item.getItemFromBlock(Blocks.stone), new PriceInfo(5, 5)), 1);
		CAREER.addTrade(new EmeraldForItems(Item.getItemFromBlock(Blocks.dirt), new PriceInfo(10, 10)), 2);
		CAREER.addTrade(new EmeraldForItems(Item.getItemFromBlock(Blocks.stone), new PriceInfo(10, 10)), 2);
		CAREER2.addTrade(new EmeraldForItems(Item.getItemFromBlock(Blocks.stone), new PriceInfo(10, 10)), 0);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void interact(EntityInteractEvent event) {
		// Set all spawning villagers to new profession for a test,
		// in reality, these methods will be used when the villager is
		// constructed elsewhere for spawning.
		if (event.target instanceof EntityVillager && !event.entity.worldObj.isRemote
				&& event.entityPlayer.getHeldItem() != null
				&& event.entityPlayer.getHeldItem().getItem() == Items.stick) {
			EntityVillager villager = (EntityVillager) event.target;

			event.entityPlayer
					.addChatMessage(new ChatComponentText(villager.getCareer() + " " + villager.getProfession() + " "
							+ net.minecraftforge.fml.common.registry.VillagerRegistry
									.getProfession(villager.getProfession()).getName()
							+ " " + net.minecraftforge.fml.common.registry.VillagerRegistry
									.getProfession(villager.getProfession()).getId()));
			System.out
					.println(net.minecraftforge.fml.common.registry.VillagerRegistry.instance().professions.getKeys());
			/**
			 * Shift right click a villager to set their profession to the one
			 * specified here
			 */
			if (event.entityPlayer.isSneaking()) {
				/*
				 * This VillagerProfession.getId() should probably check a value
				 * set while regsitering the profession with
				 * VillagerRegistry.instance().register(PROFESSION);
				 */
				int toSet = PROFESSION3.getId();

				Set<ResourceLocation> keys = net.minecraftforge.fml.common.registry.VillagerRegistry
						.instance().professions.getKeys();
				for (ResourceLocation s : keys) {
					VillagerRegistry.VillagerProfession prof = net.minecraftforge.fml.common.registry.VillagerRegistry
							.getProfession(s.getResourcePath());
					int id = net.minecraftforge.fml.common.registry.VillagerRegistry.instance().professions.getId(prof);
					System.out.println(prof.getName() + " " + prof.getId() + " " + id + " " + prof.texture);
				}

				System.out.println("id of " + toSet);
				villager.setProfession(toSet);
			}

		}
	}
}
