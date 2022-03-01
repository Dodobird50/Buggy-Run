package main;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import post_game_things.Leaderboard;
import post_game_things.LeaderboardEntry;

public class DataFix {
	public static void main( String[] args ) {

		double[] numbers = new double[] { 532.8, 758.3, 947.2 };
		for ( int i = 0; i < 3; i++ ) {
			File file = Leaderboard.file( i );
			LeaderboardEntry entry = new LeaderboardEntry( "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWw", numbers[i], 0, 0, 0, 0, 0 );

			try {
				DataOutputStream out = new DataOutputStream( new BufferedOutputStream( new FileOutputStream( file ) ) );
				out.writeLong( System.currentTimeMillis() );
				out.writeUTF( entry.getName() );
				out.writeDouble( entry.getNumberOfSeconds() );
				out.writeInt( entry.getYear() );
				out.writeInt( entry.getMonth() );
				out.writeInt( entry.getDay() );
				out.writeInt( entry.getHour() );
				out.writeInt( entry.getMinute() );
				out.close();
			}
			catch ( IOException e ) {
			}
		}

		System.out.println( "Done!" );
	}
}
