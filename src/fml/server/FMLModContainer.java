package fml.server;

import fml.Mod;

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

}
