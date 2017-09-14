package net.minecraftforge.debug;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;
import java.util.Map.Entry;

@Mod(modid = ForgeBlockStatesLoaderDebug.MODID, name = "ForgeBlockStatesLoader", version = "1.0", acceptableRemoteVersions = "*")
public class ForgeBlockStatesLoaderDebug
{
    public static final String MODID = "forgeblockstatesloader";
    public static final String ASSETS = "forgeblockstatesloader:";

    @ObjectHolder(MODID)
    public static class BLOCKS
    {
        public static final BlockWall custom_wall = null;
    }

    @ObjectHolder(MODID)
    public static class ITEMS
    {
        public static final ItemMultiTexture custom_wall = null;
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().registerAll(
            new BlockWall(Blocks.COBBLESTONE).setUnlocalizedName(MODID + ".customWall").setRegistryName(MODID, "custom_wall")
        );
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(
            new ItemMultiTexture(BLOCKS.custom_wall, BLOCKS.custom_wall, new ItemMultiTexture.Mapper()
            {
                @Override
                public String apply(ItemStack stack)
                {
                    return BlockWall.EnumType.byMetadata(stack.getMetadata()).getUnlocalizedName();
                }
            }).setRegistryName(BLOCKS.custom_wall.getRegistryName())
        );
    }

    //public static final Block blockCustom = new CustomMappedBlock();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        //blockCustom.setUnlocalizedName(MODID + ".customBlock").setRegistryName("customBlock");
        //GameRegistry.registerBlock(blockCustom);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventBusSubscriber(value = Side.CLIENT, modid = MODID)
    public static class ClientEventHandler
    {
        @SubscribeEvent
        public void registerModels(ModelRegistryEvent event)
        {
            //ModelLoader.setCustomStateMapper(blockCustom, new StateMap.Builder().withName(CustomMappedBlock.VARIANT).build());

            ModelLoader.setCustomStateMapper(BLOCKS.custom_wall, new IStateMapper()
            {
                StateMap stateMap = new StateMap.Builder().withName(BlockWall.VARIANT).withSuffix("_wall").build();

                @Override
                public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block block)
                {
                    Map<IBlockState, ModelResourceLocation> map = stateMap.putStateModelLocations(block);
                    Map<IBlockState, ModelResourceLocation> newMap = Maps.newHashMap();

                    for (Entry<IBlockState, ModelResourceLocation> e : map.entrySet())
                    {
                        ModelResourceLocation loc = e.getValue();
                        newMap.put(e.getKey(), new ModelResourceLocation(ASSETS + loc.getResourcePath(), loc.getVariant()));
                    }

                    return newMap;
                }
            });
            ModelLoader.setCustomModelResourceLocation(ITEMS.custom_wall, 0, new ModelResourceLocation(ASSETS + "cobblestone_wall", "inventory"));
            ModelLoader.setCustomModelResourceLocation(ITEMS.custom_wall, 1, new ModelResourceLocation(ASSETS + "mossy_cobblestone_wall", "inventory"));
        }
    }

    // this block is never actually used, it's only needed for the error message on load to see the variant it maps to
    // disabling until we can make it a proper test
    /*public static class CustomMappedBlock extends Block
    {
        public static final PropertyEnum<CustomVariant> VARIANT = PropertyEnum.create("type", CustomVariant.class);

        protected CustomMappedBlock() {
            super(Material.rock);

            this.setUnlocalizedName(MODID + ".customMappedBlock");
        }

        @Override
        protected BlockStateContainer createBlockState() {
            return new BlockStateContainer(this,  VARIANT);
        }

        @Override
        public int getMetaFromState(IBlockState state)
        {
            return ((CustomVariant)state.getValue(VARIANT)).ordinal();
        }

        @Override
        public IBlockState getStateFromMeta(int meta)
        {
            if(meta > CustomVariant.values().length || meta < 0)
                meta = 0;

            return this.getDefaultState().withProperty(VARIANT, CustomVariant.values()[meta]);
        }

        public static enum CustomVariant implements IStringSerializable {
            type_a,
            type_b;

            public String getName() { return this.toString(); };
        }
    }*/
}
