/*
 * The FML Forge Mod Loader suite.
 * Copyright (C) 2012 cpw
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package cpw.mods.fml.common;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * A simple delegating class loader used to load mods into the system
 * 
 * 
 * @author cpw
 *
 */
public class ModClassLoader extends URLClassLoader
{

    public ModClassLoader()
    {
        super(new URL[0], ModClassLoader.class.getClassLoader());
    }

    public ModClassLoader(ClassLoader parent) {
        super(new URL[0], null);
    }
    public void addFile(File modFile) throws MalformedURLException
    {
        ClassLoader cl=getParent();
        if (cl instanceof URLClassLoader) {
            URLClassLoader ucl=(URLClassLoader) cl;
            URL url = modFile.toURI().toURL();
            try {
                Method addUrl=URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                addUrl.setAccessible(true);
                addUrl.invoke(ucl, url);
            } catch (Exception e) {
                Loader.log.severe("A fatal error occured attempting to load a file into the classloader");
                throw new LoaderException(e);
            }
        }
    }
    
    public File[] getParentSources() {
        ClassLoader cl=getParent();
        if (cl instanceof URLClassLoader) {
            URLClassLoader ucl=(URLClassLoader) cl;
            URL[] pUrl=ucl.getURLs();
            File[] sources=new File[pUrl.length];
            try
            {
                for (int i=0; i<pUrl.length; i++) {
                    sources[i]=new File(pUrl[i].toURI());
                }
                return sources;
            }
            catch (URISyntaxException e)
            {
                Loader.log.throwing("ModClassLoader", "getParentSources", e);
            }
        }
        Loader.log.severe("Unable to process our input to locate the minecraft code");
        throw new LoaderException();
    }
}
