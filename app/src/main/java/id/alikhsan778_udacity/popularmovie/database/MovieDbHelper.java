package id.alikhsan778_udacity.popularmovie.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import id.alikhsan778_udacity.popularmovie.database.MovieContract.MovieEntry;
import id.alikhsan778_udacity.popularmovie.database.MovieContract.MovieTrailerEntry;
import id.alikhsan778_udacity.popularmovie.database.MovieContract.MovieReviewEntry;
import id.alikhsan778_udacity.popularmovie.database.MovieContract.MovieFavoriteEntry;



public class MovieDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";
    private static final String INTEGER_TYPE = " INTEGER ";
    private static final String TEXT_TYPE = " TEXT ";
    private static final String COMMA_SEP = " , ";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                    MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MovieEntry.COLUMN_NAME_ID + INTEGER_TYPE + COMMA_SEP +
                    MovieEntry.COLUMN_NAME_ORIGINAL_TITLE + TEXT_TYPE + COMMA_SEP +
                    MovieEntry.COLUMN_NAME_IMAGE_THUMBNAIL + TEXT_TYPE + COMMA_SEP +
                    MovieEntry.COLUMN_NAME_PLOT_SYNOPSIS + TEXT_TYPE + COMMA_SEP +
                    MovieEntry.COLUMN_NAME_VOTE_AVERAGE + TEXT_TYPE + COMMA_SEP +
                    MovieEntry.COLUMN_NAME_RELEASE_DATE + TEXT_TYPE + " );";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME;

    private static final String SQL_CREATE_ENTRIES_VIDEOS =
            "CREATE TABLE " + MovieTrailerEntry.TABLE_NAME + " (" +
                    MovieTrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MovieTrailerEntry.COLUMN_NAME_MOVIE_KEY + INTEGER_TYPE + COMMA_SEP +
                    MovieTrailerEntry.COLUMN_NAME_TRAILER_KEY + TEXT_TYPE + COMMA_SEP +
                    " FOREIGN KEY(" + MovieTrailerEntry.COLUMN_NAME_MOVIE_KEY + ") REFERENCES " +
                    MovieEntry.TABLE_NAME  + "(" + MovieEntry._ID + ") " +
                    " );";

    private static final String SQL_DELETE_ENTRIES_VIDEOS =
            "DROP TABLE IF EXISTS " + MovieTrailerEntry.TABLE_NAME;

    private static final String SQL_CREATE_ENTRIES_REVIEWS =
            "CREATE TABLE " + MovieReviewEntry.TABLE_NAME + " (" +
                    MovieReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MovieReviewEntry.COLUMN_NAME_MOVIE_KEY + INTEGER_TYPE + COMMA_SEP +
                    MovieReviewEntry.COLUMN_NAME_AUTHOR + TEXT_TYPE + COMMA_SEP +
                    MovieReviewEntry.COLUMN_NAME_CONTENT + TEXT_TYPE + COMMA_SEP +
                    " FOREIGN KEY(" + MovieReviewEntry.COLUMN_NAME_MOVIE_KEY + ") REFERENCES " +
                    MovieEntry.TABLE_NAME  + "(" + MovieEntry._ID + ") " +
                    " );";

    private static final String SQL_DELETE_ENTRIES_REVIEWS =
            "DROP TABLE IF EXISTS " + MovieReviewEntry.TABLE_NAME;

    private static final String SQL_CREATE_ENTRIES_FAVORITE =
            "CREATE TABLE " + MovieFavoriteEntry.TABLE_NAME + " (" +
                    MovieFavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MovieFavoriteEntry.COLUMN_NAME_ID + INTEGER_TYPE + COMMA_SEP +
                    MovieFavoriteEntry.COLUMN_NAME_ORIGINAL_TITLE + TEXT_TYPE + COMMA_SEP +
                    MovieFavoriteEntry.COLUMN_NAME_IMAGE_THUMBNAIL + TEXT_TYPE + COMMA_SEP +
                    MovieFavoriteEntry.COLUMN_NAME_PLOT_SYNOPSIS + TEXT_TYPE + COMMA_SEP +
                    MovieFavoriteEntry.COLUMN_NAME_VOTE_AVERAGE + TEXT_TYPE + COMMA_SEP +
                    MovieFavoriteEntry.COLUMN_NAME_RELEASE_DATE + TEXT_TYPE + " );";

    private static final String SQL_DELETE_ENTRIES_FAVORITE =
            "DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES_VIDEOS);
        db.execSQL(SQL_CREATE_ENTRIES_REVIEWS);
        db.execSQL(SQL_CREATE_ENTRIES_FAVORITE);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_DELETE_ENTRIES_VIDEOS);
        db.execSQL(SQL_DELETE_ENTRIES_REVIEWS);
        db.execSQL(SQL_DELETE_ENTRIES_FAVORITE);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
