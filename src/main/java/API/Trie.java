package API;

import util.TrieNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * An uncompressed prefix tree built from the scrabble dictionary available from the hardcoded URL.
 *
 * Created by sujay on 7/21/17.
 */
public class Trie {

    /**
     * The root of the trie.
     */
    private TrieNode root;

    /**
     * Constructor that builds prefix tree from the scrabble dictionary located at the hardcoded URL.
     */
    public Trie()
    {
        setRoot(new TrieNode(false));
        try
        {
            URL dictionary = new URL("https://raw.githubusercontent.com/sujayt123/ScrabbleClone/master/dictionary.txt");
            try (BufferedReader br = new BufferedReader(new InputStreamReader(dictionary.openStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    // process the line.
                    getRoot().insertWord(line, 0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        catch(MalformedURLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Constructor that builds a trie from the dictionary .txt file located at the provided URL.
     *
     * Requires:
     * - dictionaryTextFileOnline to be a well-formed URL that points to a dictionary online
     * - the dictionary itself must contain exactly one word per line
     *
     * @param dictionaryTextFileOnline a link to a valid dictionary online
     */
    public Trie(URL dictionaryTextFileOnline)
    {
        setRoot(new TrieNode(false));
        try (BufferedReader br = new BufferedReader(new InputStreamReader(dictionaryTextFileOnline.openStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                // process the line.
                getRoot().insertWord(line.toUpperCase(), 0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the trie node corresponding to the prefix s, null otherwise.
     * @param s a string whose existence as a prefix we check in the trie
     * @return the trie node corresponding to prefix s, null otherwise
     */
    public TrieNode getNodeForPrefix(String s)
    {
        return getRoot().getNodeForPrefix(s, 0);
    }

    /**
     * Gets the root of the trie.
     *
     * @return the root of the trie
     */
    public TrieNode getRoot() {
        return root;
    }

    /**
     * Sets the root of the trie.
     * @param root the root of the trie
     */
    private void setRoot(TrieNode root) {
        this.root = root;
    }
}
