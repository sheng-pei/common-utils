package ppl.common.utils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public final class Collections {
    private Collections() {}

    @SuppressWarnings("rawtypes")
    private static final Stack EMPTY_STACK = new Stack() {
        @Override
        public Object push(Object item) {
            throw new UnsupportedOperationException();
        }

        @Override
        public synchronized void ensureCapacity(int minCapacity) {
            if (minCapacity > 0) {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        public synchronized void setSize(int newSize) {
            if (newSize > 0) {
                throw new UnsupportedOperationException();
            }
            super.setSize(newSize);
        }

        @Override
        public synchronized void insertElementAt(Object obj, int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public synchronized void addElement(Object obj) {
            throw new UnsupportedOperationException();
        }

        @Override
        public synchronized boolean add(Object object) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int index, Object element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public synchronized boolean addAll(Collection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public synchronized boolean retainAll(Collection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public synchronized boolean addAll(int index, Collection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Enumeration elements() {
            return java.util.Collections.emptyEnumeration();
        }

        @Override
        public synchronized ListIterator listIterator(int index) {
            if (index < 0 || index > size())
                throw new IndexOutOfBoundsException("Index: " + index);
            return java.util.Collections.emptyListIterator();
        }

        @Override
        public synchronized ListIterator listIterator() {
            return java.util.Collections.emptyListIterator();
        }

        @Override
        public synchronized Iterator iterator() {
            return java.util.Collections.emptyIterator();
        }

        @Override
        public synchronized boolean removeIf(Predicate filter) {
            Objects.requireNonNull(filter);
            return false;
        }

        @Override
        public synchronized void replaceAll(UnaryOperator operator) {
            Objects.requireNonNull(operator);
        }

        @Override
        public Spliterator spliterator() {
            return Spliterators.emptySpliterator();
        }

        @Override
        public synchronized void forEach(Consumer action) {
            Objects.requireNonNull(action);
        }

        @Override
        public synchronized void sort(Comparator c) {
        }
    };

    public static boolean isEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> c) {
        return !isEmpty(c);
    }

    public static <E> Stack<E> emptyStack() {
        @SuppressWarnings("unchecked")
        Stack<E> ret = (Stack<E>) EMPTY_STACK;
        return ret;
    }

    public static <E> List<E> emptyList() {
        return java.util.Collections.emptyList();
    }

    public static <E> Iterator<E> emptyIterator() {
        return java.util.Collections.emptyIterator();
    }

    public static <E> Set<E> newSetFromMap(Map<E, Boolean> map) {
        return java.util.Collections.newSetFromMap(map);
    }

}
