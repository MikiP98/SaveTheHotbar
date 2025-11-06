package io.github.mikip98.savethehotbar.config.enums;

public enum LogicOperator {
    OR,
    AND;

    public boolean apply(boolean one, boolean two) {
        return switch (this) {
            case OR -> one || two;
            case AND -> one && two;
        };
    }
}
