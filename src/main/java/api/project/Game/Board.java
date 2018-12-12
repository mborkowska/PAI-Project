package api.project.Game;

public class Board {
	public enum fieldType {
		BLANK, PLAYER, MONSTER, DIAMOND
	}

	private fieldType[][] fields;
	private int cols;
	private int rows;

	public Board(int cols, int rows) {
		this.cols = cols;
		this.rows = rows;
		fields = new fieldType[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				fields[i][j] = fieldType.BLANK;
			}
		}
	}

	public void setAt(int x, int y, fieldType type) {
		fields[x][y] = type;
	}

	public fieldType getAt(int x, int y) {
		return fields[x][y];
	}

	public String display(Coords coords) {
		String result = "";
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (i == coords.getX() && j == coords.getY()) {
					result += " | Y";
				} else {
					if (fields[i][j] == fieldType.PLAYER) {
						result += " | P";
					}
					if (fields[i][j] == fieldType.MONSTER) {
						result += " | M";
					}
					if (fields[i][j] == fieldType.BLANK) {
						result += " |   ";
					}
					if (fields[i][j] == fieldType.DIAMOND) {
						result += " | X";
					}
				}

			}
			if (i < rows - 1)
				result += " |\n";
		}

		return result;
	}
}
