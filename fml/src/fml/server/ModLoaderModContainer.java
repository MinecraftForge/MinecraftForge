package fml.server;

import net.minecraft.src.BaseMod;

public class ModLoaderModContainer implements ModContainer {
  private Class<? extends BaseMod> modClazz;
  private BaseMod mod;
  private boolean isTicking;
  public ModLoaderModContainer(Class<? extends BaseMod> modClazz) {
    this.modClazz=modClazz;
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
  public void tickStart(float tickTime) {
    if (isTicking) {
      isTicking=mod.onTickInGame(tickTime);
    }
  }
  @Override
  public void tickEnd(float tickTime) {
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
}
