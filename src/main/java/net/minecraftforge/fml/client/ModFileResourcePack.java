/*
 * Minecraft Forge
 * Copyright (c) 2018.
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

package net.minecraftforge.fml.client;

import net.minecraft.resources.AbstractResourcePack;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ModFileResourcePack extends AbstractResourcePack
{
    private final ModFile modFile;

    public ModFileResourcePack(final ModFile modFile)
    {
        super(new File("dummy"));
        this.modFile = modFile;
    }

    public ModFile getModFile() {
        return this.modFile;
    }

    @Override
    public String func_195762_a()
    {
        return modFile.getFileName();
    }

    @Override
    protected InputStream func_195766_a(String name) throws IOException
    {
        return Files.newInputStream(modFile.getLocator().findPath(modFile, name));
    }

    @Override
    protected boolean func_195768_c(String name)
    {
        return Files.exists(modFile.getLocator().findPath(modFile, name));
    }

    @Override
    public Collection<ResourceLocation> func_195758_a(ResourcePackType p_195758_1_, String p_195758_2_, int p_195758_3_, Predicate<String> p_195758_4_)
    {

        try
        {
            return Files.walk(modFile.getLocator().findPath(modFile, p_195758_1_.func_198956_a())).
                    filter(path -> !path.toString().endsWith(".mcmeta")).
                    filter(path -> path.getNameCount() > 2 && p_195758_2_.equals(path.getName(2).toString())).
                    filter(path -> p_195758_4_.test(path.subpath(3, Math.min(p_195758_3_+3, path.getNameCount())).toString())).
                    map(path -> new ResourceLocation(path.getName(1).toString(),path.subpath(3,Math.min(p_195758_3_+3, path.getNameCount())).toString())).
                    collect(Collectors.toList());
        }
        catch (IOException e)
        {
            return Collections.emptyList();
        }
    }

    @Override
    public Set<String> func_195759_a(ResourcePackType p_195759_1_)
    {
        try {
            return Files.walk(modFile.getLocator().findPath(modFile, p_195759_1_.func_198956_a()),1).map(p->p.getFileName().toString()).collect(Collectors.toSet());
        }
        catch (IOException e)
        {
            return Collections.emptySet();
        }
    }

    @Override
    public void close() throws IOException
    {

    }
}
