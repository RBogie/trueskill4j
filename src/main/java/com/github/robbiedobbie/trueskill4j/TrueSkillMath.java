package com.github.robbiedobbie.trueskill4j;

import org.apache.commons.math3.distribution.NormalDistribution;

public class TrueSkillMath {

    /**
     * Normal distribution that we use to calculate draw probability from a draw margin, and the other way around.
     * It is the basis of our Cumulative Distribution Function.
     * <br>
     * Since no random sampling is done, we don't pass a random generator.
     */
    private final static NormalDistribution DRAW_DISTRIBUTION = new NormalDistribution(null, 0.0, 1.0);

    /**
     * Calculates the probability of a draw between two teams, given the amount of players in both teams.
     * The formula is taken from page 6 of the TrueSkill technical report (MSR-TR-2006-80).<br><br>
     * <p>
     * Formula: \(Draw Probability = 2\Phi \left ( \frac{\varepsilon }{\sqrt(n_1 + n_2)\beta } \right ) - 1\)
     *
     * @param n1         Number of players on team 1
     * @param n2         Number of players on team 2
     * @param beta       The performance variance used. Normally should be (DEFAULT_MEAN)/6.
     * @param drawMargin The drawMargin for the game
     * @return The probability of a draw occuring
     */
    public static double getDrawProbability(int n1, int n2, double beta, double drawMargin) {
        return 2.0 * DRAW_DISTRIBUTION.cumulativeProbability(drawMargin / (Math.sqrt(n1 + n2) * beta)) - 1;
    }

    /**
     * Calculates the draw margin for a game given the probability of a draw between two teams.
     * The formula is derived from the one on page 6 of the TrueSkill technical report (MSR-TR-2006-80).<br><br>
     * <p>
     * Original formula: \(Draw Probability = 2\Phi \left ( \frac{\varepsilon }{\sqrt(n_1 + n_2)\beta } \right ) - 1\)
     * <br>
     * Derived formula:  \(\varepsilon = \Phi^{-1} \left ( \frac{Draw Probability + 1}{2} \right ) \sqrt(n_1 + n_2)\beta\)
     *
     * @param n1              Number of players on team 1
     * @param n2              Number of players on team 2
     * @param beta            The performance variance used. Normally should be (DEFAULT_MEAN)/6.
     * @param drawProbability The probability of a draw occuring
     * @return The drawMargin for the game
     */
    public static double getDrawMargin(int n1, int n2, double beta, double drawProbability) {
        return DRAW_DISTRIBUTION.inverseCumulativeProbability((drawProbability + 1) / 2) * (Math.sqrt(n1 + n2) * beta);
    }

    /**
     * Helper method for {@link #functionV(double, double, boolean) functionV}
     *
     * @param c The amount deltaMean and drawMargin have to be divided by.
     */
    public static double functionV(double deltaMean, double drawMargin, double c, boolean draw) {
        return functionV(deltaMean / c, drawMargin / c, draw);
    }

    /**
     * The v function as described in the TrueSkill technical report (MSR-TR-2006-80) at page 4.
     * <br><br>
     * The formula when the game was not a draw:
     * $$\frac{\mathcal{N}(t - \varepsilon)}{\Phi(t - \varepsilon)}$$<br>
     * <p>
     * The formula when the game was a draw:
     * $$\frac{\mathcal{N}(- \varepsilon - t ) - \mathcal{N}(\varepsilon - t )}
     * {\Phi(\varepsilon - t) - \Phi(- \varepsilon - t)}$$
     *
     * @param deltaMean  The difference in mean between the winner and the loser. (μWinner - μloser)
     * @param drawMargin The draw margin for the game
     * @param draw       Whether the game was a draw
     * @return The additive and multiplicative correction term for the mean
     */
    public static double functionV(double deltaMean, double drawMargin, boolean draw) {
        if (draw) {
            double numerator = DRAW_DISTRIBUTION.density(-drawMargin - deltaMean) -
                    DRAW_DISTRIBUTION.density(drawMargin - deltaMean);

            double denominator = DRAW_DISTRIBUTION.cumulativeProbability(drawMargin - deltaMean) -
                    DRAW_DISTRIBUTION.cumulativeProbability(-drawMargin - deltaMean);

            return numerator / denominator;
        } else {
            return DRAW_DISTRIBUTION.density(deltaMean - drawMargin) /
                    DRAW_DISTRIBUTION.cumulativeProbability(deltaMean - drawMargin);
        }
    }

    /**
     * Helper method for {@link #functionW(double, double, boolean) functionV}
     *
     * @param c The amount deltaMean and drawMargin have to be divided by.
     */
    public static double functionW(double deltaMean, double drawMargin, double c, boolean draw) {
        return functionW(deltaMean / c, drawMargin / c, draw);
    }

    /**
     * The w function as described in the TrueSkill technical report (MSR-TR-2006-80) at page 4.
     * <br><br>
     * The formula when the game was not a draw:
     * $$V(t, \varepsilon) (V(t, \varepsilon) + t - \varepsilon)$$<br>
     * <p>
     * The formula when the game was a draw:
     * $$V^2(t, \varepsilon) + \frac{(\varepsilon - t )\mathcal{N}(\varepsilon - t ) + (\varepsilon + t )\mathcal{N}(\varepsilon + t )}
     * {\Phi(\varepsilon - t) - \Phi(- \varepsilon - t)}$$
     *
     * @param deltaMean  The difference in mean between the winner and the loser. (μWinner - μloser)
     * @param drawMargin The draw margin for the game
     * @param draw       Whether the game was a draw
     * @return The additive and multiplicative correction term for the variance
     */
    public static double functionW(double deltaMean, double drawMargin, boolean draw) {
        double vResult = functionV(deltaMean, drawMargin, draw);
        if (draw) {
            double numerator = ((drawMargin - deltaMean) * DRAW_DISTRIBUTION.density(drawMargin - deltaMean)) +
                    ((drawMargin + deltaMean) * DRAW_DISTRIBUTION.density(drawMargin + deltaMean));

            double denominator = DRAW_DISTRIBUTION.cumulativeProbability(drawMargin - deltaMean) -
                    DRAW_DISTRIBUTION.cumulativeProbability(-drawMargin - deltaMean);

            return vResult * vResult + numerator / denominator;
        } else {
            return vResult * (vResult + deltaMean - drawMargin);
        }
    }
}
