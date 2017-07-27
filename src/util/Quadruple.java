package util;

/**
 * Created by sujay on 7/27/17.
 */
public class Quadruple<T, U, V, W> {
    private T a;
    private U b;
    private V c;
    private W d;

    public Quadruple(T a, U b, V c, W d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public T getA() {
        return a;
    }

    public void setA(T a) {
        this.a = a;
    }

    public U getB() {
        return b;
    }

    public void setB(U b) {
        this.b = b;
    }

    public V getC() {
        return c;
    }

    public void setC(V c) {
        this.c = c;
    }

    public W getD() {
        return d;
    }

    public void setD(W d) {
        this.d = d;
    }
}
