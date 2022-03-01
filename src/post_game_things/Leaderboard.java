package post_game_things;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import main.BuggyRun;

import java.io.*;
import javafx.geometry.Insets;
import java.util.ArrayList;
import java.util.Collections;
import javafx.scene.layout.GridPane;

public class Leaderboard extends GridPane {
	private ArrayList<LeaderboardEntry> leaderboardEntries;
	private File file;
//	private boolean isCorrupted;
	private final int numberOfSpots;
	private boolean isDarkMode;

	// Major difference from other panes: leaderboard adds itself to its parent node
	public Leaderboard( int difficulty, boolean isDarkMode ) {
		setHgap( 1.5 * BuggyRun.gridCellWidth() );
		setVgap( 1.2 * BuggyRun.gridCellWidth() );
		setPadding( new Insets( 2 * BuggyRun.gridCellWidth() ) );
		this.isDarkMode = isDarkMode;

		numberOfSpots = 21;

		file = file( difficulty );
		DataInputStream in = null;
		try {
			in = new DataInputStream( new BufferedInputStream( new FileInputStream( file ) ) );
		}
		catch ( IOException e ) {
			leaderboardEntries = new ArrayList<LeaderboardEntry>();
			updateDisplay();
//			isCorrupted = true;
			return;
		}

		leaderboardEntries = new ArrayList<LeaderboardEntry>();
		try {
			double lastModified = in.readLong();
			if ( Math.abs( lastModified - file.lastModified() ) > 1000 ) {
				in.close();
				throw new Exception();
			}

			while ( true ) {
				String name = in.readUTF();
				double numberOfSeconds = in.readDouble();
				int year = in.readInt();
				int month = in.readInt();
				int day = in.readInt();
				int hour = in.readInt();
				int minute = in.readInt();
				leaderboardEntries.add( new LeaderboardEntry( name, numberOfSeconds, year, month, day, hour, minute ) );
			}
		}
		catch ( EOFException ignore ) {
		}
		catch ( Exception e ) {
//			isCorrupted = true;
			leaderboardEntries.clear();
		}

		Collections.sort( leaderboardEntries );
		updateDisplay();
	}

	public void updateDisplay() {
		getChildren().clear();
		Text rankHeader = new Text( "Rank:   " );
		setDefaultFont( rankHeader, null, true );
		Text nameHeader = new Text( "Name:" );
		setDefaultFont( nameHeader, null, true );
		nameHeader.setWrappingWidth( 18 * BuggyRun.gridCellWidth() );
		Text dateHeader = new Text( "Date:" );
		dateHeader.setWrappingWidth( 18 * BuggyRun.gridCellWidth() );
		setDefaultFont( dateHeader, null, true );
		Text timeHeader = new Text( "Time:" );
		setDefaultFont( timeHeader, null, true );

		add( rankHeader, 0, 0 );
		add( nameHeader, 1, 0 );
		add( dateHeader, 2, 0 );
		add( timeHeader, 3, 0 );
		if ( isDarkMode ) {
			rankHeader.setFill( Color.WHITE );
			nameHeader.setFill( Color.WHITE );
			dateHeader.setFill( Color.WHITE );
			timeHeader.setFill( Color.WHITE );
		}

		int rank = 1;
		int pos = 1;
		Collections.sort( leaderboardEntries );
		for ( int i = 0; i < leaderboardEntries.size() && pos <= numberOfSpots; i++ ) {
			LeaderboardEntry leaderboardEntry = leaderboardEntries.get( i );
			if ( pos > numberOfSpots )
				leaderboardEntries.remove( leaderboardEntry );

			if ( i > 0 && leaderboardEntry.compareTo( leaderboardEntries.get( i - 1 ) ) == 0 )
				rank--;

			String output = rank + "";
			Text text = new Text( output );
			setDefaultFont( text, leaderboardEntry, false );

			add( text, 0, pos );

			if ( leaderboardEntry instanceof PendingLeaderboardEntry )
				output = "You";
			else
				output = leaderboardEntry.getName();
			text = new Text( output );
			setDefaultFont( text, leaderboardEntry, false );
			add( text, 1, pos );
			
			double width = text.computeAreaInScreen() / ( 1.6 * BuggyRun.gridCellWidth() );
			if ( width > 17 * BuggyRun.gridCellWidth() && output.length() > 0 ) {
				text.setText( output + "..." );
				while ( width > 17 * BuggyRun.gridCellWidth() && output.length() > 0 ) {
					output = output.substring( 0, output.length() - 1 );
					text.setText( output + "..." );
					width = text.computeAreaInScreen() / ( 1.6 * BuggyRun.gridCellWidth() );
				}
			}

			text = new Text( leaderboardEntry.dateStringForm() );
			setDefaultFont( text, leaderboardEntry, false );
			add( text, 2, pos );

			text = new Text( leaderboardEntry.timeStringForm() );
			setDefaultFont( text, leaderboardEntry, false );
			add( text, 3, pos );

			++rank;
			++pos;
		}
	}

