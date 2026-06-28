package io.github.mikip98.savethehotbar.config.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FloatRange {
    float min() default -Float.MAX_VALUE;
    float max() default Float.MAX_VALUE;
}
