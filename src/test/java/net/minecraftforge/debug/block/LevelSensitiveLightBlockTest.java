/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.block;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Adds a light source block that can be switched on and off by right-clicking and saves its state in a BlockEntity
 * to test whether level/pos-sensitive light sources work correctly
 */
@Mod(LevelSensitiveLightBlockTest.MOD_ID)
public class LevelSensitiveLightBlockTest
{
    static final String MOD_ID = "level_sensitive_light_block_test";
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MOD_ID);

    private static final RegistryObject<Block> LIGHT_BLOCK = BLOCKS.register("light_block", LightBlock::new);
    private static final RegistryObject<Item> LIGHT_BLOCK_ITEM = ITEMS.register(
            "light_block", () -> new BlockItem(LIGHT_BLOCK.get(), new Item.Properties())
    );
    private static final RegistryObject<BlockEntityType<LightBlockEntity>> LIGHT_BLOCK_ENTITY = BLOCK_ENTITIES.register(
            "light_block", () -> BlockEntityType.Builder.of(LightBlockEntity::new, LIGHT_BLOCK.get()).build(null)
    );

    public LevelSensitiveLightBlockTest()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
    }

    private static class LightBlock extends Block implements EntityBlock
    {
        public LightBlock()
        {
            super(BlockBehaviour.Properties.of());
        }

        @Override
        @SuppressWarnings("deprecation")
        public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
        {
            if (!level.isClientSide() && level.getBlockEntity(pos) instanceof LightBlockEntity be)
            {
                be.switchLight();
            }
            return InteractionResult.SUCCESS;
        }

        @Override
        public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos)
        {
            if (pos == BlockPos.ZERO || (level.getExistingBlockEntity(pos) instanceof LightBlockEntity be && be.lit))
            {
                return 15;
            }
            return 0;
        }

        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
        {
            return new LightBlockEntity(pos, state);
        }
    }

    private static class LightBlockEntity extends BlockEntity
    {
        private boolean lit = false;

        public LightBlockEntity(BlockPos pos, BlockState state)
        {
            super(LIGHT_BLOCK_ENTITY.get(), pos, state);
        }

        public void switchLight()
        {
            setLit(!lit);
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            setChanged();
        }

        private void setLit(boolean lit)
        {
            if (lit != this.lit)
            {
                this.lit = lit;
                level.getLightEngine().checkBlock(worldPosition);
            }
        }

        @Override
        public Packet<ClientGamePacketListener> getUpdatePacket()
        {
            return ClientboundBlockEntityDataPacket.create(this);
        }

        @Override
        public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt)
        {
            CompoundTag tag = pkt.getTag();
            if (tag != null)
            {
                setLit(tag.getBoolean("lit"));
            }
        }

        @Override
        public CompoundTag getUpdateTag()
        {
            CompoundTag tag = super.getUpdateTag();
            tag.putBoolean("lit", lit);
            return tag;
        }

        @Override
        public void handleUpdateTag(CompoundTag tag)
        {
            super.handleUpdateTag(tag);
            lit = tag.getBoolean("lit");
        }

        @Override
        public void load(CompoundTag tag)
        {
            super.load(tag);
            lit = tag.getBoolean("lit");
        }

        @Override
        protected void saveAdditional(CompoundTag tag)
        {
            super.saveAdditional(tag);
            tag.putBoolean("lit", lit);
        }
    }
}
