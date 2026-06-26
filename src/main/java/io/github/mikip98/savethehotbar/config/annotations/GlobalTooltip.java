package io.github.mikip98.savethehotbar.config.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// TODO: REMOVE THIS IF THIS GETS MERGED INTO CLOTH-CONFIG DIRECTLY

/**
 * Applies a tooltip to every field that supports it, defined in your lang file.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GlobalTooltip {
    int count() default 1;
}
