package lesson4;

import java.util.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Префиксное дерево для строк
 */
public class Trie extends AbstractSet<String> implements Set<String> {
    private final Node root = new Node();
    private int size = 0;

    private static class Node {
        SortedMap<Character, Node> children = new TreeMap<>();
        Node parent = null;
        String value = null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        root.children.clear();
        size = 0;
    }

    private String withZero(String initial) {
        return initial + (char) 0;
    }

    @Nullable
    private Node findNode(String element) {
        Node current = root;
        for (char character : element.toCharArray()) {
            if (current == null) return null;
            current = current.children.get(character);
        }
        return current;
    }

    @Override
    public boolean contains(Object o) {
        String element = (String) o;
        return findNode(withZero(element)) != null;
    }

    @Override
    public boolean add(String element) {
        Node current = root;
        boolean modified = false;
        StringBuilder nodeValue = new StringBuilder();
        for (char character : withZero(element).toCharArray()) {
            nodeValue.append(character);
            Node child = current.children.get(character);
            if (child != null) current = child;
            else {
                modified = true;
                Node newChild = new Node();
                current.children.put(character, newChild);
                newChild.parent = current;
                newChild.value = nodeValue.toString();
                current = newChild;
            }
        }
        if (modified) size++;
        return modified;
    }

    @Override
    public boolean remove(Object o) {
        String element = (String) o;
        Node current = findNode(element);
        if (current == null) return false;
        if (current.children.remove((char) 0) != null) {
            size--;
            removeFree(current);
            return true;
        }
        return false;
    }

    private void removeFree(@NotNull Node current) {
        if (current.children.size() == 0 && current != root) {
            current.parent.children.remove(current.value.charAt(current.value.length() - 1));
            removeFree(current.parent);
        }
    }

    /**
     * Итератор для префиксного дерева
     *
     * Спецификация: {@link Iterator} (Ctrl+Click по Iterator)
     *
     * Сложная
     */
    @NotNull
    @Override
    public Iterator<String> iterator() {
        return new PrefixTrieIterator();
    }

    public class PrefixTrieIterator implements Iterator<String> {
        private Node previousNode, startNode, finishNode;
        private final int maxIndex = size();
        private int index = 0;

        //T = O(1)
        //R = O(1)
        @Override
        public boolean hasNext() {
            return index != maxIndex;
        }

        //T = O(N), где N - длина удаляемого слова
        //R = O(1)
        @Override
        public String next() {
            if (maxIndex == 0 || index == maxIndex) throw new NoSuchElementException();
            index++;
            if (startNode == null) {
                startNode = findLeftest(root);
                finishNode = startNode;
                return startNode.parent.value;
            }
            previousNode = startNode;
            boolean visited = true;
            while (visited || (startNode != finishNode)) {
                visited = true;
                finishNode = startNode;
                for (Map.Entry<Character, Node> childNode : finishNode.parent.children.entrySet()) {
                    if (!visited) {
                        startNode = findLeftest(childNode.getValue());
                        if (startNode.value.charAt(startNode.value.length() - 1) == (char) 0) {
                            finishNode = startNode;
                            return startNode.parent.value;
                        }
                    } else if (childNode.getKey() == startNode.value.charAt(startNode.value.length() - 1)) visited = false;
                }
                startNode = finishNode.parent;
            }
            return finishNode.value;
        }

        private Node findLeftest(@NotNull Node node) {
            if (node.children.size() != 0) {
                for (Map.Entry<Character, Node> childNode : node.children.entrySet()) {
                    if (childNode.getKey() == (char) 0) return childNode.getValue();
                    else return findLeftest(childNode.getValue());
                }
            }
            return node;
        }

        //T = O(N)
        //R = O(1)
        @Override
        public void remove() {
            if (finishNode == null) throw new IllegalStateException();
            startNode = previousNode;
            finishNode.parent.children.remove((char) 0);
            removeFree(finishNode.parent);
            finishNode = null;
            size--;
        }
    }
}