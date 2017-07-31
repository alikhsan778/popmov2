package id.alikhsan778_udacity.popularmovie;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import id.alikhsan778_udacity.popularmovie.database.MovieContract;


public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String TAG = MovieDetailFragment.class.getSimpleName();

    public static final String DETAIL_URI = "URI";
    private Uri mUri;
    public static final int MOVIE_DETAIL_LOADER = 1;
    private boolean isFavorite = false;

    private TextView title, releaseDate, plotSynopsis, txRate,tv_markasfavo;
    private CardView clickMarkAsFavorite,actionCus;
    private ImageView startFavoriteImg;
    ImageView img_rate_1;

    //private RatingBar rating;
    private ImageView poster;
    private ImageView backdrop;
    private RecyclerView movies, reviews;

    private List<String> moviesKey = new ArrayList<>();
    private List<Pair<String, String>> reviewsAuthorAndContent = new ArrayList<>();

    public MovieDetailFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(DETAIL_URI)) {
            mUri = arguments.getParcelable(DETAIL_URI);
            getActivity().getSupportLoaderManager().restartLoader(MOVIE_DETAIL_LOADER, null, this);
        }
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_movie_detail, container, false);
        title = (TextView)rootView.findViewById(R.id.title);
        releaseDate = (TextView)rootView.findViewById(R.id.releaseDate);
        poster = (ImageView)rootView.findViewById(R.id.poster);
        backdrop = (ImageView)rootView.findViewById(R.id.img_background);
        clickMarkAsFavorite = (CardView) rootView.findViewById(R.id.mark_as_favorite);
        startFavoriteImg = (ImageView)rootView.findViewById(R.id.mark_as_favorite_img);
        //rating = (RatingBar)rootView.findViewById(R.id.rating);
        plotSynopsis = (TextView)rootView.findViewById(R.id.synopsis);
        txRate = (TextView)rootView.findViewById(R.id.tv_value);
        img_rate_1 = (ImageView)rootView.findViewById(R.id.rs_rate);
        movies = (RecyclerView)rootView.findViewById(R.id.movies);
        reviews = (RecyclerView)rootView.findViewById(R.id.reviews);
        tv_markasfavo = (TextView)rootView.findViewById(R.id.tv_pavo);
        actionCus= (CardView) rootView.findViewById(R.id.tv_custom);

        RecyclerView.LayoutManager layoutManagerMovies = new LinearLayoutManager(getActivity());
        RecyclerView.LayoutManager layoutManagerReviews = new LinearLayoutManager(getActivity());

        movies.setLayoutManager(layoutManagerMovies);
        reviews.setLayoutManager(layoutManagerReviews);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_share, menu);

        MenuItem shareItem = menu.findItem(R.id.action_share);
        final ShareActionProvider myShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        if (moviesKey.size() > 0){
            for (String s: moviesKey){
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v="+s);
                PackageManager packageManager = getActivity().getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(sharingIntent, 0);
                boolean isIntentSafe = activities.size() > 0;
                if (isIntentSafe && myShareActionProvider != null) {
                    myShareActionProvider.setShareIntent(sharingIntent);
                }
                break;
            }
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), mUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor mCursor) {
        if (mCursor != null && mCursor.moveToFirst() && getView() != null){

            processDetail(mCursor);
            processTrailer(mCursor);
            processReview(mCursor);

            while (mCursor.moveToNext()){
                processTrailer(mCursor);
                processReview(mCursor);
            }

            movies.setAdapter(new RecyclerView.Adapter<ViewHolder.ViewHolderTrailer>(){

                @Override
                public ViewHolder.ViewHolderTrailer onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.trailer_item, null);
                    ViewHolder.ViewHolderTrailer vht = new ViewHolder.ViewHolderTrailer(view);
                    return vht;
                }

                @Override
                public void onBindViewHolder(ViewHolder.ViewHolderTrailer holder, int position) {
                    final String youtubeId = moviesKey.get(position);
                    final String linkYoutube = "http://www.youtube.com/watch?v=";
                    holder.tv_trailer.setText("Check this link ---> "+linkYoutube+youtubeId);
                    holder.tv_trailer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent sendIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v="+youtubeId));
                            if (sendIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                startActivity(sendIntent);
                            }
                        }
                    });
                }

                @Override
                public int getItemCount() {
                    return moviesKey.size();
                }
            });
            reviews.setAdapter(new RecyclerView.Adapter<ViewHolder.ViewHolderReview>(){

                @Override
                public ViewHolder.ViewHolderReview onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.review_item, null);
                    ViewHolder.ViewHolderReview vhr = new ViewHolder.ViewHolderReview(view);
                    return vhr;
                }

                @Override
                public void onBindViewHolder(ViewHolder.ViewHolderReview holder, int position) {
                    final Pair<String, String> data = reviewsAuthorAndContent.get(position);
                    holder.tv_user.setText(data.first);
                    holder.tv_review.setText(data.second);
                }

                @Override
                public int getItemCount() {
                    return reviewsAuthorAndContent.size();
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    private void processDetail(Cursor mCursor){
        final String movieId = mCursor.getString(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_ID));
        final String title = mCursor.getString(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_ORIGINAL_TITLE));
        final String releaseDate = mCursor.getString(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_RELEASE_DATE));
        final String posterPath = mCursor.getString(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_IMAGE_THUMBNAIL));
        final String voteAverage = mCursor.getString(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_VOTE_AVERAGE));
        final double rating = Double.valueOf(voteAverage);
        final String plotSynopsys = mCursor.getString(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_PLOT_SYNOPSIS));

        this.title.setText(title);
        this.releaseDate.setText(releaseDate);
        Picasso.with(getActivity())
                .load(Constant.MOVIE_DB_IMAGE_PATH + posterPath)
                .placeholder(R.drawable.ic_sync_pic)
                .fit()
                .into(poster);
        Picasso.with(getActivity())
                .load(Constant.MOVIE_DB_IMAGE_PATH+posterPath)
                .placeholder(R.drawable.ic_sync_pic)
                .fit()
                .into(backdrop);
        startFavoriteImg.setImageResource(R.drawable.ic_star_border);
        actionCus.setVisibility(View.INVISIBLE);
        this.plotSynopsis.setText(plotSynopsys);
        this.txRate.setText(String.valueOf(rating));
        tv_markasfavo.setText(R.string.fav);
        img_rate_1.setImageResource(R.drawable.ic_star_gren_full_d);




        // TODO: 10/29/16 should run on background thread
        final Long movId = Long.parseLong(movieId);
        final String selection = MovieContract.MovieFavoriteEntry.COLUMN_NAME_ID + " = ?";
        final String [] selectionArg = new String[]{movieId};
        final Cursor cursor = getContext().getContentResolver().query(MovieContract.MovieFavoriteEntry.CONTENT_URI_MOVIE_FAVORITE
                , MovieContract.MovieFavoriteEntry.PROJECTION
                , selection, selectionArg, null);
        if (!cursor.moveToFirst()){
            isFavorite = false;
            startFavoriteImg.setImageResource(R.drawable.ic_star_border);
            tv_markasfavo.setText(R.string.fav);
            actionCus.setVisibility(View.INVISIBLE);
        } else {
            isFavorite = true;
            startFavoriteImg.setImageResource(R.drawable.ic_star_full);
            tv_markasfavo.setText(R.string.unfavorite);
            actionCus.setVisibility(View.VISIBLE);
        }
        cursor.close();
        this.clickMarkAsFavorite.setTag(movieId);
        this.clickMarkAsFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFavorite){
                    ContentValues values = new ContentValues();
                    values.put(MovieContract.MovieFavoriteEntry.COLUMN_NAME_ID, movId);
                    values.put(MovieContract.MovieFavoriteEntry.COLUMN_NAME_ORIGINAL_TITLE, title);
                    values.put(MovieContract.MovieFavoriteEntry.COLUMN_NAME_IMAGE_THUMBNAIL, posterPath);
                    values.put(MovieContract.MovieFavoriteEntry.COLUMN_NAME_PLOT_SYNOPSIS, plotSynopsys);
                    values.put(MovieContract.MovieFavoriteEntry.COLUMN_NAME_RELEASE_DATE, releaseDate);
                    values.put(MovieContract.MovieFavoriteEntry.COLUMN_NAME_VOTE_AVERAGE, voteAverage);
                    getContext().getContentResolver().insert(MovieContract.MovieFavoriteEntry.CONTENT_URI_MOVIE_FAVORITE, values);
                    startFavoriteImg.setImageResource(R.drawable.ic_star_full);
                    tv_markasfavo.setText(R.string.unfavorite);
                    actionCus.setVisibility(View.VISIBLE);
                    isFavorite = true;
                } else {
                    getContext().getContentResolver().delete(MovieContract.MovieFavoriteEntry.CONTENT_URI_MOVIE_FAVORITE, selection, selectionArg);
                    startFavoriteImg.setImageResource(R.drawable.ic_star_border);
                    tv_markasfavo.setText(R.string.fav);
                    actionCus.setVisibility(View.INVISIBLE);
                    isFavorite = false;
                }
                getContext().getContentResolver().query(MovieContract.MovieFavoriteEntry.CONTENT_URI_MOVIE_FAVORITE,
                        MovieContract.MovieFavoriteEntry.PROJECTION,
                        null,
                        null,
                        null);
            }
        });
    }

    private void starRate() {

    }

    private void processTrailer(Cursor cursor){
        final String videoCode = cursor.getString(cursor.getColumnIndex(MovieContract.MovieTrailerEntry.COLUMN_NAME_TRAILER_KEY));
        boolean isFind = false;
        if (videoCode != null && !TextUtils.isEmpty(videoCode)){
            for (String s : moviesKey){
                if (s.equalsIgnoreCase(videoCode)) {
                    isFind = true;
                    break;
                }
            }
            if (!isFind) moviesKey.add(videoCode);
        }
    }

    private void processReview(Cursor cursor){
        final String reviewAuthor = cursor.getString(cursor.getColumnIndex(MovieContract.MovieReviewEntry.COLUMN_NAME_AUTHOR));
        final String reviewContent = cursor.getString(cursor.getColumnIndex(MovieContract.MovieReviewEntry.COLUMN_NAME_CONTENT));
        if (reviewAuthor != null && reviewContent != null){
            boolean isFind = false;
            for (Pair<String, String> data : reviewsAuthorAndContent){
                if (data.first.equalsIgnoreCase(reviewAuthor) && data.second.equalsIgnoreCase(reviewContent)) {
                    isFind = true;
                    break;
                }
            }
            if (!isFind)
                reviewsAuthorAndContent.add(new Pair<String, String>(reviewAuthor, reviewContent));
        }
    }
}
