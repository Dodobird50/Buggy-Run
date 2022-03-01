package additional_panes;

import javafx.scene.text.FontWeight;
import javafx.geometry.Pos;
import javafx.util.Duration;
import main.BuggyRun;
import javafx.animation.KeyFrame;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import miscellaneous.LevelData;
import javafx.animation.Timeline;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;

public class TimerPane extends StackPane {
	private double numberOfSecondsLeft;
	private double originalNumberOfSeconds;
	private Text displayTime;
	private Rectangle timeBar;
	private Timeline timeline;
	private static final double warningRatio = 0.5;
	private static final double criticalRatio = 0.2;
	private int currentLevel;
	private int difficulty;
	private String color;	// Used to keep track of color of the timer (red, yellow, green, or blue)
	private String fontWeight;	// Used to keep track of FontWeight of display

	// Constructor does not automatically start the timer
	// Height of TimerPane is 5 * BuggyRun.gridCellWidth()
	public TimerPane( int currentLevel, int difficulty ) {
		this.currentLevel = currentLevel;
		this.difficulty = difficulty;
		numberOfSecondsLeft = LevelData.getSecondsInLevel( currentLevel, difficulty );
		originalNumberOfSeconds = numberOfSecondsLeft;

		setMinWidth( 48 * BuggyRun.gridCellWidth() );
		setMaxWidth( 48 * BuggyRun.gridCellWidth() );
		setStyle( "-fx-border-color: black" );
		setBackground( new Background( new BackgroundFill( Color.rgb( 225, 255, 225 ), null, null ) ) );
		color = "green";
		fontWeight = "bold";

		displayTime = new Text();
		displayTime.setText( numberOfSecondsLeft + " seconds" );
		displayTime.setFont( Font.font( "Calibri", FontWeight.EXTRA_BOLD, 2.5 * BuggyRun.gridCellWidth() ) );
		displayTime.setFill( Color.DARKGREEN );
		timeBar = new Rectangle( 47.75 * BuggyRun.gridCellWidth(), 5 * BuggyRun.gridCellWidth() );
		timeBar.setFill( Color.GREEN.brighter() );

		timeline = new Timeline( new KeyFrame( Duration.seconds( 0.1 ), e -> {
			if ( numberOfSecondsLeft > 0 ) {
				numberOfSecondsLeft -= 0.1;
				numberOfSecondsLeft = Double.parseDouble( String.format( "%.1f", numberOfSecondsLeft ) );
				updateDisplay();
			}
			if ( numberOfSecondsLeft == 0 )
				timeline.stop();
		} ) );
		timeline.setCycleCount( -1 );

		setAlignment( Pos.CENTER );
		StackPane.setAlignment( timeBar, Pos.CENTER_LEFT );
		getChildren().addAll( timeBar, displayTime );
	}

