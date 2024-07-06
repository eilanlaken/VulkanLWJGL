package org.example.engine.core.collections;

// TODO: implement.
@Deprecated public class LinkedListInt {

    public int[] items;
    public boolean[] removed;
    public int size;

    public LinkedListInt(int capacity) {
        this.items = new int[capacity];
        this.removed = new boolean[capacity];
    }

    public int get(int index) {
        if (index >= size) throw new CollectionsException("Index out of bounds.");
        int i = 0;
        while (index > 0) {
            if (!removed[i]) {


            } else {

            }
        }
        return items[index];
    }

    public void add(int value) {
        if (size + 1 >= items.length) resize(Math.max(8, size + 10));
        items[size] = value;
        size++;
    }


    private void resize(int newSize) {
        int[] newItems = new int[newSize];
        int[] items = this.items;
        System.arraycopy(items, 0, newItems, 0, Math.min(size, newItems.length));
        this.items = newItems;

        boolean[] newRemoved = new boolean[newSize];
        boolean[] removed = this.removed;
        System.arraycopy(removed, 0, newRemoved, 0, Math.min(size, newRemoved.length));
        this.removed = newRemoved;
    }

    @Override
    public String toString () {
        if (size == 0) return "[]";
        int[] items = this.items;
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
