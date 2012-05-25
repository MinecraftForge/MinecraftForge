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

import java.util.Arrays;

/**
 * @author cpw
 *
 */
public class FMLModLoaderContainer extends FMLModContainer
{

    /**
     * @param dummy
     */
    public FMLModLoaderContainer()
    {
        super("Forge Mod Loader");
    }

    /* (non-Javadoc)
     * @see cpw.mods.fml.common.FMLModContainer#getMetadata()
     */
    @Override
    public ModMetadata getMetadata()
    {
        if (super.getMetadata()==null) {
            ModMetadata md=new ModMetadata(this);
            setMetadata(md);
            md.name="Forge Mod Loader";
            md.version=Loader.instance().getFMLVersionString();
            md.credits="Made possible with help from many people";
            md.authorList=Arrays.asList("cpw, LexManos");
            md.description="The Forge Mod Loader provides the ability for systems to load mods " +
            		"from the file system. It also provides key capabilities for mods to be able " +
            		"to cooperate and provide a good modding environment. " +
            		"The mod loading system is compatible with ModLoader, all your ModLoader " +
            		"mods should work.";
            md.url="https://github.com/cpw/FML/wiki";
            md.updateUrl="https://github.com/cpw/FML/wiki";
            md.screenshots=new String[0];
            md.logoFile="";
        }
        return super.getMetadata();
    }
    
    /* (non-Javadoc)
     * @see cpw.mods.fml.common.FMLModContainer#getName()
     */
    @Override
    public String getName()
    {
        return "Forge Mod Loader";
    }
    
    /* (non-Javadoc)
     * @see cpw.mods.fml.common.FMLModContainer#getVersion()
     */
    @Override
    public String getVersion()
    {
        return Loader.instance().getFMLVersionString();
    }
}
