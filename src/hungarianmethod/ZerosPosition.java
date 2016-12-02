package hungarianmethod;

/**
 * Класс определяющий позицию нуля.
 *
 * @author Alina Skorokhodova <alina.skorokhodova@vistar.su>
 */
public class ZerosPosition {

	private final int row;
	private final int column;

	public ZerosPosition(int row, int column) {
		this.row = row;
		this.column = column;
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

	@Override
	public String toString() {
		return "Zero[row=" + (row+1) +
				", column=" + (column+1) +
				"]";
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ZerosPosition)) {
			return false;
		}
		ZerosPosition pairo = (ZerosPosition) o;
		return this.row == pairo.getRow()
				|| this.column == pairo.getColumn();
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 19 * hash + this.row;
		hash = 19 * hash + this.column;
		return hash;
	}

}
