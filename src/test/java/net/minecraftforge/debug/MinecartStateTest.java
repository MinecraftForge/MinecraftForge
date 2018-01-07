package net.minecraftforge.debug;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRail;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import javax.annotation.Nullable;

/**
 * A test for {@link EntityMinecart#handleRider(Entity, int)}
 * and {@link BlockRailBase#determineCartState(World, BlockPos, IBlockState, EntityMinecart)}.
 */
@Mod(modid = MinecartStateTest.MOD_ID, name = "Minecart State Test")
public class MinecartStateTest
{
    static final String MOD_ID = "minecart_state_test";
    static final boolean ENABLED = true;
    BlockRailBase railBlock;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (!ENABLED)
            return;
        MinecraftForge.EVENT_BUS.register(this);
        ModMetadata meta = event.getModMetadata();
        meta.description = "enabled"; // Allows screenshots showing this is enabled
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event)
    {
        railBlock = new MyRail();
        railBlock.setRegistryName(MOD_ID, "rail_block");
        event.getRegistry().register(railBlock);
    }

    @SubscribeEvent
    public void registerEntities(RegistryEvent.Register<EntityEntry> event)
    {
        EntityRegistry.registerModEntity(new ResourceLocation(MOD_ID, "my_cart"), MyCart.class, "my_cart", 0, this, 80, 3, true);
    }

    private static class MyRail extends BlockRail
    {
        @Override
        public int determineCartState(World world, BlockPos pos, IBlockState state, EntityMinecart cart)
        {
            return EntityMinecart.SLOWING_MASK;
        }
    }

    public static class MyCart extends EntityMinecartEmpty
    {

        public MyCart(World worldIn)
        {
            super(worldIn);
        }

        @Override
        protected int handleRider(@Nullable Entity entity, int flags)
        {
            int ret = super.handleRider(entity, flags);
            if (entity instanceof EntityPlayer)
                ret |= BOOSTING_MASK;
            return ret;
        }

        @Override
        public boolean isPoweredCart()
        {
            return false;
        }

        @Override
        public boolean canBeRidden()
        {
            return true;
        }
    }
}
