package com.example.Android_audio_talkback_demo_program;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

// Reception 。
public class FrgndSrvc extends Service
{
    MainActivity m_MainActivityPt; //存放主界面类对象的内存指针。

    public class FrgndSrvcBinder extends Binder
    {
        public void SetForeground( MainActivity MainActivityPt )
        {
            m_MainActivityPt = MainActivityPt;

            NotificationManager p_NotificationManagerPt = (NotificationManager) getSystemService( NOTIFICATION_SERVICE ); //存放通知管理器对象的内存指针。

            if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) //如果当前系统 for Andoird 8.0及以上。
            {
                //创建 Status notification 的通知渠道，并Настраивать for  Quiet sound。
                NotificationChannel p_NotificationChannel = new NotificationChannel( "status", " Status notification ", NotificationManager.IMPORTANCE_HIGH );
                p_NotificationChannel.setSound( null, null );
                p_NotificationManagerPt.createNotificationChannel( p_NotificationChannel );
            }

            //创建通知。
            PendingIntent pendingIntent = PendingIntent.getActivity( m_MainActivityPt,0, new Intent(m_MainActivityPt, MainActivity.class ),0 );
            Notification notification =
                    new NotificationCompat.Builder( m_MainActivityPt, "status" ) //Android API 14及以上 version  use 。
                    //new NotificationCompat.Builder( m_MainActivityPt ) //Android API 9~25 version  use 。
                    .setSmallIcon( R.mipmap.ic_launcher )
                    .setContentTitle("Android下 Audio  Intercom demo program ")
                    .setContentText( " Reception " )
                    .setSound( null )
                    .setContentIntent( pendingIntent )
                    .build();

            // Send notification ， And become  Reception 。
            startForeground( 1, notification );
        }
    }

    @Nullable
    @Override
    public IBinder onBind( Intent intent ) //this 服务被绑定。
    {
        return new FrgndSrvcBinder();
    }

    @Override
    public boolean onUnbind( Intent intent ) //this 服务被解除绑定。
    {
        stopForeground( true ); //退出 Reception ， And become 普通服务。

        return super.onUnbind( intent );
    }

    @Override
    public void onDestroy() //this 服务被destroy。
    {
        super.onDestroy();
    }
}