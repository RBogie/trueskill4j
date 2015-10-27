package com.github.robbiedobbie.trueskill4j;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class TrueSkillRanking {
	public static final double DRAW_PROBABILITY = 0;

	@Getter
	@FieldDefaults(level = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class Rating {
		public static final double DEFAULT_MEAN = 25;
		public static final double DEFAULT_STANDARD_DEVIATION = 8.3;
		public static final double CONSERVATIVE_ESTIMATE_RATIO = 3;
		
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

		public double getTrueSkillEstimate() {
			return mean - CONSERVATIVE_ESTIMATE_RATIO * standardDeviation;
		}
	}

	private static Comparator<Rankable> rankableComparator = new Comparator<Rankable>() {
		public int compare(Rankable rankable, Rankable t1) {
			Rating r1 = rankable.getRating();
			Rating r2 = t1.getRating();

			if(rankable == t1) //Same object is always equal
				return 0;

			if(r1.getTrueSkillEstimate() > r2.getTrueSkillEstimate())
				return 1;
			else if(r1.getTrueSkillEstimate() < r2.getTrueSkillEstimate())
				return -1;
			return 0;
		}
	};

	Set<Rankable> players = new TreeSet<Rankable>(rankableComparator);

	public void addPlayer(Rankable player) {
		players.add(player);
	}

	public boolean managesPlayer(Rankable player) {
		return players.contains(player);
	}

	public void addMatchData(List<Tuple<Rankable, Integer>> matchInfo) {
		if(players.size() != 2) {
			throw new RuntimeException(new UnsupportedOperationException("Library (currently) only support 1v1 matches"));
		}

		Rankable winner = null;
		Rankable loser = null;
		int scoreBestPlayerFound = Integer.MAX_VALUE;
		//TODO: Hackish but will do actual sorting when doing final implementation with playerGraphs
		for(Tuple<Rankable, Integer> tuple : matchInfo) {
			if(tuple.getY() < scoreBestPlayerFound) {
				scoreBestPlayerFound = tuple.getY();
				winner = tuple.getX();
			} else {
				loser = tuple.getX();
			}
		}
	}

	private enum PlayStatus {
		Win, Loss, Draw;
	}

	private Rating calculateNewRatingForPlayer(Rankable player, Rankable opponent, PlayStatus playStatus) {

	}

}
