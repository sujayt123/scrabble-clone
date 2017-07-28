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
     * Indicates whether the character in the Trie's root-child-child-...-this chain comprises a valid word.
     */
    private boolean isWord;

    /**
     * Constructs a new TrieNode
     *
     * @param isWord true if the trienode corresponds to a completed word, false otherwise
     */
    TrieNode(boolean isWord)
    {
        this.isWord = isWord;
        outgoingEdges = new HashMap<>();
    }

    /**
     * Get the outgoing edges.
     *
     * @return outgoing edges from this trie node within the containing prefix tree
     */
    public Map<Character, TrieNode> getOutgoingEdges() {
        return outgoingEdges;
    }

    /**
     * Returns true if this trienode corresponds to a terminal word, false otherwise.
     * @return isWord
     */

    public boolean isWord() {
        return isWord;
    }

    /**
     * Recursively inserts a word into the prefix tree, one trienode at a time.
     *
     * @param s the string to insert
     * @param index the letter in the string to insert into this trienode
     */
    void insertWord(String s, int index) {
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

    /**
     * Gets the trie node for a provided prefix if it exists, null otherwise.
     *
     * @param s the string to consider
     * @param index the letter in the string to look at in this trienode
     * @return the trie node for a provided prefix if it exists, null otherwise.
     */
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
