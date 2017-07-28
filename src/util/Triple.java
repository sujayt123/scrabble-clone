package util;

/**
 * A generic class to hold a triple of data.
 *
 * @param <T> the type of the first element
 * @param <U> the type of the second element
 * @param <V> the type of the third element
 * Created by sujay on 7/27/17.
 */

public class Triple<T, U, V> {
    /**
     * First element
     */
    private T a;

    /**
     * Second element
     */
    private U b;

    /**
     * Third element
     */
    private V c;

    /**
     * Constructs a triple given the three elements of data.
     * @param a first elem
     * @param b second elem
     * @param c third elem
     */
    public Triple(T a, U b, V c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    /**
     * Returns the first element of this triple.
     * @return a
     */
    public T getA() {
        return a;
    }

    /**
     * Sets the first element of this triple.
     * @param a the first element
     */
    public void setA(T a) {
        this.a = a;
    }

    /**
     * Returns the second element of this triple.
     * @return b
     */
    public U getB() {
        return b;
    }

    /**
     * Sets the second element of this triple.
     * @param b the first element
     */
    public void setB(U b) {
        this.b = b;
    }

    /**
     * Returns the third element of this triple.
     * @return c
     */
    public V getC() {
        return c;
    }

    /**
     * Sets the third element of this triple.
     * @param c the first element
     */
    public void setC(V c) {
        this.c = c;
    }
}
