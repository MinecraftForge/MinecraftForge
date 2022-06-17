/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.common.asm;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;

/**
 * Removes the final modifier from fields with the @ObjectHolder annotation, prevents the JITer from in lining them so our runtime replacements can work.
 * Will also de-finalize all fields in on class level annotations.
 */
public class ObjectHolderDefinalize implements ILaunchPluginService {
    // Hardcoded map of vanilla classes that should have object holders for each field of the given registry type.
    // IMPORTANT: Updates to this collection must be reflected in ObjectHolderRegistry. Duplicated cuz classloaders, yay!
    // Classnames are validated in ObjectHolderRegistry.
    private static final Map<String, VanillaObjectHolderData> VANILLA_OBJECT_HOLDERS = Stream.of(
            new VanillaObjectHolderData("net.minecraft.world.level.block.Blocks", "block", "net.minecraft.world.level.block.Block"),
            new VanillaObjectHolderData("net.minecraft.world.item.Items", "item", "net.minecraft.world.item.Item"),
            new VanillaObjectHolderData("net.minecraft.world.item.enchantment.Enchantments", "enchantment", "net.minecraft.world.item.enchantment.Enchantment"),
            new VanillaObjectHolderData("net.minecraft.world.effect.MobEffects", "mob_effect", "net.minecraft.world.effect.MobEffect"),
            new VanillaObjectHolderData("net.minecraft.core.particles.ParticleTypes", "particle_type", "net.minecraft.core.particles.ParticleType"),
            new VanillaObjectHolderData("net.minecraft.sounds.SoundEvents", "sound_event", "net.minecraft.sounds.SoundEvent")
    ).collect(Collectors.toMap(VanillaObjectHolderData::holderClass, Function.identity()));
    private final String OBJECT_HOLDER = "Lnet/minecraftforge/registries/ObjectHolder;"; //Don't directly reference this to prevent class loading.

    @Override
    public String name() {
        return "object_holder_definalize";
    }

    private static final EnumSet<Phase> YAY = EnumSet.of(Phase.AFTER);
    private static final EnumSet<Phase> NAY = EnumSet.noneOf(Phase.class);

    @Override
    public EnumSet<Phase> handlesClass(Type classType, boolean isEmpty)
    {
        return isEmpty ? NAY : YAY;
    }

    private boolean hasHolder(List<AnnotationNode> lst)
    {
        return lst != null && lst.stream().anyMatch(n -> n.desc.equals(OBJECT_HOLDER));
    }

    private String getValue(List<AnnotationNode> lst)
    {
        AnnotationNode ann = lst.stream().filter(n -> n.desc.equals(OBJECT_HOLDER)).findFirst().get();
        if (ann.values != null)
        {
            for (int x = 0; x < ann.values.size() - 1; x += 2) {
                if (ann.values.get(x).equals("value")) {
                    return (String)ann.values.get(x + 1);
                }
            }
        }
        return null;
    }

    @Override
    public int processClassWithFlags(final Phase phase, final ClassNode classNode, final Type classType, final String reason)
    {
        final AtomicBoolean changes = new AtomicBoolean();
        //Must be public static finals, and non-array objects
        final int flags = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL;

        //Fix Annotated Fields before injecting from class level
        classNode.fields.stream().filter(f -> ((f.access & flags) == flags) && f.desc.startsWith("L") && hasHolder(f.visibleAnnotations)).forEach(f ->
        {
            int prev = f.access;
            f.access &= ~Opcodes.ACC_FINAL; //Strip final
            f.access |= Opcodes.ACC_SYNTHETIC; //Add Synthetic so we can check in runtime. ? Good idea?
            changes.compareAndSet(false, prev != f.access);
        });

        if (VANILLA_OBJECT_HOLDERS.containsKey(classType.getClassName())) //Class level, de-finalize all fields and add @ObjectHolder to them!
        {
            classNode.fields.stream().filter(f -> ((f.access & flags) == flags) && f.desc.startsWith("L")).forEach(f ->
            {
                int prev = f.access;
                f.access &= ~Opcodes.ACC_FINAL;
                f.access |= Opcodes.ACC_SYNTHETIC;
                /*if (!hasHolder(f.visibleAnnotations)) //Add field level annotation, doesn't do anything until after we figure out how ASMDataTable is gatherered
                {
                   if (value == null)
                       f.visitAnnotation(OBJECT_HOLDER, true);
                   else
                       f.visitAnnotation(OBJECT_HOLDER, true).visit("value", value + ":" + f.name.toLowerCase());
                }*/
                changes.compareAndSet(false, prev != f.access);
            });
        }
        return changes.get() ? ComputeFlags.SIMPLE_REWRITE : ComputeFlags.NO_REWRITE;
    }

    private record VanillaObjectHolderData(String holderClass, String registryName, String registryType) {}
}
