package com.re.paas.internal.i18n.geonames;

public class Cities1000Features {

	 // fipscode (subject to change to iso code), isocode for the
    // us and ch, see file admin1Codes.txt for display names of this code;
    // varchar(20
    public final static int ADMIN1 = 10;
    // code for the second administrative division, a county in the US, see file
    // admin2Codes.txt;
    // varchar(80)
    public final static int ADMIN2 = 11;
    // code for third level administrative division, varchar(20)
    public final static int ADMIN3 = 12;
    // code for fourth level administrative division, varchar(20)
    public final static int ADMIN4 = 13;
    // comma separated varchar(4000)
    public final static int ALTERNATE_NAMES = 3;
    // name of geographical point in plain ascii characters, varchar(200)
    public final static int ASCIINAME = 2;
    // alternate country codes, comma separated, ISO-3166 2-letter country code,
    // 60 characters
    public final static int CC2 = 9;
    // ISO-3166 2-letter country code, 2 characters
    public final static int COUNTRY_CODE = 8;
    // elevation : in meters, integer
    public final static int ELEVATION = 15;
    // see http://www.geonames.org/export/codes.html, char(1)
    public final static int FEATURE_CLASS = 6;
    // see http://www.geonames.org/export/codes.html, varchar(10)
    public final static int FEATURE_CODE = 7;
    // integer id of record in geonames database
    public final static int GEONAMEID = 0;
    // average elevation of 30'x30' (ca 900mx900m) area in meters, integer
    public final static int GTOPO30 = 16;
    // latitude in decimal degrees (wgs84)
    public final static int LATITUDE = 4;
    // longitude in decimal degrees (wgs84)
    public final static int LONGITUDE = 5;
    // modification date : date of last modification in yyyy-MM-dd format
    public final static int MODIFICATION_DATE = 18;

    // name of geographical point (utf8) varchar(200)
    public final static int NAME = 1;

    // integer
    public final static int POPULATION = 14;

    // timezone : the timezone id (see file timeZone.txt)
    public final static int TIMEZONE = 17;
	
}
