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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import cpw.mods.fml.common.modloader.ModLoaderModContainer;
/**
 * @author cpw
 *
 */
public class ModMetadata
{
    public enum ModType { MODLOADER, FML };
    public ModContainer mod;
    public ModType type;
    
    public String name;
    public String description;
    
    public String url="";
    public String updateUrl="";
    
    public String logoFile="";
    public String version="";
    public List<String> authorList=new ArrayList<String>(1);
    public String credits="";
    public String parent="";
    public String[] screenshots;
    
    public ModContainer parentMod;
    public List<ModContainer> childMods = new ArrayList<ModContainer>(1);

    /**
     * @param mod2
     * @param type2
     */
    public ModMetadata(ModContainer mod)
    {
        this.mod=mod;
        this.type=(mod instanceof FMLModContainer ? ModType.FML : ModType.MODLOADER);
    }
    
    public void associate(Map<String, ModContainer> mods) {
        if (parent!=null && parent.length() > 0) {
            ModContainer mc=mods.get(parent);
            if (mc!=null && mc.getMetadata()!=null) {
                mc.getMetadata().childMods.add(mod);
                parentMod = mc;
            }
        }
    }

    /**
     * @return
     */
    public String getChildModCountString()
    {
        return String.format("%d child mod%s", childMods.size(), childMods.size()!=1 ? "s" : "");
    }

    public String getAuthorList() {
        StringBuilder sb=new StringBuilder();
        for (int i=0; i<authorList.size(); i++) {
            sb.append(authorList.get(i));
            if (i<authorList.size()-1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
    /**
     * @return
     */
    public String getChildModList()
    {
        StringBuilder sb=new StringBuilder();
        for (int i=0; i<childMods.size(); i++) {
            sb.append(childMods.get(i).getMetadata().name);
            if (i<childMods.size()-1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
