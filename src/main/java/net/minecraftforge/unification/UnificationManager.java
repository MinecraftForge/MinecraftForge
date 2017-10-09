/*
 * Minecraft Forge
 * Copyright (c) 2016.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.minecraftforge.unification;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.base.Preconditions;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

/**
 * The unification manager is the main access point for getting instances of:
 * {@link UnifiedMaterial}, {@link UnifiedForm}, and {@link Unifier}.
 */
public final class UnificationManager
{
    private static final Map<String, UnifiedMaterial> MATERIALS = new HashMap<>();
    private static final Map<String, UnifiedForm> FORMS = new HashMap<>();
    @Nullable
    private static Unifier<ItemStack> ITEM_STACK_UNIFICATION_REGISTRY;
    @Nullable
    private static Unifier<IBlockState> BLOCK_STATE_UNIFICATION_REGISTRY;

    public static Unifier<ItemStack> getItemStackUnifier()
    {
        if (ITEM_STACK_UNIFICATION_REGISTRY == null)
        {
            ITEM_STACK_UNIFICATION_REGISTRY = createItemStackUnificationRegistry();
        }
        return ITEM_STACK_UNIFICATION_REGISTRY;
    }

    public static Unifier<IBlockState> getBlockStateUnifier()
    {
        if (BLOCK_STATE_UNIFICATION_REGISTRY == null)
        {
            BLOCK_STATE_UNIFICATION_REGISTRY = createBlockStateUnificationRegistry();
        }
        return BLOCK_STATE_UNIFICATION_REGISTRY;
    }

    /**
     * Create a new {@link Unifier}.
     * See {@link UnificationManager#createItemStackUnificationRegistry()} and {@link UnificationManager#createBlockStateUnificationRegistry()} for examples.
     *
     * @param validator          checks that anything added to the {@link Unifier} is valid. Throws an exception if it is not valid.
     * @param namer              gets the resource location from variants, so the {@link Unified} can sort them.
     * @param copier             copies values so they can't be mutated. If T is immutable, just use {@link Function#identity()}.
     * @param enforceLoaderState if true, an exception will be thrown when {@link Unifier#add} is called after preInit,
     *                           or when {@link Unifier#get} is called before init.
     */
    public static <T> Unifier<T> createUnifier(Consumer<T> validator, Function<T, ResourceLocation> namer, Function<T, T> copier, boolean enforceLoaderState)
    {
        return new Unifier<>(validator, namer, copier, enforceLoaderState);
    }

    /**
     * @return all the {@link UnifiedMaterial} that have been added.
     */
    public static Collection<UnifiedMaterial> getMaterials()
    {
        return Collections.unmodifiableCollection(MATERIALS.values());
    }

    /**
     * @return all the {@link UnifiedForm} that have been added.
     */
    public static Collection<UnifiedForm> getForms()
    {
        return Collections.unmodifiableCollection(FORMS.values());
    }

    /**
     * Get an {@link UnifiedMaterial} from a unique Id. Creates one if it does not exist.
     * Many are already defined in {@link UnificationConstants}.
     */
    public static UnifiedMaterial getMaterial(String uid)
    {
        Preconditions.checkNotNull(uid, "uid must not be null.");
        return MATERIALS.computeIfAbsent(uid, UnifiedMaterial::new);
    }

    /**
     * Get an {@link UnifiedForm} from a unique Id. Creates one if it does not exist.
     * Many are already defined in {@link UnificationConstants}.
     */
    public static UnifiedForm getForm(String uid)
    {
        Preconditions.checkNotNull(uid, "uid must not be null.");
        return FORMS.computeIfAbsent(uid, UnifiedForm::new);
    }

    private static Unifier<ItemStack> createItemStackUnificationRegistry()
    {
        Consumer<ItemStack> validator = itemStack -> {
            Preconditions.checkNotNull(itemStack, "itemStack must not be null");
            Preconditions.checkArgument(!itemStack.isEmpty(), "itemStack must not be empty");
            Preconditions.checkArgument(itemStack.getMetadata() != OreDictionary.WILDCARD_VALUE, "itemStack must be an actual ItemStack, not a wildcard.");
            Preconditions.checkArgument(itemStack.getCount() == 1, "itemStack count must be 1");
        };
        Function<ItemStack, ResourceLocation> namer = itemStack -> itemStack.getItem().getRegistryName();
        return createUnifier(validator, namer, ItemStack::copy, true);
    }

    private static Unifier<IBlockState> createBlockStateUnificationRegistry()
    {
        Consumer<IBlockState> validator = blockState -> {
            Preconditions.checkNotNull(blockState, "blockState must not be null");
            Preconditions.checkNotNull(blockState.getBlock(), "blockState's block must not be null");
        };
        Function<IBlockState, ResourceLocation> namer = blockState -> blockState.getBlock().getRegistryName();
        return createUnifier(validator, namer, Function.identity(), true);
    }

    private UnificationManager()
    {
    }
}
