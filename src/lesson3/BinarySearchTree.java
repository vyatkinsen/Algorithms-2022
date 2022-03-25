package lesson3;

import java.util.*;
import kotlin.NotImplementedError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// attention: Comparable is supported but Comparator is not
public class BinarySearchTree<T extends Comparable<T>> extends AbstractSet<T> implements CheckableSortedSet<T> {

    private static class Node<T> {
        final T value;
        Node<T> left = null;
        Node<T> right = null;
        Node<T> parent = null;

        Node(T value) {
            this.value = value;
        }
    }

    private Node<T> root = null;

    private int size = 0;

    @Override
    public int size() {
        return size;
    }

    private Node<T> find(T value) {
        if (root == null) return null;
        return find(root, value);
    }

    private Node<T> find(Node<T> start, T value) {
        int comparison = value.compareTo(start.value);
        if (comparison == 0) {
            return start;
        }
        else if (comparison < 0) {
            if (start.left == null) return start;
            return find(start.left, value);
        }
        else {
            if (start.right == null) return start;
            return find(start.right, value);
        }
    }

    @Override
    public boolean contains(Object o) {
        @SuppressWarnings("unchecked")
        T t = (T) o;
        Node<T> closest = find(t);
        return closest != null && t.compareTo(closest.value) == 0;
    }

    /**
     * Добавление элемента в дерево
     *
     * Если элемента нет в множестве, функция добавляет его в дерево и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     *
     * Спецификация: {@link Set#add(Object)} (Ctrl+Click по add)
     *
     * Пример
     */
    @Override
    public boolean add(T t) {
        Node<T> closest = find(t);
        int comparison = closest == null ? -1 : t.compareTo(closest.value);
        if (comparison == 0) {
            return false;
        }
        Node<T> newNode = new Node<>(t);
        if (closest == null) {
            root = newNode;
        }
        else if (comparison < 0) {
            assert closest.left == null;
            closest.left = newNode;
            closest.left.parent = closest;
        }
        else {
            assert closest.right == null;
            closest.right = newNode;
            closest.right.parent = closest;
        }
        size++;
        return true;
    }


    /**
     * Удаление элемента из дерева
     *
     * Если элемент есть в множестве, функция удаляет его из дерева и возвращает true.
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
        Node<T> deletingNode = root;
        T deletingValue = (T) o;
        if (deletingNode == null) return false;

        while (deletingNode.value != deletingValue) {                  //в этом цикле  ищем в дереве удаляемый узел
            if (deletingValue.compareTo(deletingNode.value) < 0) deletingNode = deletingNode.left;
            else deletingNode = deletingNode.right;
            if (deletingNode == null) return false;
        }
        size--;
        if (deletingNode.left == null && deletingNode.right == null) { //когда удаляемый узел - лист (нет потомков)
            substitution(deletingNode, null);
        } else if (deletingNode.right == null) {                       //когда у удаляемого узла 1 левый потомок
            substitution(deletingNode, deletingNode.left);
        } else if (deletingNode.left == null)                          //когда у удаляемого узла 1 правый потомок
            substitution(deletingNode, deletingNode.right);
        else {                                                         //когда у удаляемого узла 2 потомка
            Node<T> descendant = findMinNode(deletingNode.right);
            substitution(deletingNode, descendant);
            descendant.left = deletingNode.left;
        }
        return true;
    }

    /**
     * Функция производит замену узла replaceableNode на replacementNode
     */
    protected void substitution(Node<T> replaceableNode, Node<T> replacementNode) {
        if (replaceableNode == root) {
            root = replacementNode;
        } else if (replaceableNode.parent.left == replaceableNode) {
            replaceableNode.parent.left = replacementNode;
        } else {
            replaceableNode.parent.right = replacementNode;
        }
    }

    /**
     * Функция ищет самый левый лист из правой ветки (относительно узла startNode).
     * То есть, будет найдено минимальное число из множества чисел, которые >= startNode.
     * Функция возвращает этот самый лист.
     */
    protected Node<T> findMinNode(Node<T> startNode) {
        Node<T> parentOfMinNode, minNode, current;
        parentOfMinNode = minNode = null;
        current = startNode;
        while (current != null) {
            parentOfMinNode = minNode;
            minNode = current;
            current = current.left;
        }
        if (minNode != startNode) {
            parentOfMinNode.left = minNode.right;
            minNode.right = startNode;
        }
        return minNode;
    }


    @Nullable
    @Override
    public Comparator<? super T> comparator() {
        return null;
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new BinarySearchTreeIterator();
    }

    public class BinarySearchTreeIterator implements Iterator<T> {
        private Node<T> startNode, finishNode;
        private final int maxIndex;
        private int index = 0;

