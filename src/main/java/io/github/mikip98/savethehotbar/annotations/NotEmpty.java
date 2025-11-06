package io.github.mikip98.savethehotbar.annotations;

import java.lang.annotation.*;

/**
 * Indicates that a value (e.g., String, Collection, Map, or array)
 * should not be empty, but may be null unless also annotated with @NotNull.
 *
 * Use together with @NotNull to require both non-null and non-empty values.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
public @interface NotEmpty {
}