package com.github.robbiedobbie.trueskill4j;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Rating {
	public static final double DEFAULT_MEAN = 25;
	public static final double DEFAULT_STANDARD_DEVIATION = DEFAULT_MEAN/3;
	public static final double DEFAULT_CONSERVATIVE_ESTIMATE_RATIO = 3;

	/**
	 * The mean value (μ) of the current rating of the player
	 */
	double mean;
	/**
	 * The standard deviation (σ) of the rating of the player
	 */
	double standardDeviation;

	/**
	 * The amount of times you want to substract the standard deviation from the mean to get the TrueSkill estimate. In
	 * normal TrueSkill the mean would be 25, the standard deviation would be 25/3 and this ratio would be 3. The
	 * TrueSkill estimate would then be lower than the actual skill in 99% of the cases.
	 */
	double conservativeEstimateRatio;

	public Rating() {
		this(DEFAULT_MEAN, DEFAULT_STANDARD_DEVIATION, DEFAULT_CONSERVATIVE_ESTIMATE_RATIO);
	}

	public double getTrueSkillEstimate() {
		return mean - (conservativeEstimateRatio * standardDeviation);
	}
}