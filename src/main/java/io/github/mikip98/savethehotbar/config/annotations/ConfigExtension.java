package io.github.mikip98.savethehotbar.config.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class ConfigExtension {
    private ConfigExtension() {}

    public static class Gui {
        private Gui() {}

        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface FloatRange {
            float min() default -Float.MAX_VALUE;
            float max() default Float.MAX_VALUE;
        }

        /**
         * Applies a tooltip to every field that supports it, defined in your lang file.
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.TYPE)
        public @interface GlobalTooltip {
            int count() default 1;
        }

        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface IntRange {
            int min() default Integer.MIN_VALUE;
            int max() default Integer.MAX_VALUE;
        }

        /**
         * Prevents {@link GlobalTooltip} from working on the given field.
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface SkipGlobalTooltip {}
    }
}
