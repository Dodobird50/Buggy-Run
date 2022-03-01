package items_on_screen;

import main.BuggyRun;

public class VerticalBomb extends Bomb {
	public VerticalBomb( int row, int column ) {
		super( row, column );
	}

	public void drop() {
		setCenterY( getCenterY() + BuggyRun.gridCellWidth() );
	}

	@Override
	public int row() {
		return ( (int) ( getCenterY() - BuggyRun.gridCellWidth() / 2.0 ) ) / BuggyRun.gridCellWidth();
	}

	@Override
	public int column() {
		return ( (int) ( getCenterX() - BuggyRun.gridCellWidth() / 2.0 ) ) / BuggyRun.gridCellWidth();
	}
}
