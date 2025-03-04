/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import com.mojang.authlib.GameProfile;

import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.Connection;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.GameProtocols;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ForgeI18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

public interface IForgeGameTestHelper {
    private GameTestHelper self() {
        return (GameTestHelper) this;
    }

    default void say(String message) {
        this.say(message, Style.EMPTY);
    }

    default void say(String message, Style style) {
        this.say(this.getMessage(message).withStyle(style));
    }

    default void say(String message, ChatFormatting style) {
        this.say(this.getMessage(message).withStyle(style));
    }

    default void say(String message, int color) {
        this.say(this.getMessage(message).withColor(color));
    }

    default void say(Component component) {
        this.self().getLevel().players().forEach(p -> p.sendSystemMessage(component));
    }

    private MutableComponent getMessage(String message) {
        return ForgeI18n.getPattern(message) != null ? Component.translatable(message) : Component.literal(message);
    }

    default void assertTrue(boolean value, Supplier<String> message) {
        if (!value)
            throw new GameTestAssertException(message.get());
    }

    default void assertFalse(boolean value, Supplier<String> message) {
        if (value)
            throw new GameTestAssertException(message.get());
    }

    default <N> void assertValueEqual(N expected, N actual, String name, String message) {
        this.assertValueEqual(expected, actual, name, () -> message);
    }

    default <N> void assertValueEqual(N expected, N actual, String name, Supplier<String> message) {
        if (!Objects.equals(expected, actual))
            throw new GameTestAssertException("%s -- Expected %s to be %s, but was %s".formatted(message.get(), name, expected, actual));
    }

    default <N> void assertValueEqual(N[] expected, N[] actual, String name, String message) {
        this.assertValueEqual(expected, actual, name, () -> message);
    }

    default <N> void assertValueEqual(N[] expected, N[] actual, String name, Supplier<String> message) {
        if (!Objects.deepEquals(expected, actual))
            throw new GameTestAssertException("%s -- Expected %s to be %s, but was %s".formatted(message.get(), name, Arrays.toString(expected), Arrays.toString(actual)));
    }

    default <N> void assertValueNotEqual(N expected, N actual, String name, String message) {
        this.assertValueNotEqual(expected, actual, name, () -> message);
    }

    default <N> void assertValueNotEqual(N expected, N actual, String name, Supplier<String> message) {
        if (Objects.equals(expected, actual))
            throw new GameTestAssertException("%s -- Expected %s to NOT be %s, but was".formatted(message.get(), name, expected));
    }

    default <N> void assertValueNotEqual(N[] expected, N[] actual, String name, String message) {
        this.assertValueNotEqual(expected, actual, name, () -> message);
    }

    default <N> void assertValueNotEqual(N[] expected, N[] actual, String name, Supplier<String> message) {
        if (!Objects.deepEquals(expected, actual))
            throw new GameTestAssertException("%s -- Expected %s to NOT be %s, but was".formatted(message.get(), name, Arrays.toString(expected)));
    }

    default <E> Registry<E> registryLookup(ResourceKey<? extends Registry<? extends E>> registryKey) {
        return this.self().getLevel().registryAccess().lookupOrThrow(registryKey);
    }

    default ServerPlayer makeMockServerPlayer() {
        return makeMockServerPlayer(true);
    }

    default ServerPlayer makeMockServerPlayer(boolean creative) {
        var level = self().getLevel();
        var cookie = CommonListenerCookie.createInitial(new GameProfile(UUID.randomUUID(), "test-mock-player"), false);
        var player = new ServerPlayer(level.getServer(), level, cookie.gameProfile(), cookie.clientInformation()) {
            public boolean isSpectator() {
                return false;
            }

            public boolean isCreative() {
                return creative;
            }
        };
        var connection = new Connection(PacketFlow.SERVERBOUND);
        @SuppressWarnings("unused") // The constructor has side effects
        var channel = new EmbeddedChannel(connection);
        var server = level.getServer();

        var listener = new ServerGamePacketListenerImpl(server, connection, player, cookie);
        var info = GameProtocols.SERVERBOUND_TEMPLATE.bind(RegistryFriendlyByteBuf.decorator(server.registryAccess()));
        connection.setupInboundProtocol(info, listener);
        return player;
    }

    /**
     * Registers an event listener that will be unregistered when the test is finished running.
     */
    default <E extends Event> void addEventListener(Consumer<E> consumer) {
        MinecraftForge.EVENT_BUS.addListener(consumer);
        self().addCleanup(success -> MinecraftForge.EVENT_BUS.unregister(consumer));
    }

    /**
     * Registers an event listener that will be unregistered when the test is finished running.
     */
    default void registerEventListener(Object handler) {
        MinecraftForge.EVENT_BUS.register(handler);
        self().addCleanup(success -> MinecraftForge.EVENT_BUS.unregister(handler));
    }

    /**
     * Creates a floor of stone blocks at the bottom of the test area.
     */
    default void makeFloor() {
        makeFloor(Blocks.STONE);
    }

    /**
     * Creates a floor of the specified block under the test area.
     */
    default void makeFloor(Block block) {
        makeFloor(block, -1);
    }

    /**
     * Creates a floor of the specified block at the specified height.
     */
    default void makeFloor(Block block, int height) {
        var bounds = self().getBounds();
        var pos = new BlockPos.MutableBlockPos();
        for (int x = 0; x < (int) bounds.getXsize(); x++) {
            for (int y = 0; y < (int) bounds.getZsize(); y++) {
                pos.set(x, height, y);
                if (self().getBlockState(pos).is(Blocks.AIR))
                    self().setBlock(pos, block);
            }
        }
    }

    default void setAndAssertBlock(int x, int y, int z, Block block) {
        this.setAssertAndGetBlock(x, y, z, block);
    }

