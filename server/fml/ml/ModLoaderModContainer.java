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
package fml.ml;

import java.util.ArrayList;
import java.util.List;

import fml.Loader;
import fml.LoaderException;
import fml.ModContainer;
import fml.obf.FMLHandler;
import net.minecraft.src.BaseMod;

public class ModLoaderModContainer implements ModContainer {
  private Class<? extends BaseMod> modClazz;
  private BaseMod mod;
  private boolean isTicking;
  private String modSource ;
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
      isTicking=mod.onTickInGame(FMLHandler.INSTANCE.getServer());
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
}
