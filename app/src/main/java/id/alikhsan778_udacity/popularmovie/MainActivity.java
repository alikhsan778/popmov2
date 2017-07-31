package id.alikhsan778_udacity.popularmovie;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.squareup.picasso.Picasso;

import id.alikhsan778_udacity.popularmovie.database.MovieContract;
import id.alikhsan778_udacity.popularmovie.servicez.SyncAdapter;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private boolean mTwoPane;
    Toolbar toolbar;
    RecyclerView recyclerView;
    private MoviePosterListAdapter moviePosterListAdapter;
    private GridLayoutManager gridLayoutManager;
    public static final String IS_TWO_PANE = "IS_TWO_PANE";
    private boolean isTwoPane = false;
    public static int mPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_POSITION = "selected_position";
    private static final int MOVIES_LOADER = 0;
    private static final int MOVIES_FAVORITE_LOADER = 1;
    public static final String CURRENT_STATE = "CURRENT_STATE";
    private boolean currentState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.item_list);

        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());


        gridLayoutManager = new GridLayoutManager(MainActivity.this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        setupRecyclerView();

        if (findViewById(R.id.item_detail_container) != null) {
            mTwoPane = true;
        }

        if (ConnectionChecking.isConnected(this)) {
            SyncAdapter.initializeSyncAdapter(this);
        }

        if (savedInstanceState != null) {
            currentState = savedInstanceState.getBoolean(CURRENT_STATE);
        }

        if (!currentState)
            getSupportLoaderManager().initLoader(MOVIES_LOADER, null, this);
        else getSupportLoaderManager().initLoader(MOVIES_FAVORITE_LOADER, null, this);
    }

    private void setupRecyclerView() {
        updateAdapter(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.menu_seting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.popularity:
                fetchPopularity();
                return true;
            case R.id.rating:
                fetchRating();
                return true;
            case R.id.favorite:
                fetchFavorite();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fetchPopularity() {
        currentState = false;
        if (ConnectionChecking.isConnected(this)) {
            SyncAdapter.syncImmediately(this, Constant.USED_INT_PARAM_POPULAR, Constant.UNUSED_INT_PARAM, null);
        }
    }

    private void fetchRating() {
        currentState = false;
        if (ConnectionChecking.isConnected(this)) {
            SyncAdapter.syncImmediately(this, Constant.USED_INT_PARAM_RATED, Constant.UNUSED_INT_PARAM, null);
        }
    }

    private void fetchFavorite() {
        currentState = true;
        getSupportLoaderManager().restartLoader(MOVIES_FAVORITE_LOADER, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_POSITION, mPosition);
        }
        outState.putBoolean(IS_TWO_PANE, isTwoPane);
        outState.putBoolean(CURRENT_STATE, currentState);
        super.onSaveInstanceState(outState);
    }

    //Todo: check value cursor to set Recyclerview Adapter
    private void updateAdapter(Cursor cursor) {
        if (moviePosterListAdapter == null && cursor != null) {
            moviePosterListAdapter = new MoviePosterListAdapter(this, cursor);
            recyclerView.setAdapter(moviePosterListAdapter);
        } else if (moviePosterListAdapter != null && cursor != null) {
            moviePosterListAdapter.setCursor(cursor);
            moviePosterListAdapter.notifyDataSetChanged();
        }
    }

    //Todo : Set LoaderManger is here
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case MOVIES_LOADER:
                return new CursorLoader(
                        this,
                        MovieContract.MovieEntry.CONTENT_URI_MOVIE,
                        MovieContract.MovieEntry.PROJECTION,
                        null,
                        null,
                        null);
            case MOVIES_FAVORITE_LOADER:
                return new CursorLoader(
                        this,
                        MovieContract.MovieFavoriteEntry.CONTENT_URI_MOVIE_FAVORITE,
                        MovieContract.MovieFavoriteEntry.PROJECTION,
                        null,
                        null,
                        null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case MOVIES_LOADER:
            case MOVIES_FAVORITE_LOADER:

                if (data != null)
                    updateAdapter(data);
                if (mPosition != ListView.INVALID_POSITION) {
                    recyclerView.smoothScrollToPosition(mPosition);
                }

                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        updateAdapter(null);
    }//Todo:set loader in end

    //Todo:this class for movies poster list adapter
    public class MoviePosterListAdapter extends RecyclerView.Adapter<MoviePosterListAdapter.moviePosterViewHolder> {

        private Context mContext;
        private Cursor mCursor;

        public MoviePosterListAdapter(Context context, Cursor cursor) {
            this.mContext = context;
            this.mCursor = cursor;
        }

        public void setCursor(Cursor mCursor) {
            this.mCursor = mCursor;
        }

        @Override
        public MoviePosterListAdapter.moviePosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View inflate = LayoutInflater.from(mContext).inflate(R.layout.movies_poster_item, parent, false);
            MoviePosterListAdapter.moviePosterViewHolder viewHolder = new MoviePosterListAdapter.moviePosterViewHolder(inflate);
            return viewHolder;
        }

        //Todo:set value to bind in itemview viewholder
        @Override
        public void onBindViewHolder(MoviePosterListAdapter.moviePosterViewHolder holder, final int position) {
            //Todo: Check value cursor when move to position
            if (mCursor.moveToPosition(position)) {
//Todo: parsing value cursor inside new type data is same below
                final long id = mCursor.getLong(mCursor.getColumnIndex(MovieContract.MovieEntry._ID));
                final int dataId = mCursor.getInt(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_ID));
                final String posterPath = mCursor.getString(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_IMAGE_THUMBNAIL));

                Picasso.with(mContext)
                        .load(Constant.MOVIE_DB_IMAGE_PATH + posterPath)
                        .placeholder(R.drawable.ic_sync_pic)
                        .error(R.drawable.ic_warning_pic)
                        .fit()
                        .into(holder.img_poster);

                if (mTwoPane) {
                    //if poster is clicked
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri uri = null;
                            if (!currentState) {
                                uri = MovieContract.MovieEntry.buildMovieWithTrailerAndReview(id, dataId);
                            } else {
                                uri = MovieContract.MovieFavoriteEntry.buildMovieWithTrailerAndReview(id, dataId);
                            }

                            SyncAdapter.syncImmediately(mContext, Constant.USED_INT_PARAM_DETAIL, dataId, uri);
                            mPosition = position;
                            Bundle arguments = new Bundle();
                            arguments.putParcelable(MovieDetailFragment.DETAIL_URI, uri);
                            MovieDetailFragment fragment = new MovieDetailFragment();
                            fragment.setArguments(arguments);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.item_detail_container, fragment)
                                    .commit();
                        }
                    });

                } else {
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri uri = null;
                            if (!currentState) {
                                uri = MovieContract.MovieEntry.buildMovieWithTrailerAndReview(id, dataId);
                            } else {
                                uri = MovieContract.MovieFavoriteEntry.buildMovieWithTrailerAndReview(id, dataId);
                            }

                            SyncAdapter.syncImmediately(mContext, Constant.USED_INT_PARAM_DETAIL, dataId, uri);
                            mPosition = position;
                            Intent intent = new Intent(mContext, DetailActivity.class);
                            intent.setData(uri);
                            mContext.startActivity(intent);
                        }
                    });

                }
            }
        }

        //Todo: Set count item recyclerview same this cursor count
        @Override
        public int getItemCount() {
            return mCursor.getCount();
        }

        //Todo:declar itemview to display in recyclerview
        public class moviePosterViewHolder extends RecyclerView.ViewHolder {
            private ImageView img_poster;

            public moviePosterViewHolder(View itemView) {
                super(itemView);
                img_poster = (ImageView) itemView.findViewById(R.id.movie_image);

            }
        }

    }

}
