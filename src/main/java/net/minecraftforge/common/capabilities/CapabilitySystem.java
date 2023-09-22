/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeItem;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.EventSubclassTransformer;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.spongepowered.asm.service.MixinService;

import java.io.IOException;
import java.util.HashMap;
import java.util.function.Consumer;


// TODO: USE ASM
public class CapabilitySystem {
    private static final HashMap<Class<?>, Class<? extends Event>> FIND = new HashMap<>();
    private static final Logger LOGGER = LogUtils.getLogger();

    static {
        FIND.put(Item.class, AttachCapabilitiesEvent.AttachItemEvent.class);
        FIND.put(Entity.class, AttachCapabilitiesEvent.AttachEntityEvent.class);
        FIND.put(BlockEntity.class, AttachCapabilitiesEvent.AttachBlockEntityEvent.class);
        FIND.put(Level.class, AttachCapabilitiesEvent.AttachLevelEvent.class);
        FIND.put(LevelChunk.class, AttachCapabilitiesEvent.AttachLevelChunkEvent.class);
    }

    public static <T extends Event> void register(Class<?> type, Consumer<T> eventConsumer) {
        CapabilityEventProviderTransformer transformer = new CapabilityEventProviderTransformer();
        ClassNode node = null;
        try {
            node = MixinService.getService().getBytecodeProvider().getClassNode(IForgeItem.class.getName());
            if (node != null)
                transformer.transform(node);
            var a = node;
        } catch (ClassNotFoundException | IOException e) {

        }
    }

    @SuppressWarnings("all")
    public static <T extends Event> void addListener(Class<?> type, Consumer<T> eventConsumer) {
        register(type, eventConsumer);
        //MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, (Class<T>) FIND.get(type), (Consumer<T>) eventConsumer);
    }

}