	private synchronized void updateDisplay() {
		displayTime.setText( numberOfSecondsLeft + " seconds" );
		if ( numberOfSecondsLeft / originalNumberOfSeconds < 1 )
			timeBar.setWidth( numberOfSecondsLeft / originalNumberOfSeconds * 47.75 * BuggyRun.gridCellWidth() );
		else
			timeBar.setWidth( 47.75 * BuggyRun.gridCellWidth() );

		// Over 100% of time left
		if ( numberOfSecondsLeft / originalNumberOfSeconds > 1 ) {
			if ( !color.equals( "blue" ) ) {
				displayTime.setFill( Color.DARKBLUE );
				timeBar.setFill( Color.DEEPSKYBLUE );
				color = "blue";
			}
			// If fontWeight isn't already bold, make fontWeight bold
			if ( !fontWeight.equals( "bold" ) ) {
				displayTime.setFont( Font.font( "Calibri", FontWeight.EXTRA_BOLD, 2.5 * BuggyRun.gridCellWidth() ) );
				fontWeight = "bold";
			}
		}
		// Between 50% and 100% of time left
		else if ( numberOfSecondsLeft / originalNumberOfSeconds > warningRatio ) {
			if ( !color.equals( "green" ) ) {
				displayTime.setFill( Color.DARKGREEN );
				timeBar.setFill( Color.GREEN.brighter() );
				setBackground( new Background( new BackgroundFill( Color.rgb( 235, 255, 235 ), null, null ) ) );
				color = "green";
			}
			// If fontWeight isn't already bold, make fontWeight bold
			if ( !fontWeight.equals( "bold" ) ) {
				displayTime.setFont( Font.font( "Calibri", FontWeight.EXTRA_BOLD, 2.5 * BuggyRun.gridCellWidth() ) );
				fontWeight = "bold";
			}
		}
		// Between 20% and 50% of time left
		else if ( numberOfSecondsLeft / originalNumberOfSeconds > criticalRatio ) {
			if ( !color.equals( "yellow" ) ) {
				displayTime.setFill( Color.DARKGOLDENROD );
				timeBar.setFill( Color.GOLD );
				setBackground( new Background( new BackgroundFill( Color.rgb( 255, 255, 220 ), null, null ) ) );
				color = "yellow";
			}
			// If fontWeight isn't already bold, make fontWeight bold
			if ( !fontWeight.equals( "bold" ) ) {
				displayTime.setFont( Font.font( "Calibri", FontWeight.EXTRA_BOLD, 2.5 * BuggyRun.gridCellWidth() ) );
				fontWeight = "bold";
			}
		}
		// Under 20% of time left
		else {
			if ( !color.equals( "red" ) ) {
				displayTime.setFill( Color.DARKRED );
				setBackground( new Background( new BackgroundFill( Color.rgb( 255, 225, 225 ), null, null ) ) );
				color = "red";
			}

			int red = (int) ( 1.35 * percentOfTimeLeft() / criticalRatio ) + 120;
			timeBar.setFill( Color.rgb( red, 0, 0 ) );

			// If tenths place is 5, 6, 7, 8, or 9, or if time is up
			if ( numberOfSecondsLeft - (int) numberOfSecondsLeft > 0.5 || numberOfSecondsLeft == 0 ) {
				// If fontWeight isn't already black, make fontWeight black
				if ( !fontWeight.equals( "black" ) ) {
					displayTime.setFont( Font.font( "Calibri", FontWeight.BLACK, 2.5 * BuggyRun.gridCellWidth() ) );
					fontWeight = "black";
				}

			}
			// If tenths place is 0, 1, 2, 3, or 4
			else {
				// If fontWeight isn't already light, make fontWeight light
				if ( !fontWeight.equals( "light" ) ) {
					displayTime
							.setFont( Font.font( "Calibri", FontWeight.EXTRA_LIGHT, 2.5 * BuggyRun.gridCellWidth() ) );
					fontWeight = "light";
				}
			}
		}
	}

	// Play/resume the timer
	public void play() {
		timeline.play();
	}

	// Pause/stop the timer
	public void pause() {
		timeline.pause();
	}

	public double getOriginalNumberOfSeconds() {
		return originalNumberOfSeconds;
	}

	// Advace timer to next level. Does not start it though!
	public void levelUp() {
		// Stop the timer
		pause();

		// Update numberOfSecondsLeft and originalNumberOfSeconds
		currentLevel++;
		numberOfSecondsLeft = LevelData.getSecondsInLevel( currentLevel, difficulty );
		originalNumberOfSeconds = numberOfSecondsLeft;
		updateDisplay();
	}

	public double getNumberOfSecondsLeft() {
		return numberOfSecondsLeft;
	}

	public void setNumberOfSecondsLeft( double numberOfSecondsLeft ) {
		numberOfSecondsLeft = Double.parseDouble( String.format( "%.1f", numberOfSecondsLeft ) );
		this.numberOfSecondsLeft = numberOfSecondsLeft;
		updateDisplay();
	}

	public void addTime( double numberOfSeconds ) {
		numberOfSecondsLeft += numberOfSeconds;
		numberOfSecondsLeft = Double.parseDouble( String.format( "%.1f", numberOfSecondsLeft ) );
		updateDisplay();
	}

	// Speed up the timer
	public void fastMode() {
		timeline.setRate( 5.0 / 3 );
	}

	// Slow down the timer
	public void slowMode() {
		timeline.setRate( 0.4 );
	}

	public void normalMode() {
		timeline.setRate( 1 );
	}

	public double percentOfTimeLeft() {
		return numberOfSecondsLeft / originalNumberOfSeconds * 100;
	}

	public double percentOfTimePassed() {
		return 100 - percentOfTimeLeft();
	}
}