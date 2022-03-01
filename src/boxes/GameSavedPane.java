package boxes;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import main.BuggyRun;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

public class GameSavedPane extends Pane {
	private Rectangle background;
	private Button backToMainMenu;
	private Button closeGame;

	public GameSavedPane( Rectangle backgroundFromScreen ) {
		background = new Rectangle( 30 * BuggyRun.gridCellWidth(), 12 * BuggyRun.gridCellWidth() );
		background.setFill( Color.LIME );
		background.setStroke( Color.BLACK );
		background.setStrokeWidth( 0.6 * BuggyRun.gridCellWidth() );
		getChildren().add( background );
		setMaxWidth( 35 * BuggyRun.gridCellWidth() );
		setMaxHeight( 12 * BuggyRun.gridCellWidth() );
		setTranslateX( backgroundFromScreen.getWidth() / 2 - background.getWidth() / 2 );
		translateYProperty().bind( backgroundFromScreen.heightProperty().divide( 2 ).subtract( 6 * BuggyRun.gridCellWidth() ) );

		Text gameSaved = new Text( "Game successfully saved!" );
		gameSaved.setFont( Font.font( "Calibri", FontWeight.BOLD, 2.4 * BuggyRun.gridCellWidth() ) );
		gameSaved.setTranslateX( 1.5 * BuggyRun.gridCellWidth() );
		gameSaved.setTranslateY( 4 * BuggyRun.gridCellWidth() );
		getChildren().add( gameSaved );

		backToMainMenu = new Button( "Main menu" );
		backToMainMenu.setFont( Font.font( "Calibri", FontWeight.BOLD, 1.6 * BuggyRun.gridCellWidth() ) );
		backToMainMenu.setTranslateX( 1.5 * BuggyRun.gridCellWidth() );
		backToMainMenu.setTranslateY( 7 * BuggyRun.gridCellWidth() );
		getChildren().add( backToMainMenu );

		closeGame = new Button( "Close game" );
		closeGame.setFont( Font.font( "Calibri", FontWeight.BOLD, 1.6 * BuggyRun.gridCellWidth() ) );
		closeGame.setTranslateX( 17.5 * BuggyRun.gridCellWidth() );
		closeGame.setTranslateY( 7 * BuggyRun.gridCellWidth() );
		getChildren().add( closeGame );
	}

	public Button getBackToMainMenu() {
		return backToMainMenu;
	}

	public Button getCloseGame() {
		return closeGame;
	}
	
	public void setDarkMode( boolean isDarkMode ) {
		if ( isDarkMode ) {
			background.setFill( Color.LIME );
			background.setStroke( Color.BLACK );
		}
		else {
			background.setFill( Color.FORESTGREEN );
			background.setStroke( Color.WHITE );
		}
	}
}