package net.minecraftforge.fml;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.minecraftforge.api.distmarker.Dist;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface ImplementOn {
    Dist dist();
    String method();
}
