package fml.server;

public interface ModContainer {
  boolean wantsPreInit();
  boolean wantsPostInit();
  void preInit();
  void init();
  void postInit();
  String getName();
  void tickStart(float tickTime);
  void tickEnd(float tickTime);
  boolean matches(Object mod);
}