	public void updateFile() {
		try {
			DataOutputStream out = new DataOutputStream( new BufferedOutputStream( new FileOutputStream( file ) ) );
			out.writeLong( System.currentTimeMillis() );
			for ( LeaderboardEntry leaderboardEntry : leaderboardEntries ) {
				if ( leaderboardEntry instanceof PendingLeaderboardEntry )
					continue;

				out.writeUTF( leaderboardEntry.getName() );
				out.writeDouble( leaderboardEntry.getNumberOfSeconds() );
				out.writeInt( leaderboardEntry.getYear() );
				out.writeInt( leaderboardEntry.getMonth() );
				out.writeInt( leaderboardEntry.getDay() );
				out.writeInt( leaderboardEntry.getHour() );
				out.writeInt( leaderboardEntry.getMinute() );
			}
			out.close();
		}
		catch ( IOException e ) {
		}
	}

	public boolean addPendingEntry( PendingLeaderboardEntry pendingLeaderboardEntry ) {
		leaderboardEntries.add( pendingLeaderboardEntry );
		Collections.sort( leaderboardEntries );
		updateDisplay();
		// Do not update file

		return leaderboardEntries.indexOf( pendingLeaderboardEntry ) < numberOfSpots;
	}

	public void removeEntry( LeaderboardEntry leaderboardEntry ) {
		leaderboardEntries.remove( leaderboardEntry );
		updateDisplay();
		updateFile();
	}

	public void replacePendingEntryWithEntry( String name ) {
		PendingLeaderboardEntry pendingLeaderboardEntry = null;
		for ( LeaderboardEntry e : leaderboardEntries ) {
			if ( e instanceof PendingLeaderboardEntry ) {
				pendingLeaderboardEntry = (PendingLeaderboardEntry) e;
				break;
			}
		}
		if ( pendingLeaderboardEntry != null ) {
			leaderboardEntries.remove( pendingLeaderboardEntry );
			leaderboardEntries.add( pendingLeaderboardEntry.convertToEntry( name ) );
			Collections.sort( leaderboardEntries );
			updateDisplay();
			updateFile();
		}
	}

	private void setDefaultFont( Text text, LeaderboardEntry leaderboardEntry, boolean isHeader ) {
		if ( !isHeader )
			text.setFont( Font.font( "Calibri", FontWeight.BOLD, 1.6 * BuggyRun.gridCellWidth() ) );
		else {
			text.setFont( Font.font( "Calibri", FontWeight.BOLD, 2 * BuggyRun.gridCellWidth() ) );
			return;
		}

		if ( leaderboardEntry instanceof PendingLeaderboardEntry ) {
			if ( isDarkMode )
				text.setFill( Color.LIME );
			else
				text.setFill( Color.GREEN );
		}
		else {
			if ( isDarkMode )
				text.setFill( Color.WHITE );
		}
	}

	public int getNumberOfRows() {
		return leaderboardEntries.size() - 1;
	}

//	public boolean isCorrupted() {
//		return isCorrupted;
//	}

//	public static boolean isLeaderboardFileCorrupted( int difficulty ) {
//		File file = file( difficulty );
//
//		if ( !file.exists() ) {
//			try {
//				file.createNewFile();
//			}
//			catch ( IOException ex ) {
//			}
//		}
//
//		DataInputStream in = null;
//		try {
//			in = new DataInputStream( new BufferedInputStream( new FileInputStream( file ) ) );
//		}
//		catch ( IOException e ) {
//			return true;
//		}
//
//		try {
//			double lastModified = in.readDouble();
//			if ( Math.abs( lastModified - file.lastModified() ) > 1000 ) {
//				in.close();
//				return true;
//			}
//		}
//		catch ( Exception e ) {
//			return true;
//		}
//
//		try {
//			while ( true ) {
//				Object o = in.readObject();
//				if ( !( o instanceof LeaderboardEntry ) ) {
//					in.close();
//					return true;
//				}
//			}
//		}
//		catch ( EOFException e ) {
//			return false;
//		}
//		catch ( Exception e ) {
//			return true;
//		}
//	}

	public static void resetLeaderboard( int difficulty ) {
		File file = file( difficulty );

		try {
			DataOutputStream out = new DataOutputStream( new BufferedOutputStream( new FileOutputStream( file ) ) );
			out.writeLong( System.currentTimeMillis() );
			out.close();
		}
		catch ( IOException ex ) {
		}
	}

//	public static boolean isLeaderboardFileEmpty( int difficulty ) {
//		File file = file( difficulty );
//
////		if ( isLeaderboardFileCorrupted( difficulty ) )
////			return false;
//
//		try {
//			DataInputStream in = new DataInputStream( new BufferedInputStream( new FileInputStream( file ) ) );
//			in.readDouble();
//			in.readObject();
//			in.close();
//		}
//		catch ( EOFException e ) {
//			return true;
//		}
//		catch ( Exception e ) {
//			return false;
//		}
//
//		return false;
//	}

	public static String filePath( int difficulty ) {
		if ( difficulty == 0 )
			return "do-not-touch" + File.separator + "easyleaderboard.dat";
		else if ( difficulty == 1 )
			return "do-not-touch" + File.separator + "normalleaderboard.dat";
		else if ( difficulty == 2 )
			return "do-not-touch" + File.separator + "hardleaderboard.dat";
		else
			throw new IllegalArgumentException();
	}

	public static File file( int difficulty ) {
		File file = new File( filePath( difficulty ) );
		if ( !file.exists() )
			try {
				file.createNewFile();
				DataOutputStream out = new DataOutputStream( new BufferedOutputStream( new FileOutputStream( file ) ) );
				out.writeLong( System.currentTimeMillis() );
				out.close();
			}
			catch ( IOException e ) {
				e.printStackTrace();
			}
		return file;
	}
}