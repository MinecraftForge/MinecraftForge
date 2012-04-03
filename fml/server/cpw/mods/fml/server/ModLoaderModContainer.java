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
package cpw.mods.fml.server;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import net.minecraft.src.BaseMod;
import cpw.mods.fml.common.ICraftingHandler;
import cpw.mods.fml.common.IDispenseHandler;
import cpw.mods.fml.common.IPickupNotifier;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderException;
import cpw.mods.fml.common.ModContainer;

public class ModLoaderModContainer implements ModContainer {
  private Class<? extends BaseMod> modClazz;
  private BaseMod mod;
  private boolean isTicking;
  private String modSource ;
  private ArrayList<String> dependencies;
  private ArrayList<String> preDependencies;
  private ArrayList<String> postDependencies;
  public ModLoaderModContainer(Class<? extends BaseMod> modClazz, String modSource) {
    this.modClazz=modClazz;
    this.modSource =modSource;
  }

  public boolean wantsPreInit() {
    return true;
  }

  public boolean wantsPostInit() {
    return true;
  }

  @Override
  public void preInit() {
    try {
      mod=modClazz.newInstance();
    } catch (Exception e) {
      throw new LoaderException(e);
    }
  }

  @Override
  public void init() {
    mod.load();
  }

  @Override
  public void postInit() {
    mod.modsLoaded();
  }

  @Override
  public void tickStart() {
    if (isTicking) {
      isTicking=mod.onTickInGame(FMLHandler.instance().getServer());
    }
  }
  @Override
  public void tickEnd() {
    // NOOP for modloader
  }

  @Override
  public String getName() {
    return mod!=null?mod.getName():null;
  }

  public static ModContainer findContainerFor(BaseMod mod) {
    for (ModContainer mc :Loader.getModList()) {
      if (mc.matches(mod)) {
        return mc; 
      }
    }
    return null;
  }

  @Override
  public boolean matches(Object mod) {
    return modClazz.isInstance(mod);
  }

  public void setTicking(boolean enable) {
    isTicking=enable;
  }

  public static List<BaseMod> findAll() {
    ArrayList<BaseMod> modList=new ArrayList<BaseMod>();
    for (ModContainer mc : Loader.getModList()) {
      if (mc instanceof ModLoaderModContainer) {
        modList.add(((ModLoaderModContainer)mc).mod);
      }
    }
    return modList;
  }

  @Override
  public String getSource() {
    return modSource;
  }

  @Override
  public Object getMod() {
    return mod;
  }

  @Override
  public boolean generatesWorld() {
    return true;
  }

  @Override
  public IWorldGenerator getWorldGenerator() {
    return mod;
  }

  @Override
  public int lookupFuelValue(int itemId, int itemDamage) {
    return mod.addFuel(itemId, itemDamage);
  }

  @Override
  public boolean wantsPickupNotification() {
    return true;
  }

  @Override
  public IPickupNotifier getPickupNotifier() {
    return mod;
  }

  /* (non-Javadoc)
   * @see cpw.mods.fml.common.ModContainer#wantsToDispense()
   */
  @Override
  public boolean wantsToDispense() {
    return true;
  }

  /* (non-Javadoc)
   * @see cpw.mods.fml.common.ModContainer#getDispenseHandler()
   */
  @Override
  public IDispenseHandler getDispenseHandler() {
    return mod;
  }

  /* (non-Javadoc)
   * @see cpw.mods.fml.common.ModContainer#wantsCraftingNotification()
   */
  @Override
  public boolean wantsCraftingNotification() {
    return true;
  }

  /* (non-Javadoc)
   * @see cpw.mods.fml.common.ModContainer#getCraftingHandler()
   */
  @Override
  public ICraftingHandler getCraftingHandler() {
    return mod;
  }
  
  private void computeDependencies() {
    dependencies = new ArrayList<String>();
    preDependencies = new ArrayList<String>();
    postDependencies = new ArrayList<String>();
    if (mod.getPriorities()==null || mod.getPriorities().length()==0) {
      return;
    }
    StringTokenizer st=new StringTokenizer(mod.getPriorities(),";");
    for (; st.hasMoreTokens(); ) {
      String dep=st.nextToken();
      String[] depparts=dep.split(":");
      if (depparts.length<2) {
        throw new LoaderException();
      }
      if ("required-before".equals(depparts[0]) || "required-after".equals(depparts[0])) {
        dependencies.add(depparts[1]);
      }
      
      if ("required-before".equals(depparts[0]) || "before".equals(depparts[0])) {
        preDependencies.add(depparts[1]);
      }
      
      if ("required-after".equals(depparts[0]) || "after".equals(depparts[0])) {
        postDependencies.add(depparts[1]);
      }
    }
  }
  /* (non-Javadoc)
   * @see cpw.mods.fml.common.ModContainer#getDependencies()
   */
  @Override
  public List<String> getDependencies() {
    if (dependencies==null) {
      computeDependencies();
    }
    return dependencies;
  }

  @Override
  public List<String> getPreDepends() {
    if (dependencies==null) {
      computeDependencies();
    }
    return preDependencies;
  }

  @Override
  public List<String> getPostDepends() {
    if (dependencies==null) {
      computeDependencies();
    }
    return postDependencies;
  }
  
  public String toString() {
    return modSource;
  }
}
