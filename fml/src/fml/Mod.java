package fml;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Mod {
  String name() default "";
  String version() default "";
  boolean wantsPreInit() default false;
  boolean wantsPostInit() default false;
  public @interface PreInit {}
  public @interface Init {}
  public @interface PostInit {}
}
