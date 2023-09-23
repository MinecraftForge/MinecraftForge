/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.capabilities;

import org.objectweb.asm.tree.MethodNode;

import static org.objectweb.asm.Type.getMethodDescriptor;
import static org.objectweb.asm.Type.getType;

public class Names {
    public static final String EVENT_PROVIDER = "Lnet/minecraftforge/common/capabilities/ICapabilityEventProvider";
    public static final String ATTACH_EVENT_TYPE = "(Ljava/lang/Object;)Lnet/minecraftforge/event/AttachCapabilitiesEvent;";
    public static final Method EVENT_PROVIDER_CREATE = new Method("createAttachCapabilitiesEvent", getMethodDescriptor(getType(ATTACH_EVENT_TYPE)));

    static record Method(String name, String desc) {
        boolean equals(MethodNode node) {
            return this.name.equals(node.name) && this.desc.equals(node.desc);
        }
    }
}
