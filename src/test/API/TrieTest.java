package test.API;

import main.API.Trie;
import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.*;

/**
 * Created by sujay on 7/28/17.
 */
public class TrieTest {


    @Test
    public void getNodeForPrefix() throws Exception {

        Trie trie = new Trie();

        // Check that terminal words are stored correctly.
        assertTrue(trie.getNodeForPrefix("ZEITGEISTS").isWord());
        assertTrue(trie.getNodeForPrefix("GREEN").isWord());
        assertTrue(trie.getNodeForPrefix("JOES").isWord());
        assertTrue(trie.getNodeForPrefix("MIZENS").isWord());

        assertFalse(trie.getNodeForPrefix("RI").isWord());
        assertFalse(trie.getNodeForPrefix("KO").isWord());
        assertFalse(trie.getNodeForPrefix("CURRYIN").isWord());

        assertNull(trie.getNodeForPrefix("SZSDFIPN"));
        assertNull(trie.getNodeForPrefix("EXTREMELYNULL"));
        assertNull(trie.getNodeForPrefix("ZAMBORINEES"));
        assertNull(trie.getNodeForPrefix("ZWEI"));

        // Use a mega dictionary available at the provided link
        trie = new Trie(new URL("https://raw.githubusercontent.com/dwyl/english-words/master/words.txt"));
        assertTrue(trie.getNodeForPrefix("ZURBARAN").isWord());
        assertTrue(trie.getNodeForPrefix("ABINERI").isWord());
        assertTrue(trie.getNodeForPrefix("JOETE").isWord());
        assertTrue(trie.getNodeForPrefix("JOGGLER").isWord());

        assertFalse(trie.getNodeForPrefix("JOGGLEWOR").isWord());
        assertFalse(trie.getNodeForPrefix("JONGLER").isWord());
        assertFalse(trie.getNodeForPrefix("KENSEIKA").isWord());

        assertNull(trie.getNodeForPrefix("SZSDFIPN"));
        assertNull(trie.getNodeForPrefix("EXTREMELYNULL"));
        assertNull(trie.getNodeForPrefix("ZAMBORINEES"));

    }

}