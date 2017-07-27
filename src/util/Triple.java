package util;

/**
 * A triple that holds generic data.
 *
 * Created by sujay on 7/27/17.
 */
public class Triple<T, U, V> {
    private T a;
    private U b;
    private V c;

    public Triple(T a, U b, V c) {
        this.a = a;
        this.b = b;
        this.c = c;
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
}
