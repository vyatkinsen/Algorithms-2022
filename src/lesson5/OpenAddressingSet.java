package lesson5;

import org.jetbrains.annotations.NotNull;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class OpenAddressingSet<T> extends AbstractSet<T> {
    private final int bits;

    private final int capacity;

    private final Object[] storage;

    private final Object OBJDELETED = new Object();

    private int size = 0;

    private int startingIndex(Object element) {
        return element.hashCode() & (0x7FFFFFFF >> (31 - bits));
    }

    public OpenAddressingSet(int bits) {
        if (bits < 2 || bits > 31) throw new IllegalArgumentException();
        this.bits = bits;
        capacity = 1 << bits;
        storage = new Object[capacity];
    }

    @Override
    public int size() {
        return size;
    }

    /**
     * Проверка, входит ли данный элемент в таблицу
     */
    @Override
    public boolean contains(Object o) {
        int index = startingIndex(o);
        Object current = storage[index];
        while (current != null) {
            if (current.equals(o)) return true;
            index = (index + 1) % capacity;
            current = storage[index];
        }
        return false;
    }

    /**
     * Добавление элемента в таблицу.
     *
     * Не делает ничего и возвращает false, если такой же элемент уже есть в таблице.
     * В противном случае вставляет элемент в таблицу и возвращает true.
     *
     * Бросает исключение (IllegalStateException) в случае переполнения таблицы.
     * Обычно Set не предполагает ограничения на размер и подобных контрактов,
     * но в данном случае это было введено для упрощения кода.
     */
    @Override
    public boolean add(T t) {
        int startingIndex = startingIndex(t);
        int index = startingIndex;
        Object current = storage[index];
        while (current != OBJDELETED && current != null) {
            if (current.equals(t)) return false;
            index = (index + 1) % capacity;
            if (index == startingIndex) throw new IllegalStateException("Table is full");
            current = storage[index];
        }
        storage[index] = t;
        size++;
        return true;
    }

    /**
     * Удаление элемента из таблицы
     *
     * Если элемент есть в таблице, функция удаляет его из дерева и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     * Высота дерева не должна увеличиться в результате удаления.
     *
     * Спецификация: {@link Set#remove(Object)} (Ctrl+Click по remove)
     *
     * Средняя
     */
    //T = O(N)
    //R = O(1)
    @Override
    public boolean remove(Object o) {
        int index = startingIndex(o);
        Object current = storage[index];
        while (current != null) {
            if (current.equals(o)) {
                storage[index] = OBJDELETED;
                size--;
                return true;
            }
            index = (index + 1) % capacity;
            current = storage[index];
        }
        return false;
    }

    /**
     * Создание итератора для обхода таблицы
     *
     * Не забываем, что итератор должен поддерживать функции next(), hasNext(),
     * и опционально функцию remove()
     *
     * Спецификация: {@link Iterator} (Ctrl+Click по Iterator)
     *
     * Средняя (сложная, если поддержан и remove тоже)
     */
    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new OpenAddressingSetIterator();
    }

    public class OpenAddressingSetIterator implements Iterator<T> {
        private final int maxIndex = size();
        private int iterationIndex = 0;

        private Object currentObject;
        private int currentObjectIndex = -1;

        //T = O(1)
        //R = O(1)
        @Override
        public boolean hasNext() {
            return iterationIndex != maxIndex;
        }

        //T = O(N)
        //R = O(1)
        @SuppressWarnings("unchecked")
        @Override
        public T next() {
            if (iterationIndex == maxIndex || maxIndex == 0) throw new NoSuchElementException();
            currentObjectIndex++;
            currentObject = storage[currentObjectIndex];
            while (currentObject == null || currentObject == OBJDELETED) {
                currentObjectIndex++;
                currentObject = storage[currentObjectIndex];
            }
            iterationIndex++;
            return (T) currentObject;
        }

        //T = O(1)
        //R = O(1)
        @Override
        public void remove() {
            if (currentObject == null || currentObjectIndex < 0) throw new IllegalStateException();
            storage[currentObjectIndex] = OBJDELETED;
            currentObject = null;
            size--;
        }
    }
}
