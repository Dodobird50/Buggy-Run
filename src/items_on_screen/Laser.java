package items_on_screen;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import main.BuggyRun;

public class Laser extends Pane implements ItemOnScreen {

	private IntegerProperty status;
	private Timeline timeline;
	private Rectangle laserBeam;
	private FadeTransition shootLaserBeam;

	private int rowNumber;

	public Laser( Player player ) {
		this( player.row(), 3, 0 );
	}

	public Laser( int rowNumber, int stageNumber, double jumpTo ) {
		if ( stageNumber < 0 )
			throw new IllegalArgumentException( "stageNumber must be greater than or equal to 0" );

		this.rowNumber = rowNumber;
		setTranslateY( ( rowNumber + 0.71 ) * BuggyRun.gridCellWidth() );
		status = new SimpleIntegerProperty();
		status.set( stageNumber );

		timeline = new Timeline( new KeyFrame( Duration.seconds( 1 ), e -> {
			getChildren().clear();

			status.set( status.get() - 1 );
			int num = status.get();

			if ( num > 0 ) {
				for ( int i = 0; i < BuggyRun.numColumns(); i++ ) {
					int xCoordinate = BuggyRun.gridCellWidth() * i;
					Text countdown = new Text( num + "" );
					countdown.setX( xCoordinate );
					countdown.setY( 0 );
					countdown.setFill( Color.RED );
					countdown.setWrappingWidth( BuggyRun.gridCellWidth() );
					countdown.setTextAlignment( TextAlignment.CENTER );
					countdown.setFont( Font.font( "Calibri", FontWeight.BLACK, BuggyRun.gridCellWidth() ) );

					getChildren().add( countdown );
				}
			}
			else if ( num == 0 ) {
				getChildren().add( laserBeam );
				shootLaserBeam.play();
			}
		} ) );
		// Make timeline play immediately
		timeline.jumpTo( Duration.seconds( jumpTo ) );
		timeline.setCycleCount( -1 );
		timeline.play();

		laserBeam = new Rectangle( 0, -0.71 * BuggyRun.gridCellWidth(),
				BuggyRun.numColumns() * BuggyRun.gridCellWidth(), BuggyRun.gridCellWidth() );
		laserBeam.setFill( Color.RED );
		laserBeam.setOpacity( 0 );

		shootLaserBeam = new FadeTransition();
		shootLaserBeam.setNode( laserBeam );
		shootLaserBeam.setFromValue( 1 );
		shootLaserBeam.setToValue( 0 );

		int num = status.get();
		if ( num > 0 ) {
			for ( int i = 0; i < BuggyRun.numColumns(); i++ ) {
				int xCoordinate = BuggyRun.gridCellWidth() * i;
				Text countdown = new Text( num + "" );
				countdown.setX( xCoordinate );
				countdown.setY( 0 );
				countdown.setFill( Color.RED );
				countdown.setWrappingWidth( BuggyRun.gridCellWidth() );
				countdown.setTextAlignment( TextAlignment.CENTER );
				countdown.setFont( Font.font( "Calibri", FontWeight.EXTRA_BOLD, BuggyRun.gridCellWidth() ) );

				getChildren().add( countdown );
			}
		}
		else {
			getChildren().add( laserBeam );
			shootLaserBeam.jumpTo( Duration.seconds( jumpTo ) );
			shootLaserBeam.play();
		}

	}

	@Override
	public int row() {
		return rowNumber;
	}

	@Override
	public int column() {
		return -1; // Laser does not have defined column
	}

	public IntegerProperty statusProperty() {
		return status;
	}

	public int getStatus() {
		return status.get();
	}

	public double getJumpTo() {
		return timeline.getCurrentTime().toSeconds();
	}
	
	public Rectangle getLaserBeam() {
		return laserBeam;
	}

	public void pause() {
		timeline.pause();
		if ( shootLaserBeam.getStatus() == Animation.Status.RUNNING )
			shootLaserBeam.pause();
	}

	public void resume() {
		timeline.play();
		if ( shootLaserBeam.getStatus() == Animation.Status.PAUSED )
			shootLaserBeam.play();
	}
	
	public boolean killsPlayer( Player player, boolean checkWholeBody ) {
		if ( player.isUnderInvincibilityEffect() || !player.isAlive() || status.get() != 0 )
			return false;

		for ( int i = 0; i < player.size(); i++ ) {
			if ( !checkWholeBody && i > 0 )
				break;

			int playerRectangleRow = (int) player.get( i ).getY() / BuggyRun.gridCellWidth();
			if ( rowNumber == playerRectangleRow )
				return true;
		}

		return false;
	}

}
