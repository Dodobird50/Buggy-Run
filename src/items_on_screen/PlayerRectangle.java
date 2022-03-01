package items_on_screen;

import javafx.scene.shape.Rectangle;
import main.BuggyRun;

public class PlayerRectangle extends Rectangle implements ItemOnScreen {

	public PlayerRectangle( int row, int column ) {
		super( column * BuggyRun.gridCellWidth(), row * BuggyRun.gridCellWidth(), BuggyRun.gridCellWidth(),
				BuggyRun.gridCellWidth() );
	}

	@Override
	public int row() {
		return (int) getY() / BuggyRun.gridCellWidth();
	}

	@Override
	public int column() {
		return (int) getX() / BuggyRun.gridCellWidth();
	}

}