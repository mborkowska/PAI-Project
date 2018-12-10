package api.project;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import api.project.Game.Board;
import api.project.Game.Monster;
import api.project.Game.Player;
import api.project.Game.Board.fieldType;

public class GameLogicTests {
	// @Test
	/*
	 * public void CharactersTest() { Board board = new Board(); Player player = new
	 * Player(); Monster monster = new Monster(); player.setRandomPosition();
	 * monster.setRandomPosition(); board.setAt(player.position.getX(),
	 * player.position.getY(), fieldType.PLAYER);
	 * board.setAt(monster.position.getX(), monster.position.getY(),
	 * fieldType.MONSTER); System.out.println(board.display());
	 * 
	 * assertEquals(20, player.life); assertEquals(20, player.weapon.getAmmo());
	 * player.shoot(); player.getDamage(); assertEquals(19, player.life);
	 * assertEquals(19, player.weapon.getAmmo()); }
	 */
	@Test
	public void CheckingMonstersTest() {
		int x = 0;
		int y = 0;

		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				if (i+x != x && j+y != y) {
					if (i + x <= 9 && i + x >= 0 && j + y <= 9 && j + y >= 0) {
						System.out.println((i + x) + ", " + (j + y));
					}
				}
			}
		}
	}
}
