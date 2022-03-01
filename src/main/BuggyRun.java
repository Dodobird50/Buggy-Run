package main;

import javafx.beans.InvalidationListener;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.shape.Circle;
import javafx.geometry.Insets;
import miscellaneous.*;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.geometry.Pos;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import items_on_screen.*;
import post_game_things.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import additional_panes.LevelTracker;
import additional_panes.PointsTracker;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.control.Button;
import additional_panes.TimerPane;
import boxes.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.Pane;
import pre_game_panes.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Application;

public class BuggyRun extends Application {
	private static final int numColumns = 80;
	private static final int numLevels = 16;
	private static final int invincibilityCost = 25;
	private static int gridCellWidth;

	private Stage primaryStage;
	private VBox base;
	private Timeline keepFocusOnBase;

	private MainMenu mainMenu;
	private InstructionsPane instructionsPane;
	private DifficultySelectionPane difficultySelectionPane;

	private int currentLevel;
	private int difficulty;

	private GameStatus gameStatus;
	private Pane screen;
	private Rectangle background;
	private Line safeLine;
	private FinishLine finishLine;
	private Text paused;
	private Player player;
	private PointsTracker pointsTracker;

	private ArrayList<VerticalBomb> verticalBombs;
	private VerticalBomb[][] verticalBombsGrid;

	private ArrayList<HorizontalBomb> horizontalBombs;
	private HorizontalBomb[][] horizontalBombsGrid;

	private ArrayList<Coin> coins;
	private Coin[][] coinsGrid;

	private ArrayList<ExtraLife> extraLives;
	private ExtraLife[][] extraLivesGrid;
	private HashSet<Integer> levelsWithExtraLives;

	private ArrayList<TimeBoost> timeBoosts;
	private TimeBoost[][] timeBoostsGrid;
	private ArrayList<StatusEffectCoin> statusEffectCoins;
	private StatusEffectCoin[][] statusEffectCoinsGrid;

	private ArrayList<Laser> lasers;
	private Laser[] lasersGrid;

	private HBox timeControl;
	private TimerPane timer;
	private Button pauseResume;

	private Timeline dropAndGenerateBombs;
	private Timeline generateLasers;

	private FadeTransition lifeLost;
	private Rectangle redRectangle;

	private FadeTransition lifeGained;
	private Rectangle greenRectangle;
	private FadeTransition timeBoosted;
	private Rectangle blueRectangle;
	private FadeTransition leveledUp;
	private ImageView levelUpArrow;
	private Timeline checkTimeLeft;
	private Timeline delayRespawn;
	private ArrayList<Long> startAndStopTimes;

	private Timeline movePlayerUp;
	private Timeline movePlayerRight;
	private Timeline movePlayerDown;
	private Timeline movePlayerLeft;

	private StatusEffectDisplay statusEffectDisplay;
	private static final StatusEffect[] negativeStatusEffects = new StatusEffect[] { StatusEffect.POISON,
			StatusEffect.BLINDNESS, StatusEffect.TIME_DILATION };
	private static final StatusEffect[] positiveStatusEffects = new StatusEffect[] { StatusEffect.DOUBLE_COINS,
			StatusEffect.DOUBLE_COINS, StatusEffect.TIME_CONTRACTION, StatusEffect.TIME_CONTRACTION,
			StatusEffect.INVINCIBILITY };
	private Timeline poisonTimeline;
	private Timeline timeDilationTimeline;
	private Timeline blindnessTimeline;
	private Timeline doubleCoinsTimeline;
	private Timeline timeContractionTimeline;
	private Timeline invincibilityTimeline;
	private int coinMultiplier;

	private HBox hBox;
	private HBox lifebar;
	private FadeTransition criticalLifebar;
	private LevelTracker levelTracker;

	private Leaderboard leaderboard;
	private Button addYourName;
	private Button backToMainMenu;
	private ConfirmDecisionBox confirmReturnToMainMenu;

	private File dataFolder;
	private NameInputBoxWithPassword addSavedGame;
	private GameSavedPane gameSavedPane;
	private Text fileIsCorrupted;
//	private final String corruptedLeaderboardMessage = "\nUnfortunately, the leaderboard data for this difficulty is "
//			+ "corrupted. Would you like to reset the leaderboard? Any previously stored data that cannot be accessed "
//			+ "anyway will be lost.";
	private final String corruptedSaveFileMessage = "\nUnfortunately, the save file is corrupted. Would you like to "
			+ "delete the file?";
	private LoginBox loadSavedGameLogin;
	private LoginBox deleteSavedGameLogin;
	private ConfirmDecisionBox confirmLoadSavedGame;
	private EraseLeaderboardDataBox eraseLeaderboardDataBox;
	private ConfirmDecisionBox confirmDeleteSaveOrEraseLeaderboards;

	private SettingsPane settingsPane;
	private Text seeMe1;
	private Text seeMe2;
	private Text seeMe3;
	private static double widthCalibration;
	private static double heightCalibration;

	public void start( Stage primaryStage ) {
		// Determine gridCellWidth
		DataInputStream in = null;
		gridCellWidth = 8;
		widthCalibration = 6;
		heightCalibration = 29;
		try {
			in = new DataInputStream( new BufferedInputStream(
					new FileInputStream( new File( "do-not-touch" + File.separator + "settings.dat" ) ) ) );

			in.readBoolean();
			gridCellWidth = in.readInt();
			widthCalibration = in.readDouble();
			heightCalibration = in.readDouble();
			in.close();
		}
		catch ( IOException ex ) {
			ex.printStackTrace();

			try {
				new File( "do-not-touch" + File.separator + "settings.dat" ).createNewFile();

				DataOutputStream out = new DataOutputStream( new BufferedOutputStream(
						new FileOutputStream( "do-not-touch" + File.separator + "settings.dat" ) ) );
				out.writeBoolean( false );
				out.writeInt( 8 );
				out.writeDouble( 6 );
				out.writeDouble( 29 );
				out.close();
			}
			catch ( IOException ex1 ) {
				ex1.printStackTrace();
			}
		}

		this.primaryStage = primaryStage;
		base = new VBox( 0.8 * gridCellWidth );
		base.setAlignment( Pos.TOP_CENTER );
		keepFocusOnBase = new Timeline( new KeyFrame( Duration.seconds( 0.1 ), e -> {
			if ( gameStatus == GameStatus.PAUSED && screen.getChildren().contains( addSavedGame ) )
				return;
			if ( gameStatus == GameStatus.NOT_RUNNING && ( mainMenu.getChildren().contains( loadSavedGameLogin )
					|| mainMenu.getChildren().contains( deleteSavedGameLogin ) ) )
				return;

			if ( base != null && !base.isFocused() )
				base.requestFocus();
		} ) );
		keepFocusOnBase.setCycleCount( Timeline.INDEFINITE );

		setupPreGamePanes();

		Scene scene = new Scene( base );
		this.primaryStage.setScene( scene );
		this.primaryStage.setTitle( "Buggy Run" );
		this.primaryStage.setWidth( numColumns * gridCellWidth + widthCalibration );
		this.primaryStage.setHeight( mainMenu.getPaneBackground().getHeight() + heightCalibration );
		this.primaryStage.setX( 550 );
		this.primaryStage.setY( 0 );
		this.primaryStage.setResizable( false );

		Image image = new Image( "file:do-not-touch" + File.separator + "icon.png" );
		this.primaryStage.getIcons().add( image );
		this.primaryStage.show();

		gameStatus = GameStatus.NOT_RUNNING;
		setupKeyInput();
	}

