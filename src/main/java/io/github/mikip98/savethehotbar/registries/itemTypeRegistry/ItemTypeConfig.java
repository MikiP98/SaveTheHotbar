package io.github.mikip98.savethehotbar.registries.itemTypeRegistry;

import net.minecraft.world.item.Item;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

// TODO: Consider making this into a registry, by freezing the sets after runtime and converting to primitive arrays
public class ItemTypeConfig {
    protected @NotNull TagKey<Item> tagOverride;
    protected Set<TagKey<Item>> tags = new LinkedHashSet<>();
    protected Set<Class<?>> classes = new LinkedHashSet<>();
    protected Set<Predicate<Item>> predicates = new LinkedHashSet<>();

    public ItemTypeConfig(@NotNull TagKey<Item> tagOverride) {
        this.tagOverride = tagOverride;
    }

    public @NotNull ItemTypeConfig addClasses(@NotNull Class<?>... classes) {
        this.classes.addAll(List.of(classes));
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    @SafeVarargs
    public final @NotNull ItemTypeConfig addTags(@NotNull TagKey<Item>... tags) {
        this.tags.addAll(List.of(tags));
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    @SafeVarargs
    public final @NotNull ItemTypeConfig addPredicates(@NotNull Predicate<Item>... predicates) {
        this.predicates.addAll(List.of(predicates));
        return this;
    }

    public boolean isItemStackOfType(ItemStack itemStack) {
        final Item item = itemStack.getItem();
        return itemStack.is(tagOverride)
                || tags.stream().anyMatch(itemStack::is)
                || classes.stream().anyMatch((clazz) -> clazz.isInstance(item))
                || predicates.stream().anyMatch((predicate) -> predicate.test(item));
    }

    public boolean isConfigured() {
        return !tags.isEmpty() || !classes.isEmpty() || !predicates.isEmpty();
    }
}
