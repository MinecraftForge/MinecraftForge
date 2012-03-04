package net.minecraft.src;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MLProp {
	String name() default "";
	String info() default "";
	double min() default Double.NEGATIVE_INFINITY;
	double max() default Double.POSITIVE_INFINITY;
    public int reobf() default 0;
}