	private void setupPreGamePanes() {
		mainMenu = new MainMenu();
		base.getChildren().add( mainMenu );
		dataFolder = new File( "do-not-touch" + File.separator + "data" );

		// "New game" button loads difficultySelectionPane
		mainMenu.getNewGame().setOnAction( e -> {
			if ( !mainMenuContainsBox() ) {
				base.getChildren().remove( mainMenu );
//				difficultySelectionPane.updateLeaderboardsCorrupted();
				difficultySelection();
			}
		} );

		// "Instructions" button loads instructionsPane
		mainMenu.getHowToPlay().setOnAction( e -> {
			if ( !mainMenuContainsBox() ) {
				base.getChildren().remove( mainMenu );
				mainMenu.stopAnimation();
				base.getChildren().add( instructionsPane );
				instructionsPane.requestFocus();
			}
		} );

		fileIsCorrupted = new Text();
		fileIsCorrupted.setFont( Font.font( "Calibri", FontWeight.BOLD, 25 ) );
		fileIsCorrupted.setWrappingWidth( numColumns * gridCellWidth - 50 );
		fileIsCorrupted.setFill( Color.RED );
		fileIsCorrupted.setTextAlignment( TextAlignment.JUSTIFY );

		Button yes = new Button( "Yes" );
		yes.setFont( Font.font( "Calibri", FontWeight.BOLD, 25 ) );
		Button no = new Button( "No" );
		no.setFont( Font.font( "Calibri", FontWeight.BOLD, 25 ) );

		// continueGame button loads confirmLoadSavedGame if saved data
		// exists, corrupted or not, and if
		// mainMenu doesn't already contain any ConfirmPanes
		mainMenu.getContinueGame().setOnAction( e -> {
			// If there is saved data, corrupted or not
			if ( savedDataExists() ) {
				// Add confirmLoadSavedGame if confirmLoadSavedGame isn't
				// already in mainMenu, and mainMenu
				// doesn't have confirmDeleteOrResetLeaderboards
				if ( !mainMenuContainsBox() )
					mainMenu.getChildren().add( loadSavedGameLogin );
			}
		} );

		loadSavedGameLogin = new LoginBox( mainMenu.getPaneBackground() );
		loadSavedGameLogin.getNext().setOnAction( e -> {
			String name = loadSavedGameLogin.getName();
			String password = loadSavedGameLogin.getPassword();

			try {
				if ( !authentication( name, password ) ) {
					loadSavedGameLogin.setError( true );
					return;
				}
			}
			// File is corrupted
			catch ( Exception ex ) {
				base.getChildren().clear();
				base.getChildren().add( fileIsCorrupted );
				fileIsCorrupted.setText( corruptedSaveFileMessage );
				this.primaryStage.setHeight( 37 * BuggyRun.gridCellWidth() );

				HBox hBox = new HBox( 25 * gridCellWidth );
				hBox.setAlignment( Pos.CENTER );
				hBox.setPadding( new Insets( 2 * gridCellWidth, 0, 0, 0 ) );
				hBox.getChildren().addAll( yes, no );
				base.getChildren().add( hBox );

				yes.setOnAction( e1 -> {
					String nameToDelete = loadSavedGameLogin.getName();
					File file = new File( filePath( nameToDelete ) );
					file.delete();

					updateMainMenuContinueGameButton();
					loadMainMenu();
					loadSavedGameLogin.reset();
				} );
				no.setOnAction( e1 -> {
					loadMainMenu();
					loadSavedGameLogin.reset();
				} );
			}

			loadSavedGameLogin.setError( false );
			mainMenu.getChildren().remove( loadSavedGameLogin );
			mainMenu.getChildren().add( confirmLoadSavedGame );
			confirmLoadSavedGame.setMainMessage( "Load saved game, ###?", name );
		} );
		loadSavedGameLogin.getCancel().setOnAction( e -> {
			mainMenu.getChildren().remove( loadSavedGameLogin );
			loadSavedGameLogin.reset();
		} );

		confirmLoadSavedGame = new ConfirmDecisionBox( mainMenu.getPaneBackground(), 18 * gridCellWidth,
				"Load saved game?", null,
				"Once you start, the current save point will be deleted. In other words, you can't return to this "
						+ "save point if things go south down the road! Finally, don't forget to resave if you don't "
						+ "finish.",
				"I'm in!", "Not now" );
		confirmLoadSavedGame.getYes().setOnAction( e -> {
			// Try to load game
			try {
				loadGame( loadSavedGameLogin.getName() );
				// Delete file
				File file = new File( filePath( loadSavedGameLogin.getName() ) );
				file.delete();
			}
			// If file is corrupted
			catch ( Exception ex ) {
				base.getChildren().clear();
				base.getChildren().add( fileIsCorrupted );
				fileIsCorrupted.setText( corruptedSaveFileMessage );
				primaryStage.setHeight( 37 * BuggyRun.gridCellWidth() );

				HBox hBox = new HBox( 25 * gridCellWidth );
				hBox.setAlignment( Pos.CENTER );
				hBox.setPadding( new Insets( 2 * gridCellWidth, 0, 0, 0 ) );
				hBox.getChildren().addAll( yes, no );
				base.getChildren().add( hBox );

				yes.setOnAction( e1 -> {
					String nameToDelete = loadSavedGameLogin.getName();
					File file = new File( filePath( nameToDelete ) );
					file.delete();

					updateMainMenuContinueGameButton();
					loadMainMenu();
				} );
				no.setOnAction( e1 -> {
					loadMainMenu();
				} );
			}

			loadSavedGameLogin.reset();
		} );
		// Remove confirmLoadSavedGame off mainMenu
		confirmLoadSavedGame.getNo().setOnAction( e -> {
			mainMenu.getChildren().remove( confirmLoadSavedGame );
			loadSavedGameLogin.reset();
		} );

		mainMenu.getEasyModeLeaderboard().setOnAction( e -> {
			if ( mainMenuContainsBox() )
				return;

//			if ( Leaderboard.isLeaderboardFileCorrupted( 0 ) ) {
//				base.getChildren().clear();
//				base.getChildren().add( fileIsCorrupted );
//				fileIsCorrupted.setText( corruptedLeaderboardMessage );
//				this.primaryStage.setHeight( 37 * gridCellWidth );
//
//				HBox hBox = new HBox( 25 * gridCellWidth );
//				hBox.setAlignment( Pos.CENTER );
//				hBox.setPadding( new Insets( 2 * gridCellWidth, 0, 0, 0 ) );
//				hBox.getChildren().addAll( yes, no );
//				base.getChildren().add( hBox );
//
//				yes.setOnAction( e1 -> {
//					Leaderboard.resetLeaderboard( 0 );
//					updateMainMenuLeaderboardButtons();
//					loadMainMenu();
//				} );
//				no.setOnAction( e1 -> {
//					loadMainMenu();
//				} );
//
//				return;
//			}
			loadLeaderboard( 0 );
		} );
		mainMenu.getNormalModeLeaderboard().setOnAction( e -> {
			if ( mainMenuContainsBox() )
				return;

//			if ( Leaderboard.isLeaderboardFileCorrupted( 1 ) ) {
//				base.getChildren().clear();
//				base.getChildren().add( fileIsCorrupted );
//				fileIsCorrupted.setText( corruptedLeaderboardMessage );
//				this.primaryStage.setHeight( 37 * gridCellWidth );
//
//				HBox hBox = new HBox( 25 * gridCellWidth );
//				hBox.setAlignment( Pos.CENTER );
//				hBox.setPadding( new Insets( 2 * gridCellWidth, 0, 0, 0 ) );
//				hBox.getChildren().addAll( yes, no );
//				base.getChildren().add( hBox );
//
//				yes.setOnAction( e1 -> {
//					Leaderboard.resetLeaderboard( 1 );
//					updateMainMenuLeaderboardButtons();
//					loadMainMenu();
//				} );
//				no.setOnAction( e1 -> {
//					loadMainMenu();
//				} );
//
//				return;
//			}
			loadLeaderboard( 1 );
		} );

		mainMenu.getHardModeLeaderboard().setOnAction( e -> {
			if ( mainMenuContainsBox() )
				return;

//			if ( Leaderboard.isLeaderboardFileCorrupted( 2 ) ) {
//				base.getChildren().clear();
//				base.getChildren().add( fileIsCorrupted );
//				fileIsCorrupted.setText( corruptedLeaderboardMessage );
//				this.primaryStage.setHeight( 37 * gridCellWidth );
//
//				HBox hBox = new HBox( 25 * gridCellWidth );
//				hBox.setAlignment( Pos.CENTER );
//				hBox.setPadding( new Insets( 2 * gridCellWidth, 0, 0, 0 ) );
//				hBox.getChildren().addAll( yes, no );
//				base.getChildren().add( hBox );
//
//				yes.setOnAction( e1 -> {
//					Leaderboard.resetLeaderboard( 2 );
//					updateMainMenuLeaderboardButtons();
//					loadMainMenu();
//				} );
//				no.setOnAction( e1 -> {
//					loadMainMenu();
//				} );
//
//				return;
//			}
			loadLeaderboard( 2 );
		} );

		mainMenu.getQuit().setOnAction( e -> {
			System.exit( 0 );
		} );

		instructionsPane = new InstructionsPane();
		instructionsPane.getBack().setOnAction( e -> {
			if ( !instructionsPane.goBack() ) {
				base.getChildren().remove( instructionsPane );
				loadMainMenu();
			}
		} );
		instructionsPane.getNext().setOnAction( e -> {
			if ( !instructionsPane.goForward() ) {
				base.getChildren().remove( instructionsPane );
				difficultySelection();
			}
		} );

		confirmDeleteSaveOrEraseLeaderboards = new ConfirmDecisionBox( mainMenu.getPaneBackground(),
				12 * BuggyRun.gridCellWidth(), "", null, "You can't get this data back." );

		deleteSavedGameLogin = new LoginBox( mainMenu.getPaneBackground() );
		deleteSavedGameLogin.getNext().setOnAction( e -> {
			String name = deleteSavedGameLogin.getName();
			String password = deleteSavedGameLogin.getPassword();

			try {
				if ( !authentication( name, password ) ) {
					deleteSavedGameLogin.setError( true );
					return;
				}
			}
			catch ( Exception ex ) {
				base.getChildren().clear();
				base.getChildren().add( fileIsCorrupted );
				fileIsCorrupted.setText( corruptedSaveFileMessage );
				primaryStage.setHeight( 37 * gridCellWidth );

				HBox hBox = new HBox( 25 * gridCellWidth );
				hBox.setPadding( new Insets( 2 * gridCellWidth, 0, 0, 0 ) );
				hBox.setAlignment( Pos.CENTER );
				hBox.getChildren().addAll( yes, no );
				base.getChildren().add( hBox );

				yes.setOnAction( e1 -> {
					String nameToDelete = deleteSavedGameLogin.getName();
					File file = new File( filePath( nameToDelete ) );
					file.delete();

					updateMainMenuContinueGameButton();
					loadMainMenu();
					deleteSavedGameLogin.reset();
				} );
				no.setOnAction( e1 -> {
					loadMainMenu();
					deleteSavedGameLogin.reset();
				} );
				return;
			}

			deleteSavedGameLogin.setError( false );
			mainMenu.getChildren().remove( deleteSavedGameLogin );
			mainMenu.getChildren().add( confirmDeleteSaveOrEraseLeaderboards );
			confirmDeleteSaveOrEraseLeaderboards.setMainMessage( "Delete saved game, ###?", name );
		} );
		deleteSavedGameLogin.getCancel().setOnAction( e -> {
			mainMenu.getChildren().remove( deleteSavedGameLogin );
			deleteSavedGameLogin.reset();
		} );

		mainMenu.getDeleteSave().setOnAction( e -> {
			if ( !mainMenuContainsBox() )
				mainMenu.getChildren().add( deleteSavedGameLogin );
		} );

		mainMenu.getResetLeaderboards().setOnAction( e -> {
			if ( !mainMenuContainsBox() )
				mainMenu.getChildren().add( eraseLeaderboardDataBox );
		} );

		eraseLeaderboardDataBox = new EraseLeaderboardDataBox( mainMenu.getPaneBackground() );
		eraseLeaderboardDataBox.getNext().setOnAction( e -> {
			mainMenu.getChildren().remove( eraseLeaderboardDataBox );
			mainMenu.getChildren().add( confirmDeleteSaveOrEraseLeaderboards );
			confirmDeleteSaveOrEraseLeaderboards.setMainMessage( "Reset selected leaderboards?" );
		} );
		eraseLeaderboardDataBox.getCancel().setOnAction( e -> {
			mainMenu.getChildren().remove( eraseLeaderboardDataBox );
			eraseLeaderboardDataBox.reset();
		} );

		confirmDeleteSaveOrEraseLeaderboards.getYes().setOnAction( e -> {
			// Determine what caused confirmDeleteSaveOrResetLeaderboards to get added to
			// mainMenu through its
			// mainMessage
			if ( confirmDeleteSaveOrEraseLeaderboards.getMainMessage().indexOf( "Delete saved game" ) >= 0 ) {
				// If caused by deleteSavedGame, delete save data
				File file = new File( filePath( deleteSavedGameLogin.getName() ) );
				file.delete();

				updateMainMenuContinueGameButton();
				deleteSavedGameLogin.reset();
			}
			else if ( confirmDeleteSaveOrEraseLeaderboards.getMainMessage().equals( "Reset selected leaderboards?" ) ) {
				// If caused by resetLeaderboards, delete leaderboard data
				String selectedLeaderboards = eraseLeaderboardDataBox.getSelected();
				if ( selectedLeaderboards.indexOf( "easy" ) >= 0 )
					Leaderboard.resetLeaderboard( 0 );
				if ( selectedLeaderboards.indexOf( "normal" ) >= 0 )
					Leaderboard.resetLeaderboard( 1 );
				if ( selectedLeaderboards.indexOf( "hard" ) >= 0 )
					Leaderboard.resetLeaderboard( 2 );

//				updateMainMenuLeaderboardButtons();
				eraseLeaderboardDataBox.reset();
//				eraseLeaderboardDataBox.updateSelectability();
			}
			mainMenu.getChildren().remove( confirmDeleteSaveOrEraseLeaderboards );
		} );
		// If "No" is selected, remove confirmDeleteSaveOrResetLeaderboards from
		// mainMenu
		confirmDeleteSaveOrEraseLeaderboards.getNo().setOnAction( e -> {
			mainMenu.getChildren().remove( confirmDeleteSaveOrEraseLeaderboards );
			if ( confirmDeleteSaveOrEraseLeaderboards.getMainMessage().indexOf( "Delete saved game" ) >= 0 )
				deleteSavedGameLogin.reset();
			else if ( confirmDeleteSaveOrEraseLeaderboards.getMainMessage().equals( "Erase selected leaderboards?" ) )
				eraseLeaderboardDataBox.reset();

		} );

		difficultySelectionPane = new DifficultySelectionPane();

		settingsPane = new SettingsPane( primaryStage, this, gridCellWidth );
		mainMenu.setDarkMode( isDarkMode() );
		instructionsPane.setDarkMode( isDarkMode() );
		difficultySelectionPane.setDarkMode( isDarkMode() );
		settingsPane.setDarkMode( isDarkMode() );

		if ( isDarkMode() )
			base.setBackground( new Background( new BackgroundFill( Color.DARKBLUE.darker().darker(), null, null ) ) );
		else
			base.setBackground( new Background( new BackgroundFill( Color.gray( 0.95 ), null, null ) ) );
		settingsPane.isDarkModeProperty().addListener( ov -> {
			boolean isDarkMode = settingsPane.isDarkModeSelected();
			mainMenu.setDarkMode( isDarkMode );
			instructionsPane.setDarkMode( isDarkMode );
			difficultySelectionPane.setDarkMode( isDarkMode );
			settingsPane.setDarkMode( isDarkMode );

			if ( isDarkMode )
				base.setBackground(
						new Background( new BackgroundFill( Color.DARKBLUE.darker().darker(), null, null ) ) );
			else
				base.setBackground( new Background( new BackgroundFill( Color.gray( 0.95 ), null, null ) ) );

			if ( isDarkMode() ) {
				seeMe1.setFill( Color.WHITE );
				seeMe2.setFill( Color.WHITE );
				seeMe3.setFill( Color.WHITE );
			}
			else {
				seeMe1.setFill( Color.BLACK );
				seeMe2.setFill( Color.BLACK );
				seeMe3.setFill( Color.BLACK );
			}

		} );

		mainMenu.getSettings().setOnAction( e -> {
			if ( mainMenuContainsBox() )
				return;

			base.getChildren().clear();
			base.getChildren().add( settingsPane );
			keepFocusOnBase.play();
		} );

		seeMe1 = new Text( "If you can see this entire text, then current display size is okay for easy mode!" );
		seeMe1.setFont( Font.font( "Calibri", FontWeight.BOLD, 1.5 * gridCellWidth ) );
		seeMe1.setWrappingWidth( gridCellWidth * numColumns );
		seeMe1.setTextAlignment( TextAlignment.CENTER );

		seeMe2 = new Text( "If you can see this entire text, then current display size is okay for normal mode!" );
		seeMe2.setFont( Font.font( "Calibri", FontWeight.BOLD, 1.5 * gridCellWidth ) );
		seeMe2.setWrappingWidth( gridCellWidth * numColumns );
		seeMe2.setTextAlignment( TextAlignment.CENTER );

		seeMe3 = new Text( "If you can see this entire text, then current display size is okay for hard mode!" );
		seeMe3.setFont( Font.font( "Calibri", FontWeight.BOLD, 1.5 * gridCellWidth ) );
		seeMe3.setWrappingWidth( gridCellWidth * numColumns );
		seeMe3.setTextAlignment( TextAlignment.CENTER );

		if ( isDarkMode() ) {
			seeMe1.setFill( Color.WHITE );
			seeMe2.setFill( Color.WHITE );
			seeMe3.setFill( Color.WHITE );
		}
		else {
			seeMe1.setFill( Color.BLACK );
			seeMe2.setFill( Color.BLACK );
			seeMe3.setFill( Color.BLACK );
		}

		settingsPane.getTestAccommodation().setOnAction( e -> {
			if ( settingsPane.getTestAccommodation().getText()
					.equals( "Test accommodation for current display size" ) ) {
				primaryStage.setHeight(
						LevelData.getScreenHeight( numLevels, 2 ) + 10 * gridCellWidth + heightCalibration );
				settingsPane.getPaneBackground()
						.setHeight( LevelData.getScreenHeight( numLevels, 2 ) + 10 * gridCellWidth );
				settingsPane.getCalibrate().setDisable( true );
				settingsPane.getTestAccommodation().setText( "Stop testing accommodation for current display size" );
				settingsPane.getSlider().setDisable( true );
				settingsPane.getExit().setDisable( true );

				// Y coordinates are 0.4 * gridCellWidth less than getScreenHeight of last level of each
				// difficulty
				seeMe1.setY( LevelData.getScreenHeight( numLevels, 0 ) + 9.6 * gridCellWidth );
				seeMe2.setY( LevelData.getScreenHeight( numLevels, 1 ) + 9.6 * gridCellWidth );
				seeMe3.setY( LevelData.getScreenHeight( numLevels, 2 ) + 9.6 * gridCellWidth );

				settingsPane.getChildren().addAll( seeMe1, seeMe2, seeMe3 );
			}
			else {
				primaryStage.setHeight( mainMenu.getPaneBackground().getHeight() + heightCalibration );
				settingsPane.getPaneBackground().setHeight( mainMenu.getPaneBackground().getHeight() );
				settingsPane.getCalibrate().setDisable( false );
				settingsPane.getTestAccommodation().setDisable( false );
				settingsPane.getTestAccommodation().setText( "Test accommodation for current display size" );
				settingsPane.getSlider().setDisable( false );
				settingsPane.getExit().setDisable( false );

				settingsPane.getChildren().removeAll( seeMe1, seeMe2, seeMe3 );
			}
		} );
		settingsPane.getCalibrate().setOnAction( e -> {
			base.requestFocus();
			if ( settingsPane.getCalibrate().getText().equals( "Calibrate screen size" ) ) {
				settingsPane.getCalibrate().setText( "Stop calibrating screen size" );
				settingsPane.getSlider().setDisable( true );
				settingsPane.getTestAccommodation().setDisable( true );
				settingsPane.getExit().setDisable( true );
			}
			else {
				settingsPane.getCalibrate().setText( "Calibrate screen size" );
				settingsPane.getSlider().setDisable( false );
				settingsPane.getTestAccommodation().setDisable( false );
				settingsPane.getExit().setDisable( false );
			}
		} );

		settingsPane.getExit().setOnAction( e -> {
			settingsPane.getCalibrate().setText( "Calibrate screen size" );
			loadMainMenu();
			keepFocusOnBase.stop();
		} );

		// Bind fills of all PreGamePanes together
		mainMenu.getPaneBackground().fillProperty()
				.bindBidirectional( instructionsPane.getPaneBackground().fillProperty() );
		instructionsPane.getPaneBackground().fillProperty()
				.bindBidirectional( difficultySelectionPane.getPaneBackground().fillProperty() );
		difficultySelectionPane.getPaneBackground().fillProperty()
				.bindBidirectional( settingsPane.getPaneBackground().fillProperty() );

		updateMainMenuContinueGameButton();
//		updateMainMenuLeaderboardButtons();
	}

	private boolean mainMenuContainsBox() {
		return mainMenu.getChildren().contains( eraseLeaderboardDataBox )
				|| mainMenu.getChildren().contains( confirmDeleteSaveOrEraseLeaderboards )
				|| mainMenu.getChildren().contains( loadSavedGameLogin ) || mainMenu.getChildren().contains( deleteSavedGameLogin )
				|| mainMenu.getChildren().contains( confirmLoadSavedGame );
	}

	private void updateMainMenuContinueGameButton() {
		if ( !savedDataExists() ) {
			mainMenu.getContinueGame().setText( "No saved progress yet..." );
			mainMenu.getContinueGame().setDisable( true );
			mainMenu.getDeleteSave().setDisable( true );
		}
		else {
			mainMenu.getContinueGame().setText( "Continue game" );
			mainMenu.getContinueGame().setDisable( false );
			mainMenu.getDeleteSave().setDisable( false );
		}
	}

//	private void updateMainMenuLeaderboardButtons() {
//		boolean[] areLeaderboardsEmpty = new boolean[4];
//		Button[] leaderboardButtons = { mainMenu.getEasyModeLeaderboard(), mainMenu.getNormalModeLeaderboard(),
//				mainMenu.getHardModeLeaderboard() };
//		String[] difficulties = { "Easy", "Normal", "Hard" };
//		for ( int i = 0; i < difficulties.length; ++i )
//			areLeaderboardsEmpty[i] = Leaderboard.isLeaderboardFileEmpty( i );

//		for ( int i = 0; i < difficulties.length; i++ ) {
//			if ( areLeaderboardsEmpty[i] ) {
//				leaderboardButtons[i].setText( "No data yet..." );
//				leaderboardButtons[i].setDisable( true );
//			}
//			else {
//				leaderboardButtons[i].setText( difficulties[i] + " mode leaderboard" );
//				leaderboardButtons[i].setDisable( false );
//			}
//		}

//		if ( areLeaderboardsEmpty[0] && areLeaderboardsEmpty[1] && areLeaderboardsEmpty[2] )
//			mainMenu.getResetLeaderboards().setDisable( true );
//		else
//			mainMenu.getResetLeaderboards().setDisable( false );
//	}

	private boolean savedDataExists() {
		return dataFolder.listFiles().length > 0;
	}

	private void loadMainMenu() {
		// Stop all status effect timelines
		if ( poisonTimeline != null ) {
			poisonTimeline.stop();
			timeDilationTimeline.stop();
			blindnessTimeline.stop();
			doubleCoinsTimeline.stop();
			timeContractionTimeline.stop();
			invincibilityTimeline.stop();
			checkTimeLeft.stop();
			dropAndGenerateBombs.stop();
		}

		// Have only mainMenu on base
		base.getChildren().clear();
		base.getChildren().add( mainMenu );

		mainMenu.resumeAnimation();
		primaryStage.setHeight( mainMenu.getPaneBackground().getHeight() + heightCalibration );

		gameStatus = GameStatus.NOT_RUNNING;

		// Remove any boxes
		mainMenu.getChildren().removeAll( confirmLoadSavedGame, confirmDeleteSaveOrEraseLeaderboards, loadSavedGameLogin,
				deleteSavedGameLogin, eraseLeaderboardDataBox );

		updateMainMenuContinueGameButton();
//		updateMainMenuLeaderboardButtons();
		System.gc();

//		eraseLeaderboardDataBox.updateSelectability();

		base.requestFocus();
	}

