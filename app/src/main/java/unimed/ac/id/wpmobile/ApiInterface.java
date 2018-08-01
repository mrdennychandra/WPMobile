package unimed.ac.id.wpmobile;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {

    @GET("posts")
    Call<List<PostResult>> getPosts();
}

