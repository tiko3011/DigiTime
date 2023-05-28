package com.example.smartnotifyer.ui.limits;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.smartnotifyer.MainActivity;
import com.example.smartnotifyer.R;
import com.example.smartnotifyer.ui.UsageConverter;
import com.example.smartnotifyer.ui.apps.AppsFragment;

public class LimitActivity extends AppCompatActivity {
    static int weeklyUsage = AppsFragment.weeklyUsage;
    public static long usageLimit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_limit);

//        TextView tvUsage = findViewById(R.id.tv_usage_time);     tvUsage.setText(UsageConverter.convertMinuteToString(AppsFragment.weeklyUsage));
//        TextView tvTarget = findViewById(R.id.tv_limit);
//        TextView tvComment = findViewById(R.id.tv_info_reduction);
//
//        Button btnNext = findViewById(R.id.btn_confirm);
//        btnNext.setOnClickListener(v -> {
//
//        });
//        Button btnBack = findViewById(R.id.btn_back);
//        btnBack.setOnClickListener(v -> {
//            Intent intent = new Intent(LimitActivity.this, MainActivity.class);
//            startActivity(intent);
//        });
//
//        SeekBar barSetLimit = findViewById(R.id.bar_set_limit);
//        barSetLimit.setMax(weeklyUsage * 2);
//        barSetLimit.setProgress(weeklyUsage);
//
//        barSetLimit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                tvTarget.setText(UsageConverter.convertMinuteToString(barSetLimit.getMax() - barSetLimit.getProgress()));
//
//                if (progress < weeklyUsage) {
//                    tvComment.setText("That's more than you normally use!");
//                } else {
//                    double reductionPercent = (double) (barSetLimit.getProgress() - barSetLimit.getMax() / 2) / weeklyUsage * 100;
//                    tvComment.setText("That's a reduction of " + UsageConverter.decimalFormat.format(reductionPercent) + "% !");
//                }
//            }
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                // Called when the user starts interacting with the SeekBar
//            }
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                // Called when the user stops interacting with the SeekBar
//                usageLimit = barSetLimit.getMax() - barSetLimit.getProgress();
//            }
//        });

    }
}