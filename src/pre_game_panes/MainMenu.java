package pre_game_panes;

import miscellaneous.Direction;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import main.BuggyRun;
import javafx.animation.KeyFrame;
import items_on_screen.Player;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.shape.Rectangle;

public class MainMenu extends PreGamePane {
	private Text title;

	private Button newGame;
	private Button continueGame;
	private Button deleteSave;

	private Button easyModeLeaderboard;
	private Button mediumModeLeaderboard;
	private Button hardModeLeaderboard;
	private Button resetLeaderboards;

	private Button howToPlay;
	private Button settings;
	private Button quit;

	private Player[] players;
	private Timeline animation;

	public MainMenu() {
		this( false );
	}

	public MainMenu( boolean isDarkMode ) {
		super( isDarkMode );

		title = new Text( "Buggy Run" );
		title.setFont( Font.font( "Calibri", FontWeight.BOLD, 7 * BuggyRun.gridCellWidth() ) );
		title.setWrappingWidth( BuggyRun.gridCellWidth() * BuggyRun.numColumns() );
		title.setTextAlignment( TextAlignment.CENTER );
		title.setTranslateY( 9.5 * BuggyRun.gridCellWidth() );
		getChildren().add( title );
		title.setOnMouseClicked( e -> {
			toggleBackgroundColor();
		} );
		players = new Player[4];

		// Aqua, almost white, yellow, lavender
		Color[] colors = { Color.rgb( 0, 225, 225 ), Color.rgb( 240, 240, 240 ), Color.YELLOW,
				Color.rgb( 218, 179, 255 ) };
		for ( int i = 0; i < 4; ++i ) {
			players[i] = new Player( colors[i] );
			int size = (int) ( Math.random() * 4 ) + 5;
			for ( int j = 1; j < size; j++ ) {
				players[i].grow();
			}
		}

		players[0].spawn( BuggyRun.gridCellWidth() * BuggyRun.numColumns() / 2 - BuggyRun.gridCellWidth(),
				getPaneBackground().getHeight() - 3 * BuggyRun.gridCellWidth() );
		players[1].spawn( BuggyRun.gridCellWidth() * BuggyRun.numColumns() / 2 - BuggyRun.gridCellWidth(),
				BuggyRun.gridCellWidth() * 2 );
		players[2].spawn( BuggyRun.gridCellWidth() * 4, BuggyRun.gridCellWidth() * 3 );
		players[3].spawn( BuggyRun.gridCellWidth() * ( BuggyRun.numColumns() - 5 ), BuggyRun.gridCellWidth() * 10 );

		newGame = new Button( "New game" );
		newGame.setFont( Font.font( "Calibri", FontWeight.EXTRA_BOLD, 1.6 * BuggyRun.gridCellWidth() ) );
		newGame.setFocusTraversable( false );

		howToPlay = new Button( "How to play" );
		howToPlay.setFont( Font.font( "Calibri", FontWeight.EXTRA_BOLD, 1.6 * BuggyRun.gridCellWidth() ) );
		howToPlay.setFocusTraversable( false );

		continueGame = new Button( "Continue game" );
		continueGame.setFont( Font.font( "Calibri", FontWeight.EXTRA_BOLD, 1.6 * BuggyRun.gridCellWidth() ) );
		continueGame.setFocusTraversable( false );

		easyModeLeaderboard = new Button( "Easy mode leaderboard" );
		easyModeLeaderboard.setFont( Font.font( "Calibri", FontWeight.EXTRA_BOLD, 1.6 * BuggyRun.gridCellWidth() ) );
		easyModeLeaderboard.setFocusTraversable( false );

		mediumModeLeaderboard = new Button( "Normal mode leaderboard" );
		mediumModeLeaderboard.setFont( Font.font( "Calibri", FontWeight.EXTRA_BOLD, 1.6 * BuggyRun.gridCellWidth() ) );
		mediumModeLeaderboard.setFocusTraversable( false );

		hardModeLeaderboard = new Button( "Hard mode leaderboard" );
		hardModeLeaderboard.setFont( Font.font( "Calibri", FontWeight.EXTRA_BOLD, 1.6 * BuggyRun.gridCellWidth() ) );
		hardModeLeaderboard.setFocusTraversable( false );

		deleteSave = new Button( "Delete saved game" );
		deleteSave.setFont( Font.font( "Calibri", FontWeight.EXTRA_BOLD, 1.6 * BuggyRun.gridCellWidth() ) );
		deleteSave.setFocusTraversable( false );

		resetLeaderboards = new Button( "Reset leaderboards" );
		resetLeaderboards.setFont( Font.font( "Calibri", FontWeight.EXTRA_BOLD, 1.6 * BuggyRun.gridCellWidth() ) );
		resetLeaderboards.setFocusTraversable( false );

		settings = new Button( "Settings" );
		settings.setFont( Font.font( "Calibri", FontWeight.EXTRA_BOLD, 1.6 * BuggyRun.gridCellWidth() ) );
		settings.setFocusTraversable( false );

		quit = new Button( "Quit game :(" );
		quit.setFont( Font.font( "Calibri", FontWeight.EXTRA_BOLD, 1.6 * BuggyRun.gridCellWidth() ) );
		quit.setFocusTraversable( false );

		GridPane gridPane = new GridPane();
		gridPane.setHgap( 8 * BuggyRun.gridCellWidth() );
		gridPane.setVgap( 2 * BuggyRun.gridCellWidth() );

		gridPane.add( newGame, 0, 0 );
		newGame.setMinWidth( 18.5 * BuggyRun.gridCellWidth() );
		newGame.setMaxWidth( 18.5 * BuggyRun.gridCellWidth() );

		gridPane.add( continueGame, 0, 1 );
		continueGame.setMinWidth( 18.5 * BuggyRun.gridCellWidth() );
		continueGame.setMaxWidth( 18.5 * BuggyRun.gridCellWidth() );

		gridPane.add( deleteSave, 0, 2 );
		deleteSave.setMinWidth( 18.5 * BuggyRun.gridCellWidth() );
		deleteSave.setMaxWidth( 18.5 * BuggyRun.gridCellWidth() );

		gridPane.add( easyModeLeaderboard, 1, 0 );
		easyModeLeaderboard.setMinWidth( 21 * BuggyRun.gridCellWidth() );
		easyModeLeaderboard.setMaxWidth( 21 * BuggyRun.gridCellWidth() );

		gridPane.add( mediumModeLeaderboard, 1, 1 );
		mediumModeLeaderboard.setMinWidth( 21 * BuggyRun.gridCellWidth() );
		mediumModeLeaderboard.setMaxWidth( 21 * BuggyRun.gridCellWidth() );

		gridPane.add( hardModeLeaderboard, 1, 2 );
		hardModeLeaderboard.setMinWidth( 21 * BuggyRun.gridCellWidth() );
		hardModeLeaderboard.setMaxWidth( 21 * BuggyRun.gridCellWidth() );

		gridPane.add( resetLeaderboards, 1, 3 );
		resetLeaderboards.setMinWidth( 21 * BuggyRun.gridCellWidth() );
		resetLeaderboards.setMaxWidth( 21 * BuggyRun.gridCellWidth() );

		gridPane.add( howToPlay, 2, 0 );
		howToPlay.setMinWidth( 18.5 * BuggyRun.gridCellWidth() );
		howToPlay.setMaxWidth( 18.5 * BuggyRun.gridCellWidth() );

		gridPane.add( settings, 2, 1 );
		settings.setMinWidth( 18.5 * BuggyRun.gridCellWidth() );
		settings.setMaxWidth( 18.5 * BuggyRun.gridCellWidth() );

		gridPane.add( quit, 2, 2 );
		quit.setMinWidth( 18.5 * BuggyRun.gridCellWidth() );
		quit.setMaxWidth( 18.5 * BuggyRun.gridCellWidth() );

		gridPane.setTranslateX( 3 * BuggyRun.gridCellWidth() );
		gridPane.setTranslateY( 13 * BuggyRun.gridCellWidth() );
		getChildren().add( gridPane );
		gridPane.setOnMouseClicked( e -> {
			toggleBackgroundColor();
		} );

		for ( Player player : players ) {
			for ( Rectangle r : player ) {
				getChildren().add( r );
			}
		}

		animation = new Timeline( new KeyFrame( Duration.millis( 150 ), e -> {
			players[0].move( Direction.randomDirection(), getPaneBackground(), this, players[1], players[2],
					players[3] );
			players[1].move( Direction.randomDirection(), getPaneBackground(), this, players[0], players[2],
					players[3] );
			players[2].move( Direction.randomDirection(), getPaneBackground(), this, players[0], players[1],
					players[3] );
			players[3].move( Direction.randomDirection(), getPaneBackground(), this, players[0], players[1],
					players[2] );
		} ) );
		animation.setCycleCount( Timeline.INDEFINITE );
		animation.play();
	}

	public Button getNewGame() {
		return newGame;
	}

	public Button getHowToPlay() {
		return howToPlay;
	}

	public Button getContinueGame() {
		return continueGame;
	}

	public void stopAnimation() {
		animation.stop();
	}

	public void resumeAnimation() {
		animation.play();
	}

	public Button getEasyModeLeaderboard() {
		return easyModeLeaderboard;
	}

	public Button getNormalModeLeaderboard() {
		return mediumModeLeaderboard;
	}

	public Button getHardModeLeaderboard() {
		return hardModeLeaderboard;
	}

	public Button getDeleteSave() {
		return deleteSave;
	}

	public Button getResetLeaderboards() {
		return resetLeaderboards;
	}

	public Button getSettings() {
		return settings;
	}

	public Button getQuit() {
		return quit;
	}

	@Override
	public void setDarkMode( boolean isDarkMode ) {
		super.setDarkMode( isDarkMode );
		if ( isDarkMode() )
			title.setFill( Color.WHITE );
		else
			title.setFill( Color.BLACK );
	}

}
