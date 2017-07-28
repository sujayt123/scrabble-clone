package main.util;

/**
 * A generic class to hold a quadruple of data.
 *
 * @param <T> the type of the first element
 * @param <U> the type of the second element
 * @param <V> the type of the third element
 * Created by sujay on 7/27/17.
 */

public class Quadruple<T, U, V, W> {
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
     * Fourth element
     */
    private W d;

    /**
     * Constructs a quadruple given the four elements of data.
     * @param a first elem
     * @param b second elem
     * @param c third elem
     * @param d fourth elem
     */
    public Quadruple(T a, U b, V c, W d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    /**
     * Returns the first element of this quadruple.
     * @return a
     */
    public T getA() {
        return a;
    }

    /**
     * Sets the first element of this quadruple.
     * @param a the first element
     */
    public void setA(T a) {
        this.a = a;
    }

    /**
     * Returns the second element of this quadruple.
     * @return b
     */
    public U getB() {
        return b;
    }

    /**
     * Sets the second element of this quadruple.
     * @param b the first element
     */
    public void setB(U b) {
        this.b = b;
    }

    /**
     * Returns the third element of this quadruple.
     * @return c
     */
    public V getC() {
        return c;
    }

    /**
     * Sets the third element of this quadruple.
     * @param c the first element
     */
    public void setC(V c) {
        this.c = c;
    }

    /**
     * Returns the fourth element of this quadruple
     * @return d
     */
    public W getD() {
        return d;
    }

    /**
     * Sets the fourth element of this quadruple.
     * @param d the fourth element
     */
    public void setD(W d) {
        this.d = d;
    }
}
