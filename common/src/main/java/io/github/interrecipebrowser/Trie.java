package io.github.interrecipebrowser;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

import java.util.ArrayList;
import java.util.List;

public class Trie extends ArrayList<Trie.Vertex> {
    public Trie() {
        super();
        this.add(new Vertex());
    }

    public void add(String string) {
        int[] index = {0};
        string.codePoints().forEach(ch -> {
            index[0] = this.get(index[0]).next.computeIfAbsent(ch, _key -> {
                synchronized (this) {
                    this.add(new Vertex());
                    return this.size() - 1;
                }
            });
        });
        this.get(index[0]).setLeaf(true);
    }

    public List<String> prefixGet(String prefix) {
        ArrayList<String> list = new ArrayList<>();
        int index = 0;

        for (int ch : prefix.codePoints().toArray()) {
            Int2IntMap next = this.get(index).next;
            if(!next.containsKey(ch)) return list;
            index = next.get(ch);
        }

        getAllLeaves(index, prefix, list);
        return list;
    }

    private void getAllLeaves(int index, String prefix, List<String> list) {
        Vertex vertex = this.get(index);
        if(vertex.isLeaf()) {
            list.add(prefix);
        }
        vertex.next.int2IntEntrySet().stream().parallel().forEach(entry -> {
            getAllLeaves(entry.getIntValue(), prefix + String.valueOf(Character.toChars(entry.getIntKey())), list);
        });
    }

    public class Vertex {
        public final Int2IntMap next = new Int2IntOpenHashMap();
        private boolean leaf = false;


        public Vertex() {}


        public boolean isLeaf() {
            return leaf;
        }

        public void setLeaf(boolean leaf) {
            this.leaf = leaf;
        }
    }
}
