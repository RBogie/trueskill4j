package com.github.robbiedobbie.trueskill4j;

/**
 * A tuple which holds an object pair.
 *
 * @author Rob Bogie (bogie.rob@gmail.com)
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

    /**
     * Helper method to create a tuple without specifying generic types.
     * @return A newly created tuple containing the objects supplied.
     */
    public <X, Y> Tuple<X, Y> t(X x, Y y) {
        return new Tuple<X, Y>(x, y);
    }
}
