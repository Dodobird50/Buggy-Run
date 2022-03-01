package items_on_screen;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import main.BuggyRun;

public class StatusEffectCoin extends Circle implements ItemOnScreen {
	public StatusEffectCoin( int row, int column, boolean isDarkMode ) {
		super( ( column + 0.5 ) * BuggyRun.gridCellWidth(), ( row + 0.5 ) * BuggyRun.gridCellWidth(),
				BuggyRun.gridCellWidth() / 2.0 );
		
		if ( isDarkMode )
			setFill( Color.GRAY );
		else
			setFill( Color.DARKGRAY );
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