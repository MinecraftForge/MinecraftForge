/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.server.permission.nodes;

import com.google.common.base.Preconditions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.permission.handler.IPermissionHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents the basic unit at the heart of the permission system.
 *
 * <p>A permission indicates the ability for an actor to perform an action,
 * in its most general sense. In the permission system, all permissions are encoded as instances of this class,
 * optionally integrated by a {@link PermissionDynamicContext}.</p>
 *
 * <p>A node is uniquely identified by its `nodeName`,
 * which is a dot-separated string providing meaning to the node itself.
 * The suggested structure of the name is `modid.name`, where `modid` is the ID of the owner of the node.
 * This API <strong>does not require</strong> any implicit hierarchy,
 * so `modid.name` is not considered a parent of `modid.name.sub`. Such decisions are left to the {@link IPermissionHandler}.</p>
 *
 * <p>Each node also has an associated {@link PermissionType}, detailing its exact type,
 * along with a {@link PermissionResolver} that is used to obtain the default value of the permission.
 * More information can be found on their documentation.</p>
 *
 * <p>A node can also be bound to additional {@link PermissionDynamicContextKey}s,
 * which are used when querying the availability of the permission.
 * There are no restrictions on their amount. It is also not mandatory to provide a value for all dynamics in a permission query.
 * See the above link for more information.</p>
 *
 * <p>Each node should be registered via the {@link PermissionGatherEvent.Nodes} and stored statically in a field.
 * That instance should then be reused every-time a permission check needs to be performed via {@link net.minecraftforge.server.permission.PermissionAPI#getPermission(ServerPlayer, PermissionNode, PermissionDynamicContext[])}.</p>
 */
public final class PermissionNode<T>
{
    private final String nodeName;
    private final PermissionType<T> type;
    private final PermissionResolver<T> defaultResolver;
    private final PermissionDynamicContextKey<?>[] dynamics;

    @Nullable
    private Component readableName;
    @Nullable
    private Component description;

    /**
     * Calls {@link PermissionNode#PermissionNode(String, PermissionType, PermissionResolver, PermissionDynamicContextKey[])}
     * with "namespace.path" as the first parameter
     */
    public PermissionNode(ResourceLocation nodeName, PermissionType<T> type, PermissionResolver<T> defaultResolver, PermissionDynamicContextKey... dynamics)
    {
        this(nodeName.getNamespace(), nodeName.getPath(), type, defaultResolver, dynamics);
    }

    /**
     * Calls {@link PermissionNode#PermissionNode(String, PermissionType, PermissionResolver, PermissionDynamicContextKey[])}
     * with "modid.nodename" as the first parameter
     */
    public PermissionNode(String modID, String nodeName, PermissionType<T> type, PermissionResolver<T> defaultResolver, PermissionDynamicContextKey... dynamics)
    {
        this(modID + "." + nodeName, type, defaultResolver, dynamics);
    }

    /**
     * @param nodeName        The identifier of a node, recommended identifier structure: "modid.path.for.node"
     * @param type            type object for the PermissionNode, only use types in {@link PermissionTypes}
     * @param defaultResolver Default resolver for the permission, can but doesn't have to be used to by PermissionHandlers
     * @param dynamics        PermissionDynamicContextKey is a dynamic component for permission nodes, similar to BlockState Properties.
     *                        They <strong>must</strong> be passed into the constructor if you want to use them.
     */
    private PermissionNode(String nodeName, PermissionType<T> type, PermissionResolver<T> defaultResolver, PermissionDynamicContextKey... dynamics)
    {
        this.nodeName = nodeName;
        this.type = type;
        this.dynamics = dynamics;
        this.defaultResolver = defaultResolver;
    }

    /**
     * Allows you to set a human-readable name and description for your Permission.
     *
     * <p>Note: Even though not used by Default, PermissionHandlers may display this information in game,
     * or provide it to the user by other means.<br>
     * You may use {@link net.minecraft.network.chat.Component#translatable(String) translatable components}, but you'll
     * need 2 language files. One inside the data directory for the server and one inside assets for the client.</p>
     *
     * @param readableName an easier to read name for the PermissionNode,
     *                     when using TranslatableComponent, key should be of format {@code "permission.name.<nodename>"}
     * @param description  description for the PermissionNode
     *                     when using TranslatableComponent, key should be of format {@code "permission.desc.<nodename>"}
     * @return itself with the new information set.
     */
    public PermissionNode setInformation(@NotNull Component readableName, @NotNull Component description)
    {
        Preconditions.checkNotNull(readableName, "Readable name for PermissionNodes must not be null %s", this.nodeName);
        Preconditions.checkNotNull(description, "Description for PermissionNodes must not be null %s", this.nodeName);

        this.readableName = readableName;
        this.description = description;

        return this;
    }

    public String getNodeName()
    {
        return nodeName;
    }

    public PermissionType<T> getType()
    {
        return type;
    }

    public PermissionDynamicContextKey<?>[] getDynamics()
    {
        return dynamics;
    }

    public PermissionResolver<T> getDefaultResolver()
    {
        return defaultResolver;
    }

    @Nullable
    public Component getReadableName()
    {
        return readableName;
    }

    @Nullable
    public Component getDescription()
    {
        return description;
    }

    /**
     * Utility Interface used for resolving the default value of PermissionNodes
     *
     * @param <T> generic value of the PermissionType of a PermissionNode
     */
    @FunctionalInterface
    public interface PermissionResolver<T>
    {
        /**
         * @param player     an online player
         * @param playerUUID if the player is null, this UUID belongs to an offline player,
         *                   otherwise it must match the UUID of the passed in player.
         * @param context    may contain DynamicContext if it was provided
         * @return according Permission Value
         */
        T resolve(@Nullable ServerPlayer player, UUID playerUUID, PermissionDynamicContext<?>... context);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof PermissionNode otherNode)) return false;
        return nodeName.equals(otherNode.nodeName) && type.equals(otherNode.type);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(nodeName, type);
    }
}
