package items_on_screen;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import main.BuggyRun;

public abstract class Bomb extends Circle implements ItemOnScreen {
	public Bomb( int row, int column ) {
		super( ( column + 0.5 ) * BuggyRun.gridCellWidth(), ( row + 0.5 ) * BuggyRun.gridCellWidth(),
				BuggyRun.gridCellWidth() / 2.0 );
	}

	public abstract void drop();

	public boolean isOutOfBounds( Rectangle screen ) {
		return ( getCenterX() > screen.getWidth() || getCenterY() > screen.getHeight() );
	}
}