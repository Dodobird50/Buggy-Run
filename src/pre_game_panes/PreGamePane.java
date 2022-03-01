package pre_game_panes;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import main.BuggyRun;

public abstract class PreGamePane extends Pane {
	private Rectangle background;
	private static final Paint[] lightColors = { Color.GOLD, Color.LIGHTGREEN, Color.LIGHTSKYBLUE, Color.ORANGE,
			Color.LIGHTPINK, Color.LAVENDER, Color.LIGHTGRAY, Color.TURQUOISE };
	private static final Paint[] darkColors = { Color.DARKBLUE.darker().darker(), Color.BLACK, Color.PURPLE.darker(),
			Color.DARKRED.darker().darker(), Color.DARKGREEN.darker().darker(), Color.INDIGO.darker(),
			Color.BROWN.darker() };
	private BooleanProperty isDarkMode;

	public PreGamePane() {
		this( false );
	}

	public PreGamePane( boolean isDarkMode ) {
		int height = 35 * BuggyRun.gridCellWidth();

		background = new Rectangle( 0, 0, BuggyRun.gridCellWidth() * BuggyRun.numColumns(), height );
		int index = (int) ( Math.random() * lightColors.length );
		background.setFill( lightColors[index] );
		if ( !isDarkMode )
			background.setStroke( Color.BLACK );
		background.setStrokeWidth( 0.3 * BuggyRun.gridCellWidth() );
		getChildren().add( background );
		this.isDarkMode = new SimpleBooleanProperty( isDarkMode );

		background.setOnMouseClicked( e -> {
			toggleBackgroundColor();
		} );
	}

	public Rectangle getPaneBackground() {
		return background;
	}

	public boolean isDarkMode() {
		return isDarkMode.get();
	}

	public BooleanProperty isDarkModeProperty() {
		return isDarkMode;
	}

	public void setDarkMode( boolean isDarkMode ) {
		if ( this.isDarkMode.get() != isDarkMode ) {
			this.isDarkMode.set( isDarkMode );
			toggleBackgroundColor();
		}
	}

	public static Paint[] getLightColors() {
		return lightColors;
	}

	public static Paint[] getDarkColors() {
		return darkColors;
	}

	public void toggleBackgroundColor() {
		Paint newPaint;
		if ( !isDarkMode() ) {
			newPaint = lightColors[(int) ( Math.random() * lightColors.length )];
			while ( newPaint == getPaneBackground().getFill() )
				newPaint = lightColors[(int) ( Math.random() * lightColors.length )];
		}
		else {
			newPaint = darkColors[(int) ( Math.random() * darkColors.length )];
			while ( newPaint == background.getFill() )
				newPaint = darkColors[(int) ( Math.random() * darkColors.length )];
		}

		getPaneBackground().setFill( newPaint );
	}
}
