package additional_panes;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import main.BuggyRun;

public class LevelTracker extends Text {
	private int currentLevel;
	private int difficulty;
	private boolean isDarkMode;

	public LevelTracker( int currentLevel, int difficulty, boolean isDarkMode ) {
		this.currentLevel = currentLevel;
		this.difficulty = difficulty;
		this.isDarkMode = isDarkMode;
		updateDisplay();
		setFont( Font.font( "Calibri", FontWeight.BOLD, 3 * BuggyRun.gridCellWidth() ) );
	}

	private void updateDisplay() {
		String text = "Level " + currentLevel + " of " + BuggyRun.numLevels();
		if ( difficulty == 0 )
			text += " (easy)";
		else if ( difficulty == 1 )
			text += " (normal)";
		else if ( difficulty == 2 )
			text += " (hard)";

		setText( text );
		setFont( Font.font( "Calibri", FontWeight.BOLD, 3 * BuggyRun.gridCellWidth() ) );

		if ( isDarkMode )
			setFill( Color.WHITE );
	}

	public void levelUp() {
		if ( currentLevel < BuggyRun.numLevels() ) {
			++currentLevel;
			updateDisplay();
		}
		
	}

	public void gameOver() {
		String text = "You reached level " + currentLevel + " of " + BuggyRun.numLevels();
		if ( difficulty == 0 )
			text += " (easy).";
		else if ( difficulty == 1 )
			text += " (normal).";
		else if ( difficulty == 2 )
			text += " (hard).";

		setText( text );
		if ( isDarkMode )
			setFill( Color.WHITE );
	}

	public void setDarkMode( boolean isDarkMode ) {
		this.isDarkMode = isDarkMode;
		updateDisplay();
	}
}