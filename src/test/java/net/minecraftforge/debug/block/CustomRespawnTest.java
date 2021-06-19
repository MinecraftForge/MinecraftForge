package net.minecraftforge.debug.block;

import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(CustomRespawnTest.MODID)
public class CustomRespawnTest
{
    public static final boolean ENABLE = true;
    public static final String MODID = "custom_respawn_test";

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final RegistryObject<Block> TEST_RESPAWN_BLOCK = BLOCKS.register("test_respawn_block", () -> new CustomRespawnBlock(Block.Properties.of(Material.WOOD)));

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<Item> TEST_RESPAWN_BLOCK_ITEM = ITEMS.register("test_respawn_block", () -> new BlockItem(TEST_RESPAWN_BLOCK.get(), (new Item.Properties())));

    public CustomRespawnTest()
    {
        if (ENABLE)
        {
            final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
            BLOCKS.register(eventBus);
            ITEMS.register(eventBus);
        }
    }

    public static class CustomRespawnBlock extends Block
    {

        public CustomRespawnBlock(Properties propertiesIn)
        {
            super(propertiesIn);
        }

        @Override
        public ActionResultType use(BlockState p_225533_1_, World world, BlockPos pos, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_)
        {
            if (!world.isClientSide && p_225533_4_ instanceof ServerPlayerEntity)
            {
                ((ServerPlayerEntity)p_225533_4_).setRespawnPosition(world.dimension(), pos, 0, false, true);
            }
            return ActionResultType.sidedSuccess(world.isClientSide);
        }

        @Override
        public Optional<Vector3d> getRespawnPosition(BlockState state, EntityType<?> type, IWorldReader world, BlockPos pos, float orientation, @Nullable LivingEntity entity)
        {
            return RespawnAnchorBlock.findStandUpPosition(type, world, pos);
        }
    }

}
