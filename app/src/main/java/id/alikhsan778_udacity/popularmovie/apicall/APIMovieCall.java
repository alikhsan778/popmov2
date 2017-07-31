package id.alikhsan778_udacity.popularmovie.apicall;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class APIMovieCall {
    public static final String BASE_URL = "https://api.themoviedb.org/3/movie/";
    public static InAPIMovieCall getCalledMoviesAPI(){
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        InAPIMovieCall calledMoviesAPI = retrofit.create(InAPIMovieCall.class);
        return calledMoviesAPI;
    }

}
