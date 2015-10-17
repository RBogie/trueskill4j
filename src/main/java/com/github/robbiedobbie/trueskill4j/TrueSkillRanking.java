package com.github.robbiedobbie.trueskill4j;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

public class TrueSkillRanking {
	@Getter
	@FieldDefaults(level = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class Rating {
		public static final double DEFAULT_MEAN = 25;
		public static final double DEFAULT_STANDARD_DEVIATION = 8.3;
		
		/**
		 * The mean value (μ) of the current rating of the player
		 */
		double mean;
		/**
		 * The standard deviation (σ) of the rating of the player
		 */
		double standardDeviation;
		
		public Rating() {
			this(DEFAULT_MEAN, DEFAULT_STANDARD_DEVIATION);
		}
	}
	
	
	
	public <T> void addPlayer(Player<T> player) {
		
	}

}
