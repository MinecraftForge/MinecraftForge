package net.minecraftforge.common.capabilities;

import java.lang.annotation.*;

/**
 * When placed on a FIELD, the field will be set to an
 * instance of Capability once that capability is registered.
 * That field must be static and be able to hold a instance
 * of 'Capability'
 *
 * Example:
 *  @CapabilityInject(IExampleCapability.class)
 *  private static final Capability<IExampleCapability> TEST_CAP = null;
 *
 * When placed on a METHOD, the method will be invoked once the
 * capability is registered. This allows you to have a 'enable features'
 * callback. It MUST have one parameter of type 'Capability;
 *
 * Example:
 *  @CapabilityInject(IExampleCapability.class)
 *  private static void capRegistered(Capability<IExampleCapability> cap) {}
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface CapabilityInject
{
    /**
     * The capability interface to listen for registration.
     * Note:
     *   When reading annotations, DO NOT call this function as it will cause a hard dependency on the class.
     */
    Class<?> value();
}
