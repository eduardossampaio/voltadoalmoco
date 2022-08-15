package apps.esampaio.com.voltadoalmoco.services.workers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.work.ForegroundInfo;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Calendar;
import java.util.Date;

import apps.esampaio.com.voltadoalmoco.services.notifications.Notifications;

public class CountdownWorker extends Worker {

    public static final String INIT_TIME_PARAMETER = "apps.esampaio.com.voltadoalmoco.CountdownWorker.INIT_TIME_PARAMETER";
    public static final String WORKER_TAG = "apps.esampaio.com.voltadoalmoco.WORKERS.COUNTDOWN_WORKER";
    public static final Integer COUNTDOWN_NOTIFICATION_ID = 3355;

    private Date initTime;
    private Date endTime;

    public CountdownWorker(Context context, WorkerParameters parameters) {
       super(context,parameters);
    }


    @NonNull
    @Override
    public Result  doWork() {
        Long initTimeMillis = getInputData().getLong(INIT_TIME_PARAMETER,System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        initTime = new Date(initTimeMillis);
        calendar.setTime(initTime);
        calendar.add(Calendar.HOUR, 1);
        calendar.set(Calendar.SECOND,59);
        endTime = calendar.getTime();

        Long diff = 0L;
        int minutes = 0;
        int lastMinute = 0;

        do {
            diff = endTime.getTime() - System.currentTimeMillis();
            minutes = (int) (diff / 1000) / 60;

            if (minutes != lastMinute) {
                lastMinute = minutes;
                Log.d("MINUTES REMAINING", "" + lastMinute);
                notifyProgress(lastMinute);
            }
            Log.d("DIFF", "" + diff);
        } while (diff > 0);
        return Result.success();
    }

    private void notifyProgress(int remainingMinutes){
        Intent intent = new Intent("WORKER_RESULT");
        intent.putExtra("REMAINING_MINUTES", remainingMinutes);
        getApplicationContext().sendBroadcast(intent);
        setForegroundAsync(createForegroundInfo(remainingMinutes));
    }

    @NonNull
    private ForegroundInfo createForegroundInfo(@NonNull Integer remainingMinutes) {
        Log.d("WORK REMAINING", "" + remainingMinutes);

        Notification notification = Notifications.Companion.createCountdownProgressNotification(getApplicationContext(),remainingMinutes);
        return new ForegroundInfo(COUNTDOWN_NOTIFICATION_ID, notification);
    }


}