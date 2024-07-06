package org.example.engine.core.collections;

import org.example.engine.core.math.MathUtils;
import org.example.engine.core.memory.MemoryPool;

import java.util.*;

public class Array<T> implements Iterable<T>, MemoryPool.Reset {

    public  int              size;
    public  T[]              items;
    public  boolean          ordered;
    private ArrayIterable<T> iterable;

    /** Creates an ordered array with a capacity of 16. */
    public Array() {
        this(true, 16);
    }

    /** Creates an ordered array with the specified capacity. */
    public Array(int capacity) {
        this(true, capacity);
    }

    public Array(boolean ordered, int capacity) {
        capacity = Math.max(1, capacity);
        this.ordered = ordered;
        items = (T[])new Object[capacity];
    }

    /** Creates a new array with {@link #items} of the specified type.
     * @param ordered If false, methods that remove elements may change the order of other elements in the array, which avoids a
     *           memory copy.
     * @param capacity Any elements added beyond this will cause the backing array to be grown. */
    public Array(boolean ordered, int capacity, Class arrayType) {
        this.ordered = ordered;
        items = (T[]) CollectionsUtils.createArray(arrayType, capacity);
    }

    public Array(boolean ordered, T[] array, int start, int count) {
        this(ordered, count, array.getClass().getComponentType());
        size = count;
        System.arraycopy(array, start, items, 0, size);
    }

    /** Creates a new array containing the elements in the specified array. The new array will have the same type of backing array
     * and will be ordered if the specified array is ordered. The capacity is set to the number of elements, so any subsequent
     * elements added will cause the backing array to be grown. */
    public Array (Array<? extends T> array) {
        this(array.ordered, array.size, array.items.getClass().getComponentType());
        size = array.size;
        System.arraycopy(array.items, 0, items, 0, size);
    }

    /** Creates a new ordered array containing the elements in the specified array. The new array will have the same type of
     * backing array. The capacity is set to the number of elements, so any subsequent elements added will cause the backing array
     * to be grown. */
    public Array (T[] array) {
        this(true, array, 0, array.length);
    }

    public void add(T value) {
        T[] items = this.items;
        if (size == items.length) items = resize(Math.max(8, (int)(size * 1.75f)));
        items[size++] = value;
    }

    public void add(T value1, T value2) {
        T[] items = this.items;
        if (size + 1 >= items.length) items = resize(Math.max(8, (int)(size * 1.75f)));
        items[size] = value1;
        items[size + 1] = value2;
        size += 2;
    }

    public void add(T value1, T value2, T value3) {
        T[] items = this.items;
        if (size + 2 >= items.length) items = resize(Math.max(8, (int)(size * 1.75f)));
        items[size] = value1;
        items[size + 1] = value2;
        items[size + 2] = value3;
        size += 3;
    }

    public void add(T value1, T value2, T value3, T value4) {
        T[] items = this.items;
        if (size + 3 >= items.length) items = resize(Math.max(8, (int)(size * 1.8f))); // 1.75 isn't enough when size=5.
        items[size] = value1;
        items[size + 1] = value2;
        items[size + 2] = value3;
        items[size + 3] = value4;
        size += 4;
    }

    public void addAll(Array<? extends T> array) {
        addAll(array.items, 0, array.size);
    }

    public void addAll(Array<? extends T> array, int start, int count) {
        if (start + count > array.size)
            throw new IllegalArgumentException("start + count must be <= size: " + start + " + " + count + " <= " + array.size);
        addAll(array.items, start, count);
    }

    public void addAll(T... array) {
        addAll(array, 0, array.length);
    }

    public void addAll(T[] array, int start, int count) {
        T[] items = this.items;
        int sizeNeeded = size + count;
        if (sizeNeeded > items.length) items = resize(Math.max(Math.max(8, sizeNeeded), (int)(size * 1.75f)));
        System.arraycopy(array, start, items, size, count);
        size = sizeNeeded;
    }

