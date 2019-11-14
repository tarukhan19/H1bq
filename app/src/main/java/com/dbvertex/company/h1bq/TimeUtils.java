package com.dbvertex.company.h1bq;

public class TimeUtils {
    private TimeUtils() {
    }


        private static void appendTimeAndUnit(StringBuffer timeBuf, long time, String unit) {
            if (time < 1) {
                return;
            }

            timeBuf.append(time);
            timeBuf.append(unit);
        }

        private static void prependTimeAndUnit(StringBuffer timeBuf, long time, String unit) {
            if (time < 1) {
                return;
            }

            if (timeBuf.length() > 0) {
                timeBuf.insert(0, " ");
            }

            timeBuf.insert(0, unit);
            timeBuf.insert(0, time);
        }

        /**
         * Provide the Millisecond time value in {year}y {day}d {hour}h {minute}m {second}s {millisecond}ms. <br>
         * Omitted if there is no value for that unit.
         *
         * @param timeInMillis
         * @return
         *
         * @since 2018. 1. 9.
         */
        public static String toYYYYHHmmssS(long timeInMillis)
        {

            if (timeInMillis < 1) {
                return "0 ms";
            }

            StringBuffer timeBuf = new StringBuffer();

            long millis = timeInMillis % 1000;
            appendTimeAndUnit(timeBuf, millis, "ms");

            // second (1000ms) & above
            long time = timeInMillis / 1000;
            if (time < 1) {
               //Log.e("timeBuf.toString()",timeBuf.toString()+"");
                return timeBuf.toString();
            }

            long seconds = time % 60;
            prependTimeAndUnit(timeBuf, seconds, "s");

            // minute(60s) & above
            time = time / 60;
            if (time < 1) {
                //Log.e("timeBuf.toString()",timeBuf.toString()+"");

                return timeBuf.toString();
            }

            long minutes = time % 60;
            prependTimeAndUnit(timeBuf, minutes, "m");

            // hour(60m) & above
            time = time / 60;
            if (time < 1) {
                //Log.e("timeBuf.toString()",timeBuf.toString()+"");

                return timeBuf.toString();
            }

            long hours = time % 24;
            prependTimeAndUnit(timeBuf, hours, "h");

            // day(24h) & above
            time = time / 24;
            if (time < 1) {
                //Log.e("timeBuf.toString()",timeBuf.toString()+"");

                return timeBuf.toString();
            }

            long day = time % 365;
            prependTimeAndUnit(timeBuf, day, "d");

            // year(365d) ...
            time = time / 365;
            if (time < 1) {
                //Log.e("timeBuf.toString()",timeBuf.toString()+"");

                return timeBuf.toString();
            }

            prependTimeAndUnit(timeBuf, time, "y");
            //Log.e("timeBuf.toString()",timeBuf.toString()+"");

            return timeBuf.toString();

        }    }