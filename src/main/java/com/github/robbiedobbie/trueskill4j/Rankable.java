package com.github.robbiedobbie.trueskill4j;


public interface Rankable<T> extends Comparable<T>{
    public Rating getRating();

    public void setRating(Rating r);
}