    default void setAndAssertBlock(int x, int y, int z, BlockState state) {
        this.setAssertAndGetBlock(x, y, z, state);
    }

    default void setAndAssertBlock(BlockPos pos, Block block) {
        this.setAssertAndGetBlock(pos, block);
    }

    default void setAndAssertBlock(BlockPos pos, BlockState state) {
        this.setAssertAndGetBlock(pos, state);
    }

    default BlockState setAssertAndGetBlock(int x, int y, int z, Block block) {
        return this.setAssertAndGetBlock(x, y, z, block.defaultBlockState());
    }

    default BlockState setAssertAndGetBlock(int x, int y, int z, BlockState state) {
        return this.setAssertAndGetBlock(new BlockPos(x, y, z), state);
    }

    default BlockState setAssertAndGetBlock(BlockPos pos, Block block) {
        return this.setAssertAndGetBlock(pos, block.defaultBlockState());
    }

    default BlockState setAssertAndGetBlock(BlockPos pos, BlockState state) {
        this.assertTrue(
                this.self().getLevel().setBlock(this.self().absolutePos(pos), state, Block.UPDATE_ALL),
                () -> "Failed to set block at pos %s : %s".formatted(pos, state.getBlock())
        );
        return state;
    }

    default void removeAllItemEntitiesInRange(BlockPos pos, double range) {
        BlockPos blockpos = this.self().absolutePos(pos);
        for (ItemEntity itemEntity : this.self().getLevel().getEntities(EntityType.ITEM, new AABB(blockpos).inflate(range), Entity::isAlive)) {
            itemEntity.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    default <T> Flag<T> flag(String name) {
        return new Flag<>(name);
    }

    default IntFlag intFlag(String name) {
        return new IntFlag(name);
    }

    default IntFlag intFlag(String name, int value) {
        return this.intFlag(name, (long) value);
    }

    default IntFlag intFlag(String name, long value) {
        return Util.make(new IntFlag(name), flag -> flag.set(value));
    }

    default BoolFlag boolFlag(String name) {
        return new BoolFlag(name);
    }

    public static class Flag<T> {
        private final String name;
        @Nullable
        protected T value = null;

        public Flag(String name) {
            this.name = name;
        }

        public void set(T value) {
            this.value = value;
        }

        @Nullable
        public T get() {
            return this.value;
        }

        public void assertUnset() {
            this.assertUnset((Supplier<String>) null);
        }

        public void assertUnset(String message) {
            this.assertUnset(message != null ? () -> message : null);
        }

        public void assertUnset(Supplier<String> message) {
            if (this.value != null) {
                String s = message != null ? message.get() + " -- " : "";
                throw new GameTestAssertException(s + "Expected " + name + " to be null, but was " + this.value);
            }
        }

        public void assertSet() {
            this.assertSet((Supplier<String>) null);
        }

        public void assertSet(String message) {
            this.assertSet(message != null ? () -> message : null);
        }

        public void assertSet(Supplier<String> message) {
            if (this.value == null) {
                String s = message != null ? message.get() + " -- " : "";
                throw new GameTestAssertException(s + "Flag " + name + " was never set");
            }
        }

        public void assertEquals(T expected) {
            this.assertEquals(expected, (Supplier<String>) null);
        }

        public void assertEquals(T expected, String message) {
            this.assertEquals(expected, message != null ? () -> message : null);
        }

        public void assertEquals(T expected, Supplier<String> message) {
            assertSet(message);
            if (expected != null && !expected.equals(this.value)) {
                String s = message != null ? message.get() + " -- " : "";
                throw new GameTestAssertException(s + "Expected " + name + " to be " + expected + ", but was " + this.value);
            }
        }
    }

    public static class IntFlag extends Flag<Long> {
        public IntFlag(String name) {
            super(name);
        }

        public void set(long value) {
            super.set(value);
        }

        public void increment() {
            this.decrement(1L);
        }

        public void increment(int amount) {
            this.decrement((long) amount);
        }

        public void increment(long amount) {
            if (this.value != null)
                this.set(this.value + amount);
        }

        public void decrement() {
            this.decrement(1L);
        }

        public void decrement(int amount) {
            this.decrement((long) amount);
        }

        public void decrement(long amount) {
            if (this.value != null)
                this.set(this.value - amount);
        }

        public byte getByte() {
            return this.value == null ? -1 : this.value.byteValue();
        }

        public int getInt() {
            return this.value == null ? -1 : this.value.intValue();
        }

        public long getLong() {
            return this.value == null ? -1 : this.value.longValue();
        }

        public void assertEquals(int expected) {
            super.assertEquals((long) expected);
        }

        public void assertEquals(long expected) {
            super.assertEquals(expected);
        }

        public void assertEquals(int expected, String message) {
            super.assertEquals((long) expected, message);
        }

        public void assertEquals(long expected, String message) {
            super.assertEquals(expected, message);
        }

        public void assertEquals(int expected, Supplier<String> message) {
            super.assertEquals((long) expected, message);
        }

        public void assertEquals(long expected, Supplier<String> message) {
            super.assertEquals(expected, message);
        }
    }

    public static class BoolFlag extends Flag<Boolean> {
        public BoolFlag(String name) {
            super(name);
        }

        public void set(boolean value) {
            super.set(value);
        }

        public boolean getBool() {
            return this.value != null && this.value;
        }

        public void assertEquals(boolean expected) {
            super.assertEquals(expected);
        }

        public void assertEquals(boolean expected, String message) {
            super.assertEquals(expected, message);
        }

        public void assertEquals(boolean expected, Supplier<String> message) {
            super.assertEquals(expected, message);
        }
    }
}