	private void loadLeaderboard( int difficulty ) {
		mainMenu.stopAnimation();

		base.getChildren().clear();
		screen = new Pane();
		base.getChildren().add( screen );
		background = new Rectangle( 0, 0, numColumns * gridCellWidth, 70 * BuggyRun.gridCellWidth() );
		background.setFill( mainMenu.getPaneBackground().getFill() );
		if ( !isDarkMode() )
			background.setStroke( Color.BLACK );
		background.setStrokeWidth( 0.5 * gridCellWidth );
		screen.getChildren().add( background );

		leaderboard = new Leaderboard( difficulty, isDarkMode() );
		screen.getChildren().add( leaderboard );
		leaderboard.updateDisplay();

		// Add backToMainMenu button
		Button backToMainMenu = new Button( "Main menu" );
		backToMainMenu.setFont( Font.font( "Calibri", FontWeight.BOLD, 2 * BuggyRun.gridCellWidth() ) );
		backToMainMenu.setOnAction( e -> {
			loadMainMenu();
		} );
		backToMainMenu.setPrefWidth( 13 * gridCellWidth );
		backToMainMenu.setPrefHeight( 3 * gridCellWidth );

		backToMainMenu.setFocusTraversable( false );
		backToMainMenu.setTranslateX( gridCellWidth() * numColumns() - 16 * BuggyRun.gridCellWidth() );
		backToMainMenu.setTranslateY( background.getHeight() - 6 * BuggyRun.gridCellWidth() );
		screen.getChildren().add( backToMainMenu );
		primaryStage.setHeight( background.getHeight() + heightCalibration );
	}

	private void difficultySelection() {
		mainMenu.stopAnimation();

		base.getChildren().clear();
		base.getChildren().add( difficultySelectionPane );
		difficultySelectionPane.getEasy().setOnAction( e -> {
//			if ( !Leaderboard.isLeaderboardFileCorrupted( 0 ) )
			startNewGame( 0 );
		} );
		difficultySelectionPane.getNormal().setOnAction( e -> {
//			if ( !Leaderboard.isLeaderboardFileCorrupted( 1 ) )
			startNewGame( 1 );
		} );
		difficultySelectionPane.getHard().setOnAction( e -> {
//			if ( !Leaderboard.isLeaderboardFileCorrupted( 2 ) )
			startNewGame( 2 );
		} );
		difficultySelectionPane.getBackToMainMenu().setOnAction( e -> loadMainMenu() );
	}

	private void loadGame( String name ) throws Exception {
		mainMenu.stopAnimation();
		base.getChildren().clear();
		primaryStage.setY( 5 );

		DataInputStream in = null;
		try {
			in = new DataInputStream( new BufferedInputStream( new FileInputStream( new File( filePath( name ) ) ) ) );

			difficulty = in.readInt();
			coinMultiplier = in.readInt();
			currentLevel = in.readInt();
			startAndStopTimes = new ArrayList<Long>();
			int startAndStopTimesSize = in.readInt();
			for ( int i = 0; i < startAndStopTimesSize; i++ ) {
				startAndStopTimes.add( in.readLong() );
			}

			double secondsLeft = in.readDouble();
			int livesLeft = in.readInt();
			int currentPoints = in.readInt();

			setupScreen();
			setupTimeControl();
			levelTracker = new LevelTracker( currentLevel, difficulty, isDarkMode() );
			setupLifebar( livesLeft );
			hBox = new HBox( 14 * gridCellWidth );
			hBox.setMinWidth( gridCellWidth * numColumns );
			hBox.setAlignment( Pos.CENTER );
			hBox.getChildren().addAll( levelTracker, lifebar );
			base.getChildren().add( hBox );

			primaryStage.setHeight(
					LevelData.getScreenHeight( currentLevel, difficulty ) + 10 * gridCellWidth + heightCalibration );

			timer.setNumberOfSecondsLeft( secondsLeft );
			pointsTracker.setCurrentPoints( currentPoints );

			resetGrids();

			coins = new ArrayList<Coin>();
			int coinsSize = in.readInt();
			for ( int i = 0; i < coinsSize; i++ ) {
				int value = in.readInt();
				int row = in.readInt();
				int column = in.readInt();
				Coin coin = new Coin( value, row, column, isDarkMode() );
				addCoin( coin );
			}

			extraLives = new ArrayList<ExtraLife>();
			int extraLivesSize = in.readInt();
			for ( int i = 0; i < extraLivesSize; i++ ) {
				int x = in.readInt();
				int y = in.readInt();
				ExtraLife extraLife = new ExtraLife( x, y, isDarkMode() );
				addExtraLife( extraLife );
			}

			// Hashed password
			in.readUTF();

			levelsWithExtraLives = new HashSet<Integer>();
			int levelsWithExtraLivesSize = in.readInt();
			for ( int i = 0; i < levelsWithExtraLivesSize; i++ ) {
				int level = in.readInt();
				levelsWithExtraLives.add( level );
			}

			verticalBombs = new ArrayList<VerticalBomb>();
			int bombsSize = in.readInt();
			for ( int i = 0; i < bombsSize; i++ ) {
				int row = in.readInt();
				int column = in.readInt();
				VerticalBomb bomb = new VerticalBomb( row, column );
				addVerticalBomb( bomb );
			}

			horizontalBombs = new ArrayList<HorizontalBomb>();
			bombsSize = in.readInt();
			for ( int i = 0; i < bombsSize; i++ ) {
				int row = in.readInt();
				int column = in.readInt();
				HorizontalBomb bomb = new HorizontalBomb( row, column );
				addHorizontalBomb( bomb );
			}

			lasers = new ArrayList<Laser>();
			int lasersSize = in.readInt();
			for ( int i = 0; i < lasersSize; i++ ) {
				int row = in.readInt();
				int status = in.readInt();
				double jumpTo = in.readDouble();
				Laser laser = new Laser( row, status, jumpTo );
				addLaser( laser );
			}

			statusEffectCoins = new ArrayList<StatusEffectCoin>();
			int statusEffectCoinsSize = in.readInt();
			for ( int i = 0; i < statusEffectCoinsSize; i++ ) {
				int row = in.readInt();
				int column = in.readInt();
				StatusEffectCoin statusEffectCoin = new StatusEffectCoin( row, column, isDarkMode() );
				addStatusEffectCoin( statusEffectCoin );
			}

			timeBoosts = new ArrayList<TimeBoost>();
			int timeBoostsSize = in.readInt();
			for ( int i = 0; i < timeBoostsSize; i++ ) {
				int row = in.readInt();
				int column = in.readInt();
				TimeBoost timeBoost = new TimeBoost( row, column, isDarkMode() );
				addTimeBoost( timeBoost );
			}

			ArrayList<PlayerRectangle> rectangles = new ArrayList<PlayerRectangle>();
			int rectanglesSize = in.readInt();
			for ( int i = 0; i < rectanglesSize; i++ ) {
				int row = in.readInt();
				int column = in.readInt();
				PlayerRectangle playerRectangle = new PlayerRectangle( row, column );
				if ( i == 0 )
					playerRectangle.setFill( Color.DEEPSKYBLUE );
				else
					playerRectangle.setFill( playerColor() );
				playerRectangle.setStroke( Color.BLACK );
				playerRectangle.setStrokeWidth( 0.1 * gridCellWidth );
				rectangles.add( playerRectangle );
			}
			player = new Player( rectangles, playerColor() );
			player.setAlive( true );
			addPlayerToScreen();

			boolean[] areRectanglesUnlocked = new boolean[numColumns];
			boolean isUnlocked = false;
			for ( int i = 0; i < areRectanglesUnlocked.length; i++ ) {
				areRectanglesUnlocked[i] = in.readBoolean();
				if ( areRectanglesUnlocked[i] )
					isUnlocked = true;
			}
			double fastForwardSeconds = 0;
			if ( isUnlocked ) {
				fastForwardSeconds = in.readDouble();
				// Reset finishLine, since setupScreen() added the default locked one
				screen.getChildren().remove( finishLine );
				int numberOfUnlockedRectangles = LevelData.getNumberOfUnlockedRectangles( currentLevel, difficulty );
				finishLine = new FinishLine( areRectanglesUnlocked, fastForwardSeconds, numberOfUnlockedRectangles );
				screen.getChildren().add( finishLine );
			}

			if ( movePlayerUp != null ) {
				movePlayerUp.stop();
				movePlayerLeft.stop();
				movePlayerRight.stop();
				movePlayerDown.stop();
			}

			setupAnimations();
			StatusEffect[] statusEffects = { StatusEffect.POISON, StatusEffect.TIME_DILATION, StatusEffect.BLINDNESS,
					StatusEffect.DOUBLE_COINS, StatusEffect.TIME_CONTRACTION, StatusEffect.INVINCIBILITY };
			for ( int i = 0; i < 6; i++ ) {
				if ( in.readBoolean() ) {
					fastForwardSeconds = in.readDouble();
					loadStatusEffect( statusEffects[i], fastForwardSeconds );
				}
			}
		}
		catch ( Exception e ) {
//			e.printStackTrace();
			// File is corrupted
			if ( in != null )
				in.close();

			throw new Exception();
		}

		in.close();

		// Point player's location
		int xCoordinate = (int) ( player.getHead().getX() ) + gridCellWidth / 2;
		int yCoordinate = (int) ( player.getHead().getY() ) + gridCellWidth / 2;
		Circle green = new Circle( xCoordinate, yCoordinate, 5 * gridCellWidth );
		green.setFill( Color.LIME );
		screen.getChildren().add( green );

		FadeTransition spawn = new FadeTransition();
		spawn.setNode( green );
		spawn.setFromValue( 0.7 );
		spawn.setToValue( 0 );
		spawn.setDuration( Duration.seconds( 1 ) );
		spawn.play();
		spawn.setOnFinished( e -> {
			screen.getChildren().remove( green );
		} );

		gameStatus = GameStatus.RUNNING;
		startAndStopTimes.add( System.currentTimeMillis() );
		timer.play();
		base.requestFocus();
	}

	private String filePath( String name ) {
		return dataFolder.getAbsolutePath() + File.separator + name + ".dat";
	}

	private boolean authentication( String name, String password ) throws Exception {
		File file = new File( filePath( name ) );
		if ( !file.exists() )
			return false;

		DataInputStream in = null;
		try {
			in = new DataInputStream( new BufferedInputStream( new FileInputStream( file ) ) );

			// Skip through data before password
			in.readInt();
			in.readInt();
			in.readInt();
			int size = in.readInt();
			for ( int i = 0; i < size; i++ ) {
				in.readDouble();
			}

			in.readDouble();
			in.readInt();
			in.readInt();

			size = in.readInt();
			for ( int i = 0; i < size; i++ )
				for ( int j = 0; j < 3; j++ )
					in.readInt();

			size = in.readInt();
			for ( int i = 0; i < size; i++ ) {
				in.readInt();
				in.readInt();
			}

			String hashCodeFromFile = in.readUTF();
			in.close();

			for ( int pepper = 0; pepper < 100; pepper++ ) {
				String hashedPassword = password + pepper;
				for ( int i = 0; i <= 50; i++ ) {
					hashedPassword = hashPassword( hashedPassword );
					if ( i >= 20 && hashedPassword.equals( hashCodeFromFile ) ) {
						in.close();
						return true;
					}
				}
			}
			return false;
		}
		catch ( Exception e ) {
			// File is corrupted
			if ( in != null )
				in.close();

			throw new Exception();
		}
	}

	private void startNewGame( int difficulty ) {
		base.getChildren().clear();
		this.currentLevel = 1;
		this.difficulty = difficulty;
		coinMultiplier = 1;

		setupDisplay();

		verticalBombs = new ArrayList<VerticalBomb>();
		horizontalBombs = new ArrayList<HorizontalBomb>();
		coins = new ArrayList<Coin>();
		extraLives = new ArrayList<ExtraLife>();
		levelsWithExtraLives = new HashSet<>();
		// Ensure one extra life is in the first 7 levels
		levelsWithExtraLives.add( (int) ( Math.random() * 7 ) + 1 );
		while ( levelsWithExtraLives.size() < LevelData.getNumberOfExtraLives( difficulty ) )
			levelsWithExtraLives.add( (int) ( Math.random() * 12 ) + 5 );
		timeBoosts = new ArrayList<TimeBoost>();
		statusEffectCoins = new ArrayList<StatusEffectCoin>();
		lasers = new ArrayList<Laser>();

		int numRows = LevelData.numRows( currentLevel, difficulty );

		// Grids might remain from a previous game
		resetGrids();

		generateCoins( numRows );
		generateStatusEffectCoins( numRows );
		generateExtraLives( numRows );
		generateTimeBoosts( numRows );

		player = new Player( playerColor() );
		for ( int i = 1; i < currentLevel; i++ ) {
			if ( LevelData.isPlayerGrowsUponBeatingLevel( i, difficulty ) )
				player.grow();
		}

		player.spawn( gridCellWidth * numColumns / 2 - gridCellWidth,
				LevelData.getScreenHeight( currentLevel, difficulty ) - gridCellWidth );
		addPlayerToScreen();
		player.setAlive( true );

		if ( movePlayerUp != null ) {
			movePlayerUp.stop();
			movePlayerLeft.stop();
			movePlayerRight.stop();
			movePlayerDown.stop();
		}

		setupAnimations();
		gameStatus = GameStatus.RUNNING;
		startAndStopTimes = new ArrayList<Long>();
		startAndStopTimes.add( System.currentTimeMillis() );

		base.requestFocus();
		// Play timer, as timer's constructor doesn't make it automatically play
		timer.play();
	}

	private void setupDisplay() {
		setupScreen();
		setupTimeControl();
		setupLifebar( 3 );
		levelTracker = new LevelTracker( currentLevel, difficulty, isDarkMode() );
		hBox = new HBox( 14 * gridCellWidth );
		hBox.setAlignment( Pos.CENTER );
		hBox.getChildren().addAll( levelTracker, lifebar );
		base.getChildren().add( hBox );

		primaryStage.setHeight(
				LevelData.getScreenHeight( currentLevel, difficulty ) + 10 * gridCellWidth + heightCalibration );
	}

