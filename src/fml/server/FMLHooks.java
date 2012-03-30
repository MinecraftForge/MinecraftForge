package fml.server;

public enum FMLHooks {
  INSTANCE;
  public void worldTickStart(float tickTime) {
    for (ModContainer mod : Loader.getModList()) {
      mod.tickStart(tickTime);
    }
  }
  
  public void worldTickEnd(float tickTime) {
    for (ModContainer mod : Loader.getModList()) {
      mod.tickEnd(tickTime);
    }
  }
}
