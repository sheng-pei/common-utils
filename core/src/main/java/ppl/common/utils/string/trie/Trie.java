package ppl.common.utils.string.trie;

import ppl.common.utils.Collections;
import ppl.common.utils.Maps;
import ppl.common.utils.ext.parser.ExtParser;

import java.util.*;
import java.util.stream.Collectors;

public class Trie<E> {
    private final TrieNode<E> root;
    private final IdentityHashMap<TrieNode<E>, Map<Character, TrieNode<E>>> trie;

    public Trie() {
        this.root = new TrieNode<>();
        this.trie = new IdentityHashMap<>();
    }

    private Trie(TrieNode<E> root, IdentityHashMap<TrieNode<E>, Map<Character, TrieNode<E>>> trie) {
        this.root = root;
        this.trie = trie;
    }

    public void put(String prefix, E e) {
        Objects.requireNonNull(prefix);
        if (prefix.isEmpty()) {
            root.setEle(e);
            return;
        }

        Stack<TrieNode<E>> path = Collections.emptyStack();
        char[] chars = prefix.toCharArray();
        TrieNode<E> curr = root;
        for (char c : chars) {
            Map<Character, TrieNode<E>> children = trie.computeIfAbsent(curr, k -> new HashMap<>());
            final TrieNode<E> parent = curr;
            curr = children.computeIfAbsent(c, k -> new TrieNode<>(parent, k));
            if (e == null) {
                if (path.isEmpty()) {
                    path = new Stack<>();
                }
                path.add(curr);
            }
        }
        curr.setEle(e);

        while (!path.isEmpty()) {
            emitIfEmpty(path.pop());
        }
    }

    public void remove(String prefix) {
        put(prefix, null);
    }

    private void emitIfEmpty(TrieNode<E> node) {
        if (node.getEle() == null && !trie.containsKey(node)) {
            if (node != root) {
                Map<Character, TrieNode<E>> children = trie.get(node.getParent());
                if (children != null) {
                    children.remove(node.getIncident());
                }
            }
        }
    }

    public E get(String prefix) {
        Objects.requireNonNull(prefix);
        char[] chars = prefix.toCharArray();
        TrieNode<E> curr = root;
        for (char c : chars) {
            Map<Character, TrieNode<E>> children = trie.get(curr);
            if (Maps.isEmpty(children) || !children.containsKey(c)) {
                return null;
            }
            curr = children.get(c);
        }
        return curr.getEle();
    }

    public E get(String prefix, E def) {
        E ret = get(prefix);
        return ret == null ? def : ret;
    }

    public Searcher searcher(E def) {
        return new Searcher(def);
    }

    public Searcher searcher() {
        return new Searcher();
    }

    public class Searcher {
        private TrieNode<E> curr = root;
        private E def;

        private Searcher() {}

        private Searcher(E def) {
            this.def = def;
        }

        public E current() {
            E ret = def;
            if (curr != null && curr.getEle() != null) {
                ret = curr.getEle();
            }
            return ret;
        }

        public boolean hasNext() {
            return curr != null;
        }

        public E next(char c) {
            if (curr == null) {
                return def;
            }

            Map<Character, TrieNode<E>> children = trie.get(curr);
            if (Maps.isEmpty(children) || !children.containsKey(c)) {
                curr = null;
                return def;
            }
            curr = children.get(c);
            return current();
        }
    }

    public Trie<E> copy() {
        Map<TrieNode<E>, Map<Character, TrieNode<E>>> trie = this.trie;
        Set<TrieNode<E>> exists = trie.values().stream()
                .flatMap(m -> m.values().stream())
                .collect(() -> Collections.newSetFromMap(new IdentityHashMap<>()),
                        Set::add, Set::addAll);
        exists.add(this.root);

        IdentityHashMap<TrieNode<E>, TrieNode<E>> newNodes = new IdentityHashMap<>();
        for (TrieNode<E> e : exists) {
            copyNode(e, newNodes);
        }

        TrieNode<E> root = newNodes.get(this.root);
        IdentityHashMap<TrieNode<E>, Map<Character, TrieNode<E>>> map = new IdentityHashMap<>();
        for (TrieNode<E> k : trie.keySet()) {
            Map<Character, TrieNode<E>> m = map.computeIfAbsent(newNodes.get(k), k1 -> new HashMap<>());
            for (Character c : trie.get(k).keySet()) {
                m.put(c, newNodes.get(trie.get(k).get(c)));
            }
        }
        return new Trie<>(root, map);
    }

    private TrieNode<E> copyNode(TrieNode<E> source, IdentityHashMap<TrieNode<E>, TrieNode<E>> newNodes) {
        if (source == null) {
            return null;
        }

        if (newNodes.containsKey(source)) {
            return newNodes.get(source);
        }

        TrieNode<E> newParent = copyNode(source.getParent(), newNodes);
        TrieNode<E> ret = new TrieNode<>(newParent, source.getIncident());
        ret.setEle(source.getEle());
        newNodes.put(source, ret);
        return ret;
    }

    public List<E> getAll() {
        return trie.values().stream()
                .flatMap(m -> m.values().stream())
                .map(TrieNode::getEle)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}
