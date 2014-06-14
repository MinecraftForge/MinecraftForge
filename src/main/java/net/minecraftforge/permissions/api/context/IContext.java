package net.minecraftforge.permissions.api.context;

/**
 * This is a marker context for all other Context classes.
 * <p>
 *     Any class implementing IContext should be immutable and not carry any potentially problematic references.
 *     Problematic references include but are not limited to instances of World, Entity, and TileEntity.
 *     It is best instead for contexts to supply information in the way of IDs or locations in order to obtain
 *     the desired instances. It is also a good idea to simply copy the information into primitive types and allow
 *     them to be accessible va getters.
 * </p>
 */
public interface IContext {}
