package ppl.common.utils.string.trie;

class TrieNode<E> {

    private final TrieNode<E> parent;
    private final Character incident;

    private E ele;

    public TrieNode() {
        this.parent = null;
        this.incident = null;
    }

    public TrieNode(TrieNode<E> parent, Character incident) {
        this.parent = parent;
        this.incident = incident;
    }

    public TrieNode<E> getParent() {
        return parent;
    }

    public Character getIncident() {
        return incident;
    }

    public E getEle() {
        return ele;
    }

    public void setEle(E ele) {
        this.ele = ele;
    }
}
