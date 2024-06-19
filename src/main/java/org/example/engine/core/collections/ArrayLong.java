package org.example.engine.core.collections;

import org.example.engine.core.math.MathUtils;

import java.util.Arrays;

public class ArrayLong {

    public long[] items;
    public int size;
    public boolean ordered;

    public ArrayLong() {
        this(true, 16);
    }

    public ArrayLong(int capacity) {
        this(true, capacity);
    }

    public ArrayLong(boolean ordered, int capacity) {
        this.ordered = ordered;
        this.items = new long[capacity];
    }

    public void add(final long value) {
        long[] items = this.items;
        if (size + 1 >= items.length) items = resize(Math.max(8, size * 2));
        items[size] = value;
        size++;
    }

    public void add(final long v1, final long v2) {
        long[] items = this.items;
        if (size + 1 >= items.length) items = resize(Math.max(8, size * 2));
        items[size] = v1;
        items[size + 1] = v2;
        size += 2;
    }

    public void add(final long v1, final long v2, final long v3) {
        long[] items = this.items;
        if (size + 1 >= items.length) items = resize(Math.max(8, size * 2));
        items[size] = v1;
        items[size + 1] = v2;
        items[size + 2] = v3;
        size += 3;
    }

    public long get(int index) {
        if (index >= size) throw new IndexOutOfBoundsException("Tried to retrieve array element at index " + index + " >= " + size);
        return items[index];
    }

    public long getCircular(int index) {
        if (index >= size) return items[index % size];
        else if (index < 0) return items[index % size + size];
        return items[index];
    }

    public boolean contains(long value) {
        int i = size - 1;
        long[] items = this.items;
        while (i >= 0)
            if (items[i--] == value) return true;
        return false;
    }

    public long removeIndex(int index) {
        if (index >= size) throw new IndexOutOfBoundsException("Tried to retrieve array element at index " + index + " >= " + size);
        long[] items = this.items;
        long value = items[index];
        size--;
        if (ordered)
            System.arraycopy(items, index + 1, items, index, size - index);
        else
            items[index] = items[size];
        return value;
    }

    public boolean removeValue(long value) {
        long[] items = this.items;
        for (int i = 0, n = size; i < n; i++) {
            if (items[i] == value) {
                removeIndex(i);
                return true;
            }
        }
        return false;
    }

    public long first() {
        if (size == 0) throw new IllegalStateException("Empty array cannot access index 0.");
        return items[0];
    }

    /** Returns the last item. */
    public long peek () {
        return items[size - 1];
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

    private long[] resize(int newSize) {
        long[] newItems = new long[newSize];
        long[] items = this.items;
        System.arraycopy(items, 0, newItems, 0, Math.min(size, newItems.length));
        this.items = newItems;
        return newItems;
    }

    public void sort() {
        Arrays.sort(items);
    }

    public void shuffle() {
        long[] items = this.items;
        for (int i = size - 1; i >= 0; i--) {
            int ii = MathUtils.random(i);
            long temp = items[i];
            items[i] = items[ii];
            items[ii] = temp;
        }
    }

    @Override
    public int hashCode () {
        if (!ordered) return super.hashCode();
        long[] items = this.items;
        int h = 1;
        if (size > 0) {
            int first = (int)(items[0] ^ (items[0] >>> 32)); // First element
            // Combine the sampled elements into the hash code
            h = h * 31 + first;
        }
        return h;
    }

    public boolean equals (Object object) {
        if (object == this) return true;
        if (!ordered) return false;
        if (!(object instanceof ArrayLong)) return false;
        ArrayLong array = (ArrayLong) object;
        if (!array.ordered) return false;
        int n = size;
        if (n != array.size) return false;
        long[] items1 = this.items, items2 = array.items;
        for (int i = 0; i < n; i++)
            if (items1[i] != items2[i]) return false;
        return true;
    }

    @Override
    public String toString () {
        if (size == 0) return "[]";
        long[] items = this.items;
        StringBuilder buffer = new StringBuilder(32);
        buffer.append('[');
        buffer.append(items[0]);
        for (int i = 1; i < size; i++) {
            buffer.append(", ");
            buffer.append(items[i]);
        }
        buffer.append(']');
        return buffer.toString();
    }

}
