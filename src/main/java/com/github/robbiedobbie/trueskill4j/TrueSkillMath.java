package com.github.robbiedobbie.trueskill4j;

import org.apache.commons.math3.distribution.NormalDistribution;

public class TrueSkillMath {

    /**
     * NOrmal distribution that we use to calculate draw probability from a draw margin, and the other way around.
     *
     * Since no random sampling is done, we don't pass a random generator.
     */
    private final static NormalDistribution DRAW_DISTRIBUTION = new NormalDistribution(null, 0.0, 1.0);

    /**
     * Calculates the probability of a draw between two teams, given the amount of players in both teams.
     * The formula is taken from page 6 of the TrueSkill technical report (MSR-TR-2006-80).
     *
     * Formula: Draw Probability = 2\Phi \left ( \frac{\varepsilon }{\sqrt(n_1 + n_2)\beta } \right ) - 1
     *
     * @param n1 Number of players on team 1
     * @param n2 Number of players on team 2
     * @param beta The performance variance used. Normally should be (DEFAULT_MEAN)/6.
     * @param drawMargin The drawMargin for the game
     *
     * @return The probability of a draw occuring
     */
    public static double getDrawProbability(int n1, int n2, double beta, double drawMargin) {
        return 2.0 * DRAW_DISTRIBUTION.cumulativeProbability(drawMargin / (Math.sqrt(n1 + n2) * beta)) - 1;
    }

    /**
     * Calculates the draw margin for a game given the probability of a draw between two teams.
     * The formula is derived from the one on page 6 of the TrueSkill technical report (MSR-TR-2006-80).
     *
     * Original formula: Draw Probability = 2\Phi \left ( \frac{\varepsilon }{\sqrt(n_1 + n_2)\beta } \right ) - 1
     * Derived formula:  \varepsilon = \Phi^{-1} \left ( \frac{Draw Probability + 1}{2} \right ) \sqrt(n_1 + n_2)\beta
     *
     * @param n1 Number of players on team 1
     * @param n2 Number of players on team 2
     * @param beta The performance variance used. Normally should be (DEFAULT_MEAN)/6.
     * @param drawProbability The probability of a draw occuring
     *
     * @return The drawMargin for the game
     */
    public static double getDrawMargin(int n1, int n2, double beta, double drawProbability) {
        return DRAW_DISTRIBUTION.inverseCumulativeProbability((drawProbability + 1) / 2) * (Math.sqrt(n1 + n2) * beta);
    }
}
