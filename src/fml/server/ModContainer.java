package fml.server;

public interface ModContainer {
  boolean wantsPreInit();
  boolean wantsPostInit();
  void preInit();
  void init();
  void postInit();
}
