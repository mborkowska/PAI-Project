package api.project.Game;

public class Board {
	public enum fieldType {
		BLANK, PLAYER, MONSTER, DIMOND
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
		fields[rows/2][cols/2] = fieldType.DIMOND;
	}

	public void setAt(int x, int y, fieldType type) {
		fields[x][y] = type;
	}

	public fieldType getAt(int x, int y) {
		return fields[x][y];
	}

	public String display() {
		String result = "";
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (fields[i][j] == fieldType.PLAYER) {
					result += " | P";
				}
				if (fields[i][j] == fieldType.MONSTER) {
					result += " | M";
				}
				if (fields[i][j] == fieldType.BLANK) {
					result += " |   ";
				}
				if (fields[i][j] == fieldType.DIMOND) {
					result += " | X";
				}

			}
			result += " |\n";
		}
		return result;
	}
}
