package net.minecraftforge.fmp.microblock;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IInteractionObject;
import net.minecraftforge.fmp.multipart.MultipartRegistry;

/**
 * The microblock registry. This is where you need to register your microblock materials as well as your micro classes.
 * Also handles the registration of the default micro materials.
 *
 * @see IMicroblock
 * @see IMicroMaterial
 * @see BlockMicroMaterial
 * @see MicroblockClass
 */
public class MicroblockRegistry
{
    
    private static final Map<ResourceLocation, IMicroMaterial> materials = new LinkedHashMap<ResourceLocation, IMicroMaterial>();
    private static final Set<MicroblockClass> microClasses = new HashSet<MicroblockClass>();

    public static void registerMicroClass(MicroblockClass microClass)
    {
        microClasses.add(microClass);
        MultipartRegistry.registerPartFactory(microClass, microClass.getType());
    }

    public static <T extends IMicroMaterial> T registerMaterial(T material)
    {
        if (material == null)
        {
            throw new NullPointerException("Attempting to register a null micro material!");
        }
        ResourceLocation name = material.getType();
        if (materials.containsKey(name))
        {
            throw new IllegalArgumentException("Attempting to register a micro material with a name that's already in use!");
        }
        materials.put(name, material);
        return material;
    }

    public static BlockMicroMaterial registerMaterial(IBlockState blockState, float hardness)
    {
        return registerMaterial(new BlockMicroMaterial(blockState, hardness));
    }

    public static BlockMicroMaterial registerMaterial(IBlockState blockState)
    {
        return registerMaterial(new BlockMicroMaterial(blockState));
    }

    public static BlockMicroMaterial registerMaterial(Block block, int meta)
    {
        return registerMaterial(block.getStateFromMeta(meta));
    }

    public static BlockMicroMaterial[] registerMaterial(Block block, int fromMeta, int toMeta)
    {
        BlockMicroMaterial[] materials = new BlockMicroMaterial[toMeta - fromMeta + 1];
        for (int i = fromMeta; i <= toMeta; i++)
        {
            materials[i - fromMeta] = registerMaterial(block, i);
        }
        return materials;
    }

    public static BlockMicroMaterial registerMaterial(Block block)
    {
        return registerMaterial(block.getDefaultState());
    }

    public static Collection<IMicroMaterial> getRegisteredMaterials()
    {
        return Collections.unmodifiableCollection(materials.values());
    }

    public static IMicroMaterial getMaterial(ResourceLocation name)
    {
        return materials.get(name);
    }

    static
    {
        registerMaterial(Blocks.STONE, 0, 6);
        registerMaterial(Blocks.COBBLESTONE);
        registerMaterial(Blocks.PLANKS, 0, 5);
        registerMaterial(Blocks.LAPIS_BLOCK);
        registerMaterial(Blocks.SANDSTONE, 0, 2);
        registerMaterial(Blocks.WOOL, 0, 15);
        registerMaterial(Blocks.GOLD_BLOCK);
        registerMaterial(Blocks.IRON_BLOCK);
        registerMaterial(Blocks.BRICK_BLOCK);
        registerMaterial(Blocks.BOOKSHELF);
        registerMaterial(Blocks.MOSSY_COBBLESTONE);
        registerMaterial(Blocks.OBSIDIAN);
        registerMaterial(Blocks.DIAMOND_BLOCK);
        registerMaterial(Blocks.PUMPKIN);
        registerMaterial(Blocks.NETHERRACK);
        registerMaterial(Blocks.SOUL_SAND);
        registerMaterial(Blocks.STONEBRICK, 0, 3);
        registerMaterial(Blocks.NETHER_BRICK);
        registerMaterial(Blocks.END_STONE);
        registerMaterial(Blocks.EMERALD_BLOCK);
        registerMaterial(Blocks.QUARTZ_BLOCK, 0, 2);
        registerMaterial(Blocks.STAINED_HARDENED_CLAY, 0, 15);
        registerMaterial(Blocks.PRISMARINE, 0, 2);
        registerMaterial(Blocks.HAY_BLOCK);
        registerMaterial(Blocks.HARDENED_CLAY);
        registerMaterial(Blocks.COAL_BLOCK);
        registerMaterial(Blocks.ICE);
        registerMaterial(Blocks.PACKED_ICE);
        registerMaterial(Blocks.RED_SANDSTONE, 0, 2);
        registerMaterial(Blocks.GLASS);
        registerMaterial(Blocks.STAINED_GLASS, 0, 15);
        registerMaterial(Blocks.LIT_PUMPKIN);
        registerMaterial(Blocks.GLOWSTONE);
        registerMaterial(Blocks.SEA_LANTERN);
        registerMaterial(Blocks.REDSTONE_BLOCK);

        registerMaterial(new BlockMicroMaterial(Blocks.CRAFTING_TABLE.getDefaultState())
                .withDelegate(new Function<Tuple<IMicroblock, Boolean>, MicroblockDelegate>()
                {
                    @Override
                    public MicroblockDelegate apply(Tuple<IMicroblock, Boolean> input)
                    {
                        return new CraftingTableMicroblockDelegate(input.getFirst());
                    }
                }));
    }

    private static final class CraftingTableMicroblockDelegate extends MicroblockDelegate
    {
        
        public CraftingTableMicroblockDelegate(IMicroblock delegated)
        {
            super(delegated);
        }

        @Override
        public EnumActionResult onActivated(EntityPlayer player, EnumHand hand, ItemStack heldItem, RayTraceResult hit)
        {
            if (!delegated.getWorld().isRemote)
            {
                player.displayGui(new InterfaceMicroCraftingTable(delegated));
                player.addStat(StatList.CRAFTING_TABLE_INTERACTION);
            }

            return EnumActionResult.SUCCESS;
        }

        @Override
        public void onRemoved()
        {
        }

    }

    private static class InterfaceMicroCraftingTable implements IInteractionObject
    {
        
        private final IMicroblock microblock;

        public InterfaceMicroCraftingTable(IMicroblock microblock)
        {
            this.microblock = microblock;
        }

        @Override
        public String getName()
        {
            return null;
        }

        @Override
        public boolean hasCustomName()
        {
            return false;
        }

        @Override
        public ITextComponent getDisplayName()
        {
            return new TextComponentTranslation(Blocks.CRAFTING_TABLE.getUnlocalizedName() + ".name");
        }

        @Override
        public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
        {
            return new ContainerWorkbench(playerInventory, microblock.getWorld(), microblock.getPos())
            {
                @Override
                public boolean canInteractWith(EntityPlayer player)
                {
                    BlockPos pos = microblock.getPos();
                    return microblock.getContainer() == null ? false
                            : player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
                }
            };
        }

        @Override
        public String getGuiID()
        {
            return "minecraft:crafting_table";
        }
    }

}
