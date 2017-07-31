package id.alikhsan778_udacity.popularmovie;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by ulfiaizzati on 10/27/16.
 */

public class ViewHolder {

    static class ViewHolderTrailer extends RecyclerView.ViewHolder{
        TextView tv_trailer;
        public ViewHolderTrailer(View view){
            super(view);
            tv_trailer = (TextView)view.findViewById(R.id.trailer);
        }
    }

    static class ViewHolderReview extends RecyclerView.ViewHolder{
        TextView tv_user;
        TextView tv_review;
        public ViewHolderReview(View view){
            super(view);
            tv_user = (TextView)view.findViewById(R.id.tv_author);
            tv_review = (TextView)view.findViewById(R.id.tv_comment);
        }
    }

}
