package org.example.engine.core.collections;

import org.example.engine.core.math.MathUtils;

import java.util.Objects;

public class Array<T> {

    public T[] items;
    public int size;
    public boolean ordered;

    public Array() {
        this(16, true);
    }

    public Array (int capacity) {
        this(capacity, true);
    }

    public Array(int capacity, boolean ordered) {
        this.ordered = ordered;
        this.items = (T[]) new Object[capacity];
    }

    public void add(final T value) {
        T[] items = this.items;
        if (size == items.length) items = resize(size * 2);
        items[size] = value;
        size++;
    }

    public void add(final T v1, final T v2) {
        T[] items = this.items;
        if (size + 1 >= items.length) items = resize(size * 2);
        items[size] = v1;
        items[size + 1] = v2;
        size += 2;
    }

    public void add(final T v1, final T v2, final T v3) {
        T[] items = this.items;
        if (size + 3 >= items.length) items = resize(size * 2);
        items[size] = v1;
        items[size + 1] = v2;
        items[size + 1] = v3;
        size += 3;
    }

    public T get(int index) {
        if (index >= size) throw new IndexOutOfBoundsException("Tried to retrieve array element at index " + index + " >= " + size);
        return items[index];
    }

    public void set(int index, T value) {
        if (index >= size) throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + size);
        items[index] = value;
    }

    public boolean contains(final T value, boolean identity) {
        T[] items = this.items;
        int i = size - 1;
        if (identity || value == null) {
            while (i >= 0)
                if (items[i--] == value) return true;
        } else {
            while (i >= 0)
                if (value.equals(items[i--])) return true;
        }
        return false;
    }

    public boolean containsAll(Array<? extends T> values, boolean identity) {
        T[] items = values.items;
        for (int i = 0, n = values.size; i < n; i++)
            if (!contains(items[i], identity)) return false;
        return true;
    }

    public boolean containsAny(Array<? extends T> values, boolean identity) {
        T[] items = values.items;
        for (int i = 0, n = values.size; i < n; i++)
            if (contains(items[i], identity)) return true;
        return false;
    }

    public int indexOf(final T value, boolean identity) {
        T[] items = this.items;
        if (identity || value == null) {
            for (int i = 0, n = size; i < n; i++)
                if (items[i] == value) return i;
        } else {
            for (int i = 0, n = size; i < n; i++)
                if (value.equals(items[i])) return i;
        }
        return -1;
    }

    public boolean remove(final T value, boolean identity) {
        T[] items = this.items;
        if (identity || value == null) {
            for (int i = 0, n = size; i < n; i++) {
                if (items[i] == value) {
                    removeIndex(i);
                    return true;
                }
            }
        } else {
            for (int i = 0, n = size; i < n; i++) {
                if (value.equals(items[i])) {
                    removeIndex(i);
                    return true;
                }
            }
        }
        return false;
    }

    public T removeIndex(int index) {
        if (index >= size) throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + size);
        T[] items = this.items;
        T value = items[index];
        size--;
        if (ordered)
            System.arraycopy(items, index + 1, items, index, size - index);
        else
            items[index] = items[size];
        items[size] = null;
        return value;
    }

    public T first() {
        if (size == 0) throw new IllegalStateException("Empty array cannot access index 0.");
        return items[0];
    }

    public boolean notEmpty() {
        return size > 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        size = 0;
    }

    /** Reduces the size of the backing array to the size of the actual items. This is useful to release memory when many items
     * have been removed, or if it is known that more items will not be added. */
    public void pack() {
        if (items.length != size) resize(size);
    }

    public void shuffle() {
        T[] items = this.items;
        for (int i = size - 1; i >= 0; i--) {
            int ii = MathUtils.random(i);
            T temp = items[i];
            items[i] = items[ii];
            items[ii] = temp;
        }
    }

    @Override
    public int hashCode() {
        if (!ordered) return super.hashCode();
        T[] items = this.items;
        int h = 1;
        if (size > 0) {
            int firstHashcode = 0;
            if (items[0] != null) firstHashcode = items[0].hashCode();
            // Combine the sampled elements into the hash code
            h = h * 31 + firstHashcode;
        }
        return h;
    }
    @Override
    public boolean equals(Object object) {
        if (object == this) return true;
        if (!ordered) return false;
        if (!(object instanceof Array)) return false;
        Array array = (Array)object;
        if (!array.ordered) return false;
        int n = size;
        if (n != array.size) return false;
        Object[] items1 = this.items, items2 = array.items;
        for (int i = 0; i < n; i++) {
            Object o1 = items1[i], o2 = items2[i];
            if (!(Objects.equals(o1, o2))) return false;
        }
        return true;
    }

    private T[] resize(int newSize) {
        T[] items = this.items;
        T[] newItems = (T[]) CollectionsUtils.createArray(items.getClass().getComponentType(), newSize);
        System.arraycopy(items, 0, newItems, 0, Math.min(size, newItems.length));
        this.items = newItems;
        return newItems;
    }

}
