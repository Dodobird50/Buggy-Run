package items_on_screen;

import main.BuggyRun;

public class HorizontalBomb extends Bomb {

	public HorizontalBomb( int row, int column ) {
		super( row, column );
	}

	public void drop() {
		setCenterX( getCenterX() + BuggyRun.gridCellWidth() );
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
