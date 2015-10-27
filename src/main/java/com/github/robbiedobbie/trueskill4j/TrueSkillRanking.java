package com.github.robbiedobbie.trueskill4j;

import lombok.Builder;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class is responsible for managing a ranking pool with the TrueSkill ranking algorithm.
 *
 * @author Rob Bogie (bogie.rob@gmail.com)
 */
public class TrueSkillRanking {

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

	public final double drawProbability;
	public final double beta;
	/**
	 * The amount of times you want to substract the standard deviation from the mean to get the TrueSkill estimate. In
	 * normal TrueSkill the mean would be 25, the standard deviation would be 25/3 and this ratio would be 3. The
	 * TrueSkill estimate would then be lower than the actual skill in 99% of the cases.
	 */
	public final double conservativeEstimateRatio;

	Set<Rankable> players = new TreeSet<Rankable>(rankableComparator);

	@Builder
	private TrueSkillRanking(double drawProbability, double beta, double conservativeEstimateRatio) {
		if(drawProbability < 0.0 || drawProbability > 1.0)
			throw new IllegalArgumentException("The drawProbability should always be between 0 and 1!");

		this.drawProbability = drawProbability;
		this.beta = beta;
		this.conservativeEstimateRatio = conservativeEstimateRatio;

	}

	public void addPlayer(Rankable player) {
		players.add(player);
	}

	public boolean managesPlayer(Rankable player) {
		return players.contains(player);
	}

	public void addMatchData(List<Tuple<Rankable, Integer>> matchInfo) {
		if(players.size() != 2) {
			throw new RuntimeException(
					new UnsupportedOperationException("Library (currently) only support 1v1 matches"));
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
		Rating winner = playStatus == PlayStatus.Loss ? opponent.getRating() : player.getRating();
		Rating loser = playStatus == PlayStatus.Loss ? player.getRating() : opponent.getRating();

		Rating playerRating = player.getRating();

		double drawMargin = TrueSkillMath.getDrawMargin(1, 1, beta, drawProbability);

		return new Rating(playerRating.getMean(), playerRating.getStandardDeviation(), conservativeEstimateRatio);
	}

}
