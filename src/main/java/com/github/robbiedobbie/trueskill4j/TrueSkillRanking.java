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

    private static Comparator<Rankable<?>> rankableComparator = new Comparator<Rankable<?>>() {
        public int compare(Rankable<?> rankable, Rankable<?> t1) {
            Rating r1 = rankable.getRating();
            Rating r2 = t1.getRating();

            if (rankable == t1) //Same object is always equal
                return 0;

            if (r1.getTrueSkillEstimate() > r2.getTrueSkillEstimate())
                return -1;
            else if (r1.getTrueSkillEstimate() < r2.getTrueSkillEstimate())
                return 1;
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

    public final double dynamicsFactor;

    TreeSet<Rankable<?>> players = new TreeSet<Rankable<?>>(rankableComparator);

    @Builder
    private TrueSkillRanking(double drawProbability, double beta, double conservativeEstimateRatio, double dynamicsFactor) {
        if (drawProbability < 0.0 || drawProbability > 1.0)
            throw new IllegalArgumentException("The drawProbability should always be between 0 and 1!");

        this.drawProbability = drawProbability;
        this.beta = beta;
        this.conservativeEstimateRatio = conservativeEstimateRatio;
        this.dynamicsFactor = dynamicsFactor;
    }

    public <T> void addPlayer(Rankable<T> player) {
        if(player.getRating() == null)
            player.setRating(new Rating());
        players.add(player);
    }

    public TreeSet<Rankable<?>> getPlayers() {
        return players;
    }

    public <T> boolean managesPlayer(Rankable<T> player) {
        return players.contains(player);
    }

    public <T> void addMatchData(List<Tuple<Rankable<T>, Integer>> matchInfo) {
        if (matchInfo.size() != 2) {
            throw new RuntimeException(
                    new UnsupportedOperationException("Library (currently) only support 1v1 matches"));
        }

        Rankable winner = null;
        Rankable loser = null;
        int scoreBestPlayerFound = Integer.MAX_VALUE;
        //TODO: Hackish but will do actual sorting when implementing draws.
        for (Tuple<Rankable<T>, Integer> tuple : matchInfo) {
            if (tuple.getY() < scoreBestPlayerFound) {
                scoreBestPlayerFound = tuple.getY();
                winner = tuple.getX();
            } else {
                loser = tuple.getX();
            }
        }

        //Remove them for the list, so that they will be added with new values, and sorted accordingly.
        players.remove(winner);
        players.remove(loser);

        //Calculate new ratings, but don't yet store them. It would influence the calculations for the other player.
        Rating winnerRating = calculateNewRatingForPlayer(winner, loser, PlayStatus.Win);
        Rating loserRating = calculateNewRatingForPlayer(loser, winner, PlayStatus.Loss);

        winner.setRating(winnerRating);
        loser.setRating(loserRating);

        players.add(winner);
        players.add(loser);
    }

    private enum PlayStatus {
        Win, Loss, Draw;
    }

    /**
     * Calculates the new skill for the player, given it's original rating, and the rating of the opponent before the
     * match. Uses the formula from section "How to update skills" on:
     * http://research.microsoft.com/en-us/projects/trueskill/details.aspx <br><br>
     * <p>
     * Translated for a universal user, the formulas become:
     * $$\mu_\textrm{player} = \mu_\textrm{player} + winloss \cdot \frac{\sigma_\textrm{player}^2}{c}\cdot v\left (
     * \frac{\mu_\textrm{winner}-\mu_\textrm{loser}}{c}, \frac{\varepsilon}{c} \right )$$<br>
     * and <br>
     * $$\sigma^2_\textrm{player} = \sigma^2_\textrm{player} \cdot\left ( 1 - \frac{\sigma^2_\textrm{player}}{c^2} \cdot
     * w\left (\frac{\mu_\textrm{winner}-\mu_\textrm{loser}}{c}, \frac{\varepsilon}{c} \right )\right )$$
     * <p>
     * Note that $$winloss$$ is 1, except when the player has lost, in which case it is -1.
     *
     * @param player     The player which we are calculating a new ranking
     * @param opponent   The opponent against the player was playing
     * @param playStatus Whether player won, lost or had a tie
     * @return The new rating for player.
     */
    public <T> Rating calculateNewRatingForPlayer(Rankable<T> player, Rankable<T> opponent, PlayStatus playStatus) {
        Rating winner = playStatus == PlayStatus.Loss ? opponent.getRating() : player.getRating();
        Rating loser = playStatus == PlayStatus.Loss ? player.getRating() : opponent.getRating();

        Rating playerRating = player.getRating();

        double drawMargin = TrueSkillMath.getDrawMargin(1, 1, beta, drawProbability);

        double c2 = 2 * Math.pow(beta, 2) +
                Math.pow(winner.getStandardDeviation(), 2) +
                Math.pow(loser.getStandardDeviation(), 2);
        double c = Math.sqrt(c2);

        double deltaMean = winner.getMean() - loser.getMean();

        int winloss = (playStatus == PlayStatus.Loss) ? -1 : 1;

        //Update mean
        double v = TrueSkillMath.functionV(deltaMean, drawMargin, c, (playStatus == PlayStatus.Draw));

        double variance = Math.pow(playerRating.getStandardDeviation(), 2);
        variance += Math.pow(dynamicsFactor, 2); //Add the dynamics factor.

        double newMean = (variance / c);
        newMean *= v;
        newMean *= winloss;
        newMean += playerRating.getMean();

        //Update Variance
        double newVariance = 1 - (variance/c2) *
                TrueSkillMath.functionW(deltaMean, drawMargin, c, (playStatus == PlayStatus.Draw));
        newVariance *= variance;

        double newStandardDeviation = Math.sqrt(newVariance);

        return new Rating(newMean, newStandardDeviation, conservativeEstimateRatio);
    }

}
