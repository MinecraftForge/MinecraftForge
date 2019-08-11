/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.client.extensions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelDynBucket;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.client.ClientHooks;
import net.minecraftforge.fml.loading.toposort.CyclePresentException;
import net.minecraftforge.fml.loading.toposort.TopologicalSort;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface IForgeAtlasTexture
{
    Logger LOGGER = LogManager.getLogger();

    default AtlasTexture getAtlasTexture()
    {
        return (AtlasTexture)this;
    }

    @FunctionalInterface
    interface SpriteProvider
    {
        TextureAtlasSprite create(IResourceManager manager) throws Exception;
    }

    default List<TextureAtlasSprite> resolveSpriteDependencies(Collection<TextureAtlasSprite> sprites)
    {
        Map<ResourceLocation, TextureAtlasSprite> idToSprite = Maps.newHashMap();
        MutableGraph<TextureAtlasSprite> graph = GraphBuilder.directed().allowsSelfLoops(false).build();
        sprites.forEach(node -> {
            graph.addNode(node);
            idToSprite.put(node.getName(), node);
        });
        sprites.forEach(s -> s.getDependencies().forEach(dep -> {
            final TextureAtlasSprite dependency = idToSprite.get(dep);
            if (dependency == null)
            {
                ClientHooks.trackBrokenTexture(dep, "Missing dependency requested from sprite " + s.getName());
            }
            else
            {
                graph.putEdge(dependency, s);
            }
        }));

        try
        {
            return TopologicalSort.topologicalSort(graph, null);
        }
        catch (CyclePresentException e)
        {
            Set<Set<TextureAtlasSprite>> cycles = e.getCycles();
            cycles.stream().flatMap(Set::stream).distinct().forEach(sprite -> {
                ClientHooks.trackBrokenTexture(sprite.getName(), "Texture is part of dependency cycle");
                idToSprite.remove(sprite.getName());
            });
            return ImmutableList.copyOf(idToSprite.values());
        }
    }

    default Collection<TextureAtlasSprite> collectSprites(IResourceManager resourceManager, Set<ResourceLocation> spritesToLoad, BiFunction<IResourceManager, Set<ResourceLocation>, Collection<TextureAtlasSprite>> vanillaResolver)
    {
        List<SpriteProvider> customSpriteProviders = Lists.newArrayList();
        if (getAtlasTexture() == Minecraft.getInstance().getTextureMap())
        {
            customSpriteProviders.add(manager -> new ModelLoader.White());
            ModelDynBucket.LoaderDynBucket.INSTANCE.register(getAtlasTexture(), resourceManager, customSpriteProviders);
        }
        ModLoader.get().postEvent(new TextureStitchEvent.Pre(getAtlasTexture(), resourceManager, spritesToLoad, customSpriteProviders));

        List<CompletableFuture<TextureAtlasSprite>> customSprites = customSpriteProviders.stream()
                .map(spriteProvider ->
                        CompletableFuture.supplyAsync(() -> {
                            try
                            {
                                return spriteProvider.create(resourceManager);
                            }
                            catch (Exception ex)
                            {
                                throw new CompletionException(ex);
                            }
                        }, Util.getServerExecutor())
                ).collect(Collectors.toList());

        Map<ResourceLocation, TextureAtlasSprite> resolvedSprites = customSprites.stream()
                .flatMap(future -> {
                    try
                    {
                        return Stream.of(future.get());
                    }
                    catch (final Exception e)
                    {
                        LOGGER.error("Failed to create custom sprite", e);
                        return Stream.empty();
                    }
                })
                .peek(sprite -> spritesToLoad.addAll(sprite.getDependencies()))
                .collect(Collectors.toMap(TextureAtlasSprite::getName, Function.identity()));

        spritesToLoad.removeAll(resolvedSprites.keySet());
        vanillaResolver.apply(resourceManager, spritesToLoad).forEach(sprite -> resolvedSprites.putIfAbsent(sprite.getName(), sprite));
        return ImmutableList.copyOf(resolvedSprites.values());
    }

    default void onTextureStitchedPost()
    {
        ModLoader.get().postEvent(new TextureStitchEvent.Post(getAtlasTexture()));
    }

    default List<TextureAtlasSprite> processTextures(IResourceManager resourceManager, Collection<TextureAtlasSprite> sprites)
    {
        Map<ResourceLocation, CompletableFuture<TextureAtlasSprite>> results = Maps.newConcurrentMap();

        AtlasTexture map = getAtlasTexture();
        for (TextureAtlasSprite sprite : resolveSpriteDependencies(sprites))
        {
            if (sprite == getMissingTexture())
            {
                results.put(sprite.getName(), CompletableFuture.completedFuture(sprite));
            }
            else
            {
                results.put(sprite.getName(), CompletableFuture.supplyAsync(() -> {
                    try
                    {
                        ResourceLocation resourcelocation = map.getSpritePath(sprite.getName());
                        sprite.load(resourceManager, resourcelocation, map.getMipmapLevels() + 1, results::get);
                        map.generateMipmaps(sprite);
                        return sprite;
                    }
                    catch (Exception e)
                    {
                        throw new CompletionException(e);
                    }
                }, Util.getServerExecutor()));
            }
        }

        List<TextureAtlasSprite> result = Lists.newArrayList();
        results.forEach((location, spriteFuture) -> {
            try
            {
                result.add(spriteFuture.get());
            }
            catch (final Exception e)
            {
                ClientHooks.trackBrokenTexture(location, e.getMessage());
            }
        });
        return result;
    }

    TextureAtlasSprite getMissingTexture();
}
