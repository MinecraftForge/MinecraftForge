package net.minecraftforge.permissions.api;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.minecraft.dispenser.ILocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.permissions.api.context.IContext;

import com.google.gson.JsonObject;

public interface PermBuilderFactory<T extends PermBuilder>
{
    /**
     * The name of this permissions provider (usually the modid)
     * @return name Name of permissions provider
     */
    String getName();
    
    /**
     * This method should return a fresh unadulterated PermBuilder instance with no default values.
     * @return a new instance of your PermBuilder.
     */
    T builder();

    /**
     * This method should return a PermBuilder instance with the player and PermNode set.
     * @return a new instance of your PermBuilder.
     */
    T builder(EntityPlayer player, String permNode);

    /**
     * @return The default IContext instance of this object for this Implementation.
     */
    IContext getDefaultContext(EntityPlayer player);

    /**
     * @return The default IContext instance of this object for this Implementation.
     */
    IContext getDefaultContext(TileEntity te);

    /**
     * @return The default IContext instance of this object for this Implementation.
     */
    IContext getDefaultContext(ILocation loc);

    /**
     * @return The default IContext instance of this object for this Implementation.
     */
    IContext getDefaultContext(Entity entity);

    /**
     * @return The default IContext instance of this object for this Implementation.
     */
    IContext getDefaultContext(World world);

    /**
     * @return A IContext signifying the Server as a whole.
     */
    IContext getGlobalContext();

    /**
     * At the very least, this method should return an anonymous instance of IContext.
     * This method should NEVER return null.
     * @return The default IContext instance of this object for this Implementation.
     */
    IContext getDefaultContext(Object whoKnows);

    /**
     * This is where permissions are registered with their default value.
     * @param perms
     */
    void registerPermissions(List<PermReg> perms);
    
    /**
     * Called on ServerStarted, during Forge's init
     * Do any implementation-specific handling here.
     */
    void initialize();
    
    /**
     * Get the groups a player is in
     * @param player
     * @return A list of groups the player is in
     */
    Collection<IGroup> getGroup(EntityPlayer player);
    
    /**
     * Get a group with a given name
     * @param name
     * @return A group if it exists, null if not found
     */
    IGroup getGroup(String name);
    
    /**
     * Get all groups known to the implementation
     * @return A list of all groups
     */
    Collection<IGroup> getAllGroups();

    public static class PermReg
    {
        public final String              key;
        public final RegisteredPermValue role;
        public final JsonObject          data;

        public PermReg(String key, RegisteredPermValue value, JsonObject obj)
        {
            this.key = key;
            this.role = value;
            this.data = obj;
        }
    }
}
