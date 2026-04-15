package io.github.mikip98.savethehotbar.registries.itemTypeRegistry;

import io.github.mikip98.savethehotbar.annotations.NotEmpty;
import lombok.Getter;
import net.minecraft.world.item.Item;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Getter
public class ItemTypeConfig {
    protected Set<Class<?>> classes = new HashSet<>();
    protected Set<TagKey<Item>> tags = new HashSet<>();
    protected @NotNull TagKey<Item> tagOverride;
    protected @Nullable Function<Item, Boolean> predicate = null;

    public ItemTypeConfig(@NotNull TagKey<Item> tagOverride) {
        this.tagOverride = tagOverride;
    }

    public final ItemTypeConfig addClasses(@NotNull Class<?>... classes) {
        this.classes.addAll(List.of(classes));
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    @SafeVarargs
    public final ItemTypeConfig addTags(@NotNull @NotEmpty TagKey<Item>... tags) {
        this.tags.addAll(List.of(tags));
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull ItemTypeConfig addPredicate(@NotNull @NotEmpty Function<Item, Boolean> predicate) {
        this.predicate = predicate;
        return this;
    }
}
