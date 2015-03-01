package net.minecraftforge.newmaterialex;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreNamePredicate;

@Mod(modid = "newmaterialex")
@ObjectHolder("newmaterialex")
public class NewMaterialExampleMod
{
    public static final Block neowood_plank = null;
    public static final Item neocreature_hide = null;
    public static final Item neoium_ingot = null;
    public static final Item neoium_sword = null;
    public static final Item neoium_helmet = null;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        // new creative tab
        new CreativeTabs("newmaterialex")
        {
            @Override
            public Item getTabIconItem()
            {
                return Items.nether_wart;
            }
            @Override
            public void displayAllReleventItems(List list)
            {
                // give a list of items required for the demonstration
                list.add(new ItemStack(Blocks.anvil));

                list.add(new ItemStack(neowood_plank));
                list.add(new ItemStack(Items.wooden_sword, 1, 59));

                list.add(new ItemStack(neocreature_hide));
                list.add(new ItemStack(Items.leather_helmet, 1, 55));

                list.add(new ItemStack(neoium_ingot));
                list.add(new ItemStack(neoium_sword, 1, 99));
                list.add(new ItemStack(neoium_helmet, 1, 110));
            }
        };

        // register material items
        Block b = new Block(Material.wood) {}.setUnlocalizedName("neowoodPlank");
        GameRegistry.registerBlock(b, "neowood_plank");
        GameRegistry.registerItem(new Item().setUnlocalizedName("neocreature_hide"), "neocreature_hide");
        GameRegistry.registerItem(new Item().setUnlocalizedName("neoiumIngot"), "neoium_ingot");

        // construct instances for ToolMaterial and ArmorMaterial
        ToolMaterial toolMaterialNeoium = EnumHelper
                .addToolMaterial("NEOIUM", 10, 99, 10, 10, 10)
                .setRepairMaterial(new OreNamePredicate("ingotNeoium"));
        ArmorMaterial armorMaterialNeoium = EnumHelper
                .addArmorMaterial("NEOIUM", "newmaterialex:neoium", 10, new int[] {10, 10, 10, 10}, 10)
                .setRepairMaterial(toolMaterialNeoium.getRepairMaterial());

        // register equipment items
        Item i = new ItemSword(toolMaterialNeoium).setUnlocalizedName("neoiumSword");
        GameRegistry.registerItem(i, "neoium_sword");
        i = new ItemArmor(armorMaterialNeoium, -1, 0).setUnlocalizedName("neoiumHelmet");
        GameRegistry.registerItem(i, "neoium_helmet");
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // register material items with their OreDictionary names
        // wooden_sword is repairable with neowood_plank with this
        OreDictionary.registerOre("plankWood", neowood_plank);

        // leather_helmet is repairable with neocreature_hide with this
        OreDictionary.registerOre("leather", neocreature_hide);

        // neoium_sword / neoium_helmet is repairable with neoium_ingot with this
        OreDictionary.registerOre("ingotNeoium", neoium_ingot);
    }
}