	// setupScreen setups things on the screen excluding player, coins, bombs,
	// etc...
	private void setupScreen() {
		screen = new Pane();
		base.getChildren().add( screen );
		background = new Rectangle( 0, 0, numColumns * gridCellWidth,
				LevelData.getScreenHeight( currentLevel, difficulty ) );
		screen.minHeightProperty().bind( background.heightProperty() );
		screen.maxHeightProperty().bind( background.heightProperty() );
		if ( settingsPane.isDarkMode() )
			background.setStroke( Color.WHITE );
		else
			background.setStroke( Color.BLACK );
		background.setStrokeWidth( 0.2 * gridCellWidth );
		background.setFill( Color.TRANSPARENT );
		screen.getChildren().add( background );

		pointsTracker = new PointsTracker( currentLevel, difficulty, isDarkMode() );
		pointsTracker.setX( gridCellWidth * numColumns - 31 * BuggyRun.gridCellWidth );
		pointsTracker.setWrappingWidth( 30 * BuggyRun.gridCellWidth );
		pointsTracker.setTextAlignment( TextAlignment.RIGHT );
		pointsTracker.yProperty().bind( background.heightProperty().subtract( gridCellWidth ) );
		screen.getChildren().add( pointsTracker );

		finishLine = new FinishLine( LevelData.getNumberOfUnlockedRectangles( currentLevel, difficulty ) );
		screen.getChildren().add( finishLine );
		safeLine = new Line( 0, gridCellWidth * 3, gridCellWidth * numColumns, gridCellWidth * 3 );
		safeLine.setStrokeWidth( 0.3 * gridCellWidth );
		if ( isDarkMode() )
			safeLine.setStroke( Color.LIME );
		else
			safeLine.setStroke( Color.DARKGREEN );
		if ( LevelData.hasSafeArea( currentLevel, difficulty ) )
			screen.getChildren().add( safeLine );

		statusEffectDisplay = new StatusEffectDisplay();
		statusEffectDisplay.setTranslateX( 1.5 * gridCellWidth );
		statusEffectDisplay.translateYProperty().bind( background.heightProperty().subtract( 7 * gridCellWidth ) );
		screen.getChildren().add( statusEffectDisplay );

		addSavedGame = new NameInputBoxWithPassword( background );

		addSavedGame.getOk().setOnAction( e -> {
			// Check if name or password is blank
			if ( addSavedGame.getName().length() == 0 || addSavedGame.getPassword().length() == 0 ) {
				addSavedGame.error( "Please enter a name and password" );
				return;
			}

			// Check if name already exists
			for ( File file : dataFolder.listFiles() ) {
				String filePath = file.getPath();
				if ( filePath.indexOf( addSavedGame.getName() + ".dat" ) >= 0 ) {
					addSavedGame.error( "Name is already taken" );
					return;
				}
			}

			if ( !addSavedGame.getPassword().equals( addSavedGame.getConfirmPassword() ) ) {
				addSavedGame.error( "Passwords do not match" );
				return;
			}
			
			saveCurrentGame( addSavedGame.getName(), addSavedGame.getPassword() );
			screen.getChildren().remove( addSavedGame );
			screen.getChildren().add( gameSavedPane );
			timeControl.getChildren().remove( pauseResume );

			// Stop game
			gameStatus = GameStatus.NOT_RUNNING;
		} );
		addSavedGame.getCancel().setOnAction( e -> {
			// Resume game
			resumeGame();
			addSavedGame.reset();
		} );

		gameSavedPane = new GameSavedPane( background );
		gameSavedPane.getBackToMainMenu().setOnAction( e -> loadMainMenu() );
		gameSavedPane.getCloseGame().setOnAction( e -> System.exit( 0 ) );

		confirmReturnToMainMenu = new ConfirmDecisionBox( background, 16 * gridCellWidth, "Back to main menu?", null,
				"ALL of your progress will be lost. Ever used Microsoft Word before instead of Google Docs? To save "
						+ "your progress, press CTRL + S.",
				"Yes, take me home.", "No, let me party." );
		confirmReturnToMainMenu.getYes().setOnAction( e -> loadMainMenu() );
		confirmReturnToMainMenu.getNo().setOnAction( e -> {
			// Resume game
			resumeGame();
			screen.getChildren().remove( confirmReturnToMainMenu );
		} );

		paused = new Text( "Paused" );
		paused.setWrappingWidth( numColumns * gridCellWidth );
		paused.setTextAlignment( TextAlignment.CENTER );
		paused.yProperty().bind( background.heightProperty().divide( 2 ).add( 7.5 * BuggyRun.gridCellWidth() ) );
		paused.setFont( Font.font( "Calibri", FontWeight.EXTRA_BOLD, 15 * BuggyRun.gridCellWidth() ) );
		paused.setFill( Color.RED );
	}

	private void setupTimeControl() {
		timeControl = new HBox( 2 * gridCellWidth );
		timeControl.setPadding( new Insets( 0, 0, 0, 2 * gridCellWidth ) );
		timer = new TimerPane( currentLevel, difficulty );

		pauseResume = new Button( "Pause" );
		pauseResume.setFont( Font.font( "Calibri", FontWeight.BOLD, 2.1 * gridCellWidth ) );
		pauseResume.setMinWidth( 10 * gridCellWidth );
		pauseResume.setOnAction( e -> {
			if ( secondsElapsed() < 0.2 )
				return;

			if ( pauseResume.getText().equals( "Pause" ) ) {
				pauseGame();
			}
			else if ( pauseResume.getText().equals( "Resume" ) ) {
				resumeGame();
				base.requestFocus();
			}
		} );
		pauseResume.setFocusTraversable( false );

		backToMainMenu = new Button( "Main menu" );
		backToMainMenu.setFont( Font.font( "Calibri", FontWeight.BOLD, 2.1 * gridCellWidth ) );
		backToMainMenu.setMinWidth( 14 * gridCellWidth );
		backToMainMenu.setOnAction( e -> {
			if ( gameStatus == GameStatus.NOT_RUNNING )
				loadMainMenu();

			// If game is still running or paused, only allow if screen doesn't already
			// contain confirmReturnToMainMenu,
			// screen doesn't contain confirmSaveGame and delayRespawn isn't running
			if ( !screen.getChildren().contains( confirmReturnToMainMenu )
					&& !screen.getChildren().contains( addSavedGame )
					&& delayRespawn.getStatus() != Animation.Status.RUNNING && secondsElapsed() >= 0.2 ) {
				pauseGame();
				screen.getChildren().remove( paused );
				screen.getChildren().add( confirmReturnToMainMenu );
			}
		} );
		backToMainMenu.setFocusTraversable( false );

		timeControl.setAlignment( Pos.CENTER_LEFT );
		timeControl.getChildren().addAll( timer, pauseResume, backToMainMenu );

		base.getChildren().add( timeControl );
	}

	private void setupLifebar( int numberOfLives ) {
		lifebar = new HBox( gridCellWidth );
		for ( int i = 0; i < numberOfLives; i++ ) {
			ImageView heart;
			if ( !isDarkMode() )
				heart = new ImageView( "file:do-not-touch" + File.separator + "heart.png" );
			else
				heart = new ImageView( "file:do-not-touch" + File.separator + "white heart.png" );
			heart.setFitWidth( 3 * gridCellWidth );
			heart.setFitHeight( 3 * gridCellWidth );
			lifebar.getChildren().add( heart );
		}
	}

	private void addPlayerToScreen() {
		for ( int i = 0; i < player.size(); i++ ) {
			screen.getChildren().add( player.get( i ) );
		}
	}

	private void removePlayerFromScreen() {
		for ( int i = 0; i < player.size(); i++ ) {
			screen.getChildren().remove( player.get( i ) );
		}
	}

	private void setupKeyInput() {
		base.setOnKeyPressed( e -> {
			if ( gameStatus == GameStatus.RUNNING ) {
				switch ( e.getCode() ) {
//					case U:
//						if ( e.isControlDown() ) {
//							while ( currentLevel < 16 )
//								levelUp();
//							break;
//						}
//						levelUp();
//						break;
//					case I:
//						addLifeToLifebar();
//						break;
//					case O:
//						timeBoost( 7 );
//						break;
//					case Y:
//						addStatusEffect( StatusEffect.TIME_CONTRACTION );
//						break;
					case UP:
						// Move up
						if ( movePlayerUp.getStatus() != Animation.Status.RUNNING ) {
							// If any other directions are active, pause them
							// If they were already paused, stop them
							if ( movePlayerRight.getStatus() == Animation.Status.RUNNING )
								movePlayerRight.pause();
							else if ( movePlayerRight.getStatus() == Animation.Status.PAUSED )
								movePlayerRight.stop();

							if ( movePlayerLeft.getStatus() == Animation.Status.RUNNING )
								movePlayerLeft.pause();
							else if ( movePlayerLeft.getStatus() == Animation.Status.PAUSED )
								movePlayerLeft.stop();

							if ( movePlayerDown.getStatus() == Animation.Status.RUNNING )
								movePlayerDown.pause();
							else if ( movePlayerDown.getStatus() == Animation.Status.PAUSED )
								movePlayerDown.stop();

							movePlayer( Direction.UP );
							movePlayerUp.play();
						}
						break;
					case W:
						// Move up
						if ( !e.isControlDown() ) {
							if ( movePlayerUp.getStatus() != Animation.Status.RUNNING ) {
								if ( movePlayerRight.getStatus() == Animation.Status.RUNNING )
									movePlayerRight.pause(); // Store direction
								else if ( movePlayerRight.getStatus() == Animation.Status.PAUSED )
									movePlayerRight.stop();

								if ( movePlayerLeft.getStatus() == Animation.Status.RUNNING )
									movePlayerLeft.pause(); // Store direction
								else if ( movePlayerLeft.getStatus() == Animation.Status.PAUSED )
									movePlayerLeft.stop();

								if ( movePlayerDown.getStatus() == Animation.Status.RUNNING )
									movePlayerDown.pause(); // Store direction
								else if ( movePlayerDown.getStatus() == Animation.Status.PAUSED )
									movePlayerDown.stop();

								movePlayer( Direction.UP );
								movePlayerUp.play();
							}
							break;
						}
						// Close program
						System.exit( 0 );
						break;
					case LEFT:
					case A:
						// Move left
						if ( movePlayerLeft.getStatus() != Animation.Status.RUNNING ) {
							if ( movePlayerRight.getStatus() == Animation.Status.RUNNING )
								movePlayerRight.pause();
							else if ( movePlayerRight.getStatus() == Animation.Status.PAUSED )
								movePlayerRight.stop();

							if ( movePlayerUp.getStatus() == Animation.Status.RUNNING )
								movePlayerUp.pause();
							else if ( movePlayerUp.getStatus() == Animation.Status.PAUSED )
								movePlayerUp.stop();

							if ( movePlayerDown.getStatus() == Animation.Status.RUNNING )
								movePlayerDown.pause();
							else if ( movePlayerDown.getStatus() == Animation.Status.PAUSED )
								movePlayerDown.stop();

							movePlayer( Direction.LEFT );
							movePlayerLeft.play();
						}
						break;
					case RIGHT:
					case D:
						// Move right
						if ( movePlayerRight.getStatus() != Animation.Status.RUNNING ) {
							if ( movePlayerLeft.getStatus() == Animation.Status.RUNNING )
								movePlayerLeft.pause();
							else if ( movePlayerLeft.getStatus() == Animation.Status.PAUSED )
								movePlayerLeft.stop();

							if ( movePlayerUp.getStatus() == Animation.Status.RUNNING )
								movePlayerUp.pause();
							else if ( movePlayerUp.getStatus() == Animation.Status.PAUSED )
								movePlayerUp.stop();

							if ( movePlayerDown.getStatus() == Animation.Status.RUNNING )
								movePlayerDown.pause();
							else if ( movePlayerDown.getStatus() == Animation.Status.PAUSED )
								movePlayerDown.stop();

							movePlayer( Direction.RIGHT );
							movePlayerRight.play();
						}
						break;
					case DOWN:
						// Move down
						if ( movePlayerDown.getStatus() != Animation.Status.RUNNING ) {
							if ( movePlayerRight.getStatus() == Animation.Status.RUNNING )
								movePlayerRight.pause();
							else if ( movePlayerRight.getStatus() == Animation.Status.PAUSED )
								movePlayerRight.stop();

							if ( movePlayerUp.getStatus() == Animation.Status.RUNNING )
								movePlayerUp.pause();
							else if ( movePlayerUp.getStatus() == Animation.Status.PAUSED )
								movePlayerUp.stop();

							if ( movePlayerLeft.getStatus() == Animation.Status.RUNNING )
								movePlayerLeft.pause();
							else if ( movePlayerLeft.getStatus() == Animation.Status.PAUSED )
								movePlayerLeft.stop();

							movePlayer( Direction.DOWN );
							movePlayerDown.play();
						}
						break;
					case S:
						// Game must be running or paused, and delayRespawn cannot be running
						if ( e.isControlDown() && delayRespawn.getStatus() != Animation.Status.RUNNING
								&& secondsElapsed() >= 0.2 ) {
							pauseGame();
							screen.getChildren().removeAll( paused, confirmReturnToMainMenu );
							screen.getChildren().add( addSavedGame );
							break;
						}
						// Move down
						if ( movePlayerDown.getStatus() != Animation.Status.RUNNING ) {
							if ( movePlayerRight.getStatus() == Animation.Status.RUNNING )
								movePlayerRight.pause();
							else if ( movePlayerRight.getStatus() == Animation.Status.PAUSED )
								movePlayerRight.stop();

							if ( movePlayerUp.getStatus() == Animation.Status.RUNNING )
								movePlayerUp.pause();
							else if ( movePlayerUp.getStatus() == Animation.Status.PAUSED )
								movePlayerUp.stop();

							if ( movePlayerLeft.getStatus() == Animation.Status.RUNNING )
								movePlayerLeft.pause();
							else if ( movePlayerLeft.getStatus() == Animation.Status.PAUSED )
								movePlayerLeft.stop();

							movePlayer( Direction.DOWN );
							movePlayerDown.play();
						}
						break;
					case ESCAPE:
					case P:
						// Pause game
						if ( secondsElapsed() >= 0.2 )
							pauseResume.fire();
						break;
					case Q:
					case M:
						// Confirm return to main menu
						if ( secondsElapsed() >= 0.2 )
							backToMainMenu.fire();
						break;
					case SPACE:
						if ( player.isAlive() )
							purchaseInvincibility();
						break;
					case ENTER:
						if ( player.isAlive() )
							selfDestruct();
						break;
					default:
						break;
				}
			}
			else if ( gameStatus == GameStatus.PAUSED ) {
				switch ( e.getCode() ) {
					case W:
						if ( e.isControlDown() )
							System.exit( 0 ); // Close program
						break;
					case S:
						if ( e.isControlDown() && !screen.getChildren().contains( addSavedGame )
								&& !screen.getChildren().contains( gameSavedPane ) && secondsElapsed() >= 0.2 ) {
							pauseGame();
							screen.getChildren().removeAll( paused, confirmReturnToMainMenu );
							screen.getChildren().add( addSavedGame );
						}
						break;
					case ESCAPE:
					case P:
						// P may be typed during name and/or password
						if ( !screen.getChildren().contains( addSavedGame ) )
							pauseResume.fire();
						break;
					case Q:
					case M:
						// M may be typed during name and/or password
						if ( !screen.getChildren().contains( addSavedGame ) )
							backToMainMenu.fire();
						break;
					default:
						break;
				}
			}
			else {
				switch ( e.getCode() ) {
					case W:
						if ( e.isControlDown() )
							System.exit( 0 ); // Close program
						break;

					case UP:
						if ( !settingsPane.getCalibrate().getText().equals( "Calibrate screen size" )
								&& heightCalibration >= 1 ) {
							primaryStage.setHeight( (int) ( primaryStage.getHeight() - 1 ) );
							heightCalibration = primaryStage.getHeight() - 35 * gridCellWidth;
							settingsPane.saveSettings();
						}
						break;
					case DOWN:
						if ( !settingsPane.getCalibrate().getText().equals( "Calibrate screen size" ) ) {
							primaryStage.setHeight( (int) ( primaryStage.getHeight() + 1 ) );
							heightCalibration = primaryStage.getHeight() - 35 * gridCellWidth;
							settingsPane.saveSettings();
						}
						break;
					case LEFT:
						if ( !settingsPane.getCalibrate().getText().equals( "Calibrate screen size" )
								&& widthCalibration >= 1 ) {
							primaryStage.setWidth( (int) ( primaryStage.getWidth() - 1 ) );
							widthCalibration = primaryStage.getWidth() - gridCellWidth * numColumns;
							settingsPane.saveSettings();

						}
						else if ( base.getChildren().size() == 1 && base.getChildren().contains( instructionsPane ) ) {
							if ( !instructionsPane.goBack() )
								loadMainMenu();
						}
						break;
					case A:
						if ( base.getChildren().size() == 1 && base.getChildren().contains( instructionsPane ) ) {
							if ( !instructionsPane.goBack() )
								loadMainMenu();
						}
						break;
					case RIGHT:
						if ( !settingsPane.getCalibrate().getText().equals( "Calibrate screen size" ) ) {
							primaryStage.setWidth( (int) ( primaryStage.getWidth() + 1 ) );
							widthCalibration = primaryStage.getWidth() - gridCellWidth * numColumns;
							settingsPane.saveSettings();
						}
						else if ( base.getChildren().size() == 1 && base.getChildren().contains( instructionsPane ) ) {
							if ( !instructionsPane.goForward() ) {
								base.getChildren().remove( instructionsPane );
								difficultySelection();
							}
						}
						break;
					case SPACE:
					case D:
						if ( base.getChildren().size() == 1 && base.getChildren().contains( instructionsPane ) ) {
							if ( !instructionsPane.goForward() ) {
								base.getChildren().remove( instructionsPane );
								difficultySelection();
							}
						}
						break;
					case ENTER:
						if ( base.getChildren().size() == 1 && base.getChildren().contains( instructionsPane ) ) {
							if ( !instructionsPane.goForward() ) {
								base.getChildren().remove( instructionsPane );
								difficultySelection();
							}
						}
						break;
					default:
						break;
				}
			}
		} );
		base.setOnKeyReleased( e -> {
			if ( gameStatus == GameStatus.RUNNING ) {
				switch ( e.getCode() ) {
					case UP:
					case W:
						movePlayerUp.stop();
						if ( movePlayerDown.getStatus() == Animation.Status.PAUSED )
							movePlayerDown.play();
						if ( movePlayerLeft.getStatus() == Animation.Status.PAUSED )
							movePlayerLeft.play();
						if ( movePlayerRight.getStatus() == Animation.Status.PAUSED )
							movePlayerRight.play();
						break;
					case LEFT:
					case A:
						movePlayerLeft.stop();
						if ( movePlayerDown.getStatus() == Animation.Status.PAUSED )
							movePlayerDown.play();
						if ( movePlayerUp.getStatus() == Animation.Status.PAUSED )
							movePlayerUp.play();
						if ( movePlayerRight.getStatus() == Animation.Status.PAUSED )
							movePlayerRight.play();
						break;
					case RIGHT:
					case D:
						movePlayerRight.stop();
						if ( movePlayerDown.getStatus() == Animation.Status.PAUSED )
							movePlayerDown.play();
						if ( movePlayerLeft.getStatus() == Animation.Status.PAUSED )
							movePlayerLeft.play();
						if ( movePlayerUp.getStatus() == Animation.Status.PAUSED )
							movePlayerUp.play();
						break;
					case DOWN:
					case S:
						movePlayerDown.stop();
						if ( movePlayerUp.getStatus() == Animation.Status.PAUSED )
							movePlayerUp.play();
						if ( movePlayerLeft.getStatus() == Animation.Status.PAUSED )
							movePlayerLeft.play();
						if ( movePlayerRight.getStatus() == Animation.Status.PAUSED )
							movePlayerRight.play();
						break;
					default:
						break;
				}
			}
		} );

		base.requestFocus();
	}

