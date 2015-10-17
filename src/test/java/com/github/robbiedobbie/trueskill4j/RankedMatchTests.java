package com.github.robbiedobbie.trueskill4j;

import org.junit.Test;

public class RankedMatchTests{

	@Test
	public void testMatch1v1() {
		Player<Integer> player1 = new Player<Integer>(1);
		Player<Integer> player2 = new Player<Integer>(2);
		
		TrueSkillRanking ranking = new TrueSkillRanking();
		ranking.addPlayer(player1);
		ranking.addPlayer(player2);
	}

}
