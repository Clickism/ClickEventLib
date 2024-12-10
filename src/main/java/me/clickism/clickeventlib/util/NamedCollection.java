package me.clickism.clickeventlib.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A collection of named objects.
 * Provides automatic O(1) access to objects by name, through a synced map from name to object.
 * The collection must be backed by a parent collection.
 *
 * @param <T> type of the named objects
 */
public class NamedCollection<T extends Named> extends AbstractCollection<T> {
    private final Collection<T> parent;
    private final Map<String, T> nameMap;

    /**
     * Creates a new named collection.
     *
     * @param parent parent collection
     */
    public NamedCollection(Collection<T> parent) {
        this.parent = parent;
        this.nameMap = new HashMap<>();
        parent.forEach(option -> nameMap.put(option.getName(), option));
    }

    /**
     * Sorts the named collection using the given comparator.
     *
     * @param comparator comparator
     */
    public void sort(Comparator<T> comparator) {
        List<T> list = new ArrayList<>(parent);
        list.sort(comparator);
        parent.clear();
        parent.addAll(list);
    }

    /**
     * Gets the object with the given name.
     *
     * @param name name of the object
     * @return object with the given name, or null if not found
     */
    @Nullable
    public T get(String name) {
        return nameMap.get(name);
    }

    @Override
    @NotNull
    public Iterator<T> iterator() {
        return new NamedIterator<>(parent, nameMap);
    }

    @Override
    public int size() {
        return parent.size();
    }

    @Override
    public boolean add(T t) {
        nameMap.put(t.getName(), t);
        return parent.add(t);
    }

    /**
     * Iterator for NamedCollection that overrides Collection#remove.
     *
     * @param <T>
     */
    private static class NamedIterator<T extends Named> implements Iterator<T> {
        private final Map<String, T> selectionsMap;
        private final Iterator<T> iterator;
        private T current;

        private NamedIterator(Collection<T> selections, Map<String, T> selectionsMap) {
            this.iterator = selections.iterator();
            this.selectionsMap = selectionsMap;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public T next() {
            current = iterator.next();
            return current;
        }

        @Override
        public void remove() {
            iterator.remove();
            selectionsMap.remove(current.getName());
        }
    }
}