        private BinarySearchTreeIterator() {
            maxIndex = size();
            if (root == null) return;
            startNode = findNextMinNode(root);
        }

        /**
         * Проверка наличия следующего элемента
         *
         * Функция возвращает true, если итерация по множеству ещё не окончена (то есть, если вызов next() вернёт
         * следующий элемент множества, а не бросит исключение); иначе возвращает false.
         *
         * Спецификация: {@link Iterator#hasNext()} (Ctrl+Click по hasNext)
         *
         * Средняя
         */
        //T = O(1)
        //R = O(1)
        @Override
        public boolean hasNext() { return index != maxIndex; }

        /**
         * Получение следующего элемента
         *
         * Функция возвращает следующий элемент множества.
         * Так как BinarySearchTree реализует интерфейс SortedSet, последовательные
         * вызовы next() должны возвращать элементы в порядке возрастания.
         *
         * Бросает NoSuchElementException, если все элементы уже были возвращены.
         *
         * Спецификация: {@link Iterator#next()} (Ctrl+Click по next)
         *
         * Средняя
         */
        //T = O(N), где N - высота дерева
        //R = O(1)
        @Override
        public T next() {
            if (index == maxIndex || maxIndex == 0) throw new NoSuchElementException();
            if (index == 0) {               //когда next() вызвали впервые
                index++;
                finishNode = startNode;
                return startNode.value;
            }
            index++;
            if (startNode.right != null) {  //если у текущего узла есть правый узел, то есть, если текущий узел меньший из пары
                finishNode = findNextMinNode(startNode.right); //то ищем наименьший узел из правой ветви
                startNode = finishNode;
                return startNode.value;
            } else if (index != maxIndex) startNode = findNextMinParentNode(startNode); //иначе поднимаемся вверх по ветви до тех пор, пока не найдем наименьшего родителя
            finishNode = startNode;
            return startNode.value;
        }

        public Node<T> findNextMinNode(Node<T> current) {
            return current.left != null ? findNextMinNode(current.left) : current;
        }

        public Node<T> findNextMinParentNode(Node<T> current) {
            return current == current.parent.right ? findNextMinParentNode(current.parent) : current.parent;
        }

        /**
         * Удаление предыдущего элемента
         *
         * Функция удаляет из множества элемент, возвращённый крайним вызовом функции next().
         *
         * Бросает IllegalStateException, если функция была вызвана до первого вызова next() или же была вызвана
         * более одного раза после любого вызова next().
         *
         * Спецификация: {@link Iterator#remove()} (Ctrl+Click по remove)
         *
         * Сложная
         */
        //T = O(N)
        //R = O(1)
        @Override
        public void remove() {
            if (finishNode == null) throw new IllegalStateException();
            BinarySearchTree.this.remove(finishNode.value);
            finishNode = null;
        }
    }

    /**
     * Подмножество всех элементов в диапазоне [fromElement, toElement)
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева, которые
     * больше или равны fromElement и строго меньше toElement.
     * При равенстве fromElement и toElement возвращается пустое множество.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: {@link SortedSet#subSet(Object, Object)} (Ctrl+Click по subSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Очень сложная (в том случае, если спецификация реализуется в полном объёме)
     */
    @NotNull
    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        // TODO
        throw new NotImplementedError();
    }

    /**
     * Подмножество всех элементов строго меньше заданного
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева строго меньше toElement.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: {@link SortedSet#headSet(Object)} (Ctrl+Click по headSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Сложная
     */
    @NotNull
    @Override
    public SortedSet<T> headSet(T toElement) {
        // TODO
        throw new NotImplementedError();
    }

    /**
     * Подмножество всех элементов нестрого больше заданного
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева нестрого больше toElement.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: {@link SortedSet#tailSet(Object)} (Ctrl+Click по tailSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Сложная
     */
    @NotNull
    @Override
    public SortedSet<T> tailSet(T fromElement) {
        // TODO
        throw new NotImplementedError();
    }

    @Override
    public T first() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.left != null) {
            current = current.left;
        }
        return current.value;
    }

    @Override
    public T last() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.value;
    }

    public int height() {
        return height(root);
    }

    private int height(Node<T> node) {
        if (node == null) return 0;
        return 1 + Math.max(height(node.left), height(node.right));
    }

    public boolean checkInvariant() {
        return root == null || checkInvariant(root);
    }

    private boolean checkInvariant(Node<T> node) {
        Node<T> left = node.left;
        if (left != null && (left.value.compareTo(node.value) >= 0 || !checkInvariant(left))) return false;
        Node<T> right = node.right;
        return right == null || right.value.compareTo(node.value) > 0 && checkInvariant(right);
    }

}