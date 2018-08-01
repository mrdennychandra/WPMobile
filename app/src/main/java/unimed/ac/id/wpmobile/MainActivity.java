package unimed.ac.id.wpmobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ListView listPost;
    private ApiInterface api;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listPost = (ListView) findViewById(R.id.listPost);
        api = ApiClient.getClient().create(ApiInterface.class);
        getData();
    }

    private void getData(){
        Call<List<PostResult>> call = api.getPosts();
        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("loading...");
        mProgressDialog.show();
        call.enqueue(new Callback<List<PostResult>>() {
            @Override
            public void onResponse(Call<List<PostResult>> call, Response<List<PostResult>>
                    response) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                final List<PostResult> result = response.body();
                if(result != null) {
                    List<String> titles = new ArrayList<>();
                    for(PostResult post :  result){
                        PostResult.Title title = post.getTitle();
                        titles.add(title.getRendered());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                            R.layout.row_posts,
                            R.id.title,
                            titles.toArray(new String[titles.size()]));
                    listPost.setAdapter(adapter);
                    listPost.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                            PostResult object = result.get(position);
                            String title = object.getTitle().getRendered();
                            String content = object.getContent().getRendered();
                            Intent intent = new Intent(MainActivity.this,DetailPostActivity.class);
                            intent.putExtra("title",title);
                            intent.putExtra("content",content);
                            startActivity(intent);
                        }
                    });
                }else{
                    Toast.makeText(MainActivity.this,response.message(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<PostResult>> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());
                Toast.makeText(MainActivity.this,t.toString(),Toast.LENGTH_LONG).show();
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
        });
    }
}