	private void movePlayer( Direction direction ) {
		if ( player.isAlive() ) {
			boolean moved = player.move( direction, background, finishLine );
			if ( moved ) {
				// Check methods not only check if something happened, but perform any necessary
				// action
				checkBombKillsPlayer( false );
				checkPlayerRunsIntoLaser();
				checkPlayerCollectsCoin();
				checkPlayerGetsExtraLife();
				checkPlayerGetsTimeBoost();
				checkPlayerGetsStatusEffectCoin();

				if ( player.getHead().getY() == 0 && finishLine.isUnlocked()
						&& finishLine.playerEntersUnlockedRectangle( player ) ) {
					System.out.println( String.format( "%.2f", timer.percentOfTimeLeft() ) );
					levelUp();
				}
			}
		}
	}

	private boolean checkBombKillsPlayer( boolean checkEntireBody ) {
		boolean killPlayer = false;
		if ( gameStatus != GameStatus.RUNNING )
			return false;

		for ( int i = 0; i < player.size(); i++ ) {
			if ( i > 0 && !checkEntireBody )
				break;

			int row = (int) ( player.get( i ).getY() / gridCellWidth );
			int column = (int) ( player.get( i ).getX() / gridCellWidth );
			VerticalBomb bomb = verticalBombsGrid[row][column];

			if ( bomb == null || bomb.row() != row || bomb.column() != column )
				continue;

			removeVerticalBomb( bomb );

			Circle explosion = new Circle( bomb.getCenterX(), bomb.getCenterY(), 5 * gridCellWidth );
			explosion.setFill( Color.YELLOW );
			screen.getChildren().add( explosion );
			FadeTransition explosionAnimation = new FadeTransition( Duration.seconds( 1.25 ), explosion );
			explosionAnimation.setFromValue( 1 );
			explosionAnimation.setToValue( 0 );
			explosionAnimation.setOnFinished( e -> {
				screen.getChildren().remove( explosion );
			} );
			explosionAnimation.play();

			if ( !player.isUnderInvincibilityEffect() )
				killPlayer = true;
		}

		for ( int i = 0; i < player.size(); i++ ) {
			if ( i > 0 && !checkEntireBody )
				break;

			int row = (int) ( player.get( i ).getY() / gridCellWidth );
			int column = (int) ( player.get( i ).getX() / gridCellWidth );
			HorizontalBomb bomb = horizontalBombsGrid[row][column];
			if ( bomb == null || bomb.row() != row || bomb.column() != column )
				continue;

			removeHorizontalBomb( bomb );

			Circle explosion = new Circle( bomb.getCenterX(), bomb.getCenterY(), 5 * gridCellWidth );
			explosion.setFill( Color.YELLOW );
			screen.getChildren().add( explosion );
			FadeTransition explosionAnimation = new FadeTransition( Duration.seconds( 1.25 ), explosion );
			explosionAnimation.setFromValue( 1 );
			explosionAnimation.setToValue( 0 );
			explosionAnimation.setOnFinished( e1 -> screen.getChildren().remove( explosion ) );
			explosionAnimation.play();

			if ( !player.isUnderInvincibilityEffect() )
				killPlayer = true;
		}

		if ( killPlayer ) {
			killPlayer();
			return true;
		}
		else
			return false;
	}

	private boolean checkPlayerRunsIntoLaser() {
		Laser laser = lasersGrid[player.row()];
		if ( laser != null && laser.killsPlayer( player, false ) && laser.getLaserBeam().getOpacity() > 0.2 ) {
			killPlayer();
			return true;
		}
		return false;
	}

	private void checkPlayerCollectsCoin() {
		Coin coin = coinsGrid[player.row()][player.column()];
		if ( coin != null ) {
			pointsTracker.addPoints( coin.getValue() * coinMultiplier );
			removeCoin( coin );
			if ( pointsTracker.hasEnoughPoints() )
				finishLine.unlock();
		}
	}

	private void checkPlayerGetsExtraLife() {
		ExtraLife extraLife = extraLivesGrid[player.row()][player.column()];
		if ( extraLife != null ) {
			removeExtraLife( extraLife );
			addLifeToLifebar();
		}
	}

	private void checkPlayerGetsTimeBoost() {
		TimeBoost timeBoost = timeBoostsGrid[player.row()][player.column()];
		if ( timeBoost != null ) {
			removeTimeBoost( timeBoost );
			timeBoost( 7 );
		}
	}

	private void checkPlayerGetsStatusEffectCoin() {
		StatusEffectCoin statusEffectCoin = statusEffectCoinsGrid[player.row()][player.column()];
		if ( statusEffectCoin != null ) {
			removeStatusEffectCoin( statusEffectCoin );
			// If player was already killed by bomb or laser, then do not add status effect
			if ( !player.isAlive() )
				return;

			if ( Math.random() < chanceOfNegativeStatusEffect() ) {
				StatusEffect negativeStatusEffect = negativeStatusEffects[(int) ( Math.random()
						* negativeStatusEffects.length )];
				// If total amount of time is less than 70 seconds, do not allow time dilation
				while ( LevelData.getSecondsInLevel( currentLevel, difficulty ) < 70
						&& negativeStatusEffect == StatusEffect.TIME_DILATION )
					negativeStatusEffect = negativeStatusEffects[(int) ( Math.random()
							* negativeStatusEffects.length )];
				addStatusEffect( negativeStatusEffect );
			}
			else {
				StatusEffect positiveStatusEffect = positiveStatusEffects[(int) ( Math.random()
						* positiveStatusEffects.length )];
				// If total amount of time is less than 70 seconds, do not allow time
				// contraction
				while ( LevelData.getSecondsInLevel( currentLevel, difficulty ) < 70
						&& positiveStatusEffect == StatusEffect.TIME_CONTRACTION )
					positiveStatusEffect = positiveStatusEffects[(int) ( Math.random()
							* positiveStatusEffects.length )];
				addStatusEffect( positiveStatusEffect );
			}
		}
	}

	private double chanceOfNegativeStatusEffect() {
		double percentOfPointsGained = pointsTracker.percentOfPointsGained();
		// If percentOfPointsGained is less than 10
		if ( percentOfPointsGained <= 10 )
			percentOfPointsGained = 10;
		double percentOfTimePassed = timer.percentOfTimePassed();
		// If there's extra time, return 100%
		if ( percentOfTimePassed < 0 )
			return 1;

		double negativeStatusEffectChance = 1.4 * Math.log( percentOfPointsGained / ( 1.2 * percentOfTimePassed ) );
		if ( difficulty == 0 )
			negativeStatusEffectChance += 0.4;
		else if ( difficulty == 1 )
			negativeStatusEffectChance += 0.43;
		else
			negativeStatusEffectChance += 0.47;

		// If <= 30% time left, decrease chance by 0.5% per percent,
		// starting at 0.5%
		if ( timer.percentOfTimeLeft() <= 30 )
			negativeStatusEffectChance -= ( percentOfTimePassed - 69 ) / 200;
		// If only 2 lives left, decrease chance by 5%
		if ( lifebar.getChildren().size() == 2 )
			negativeStatusEffectChance -= 0.05;
		// If only 1 life left, decrease chance by 12%
		else if ( lifebar.getChildren().size() == 1 )
			negativeStatusEffectChance -= 0.12;
		// If level is 11 or higher, decrease chance by 2% per level, starting at 2%
		if ( currentLevel >= 11 )
			negativeStatusEffectChance -= 2.0 * ( currentLevel - 10 ) / 100;

		return negativeStatusEffectChance;
	}

	private void pauseGame() {
		if ( secondsElapsed() < 0.2 )
			return;

		// Can only pause a running game and when player is alive
		if ( gameStatus != GameStatus.RUNNING || delayRespawn.getStatus() == Animation.Status.RUNNING )
			return;

		startAndStopTimes.add( System.currentTimeMillis() );
		gameStatus = GameStatus.PAUSED;

		// Prevent uncontrollable movement when game starts running again
		movePlayerUp.stop();
		movePlayerRight.stop();
		movePlayerDown.stop();
		movePlayerLeft.stop();

		pauseResume.setText( "Resume" );
		player.setAlive( false );
		// Pause timer and dropAndGenerateBombs, and freeze finishLine
		timer.pause();
		checkTimeLeft.pause();
		dropAndGenerateBombs.pause();
		generateLasers.pause();
		finishLine.freeze();

		for ( Laser laser : lasers )
			laser.pause();

		// Pause all status effect timelines
		if ( poisonTimeline.getStatus() == Animation.Status.RUNNING )
			poisonTimeline.pause();
		if ( timeDilationTimeline.getStatus() == Animation.Status.RUNNING )
			timeDilationTimeline.pause();
		if ( blindnessTimeline.getStatus() == Animation.Status.RUNNING )
			blindnessTimeline.pause();
		if ( doubleCoinsTimeline.getStatus() == Animation.Status.RUNNING )
			doubleCoinsTimeline.pause();
		if ( timeContractionTimeline.getStatus() == Animation.Status.RUNNING )
			timeContractionTimeline.pause();
		if ( invincibilityTimeline.getStatus() == Animation.Status.RUNNING )
			invincibilityTimeline.pause();

		// Pause statusEffectDisplay
		statusEffectDisplay.pauseEverything();
		// Add paused
		if ( !screen.getChildren().contains( paused ) )
			screen.getChildren().add( paused );
	}

	private void resumeGame() {
		// Can only resume a paused game
		if ( gameStatus != GameStatus.PAUSED )
			return;

		startAndStopTimes.add( System.currentTimeMillis() );
		pauseResume.setText( "Pause" );
		player.setAlive( true );
		gameStatus = GameStatus.RUNNING;

		// Resume timer and dropAndGenerateVerticalBombs, and unfreeze finishLine
		timer.play();
		checkTimeLeft.play();
		dropAndGenerateBombs.play();
		generateLasers.play();
		finishLine.unfreeze();

		for ( Laser laser : lasers )
			laser.resume();

		// Resume status effect timelines
		if ( poisonTimeline.getStatus() == Animation.Status.PAUSED )
			poisonTimeline.play();
		if ( timeDilationTimeline.getStatus() == Animation.Status.PAUSED )
			timeDilationTimeline.play();
		if ( blindnessTimeline.getStatus() == Animation.Status.PAUSED )
			blindnessTimeline.play();
		if ( doubleCoinsTimeline.getStatus() == Animation.Status.PAUSED )
			doubleCoinsTimeline.play();
		if ( timeContractionTimeline.getStatus() == Animation.Status.PAUSED )
			timeContractionTimeline.play();
		if ( invincibilityTimeline.getStatus() == Animation.Status.PAUSED )
			invincibilityTimeline.play();

		// Resume statusEffectDisplay
		statusEffectDisplay.resumeEverything();

		// Remove paused, confirmReturnToMainMenu, or addSavedGame, whichever one is on
		// the screen
		screen.getChildren().removeAll( paused, confirmReturnToMainMenu, addSavedGame );
		base.requestFocus();
	}

