package net.minecraftforge.common;

/**
 * To be implemented on vanilla enums that should be enhanced with ASM to be
 * extensible. If this is implemented on a class, the class must define a static
 * method called "create" which takes a String (enum name), and the rest of the
 * parameters matching a constructor.
 * <p>
 * For example, an enum with the constructor {@code MyEnum(Object foo)} would
 * require the method:
 * 
 * <pre>
 * public static MyEnum create(String name, Object foo)
 * {
 *     throw new IllegalStateException("Enum not extended");
 * }
 * </pre>
 * 
 * The method contents will be replaced with ASM at runtime. Multiple
 * {@code create} methods <strong>can</strong> be defined as long as each
 * matches a constructor.
 */
public interface IExtensibleEnum
{
}