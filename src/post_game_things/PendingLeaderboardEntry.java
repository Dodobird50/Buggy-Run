package post_game_things;

import java.io.Serializable;

public class PendingLeaderboardEntry extends LeaderboardEntry implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PendingLeaderboardEntry( double numberOfSeconds ) {
		super( "You", numberOfSeconds );
	}

	public PendingLeaderboardEntry( String name, double numberOfSeconds, int year, int month, int day, int hour,
			int minute ) {
		super( "You", numberOfSeconds, year, month, day, hour, minute );
	}

	public LeaderboardEntry convertToEntry( String name ) {
		return new LeaderboardEntry( name, getNumberOfSeconds(), getYear(), getMonth(), getDay(), getHour(), 
				getMinute() );
	}
}