	// One time method for each new or loaded game
	private void setupAnimations() {
		movePlayerUp = new Timeline( new KeyFrame( Duration.millis( 35 ), e -> {
			movePlayer( Direction.UP );
		} ) );
		movePlayerUp.setCycleCount( -1 );

		movePlayerRight = new Timeline( new KeyFrame( Duration.millis( 35 ), e -> {
			movePlayer( Direction.RIGHT );
		} ) );
		movePlayerRight.setCycleCount( -1 );

		movePlayerDown = new Timeline( new KeyFrame( Duration.millis( 35 ), e -> {
			movePlayer( Direction.DOWN );
		} ) );
		movePlayerDown.setCycleCount( -1 );

		movePlayerLeft = new Timeline( new KeyFrame( Duration.millis( 35 ), e -> {
			movePlayer( Direction.LEFT );
		} ) );
		movePlayerLeft.setCycleCount( -1 );
		movePlayerUp.setDelay( Duration.seconds( 0.14 ) );
		movePlayerRight.setDelay( Duration.seconds( 0.14 ) );
		movePlayerDown.setDelay( Duration.seconds( 0.14 ) );
		movePlayerLeft.setDelay( Duration.seconds( 0.14 ) );

		dropAndGenerateBombs = new Timeline( new KeyFrame( Duration.seconds( 1.25 ), e -> {
			if ( gameStatus != GameStatus.RUNNING )
				return;
			// Drop current bombs and generate new bombs at the top and left
			dropAndRemoveVerticalBombs();
			generateVerticalBombs();

			dropAndRemoveHorizontalBombs();
			generateHorizontalBombs();

			checkBombKillsPlayer( true );
			// Garbage collect after creating two new 2D arrays each time
			System.gc();
		} ) );
		dropAndGenerateBombs.setRate( LevelData.getGravityIndex( currentLevel, difficulty ) );
		dropAndGenerateBombs.setCycleCount( Timeline.INDEFINITE );
		dropAndGenerateBombs.setAutoReverse( false );
		dropAndGenerateBombs.play();

		generateLasers = new Timeline( new KeyFrame( Duration.seconds( 1 ), e -> {
			if ( LevelData.hasSafeArea( currentLevel, difficulty ) ) {
				if ( player.row() >= 3 )
					generateLaser();
			}
			else
				generateLaser();

		} ) );
		generateLasers.setCycleCount( -1 );
		if ( difficulty == 3 )
			generateLasers.setRate( 1.4 );
		else if ( difficulty == 2 )
			generateLasers.setRate( 1.2 );
		generateLasers.play();

		// redRectangle flashing means life lost
		redRectangle = new Rectangle( 0, 0, numColumns * gridCellWidth,
				LevelData.getScreenHeight( currentLevel, difficulty ) );
		redRectangle.heightProperty().bind( background.heightProperty() );
		redRectangle.setFill( Color.RED );
		redRectangle.setOpacity( 0 );
		screen.getChildren().add( redRectangle );

		// lifeLost animates redRectangle flashing
		lifeLost = new FadeTransition();
		lifeLost.setNode( redRectangle );
		lifeLost.setDuration( Duration.seconds( 1 ) );
		if ( isDarkMode() )
			lifeLost.setFromValue( 0.7 );
		else
			lifeLost.setFromValue( 0.5 );
		lifeLost.setToValue( 0 );

		// greenRectangle flashing means life gained
		greenRectangle = new Rectangle( 0, 0, numColumns * gridCellWidth,
				LevelData.getScreenHeight( currentLevel, difficulty ) );
		greenRectangle.heightProperty().bind( background.heightProperty() );
		greenRectangle.setFill( Color.GREEN );
		greenRectangle.setOpacity( 0 );
		screen.getChildren().add( greenRectangle );

		// lifeGained animates greenRectangle flashing
		lifeGained = new FadeTransition();
		lifeGained.setNode( greenRectangle );
		lifeGained.setDuration( Duration.seconds( 1 ) );
		if ( isDarkMode() )
			lifeGained.setFromValue( 0.7 );
		else
			lifeGained.setFromValue( 0.5 );
		lifeGained.setToValue( 0 );

		// blueRectangle flashing means time gained
		blueRectangle = new Rectangle( 0, 0, numColumns * gridCellWidth,
				LevelData.getScreenHeight( currentLevel, difficulty ) );
		blueRectangle.heightProperty().bind( background.heightProperty() );
		blueRectangle.setFill( Color.BLUE );
		blueRectangle.setOpacity( 0 );
		screen.getChildren().add( blueRectangle );

		// timeBoosted animates blueRectangle flashing
		timeBoosted = new FadeTransition();
		timeBoosted.setNode( blueRectangle );
		timeBoosted.setDuration( Duration.seconds( 1 ) );
		if ( isDarkMode() )
			timeBoosted.setFromValue( 0.7 );
		else
			timeBoosted.setFromValue( 0.5 );
		timeBoosted.setToValue( 0 );

		// levelUpArrow flashing means player leveled up
		levelUpArrow = new ImageView( "file:do-not-touch" + File.separator + "levelup.png" );
		levelUpArrow.setFitWidth( 20 * gridCellWidth );
		levelUpArrow.setFitHeight( 20 * gridCellWidth );
		levelUpArrow.setX( background.getWidth() / 2 - 10 * gridCellWidth );
		levelUpArrow.yProperty().bind( background.heightProperty().divide( 2 ).subtract( 10 * gridCellWidth ) );
		levelUpArrow.setOpacity( 0 );
		screen.getChildren().add( levelUpArrow );

		// leveledUp animates levelUpArrow flashing
		leveledUp = new FadeTransition();
		leveledUp.setNode( levelUpArrow );
		leveledUp.setDuration( Duration.seconds( 1 ) );
		if ( isDarkMode() )
			leveledUp.setFromValue( 0.7 );
		else
			leveledUp.setFromValue( 0.5 );
		leveledUp.setToValue( 0 );

		// checkTimeLeft checks if there is still time left in the timer
		checkTimeLeft = new Timeline( new KeyFrame( Duration.seconds( 0.1 ), e -> {
			if ( timer.getNumberOfSecondsLeft() <= 0 )
				killPlayer(); // If no time left, kill player. delayRespawn adds some time back to timer
		} ) );
		checkTimeLeft.setCycleCount( Timeline.INDEFINITE );
		checkTimeLeft.play();

		// delayRespawn delays respawning for 1.5 seconds after player is killed
		delayRespawn = new Timeline( new KeyFrame( Duration.seconds( 1.5 ), e -> {
			if ( lifebar.getChildren().size() > 0 ) {
				// If player was killed because time ran out
				if ( timer.getNumberOfSecondsLeft() <= 0 ) {
					// Set a certain fraction of time left, depending on difficulty
					if ( difficulty == 0 )
						timer.setNumberOfSecondsLeft( timer.getOriginalNumberOfSeconds() * 0.5 );
					else if ( difficulty == 1 )
						timer.setNumberOfSecondsLeft( timer.getOriginalNumberOfSeconds() * 0.4 );
					else if ( difficulty == 2 )
						timer.setNumberOfSecondsLeft( timer.getOriginalNumberOfSeconds() * 0.3 );
				}

				respawnPlayer();
			}
			else
				gameOver();
		} ) );

		// criticalLifebar animates lifebar flashing when there is only one life left
		criticalLifebar = new FadeTransition();
		criticalLifebar.setNode( lifebar );
		criticalLifebar.setFromValue( 1 );
		criticalLifebar.setToValue( 0.2 );
		criticalLifebar.setAutoReverse( true );
		criticalLifebar.setDuration( Duration.seconds( 0.32 ) );
		criticalLifebar.setCycleCount( Timeline.INDEFINITE );
		// Make sure criticalLifebar only animates when there is one life left
		lifebar.getChildren().addListener( (InvalidationListener) ov -> {
			if ( lifebar.getChildren().size() == 1 )
				criticalLifebar.play();
			else {
				criticalLifebar.stop();
				lifebar.setOpacity( 1 );
			}
		} );
		if ( lifebar.getChildren().size() == 1 )
			criticalLifebar.play();

		// Status effect timelines count time for when a status effect is active
		// Lambda expressions tell what to do when timeline is finished, or when
		// status effect is over
		poisonTimeline = new Timeline( new KeyFrame( Duration.seconds( 10 ), e -> {
			player.setUnderPoisonEffect( false );
			statusEffectDisplay.remove( StatusEffect.POISON );
		} ) );
		timeDilationTimeline = new Timeline( new KeyFrame( Duration.seconds( 10 ), e -> {
			timer.normalMode();
			statusEffectDisplay.remove( StatusEffect.TIME_DILATION );
		} ) );
		blindnessTimeline = new Timeline( new KeyFrame( Duration.seconds( 10 ), e -> {
			player.setUnderBlindnessEffect( false );
			statusEffectDisplay.remove( StatusEffect.BLINDNESS );
		} ) );
		doubleCoinsTimeline = new Timeline( new KeyFrame( Duration.seconds( 10 ), e -> {
			coinMultiplier = 1;
			statusEffectDisplay.remove( StatusEffect.DOUBLE_COINS );
		} ) );
		timeContractionTimeline = new Timeline( new KeyFrame( Duration.seconds( 10 ), e -> {
			timer.normalMode();
			statusEffectDisplay.remove( StatusEffect.TIME_CONTRACTION );
		} ) );
		invincibilityTimeline = new Timeline( new KeyFrame( Duration.seconds( 10 ), e -> {
			player.setUnderInvicibilityEffect( false );
			statusEffectDisplay.remove( StatusEffect.INVINCIBILITY );
		} ) );
	}

	private void addStatusEffect( StatusEffect statusEffect ) {
		if ( statusEffect == StatusEffect.POISON ) {
			// If able to set player under poison effect
			if ( player.setUnderPoisonEffect( true ) ) {
				statusEffectDisplay.add( StatusEffect.POISON );
				if ( poisonTimeline.getStatus() == Animation.Status.RUNNING )
					poisonTimeline.jumpTo( Duration.ZERO );
				poisonTimeline.play();
			}
		}
		else if ( statusEffect == StatusEffect.TIME_DILATION ) {
			statusEffectDisplay.add( StatusEffect.TIME_DILATION );
			timer.normalMode();
			statusEffectDisplay.remove( StatusEffect.TIME_CONTRACTION );
			timeContractionTimeline.stop();
			if ( timeDilationTimeline.getStatus() == Animation.Status.RUNNING )
				timeDilationTimeline.jumpTo( Duration.ZERO );
			timer.fastMode();
			timeDilationTimeline.play();
		}
		else if ( statusEffect == StatusEffect.BLINDNESS ) {
			if ( player.setUnderBlindnessEffect( true ) ) {
				statusEffectDisplay.add( StatusEffect.BLINDNESS );
				if ( blindnessTimeline.getStatus() == Animation.Status.RUNNING )
					blindnessTimeline.jumpTo( Duration.ZERO );
				blindnessTimeline.play();
			}
		}
		else if ( statusEffect == StatusEffect.DOUBLE_COINS ) {
			statusEffectDisplay.add( StatusEffect.DOUBLE_COINS );
			if ( doubleCoinsTimeline.getStatus() == Animation.Status.RUNNING )
				doubleCoinsTimeline.jumpTo( Duration.ZERO );
			coinMultiplier = 2;
			doubleCoinsTimeline.play();
		}
		else if ( statusEffect == StatusEffect.TIME_CONTRACTION ) {
			statusEffectDisplay.add( StatusEffect.TIME_CONTRACTION );
			timer.normalMode();
			statusEffectDisplay.remove( StatusEffect.TIME_DILATION );
			timeDilationTimeline.stop();
			if ( timeContractionTimeline.getStatus() == Animation.Status.RUNNING )
				timeContractionTimeline.jumpTo( Duration.ZERO );
			timer.slowMode();
			timeContractionTimeline.play();
		}
		else if ( statusEffect == StatusEffect.INVINCIBILITY ) {
			statusEffectDisplay.add( StatusEffect.INVINCIBILITY );
			if ( invincibilityTimeline.getStatus() == Animation.Status.RUNNING )
				invincibilityTimeline.jumpTo( Duration.ZERO );
			player.setUnderInvicibilityEffect( true );
			invincibilityTimeline.play();

			blindnessTimeline.stop();
			poisonTimeline.stop();
		}
	}

	private void loadStatusEffect( StatusEffect statusEffect, double fastForwardSeconds ) {
		if ( statusEffect == StatusEffect.POISON ) {
			player.setUnderPoisonEffect( true );
			statusEffectDisplay.load( StatusEffect.POISON, fastForwardSeconds );
			poisonTimeline.jumpTo( Duration.seconds( fastForwardSeconds ) );
			poisonTimeline.play();
		}
		else if ( statusEffect == StatusEffect.TIME_DILATION ) {
			statusEffectDisplay.load( StatusEffect.TIME_DILATION, fastForwardSeconds );
			timer.fastMode();
			timeDilationTimeline.jumpTo( Duration.seconds( fastForwardSeconds ) );
			timeDilationTimeline.play();
		}
		else if ( statusEffect == StatusEffect.BLINDNESS ) {
			player.setUnderBlindnessEffect( true );
			statusEffectDisplay.load( StatusEffect.BLINDNESS, fastForwardSeconds );
			blindnessTimeline.jumpTo( Duration.seconds( fastForwardSeconds ) );
			blindnessTimeline.play();
		}
		else if ( statusEffect == StatusEffect.DOUBLE_COINS ) {
			statusEffectDisplay.load( StatusEffect.DOUBLE_COINS, fastForwardSeconds );
			coinMultiplier = 2;
			doubleCoinsTimeline.jumpTo( Duration.seconds( fastForwardSeconds ) );
			doubleCoinsTimeline.play();
		}
		else if ( statusEffect == StatusEffect.TIME_CONTRACTION ) {
			statusEffectDisplay.load( StatusEffect.TIME_CONTRACTION, fastForwardSeconds );
			timer.slowMode();
			timeContractionTimeline.jumpTo( Duration.seconds( fastForwardSeconds ) );
			timeContractionTimeline.play();
		}
		else if ( statusEffect == StatusEffect.INVINCIBILITY ) {
			statusEffectDisplay.load( StatusEffect.INVINCIBILITY, fastForwardSeconds );
			player.setUnderInvicibilityEffect( true );
			invincibilityTimeline.jumpTo( Duration.seconds( fastForwardSeconds ) );
			invincibilityTimeline.play();
		}
	}

	private void purchaseInvincibility() {
		if ( pointsTracker.getCurrentPoints() < invincibilityCost )
			return;
		// Check if there will still be enough points after purchasing invincibility
		else if ( pointsTracker.getCurrentPoints() + totalValueOfCoins() - invincibilityCost < pointsTracker
				.getRequiredPoints() )
			return;

		pointsTracker.subtractPoints( invincibilityCost );
		if ( !pointsTracker.hasEnoughPoints() )
			finishLine.lock();
		addStatusEffect( StatusEffect.INVINCIBILITY );
	}

	private void selfDestruct() {
		killPlayer();
	}

	private void killPlayer() {
		player.setAlive( false );
		removePlayerFromScreen();

		if ( lifebar.getChildren().size() > 0 ) {
			lifebar.getChildren().remove( lifebar.getChildren().size() - 1 );
			// If out of lives, remove lifebar
			if ( lifebar.getChildren().size() == 0 )
				hBox.getChildren().remove( lifebar );
		}

		if ( lifeLost.getStatus() == Animation.Status.RUNNING )
			lifeLost.stop();
		lifeLost.play();

		timer.pause();
		checkTimeLeft.pause();
		dropAndGenerateBombs.pause();
		generateLasers.pause();
		removeAllStatusEffects();

		delayRespawn.play();
	}

	private void removeAllStatusEffects() {
		if ( player.isUnderPoisonEffect() )
			player.setUnderPoisonEffect( false );
		timer.normalMode();
		if ( player.isUnderBlindnessEffect() )
			player.setUnderBlindnessEffect( false );
		coinMultiplier = 1;
		if ( player.isUnderInvincibilityEffect() )
			player.setUnderInvicibilityEffect( false );

		statusEffectDisplay.clear();
		blindnessTimeline.stop();
		poisonTimeline.stop();
		timeDilationTimeline.stop();
		timeContractionTimeline.stop();
		doubleCoinsTimeline.stop();
		invincibilityTimeline.stop();
	}

	private void respawnPlayer() {
		player.spawn( gridCellWidth * numColumns / 2 - gridCellWidth, background.getHeight() - gridCellWidth );
		// Add player to screen
		addPlayerToScreen();
		removeBottomBombs();
		removeAllLasers();

		// Resume timer, checkTimeLeft, and dropAndGenerateBombs
		timer.play();
		checkTimeLeft.play();
		dropAndGenerateBombs.play();
		generateLasers.play();

		player.setAlive( true );
	}

	private void gameOver() {
		gameStatus = GameStatus.NOT_RUNNING;
		startAndStopTimes.add( System.currentTimeMillis() );
		movePlayerUp.stop();
		movePlayerLeft.stop();
		movePlayerRight.stop();
		movePlayerDown.stop();

		// Stop timer
		timer.pause();
		checkTimeLeft.stop();
		hBox.getChildren().remove( lifebar );

		timeControl.getChildren().remove( pauseResume );

		// Add finalResult which says "Game over!" on screen
		Text finalResult = new Text( "Game over!" );
		finalResult.setWrappingWidth( gridCellWidth * numColumns );
		finalResult.setTextAlignment( TextAlignment.CENTER );
		finalResult.yProperty().bind( background.heightProperty().divide( 2 ).add( 6 * BuggyRun.gridCellWidth() ) );
		finalResult.setFont( Font.font( "Calibri", FontWeight.BOLD, 12 * BuggyRun.gridCellWidth() ) );
		finalResult.setFill( Color.RED );
		screen.getChildren().add( finalResult );

		// Apply gameOver to levelTracker
		levelTracker.gameOver();

		// Display time elapsed
		Text timeElapsed = new Text( secondsElapsedToString() );
		timeElapsed.setFont( Font.font( "Calibri", FontWeight.EXTRA_BOLD, 1.8 * BuggyRun.gridCellWidth() ) );
		if ( isDarkMode() )
			timeElapsed.setFill( Color.WHITE );
		timeElapsed.setWrappingWidth( numColumns * gridCellWidth );
		timeElapsed.yProperty().bind( background.heightProperty().divide( 2 ).add( 8.5 * BuggyRun.gridCellWidth() ) );
		timeElapsed.setTextAlignment( TextAlignment.CENTER );
		screen.getChildren().add( timeElapsed );

		timeControl.getChildren().remove( pauseResume );
	}

	private void addVerticalBomb( VerticalBomb bomb ) {
		if ( settingsPane.isDarkMode() )
			bomb.setFill( Color.WHITE );

		screen.getChildren().add( bomb );
		verticalBombs.add( bomb );

		verticalBombsGrid[bomb.row()][bomb.column()] = bomb;
	}

	private void removeVerticalBomb( VerticalBomb bomb ) {
		screen.getChildren().remove( bomb );
		verticalBombs.remove( bomb );

		verticalBombsGrid[bomb.row()][bomb.column()] = null;
	}

