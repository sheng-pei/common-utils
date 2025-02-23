package ppl.common.utils.string.trie;

import ppl.common.utils.Collections;
import ppl.common.utils.Maps;

import java.util.*;

public class Trie<E> {
    private final TrieNode<E> root = new TrieNode<>();
    private final IdentityHashMap<TrieNode<E>, Map<Character, TrieNode<E>>> trie = new IdentityHashMap<>();

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

}
