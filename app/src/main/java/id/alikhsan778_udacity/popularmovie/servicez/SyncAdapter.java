package id.alikhsan778_udacity.popularmovie.servicez;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.util.List;
import java.util.Vector;

import id.alikhsan778_udacity.popularmovie.Constant;
import id.alikhsan778_udacity.popularmovie.Movies;
import id.alikhsan778_udacity.popularmovie.MoviesResponse;
import id.alikhsan778_udacity.popularmovie.ReviewResponse;
import id.alikhsan778_udacity.popularmovie.Reviews;
import id.alikhsan778_udacity.popularmovie.TrailerResponse;
import id.alikhsan778_udacity.popularmovie.Trailers;
import id.alikhsan778_udacity.popularmovie.apicall.InAPIMovieCall;
import id.alikhsan778_udacity.popularmovie.apicall.APIMovieCall;
import id.alikhsan778_udacity.popularmovie.database.MovieContract;
import retrofit2.Call;


public class SyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String TAG = SyncAdapter.class.getSimpleName();
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    ContentResolver mContentResolver;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(TAG, "Starting sync");
        if (extras != null && extras.containsKey(Constant.KEY_ACTION)) {
            int action = extras.getInt(Constant.KEY_ACTION);
            if (action == Constant.USED_INT_PARAM_POPULAR)
                handleActionGetPopularMovies();
            else if (action == Constant.USED_INT_PARAM_RATED)
                handleActionGetTopRatedMovies();
            else if (action == Constant.USED_INT_PARAM_DETAIL) {
                if (extras.containsKey(Constant.KEY_ID) && extras.containsKey(Constant.KEY_URI)) {
                    int id = extras.getInt(Constant.KEY_ID);
                    String sUri = extras.getString(Constant.KEY_URI);
                    Uri uri = Uri.parse(sUri);
                    actionGetTrailer(id, uri);
                    actionGetReview(id, uri);
                }
            }
        } else handleActionGetPopularMovies();

    }

    public static void syncImmediately(Context context, int action, int id, Uri uri) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        if (action != Constant.UNUSED_INT_PARAM) bundle.putInt(Constant.KEY_ACTION, action);
        if (id != Constant.UNUSED_INT_PARAM) bundle.putInt(Constant.KEY_ID, id);
        if (uri != null) bundle.putString(Constant.KEY_URI, uri.toString());
        ContentResolver.requestSync(getSyncAccount(context),
                "id.alikhsan778_udacity.popularmovie.AUTHORITY", bundle);
    }

    public static Account getSyncAccount(Context context) {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount = new Account("SyncMovies", "id.alikhsan778_udacity.popularmovie");
        if (null == accountManager.getPassword(newAccount)) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        SyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, "id.alikhsan778_udacity.popularmovie.AUTHORITY", true);
        syncImmediately(context, Constant.UNUSED_INT_PARAM, Constant.UNUSED_INT_PARAM, null);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = "id.alikhsan778_udacity.popularmovie.AUTHORITY";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }
    private void handleActionGetPopularMovies() {
        InAPIMovieCall callMovieAPI = APIMovieCall.getCalledMoviesAPI();
        Call<MoviesResponse> callPopularMovies = callMovieAPI.getPopularMovies();
        try {
            MoviesResponse moviesResponse = callPopularMovies.execute().body();
            Log.d(TAG, "ok");
            processMovie(moviesResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleActionGetTopRatedMovies() {
        InAPIMovieCall callMovieAPI = APIMovieCall.getCalledMoviesAPI();
        Call<MoviesResponse> callTopRatedMovies = callMovieAPI.getTopRatedMovies();
        try {
            MoviesResponse moviesResponse = callTopRatedMovies.execute().body();
            Log.d(TAG, "ok");
            processMovie(moviesResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processMovie(MoviesResponse moviesResponse) {
        List<Movies> movieDatas = moviesResponse.getMovies();
        Vector<ContentValues> vContentValues = new Vector<>(movieDatas.size());
        for (Movies movie : movieDatas) {
            ContentValues values = new ContentValues();
            values.put(MovieContract.MovieEntry.COLUMN_NAME_ID, movie.getId());
            values.put(MovieContract.MovieEntry.COLUMN_NAME_ORIGINAL_TITLE, movie.getOriginalTitle());
            values.put(MovieContract.MovieEntry.COLUMN_NAME_IMAGE_THUMBNAIL, movie.getPosterPath());
            //values.put(MovieContract.MovieEntry.COLUMN_NAME_IMAGE_BACKDROP,movie.getBackdropPath());
            values.put(MovieContract.MovieEntry.COLUMN_NAME_PLOT_SYNOPSIS, movie.getOverview());
            values.put(MovieContract.MovieEntry.COLUMN_NAME_RELEASE_DATE, movie.getReleaseDate());
            values.put(MovieContract.MovieEntry.COLUMN_NAME_VOTE_AVERAGE, movie.getVoteAverage());
            vContentValues.add(values);
        }
        ContentValues[] cvArray = new ContentValues[vContentValues.size()];
        vContentValues.toArray(cvArray);
        getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI_MOVIE, null, null);
        getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI_MOVIE, cvArray);
        getContext().getContentResolver().notifyChange(MovieContract.MovieEntry.CONTENT_URI_MOVIE, null);
    }

    private void actionGetTrailer(int id, Uri uri) {
        InAPIMovieCall callMovieAPI = APIMovieCall.getCalledMoviesAPI();
        Call<TrailerResponse> data = callMovieAPI.getTrailers(id);

        try {
            TrailerResponse video = data.execute().body();
            String[] projection = new String[]{MovieContract.MovieTrailerEntry.COLUMN_NAME_MOVIE_KEY, MovieContract.MovieTrailerEntry.COLUMN_NAME_TRAILER_KEY};
            String selection = MovieContract.MovieTrailerEntry.COLUMN_NAME_MOVIE_KEY + " = ? AND " + MovieContract.MovieTrailerEntry.COLUMN_NAME_TRAILER_KEY + " = ?";
            String selectionArg[];

            List<Trailers> videoResults = video.getVideoResults();
            for (Trailers result : videoResults) {
                ContentValues values = new ContentValues();
                values.put(MovieContract.MovieTrailerEntry.COLUMN_NAME_MOVIE_KEY, id);
                values.put(MovieContract.MovieTrailerEntry.COLUMN_NAME_TRAILER_KEY, result.getName());
                selectionArg = new String[]{String.valueOf(id), result.getKey()
                };
                Cursor cursor = getContext().getContentResolver().query(MovieContract.MovieTrailerEntry.CONTENT_URI_MOVIE_TRAILER, projection, selection, selectionArg, null);
                if (!cursor.moveToFirst()) {
                    getContext().getContentResolver().insert(MovieContract.MovieTrailerEntry.CONTENT_URI_MOVIE_TRAILER, values);
                }
                cursor.close();
            }
            getContext().getContentResolver().notifyChange(uri, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void actionGetReview(int id, Uri uri) {
        // TODO: Handle action Baz
        InAPIMovieCall callMovieAPI = APIMovieCall.getCalledMoviesAPI();
        Call<ReviewResponse> data = callMovieAPI.getReviews(id);

        try {
            ReviewResponse review = data.execute().body();

            String[] projection = new String[]{MovieContract.MovieReviewEntry.COLUMN_NAME_MOVIE_KEY, MovieContract.MovieReviewEntry.COLUMN_NAME_AUTHOR, MovieContract.MovieReviewEntry.COLUMN_NAME_CONTENT};
            String selection = MovieContract.MovieReviewEntry.COLUMN_NAME_MOVIE_KEY + " = ? AND " + MovieContract.MovieReviewEntry.COLUMN_NAME_AUTHOR + " = ? AND " + MovieContract.MovieReviewEntry.COLUMN_NAME_CONTENT + " = ?";
            String selectionArg[];

            List<Reviews> reviewResults = review.getReviewResults();
            for (Reviews result : reviewResults) {
                ContentValues values = new ContentValues();
                values.put(MovieContract.MovieReviewEntry.COLUMN_NAME_MOVIE_KEY, id);
                values.put(MovieContract.MovieReviewEntry.COLUMN_NAME_AUTHOR, result.getAuthor());
                values.put(MovieContract.MovieReviewEntry.COLUMN_NAME_CONTENT, result.getContent());
                selectionArg = new String[]{String.valueOf(id), result.getAuthor(), result.getContent()
                };
                Cursor cursor = getContext().getContentResolver().query(MovieContract.MovieReviewEntry.CONTENT_URI_MOVIE_REVIEW, projection, selection, selectionArg, null);
                if (!cursor.moveToFirst()) {
                    getContext().getContentResolver().insert(MovieContract.MovieReviewEntry.CONTENT_URI_MOVIE_REVIEW, values);
                }
                cursor.close();
            }
            getContext().getContentResolver().notifyChange(uri, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}