	private void removeAllVerticalBombs() {
		while ( !verticalBombs.isEmpty() ) {
			removeVerticalBomb( verticalBombs.get( 0 ) );
		}
	}

	// Remove bombs in the bottom fraction of the screen, depending on
	// difficulty
	// The harder the difficulty, the larger the fraction
	private void removeBottomBombs() {
		double[] leniency = { 0.125, 0.16, 0.2, 0.25 };
		for ( int i = 0; i < verticalBombs.size(); i++ ) {
			VerticalBomb bomb = verticalBombs.get( i );
			if ( bomb.getCenterY() > background.getHeight() * ( 1 - leniency[difficulty] ) ) {
				removeVerticalBomb( bomb );
				--i;
			}
		}
		for ( int i = 0; i < horizontalBombs.size(); i++ ) {
			HorizontalBomb bomb = horizontalBombs.get( i );
			if ( bomb.getCenterY() > background.getHeight() * ( 1 - leniency[difficulty] ) ) {
				removeHorizontalBomb( bomb );
				--i;
			}
		}
	}

	private void addHorizontalBomb( HorizontalBomb bomb ) {
		if ( settingsPane.isDarkMode() )
			bomb.setFill( Color.WHITE );

		screen.getChildren().add( bomb );
		horizontalBombs.add( bomb );

		horizontalBombsGrid[bomb.row()][bomb.column()] = bomb;
	}

	private void removeHorizontalBomb( HorizontalBomb bomb ) {
		screen.getChildren().remove( bomb );
		horizontalBombs.remove( bomb );

		horizontalBombsGrid[bomb.row()][bomb.column()] = null;
	}

	private void removeAllHorizontalBombs() {
		while ( !horizontalBombs.isEmpty() ) {
			removeHorizontalBomb( horizontalBombs.get( 0 ) );
		}
	}

	private void addLaser( Laser laser ) {
		screen.getChildren().add( laser );
		lasers.add( laser );
		lasersGrid[laser.row()] = laser;

		laser.statusProperty().addListener( ov -> {
			if ( !screen.getChildren().contains( laser ) )
				return;

			if ( laser.statusProperty().get() == 0 ) {
				if ( laser.killsPlayer( player, true ) )
					killPlayer();
			}

			if ( laser.statusProperty().get() == -1 )
				removeLaser( laser );
		} );
	}

	private void removeLaser( Laser laser ) {
		lasers.remove( laser );
		screen.getChildren().remove( laser );
		lasersGrid[laser.row()] = null;
	}

	private void removeAllLasers() {
		while ( !lasers.isEmpty() )
			removeLaser( lasers.get( 0 ) );
	}

	private void addTimeBoost( TimeBoost timeBoost ) {
		screen.getChildren().add( timeBoost );
		timeBoosts.add( timeBoost );
		timeBoostsGrid[timeBoost.row()][timeBoost.column()] = timeBoost;
	}

	private void removeTimeBoost( TimeBoost timeBoost ) {
		screen.getChildren().remove( timeBoost );
		timeBoosts.remove( timeBoost );
		timeBoostsGrid[timeBoost.row()][timeBoost.column()] = null;
	}

	private void removeAllTimeBoosts() {
		while ( !timeBoosts.isEmpty() ) {
			removeTimeBoost( timeBoosts.get( 0 ) );
		}
	}

	private void addStatusEffectCoin( StatusEffectCoin statusEffectCoin ) {
		screen.getChildren().add( statusEffectCoin );
		statusEffectCoins.add( statusEffectCoin );
		statusEffectCoinsGrid[statusEffectCoin.row()][statusEffectCoin.column()] = statusEffectCoin;
	}

	private void removeStatusEffectCoin( StatusEffectCoin statusEffectCoin ) {
		screen.getChildren().remove( statusEffectCoin );
		statusEffectCoins.remove( statusEffectCoin );
		statusEffectCoinsGrid[statusEffectCoin.row()][statusEffectCoin.column()] = null;
	}

	private void removeAllStatusEffectCoins() {
		while ( !statusEffectCoins.isEmpty() )
			removeStatusEffectCoin( statusEffectCoins.get( 0 ) );
	}

	private void addCoin( Coin coin ) {
		screen.getChildren().add( coin );
		coins.add( coin );
		coinsGrid[coin.row()][coin.column()] = coin;
	}

	private void removeCoin( Coin coin ) {
		screen.getChildren().remove( coin );
		coins.remove( coin );
		coinsGrid[coin.row()][coin.column()] = null;
	}

	private void removeAllCoins() {
		while ( !coins.isEmpty() ) {
			removeCoin( coins.get( 0 ) );
		}
	}

	private void addExtraLife( ExtraLife extraLife ) {
		screen.getChildren().add( extraLife );
		extraLives.add( extraLife );
		extraLivesGrid[extraLife.row()][extraLife.column()] = extraLife;
	}

	private void removeExtraLife( ExtraLife extraLife ) {
		screen.getChildren().remove( extraLife );
		extraLives.remove( extraLife );
		extraLivesGrid[extraLife.row()][extraLife.column()] = null;
	}

	private void removeAllExtraLives() {
		while ( !extraLives.isEmpty() ) {
			removeExtraLife( extraLives.get( 0 ) );
		}
	}

	private void generateCoins( int numRows ) {
		int[] values = { 1, 2, 3, 4, 5 };
		double multiplier = 0;
		// Beyond 1.3 or so, a small change in multiplier will make a large difference,
		// especially in the lower levels
		if ( difficulty == 0 )
			multiplier = 1.65;
		else if ( difficulty == 1 )
			multiplier = 1.5;
		else if ( difficulty == 2 )
			multiplier = 1.3;
		else if ( difficulty == 3 )
			multiplier = 1.25;

		// Reduce number of coins in the first 8 levels by decreasing multiplier
		if ( difficulty == 3 ) {
			// Expert mode: at level 1, multiplier will be 1.074, then increases by 0.022 per level until level 8
			if ( currentLevel <= 8 )
				multiplier -= 0.022 * ( 9 - currentLevel );
		}
		else if ( difficulty == 2 ) {
			// At level 1, multiplier will be 1.1, then increases by 0.025 per level until level 8
			if ( currentLevel <= 8 )
				multiplier -= 0.025 * ( 9 - currentLevel );
		}
		else if ( difficulty == 1 ) {
			// At level 1, multiplier will be 1.34
			if ( currentLevel <= 8 )
				multiplier -= 0.02 * ( 9 - currentLevel );
		}
		else {
			// At level 1, multiplier will be 1.53
			if ( currentLevel <= 8 )
				multiplier -= 0.015 * ( 9 - currentLevel );
		}

		int totalValue = 0;
		Random r = new Random();
		while ( totalValue < LevelData.getRequiredPointsToLevelUp( currentLevel, difficulty ) * multiplier ) {
			// Retrieve random value, put coin in random area, and increment
			// totalValueOfCoins
			int value = values[(int) ( Math.random() * 5 )];
			int row;
			if ( LevelData.hasSafeArea( currentLevel, difficulty ) )
				row = r.nextInt( numRows - 3 ) + 3;
			else
				row = r.nextInt( numRows - 1 ) + 1;

			int column = r.nextInt( numColumns );
			// While current position in coinsGrid is already taken, or coin overlaps with
			// status effect coin
			while ( coinsGrid[row][column] != null || statusEffectCoinsGrid[row][column] != null ) {
				if ( LevelData.hasSafeArea( currentLevel, difficulty ) )
					row = r.nextInt( numRows - 3 ) + 3;
				else
					row = r.nextInt( numRows - 1 ) + 1;
				column = r.nextInt( numColumns );
			}
			if ( Math.random() < 0.5
					&& ( LevelData.hasSafeArea( currentLevel, difficulty ) ? row == 3 : row == 1 || column == 0 ) )
				continue;

			Coin newCoin = new Coin( value, row, column, isDarkMode() );
			addCoin( newCoin );
			totalValue += value;
		}

		// Generate one more coin
		int value = values[(int) ( Math.random() * 5 )];
		int row;
		if ( LevelData.hasSafeArea( currentLevel, difficulty ) )
			row = r.nextInt( numRows - 3 ) + 3;
		else
			row = r.nextInt( numRows - 1 ) + 1;

		int column = r.nextInt( numColumns );
		// While current position in coinsGrid is already taken
		while ( coinsGrid[row][column] != null ) {
			if ( LevelData.hasSafeArea( currentLevel, difficulty ) )
				row = r.nextInt( numRows - 3 ) + 3;
			else
				row = r.nextInt( numRows - 1 ) + 1;
			column = r.nextInt( numColumns );
		}

		Coin newCoin = new Coin( value, row, column, isDarkMode() );
		addCoin( newCoin );
	}

	private void generateExtraLives( int numRows ) {
		// If currentLevel is assinged to generate an extraLife
		if ( levelsWithExtraLives.contains( currentLevel ) ) {
			Random r = new Random();
			int row;
			if ( LevelData.hasSafeArea( currentLevel, difficulty ) )
				row = r.nextInt( numRows - 4 ) + 4;
			else
				row = r.nextInt( numRows - 2 ) + 2;
			int column = r.nextInt( numColumns - 1 ) + 1;
			// ExtraLife cannot overlap with status effect coin
			while ( statusEffectCoinsGrid[row][column] != null ) {
				if ( LevelData.hasSafeArea( currentLevel, difficulty ) )
					row = r.nextInt( numRows - 4 ) + 4;
				else
					row = r.nextInt( numRows - 2 ) + 2;
				column = r.nextInt( numColumns - 1 ) + 1;

			}

			ExtraLife newExtraLife = new ExtraLife( row, column, isDarkMode() );
			addExtraLife( newExtraLife );
		}
	}

	private void generateTimeBoosts( int numRows ) {
		Random r = new Random();
		for ( int i = 0; i < LevelData.getNumberOfTimeBoosts( currentLevel, difficulty ); i++ ) {
			int row;
			if ( LevelData.hasSafeArea( currentLevel, difficulty ) )
				row = r.nextInt( numRows - 4 ) + 4;
			else
				row = r.nextInt( numRows - 2 ) + 2;
			int column = r.nextInt( numColumns - 1 ) + 1;
			// Time boost cannot overlap with status effect coin
			while ( statusEffectCoinsGrid[row][column] != null ) {
				if ( LevelData.hasSafeArea( currentLevel, difficulty ) )
					row = r.nextInt( numRows - 4 ) + 4;
				else
					row = r.nextInt( numRows - 2 ) + 2;
				column = r.nextInt( numColumns - 1 ) + 1;
			}

			TimeBoost newTimeBoost = new TimeBoost( row, column, isDarkMode() );
			addTimeBoost( newTimeBoost );
		}
	}

	private void generateStatusEffectCoins( int numRows ) {
		Random r = new Random();
		for ( int i = 0; i < LevelData.getNumberOfStatusEffectCoins( currentLevel, difficulty ); i++ ) {
			int row;
			if ( LevelData.hasSafeArea( currentLevel, difficulty ) )
				row = r.nextInt( numRows - 3 ) + 3;
			else
				row = r.nextInt( numRows - 1 ) + 1;
			int column = r.nextInt( numColumns );
			// While current position in statusEffectCoinsGrid is already taken
			while ( statusEffectCoinsGrid[row][column] != null ) {
				if ( LevelData.hasSafeArea( currentLevel, difficulty ) )
					row = r.nextInt( numRows - 3 ) + 3;
				else
					row = r.nextInt( numRows - 1 ) + 1;
				column = r.nextInt( numColumns );
			}

			StatusEffectCoin newStatusEffect = new StatusEffectCoin( row, column, isDarkMode() );
			addStatusEffectCoin( newStatusEffect );
		}
	}

	private void dropAndRemoveVerticalBombs() {
		for ( int i = 0; i < verticalBombs.size(); i++ ) {
			VerticalBomb bomb = verticalBombs.get( i );
			bomb.drop();
			if ( bomb.isOutOfBounds( background ) ) {
				screen.getChildren().remove( bomb );
				verticalBombs.remove( bomb );
				i--;
			}
		}

		verticalBombsGrid = new VerticalBomb[(int) ( background.getHeight() ) / gridCellWidth][numColumns];
		for ( VerticalBomb bomb : verticalBombs )
			verticalBombsGrid[bomb.row()][bomb.column()] = bomb;
	}

	private void dropAndRemoveHorizontalBombs() {
		for ( int i = 0; i < horizontalBombs.size(); i++ ) {
			HorizontalBomb bomb = horizontalBombs.get( i );
			bomb.drop();

			if ( bomb.isOutOfBounds( background ) ) {
				screen.getChildren().remove( bomb );
				horizontalBombs.remove( bomb );
				i--;
			}
		}

		horizontalBombsGrid = new HorizontalBomb[(int) ( background.getHeight() ) / gridCellWidth][numColumns];
		for ( HorizontalBomb bomb : horizontalBombs )
			horizontalBombsGrid[bomb.row()][bomb.column()] = bomb;
	}

	private void generateVerticalBombs() {
		for ( int col = 0; col < numColumns; col++ ) {
			// Prevent bomb from just popping out directly on top of the player
			if ( !LevelData.hasSafeArea( currentLevel, difficulty ) && player.getHead().column() == col
					&& player.getHead().row() <= 2 )
				continue;
			else if ( LevelData.hasSafeArea( currentLevel, difficulty ) && player.getHead().column() == col
					&& player.getHead().row() <= 4 && player.getHead().row() >= 3 )
				continue;

			if ( Math.random() < LevelData.getVerticalBombsFrequencies( currentLevel, difficulty ) ) {
				VerticalBomb newBomb;
				int row;
				if ( LevelData.hasSafeArea( currentLevel, difficulty ) )
					row = 3;
				else
					row = 1;

				newBomb = new VerticalBomb( row, col );
				addVerticalBomb( newBomb );
			}

		}
	}

	private void generateHorizontalBombs() {
		int firstRow;
		if ( LevelData.hasSafeArea( currentLevel, difficulty ) )
			firstRow = 3;
		else
			firstRow = 1;

		for ( int row = firstRow; row < LevelData.getScreenHeight( currentLevel, difficulty ) / gridCellWidth; row++ ) {
			if ( Math.random() < LevelData.getHorizontalBombsFrequencies( currentLevel, difficulty ) ) {
				HorizontalBomb newBomb = new HorizontalBomb( row, 0 );
				addHorizontalBomb( newBomb );
			}
		}
	}

	private void generateLaser() {
		if ( Math.random() < LevelData.getChanceOfLaser( currentLevel, difficulty )
				&& gameStatus == GameStatus.RUNNING ) {
			Laser laser = new Laser( player );
			addLaser( laser );
		}

	}

	private int totalValueOfCoins() {
		int sum = 0;
		for ( Coin coin : coins ) {
			sum += coin.getValue();
		}
		return sum;
	}

	private void levelUp() {
		if ( LevelData.isPlayerGrowsUponBeatingLevel( currentLevel, difficulty ) ) {
			if ( player.size() == 1 )
				player.grow(); // Grow twice the first time
			player.grow();
		}

		removePlayerFromScreen();
		removeAllCoins();
		removeAllVerticalBombs();
		removeAllHorizontalBombs();
		removeAllExtraLives();
		removeAllTimeBoosts();
		removeAllStatusEffectCoins();
		removeAllStatusEffects();
		removeAllLasers();

		if ( currentLevel == numLevels )
			playerWins();
		else {
			++currentLevel;
			if ( leveledUp.getStatus() == Animation.Status.RUNNING ) {
				leveledUp.stop();
				leveledUp.play();
			}
			else
				leveledUp.play();

			background.setHeight( LevelData.getScreenHeight( currentLevel, difficulty ) );
			pointsTracker.levelUp();
			timer.levelUp();
			dropAndGenerateBombs.setRate( LevelData.getGravityIndex( currentLevel, difficulty ) );
			levelTracker.levelUp();
			finishLine.lock();
			finishLine.setBaseNumberOfUnlockedRectangles(
					LevelData.getNumberOfUnlockedRectangles( currentLevel, difficulty ) );

			if ( !LevelData.hasSafeArea( currentLevel, difficulty ) )
				screen.getChildren().remove( safeLine );

			primaryStage.setHeight(
					LevelData.getScreenHeight( currentLevel, difficulty ) + 10 * gridCellWidth + heightCalibration );

			resetGrids();

			int numRows = LevelData.numRows( currentLevel, difficulty );
			generateCoins( numRows );
			generateStatusEffectCoins( numRows );
			generateExtraLives( numRows );
			generateTimeBoosts( numRows );
			addPlayerToScreen();
			player.spawn( numColumns * gridCellWidth / 2 - gridCellWidth, background.getHeight() - gridCellWidth );
			timer.play();
		}

		System.gc();
	}

