package net.minecraftforge.entity;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class CapabilityMultiPartContainer {
    @CapabilityInject(IMultiPartEntity.class)
    public static Capability<IMultiPartEntity> MULTI_PART_CONTAINER = null;
    @CapabilityInject(IPartEntity.class)
    public static Capability<IPartEntity> MULTI_PART_PART = null;
    public static void register() {

    }
}
