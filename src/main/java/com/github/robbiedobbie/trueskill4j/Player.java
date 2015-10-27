package com.github.robbiedobbie.trueskill4j;

import com.github.robbiedobbie.trueskill4j.TrueSkillRanking.Rating;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * Represents a player in the ranking.
 * 
 * @author Rob Bogie
 *
 * @param <T>
 *            The type of the incorporated data (Normally an id or a reference to some identifying class)
 */
@Data
@FieldDefaults(level = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Player<T> implements Rankable{
	/**
	 * The identifier or reference for this player.
	 */
	final T id;

	/**
	 * The current rating this player has
	 */
	Rating rating;
	
	/**
	 * Construct a player instance with the default mean and standard deviation as rating
	 * 
	 * @param id
	 *            The identifier or reference for this player.
	 */
	public Player(T id) {
		this(id, new Rating());
	}
}
