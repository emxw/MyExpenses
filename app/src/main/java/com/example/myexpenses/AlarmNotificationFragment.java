package com.example.myexpenses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.DateFormat;
import java.util.Calendar;

public class AlarmNotificationFragment extends AppCompatActivity/*implements TimePickerDialog.OnTimeSetListener*/ {

    private Toolbar mToolbar;
    private TextView alarmText;
    private Button selectAlarm, setAlarm, cancelAlarm;
    private MaterialTimePicker picker;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_notification_fragment);

        createNotificationChannel();

        alarmText = findViewById(R.id.alarmText);
        selectAlarm = findViewById(R.id.selectAlarm);
        setAlarm = findViewById(R.id.setAlarm);
        cancelAlarm = findViewById(R.id.cancelAlarm);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Reminder");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        selectAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_12H)
                        .setHour(12)
                        .setMinute(0)
                        .setTitleText("Select Alarm Time")
                        .build();

                picker.show(getSupportFragmentManager(), "notificationAlarmId");
                picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //set time in the textview
                        if (picker.getHour() > 12){
                            alarmText.setText(String.format("%02d", (picker.getHour()-12))+" : "+String.format("%02d", picker.getMinute())+" PM");
                        }
                        else{
                            alarmText.setText(picker.getHour()+" : "+picker.getMinute()+" AM");
                        }

                        calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY,picker.getHour());
                        calendar.set(Calendar.MINUTE,picker.getMinute());
                        calendar.set(Calendar.SECOND,0);
                        calendar.set(Calendar.MILLISECOND,0);
                    }
                });
//                DialogFragment timePicker = new TimePickerFragment();
//                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
        
        setAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(AlarmNotificationFragment.this, AlertReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(AlarmNotificationFragment.this, 0,intent, PendingIntent.FLAG_IMMUTABLE);
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent);
                Toast.makeText(AlarmNotificationFragment.this, "Alarm Set Successfully", Toast.LENGTH_SHORT).show();
            }
        });

        cancelAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AlarmNotificationFragment.this, AlertReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(AlarmNotificationFragment.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
                if (alarmManager == null){
                    alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                }
                alarmManager.cancel(pendingIntent);
                Toast.makeText(AlarmNotificationFragment.this, "Alarm Cancelled", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "alarmChannel";
            String description = "Channel for Alarm";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("notificationAlarmId", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
//    @Override
//    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//        Calendar c = Calendar.getInstance();
//        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
//        c.set(Calendar.MINUTE, minute);
//        c.set(Calendar.SECOND, 0);
//
//        updateTimeText(c);
//        startAlarm(c);
//    }
//
//    private void startAlarm(Calendar c) {
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//    }
//
//    private void updateTimeText(Calendar c) {
//        String timeText = "Alarm set for: " + DateFormat.getTimeInstance(DateFormat.SHORT).format(c);
//
//        alarmText.setText(timeText);
//    }
}