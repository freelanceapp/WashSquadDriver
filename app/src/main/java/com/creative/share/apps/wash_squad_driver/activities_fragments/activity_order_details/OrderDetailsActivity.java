package com.creative.share.apps.wash_squad_driver.activities_fragments.activity_order_details;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.creative.share.apps.wash_squad_driver.R;
import com.creative.share.apps.wash_squad_driver.activities_fragments.activity_work1.Work1Activity_Step1;
import com.creative.share.apps.wash_squad_driver.databinding.ActivityOrderDetailsBinding;
import com.creative.share.apps.wash_squad_driver.interfaces.Listeners;
import com.creative.share.apps.wash_squad_driver.language.LanguageHelper;
import com.creative.share.apps.wash_squad_driver.models.Order_Model;
import com.creative.share.apps.wash_squad_driver.remote.Api;
import com.creative.share.apps.wash_squad_driver.share.Common;
import com.creative.share.apps.wash_squad_driver.tags.Tags;

import java.util.Calendar;
import java.util.Locale;

import io.paperdb.Paper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailsActivity extends AppCompatActivity implements Listeners.BackListener {

    private ActivityOrderDetailsBinding binding;
    private Order_Model.Data data;
    private String lang;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, "en"));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_details);
        initView();
    }

    private void initView() {
        binding.setBackListener(this);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());

        data = (Order_Model.Data) getIntent().getExtras().getSerializable("detials");
        binding.setLang(lang);
        binding.setOrderModel(data);

        binding.btShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start();
            }
        });
    }

    @Override
    public void back() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 1002 || requestCode == 1003) && resultCode == RESULT_OK && data != null) {
            Intent intent = getIntent();
            if (intent != null) {
                intent.putExtra("reason", 1);
                setResult(RESULT_OK, intent);
            }
            finish();
        }
    }

    private void start() {
        final Dialog dialog = Common.createProgressDialog(OrderDetailsActivity.this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();


        try {
            Api.getService(lang, Tags.base_url)
                    .start(data.getId() + "", (Calendar.getInstance().getTimeInMillis() / 1000) + "").enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    dialog.dismiss();
                    if (response.isSuccessful()) {
                        // Common.CreateSignAlertDialog(adsActivity,getResources().getString(R.string.suc));
                        Toast.makeText(OrderDetailsActivity.this, getString(R.string.suc), Toast.LENGTH_SHORT).show();

                        //  adsActivity.finish(response.body().getId_advertisement());
                        Intent intent = new Intent(OrderDetailsActivity.this, Work1Activity_Step1.class);
                        intent.putExtra("detials", data);

                        startActivityForResult(intent, 1003);
                        finish();
                    } else {
                        try {

                            Toast.makeText(OrderDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            Log.e("Error", response.code() + "" + response.message() + "" + response.errorBody() + response.raw() + response.body() + response.headers());
                        } catch (Exception e) {


                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    dialog.dismiss();
                    try {
                        Toast.makeText(OrderDetailsActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                        Log.e("Error", t.getMessage());
                    } catch (Exception e) {

                    }
                }
            });
        } catch (Exception e) {
            dialog.dismiss();
            Log.e("error", e.getMessage().toString());
        }
    }
}
