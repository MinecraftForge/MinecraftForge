package net.minecraftforge.common.capabilities;

import java.lang.annotation.*;

/**
 * When placed on a FIELD, the field will be set to an<br>
 * instance of Capability once that capability is registered.<br>
 * That field must be static and be able to hold a instance<br>
 * of 'Capability'<br>
 * <br>
 * Example:<br>
 * {@code
 * @literal @CapabilityInject(IExampleCapability.class)
 * }<br>
 * {@code
 *  private static final Capability<IExampleCapability> TEST_CAP = null;
 * }<br>
 * When placed on a METHOD, the method will be invoked once the<br>
 * capability is registered. This allows you to have a 'enable features'<br>
 * callback. It MUST have one parameter of type 'Capability;<br>
 * <br>
 * Example:<br>
 * {@code
 * @literal @CapabilityInject(IExampleCapability.class)
 *  }<br>
 *  {@code
 *  private static void capRegistered(Capability<IExampleCapability> cap) {}
 *  }<br>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface CapabilityInject
{
    /**
     * The capability interface to listen for registration.<br>
     * <b>
     * Note:
     * When reading annotations, DO NOT call this function as it will cause a hard dependency on the class.
     * </b>
     */
    Class<?> value();
}
