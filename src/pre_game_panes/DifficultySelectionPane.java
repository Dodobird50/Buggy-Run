package pre_game_panes;

import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import main.BuggyRun;
//import post_game_things.Leaderboard;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;
import javafx.scene.control.Button;

public class DifficultySelectionPane extends PreGamePane {
	private Text selectDifficulty;
	private Text display;

	private Button easy;
	private Button normal;
	private Button hard;
	private Button backToMainMenu;
//	private boolean[] areLeaderboardsCorrupted;
//	private final String leaderboardIsCorruptedMessage = "Leaderboard data is corrupted for this difficulty. To "
//			+ "unlock it, you must reset the leaderboard for this difficulty.";

	public DifficultySelectionPane() {
		selectDifficulty = new Text( "Choose your fate:" );
		selectDifficulty.setFont( Font.font( "Calibri", FontWeight.BOLD, 3 * BuggyRun.gridCellWidth() ) );
		selectDifficulty.setWrappingWidth( BuggyRun.gridCellWidth() * BuggyRun.numColumns() );
		selectDifficulty.setTextAlignment( TextAlignment.CENTER );
		selectDifficulty.setY( 10 * BuggyRun.gridCellWidth() );
		getChildren().add( selectDifficulty );
		selectDifficulty.setOnMouseClicked( e -> {
			toggleBackgroundColor();
		} );

		HBox hBox = new HBox( 13 * BuggyRun.gridCellWidth() );
		hBox.setAlignment( Pos.CENTER );

		easy = new Button( "Easy" );
		easy.setFont( Font.font( "Calibri", FontWeight.BOLD, 2.4 * BuggyRun.gridCellWidth() ) );
		easy.setTextFill( Color.GREEN );
		easy.setFocusTraversable( false );
		easy.setMinWidth( 12 * BuggyRun.gridCellWidth() );
		easy.setMaxWidth( 12 * BuggyRun.gridCellWidth() );

		normal = new Button( "Normal" );
		normal.setFont( Font.font( "Calibri", FontWeight.BOLD, 2.4 * BuggyRun.gridCellWidth() ) );
		normal.setTextFill( Color.ORANGE );
		normal.setFocusTraversable( false );
		normal.setMinWidth( 12 * BuggyRun.gridCellWidth() );
		normal.setMaxWidth( 12 * BuggyRun.gridCellWidth() );

		hard = new Button( "Hard" );
		hard.setFont( Font.font( "Calibri", FontWeight.BLACK, 2.4 * BuggyRun.gridCellWidth() ) );
		hard.setTextFill( Color.RED );
		hard.setFocusTraversable( false );
		hard.setMinWidth( 12 * BuggyRun.gridCellWidth() );
		hard.setMaxWidth( 12 * BuggyRun.gridCellWidth() );

		hBox.getChildren().addAll( easy, normal, hard );
		hBox.setTranslateX( 9 * BuggyRun.gridCellWidth() );
		hBox.setTranslateY( 13 * BuggyRun.gridCellWidth() );
		getChildren().add( hBox );

		backToMainMenu = new Button( "Back to main menu" );
		backToMainMenu.setFont( Font.font( "Calibri", FontWeight.BOLD, 2.1 * BuggyRun.gridCellWidth() ) );
		backToMainMenu.setMinWidth( 20 * BuggyRun.gridCellWidth() );
		backToMainMenu.setMaxWidth( 20 * BuggyRun.gridCellWidth() );
		backToMainMenu.setTranslateX(
				BuggyRun.gridCellWidth() * BuggyRun.numColumns() / 2 - 10 * BuggyRun.gridCellWidth() );
		backToMainMenu.setTranslateY( 21.5 * BuggyRun.gridCellWidth() );
		getChildren().add( backToMainMenu );
		backToMainMenu.setFocusTraversable( false );

		display = new Text( "" );
		display.setFont( Font.font( "Calibri", FontWeight.BOLD, 2 * BuggyRun.gridCellWidth() ) );
		display.setX( 2 * BuggyRun.gridCellWidth() );
		display.setY( 29.5 * BuggyRun.gridCellWidth() );
		display.setWrappingWidth( 76 * BuggyRun.gridCellWidth() );
		display.setTextAlignment( TextAlignment.CENTER );
		getChildren().add( display );

//		updateLeaderboardsCorrupted();

		easy.setOnMouseEntered( e -> {
//			if ( !areLeaderboardsCorrupted[0] )
				display.setText( "First time playing this game or unsure of your abilities? There's always an easy "
						+ "mode to get started with!" );
//			else
//				display.setText( leaderboardIsCorruptedMessage );
		} );
		easy.setOnMouseExited( e -> {
			display.setText( "" );
		} );

		normal.setOnMouseEntered( e -> {
//			if ( !areLeaderboardsCorrupted[1] )
				display.setText(
						"A significant step up from easy mode. You might want to ensure that you have a good sense of "
								+ "maneuverability before attempting this difficulty." );
//			else
//				display.setText( leaderboardIsCorruptedMessage );
		} );
		normal.setOnMouseExited( e -> {
			display.setText( "" );
		} );

		hard.setOnMouseEntered( e -> {
//			if ( !areLeaderboardsCorrupted[2] )
				display.setText( "This difficulty will challenge you straight from level 1. Good luck!" );
//			else
//				display.setText( leaderboardIsCorruptedMessage );
		} );
		hard.setOnMouseExited( e -> {
			display.setText( "" );
		} );

		display.textProperty().addListener( ov -> {
//			if ( display.getText().startsWith( "Lea" ) )
//				display.setFill( Color.RED );
//			else {
				if ( isDarkMode() )
					display.setFill( Color.WHITE );
				else
					display.setFill( Color.BLACK );
//			}
		} );
	}

	public Button getEasy() {
		return easy;
	}

	public Button getNormal() {
		return normal;
	}

	public Button getHard() {
		return hard;
	}

	public Button getBackToMainMenu() {
		return backToMainMenu;
	}

//	public void updateLeaderboardsCorrupted() {
//		areLeaderboardsCorrupted = new boolean[] { Leaderboard.isLeaderboardFileCorrupted( 0 ),
//				Leaderboard.isLeaderboardFileCorrupted( 1 ), Leaderboard.isLeaderboardFileCorrupted( 2 ) };
//	}

	public void setDarkMode( boolean isDarkMode ) {
		super.setDarkMode( isDarkMode );
		if ( isDarkMode ) {
			selectDifficulty.setFill( Color.WHITE );
			display.setFill( Color.WHITE );
		}
		else {
			selectDifficulty.setFill( Color.BLACK );
			display.setFill( Color.BLACK );
		}
	}
}