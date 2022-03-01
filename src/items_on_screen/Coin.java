package items_on_screen;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import main.BuggyRun;

public class Coin extends Circle implements ItemOnScreen {
	private int value;

	public Coin( int value, int row, int column, boolean isDarkMode ) {
		super( ( column + 0.5 ) * BuggyRun.gridCellWidth(), ( row + 0.5 ) * BuggyRun.gridCellWidth(),
				BuggyRun.gridCellWidth() / 2.0 );
		this.value = value;
		Paint[] lightModeColors = { Color.GREEN, Color.GOLDENROD.brighter(), Color.RED, Color.BLUE, Color.PURPLE };
		Paint[] darkModeColors = { Color.GREEN.brighter(), Color.GOLD.brighter(), Color.RED,
				new Color( 0.2, 0.2, 1, 1 ), Color.PURPLE.brighter() };
		if ( !isDarkMode )
			setFill( lightModeColors[value - 1] );
		else
			setFill( darkModeColors[value - 1] );
	}

	public int getValue() {
		return this.value;
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