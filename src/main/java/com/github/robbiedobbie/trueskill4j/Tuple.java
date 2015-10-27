package com.github.robbiedobbie.trueskill4j;

/**
 * Created by rob on 10/26/15.
 */
public class Tuple<X, Y> {
    protected final X x;
    protected final Y y;

    public Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    public X getX() {
        return x;
    }

    public Y getY() {
        return y;
    }

    public <X, Y> Tuple<X, Y> t(X x, Y y) {
        return new Tuple<X, Y>(x, y);
    }
}
