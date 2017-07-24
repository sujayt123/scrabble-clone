package util;

import java.io.*;
import java.net.URL;

/**
 * Created by sujay on 7/21/17.
 */
public class Trie {

    public TrieNode root;

    public Trie() throws Exception
    {
        root = new TrieNode(false);

        URL dictionary = new URL("https://raw.githubusercontent.com/sujayt123/ScrabbleClone/master/src/util/dictionary.txt");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(dictionary.openStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                // process the line.
                root.insertWord(line, 0);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println("Read file into trie, " + TrieNode.numberInsertions + " insertions");
    }

    public TrieNode getNodeForPrefix(String s)
    {
        return root.getNodeForPrefix(s, 0);
    }

}
