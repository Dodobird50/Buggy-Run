
package additional_panes;

import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import miscellaneous.LevelData;
import javafx.scene.text.Text;
import main.BuggyRun;

public class PointsTracker extends Text {
	private int currentLevel;
	private int difficulty;
	private int currentPoints;
	private int requiredPoints;

	private boolean isDarkMode;

	public PointsTracker( int currentLevel, int difficulty, boolean isDarkMode ) {
		this.currentLevel = currentLevel;
		this.difficulty = difficulty;
		this.isDarkMode = isDarkMode;

		requiredPoints = LevelData.getRequiredPointsToLevelUp( currentLevel, difficulty );
		setFont( Font.font( "Calibri", 2 * BuggyRun.gridCellWidth() ) );

		updateDisplay();
	}

	public void addPoints( int numberOfPoints ) {
		currentPoints += numberOfPoints;
		updateDisplay();
	}

	public void subtractPoints( int numberOfPoints ) {
		currentPoints -= numberOfPoints;
		if ( currentPoints < 0 )
			currentPoints = 0;
		updateDisplay();
	}

	public boolean hasEnoughPoints() {
		return currentPoints >= requiredPoints;
	}

	public void levelUp() {
		currentLevel++;
		currentPoints = 0;
		requiredPoints = LevelData.getRequiredPointsToLevelUp( currentLevel, difficulty );
		updateDisplay();
	}

	public int getCurrentPoints() {
		return currentPoints;
	}

	public void setCurrentPoints( int currentPoints ) {
		this.currentPoints = currentPoints;
		updateDisplay();
	}

	public int getRequiredPoints() {
		return requiredPoints;
	}
	
	public void setRequiredPoints( int requiredPoints ) {
		this.requiredPoints = requiredPoints;
		updateDisplay();
	}

	public double percentOfPointsGained() {
		return currentPoints / (double) requiredPoints * 100;
	}

	private void updateDisplay() {
		setText( "You have " + currentPoints + " / " + requiredPoints + " points" );

		if ( !isDarkMode ) {
			if ( currentPoints >= requiredPoints ) {
				setFill( Color.DARKGREEN );
				setFont( Font.font( "Calibri", FontWeight.EXTRA_BOLD, 2 * BuggyRun.gridCellWidth() ) );
			}
			else if ( currentPoints >= requiredPoints / 2 ) {
				setFill( Color.GOLDENROD );
				setFont( Font.font( "Calibri", FontWeight.NORMAL, 2 * BuggyRun.gridCellWidth() ) );
			}
			else {
				setFill( Color.RED );
				setFont( Font.font( "Calibri", FontWeight.NORMAL, 2 * BuggyRun.gridCellWidth() ) );
			}
		}
		else {
			if ( currentPoints >= requiredPoints ) {
				setFill( Color.LIME );
				setFont( Font.font( "Calibri", FontWeight.BLACK, 2 * BuggyRun.gridCellWidth() ) );
			}
			else if ( currentPoints >= requiredPoints / 2 ) {
				setFill( Color.YELLOW );
				setFont( Font.font( "Calibri", FontWeight.BOLD, 2 * BuggyRun.gridCellWidth() ) );
			}
			else {
				setFill( Color.ORANGERED );
				setFont( Font.font( "Calibri", FontWeight.BOLD, 2 * BuggyRun.gridCellWidth() ) );
			}
		}
	}

	public void setDarkMode( boolean isDarkMode ) {
		this.isDarkMode = isDarkMode;
		updateDisplay();
	}
}