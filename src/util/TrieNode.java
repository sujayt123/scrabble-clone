package util;

import java.util.HashMap;
import java.util.Map;

/**
 * A node for a prefix tree.
 *
 * Created by sujay on 7/21/17.
 */
public class TrieNode {

    /**
     * The possible extensions that could be applied to the prefix representing this node
     * to compose a valid word.
     */
    private Map<Character, TrieNode> outgoingEdges;

    /**
     * A counter for debugging purposes. Not for client use.
     */
    private static int numberInsertions = 0;

    /**
     * Indicates whether the character in the Trie's root-child-child-...-this chain comprises a valid word.
     */
    private boolean isWord;

    TrieNode(boolean isWord)
    {
        this.isWord = isWord;
        outgoingEdges = new HashMap<>();
    }

    public Map<Character, TrieNode> getOutgoingEdges() {
        return outgoingEdges;
    }

    public boolean isWord() {
        return isWord;
    }

    void insertWord(String s, int index) {
        numberInsertions++;
        if (index < s.length())
        {
                TrieNode child = outgoingEdges.containsKey(s.charAt(index)) ?
                        outgoingEdges.get(s.charAt(index)) :
                        new TrieNode(false);
                outgoingEdges.put(s.charAt(index), child);
                child.insertWord(s, index + 1);
        }
        else {
            isWord = true;
        }
    }

    public TrieNode getNodeForPrefix(String s, int index) {
        if (index == s.length())
        {
            return this;
        }
        // Otherwise index is less than s.length.
        if (!outgoingEdges.containsKey(s.charAt(index)))
        {
            return null;
        }
        return outgoingEdges.get(s.charAt(index)).getNodeForPrefix(s, index + 1);
    }
}