    public T get(int index) {
        if (index >= size) throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + size);
        return items[index];
    }

    public T get(int index, T defaultValue) {
        if (index >= size || index < 0) return defaultValue;
        return items[index];
    }

    public T getCyclic(int index) {
        if (size == 0) throw new IllegalStateException(Array.class.getSimpleName() + " is empty.");
        if (index >= size) return items[index % size];
        else if (index < 0) return items[index % size + size];
        return items[index];
    }

    // if force is true, the item will be inserted into the index, growing the array if needed.
    // TODO: test
    public void set(int index, T value, boolean force) {
        if (index < 0) throw new IndexOutOfBoundsException("index can't be < 0: " + index + " < 0");

        if (!force) {
            if (index >= size) throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + size);
            items[index] = value;
            return;
        }

        if (index < size) {
            items[index] = value;
            return;
        }

        if (index > size && index < items.length) {
            items[index] = value;
            size = index + 1;
            return;
        }

        items = resize(index + 1);
        items[index] = value;
        size = index + 1;
    }

    public void insert(int index, T value) {
        if (index > size) throw new IndexOutOfBoundsException("index can't be > size: " + index + " > " + size);
        T[] items = this.items;
        if (size == items.length) items = resize(Math.max(8, (int)(size * 1.75f)));
        if (ordered)
            System.arraycopy(items, index, items, index + 1, size - index);
        else
            items[size] = items[index];
        size++;
        items[index] = value;
    }

    public void swap(int first, int second) {
        if (first >= size) throw new IndexOutOfBoundsException("first can't be >= size: " + first + " >= " + size);
        if (second >= size) throw new IndexOutOfBoundsException("second can't be >= size: " + second + " >= " + size);
        T[] items = this.items;
        T firstValue = items[first];
        items[first] = items[second];
        items[second] = firstValue;
    }

    public boolean contains (T value, boolean identity) {
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

    public int indexOf(T value, boolean identity) {
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

    public boolean removeValue (T value, boolean identity) {
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

    public boolean removeAll (Array<? extends T> array, boolean identity) {
        int size = this.size;
        int startSize = size;
        T[] items = this.items;
        if (identity) {
            for (int i = 0, n = array.size; i < n; i++) {
                T item = array.get(i);
                for (int ii = 0; ii < size; ii++) {
                    if (item == items[ii]) {
                        removeIndex(ii);
                        size--;
                        break;
                    }
                }
            }
        } else {
            for (int i = 0, n = array.size; i < n; i++) {
                T item = array.get(i);
                for (int ii = 0; ii < size; ii++) {
                    if (item.equals(items[ii])) {
                        removeIndex(ii);
                        size--;
                        break;
                    }
                }
            }
        }
        return size != startSize;
    }

    /** Removes the items between the specified indices, inclusive. */
    public void removeRange (int start, int end) {
        int n = size;
        if (end >= n) throw new IndexOutOfBoundsException("end can't be >= size: " + end + " >= " + size);
        if (start > end) throw new IndexOutOfBoundsException("start can't be > end: " + start + " > " + end);
        T[] items = this.items;
        int count = end - start + 1, lastIndex = n - count;
        if (ordered)
            System.arraycopy(items, start + count, items, start, n - (start + count));
        else {
            int i = Math.max(lastIndex, end + 1);
            System.arraycopy(items, i, items, start, n - i);
        }
        for (int i = lastIndex; i < n; i++)
            items[i] = null;
        size = n - count;
    }

    public T pop() {
        if (size == 0) throw new IllegalStateException("Array is empty.");
        --size;
        T item = items[size];
        items[size] = null;
        return item;
    }

    public T peek() {
        if (size == 0) throw new IllegalStateException("Array is empty.");
        return items[size - 1];
    }

    public T first() {
        if (size == 0) throw new IllegalStateException("Array is empty.");
        return items[0];
    }

    static public <T> Array<T> of(T... array) {
        return new Array(array);
    }

    /** Returns true if the array has one or more items. */
    public boolean notEmpty() {
        return size > 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        Arrays.fill(items, 0, size, null);
        size = 0;
    }

    public void pack() {
        if (items.length != size) resize(size);
    }

    public T[] ensureCapacity(int additionalCapacity) {
        if (additionalCapacity < 0) throw new IllegalArgumentException("additionalCapacity must be >= 0: " + additionalCapacity);
        int sizeNeeded = size + additionalCapacity;
        if (sizeNeeded > items.length) resize(Math.max(Math.max(8, sizeNeeded), (int)(size * 1.75f)));
        return items;
    }

    public T[] setSize(int newSize) {
        truncate(newSize);
        if (newSize > items.length) resize(Math.max(8, newSize));
        size = newSize;
        return items;
    }

    /** Creates a new backing array with the specified size containing the current items. */
    protected T[] resize(int newSize) {
        T[] items = this.items;
        T[] newItems = (T[]) CollectionsUtils.createArray(items.getClass().getComponentType(), newSize);
        System.arraycopy(items, 0, newItems, 0, Math.min(size, newItems.length));
        this.items = newItems;
        return newItems;
    }

    public void sort() {
        CollectionsUtils.sort(items, 0, size);
    }

    public void sort(Comparator<? super T> comparator) {
        CollectionsUtils.sort(items, comparator, 0, size);
    }

    public void reverse() {
        T[] items = this.items;
        for (int i = 0, lastIndex = size - 1, n = size / 2; i < n; i++) {
            int ii = lastIndex - i;
            T temp = items[i];
            items[i] = items[ii];
            items[ii] = temp;
        }
    }

    public void shuffle () {
        T[] items = this.items;
        for (int i = size - 1; i > 0; i--) {
            int ii = MathUtils.random(i);
            T temp = items[i];
            items[i] = items[ii];
            items[ii] = temp;
        }
    }

    public void removeDuplicates(boolean identity) {
        Array<T> uniques = new Array<>();
        for (int i = 0; i < size; i++) {
            T item = items[i];
            if (uniques.contains(item, identity)) continue;
            uniques.add(item);
        }
        this.items = uniques.items;
        this.size = uniques.size;
    }

    public boolean containsDuplicates(boolean identity) {
        for (int i = 0; i < size; i++) {
            T obj = items[i];
            for (int j = i + 1; j < size; j++) {
                T other = items[j];
                if (identity && obj == other) return true;
                if (!identity && Objects.equals(obj, other)) return true;
            }
        }
        return false;
    }

    /** Reduces the size of the array to the specified size. If the array is already smaller than the specified size, no action is
     * taken. */
    public void truncate(int newSize) {
        if (newSize < 0) throw new IllegalArgumentException("newSize must be >= 0: " + newSize);
        if (size <= newSize) return;
        for (int i = newSize; i < size; i++)
            items[i] = null;
        size = newSize;
    }

    /** Returns a random item from the array, or null if the array is empty. */
    public T random() {
        if (size == 0) return null;
        return items[MathUtils.random(0, size - 1)];
    }

    public <V> V[] toArray (Class<V> type) {
        V[] result = (V[]) CollectionsUtils.createArray(type, size);
        System.arraycopy(items, 0, result, 0, size);
        return result;
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

    public boolean equalsIdentity(Object object) {
        if (object == this) return true;
        if (!ordered) return false;
        if (!(object instanceof Array)) return false;
        Array array = (Array)object;
        if (!array.ordered) return false;
        int n = size;
        if (n != array.size) return false;
        Object[] items1 = this.items, items2 = array.items;
        for (int i = 0; i < n; i++)
            if (items1[i] != items2[i]) return false;
        return true;
    }

    @Override
    public void reset() {
        clear();
        this.ordered = true;
    }

    @Override
    public String toString() {
        if (size == 0) return "[]";
        T[] items = this.items;
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
    public Array.ArrayIterator<T> iterator() {
        if (this.iterable == null) {
            this.iterable = new Array.ArrayIterable<>(this);
        }
        return this.iterable.iterator();
    }

    public static class ArrayIterator<T> implements Iterator<T>, Iterable<T> {

        private final Array<T> array;
        private final boolean             allowRemove;
        private       int                 index;
        private       boolean             valid = true;

        public ArrayIterator (Array<T> array, boolean allowRemove) {
            this.array = array;
            this.allowRemove = allowRemove;
        }

        public boolean hasNext () {
            if (!valid) {
                throw new IllegalStateException("#iterator() cannot be used nested.");
            }
            return index < array.size;
        }

        public T next () {
            if (index >= array.size) throw new NoSuchElementException(String.valueOf(index));
            if (!valid) {
                throw new IllegalStateException("#iterator() cannot be used nested.");
            }
            return array.items[index++];
        }

        public void remove () {
            if (!allowRemove) throw new IllegalStateException("Remove not allowed.");
            index--;
            array.removeIndex(index);
        }

        public void reset () {
            index = 0;
        }
        public ArrayIterator<T> iterator () {
            return this;
        }

    }

    public static class ArrayIterable<T> implements Iterable<T> {

        private final Array<T> array;
        private final boolean             allowRemove;
        private       ArrayIterator<T>    iterator1;
        private       ArrayIterator<T>    iterator2;

        public ArrayIterable (Array<T> array) {
            this(array, true);
        }

        public ArrayIterable (Array<T> array, boolean allowRemove) {
            this.array = array;
            this.allowRemove = allowRemove;
        }

        public ArrayIterator<T> iterator () {
            if (iterator1 == null) {
                iterator1 = new ArrayIterator<>(array, allowRemove);
                iterator2 = new ArrayIterator<>(array, allowRemove);
            }
            if (!iterator1.valid) {
                iterator1.index = 0;
                iterator1.valid = true;
                iterator2.valid = false;
                return iterator1;
            }
            iterator2.index = 0;
            iterator2.valid = true;
            iterator1.valid = false;
            return iterator2;
        }
    }

}
