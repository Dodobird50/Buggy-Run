package items_on_screen;

import javafx.util.Duration;
import main.BuggyRun;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.animation.Timeline;
import javafx.scene.shape.Rectangle;

public class FinishLine extends Pane {
	private boolean[] areRectanglesUnlocked;
	private boolean isUnlocked;
	private int numberOfUnlockedRectangles;
	private Timeline shuffleUnlockedRectangles;

	public FinishLine( int numberOfUnlockedRectangles ) {
		areRectanglesUnlocked = new boolean[BuggyRun.numColumns()];
		isUnlocked = false;
		if ( numberOfUnlockedRectangles < 0 || numberOfUnlockedRectangles > BuggyRun.numColumns() )
			throw new IllegalArgumentException();
		this.numberOfUnlockedRectangles = numberOfUnlockedRectangles;
		setup();
	}

	public FinishLine( boolean[] areRectanglesUnlocked, double fastForwardSeconds, int numberOfUnlockedRectangles ) {
		this.areRectanglesUnlocked = areRectanglesUnlocked;
		for ( int x = 0; x < BuggyRun.numColumns(); ++x ) {
			int xCoordinate = x * BuggyRun.gridCellWidth();
			Rectangle rectangleToAdd = new Rectangle( xCoordinate, 0, BuggyRun.gridCellWidth(),
					BuggyRun.gridCellWidth() );
			getChildren().add( rectangleToAdd );
			if ( this.areRectanglesUnlocked[x] ) {
				rectangleToAdd.setFill( Color.GREEN );
				isUnlocked = true;
			}
			else
				rectangleToAdd.setFill( Color.RED );
		}

		this.numberOfUnlockedRectangles = numberOfUnlockedRectangles;
		shuffleUnlockedRectangles = new Timeline( new KeyFrame( Duration.seconds( 2.49 ), e -> {
			if ( isUnlocked )
				shuffle();
		} ) );
		shuffleUnlockedRectangles.setCycleCount( -1 );

		if ( isUnlocked ) {
			shuffleUnlockedRectangles.jumpTo( Duration.seconds( fastForwardSeconds ) );
			shuffleUnlockedRectangles.play();
		}

	}

	private void setup() {
		for ( int x = 0; x < BuggyRun.numColumns(); x++ ) {
			int xCoordinate = x * BuggyRun.gridCellWidth();
			Rectangle rectangleToAdd = new Rectangle( xCoordinate, 0, BuggyRun.gridCellWidth(),
					BuggyRun.gridCellWidth() );
			rectangleToAdd.setFill( Color.RED );
			getChildren().add( rectangleToAdd );
		}
		for ( int i = 0; i < areRectanglesUnlocked.length; ++i ) {
			areRectanglesUnlocked[i] = false;
		}

		shuffleUnlockedRectangles = new Timeline( new KeyFrame( Duration.seconds( 2.49 ), e -> {
			if ( isUnlocked )
				shuffle();
		} ) );
		shuffleUnlockedRectangles.setCycleCount( -1 );
	}

	private void shuffle() {
		boolean isAtLeastOneRectangleUnlocked = false;
		for ( int i = 0; i < getChildren().size(); i++ ) {
			if ( Math.random() < (double) numberOfUnlockedRectangles / BuggyRun.numColumns() ) {
				( (Rectangle) getChildren().get( i ) ).setFill( Color.GREEN );
				areRectanglesUnlocked[i] = true;
				isAtLeastOneRectangleUnlocked = true;
			}
			else {
				( (Rectangle) getChildren().get( i ) ).setFill( Color.RED );
				areRectanglesUnlocked[i] = false;
			}
		}
		
		if ( !isAtLeastOneRectangleUnlocked ) {
			// Unlock random rectangle
			int i = (int) ( Math.random() * BuggyRun.numColumns() );
			( (Rectangle) getChildren().get( i ) ).setFill( Color.GREEN );
			areRectanglesUnlocked[i] = true;
		}
	}

	public boolean playerEntersUnlockedRectangle( Player playerToCheck ) {
		boolean completedLevel = false;
		Rectangle head = playerToCheck.getHead();
		if ( head.getY() != 0 )
			return false;
		freeze();
		int col = (int) ( head.getX() / BuggyRun.gridCellWidth() );
		if ( areRectanglesUnlocked[col] && head.getY() == 0 )
			completedLevel = true;
		unfreeze();
		return completedLevel;
	}

	public void unlock() {
		if ( !isUnlocked ) {
			isUnlocked = true;
			shuffleUnlockedRectangles.jumpTo( Duration.seconds( 2.3 ) );
			shuffleUnlockedRectangles.play();
		}
	}

	public void lock() {
		if ( isUnlocked ) {
			isUnlocked = false;
			for ( int i = 0; i < getChildren().size(); ++i ) {
				( (Rectangle) getChildren().get( i ) ).setFill( Color.RED );
				areRectanglesUnlocked[i] = false;
			}
			shuffleUnlockedRectangles.stop();
		}
	}

	public void freeze() {
		shuffleUnlockedRectangles.pause();
	}

	public void unfreeze() {
		if ( isUnlocked )
			shuffleUnlockedRectangles.play();
	}

	public void setBaseNumberOfUnlockedRectangles( int baseNumberOfUnlockedRectangles ) {
		numberOfUnlockedRectangles = baseNumberOfUnlockedRectangles;
	}

	public boolean isUnlocked() {
		return isUnlocked;
	}

	public boolean isUnlockedAtColumn( int column ) {
		return areRectanglesUnlocked[column];
	}

	public boolean[] getAreRectanglesUnlocked() {
		return areRectanglesUnlocked;
	}

	public void setAreRectanglesUnlocked( boolean[] areRectanglesUnlocked ) {
		if ( this.areRectanglesUnlocked.length != areRectanglesUnlocked.length )
			throw new IllegalArgumentException( "areRectanglesUnlocked arrays must be of same length" );
		this.areRectanglesUnlocked = areRectanglesUnlocked;
		for ( int i = 0; i < getChildren().size(); ++i ) {
			if ( this.areRectanglesUnlocked[i] ) {
				( (Rectangle) getChildren().get( i ) ).setFill( Color.GREEN );
				unlock();
			}
			else
				( (Rectangle) getChildren().get( i ) ).setFill( Color.RED );
		}
	}

	public double getTimeInShuffle() {
		if ( shuffleUnlockedRectangles.getStatus() == Animation.Status.RUNNING
				|| shuffleUnlockedRectangles.getStatus() == Animation.Status.PAUSED )
			return shuffleUnlockedRectangles.getCurrentTime().toSeconds();

		return -1;
	}

	public void setTimeInShuffle( double seconds ) {
		shuffleUnlockedRectangles.jumpTo( Duration.seconds( seconds ) );
	}
}