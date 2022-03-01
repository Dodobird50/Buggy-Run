package post_game_things;

import java.io.Serializable;
import java.util.Calendar;

public class LeaderboardEntry implements Comparable<LeaderboardEntry>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private double numberOfSeconds;

	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;

	public LeaderboardEntry( String name, double numberOfSeconds ) {
		this.name = name;
		this.numberOfSeconds = numberOfSeconds;

		Calendar calendar = Calendar.getInstance();
		year = calendar.get( Calendar.YEAR );
		month = calendar.get( Calendar.MONTH ) + 1;
		day = calendar.get( Calendar.DAY_OF_MONTH );
		hour = calendar.get( Calendar.HOUR_OF_DAY );
		minute = calendar.get( Calendar.MINUTE );
	}

	public LeaderboardEntry( String name, double numberOfSeconds, int year, int month, int day, int hour, int minute ) {
		this.name = name;
		this.numberOfSeconds = numberOfSeconds;
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
	}

	public String dateStringForm() {
		String out = month + "/" + day + "/" + year + " " + hour;
		if ( minute >= 10 )
			out += ":" + minute;
		else
			out += ":0" + minute;

		return out;
	}

	public String timeStringForm() {
		double seconds = numberOfSeconds;
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

		return out;
	}

	@Override
	public int compareTo( LeaderboardEntry other ) {
		if ( numberOfSeconds < other.numberOfSeconds )
			return -1;
		else if ( numberOfSeconds > other.numberOfSeconds )
			return 1;
		else
			return 0;
	}

	@Override
	public boolean equals( Object o ) {
		if ( o instanceof LeaderboardEntry ) {
			LeaderboardEntry temp = (LeaderboardEntry) o;
			return compareTo( temp ) == 0;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (int) ( Math.pow( numberOfSeconds, Math.PI ) / name.hashCode() );
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public double getNumberOfSeconds() {
		return numberOfSeconds;
	}

	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public int getDay() {
		return day;
	}

	public int getHour() {
		return hour;
	}

	public int getMinute() {
		return minute;
	}

	public void removeTimestamp() {
		year = 0;
		month = 0;
		day = 0;
		hour = 0;
		minute = 0;
	}

	@Override
	public String toString() {
		return name + " " + numberOfSeconds + " " + year + " " + month + " " + day + " " + hour + " " + minute;
	}
}