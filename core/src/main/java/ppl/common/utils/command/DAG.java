package ppl.common.utils.command;

import ppl.common.utils.string.Strings;

import java.util.*;
import java.util.stream.Collectors;

public class DAG<K, E> {

    private final Map<K, Node<K, E>> all;

    public DAG() {
        this.all = new HashMap<>();
    }

    public boolean addNode(K key, E element) {
        Objects.requireNonNull(key, "Key is required.");
        Objects.requireNonNull(element, "Element is required.");
        if (all.containsKey(key)) {
            return false;
        }
        all.put(key, new Node<>(key, element));
        return true;
    }

    public boolean addEdge(K source, K destination) {
        Objects.requireNonNull(source, "Source key is required.");
        Objects.requireNonNull(destination, "Destination key is required.");
        if (source.equals(destination)) {
            throw new IllegalArgumentException("Source and destination of edge must be different.");
        }

        Node<K, E> srcNode = all.get(source);
        Node<K, E> dstNode = all.get(destination);
        if (srcNode == null || dstNode == null) {
            throw new IllegalArgumentException("Source and destination vertex is required.");
        }

        if (hasPath(dstNode, srcNode)) {
            throw new IllegalArgumentException(Strings.format(
                    "Will create circle, when add edge '{} -> {}'", source, destination));
        }
        return srcNode.addNext(dstNode);
    }

    public E getNode(K key) {
        Node<K, E> node = all.get(key);
        return node == null ? null : node.getElement();
    }

    @SafeVarargs
    public final List<E> getPath(K... keys) {
        if (keys.length == 0) {
            return Collections.emptyList();
        }

        return checkPaths(keys, getPaths(keys)).stream()
                .map(Node::getElement)
                .collect(Collectors.toList());
    }

    private boolean hasPath(Node<K, E> srcNode, Node<K, E> dstNode) {
        if (dstNode.hasEdge(srcNode)) {
            return true;
        }

        Set<Node<K, E>> accessibleNodes = new HashSet<>();
        Set<Node<K, E>> nextNodes = new HashSet<>();
        nextNodes.add(srcNode);
        accessibleNodes.add(srcNode);
        do {
            nextNodes = unseenDestinationNodes(accessibleNodes, nextNodes);
        } while (!nextNodes.isEmpty() && !nextNodes.contains(dstNode));
        return nextNodes.contains(dstNode);
    }

    private Set<Node<K, E>> unseenDestinationNodes(Set<Node<K, E>> seen, Set<Node<K, E>> sourceNodes) {
        Set<Node<K, E>> unseenDestinationNodes = new HashSet<>();
        for (Node<K, E> sourceNode : sourceNodes) {
            for (Node<K, E> nextNode : sourceNode.nextNodes()) {
                if (seen.add(nextNode)) {
                    unseenDestinationNodes.add(nextNode);
                }
            }
        }
        return unseenDestinationNodes;
    }

    private List<Node<K, E>> getPaths(K[] keys) {
        return Arrays.stream(keys)
                .peek(k -> {
                    if (!this.all.containsKey(k)) {
                        throw new IllegalArgumentException(Strings.format("No vertex of key '{}' exists.", k));
                    }
                }).map(this.all::get)
                .collect(Collectors.toList());
    }

    private List<Node<K, E>> checkPaths(K[] keys, List<Node<K, E>> nodes) {
        Node<K, E> src = nodes.get(0);
        for (int i = 1; i < nodes.size(); i++) {
            Node<K, E> dst = nodes.get(i);
            if (!src.hasEdge(dst)) {
                throw new IllegalArgumentException(Strings.format("No path '{}' exists.", Arrays.toString(keys)));
            }
            src = dst;
        }
        return nodes;
    }

    private static class Node<K, E> {
        private final K key;
        private final E element;
        private final HashMap<K, Node<K, E>> nextNodes;

        public Node(K key, E element) {
            this.key = key;
            this.element = element;
            this.nextNodes = new HashMap<>();
        }

        public boolean addNext(Node<K, E> node) {
            if (this.nextNodes.containsKey(node.getKey())) {
                return false;
            }
            this.nextNodes.put(node.getKey(), node);
            return true;
        }

        public K getKey() {
            return key;
        }

        public E getElement() {
            return element;
        }

        public Set<Node<K, E>> nextNodes() {
            if (this.nextNodes.isEmpty()) {
                return Collections.emptySet();
            }
            return new HashSet<>(this.nextNodes.values());
        }

        public boolean hasEdge(Node<K, E> node) {
            return this.nextNodes.containsKey(node.getKey());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node<?, ?> node = (Node<?, ?>) o;
            return Objects.equals(key, node.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }
    }
}
