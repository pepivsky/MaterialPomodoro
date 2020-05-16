package com.example.materialpomodoro;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.emmanuelkehinde.shutdown.Shutdown;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //private static final long START_TIME_IN_MILLIS = 1500000;
    private static final long START_TIME_IN_MILLIS = 5000;//Cambiar tiempo

    private TextView mTextViewCountDown;
    private Button mButtonStartPause;
    private Button mButtonReset;

    private CountDownTimer mCountDownTimer;

    private boolean mTimerRunning;

    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewCountDown = findViewById(R.id.text_view_countdown);

        mButtonStartPause = findViewById(R.id.button_start_pause);
        mButtonReset = findViewById(R.id.button_reset);

        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });

        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        updateCountDownText();
    }

    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                mButtonStartPause.setText("Start");
                mButtonStartPause.setVisibility(View.INVISIBLE);
                mButtonReset.setVisibility(View.VISIBLE);
                //Lanzar notificación

                Intent intent = new Intent(getApplicationContext(),MainActivity.class); //Intent a donde nos va a mandar la notificacion
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,intent,PendingIntent.FLAG_ONE_SHOT);
                Uri sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); //Objeto para sonido de notificacion

                //Configurando la notificacion
                Notification.Builder notificacionBuilder = new  Notification.Builder( getApplicationContext())
                        .setSmallIcon(R.drawable.tomato)
                        .setContentTitle("El pomodoro ha terminado! :)")
                        //.setContentText("Lo haces muy bien!, ahora tómate un descanso de 5 minutos y haz algo divertido, después puedes continuar trabajando ;).")
                        .setSound(sonido)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setColor(16711680)
                        .setContentText("Lo haces muy bien!, ahora tómate un descanso de 5 minutos y haz algo divertido, después puedes continuar trabajando ;).")
                        .setStyle(new Notification.BigTextStyle().bigText("Lo haces muy bien!, ahora tómate un descanso de 5 minutos y haz algo divertido, después puedes continuar trabajando ;)."))
                        ;

                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

                //Codigo para que funcionen notificaciones en android 8 o superior
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    String channelId = getString(R.string.normal_channel_id);
                    String channelName = getString(R.string.normal_channel_name);

                    NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
                    channel.enableVibration(true);
                    channel.setVibrationPattern(new long[]{100,200,50});

                    if (notificationManager != null) {
                        notificationManager.createNotificationChannel(channel);
                    }

                    notificacionBuilder.setChannelId(channelId);
                }


                if (notificationManager != null) {
                    notificationManager.notify(0,notificacionBuilder.build());
                }



            }
        }.start();

        mTimerRunning = true;
        mButtonStartPause.setText("pause");
        mButtonReset.setVisibility(View.INVISIBLE);
    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        mButtonStartPause.setText("Start");
        mButtonReset.setVisibility(View.VISIBLE);
    }

    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
        mButtonReset.setVisibility(View.INVISIBLE);
        mButtonStartPause.setVisibility(View.VISIBLE);
    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        mTextViewCountDown.setText(timeLeftFormatted);
    }

    @Override
    public void onBackPressed() {
        //Shutdown.now(this);
        Shutdown.now(this,"Presiona nuevamente para salir ;)");
        //super.onBackPressed();
    }
}