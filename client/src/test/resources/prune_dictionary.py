"""
Sujay Tadwalkar

Fakes the scrabble dictionary so that the AITest can pass in a reasonable amount of time.

"""

import random

def main():
    # We must preserve all words that are assessed to be valid during the test.
    required_words = set()
    with open('requiredWords.txt', 'r') as f:
        for line in f:
            required_words.add(line.strip())

    with open('../../../../dictionary.txt', 'r') as f:
        with open('pruned.txt', 'w') as g:
            for line in f:
                if random.random() < 0.001:
                    g.write(line.rstrip() + '\n')
                    required_words.discard(line.strip())

            for word_to_add in required_words:
                g.write(word_to_add + '\n')



if __name__ == '__main__':
    main()