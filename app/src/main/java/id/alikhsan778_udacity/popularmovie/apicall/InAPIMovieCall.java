package id.alikhsan778_udacity.popularmovie.apicall;

import id.alikhsan778_udacity.popularmovie.BuildConfig;
import id.alikhsan778_udacity.popularmovie.MoviesResponse;
import id.alikhsan778_udacity.popularmovie.ReviewResponse;
import id.alikhsan778_udacity.popularmovie.TrailerResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by ulfiaizzati on 10/13/16.
 */

public interface InAPIMovieCall {

    final String TMDB_API_KEY = BuildConfig.THE_MOVIE_API_KEY;

    @GET("popular?api_key="+TMDB_API_KEY)
    Call<MoviesResponse> getPopularMovies();

    @GET("top_rated?api_key="+TMDB_API_KEY)
    Call<MoviesResponse> getTopRatedMovies();

    @GET("{id}/videos?api_key="+TMDB_API_KEY)
    Call<TrailerResponse> getTrailers(@Path("id") int id);

    @GET("{id}/reviews?api_key="+TMDB_API_KEY)
    Call<ReviewResponse> getReviews(@Path("id") int id);

}
