package io.github.mikip98.savethehotbar.config.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// TODO: REMOVE THIS IF THIS GETS MERGED INTO CLOTH-CONFIG DIRECTLY

/**
 * Prevents {@link GlobalTooltip} from working on the given field.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SkipGlobalTooltip {
}
