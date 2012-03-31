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
package fml;


public class FMLModContainer implements ModContainer {
  private Mod modDescriptor;
  private Object modInstance;
  public FMLModContainer(Class<?> clazz) {
    modDescriptor=clazz.getAnnotation(Mod.class);
    
    try {
      modInstance=clazz.newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean wantsPreInit() {
    return modDescriptor.wantsPreInit();
  }

  @Override
  public boolean wantsPostInit() {
    return modDescriptor.wantsPostInit();
  }

  @Override
  public void preInit() {
    
  }

  @Override
  public void init() {
    // TODO Auto-generated method stub

  }

  @Override
  public void postInit() {
    // TODO Auto-generated method stub

  }

  public static ModContainer buildFor(Class<?> clazz) {
    return new FMLModContainer(clazz);
  }

  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void tickStart() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void tickEnd() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public boolean matches(Object mod) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public String getSource() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object getMod() {
    // TODO Auto-generated method stub
    return null;
  }

}
