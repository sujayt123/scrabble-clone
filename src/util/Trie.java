package util;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * An uncompressed prefix tree built from the scrabble dictionary available from the hardcoded URL.
 *
 * Created by sujay on 7/21/17.
 */
public class Trie {

    public TrieNode root;

    public Trie()
    {
        root = new TrieNode(false);
        try
        {
            URL dictionary = new URL("https://raw.githubusercontent.com/sujayt123/ScrabbleClone/master/src/util/dictionary.txt");
            try (BufferedReader br = new BufferedReader(new InputStreamReader(dictionary.openStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    // process the line.
                    root.insertWord(line, 0);
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

    public TrieNode getNodeForPrefix(String s)
    {
        return root.getNodeForPrefix(s, 0);
    }

}
