package com.appisoft.iperkz.util;


import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class DateUtils {
    public static String getSQLDateString(String inputDateString) {
		SimpleDateFormat sourceFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date date = null;
		try {
			date = sourceFormatter.parse(inputDateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Calendar cal = Calendar.getInstance();
        TimeZone timeZone = TimeZone.getTimeZone("US/Eastern");

		cal.setTimeZone(timeZone);
        cal.setTime(date);
        SimpleDateFormat finalFormmater = new SimpleDateFormat("h:mm a");
        String dateStr = finalFormmater.format(cal.getTime());
        /*
        String dateStr = getHours(cal) + ":" + cal.get(Calendar.MINUTE)
                    + " "
                    + ((cal.get(Calendar.AM_PM) == Calendar.AM)?"AM": "PM");

         */
        return dateStr;
    }

    public static String getSQLDateAsString(String inputDateString) {
        SimpleDateFormat sourceFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = null;
        try {
            date = sourceFormatter.parse(inputDateString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        TimeZone timeZone = TimeZone.getTimeZone("US/Eastern");

        cal.setTimeZone(timeZone);
        cal.setTime(date);


        SimpleDateFormat finalFormmater = new SimpleDateFormat("MMM d, yyyy, K:mm a");
        String dateStr = finalFormmater.format(date);
        /*
        String dateStr = getHours(cal) + ":" + cal.get(Calendar.MINUTE)
                + " "
                + ((cal.get(Calendar.AM_PM) == Calendar.AM)?"AM": "PM");

         */
        return dateStr;
    }

    private static String getHours(Calendar cal) {
        if (cal == null) {
            return "";
        }
        if ( cal.get(Calendar.HOUR ) == 0 ) {
            return "12";
        }
        return  ( (cal.get(Calendar.HOUR)) + "");
    }

    public static String getTime(Timestamp timestamp) {
        SimpleDateFormat format = new SimpleDateFormat("h:mm a");
        Calendar cal =Calendar.getInstance();
        cal.setTimeInMillis(timestamp.getTime());
        return format.format(cal.getTime());
    }

    public static String getCurrentDateAndTime() {
        TimeZone timeZone = TimeZone.getTimeZone("US/Eastern");
        Calendar cal = Calendar.getInstance(timeZone);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        String todaysDateStr = format.format(cal.getTime());
        return todaysDateStr;
    }

    public static String getFormattedDateAndTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        String todaysDateStr = format.format(date);
        return todaysDateStr;
    }

    public static  Date incrementDateByOne(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);
        Date nextDate = c.getTime();
        return nextDate;
    }

    public static  boolean isPastCutOffTime(String cuttOffString) {
        if (cuttOffString == null) {
            return false;
        }
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        String[] cutOffDetails = cuttOffString.split(":");
        int cutOffHour = Integer.parseInt(cutOffDetails[0]);
        int cutOffMinute = Integer.parseInt(cutOffDetails[1]);
        System.out.println ("NATHAN ::: cuttoff Hour : " + cutOffHour);
        System.out.println ("NATHAN ::: cuttoff Minute : " + cutOffMinute);
        System.out.println ("NATHAN :::  Hour : " + hour);
        System.out.println ("NATHAN :::  Minute : " + minute);

        if (cutOffHour < hour ) {
            return true;
        }
        if (cutOffHour > hour ) {
            return false;
        }
        if (cutOffHour == hour) {
            if (cutOffMinute > minute) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public static Date addMonthByOne(Date date, int months)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, months); //minus number would decrement the days
        return cal.getTime();
    }
}
