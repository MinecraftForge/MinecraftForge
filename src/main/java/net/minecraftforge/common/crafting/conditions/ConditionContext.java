/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.crafting.conditions;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagManager;

import java.util.IdentityHashMap;
import java.util.Map;

public class ConditionContext implements ICondition.IContext {
	private final TagManager tagManager;
	private Map<ResourceKey<?>, TagManager.LoadResult<?>> loadedTags = null;

	public ConditionContext(TagManager tagManager) {
		this.tagManager = tagManager;
	}

	@Override
	public <T> Tag<Holder<T>> getTag(TagKey<T> key) {
		if (loadedTags == null)
		{
			var tags = tagManager.getResult();
			if (tags.isEmpty()) throw new IllegalStateException("Tags have not been loaded yet.");

			loadedTags = new IdentityHashMap<>();
			for (var loadResult : tags)
			{
				loadedTags.put(loadResult.key(), loadResult);
			}
		}
		return (Tag) loadedTags.get(key.registry()).tags().get(key.location());
	}
}
