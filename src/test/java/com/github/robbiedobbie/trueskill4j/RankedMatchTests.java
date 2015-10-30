package com.github.robbiedobbie.trueskill4j;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RankedMatchTests{

	@Test
	public void testMatch1v1() {
		Player<Integer> player1 = new Player<Integer>(1);
		Player<Integer> player2 = new Player<Integer>(2);
		
		TrueSkillRanking ranking = TrueSkillRanking.builder()
				.drawProbability(0.1)
				.beta(25.0/6.0)
				.conservativeEstimateRatio(3.0)
				.dynamicsFactor(25.0/300.0)
				.build();

		ranking.addPlayer(player1);
		ranking.addPlayer(player2);

		System.out.println("Player 1: " + player1.getRating());
		System.out.println("Player 2: " + player2.getRating());

		ranking.addMatchData(Arrays.asList(new Tuple<Rankable, Integer>(player1, 1), new Tuple<Rankable, Integer>(player2, 2)));

		List<Rankable> rank = ranking.getRanking();
		for(Rankable rankEntry : rank) {
			Player player = (Player) rankEntry;
			System.out.println("Player " + player.getId() + ": " + rankEntry.getRating());
		}
	}

}
