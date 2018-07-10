/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.fml.common;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.common.asm.transformers.ModAPITransformer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;

import org.apache.logging.log4j.Level;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * A simple delegating class loader used to load mods into the system
 *
 *
 * @author cpw
 *
 */
public class ModClassLoader extends URLClassLoader
{
    private static final List<String> STANDARD_LIBRARIES = ImmutableList.of("jinput.jar", "lwjgl.jar", "lwjgl_util.jar", "rt.jar");
    private LaunchClassLoader mainClassLoader;
    private List<File> sources;

    public ModClassLoader(ClassLoader parent) {
        super(new URL[0], null);
        if (parent instanceof LaunchClassLoader)
        {
            this.mainClassLoader = (LaunchClassLoader)parent;
        }
        this.sources = Lists.newArrayList();
    }

    public void addFile(File modFile) throws MalformedURLException
    {
        URL url = modFile.toURI().toURL();
        mainClassLoader.addURL(url);
        this.sources.add(modFile);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException
    {
        return mainClassLoader.loadClass(name);
    }

    public File[] getParentSources() {
        try
        {
            List<File> files=new ArrayList<File>();
            for(URL url : mainClassLoader.getSources())
            {
                URI uri = url.toURI();
                if(uri.getScheme().equals("file"))
                {
                    files.add(new File(uri));
                }
            }
            return files.toArray(new File[]{});
        }
        catch (URISyntaxException e)
        {
            FMLLog.log.error("Unable to process our input to locate the minecraft code", e);
            throw new LoaderException(e);
        }
    }

    public List<String> getDefaultLibraries()
    {
        return STANDARD_LIBRARIES;
    }

    public boolean isDefaultLibrary(File file)
    {
        String home = System.getProperty("java.home"); // Nullcheck just in case some JVM decides to be stupid
        if (home != null && file.getAbsolutePath().startsWith(home)) return true;
        // Should really pull this from the json somehow, but we dont have that at runtime.
        String name = file.getName();
        if (!name.endsWith(".jar")) return false;
        String[] prefixes =
        {
            "launchwrapper-",
            "asm-all-",
            "akka-actor_2.11-",
            "config-",
            "scala-",
            "jopt-simple-",
            "lzma-",
            "realms-",
            "httpclient-",
            "httpcore-",
            "vecmath-",
            "trove4j-",
            "icu4j-core-mojang-",
            "codecjorbis-",
            "codecwav-",
            "libraryjavawound-",
            "librarylwjglopenal-",
            "soundsystem-",
            "netty-all-",
            "guava-",
            "commons-lang3-",
            "commons-compress-",
            "commons-logging-",
            "commons-io-",
            "commons-codec-",
            "jinput-",
            "jutils-",
            "gson-",
            "authlib-",
            "log4j-api-",
            "log4j-core-",
            "lwjgl-",
            "lwjgl_util-",
            "twitch-",
            "jline-",
            "jna-",
            "platform-",
            "oshi-core-",
            "netty-",
            "libraryjavasound-",
            "fastutil-"
        };
        for (String s : prefixes)
        {
            if (name.startsWith(s)) return true;
        }
        return false;
    }

    public void clearNegativeCacheFor(Set<String> classList)
    {
        mainClassLoader.clearNegativeEntries(classList);
    }

    public ModAPITransformer addModAPITransformer(ASMDataTable dataTable)
    {
        mainClassLoader.registerTransformer("net.minecraftforge.fml.common.asm.transformers.ModAPITransformer");
        List<IClassTransformer> transformers = mainClassLoader.getTransformers();
        ModAPITransformer modAPI = (ModAPITransformer) transformers.get(transformers.size()-1);
        modAPI.initTable(dataTable);
        return modAPI;
    }

    List<URL> parentURLs = null;
    public boolean containsSource(File source)
    {
        if (parentURLs == null) {
            parentURLs = Arrays.asList(mainClassLoader.getURLs());
        }
        try
        {
            return parentURLs.contains(source.toURI().toURL());
        } catch (MalformedURLException e)
        {
            // shouldn't happen
            return false;
        }
    }
}