	private void resetGrids() {
		int numRows = LevelData.getScreenHeight( currentLevel, difficulty ) / gridCellWidth();

		verticalBombsGrid = new VerticalBomb[numRows][numColumns];
		horizontalBombsGrid = new HorizontalBomb[numRows][numColumns];
		lasersGrid = new Laser[numRows];
		coinsGrid = new Coin[numRows][numColumns];
		extraLivesGrid = new ExtraLife[numRows][numColumns];
		timeBoostsGrid = new TimeBoost[numRows][numColumns];
		statusEffectCoinsGrid = new StatusEffectCoin[numRows][numColumns];
	}

	private void addLifeToLifebar() {
		ImageView heart;
		if ( !isDarkMode() )
			heart = new ImageView( "file:do-not-touch" + File.separator + "heart.png" );
		else
			heart = new ImageView( "file:do-not-touch" + File.separator + "white heart.png" );
		heart.setFitWidth( 3 * gridCellWidth );
		heart.setFitHeight( 3 * gridCellWidth );
		lifebar.getChildren().add( heart );
		if ( lifeGained.getStatus() == Animation.Status.STOPPED )
			lifeGained.play();
		else if ( lifeGained.getStatus() == Animation.Status.RUNNING ) {
			lifeGained.stop();
			lifeGained.play();
		}
	}

	private void timeBoost( double seconds ) {
		timer.addTime( seconds );
		if ( timeBoosted.getStatus() == Animation.Status.STOPPED )
			timeBoosted.play();
		else if ( timeBoosted.getStatus() == Animation.Status.RUNNING ) {
			timeBoosted.stop();
			timeBoosted.play();
		}
	}

	private void saveCurrentGame( String name, String password ) {
		try {
			DataOutputStream out = new DataOutputStream(
					new BufferedOutputStream( new FileOutputStream( new File( filePath( name ) ) ) ) );
			// Write difficulty, coinMultiplier, currentLevel, and
			// startAndStopTimes
			out.writeInt( difficulty );
			out.writeInt( coinMultiplier );
			out.writeInt( currentLevel );
			out.writeInt( startAndStopTimes.size() );
			for ( int i = 0; i < startAndStopTimes.size(); i++ ) {
				out.writeLong( startAndStopTimes.get( i ) );
			}

			// Write seconds left in timer, number of lives, and current points
			out.writeDouble( timer.getNumberOfSecondsLeft() );
			out.writeInt( lifebar.getChildren().size() );
			out.writeInt( pointsTracker.getCurrentPoints() );

			// Write x, y coordinates and value of each coin
			out.writeInt( coins.size() );
			for ( Coin coin : coins ) {
				out.writeInt( coin.getValue() );
				out.writeInt( coin.row() );
				out.writeInt( coin.column() );
			}

			// Do the same for extra lives
			out.writeInt( extraLives.size() );
			for ( ExtraLife extraLife : extraLives ) {
				out.writeInt( extraLife.row() );
				out.writeInt( extraLife.column() );
			}

			int pepper = (int) ( Math.random() * 100 );
			String hashedPassword = password + pepper;
			int iterations = (int) ( Math.random() * 21 ) + 30;
			for ( int i = 0; i < iterations; i++ ) {
				// Apply hash 30 to 50 times
				hashedPassword = hashPassword( hashedPassword );
			}
			out.writeUTF( hashedPassword );

			// And levels with extra lives
			out.writeInt( levelsWithExtraLives.size() );
			for ( int i : levelsWithExtraLives ) {
				out.writeInt( i );
			}

			// And bombs
			out.writeInt( verticalBombs.size() );
			for ( VerticalBomb bomb : verticalBombs ) {
				out.writeInt( bomb.row() );
				out.writeInt( bomb.column() );
			}

			out.writeInt( horizontalBombs.size() );
			for ( HorizontalBomb bomb : horizontalBombs ) {
				out.writeInt( bomb.row() );
				out.writeInt( bomb.column() );
			}

			// And lasers
			out.writeInt( lasers.size() );
			for ( Laser laser : lasers ) {
				out.writeInt( laser.row() );
				out.writeInt( laser.getStatus() );
				out.writeDouble( laser.getJumpTo() );
			}

			// And status effect coins
			out.writeInt( statusEffectCoins.size() );
			for ( StatusEffectCoin statusEffectCoin : statusEffectCoins ) {
				out.writeInt( statusEffectCoin.row() );
				out.writeInt( statusEffectCoin.column() );
			}

			// And time boosts
			out.writeInt( timeBoosts.size() );
			for ( TimeBoost timeBoost : timeBoosts ) {
				out.writeInt( timeBoost.row() );
				out.writeInt( timeBoost.column() );
			}

			// And each rectangle on player
			out.writeInt( player.size() );
			for ( PlayerRectangle r : player ) {
				out.writeInt( r.row() );
				out.writeInt( r.column() );
			}

			// And finish line
			boolean[] areRectanglesUnlocked = finishLine.getAreRectanglesUnlocked();
			for ( int i = 0; i < areRectanglesUnlocked.length; i++ ) {
				out.writeBoolean( areRectanglesUnlocked[i] );
			}
			if ( finishLine.isUnlocked() )
				out.writeDouble( finishLine.getTimeInShuffle() );

			Timeline[] timelines = { poisonTimeline, timeDilationTimeline, blindnessTimeline, doubleCoinsTimeline,
					timeContractionTimeline, invincibilityTimeline };
			for ( int i = 0; i < 6; i++ ) {
				// Write how much time has elapsed for running status effect
				// timelines
				if ( timelines[i].getStatus() == Animation.Status.PAUSED ) {
					double currentTime = timelines[i].getCurrentTime().toSeconds();
					out.writeBoolean( true );
					out.writeDouble( currentTime );
				}
				else
					out.writeBoolean( false );
			}
			out.close();
		}
		catch ( IOException e ) {
		}

		updateMainMenuContinueGameButton();
	}

	private String hashPassword( String password ) {
		try {
			MessageDigest md = MessageDigest.getInstance( "SHA-256" );
			byte[] bytes = md.digest( password.getBytes() );

			BigInteger number = new BigInteger( bytes );
			String hexString = number.toString( 16 );

			while ( hexString.length() < 32 ) {
				hexString = "0" + hexString;
			}

			return hexString;
		}
		catch ( NoSuchAlgorithmException e ) {
			return null;
		}
	}

	private void playerWins() {
		gameStatus = GameStatus.NOT_RUNNING;
		startAndStopTimes.add( System.currentTimeMillis() );
		movePlayerUp.stop();
		movePlayerLeft.stop();
		movePlayerRight.stop();
		movePlayerDown.stop();

		// Stop everything, and remove timeControl, lifebar, pointsTracker, and levelTracker
		dropAndGenerateBombs.stop();
		generateLasers.stop();
		base.getChildren().remove( hBox );
		timer.pause();
		screen.getChildren().clear();
		player.setAlive( false );

		// Add "Congratulations, you won!!!" to base
		screen.getChildren().add( background );
		// Shrink background to compensate for new items added
		background.setHeight( background.getHeight() - 3.9 * gridCellWidth );
		// TODO
		Text finalResult = new Text( "Congratulations, you won!!!" );
		if ( !isDarkMode() )
			finalResult.setFill( Color.BLUE );
		else
			finalResult.setFill( Color.DEEPSKYBLUE );
		finalResult.setFont( Font.font( "Calibri", FontWeight.BOLD, 4.5 * BuggyRun.gridCellWidth() ) );
		base.getChildren().add( finalResult );

		// Stop everything with time
		checkTimeLeft.stop();
		Text timeElapsed = new Text( secondsElapsedToString() + " Good game!" );
		if ( isDarkMode() )
			timeElapsed.setFill( Color.WHITE );

		timeElapsed.setFont( Font.font( "Calibri", FontWeight.EXTRA_BOLD, 1.6 * BuggyRun.gridCellWidth() ) );
		base.getChildren().add( timeElapsed );

		// Load leaderboard, and add pending entry to it
		leaderboard = new Leaderboard( difficulty, isDarkMode() );
		screen.getChildren().add( leaderboard );
		leaderboard.updateDisplay();
		PendingLeaderboardEntry pendingLeaderboardEntry = new PendingLeaderboardEntry( secondsElapsed() );
		boolean success = leaderboard.addPendingEntry( pendingLeaderboardEntry );

		NameInputBox nameInputBox = new NameInputBox( background, loadSavedGameLogin.getName() );
		nameInputBox.getOk().setOnAction( e -> {
			String name = nameInputBox.getName();
			if ( name.startsWith( "^^^" ) && name.endsWith( "^^^" ) ) {
				name = name.substring( 3, name.length() - 3 ).trim();
				if ( name.length() > 0 ) {
					pendingLeaderboardEntry.setName( name );
					pendingLeaderboardEntry.removeTimestamp();
					leaderboard.replacePendingEntryWithEntry( name );
					screen.getChildren().remove( addYourName );
					screen.getChildren().remove( nameInputBox );
				}
			}

			name = name.trim();
			if ( !name.equals( "" ) ) {
				leaderboard.replacePendingEntryWithEntry( name );
				screen.getChildren().remove( addYourName );
				screen.getChildren().remove( nameInputBox );
			}
		} );

		nameInputBox.getCancel().setOnAction( e -> {
			screen.getChildren().remove( nameInputBox );
		} );

//		if ( leaderboard.isCorrupted() ) {
//			Text leaderboardIsCorrupted = new Text( "Oops! Leaderboard data is corrupted, so you won't be able to save your data." );
//			leaderboardIsCorrupted.setX( 2 * BuggyRun.gridCellWidth() );
//			leaderboardIsCorrupted.setY( background.getHeight() - 2 * gridCellWidth );
//			leaderboardIsCorrupted.setFont( Font.font( "Calibri", FontWeight.BOLD, 1.8 * gridCellWidth ) );
//			leaderboardIsCorrupted.setFill( Color.RED );
//			screen.getChildren().add( leaderboardIsCorrupted );
//		}
		if ( !success ) {
			Text sorry = new Text(
					"Sorry that you couldn't make it to the front of the leaderboard. Better luck next time!" );
			sorry.setX( 2 * gridCellWidth );
			sorry.setY( background.getHeight() - 1.5 * gridCellWidth );
			sorry.setWrappingWidth( numColumns * gridCellWidth - 10 * gridCellWidth );
			sorry.setFont( Font.font( "Calibri", FontWeight.BOLD, 1.8 * gridCellWidth ) );
			sorry.setFill( Color.RED );
			screen.getChildren().add( sorry );
		}
		else {
			// If leaderboard is not corrupted, give option to add name
			addYourName = new Button( "Add your name!" );
			addYourName.setFont( Font.font( "Calibri", FontWeight.BOLD, 2 * gridCellWidth ) );
			addYourName.setTranslateX( 2 * gridCellWidth );
			addYourName.setTranslateY( background.getHeight() - 6 * gridCellWidth );
			screen.getChildren().add( addYourName );
			// Clicking addYourName pulls up nameInputbox
			addYourName.setOnAction( e -> {
				if ( !screen.getChildren().contains( nameInputBox ) )
					screen.getChildren().add( nameInputBox );
			} );
		}

		timeControl.getChildren().remove( pauseResume );
	}

	// Converts from seconds to "x hours, x minutes, x seconds elapsed"
	private String secondsElapsedToString() {
		double seconds = secondsElapsed();
		int hours = (int) ( seconds / 3600 );
		seconds -= 3600 * hours;
		seconds = Double.parseDouble( String.format( "%.1f", seconds ) );
		int minutes = (int) ( seconds / 60 );
		seconds -= 60 * minutes;
		seconds = Double.parseDouble( String.format( "%.1f", seconds ) );

		String out = "";
		if ( hours > 0 ) {
			String hour;
			if ( hours != 1 )
				hour = hours + " hours";
			else
				hour = "1 hour";
			out += hour;
		}
		if ( minutes > 0 ) {
			if ( out.length() > 0 )
				out += ", ";
			String minute;
			if ( minutes != 1 )
				minute = minutes + " minutes";
			else
				minute = "1 minute";
			out += minute;
		}
		if ( seconds > 0 ) {
			if ( out.length() > 0 )
				out += ", ";
			String second;
			if ( seconds != 1 )
				second = seconds + " seconds";
			else
				second = "1 second";
			second = second.replaceAll( ".0 ", " " );
			out += second;
		}

		return out + " elapsed.";
	}

	private double secondsElapsed() {
		if ( startAndStopTimes.size() % 2 == 1 ) {
			ArrayList<Long> temp = new ArrayList<Long>();
			for ( Long time : startAndStopTimes )
				temp.add( time );
			temp.add( System.currentTimeMillis() );

			double secondsElapsed = 0;
			for ( int i = 0; i < temp.size(); i++ ) {
				double startTime = temp.get( i );
				i++;
				double endTime = temp.get( i );
				secondsElapsed += ( endTime - startTime ) / 1000;
			}

			secondsElapsed = Double.parseDouble( String.format( "%.1f", secondsElapsed ) );
			return secondsElapsed;
		}

		double secondsElapsed = 0;
		for ( int i = 0; i < startAndStopTimes.size(); i++ ) {
			double startTime = startAndStopTimes.get( i );
			i++;
			double endTime = startAndStopTimes.get( i );
			secondsElapsed += ( endTime - startTime ) / 1000;
		}

		secondsElapsed = Double.parseDouble( String.format( "%.1f", secondsElapsed ) );
		return secondsElapsed;
	}

	private boolean isDarkMode() {
		return settingsPane.isDarkModeSelected();
	}

	public Color playerColor() {
		return settingsPane.getColor();
	}

	public static int gridCellWidth() {
		return gridCellWidth;
	}

	public static int numColumns() {
		return numColumns;
	}

	public static int numLevels() {
		return numLevels;
	}

	public static double getWidthCalibration() {
		return widthCalibration;
	}

	public static double getHeightCalibration() {
		return heightCalibration;
	}

	@SuppressWarnings( "static-access" )
	public void setGridCellWidth( int gridCellWidth ) {
		if ( gridCellWidth <= 0 )
			throw new IllegalArgumentException();
		this.gridCellWidth = gridCellWidth;
		Paint origColor = settingsPane.getPaneBackground().getFill();

		base = new VBox( 0.8 * gridCellWidth );
		base.setAlignment( Pos.TOP_CENTER );

		setupPreGamePanes();
		settingsPane.getPaneBackground().setFill( origColor );

		Scene scene = new Scene( base );
		primaryStage.setScene( scene );
		primaryStage.setWidth( numColumns * gridCellWidth + widthCalibration );
		primaryStage.setHeight( mainMenu.getPaneBackground().getHeight() + heightCalibration );
		primaryStage.setResizable( false );

		seeMe1.setFont( Font.font( "Calibri", FontWeight.BOLD, 1.5 * gridCellWidth ) );
		seeMe1.setWrappingWidth( gridCellWidth * numColumns );
		seeMe2.setFont( Font.font( "Calibri", FontWeight.BOLD, 1.5 * gridCellWidth ) );
		seeMe2.setWrappingWidth( gridCellWidth * numColumns );
		seeMe3.setFont( Font.font( "Calibri", FontWeight.BOLD, 1.5 * gridCellWidth ) );
		seeMe3.setWrappingWidth( gridCellWidth * numColumns );

		gameStatus = GameStatus.NOT_RUNNING;
		setupKeyInput();
		mainMenu.getSettings().fire();
	}

	public static void main( String[] args ) {
		launch( args );
	}

}