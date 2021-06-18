package com.example.Android_audio_talkback_demo_program;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;

import HeavenTao.Audio.*;
import HeavenTao.Video.*;
import HeavenTao.Media.*;
import HeavenTao.Data.*;
import HeavenTao.Sokt.*;

//The main interface message processing class.
class MainActivityHandler extends Handler
{
    String m_CurClsNameStrPt = this.getClass().getSimpleName(); //当前类名称digit符串类对象的内存指针。

    MainActivity m_MainActivityPt; //存放主界面类对象的内存指针。
    ServiceConnection m_FrgndSrvcCnctPt; //存放 Reception 连接器类对象的内存指针。

    public void handleMessage( Message MessagePt )
    {
        if( MessagePt.what == 1 ) //如果是媒体处理线程启动的消息。
        {
            if( m_MainActivityPt.m_MyMediaProcThreadPt.m_IsCreateSrvrOrClnt == 1 ) //如果是 Create server 。
            {
                ( ( EditText ) m_MainActivityPt.findViewById( R.id.IPAddrEdit ) ).setEnabled( false ); //НастраиватьIP地址控件 for 不可用。
                ( ( EditText ) m_MainActivityPt.findViewById( R.id.PortEdit ) ).setEnabled( false ); //Настраиватьport控件 for 不可用。
                ( ( RadioButton ) m_MainActivityPt.findViewById( R.id.UseTcpPrtclRadioBtn ) ).setEnabled( false ); //НастраиватьTCPprotocol按钮 for 不可用。
                ( ( RadioButton ) m_MainActivityPt.findViewById( R.id.UseUdpPrtclRadioBtn ) ).setEnabled( false ); //НастраиватьUDPprotocol按钮 for 不可用。
                ( ( Button ) m_MainActivityPt.findViewById( R.id.CreateSrvrBtn ) ).setText( "in断" ); //Настраивать Create server 按钮的内容 for “in断”。
                ( ( Button ) m_MainActivityPt.findViewById( R.id.ConnectSrvrBtn ) ).setEnabled( false ); //Настраивать Connect to the server 按钮 for 不可用。
                ( ( Button ) m_MainActivityPt.findViewById( R.id.SettingBtn ) ).setEnabled( false ); //НастраиватьНастраивать按钮 for 不可用。
            }
            else //如果是创建客户端。
            {
                ( ( EditText ) m_MainActivityPt.findViewById( R.id.IPAddrEdit ) ).setEnabled( false ); //НастраиватьIP地址控件 for 不可用。
                ( ( EditText ) m_MainActivityPt.findViewById( R.id.PortEdit ) ).setEnabled( false ); //Настраиватьport控件 for 不可用。
                ( ( RadioButton ) m_MainActivityPt.findViewById( R.id.UseTcpPrtclRadioBtn ) ).setEnabled( false ); //НастраиватьTCPprotocol按钮 for 不可用。
                ( ( RadioButton ) m_MainActivityPt.findViewById( R.id.UseUdpPrtclRadioBtn ) ).setEnabled( false ); //НастраиватьUDPprotocol按钮 for 不可用。
                ( ( Button ) m_MainActivityPt.findViewById( R.id.CreateSrvrBtn ) ).setEnabled( false ); //Настраивать Create server 按钮 for 不可用。
                ( ( Button ) m_MainActivityPt.findViewById( R.id.ConnectSrvrBtn ) ).setText( "in断" ); //Настраивать Connect to the server 按钮的内容 for “in断”。
                ( ( Button ) m_MainActivityPt.findViewById( R.id.SettingBtn ) ).setEnabled( false ); //НастраиватьНастраивать按钮 for 不可用。
            }

            //创建并绑定 Reception ，从而确保this 进程 в 转入后台或系统锁屏 Time 不会被系统限制运行，且只能放 в 主线程in执行，因 for 要 use 界面类对象。
            if( ( ( CheckBox ) m_MainActivityPt.m_LyotActivitySettingViewPt.findViewById( R.id.IsUseFrgndSrvcCheckBox ) ).isChecked() && m_FrgndSrvcCnctPt == null )
            {
                m_FrgndSrvcCnctPt = new ServiceConnection() //创建存放 Reception 连接器。
                {
                    @Override
                    public void onServiceConnected( ComponentName name, IBinder service ) // Reception 绑定success.
                    {
                        ( ( FrgndSrvc.FrgndSrvcBinder ) service ).SetForeground( m_MainActivityPt ); // will 服务Настраивать for  Reception 。
                    }

                    @Override
                    public void onServiceDisconnected( ComponentName name ) // Reception 解除绑定。
                    {

                    }
                };
                m_MainActivityPt.bindService( new Intent( m_MainActivityPt, FrgndSrvc.class ), m_FrgndSrvcCnctPt, Context.BIND_AUTO_CREATE ); //创建并绑定 Reception 。
            }
        }
        else if( MessagePt.what == 2 ) //如果是媒体处理线程退出的消息。
        {
            m_MainActivityPt.m_MyMediaProcThreadPt = null;

            if( m_FrgndSrvcCnctPt != null ) //如果已经创建并绑定了 Reception 。
            {
                m_MainActivityPt.unbindService( m_FrgndSrvcCnctPt ); //解除绑定并destroy Reception 。
                m_FrgndSrvcCnctPt = null;
            }

            ( ( EditText ) m_MainActivityPt.findViewById( R.id.IPAddrEdit ) ).setEnabled( true ); //НастраиватьIP Доступны элементы управления адресом.
            ( ( EditText ) m_MainActivityPt.findViewById( R.id.PortEdit ) ).setEnabled( true ); //Настраиватьport控件 for 可用。
            ( ( RadioButton ) m_MainActivityPt.findViewById( R.id.UseTcpPrtclRadioBtn ) ).setEnabled( true ); //НастраиватьTCPprotocol按钮 for 可用。
            ( ( RadioButton ) m_MainActivityPt.findViewById( R.id.UseUdpPrtclRadioBtn ) ).setEnabled( true ); //НастраиватьUDPprotocol按钮 for 可用。
            ( ( Button ) m_MainActivityPt.findViewById( R.id.CreateSrvrBtn ) ).setText( " Create server " ); //Настраивать Create server 按钮的内容 for “ Create server ”。
            ( ( Button ) m_MainActivityPt.findViewById( R.id.ConnectSrvrBtn ) ).setEnabled( true ); //Настраивать Connect to the server 按钮 for 可用。
            ( ( Button ) m_MainActivityPt.findViewById( R.id.ConnectSrvrBtn ) ).setText( " Connect to the server " ); //Настраивать Connect to the server 按钮的内容 for “ Connect to the server ”。
            ( ( Button ) m_MainActivityPt.findViewById( R.id.CreateSrvrBtn ) ).setEnabled( true ); //Настраивать Create server 按钮 for 可用。
            ( ( Button ) m_MainActivityPt.findViewById( R.id.SettingBtn ) ).setEnabled( true ); //НастраиватьНастраивать按钮 for 可用。
        }
        else if( MessagePt.what == 3 ) //如果是显示Log的消息。
        {
            TextView p_LogTextView = new TextView( m_MainActivityPt );
            p_LogTextView.setText( ( new SimpleDateFormat( "HH:mm:ss SSS" ) ).format( new Date() ) + "：" + MessagePt.obj );
            ( ( LinearLayout ) m_MainActivityPt.m_LyotActivityMainViewPt.findViewById( R.id.LogLinearLyot ) ).addView( p_LogTextView );
        }
        else if( MessagePt.what == 4 ) //如果是重建SurfaceView控件消息，用来Empty残余画面。
        {
            m_MainActivityPt.m_VideoInputPreviewSurfaceViewPt.setVisibility( View.GONE ); //destroyvideo enter 预览SurfaceView控件。
            m_MainActivityPt.m_VideoInputPreviewSurfaceViewPt.setVisibility( View.VISIBLE ); //创建video enter 预览SurfaceView控件。
            m_MainActivityPt.m_VideoOutputDisplaySurfaceViewPt.setVisibility( View.GONE ); //destroyvideo Output 显示SurfaceView控件。
            m_MainActivityPt.m_VideoOutputDisplaySurfaceViewPt.setVisibility( View.VISIBLE ); //创建video Output 显示SurfaceView控件。
        }
    }
}

//我的媒体处理线程类。
class MyMediaProcThread extends MediaProcThread
{
    String m_IPAddrStrPt; //存放IP地址digit符串类对象的内存指针。
    String m_PortStrPt; //存放portdigit符串类对象的内存指针。
    Handler m_MainActivityHandlerPt; //存放主界面消息处理类对象的内存指针。

    int m_UseWhatXfrPrtcl; //存放 use 什么Transfer Protocol， for 0表示TCPprotocol， for 1表示UDPprotocol。
    int m_IsCreateSrvrOrClnt; //存放 Create server 或者客户端标记， for 1表示 Create server ， for 0表示创建客户端。
    TcpSrvrSokt m_TcpSrvrSoktPt; //存放this 端TCPprotocol服务端套接digit类对象的内存指针。
    TcpClntSokt m_TcpClntSoktPt; //存放this 端TCPprotocol客户端套接digit类对象的内存指针。
    UdpSokt m_UdpSoktPt; //存放this 端UDPprotocol套接digit类对象的内存指针。
    long m_LastPktSendTime; //存放最后一个 number 据包的发送 Time 间，用于判断连接是否in断。
    long m_LastPktRecvTime; //存放最后一个 number 据包的接收 Time 间，用于判断连接是否in断。
    public static final byte PKT_TYP_CNCT_HTBT = 0x00; // number 据包 Types of ：连接请求包或心跳包。
    public static final byte PKT_TYP_AFRAME = 0x01; // number 据包 Types of ： Audio  enter  Output  frame 。
    public static final byte PKT_TYP_VFRAME = 0x02; // number 据包 Types of ：video enter  Output  frame 。
    public static final byte PKT_TYP_ACK = 0x03; // number 据包 Types of ：连接应答包或soundvideo enter  Output  frame Reply package.
    public static final byte PKT_TYP_EXIT = 0x04; // number 据包 Types of ：退出包。

    int m_LastSendAudioInputFrameIsAct; //存放最后一个发送的 Audio  enter  frame  Have无  voice sound live动， for 1表示 Have  voice sound live动， for 0表示无  voice sound live动。
    int m_LastSendAudioInputFrameIsRecv; //存放最后一个发送的 Audio  enter  frame 远端是否接收到， for 0表示没 Have收到， for 非0表示已经收到。
    int m_LastSendAudioInputFrameTimeStamp; //存放最后一个发送 Audio  enter  frame 的 Time 间戳。
    int m_LastSendVideoInputFrameTimeStamp; //存放最后一个发送video enter  frame 的 Time 间戳。
    byte m_IsRecvExitPkt; //存放是否接收到退出包， for 0表示否， for 1表示是。

    int m_UseWhatRecvOutputFrame; //存放 use 什么 Receive output  frame ， for 0表示Linked list， for 1表示Adaptive jitter buffer。
    int m_LastGetAudioOutputFrameIsAct; //存放最后一个取出的 Audio  Output  frame 是否 for  Have  voice sound live动， for 0表示否， for 非0表示是。
    int m_LastGetAudioOutputFrameVideoOutputFrameTimeStamp; //存放最后一个取出的 Audio  Output  frame 对应video Output  frame 的 Time 间戳。

    LinkedList< byte[] > m_RecvAudioOutputFrameLnkLstPt; //存放接收 Audio  Output  frame Linked list类对象的内存指针。
    LinkedList< byte[] > m_RecvVideoOutputFrameLnkLstPt; //存放接收video Output  frame Linked list类对象的内存指针。

    AAjb m_AAjbPt; //存放 Audio Adaptive jitter buffer类对象的内存指针。
    int m_AAjbMinNeedBufFrameCnt; //存放 Audio Adaptive jitter buffer的最小需缓冲 frame  number 量，unit: PCS。
    int m_AAjbMaxNeedBufFrameCnt; //存放 Audio Adaptive jitter buffer的最大需缓冲 frame  number 量，unit: PCS。
    float m_AAjbAdaptSensitivity; //存放 Audio Adaptive jitter buffer Adaptive sensitivity ，The greater the sensitivity, the more adaptive calculations currently need to buffer the number of frames， The value range is [0.0,127.0]。
    VAjb m_VAjbPt; //存放videoAdaptive jitter buffer类对象的内存指针。
    int m_VAjbMinNeedBufFrameCnt; //存放videoAdaptive jitter buffer的最小需缓冲 frame  number 量，unit: PCS。
    int m_VAjbMaxNeedBufFrameCnt; //存放videoAdaptive jitter buffer的最大需缓冲 frame  number 量，unit: PCS。
    float m_VAjbAdaptSensitivity; //存放videoAdaptive jitter buffer Adaptive sensitivity ，The greater the sensitivity, the more adaptive calculations currently need to buffer the number of frames， The value range is [0.0,127.0]。

    byte m_TmpBytePt[]; //存放临 Time  number 据。
    byte m_TmpByte2Pt[]; //存放临 Time  number 据。
    HTInt m_TmpHTIntPt; //存放临 Time  number 据。
    HTInt m_TmpHTInt2Pt; //存放临 Time  number 据。
    HTLong m_TmpHTLongPt; //存放临 Time  number 据。
    HTLong m_TmpHTLong2Pt; //存放临 Time  number 据。

    VarStr m_ErrInfoVarStrPt; //存放错误信息 dynamic digit符串类对象的内存指针，可以 for NULL。

    MyMediaProcThread( Context AppContextPt )
    {
        super( AppContextPt );
    }

    // user 定义的初始化函 number ， в this 线程刚启动 Time 回调一次，返回值表示是否成功， for 0表示成功， for 非0表示失败。
    @Override public int UserInit()
    {
        int p_Result = -1; //存放this 函 number 执行 result 的值， for 0表示成功， for 非0表示失败。

        out:
        {
            {Message p_MessagePt = new Message();p_MessagePt.what = 1;m_MainActivityHandlerPt.sendMessage( p_MessagePt );} //向主界面发送媒体处理线程启动的消息。

            m_IsRecvExitPkt = 0; //Настраивать没 Have接收到退出包。
            if( m_TmpBytePt == null ) m_TmpBytePt = new byte[1024 * 1024]; //初始化临 Time  number 据。
            if( m_TmpByte2Pt == null ) m_TmpByte2Pt = new byte[1024 * 1024]; //初始化临 Time  number 据。
            if( m_TmpHTIntPt == null ) m_TmpHTIntPt = new HTInt(); //初始化临 Time  number 据。
            if( m_TmpHTInt2Pt == null ) m_TmpHTInt2Pt = new HTInt(); //初始化临 Time  number 据。
            if( m_TmpHTLongPt == null ) m_TmpHTLongPt = new HTLong(); //初始化临 Time  number 据。
            if( m_TmpHTLong2Pt == null ) m_TmpHTLong2Pt = new HTLong(); //初始化临 Time  number 据。
            if( m_ErrInfoVarStrPt == null ) //创建并初始化错误信息 dynamic digit符串类对象。
            {
                m_ErrInfoVarStrPt = new VarStr();
                if( m_ErrInfoVarStrPt.Init() != 0 )
                {
                    m_ErrInfoVarStrPt = null;
                }
            }

            if( m_UseWhatXfrPrtcl == 0 ) //如果 use TCPprotocol。
            {
                if( m_IsCreateSrvrOrClnt == 1 ) //如果是创建this 端TCPprotocol服务端套接digit接受远端TCPprotocol客户端套接digit的连接。
                {
                    m_TcpSrvrSoktPt = new TcpSrvrSokt();

                    if( m_TcpSrvrSoktPt.Init( 4, m_IPAddrStrPt, m_PortStrPt, 1, 1, m_ErrInfoVarStrPt ) == 0 ) //如果创建并初始化已监听的this 端TCPprotocol服务端套接digitsuccess.
                    {
                        HTString p_LclNodeAddrPt = new HTString();
                        HTString p_LclNodePortPt = new HTString();

                        if( m_TcpSrvrSoktPt.GetLclAddr( null, p_LclNodeAddrPt, p_LclNodePortPt, m_ErrInfoVarStrPt ) != 0 ) //如果获取已监听的this 端TCPprotocol服务端套接digit绑定的this 地节点地址和port失败。
                        {
                            String p_InfoStrPt = "获取已监听的this 端TCPprotocol服务端套接digit绑定的this 地节点地址和port失败。原因：" + m_ErrInfoVarStrPt.GetStr();
                            Log.e( m_CurClsNameStrPt, p_InfoStrPt );
                            Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                            break out;
                        }

                        String p_InfoStrPt = "创建并初始化已监听的this 端TCPprotocol服务端套接digit[" + p_LclNodeAddrPt.m_Val + ":" + p_LclNodePortPt.m_Val + "]success.";
                        Log.i( m_CurClsNameStrPt, p_InfoStrPt );
                        Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                    }
                    else //如果创建并初始化已监听的this 端TCPprotocol服务端套接digit失败。
                    {
                        String p_InfoStrPt = "创建并初始化已监听的this 端TCPprotocol服务端套接digit[" + m_IPAddrStrPt + ":" + m_PortStrPt + "]失败。原因：" + m_ErrInfoVarStrPt.GetStr();
                        Log.e( m_CurClsNameStrPt, p_InfoStrPt );
                        Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                        break out;
                    }

                    m_TcpClntSoktPt = new TcpClntSokt();
                    HTString p_RmtNodeAddrPt = new HTString();
                    HTString p_RmtNodePortPt = new HTString();

                    while( true ) //循环接受远端TCPprotocol客户端套接digit的连接。
                    {
                        if( m_TcpSrvrSoktPt.Accept( null, p_RmtNodeAddrPt, p_RmtNodePortPt, ( short ) 1, m_TcpClntSoktPt, m_ErrInfoVarStrPt ) == 0 )
                        {
                            if( m_TcpClntSoktPt.GetTcpClntSoktPt() != 0 ) //如果Use monitoredthis 端TCPprotocol服务端套接digit接受远端TCPprotocol客户端套接digit的连接success.
                            {
                                m_TcpSrvrSoktPt.Destroy( null ); //关闭并destroy已创建的this 端TCPprotocol服务端套接digit，防止还 Have其他远端TCPprotocol客户端套接digit继续连接。
                                m_TcpSrvrSoktPt = null;

                                String p_InfoStrPt = "Use monitoredthis 端TCPprotocol服务端套接digit接受远端TCPprotocol客户端套接digit[" + p_RmtNodeAddrPt.m_Val + ":" + p_RmtNodePortPt.m_Val + "]的连接success.";
                                Log.i( m_CurClsNameStrPt, p_InfoStrPt );
                                Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                                break;
                            }
                            else //如果Use monitoredthis 端TCPprotocol服务端套接digit接受远端TCPprotocol客户端套接digit的连接ultra Time ，就重新接受。
                            {

                            }
                        }
                        else
                        {
                            m_TcpClntSoktPt = null;

                            String p_InfoStrPt = "Use monitoredthis 端TCPprotocol服务端套接digit接受远端TCPprotocol客户端套接digit的连接失败。原因：" + m_ErrInfoVarStrPt.GetStr();
                            Log.e( m_CurClsNameStrPt, p_InfoStrPt );
                            Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                            break out;
                        }

                        if( m_ExitFlag != 0 ) //如果this 线程接收到退出请求。
                        {
                            m_TcpClntSoktPt = null;

                            Log.i( m_CurClsNameStrPt, "this 线程接收到退出请求，开始准备退出。" );
                            break out;
                        }
                    }
                }
                else if( m_IsCreateSrvrOrClnt == 0 ) //如果是创建this 端TCPprotocol客户端套接digit连接远端TCPprotocol服务端套接digit。
                {
                    //Ping一下远程节点地址，这样可以快速获取ARP条目。
                    try
                    {
                        Runtime.getRuntime().exec( "ping -c 1 -w 1 " + m_IPAddrStrPt );
                    }
                    catch( Exception ignored )
                    {
                    }

                    m_TcpClntSoktPt = new TcpClntSokt();
                    int p_ReInitTimes = 1;
                    while( true ) //循环连接已监听的远端TCPprotocol服务端套接digit。
                    {
                        if( m_TcpClntSoktPt.Init( 4, m_IPAddrStrPt, m_PortStrPt, null, null, ( short ) 5000, m_ErrInfoVarStrPt ) == 0 ) //如果创建并初始化this 端TCPprotocol客户端套接digit，并连接已监听的远端TCPprotocol服务端套接digitsuccess.
                        {
                            HTString p_LclNodeAddrPt = new HTString();
                            HTString p_LclNodePortPt = new HTString();
                            HTString p_RmtNodeAddrPt = new HTString();
                            HTString p_RmtNodePortPt = new HTString();

                            if( m_TcpClntSoktPt.GetLclAddr( null, p_LclNodeAddrPt, p_LclNodePortPt, m_ErrInfoVarStrPt ) != 0 )
                            {
                                String p_InfoStrPt = "Get connected this 端TCPprotocol客户端套接digit绑定的this 地节点地址和port失败。原因：" + m_ErrInfoVarStrPt.GetStr();
                                Log.e( m_CurClsNameStrPt, p_InfoStrPt );
                                Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                                break out;
                            }
                            if( m_TcpClntSoktPt.GetRmtAddr( null, p_RmtNodeAddrPt, p_RmtNodePortPt, m_ErrInfoVarStrPt ) != 0 )
                            {
                                String p_InfoStrPt = "Get connected this 端TCPprotocol客户端套接digit连接的远端TCPprotocol客户端套接digit绑定的远程节点地址和port失败。原因：" + m_ErrInfoVarStrPt.GetStr();
                                Log.e( m_CurClsNameStrPt, p_InfoStrPt );
                                Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                                break out;
                            }

                            String p_InfoStrPt = "创建并初始化this 端TCPprotocol客户端套接digit[" + p_LclNodeAddrPt.m_Val + ":" + p_LclNodePortPt.m_Val + "]，并连接已监听的远端TCPprotocol服务端套接digit[" + p_RmtNodeAddrPt.m_Val + ":" + p_RmtNodePortPt.m_Val + "]success.";
                            Log.i( m_CurClsNameStrPt, p_InfoStrPt );
                            Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                            break; //跳出重连。
                        }
                        else
                        {
                            {String p_InfoStrPt = "创建并初始化this 端TCPprotocol客户端套接digit，并连接已监听的远端TCPprotocol服务端套接digit[" + m_IPAddrStrPt + ":" + m_PortStrPt + "]失败。原因：" + m_ErrInfoVarStrPt.GetStr();
                            Log.e( m_CurClsNameStrPt, p_InfoStrPt );
                            Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );}

                            if( p_ReInitTimes <= 5 ) //如果还需要进行重连。
                            {
                                String p_InfoStrPt = "开始第 " + p_ReInitTimes + " 次重连。";
                                Log.i( m_CurClsNameStrPt, p_InfoStrPt );
                                Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                                p_ReInitTimes++;
                                SystemClock.sleep( 500 ); //暂停一下，避免CPU use  rate 过high。
                            }
                            else //如果不需要重连了。
                            {
                                m_TcpClntSoktPt = null;
                                break out;
                            }
                        }
                    }
                }

                if( m_TcpClntSoktPt.SetNoDelay( 1, m_ErrInfoVarStrPt ) != 0 ) //如果Настраивать已连接的this 端TCPprotocol客户端套接digit的Nagle延迟算法状态 for 禁用失败。
                {
                    String p_InfoStrPt = "Настраивать已连接的this 端TCPprotocol客户端套接digit的Nagle延迟算法状态 for 禁用失败。原因：" + m_ErrInfoVarStrPt.GetStr();
                    Log.i( m_CurClsNameStrPt, p_InfoStrPt );
                    Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                    break out;
                }
            }
            else //如果 use UDPprotocol。
            {
                m_UdpSoktPt = new UdpSokt();

                if( m_IsCreateSrvrOrClnt == 1 ) //如果是创建this 端UDPprotocol套接digit接受远端UDPprotocol套接digit的连接。
                {
                    if( m_UdpSoktPt.Init( 4, m_IPAddrStrPt, m_PortStrPt, m_ErrInfoVarStrPt ) == 0 ) //如果创建并初始化已监听的this 端UDPprotocol套接digitsuccess.
                    {
                        HTString p_LclNodeAddrPt = new HTString();
                        HTString p_LclNodePortPt = new HTString();

                        if( m_UdpSoktPt.GetLclAddr( null, p_LclNodeAddrPt, p_LclNodePortPt, m_ErrInfoVarStrPt ) != 0 ) //如果获取已监听的this 端UDPprotocol套接digit绑定的this 地节点地址和port失败。
                        {
                            String p_InfoStrPt = "获取已监听的this 端UDPprotocol套接digit绑定的this 地节点地址和port失败。原因：" + m_ErrInfoVarStrPt.GetStr();
                            Log.e( m_CurClsNameStrPt, p_InfoStrPt );
                            Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                            break out;
                        }

                        String p_InfoStrPt = "创建并初始化已监听的this 端UDPprotocol套接digit[" + p_LclNodeAddrPt.m_Val + ":" + p_LclNodePortPt.m_Val + "]success.";
                        Log.i( m_CurClsNameStrPt, p_InfoStrPt );
                        Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                    }
                    else //如果创建并初始化已监听的this 端UDPprotocol套接digit失败。
                    {
                        String p_InfoStrPt = "创建并初始化已监听的this 端UDPprotocol套接digit[" + m_IPAddrStrPt + ":" + m_PortStrPt + "]失败。原因：" + m_ErrInfoVarStrPt.GetStr();
                        Log.e( m_CurClsNameStrPt, p_InfoStrPt );
                        Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                        break out;
                    }

                    HTString p_RmtNodeAddrPt = new HTString();
                    HTString p_RmtNodePortPt = new HTString();
                    HTLong p_TmpHTLong = new HTLong(  );

                    UdpSrvrReAccept:
                    while( true ) //循环接受远端UDPprotocol套接digit的连接。
                    {
                        if( m_UdpSoktPt.RecvPkt( null, p_RmtNodeAddrPt, p_RmtNodePortPt, m_TmpBytePt, m_TmpBytePt.length, p_TmpHTLong, ( short ) 1, m_ErrInfoVarStrPt ) == 0 )
                        {
                            if( p_TmpHTLong.m_Val != -1 ) //如果Use monitoredthis 端UDPprotocol套接digit开始接收远端UDPprotocol套接digit发送的一个 number 据包success.
                            {
                                if( ( p_TmpHTLong.m_Val == 1 ) && ( m_TmpBytePt[0] == PKT_TYP_CNCT_HTBT ) ) //如果是连接请求包。
                                {
                                    m_UdpSoktPt.Connect( 4, p_RmtNodeAddrPt.m_Val, p_RmtNodePortPt.m_Val, null ); //Use monitoredthis 端UDPprotocol套接digit连接已监听的远端UDPprotocol套接digit，已连接的this 端UDPprotocol套接digit只能接收连接的远端UDPprotocol套接digit发送的 number 据包。

                                    int p_ReSendTimes = 1;
                                    UdpSrvrReSend:
                                    while( true ) //循环发送连接请求包，并接收连接Reply package.
                                    {
                                        m_TmpBytePt[0] = PKT_TYP_CNCT_HTBT; //Настраивать连接请求包。
                                        if( m_UdpSoktPt.SendPkt( 4, null, null, m_TmpBytePt, 1, ( short ) 0, m_ErrInfoVarStrPt ) != 0 )
                                        {
                                            String p_InfoStrPt = "Use monitoredthis 端UDPprotocol套接digit接受远端UDPprotocol套接digit[" + p_RmtNodeAddrPt.m_Val + ":" + p_RmtNodePortPt.m_Val + "]的连接失败。原因：" + m_ErrInfoVarStrPt.GetStr();
                                            Log.i( m_CurClsNameStrPt, p_InfoStrPt );
                                            Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                                            break out;
                                        }

                                        UdpSrvrReRecv:
                                        while( true ) //循环接收连接Reply package.
                                        {
                                            if( m_UdpSoktPt.RecvPkt( null, null, null, m_TmpBytePt, m_TmpBytePt.length, p_TmpHTLong, ( short ) 1000, m_ErrInfoVarStrPt ) == 0 )
                                            {
                                                if( p_TmpHTLong.m_Val != -1 ) //如果Use monitoredthis 端UDPprotocol套接digit开始接收远端UDPprotocol套接digit发送的一个 number 据包success.
                                                {
                                                    if( ( p_TmpHTLong.m_Val >= 1 ) && ( m_TmpBytePt[0] != PKT_TYP_CNCT_HTBT ) ) //如果不是连接请求包。
                                                    {
                                                        String p_InfoStrPt = "Use monitoredthis 端UDPprotocol套接digit接受远端UDPprotocol套接digit[" + p_RmtNodeAddrPt.m_Val + ":" + p_RmtNodePortPt.m_Val + "]的连接success.";
                                                        Log.i( m_CurClsNameStrPt, p_InfoStrPt );
                                                        Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                                                        break UdpSrvrReAccept; //跳出连接循环。
                                                    }
                                                    else //如果是连接请求包，就不管，重新接收连接Reply package.
                                                    {

                                                    }
                                                }
                                                else //如果Use monitoredthis 端UDPprotocol套接digit开始接收远端UDPprotocol套接digit发送的一个 number 据包ultra Time 。
                                                {
                                                    if( p_ReSendTimes <= 5 ) //如果还需要进行重发。
                                                    {
                                                        p_ReSendTimes++;
                                                        break UdpSrvrReRecv; //重发连接请求包。
                                                    }
                                                    else //如果不需要重连了。
                                                    {
                                                        String p_InfoStrPt = "Use monitoredthis 端UDPprotocol套接digit接受远端UDPprotocol套接digit的连接失败。原因：" + m_ErrInfoVarStrPt.GetStr();
                                                        Log.e( m_CurClsNameStrPt, p_InfoStrPt );
                                                        Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                                                        break UdpSrvrReSend; //重新接受连接。
                                                    }
                                                }
                                            }
                                            else
                                            {
                                                String p_InfoStrPt = "Use monitoredthis 端UDPprotocol套接digit接受远端UDPprotocol套接digit的连接失败。原因：" + m_ErrInfoVarStrPt.GetStr();
                                                Log.e( m_CurClsNameStrPt, p_InfoStrPt );
                                                Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                                                break UdpSrvrReSend; //重新接受连接。
                                            }
                                        }
                                    }

                                    m_UdpSoktPt.Disconnect( null ); // will 已连接的this 端UDPprotocol套接digit断开连接的远端UDPprotocol套接digit，已连接的this 端UDPprotocol套接digit will 变成已监听的this 端UDPprotocol套接digit。

                                    String p_InfoStrPt = "this 端UDPprotocol套接digit继续保持监听来接受连接。";
                                    Log.i( m_CurClsNameStrPt, p_InfoStrPt );
                                    Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                                }
                                else //如果是其他包，就不管。
                                {

                                }
                            }
                            else //如果Use monitoredthis 端UDPprotocol套接digit接受到远端UDPprotocol套接digit的连接请求ultra Time 。
                            {

                            }
                        }
                        else
                        {
                            String p_InfoStrPt = "Use monitoredthis 端UDPprotocol套接digit接受远端UDPprotocol套接digit的连接失败。原因：" + m_ErrInfoVarStrPt.GetStr();
                            Log.e( m_CurClsNameStrPt, p_InfoStrPt );
                            Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                            break out;
                        }

                        if( m_ExitFlag != 0 ) //如果this 线程接收到退出请求。
                        {
                            Log.i( m_CurClsNameStrPt, "this 线程接收到退出请求，开始准备退出。" );
                            break out;
                        }
                    }
                }
                else if( m_IsCreateSrvrOrClnt == 0 ) //如果是创建this 端UDPprotocol套接digit连接远端UDPprotocol套接digit。
                {
                    //Ping一下远程节点地址，这样可以快速获取ARP条目。
                    try
                    {
                        Runtime.getRuntime().exec( "ping -c 1 -w 1 " + m_IPAddrStrPt );
                    }
                    catch( Exception ignored )
                    {
                    }

                    if( m_UdpSoktPt.Init( 4, null, null, m_ErrInfoVarStrPt ) == 0 ) //如果创建并初始化已监听的this 端UDPprotocol套接digitsuccess.
                    {
                        HTString p_LclNodeAddrPt = new HTString();
                        HTString p_LclNodePortPt = new HTString();

                        if( m_UdpSoktPt.GetLclAddr( null, p_LclNodeAddrPt, p_LclNodePortPt, m_ErrInfoVarStrPt ) != 0 ) //如果获取已监听的this 端UDPprotocol套接digit绑定的this 地节点地址和port失败。
                        {
                            String p_InfoStrPt = "获取已监听的this 端UDPprotocol套接digit绑定的this 地节点地址和port失败。原因：" + m_ErrInfoVarStrPt.GetStr();
                            Log.e( m_CurClsNameStrPt, p_InfoStrPt );
                            Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                            break out;
                        }

                        String p_InfoStrPt = "创建并初始化已监听的this 端UDPprotocol套接digit[" + p_LclNodeAddrPt.m_Val + ":" + p_LclNodePortPt.m_Val + "]success.";
                        Log.i( m_CurClsNameStrPt, p_InfoStrPt );
                        Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                    }
                    else //如果创建并初始化已监听的this 端UDPprotocol套接digit失败。
                    {
                        String p_InfoStrPt = "创建并初始化已监听的this 端UDPprotocol套接digit失败。原因：" + m_ErrInfoVarStrPt.GetStr();
                        Log.e( m_CurClsNameStrPt, p_InfoStrPt );
                        Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                        break out;
                    }

                    HTString p_RmtNodeAddrPt = new HTString();
                    HTString p_RmtNodePortPt = new HTString();
                    HTLong p_TmpHTLong = new HTLong(  );

                    if( m_UdpSoktPt.Connect( 4, m_IPAddrStrPt, m_PortStrPt, m_ErrInfoVarStrPt ) != 0 )
                    {
                        String p_InfoStrPt = "Use monitoredthis 端UDPprotocol套接digit连接已监听的远端UDPprotocol套接digit[" + m_IPAddrStrPt + ":" + m_PortStrPt + "]失败。原因：" + m_ErrInfoVarStrPt.GetStr();
                        Log.i( m_CurClsNameStrPt, p_InfoStrPt );
                        Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                        break out;
                    }

                    if( m_UdpSoktPt.GetRmtAddr( null, p_RmtNodeAddrPt, p_RmtNodePortPt, m_ErrInfoVarStrPt ) != 0 )
                    {
                        m_UdpSoktPt.Disconnect( null );
                        String p_InfoStrPt = "Get connected this 端UDPprotocol套接digit连接的远端UDPprotocol套接digit绑定的远程节点地址和port失败。原因：" + m_ErrInfoVarStrPt.GetStr();
                        Log.e( m_CurClsNameStrPt, p_InfoStrPt );
                        Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                        break out;
                    }

                    int p_ReSendTimes = 1;
                    UdpClntReSend:
                    while( true ) //循环连接已监听的远端UDPprotocol套接digit。
                    {
                        m_TmpBytePt[0] = PKT_TYP_CNCT_HTBT; //Настраивать连接请求包。
                        if( m_UdpSoktPt.SendPkt( 4, null, null, m_TmpBytePt, 1, ( short ) 0, m_ErrInfoVarStrPt ) != 0 )
                        {
                            m_UdpSoktPt.Disconnect( m_ErrInfoVarStrPt );
                            String p_InfoStrPt = "Use monitoredthis 端UDPprotocol套接digit连接已监听的远端UDPprotocol套接digit[" + p_RmtNodeAddrPt.m_Val + ":" + p_RmtNodePortPt.m_Val + "]失败。原因：" + m_ErrInfoVarStrPt.GetStr();
                            Log.i( m_CurClsNameStrPt, p_InfoStrPt );
                            Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                            break out;
                        }

                        UdpClntReRecv:
                        while( true ) //循环接收连接请求包。
                        {
                            if( m_UdpSoktPt.RecvPkt( null, null, null, m_TmpBytePt, m_TmpBytePt.length, p_TmpHTLong, ( short ) 1000, m_ErrInfoVarStrPt ) == 0 )
                            {
                                if( p_TmpHTLong.m_Val != -1 ) //如果Use connected this  端UDPprotocol套接digit开始接收远端UDPprotocol套接digit发送的一个 number 据包success.
                                {
                                    if( ( p_TmpHTLong.m_Val == 1 ) && ( m_TmpBytePt[0] == PKT_TYP_CNCT_HTBT ) ) //如果是连接请求包。
                                    {
                                        m_TmpBytePt[0] = PKT_TYP_ACK; //Настраивать连接Reply package.
                                        if( m_UdpSoktPt.SendPkt( 4, null, null, m_TmpBytePt, 1, ( short ) 0, m_ErrInfoVarStrPt ) != 0 )
                                        {
                                            m_UdpSoktPt.Disconnect( m_ErrInfoVarStrPt );
                                            String p_InfoStrPt = "Use monitoredthis 端UDPprotocol套接digit连接已监听的远端UDPprotocol套接digit[" + p_RmtNodeAddrPt.m_Val + ":" + p_RmtNodePortPt.m_Val + "]失败。原因：" + m_ErrInfoVarStrPt.GetStr();
                                            Log.e( m_CurClsNameStrPt, p_InfoStrPt );
                                            Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                                            break out;
                                        }

                                        String p_InfoStrPt = "Use monitoredthis 端UDPprotocol套接digit连接已监听的远端UDPprotocol套接digit[" + p_RmtNodeAddrPt.m_Val + ":" + p_RmtNodePortPt.m_Val + "]success.";
                                        Log.i( m_CurClsNameStrPt, p_InfoStrPt );
                                        Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                                        break UdpClntReSend; //跳出连接循环。
                                    }
                                    else //如果不是连接请求包，就不管，重新接收连接请求包。
                                    {

                                    }
                                }
                                else //如果Use connected this  端UDPprotocol套接digit开始接收远端UDPprotocol套接digit发送的一个 number 据包ultra Time 。
                                {
                                    if( p_ReSendTimes <= 5 ) //如果还需要进行重发。
                                    {
                                        p_ReSendTimes++;
                                        break UdpClntReRecv; //重发连接请求包。
                                    }
                                    else //如果不需要重连了。
                                    {
                                        m_UdpSoktPt.Disconnect( m_ErrInfoVarStrPt );
                                        String p_InfoStrPt = "Use monitoredthis 端UDPprotocol套接digit连接已监听的远端UDPprotocol套接digit[" + p_RmtNodeAddrPt.m_Val + ":" + p_RmtNodePortPt.m_Val + "]失败。原因：" + m_ErrInfoVarStrPt.GetStr();
                                        Log.e( m_CurClsNameStrPt, p_InfoStrPt );
                                        Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                                        break out;
                                    }
                                }
                            }
                            else
                            {
                                m_UdpSoktPt.Disconnect( m_ErrInfoVarStrPt );
                                String p_InfoStrPt = "Use monitoredthis 端UDPprotocol套接digit连接已监听的远端UDPprotocol套接digit失败。原因：" + m_ErrInfoVarStrPt.GetStr();
                                Log.e( m_CurClsNameStrPt, p_InfoStrPt );
                                Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                                break out;
                            }
                        }
                    }
                }
            } //protocol连接结束。

            switch( m_UseWhatRecvOutputFrame ) // use 什么 Receive output  frame 。
            {
                case 0: //如果 use Linked list。
                {
                    //初始化接收 Audio  Output  frame Linked list类对象。
                    m_RecvAudioOutputFrameLnkLstPt = new LinkedList< byte[] >(); //创建接收 Audio  Output  frame Linked list类对象。
                    Log.i( m_CurClsNameStrPt, "创建并初始化接收 Audio  Output  frame Linked list对象success." );

                    //初始化接收video Output  frame Linked list类对象。
                    m_RecvVideoOutputFrameLnkLstPt = new LinkedList< byte[] >(); //创建接收video Output  frame Linked list类对象。
                    Log.i( m_CurClsNameStrPt, "创建并初始化接收video Output  frame Linked list对象success." );
                    break;
                }
                case 1: //如果 use Adaptive jitter buffer。
                {
                    //初始化 Audio Adaptive jitter buffer类对象。
                    m_AAjbPt = new AAjb();
                    if( m_AAjbPt.Init( m_AudioOutputPt.m_SamplingRate, m_AudioOutputPt.m_FrameLen, 1, 1, 0, m_AAjbMinNeedBufFrameCnt, m_AAjbMaxNeedBufFrameCnt, m_AAjbAdaptSensitivity, 1 ) == 0 )
                    {
                        Log.i( m_CurClsNameStrPt, "创建并初始化 Audio Adaptive jitter buffer类对象success." );
                    }
                    else
                    {
                        String p_InfoStrPt = "创建并初始化 Audio Adaptive jitter buffer类对象失败。";
                        Log.e( m_CurClsNameStrPt, p_InfoStrPt );
                        Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                        break out;
                    }

                    //初始化videoAdaptive jitter buffer类对象。
                    m_VAjbPt = new VAjb();
                    if( m_VAjbPt.Init( 1, m_VAjbMinNeedBufFrameCnt, m_VAjbMaxNeedBufFrameCnt, m_VAjbAdaptSensitivity, 1 ) == 0 )
                    {
                        Log.i( m_CurClsNameStrPt, "创建并初始化videoAdaptive jitter buffer类对象success." );
                    }
                    else
                    {
                        String p_InfoStrPt = "创建并初始化videoAdaptive jitter buffer类对象失败。";
                        Log.e( m_CurClsNameStrPt, p_InfoStrPt );
                        Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                        break out;
                    }
                    break;
                }
            }

            m_LastPktSendTime = System.currentTimeMillis(); //Настраивать最后一个 number 据包的发送 Time 间 for 当前 Time 间。
            m_LastPktRecvTime = m_LastPktSendTime; //Настраивать最后一个 number 据包的接收 Time 间 for 当前 Time 间。

            m_LastSendAudioInputFrameIsAct = 0; //Настраивать最后发送的一个 Audio  enter  frame  for 无  voice sound live动。
            m_LastSendAudioInputFrameIsRecv = 1; //Настраивать最后一个发送的 Audio  enter  frame 远端已经接收到。
            m_LastSendAudioInputFrameTimeStamp = 0 - 1; //Настраивать最后一个发送 Audio  enter  frame 的 Time 间戳 for 0的前一个，因 for 第一次发送 Audio  enter  frame  Time 会递增一个步进。
            m_LastSendVideoInputFrameTimeStamp = 0 - 1; //Настраивать最后一个发送video enter  frame 的 Time 间戳 for 0的前一个，因 for 第一次发送video enter  frame  Time 会递增一个步进。

            m_LastGetAudioOutputFrameIsAct = 0; //Настраивать最后一个取出的 Audio  Output  frame  for 无  voice sound live动，因 for 如果 Do not use  Audio  Output ，只 use video Output  Time ，可以保证video正常 Output 。
            m_LastGetAudioOutputFrameVideoOutputFrameTimeStamp = 0; //Настраивать最后一个取出的 Audio  Output  frame 对应video Output  frame 的 Time 间戳 for 0。

            String p_InfoStrPt = "开始进行对讲。";
            Log.i( m_CurClsNameStrPt, p_InfoStrPt );
            Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );

            p_Result = 0; //Настраиватьthis 函 number 执行success.
        }

        return p_Result;
    }

    // user 定义的处理函 number ， в this 线程运行 Time 每隔1 millisecond就回调一次，返回值表示是否成功， for 0表示成功， for 非0表示失败。
    @Override public int UserProcess()
    {
        int p_Result = -1; //存放this 函 number 执行 result 的值， for 0表示成功， for 非0表示失败。
        int p_TmpInt;

        out:
        {
            //接收远端发送过来的一个 number 据包。
            if( ( ( m_UseWhatXfrPrtcl == 0 ) && ( m_TcpClntSoktPt.RecvPkt( m_TmpBytePt, m_TmpBytePt.length, m_TmpHTLongPt, ( short ) 0, m_ErrInfoVarStrPt ) == 0 ) ) ||
                ( ( m_UseWhatXfrPrtcl == 1 ) && ( m_UdpSoktPt.RecvPkt( null, null, null, m_TmpBytePt, m_TmpBytePt.length, m_TmpHTLongPt, ( short ) 0, m_ErrInfoVarStrPt ) == 0 ) ) )
            {
                if( m_TmpHTLongPt.m_Val != -1 ) //如果Use connected this  The end socket starts to receive the one sent by the connected remote socket number 据包success.
                {
                    m_LastPktRecvTime = System.currentTimeMillis(); //记录最后一个 number 据包的接收 Time 间。

                    if( m_TmpHTLongPt.m_Val == 0 ) //如果 number 据包的 number According to the length  for 0。
                    {
                        Log.e( m_CurClsNameStrPt, "Receive one number 据包的 number According to the length  for " + m_TmpHTLongPt.m_Val + "，表示没 Have number 据，无法继续接收。" );
                        break out;
                    }
                    else if( m_TmpBytePt[0] == PKT_TYP_CNCT_HTBT ) //如果是心跳包。
                    {
                        if( m_TmpHTLongPt.m_Val > 1 ) //如果心跳包的 number According to the length  more than the 1。
                        {
                            Log.e( m_CurClsNameStrPt, "Receive one心跳包的 number According to the length  for " + m_TmpHTLongPt.m_Val + " more than the 1，表示还 Have其他 number 据，无法继续接收。" );
                            break out;
                        }

                        Log.i( m_CurClsNameStrPt, "Receive one心跳包。" );
                    }
                    else if( m_TmpBytePt[0] == PKT_TYP_AFRAME ) //如果是 Audio  Output  frame 包。
                    {
                        if( m_TmpHTLongPt.m_Val < 1 + 4 ) //如果 Audio  Output  frame 包的 number According to the length 小于1 + 4，表示没 Have Audio  Output  frame  Time 间戳。
                        {
                            Log.e( m_CurClsNameStrPt, "Receive one Audio  Output  frame 包的 number According to the length  for " + m_TmpHTLongPt.m_Val + "小于1 + 4，表示没 Have Audio  Output  frame  Time 间戳，无法继续接收。" );
                            break out;
                        }

                        //读取 Audio  Output  frame  Time 间戳。
                        p_TmpInt = ( m_TmpBytePt[1] & 0xFF ) + ( ( m_TmpBytePt[2] & 0xFF ) << 8 ) + ( ( m_TmpBytePt[3] & 0xFF ) << 16 ) + ( ( m_TmpBytePt[4] & 0xFF ) << 24 );

                        if( m_AudioOutputPt.m_IsUseAudioOutput != 0 ) //如果要 use  Audio  Output 。
                        {
                            // will  Audio  Output  frame 放入Linked list或Adaptive jitter buffer。
                            switch( m_UseWhatRecvOutputFrame ) // use 什么 Receive output  frame 。
                            {
                                case 0: //如果 use Linked list。
                                {
                                    if( m_TmpHTLongPt.m_Val > 1 + 4 ) //如果该 Audio  Output  frame  for  Have  voice sound live动。
                                    {
                                        synchronized( m_RecvAudioOutputFrameLnkLstPt )
                                        {
                                            m_RecvAudioOutputFrameLnkLstPt.addLast( Arrays.copyOfRange( m_TmpBytePt, 1 + 4, ( int ) ( m_TmpHTLongPt.m_Val ) ) );
                                        }
                                        Log.i( m_CurClsNameStrPt, "Receive one Have  voice sound live动的 Audio  Output  frame 包，并放入接收 Audio  Output  frame Linked listsuccess. Audio  Output  frame  Time 间戳：" + p_TmpInt + "，总长度：" + m_TmpHTLongPt.m_Val + "。" );
                                    }
                                    else //如果该 Audio  Output  frame  for 无  voice sound live动。
                                    {
                                        Log.i( m_CurClsNameStrPt, "Receive one无  voice sound live动的 Audio  Output  frame 包，无需放入接收 Audio  Output  frame Linked list。 Audio  Output  frame  Time 间戳：" + p_TmpInt + "，总长度：" + m_TmpHTLongPt.m_Val + "。" );
                                    }
                                    break;
                                }
                                case 1: //如果 use Adaptive jitter buffer。
                                {
                                    synchronized( m_AAjbPt )
                                    {
                                        if( m_TmpHTLongPt.m_Val > 1 + 4 ) //如果该 Audio  Output  frame  for  Have  voice sound live动。
                                        {
                                            m_AAjbPt.PutOneByteFrame( p_TmpInt, m_TmpBytePt, 1 + 4, m_TmpHTLongPt.m_Val - 1 - 4 );
                                            Log.i( m_CurClsNameStrPt, "Receive one Have  voice sound live动的 Audio  Output  frame 包，并放入 Audio Adaptive jitter buffersuccess. Audio  Output  frame  Time 间戳：" + p_TmpInt + "，总长度：" + m_TmpHTLongPt.m_Val + "。" );
                                        }
                                        else //如果该 Audio  Output  frame  for 无  voice sound live动。
                                        {
                                            m_AAjbPt.PutOneByteFrame( p_TmpInt, m_TmpBytePt, 1 + 4, 0 );
                                            Log.i( m_CurClsNameStrPt, "Receive one无  voice sound live动的 Audio  Output  frame 包，并放入 Audio Adaptive jitter buffersuccess. Audio  Output  frame  Time 间戳：" + p_TmpInt + "，总长度：" + m_TmpHTLongPt.m_Val + "。" );
                                        }

                                        HTInt p_CurHaveBufActFrameCntPt = new HTInt(); //存放当前已缓冲 Have live动 frame 的 number 量。
                                        HTInt p_CurHaveBufInactFrameCntPt = new HTInt(); //存放当前已缓冲无 live动 frame 的 number 量。
                                        HTInt p_CurHaveBufFrameCntPt = new HTInt(); //存放当前已缓冲 frame 的 number 量。
                                        HTInt p_MinNeedBufFrameCntPt = new HTInt(); //存放Minimum number of frames to be buffered。
                                        HTInt p_MaxNeedBufFrameCntPt = new HTInt(); //Maximum buffer required for storage  frame 的 number 量。
                                        HTInt p_CurNeedBufFrameCntPt = new HTInt(); //存放当前需缓冲 frame 的 number 量。
                                        m_AAjbPt.GetBufFrameCnt( p_CurHaveBufActFrameCntPt, p_CurHaveBufInactFrameCntPt, p_CurHaveBufFrameCntPt, p_MinNeedBufFrameCntPt, p_MaxNeedBufFrameCntPt, p_CurNeedBufFrameCntPt );
                                        Log.i( m_CurClsNameStrPt, " Audio Adaptive jitter buffer： Have live动 frame ：" + p_CurHaveBufActFrameCntPt.m_Val + "，无 live动 frame ：" + p_CurHaveBufInactFrameCntPt.m_Val + "， frame ：" + p_CurHaveBufFrameCntPt.m_Val + "，最小需 frame ：" + p_MinNeedBufFrameCntPt.m_Val + "，最大需 frame ：" + p_MaxNeedBufFrameCntPt.m_Val + "，当前需 frame ：" + p_CurNeedBufFrameCntPt.m_Val + "。" );
                                    }
                                    break;
                                }
                            }
                        }
                        else //如果 Do not use  Audio  Output 。
                        {
                            if( m_TmpHTLongPt.m_Val > 1 + 4 ) //如果该 Audio  Output  frame  for  Have  voice sound live动。
                            {
                                Log.i( m_CurClsNameStrPt, "Receive one Have  voice sound live动的 Audio  Output  frame 包成功，但 Do not use  Audio  Output 。 Audio  Output  frame  Time 间戳：" + p_TmpInt + "，总长度：" + m_TmpHTLongPt.m_Val + "。" );
                            }
                            else //如果该 Audio  Output  frame  for 无  voice sound live动。
                            {
                                Log.i( m_CurClsNameStrPt, "Receive one无  voice sound live动的 Audio  Output  frame 包成功，但 Do not use  Audio  Output 。 Audio  Output  frame  Time 间戳：" + p_TmpInt + "，总长度：" + m_TmpHTLongPt.m_Val + "。" );
                            }
                        }

                        if( ( m_UseWhatXfrPrtcl == 1 ) && ( m_TmpHTLongPt.m_Val == 1 + 4 ) ) //如果是 use UDPprotocol，且this  Audio  Output  frame  for 无  voice sound live动。
                        {
                            //Настраивать Audio  Output  frame Reply package.
                            m_TmpBytePt[0] = PKT_TYP_ACK;
                            //Настраивать Audio  Output  frame  Time 间戳。
                            m_TmpBytePt[1] = ( byte ) ( p_TmpInt & 0xFF );
                            m_TmpBytePt[2] = ( byte ) ( ( p_TmpInt & 0xFF00 ) >> 8 );
                            m_TmpBytePt[3] = ( byte ) ( ( p_TmpInt & 0xFF0000 ) >> 16 );
                            m_TmpBytePt[4] = ( byte ) ( ( p_TmpInt & 0xFF000000 ) >> 24 );

                            if( m_UdpSoktPt.SendPkt( 4, null, null, m_TmpBytePt, 1 + 4, ( short ) 0, m_ErrInfoVarStrPt ) == 0 )
                            {
                                m_LastPktSendTime = System.currentTimeMillis(); //Настраивать最后一个 number 据包的发送 Time 间。
                                Log.i( m_CurClsNameStrPt, "Send one  Audio  Output  frame 应答包success. Time 间戳：" + p_TmpInt + "，总长度：" + 1 + 4 + "。" );
                            }
                            else
                            {
                                String p_InfoStrPt = "Send one  Audio  Output  frame 应答The package failed. the reason：" + m_ErrInfoVarStrPt.GetStr();
                                Log.e( m_CurClsNameStrPt, p_InfoStrPt );
                                Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                                break out;
                            }
                        }
                    }
                    else if( m_TmpBytePt[0] == PKT_TYP_VFRAME ) //如果是video Output  frame 包。
                    {
                        if( m_TmpHTLongPt.m_Val < 1 + 4 ) //如果video Output  frame 包的 number According to the length 小于1 + 4，表示没 Havevideo Output  frame  Time 间戳。
                        {
                            Log.e( m_CurClsNameStrPt, "Receive onevideo Output  frame 包的 number According to the length  for " + m_TmpHTLongPt.m_Val + "小于1 + 4，表示没 Havevideo Output  frame  Time 间戳，无法继续接收。" );
                            break out;
                        }

                        //读取video Output  frame  Time 间戳。
                        p_TmpInt = ( m_TmpBytePt[1] & 0xFF ) + ( ( m_TmpBytePt[2] & 0xFF ) << 8 ) + ( ( m_TmpBytePt[3] & 0xFF ) << 16 ) + ( ( m_TmpBytePt[4] & 0xFF ) << 24 );

                        if( m_VideoOutputPt != null ) //如果要 use video Output 。
                        {
                            // will video Output  frame 放入Linked list或Adaptive jitter buffer。
                            switch( m_UseWhatRecvOutputFrame ) // use 什么 Receive output  frame 。
                            {
                                case 0: //如果 use Linked list。
                                {
                                    if( m_TmpHTLongPt.m_Val > 1 + 4 ) //如果该video Output  frame  for  Have图像 live动。
                                    {
                                        synchronized( m_RecvVideoOutputFrameLnkLstPt )
                                        {
                                            m_RecvVideoOutputFrameLnkLstPt.addLast( Arrays.copyOfRange( m_TmpBytePt, 1 + 4, ( int ) ( m_TmpHTLongPt.m_Val ) ) );
                                        }
                                        Log.i( m_CurClsNameStrPt, "Receive one Have图像 live动的video Output  frame 包，并放入接收video Output  frame Linked listsuccess.video Output  frame  Time 间戳：" + p_TmpInt + "，总长度：" + m_TmpHTLongPt.m_Val + "。" );
                                    }
                                    else //如果该video Output  frame  for 无图像 live动。
                                    {
                                        Log.i( m_CurClsNameStrPt, "Receive one无图像 live动的video Output  frame 包，无需放入接收video Output  frame Linked list。video Output  frame  Time 间戳：" + p_TmpInt + "，总长度：" + m_TmpHTLongPt.m_Val + "。" );
                                    }

                                    break;
                                }
                                case 1: //如果 use Adaptive jitter buffer。
                                {
                                    synchronized( m_VAjbPt )
                                    {
                                        if( m_TmpHTLongPt.m_Val > 1 + 4 ) //如果该video Output  frame  for  Have图像 live动。
                                        {
                                            m_VAjbPt.PutOneByteFrame( System.currentTimeMillis(), p_TmpInt, m_TmpBytePt, 1 + 4, m_TmpHTLongPt.m_Val - 1 - 4 );
                                            Log.i( m_CurClsNameStrPt, "Receive one Have图像 live动的video Output  frame 包，并放入videoAdaptive jitter buffersuccess.video Output  frame  Time 间戳：" + p_TmpInt + "，总长度：" + m_TmpHTLongPt.m_Val + "， Types of ：" + ( m_TmpBytePt[13] & 0xff ) + "。" );
                                        }
                                        else //如果该video Output  frame  for 无图像 live动。
                                        {
                                            Log.i( m_CurClsNameStrPt, "Receive one无图像 live动的video Output  frame 包，无需放入videoAdaptive jitter buffer。video Output  frame  Time 间戳：" + p_TmpInt + "，总长度：" + m_TmpHTLongPt.m_Val + "。" );
                                        }

                                        HTInt p_CurHaveBufFrameCntPt = new HTInt(); //存放当前已缓冲 frame 的 number 量。
                                        HTInt p_MinNeedBufFrameCntPt = new HTInt(); //存放Minimum number of frames to be buffered。
                                        HTInt p_MaxNeedBufFrameCntPt = new HTInt(); //Maximum buffer required for storage  frame 的 number 量。
                                        HTInt p_CurNeedBufFrameCntPt = new HTInt(); //存放当前需缓冲 frame 的 number 量。
                                        m_VAjbPt.GetBufFrameCnt( p_CurHaveBufFrameCntPt, p_MinNeedBufFrameCntPt, p_MaxNeedBufFrameCntPt, p_CurNeedBufFrameCntPt );
                                        Log.i( m_CurClsNameStrPt, "videoAdaptive jitter buffer： frame ：" + p_CurHaveBufFrameCntPt.m_Val + "，最小需 frame ：" + p_MinNeedBufFrameCntPt.m_Val + "，最大需 frame ：" + p_MaxNeedBufFrameCntPt.m_Val + "，当前需 frame ：" + p_CurNeedBufFrameCntPt.m_Val + "。" );
                                    }

                                    break;
                                }
                            }
                        }
                        else //如果 Do not use video Output 。
                        {
                            if( m_TmpHTLongPt.m_Val > 1 + 4 ) //如果该video Output  frame  for  Have图像 live动。
                            {
                                Log.i( m_CurClsNameStrPt, "Receive one Have图像 live动的video Output  frame 包成功，但 Do not use video Output 。video Output  frame  Time 间戳：" + p_TmpInt + "，总长度：" + m_TmpHTLongPt.m_Val + "。" );
                            }
                            else //如果该video Output  frame  for 无图像 live动。
                            {
                                Log.i( m_CurClsNameStrPt, "Receive one无图像 live动的video Output  frame 包成功，但 Do not use video Output 。video Output  frame  Time 间戳：" + p_TmpInt + "，总长度：" + m_TmpHTLongPt.m_Val + "。" );
                            }
                        }
                    }
                    else if( m_TmpBytePt[0] == PKT_TYP_ACK ) //如果是连接应答包或soundvideo enter  Output  frame Reply package.
                    {
                        if( m_TmpHTLongPt.m_Val == 1 ) //如果 number 据包的 number According to the length 等于1，表示是连接应答包，就不管。
                        {

                        }
                        else //如果 number 据包的 number According to the length  more than the 1，表示是soundvideo enter  Output  frame Reply package.
                        {
                            if( m_TmpHTLongPt.m_Val != 1 + 4 )
                            {
                                Log.e( m_CurClsNameStrPt, "Receive onesoundvideo enter  Output  frame 应答包的 number According to the length  for " + m_TmpHTLongPt.m_Val + "不等于1 + 4，表示格式不正确，无法继续接收。" );
                                break out;
                            }

                            //读取 Time 间戳。
                            p_TmpInt = ( m_TmpBytePt[1] & 0xFF ) + ( ( m_TmpBytePt[2] & 0xFF ) << 8 ) + ( ( m_TmpBytePt[3] & 0xFF ) << 16 ) + ( ( m_TmpBytePt[4] & 0xFF ) << 24 );

                            Log.i( m_CurClsNameStrPt, "Receive onesoundvideo enter  Output  frame Reply package. Time 间戳：" + p_TmpInt + "，总长度：" + m_TmpHTLongPt.m_Val + "。" );

                            //Настраивать最后一个发送的 Audio  enter  frame 远端是否接收到。
                            if( m_LastSendAudioInputFrameTimeStamp == p_TmpInt ) m_LastSendAudioInputFrameIsRecv = 1;
                        }
                    }
                    else if( m_TmpBytePt[0] == PKT_TYP_EXIT ) //如果是退出包。
                    {
                        if( m_TmpHTLongPt.m_Val > 1 ) //如果退出包的 number According to the length  more than the 1。
                        {
                            Log.e( m_CurClsNameStrPt, "Receive one退出包的 number According to the length  for " + m_TmpHTLongPt.m_Val + " more than the 1，表示还 Have其他 number 据，无法继续接收。" );
                            break out;
                        }

                        m_IsRecvExitPkt = 1; //Настраивать已经接收到退出包。
                        RequireExit( 1, 0 ); //请求退出。

                        String p_InfoStrPt = "Receive one退出包。";
                        Log.i( m_CurClsNameStrPt, p_InfoStrPt );
                        Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                    }
                }
                else //如果Use connected this  The end socket starts to receive the one sent by the connected remote socket number 据包ultra Time 。
                {

                }
            }
            else //如果Use connected this  The end socket starts to receive the one sent by the connected remote socket number 据包失败。
            {
                String p_InfoStrPt = "Use connected this  The end socket starts to receive the one sent by the connected remote socket number 据The package failed. the reason：" + m_ErrInfoVarStrPt.GetStr();
                Log.e( m_CurClsNameStrPt, p_InfoStrPt );
                Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                break out;
            }

            //发送心跳包。
            if( System.currentTimeMillis() - m_LastPktSendTime >= 100 ) //如果ultra过100 millisecond没 Have发送任何 number 据包，就Send one 心跳包。
            {
                m_TmpBytePt[0] = PKT_TYP_CNCT_HTBT; //Настраивать心跳包。
                if( ( ( m_UseWhatXfrPrtcl == 0 ) && ( m_TcpClntSoktPt.SendPkt( m_TmpBytePt, 1, ( short ) 0, m_ErrInfoVarStrPt ) == 0 ) ) ||
                    ( ( m_UseWhatXfrPrtcl == 1 ) && ( m_UdpSoktPt.SendPkt( 4, null, null, m_TmpBytePt, 1, ( short ) 0, m_ErrInfoVarStrPt ) == 0 ) ) )
                {
                    m_LastPktSendTime = System.currentTimeMillis(); //记录最后一个 number 据包的发送 Time 间。
                    Log.i( m_CurClsNameStrPt, "Send one The heartbeat package is successful." );
                }
                else
                {
                    String p_InfoStrPt = "Send one Heartbeat The package failed. the reason：" + m_ErrInfoVarStrPt.GetStr();
                    Log.e( m_CurClsNameStrPt, p_InfoStrPt );
                    Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                    break out;
                }
            }

            //Determine whether the socket connection is broken.
            if( System.currentTimeMillis() - m_LastPktRecvTime > 2000 ) //如果ultra过2000 millisecond没 Have接收任何 number 据包，就判定连接已经断开了。
            {
                String p_InfoStrPt = "ultra过2000 millisecond没 Have接收任何 number 据包，判定套接digit连接已经断开了。";
                Log.e( m_CurClsNameStrPt, p_InfoStrPt );
                Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                break out;
            }

            //如果 Do not use  Audio  enter ，就取出并写入 Audio  Output  frame 。
            if( m_AudioInputPt.m_IsUseAudioInput == 0 ) GetAndWriteVideoOutputFrame( m_TmpBytePt, m_TmpHTIntPt, m_TmpHTLongPt );

            p_Result = 0; //Настраиватьthis 函 number 执行success.
        }

        return p_Result;
    }

    // user 定义的destroy函 number ， в this 线程退出 Time 回调一次。
    @Override public void UserDestroy()
    {
        SendExitPkt:
        if( ( m_ExitFlag == 1 ) && ( ( m_TcpClntSoktPt != null ) || ( ( m_UdpSoktPt != null ) && ( m_UdpSoktPt.GetRmtAddr( null, null, null, null ) == 0 ) ) ) ) //如果this 线程接收到退出请求，且this 端TCPprotocol客户端套接digit类对象不 for 空或this 端UDPprotocol套接digit类对象不 for 空且已连接远端。
        {
            //循环发送退出包。
            m_TmpBytePt[0] = PKT_TYP_EXIT; //Настраивать退出包。
            for( int p_SendTimes = ( m_UseWhatXfrPrtcl == 0 ) ? 1 : 5; p_SendTimes > 0; p_SendTimes-- )
            {
                if( ( ( m_UseWhatXfrPrtcl == 0 ) && ( m_TcpClntSoktPt.SendPkt( m_TmpBytePt, 1, ( short ) 0, m_ErrInfoVarStrPt ) != 0 ) ) ||
                    ( ( m_UseWhatXfrPrtcl == 1 ) && ( m_UdpSoktPt.SendPkt( 4, null, null, m_TmpBytePt, 1, ( short ) 0, m_ErrInfoVarStrPt ) != 0 ) ) )
                {
                    String p_InfoStrPt = "Send one 退出The package failed. the reason：" + m_ErrInfoVarStrPt.GetStr();
                    Log.e( m_CurClsNameStrPt, p_InfoStrPt );
                    Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                    break SendExitPkt;
                }
            }

            m_LastPktSendTime = System.currentTimeMillis(); //记录最后一个 number 据包的发送 Time 间。

            {String p_InfoStrPt = "Send one 退出包success.";
            Log.i( m_CurClsNameStrPt, p_InfoStrPt );
            Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );}

            if( m_IsRecvExitPkt == 0 ) //如果没 Have接收到退出包。
            {
                while( true ) //循环接收退出包。
                {
                    if( ( ( m_UseWhatXfrPrtcl == 0 ) && ( m_TcpClntSoktPt.RecvPkt( m_TmpBytePt, m_TmpBytePt.length, m_TmpHTLongPt, ( short ) 5000, m_ErrInfoVarStrPt ) == 0 ) ) ||
                        ( ( m_UseWhatXfrPrtcl == 1 ) && ( m_UdpSoktPt.RecvPkt( null, null, null, m_TmpBytePt, m_TmpBytePt.length, m_TmpHTLongPt, ( short ) 5000, m_ErrInfoVarStrPt ) == 0 ) ) )
                    {
                        if( m_TmpHTLongPt.m_Val != -1 ) //如果Use connected this  The end socket starts to receive the one sent by the connected remote socket number 据包success.
                        {
                            m_LastPktRecvTime = System.currentTimeMillis(); //记录最后一个 number 据包的接收 Time 间。

                            if( ( m_TmpHTLongPt.m_Val == 1 ) && ( m_TmpBytePt[0] == PKT_TYP_EXIT ) ) //如果是退出包。
                            {
                                String p_InfoStrPt = "An exit packet was received.";
                                Log.i( m_CurClsNameStrPt, p_InfoStrPt );
                                Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                                break SendExitPkt;
                            }
                            else //如果是其他包，继续接收。
                            {

                            }
                        }
                        else //如果Use connected this  The end socket starts to receive the one sent by the connected remote socket number 据包ultra Time 。
                        {
                            String p_InfoStrPt = "Use connected this  The end socket starts to receive the one sent by the connected remote socket number 据The package failed. the reason：" + m_ErrInfoVarStrPt.GetStr();
                            Log.e( m_CurClsNameStrPt, p_InfoStrPt );
                            Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                            break SendExitPkt;
                        }
                    }
                    else //如果Use connected this  The end socket starts to receive the one sent by the connected remote socket number 据包失败。
                    {
                        String p_InfoStrPt = "Use connected this  The end socket starts to receive the one sent by the connected remote socket number 据The package failed. the reason：" + m_ErrInfoVarStrPt.GetStr();
                        Log.e( m_CurClsNameStrPt, p_InfoStrPt );
                        Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                        break SendExitPkt;
                    }
                }
            }
        }

        //destroythis 端TCPprotocol服务端套接digit。
        if( m_TcpSrvrSoktPt != null )
        {
            m_TcpSrvrSoktPt.Destroy( null ); //关闭并destroy已创建的this 端TCPprotocol服务端套接digit。
            m_TcpSrvrSoktPt = null;

            String p_InfoStrPt = "关闭并destroy已创建的this 端TCPprotocol服务端套接digitsuccess.";
            Log.i( m_CurClsNameStrPt, p_InfoStrPt );
            Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
        }

        //destroythis 端TCPprotocol客户端套接digit。
        if( m_TcpClntSoktPt != null )
        {
            m_TcpClntSoktPt.Destroy( ( short ) -1, null ); //关闭并destroy已创建的this 端TCPprotocol客户端套接digit。
            m_TcpClntSoktPt = null;

            String p_InfoStrPt = "关闭并destroy已创建的this 端TCPprotocol客户端套接digitsuccess.";
            Log.i( m_CurClsNameStrPt, p_InfoStrPt );
            Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
        }

        //destroythis 端UDPprotocol套接digit。
        if( m_UdpSoktPt != null )
        {
            m_UdpSoktPt.Destroy( null ); //关闭并destroy已创建的this 端UDPprotocol套接digit。
            m_UdpSoktPt = null;

            String p_InfoStrPt = "关闭并destroy已创建的this 端UDPprotocol套接digitsuccess.";
            Log.i( m_CurClsNameStrPt, p_InfoStrPt );
            Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
        }

        //destroy接收 Audio  Output  frame 的Linked list类对象。
        if( m_RecvAudioOutputFrameLnkLstPt != null )
        {
            m_RecvAudioOutputFrameLnkLstPt.clear();
            m_RecvAudioOutputFrameLnkLstPt = null;

            Log.i( m_CurClsNameStrPt, "destroy Receive output  frame Linked list类对象success." );
        }

        //destroyvideoAdaptive jitter buffer类对象。
        if( m_VAjbPt != null )
        {
            m_VAjbPt.Destroy();
            m_VAjbPt = null;

            Log.i( m_CurClsNameStrPt, "destroyvideoAdaptive jitter buffer类对象success." );
        }

        //destroy Audio Adaptive jitter buffer类对象。
        if( m_AAjbPt != null )
        {
            m_AAjbPt.Destroy();
            m_AAjbPt = null;

            Log.i( m_CurClsNameStrPt, "destroy Audio Adaptive jitter buffer类对象success." );
        }

        if( m_IsCreateSrvrOrClnt == 1 ) //如果是 Create server 。
        {
            if( ( m_ExitFlag == 1 ) && ( m_IsRecvExitPkt == 1 ) ) //如果this 线程接收到退出请求，且接收到了退出包。
            {
                String p_InfoStrPt = "Because it is Create server ，且this 线程接收到退出请求，且接收到了退出包，表示是远端TCPprotocol客户端套接digit主动退出，this 线程重新初始化来继续保持监听。";
                Log.i( m_CurClsNameStrPt, p_InfoStrPt );
                {Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );}

                RequireExit( 2, 0 ); //请求重启。
                {Message clMessage = new Message();clMessage.what = 4;m_MainActivityHandlerPt.sendMessage( clMessage );} //向主界面发送重建SurfaceView控件消息。
            }
            else if( ( m_ExitFlag == 0 ) && ( m_ExitCode == -2 ) ) //如果this The thread did not receive the exit request，且退出代码 for 处理失败。
            {
                String p_InfoStrPt = "Because it is Create server ，且this The thread did not receive the exit request，且退出码 for 处理失败，表示是处理失败或连接异常断开，this 线程重新初始化来继续保持监听。";
                Log.i( m_CurClsNameStrPt, p_InfoStrPt );
                {Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );}

                RequireExit( 2, 0 ); //请求重启。
                {Message clMessage = new Message();clMessage.what = 4;m_MainActivityHandlerPt.sendMessage( clMessage );} //向主界面发送重建SurfaceView控件消息。
            }
            else //其他情况，this 线程直接退出。
            {
                {Message clMessage = new Message();clMessage.what = 2;m_MainActivityHandlerPt.sendMessage( clMessage );} //向主界面发送媒体处理线程退出的消息。
                {Message clMessage = new Message();clMessage.what = 4;m_MainActivityHandlerPt.sendMessage( clMessage );} //向主界面发送重建SurfaceView控件消息。
            }
        }
        else if( m_IsCreateSrvrOrClnt == 0 ) //如果是创建客户端。
        {
            if( ( m_ExitFlag == 0 ) && ( m_ExitCode == -2 ) ) //如果this The thread did not receive the exit request，且退出代码 for 处理失败。
            {
                String p_InfoStrPt = "Since it is creating a client，且this The thread did not receive the exit request，且退出码 for 处理失败，表示是处理失败或连接异常断开，this 线程重新初始化来重连服务端。";
                Log.e( m_CurClsNameStrPt, p_InfoStrPt );
                {Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );}

                RequireExit( 2, 0 ); //请求重启。
            }
            else //其他情况，this 线程直接退出。
            {
                {Message clMessage = new Message();clMessage.what = 2;m_MainActivityHandlerPt.sendMessage( clMessage );} //向主界面发送媒体处理线程退出的消息。
                {Message clMessage = new Message();clMessage.what = 4;m_MainActivityHandlerPt.sendMessage( clMessage );} //向主界面发送重建SurfaceView控件消息。
            }
        }
    }

    // user 定义的读取soundvideo enter  frame 函 number ， в 读取到一个 Audio  enter  frame 或video enter  frame 并处理完后回调一次， for 0表示成功， for 非0表示失败。
    @Override public int UserReadAudioVideoInputFrame( short PcmAudioInputFramePt[], short PcmAudioResultFramePt[], HTInt VoiceActStsPt, byte EncoderAudioInputFramePt[], HTLong EncoderAudioInputFrameLenPt, HTInt EncoderAudioInputFrameIsNeedTransPt, byte YU12VideoInputFramePt[], HTInt YU12VideoInputFrameWidthPt, HTInt YU12VideoInputFrameHeigthPt, byte EncoderVideoInputFramePt[], HTLong EncoderVideoInputFrameLenPt )
    {
        int p_Result = -1; //存放this 函 number 执行 result 的值， for 0表示成功， for 非0表示失败。
        int p_FramePktLen = 0; //存放 enter  Output  frame  number 据包的 number According to the length ， unit digit节。
        int p_TmpInt32 = 0;

        out:
        {
            //发送 Audio  enter  frame 。
            if( PcmAudioInputFramePt != null ) //如果要 use  Audio  enter 。
            {
                if( EncoderAudioInputFramePt != null ) //如果要 use 已编码格式 Audio  enter  frame 。
                {
                    if( VoiceActStsPt.m_Val != 0 && EncoderAudioInputFrameIsNeedTransPt.m_Val != 0 ) //如果this 次 Audio  enter  frame  for  Have  voice sound live动，且需要传输。
                    {
                        System.arraycopy( EncoderAudioInputFramePt, 0, m_TmpBytePt, 1 + 4 + 4, ( int ) EncoderAudioInputFrameLenPt.m_Val ); //Настраивать Audio  enter  Output  frame 。
                        p_FramePktLen = 1 + 4 + 4 + ( int )EncoderAudioInputFrameLenPt.m_Val; // number 据包长度 =  number 据包 Types of  +  Audio  enter  frame  Time 间戳 + video enter  frame  Time 间戳 + 已编码格式 Audio  enter  frame 。
                    }
                    else //如果this 次 Audio  enter  frame  for 无  voice sound live动，或不需要传输。
                    {
                        p_FramePktLen = 1 + 4; // number 据包长度 =  number 据包 Types of  +  Audio  enter  frame  Time 间戳。
                    }
                }
                else //如果要 use PCM格式 Audio  enter  frame 。
                {
                    if( VoiceActStsPt.m_Val != 0 ) //如果this 次 Audio  enter  frame  for  Have  voice sound live动。
                    {
                        for( p_TmpInt32 = 0; p_TmpInt32 < PcmAudioResultFramePt.length; p_TmpInt32++ ) //Настраивать Audio  enter  Output  frame 。
                        {
                            m_TmpBytePt[1 + 4 + 4 + p_TmpInt32 * 2] = ( byte ) ( PcmAudioResultFramePt[p_TmpInt32] & 0xFF );
                            m_TmpBytePt[1 + 4 + 4 + p_TmpInt32 * 2 + 1] = ( byte ) ( ( PcmAudioResultFramePt[p_TmpInt32] & 0xFF00 ) >> 8 );
                        }
                        p_FramePktLen = 1 + 4 + 4 + PcmAudioResultFramePt.length * 2; // number 据包长度 =  number 据包 Types of  +  Audio  enter  frame  Time 间戳 + video enter  frame  Time 间戳 + PCM格式 Audio  enter  frame 。
                    }
                    else //如果this 次 Audio  enter  frame  for 无  voice sound live动，或不需要传输。
                    {
                        p_FramePktLen = 1 + 4; // number 据包长度 =  number 据包 Types of  +  Audio  enter  frame  Time 间戳。
                    }
                }

                //发送 Audio  enter  frame  number 据包。
                if( p_FramePktLen != 1 + 4 ) //如果this  Audio  enter  frame  for  Have  voice sound live动，就发送。
                {
                    m_LastSendAudioInputFrameTimeStamp += 1; // Audio  enter  frame 的 Time 间戳递增一个步进。

                    //Настраивать number 据包 Types of  for  Audio  enter  frame 包。
                    m_TmpBytePt[0] = PKT_TYP_AFRAME;
                    //Настраивать Audio  enter  frame  Time 间戳。
                    m_TmpBytePt[1] = ( byte ) ( m_LastSendAudioInputFrameTimeStamp & 0xFF );
                    m_TmpBytePt[2] = ( byte ) ( ( m_LastSendAudioInputFrameTimeStamp & 0xFF00 ) >> 8 );
                    m_TmpBytePt[3] = ( byte ) ( ( m_LastSendAudioInputFrameTimeStamp & 0xFF0000 ) >> 16 );
                    m_TmpBytePt[4] = ( byte ) ( ( m_LastSendAudioInputFrameTimeStamp & 0xFF000000 ) >> 24 );
                    //Настраиватьvideo enter  frame  Time 间戳。
                    m_TmpBytePt[5] = ( byte ) ( m_LastSendVideoInputFrameTimeStamp & 0xFF );
                    m_TmpBytePt[6] = ( byte ) ( ( m_LastSendVideoInputFrameTimeStamp & 0xFF00 ) >> 8 );
                    m_TmpBytePt[7] = ( byte ) ( ( m_LastSendVideoInputFrameTimeStamp & 0xFF0000 ) >> 16 );
                    m_TmpBytePt[8] = ( byte ) ( ( m_LastSendVideoInputFrameTimeStamp & 0xFF000000 ) >> 24 );

                    if( ( ( m_UseWhatXfrPrtcl == 0 ) && ( m_TcpClntSoktPt.SendPkt( m_TmpBytePt, p_FramePktLen, ( short ) 0, m_ErrInfoVarStrPt ) == 0 ) ) ||
                        ( ( m_UseWhatXfrPrtcl == 1 ) && ( m_UdpSoktPt.SendPkt( 4, null, null, m_TmpBytePt, p_FramePktLen, ( short ) 0, m_ErrInfoVarStrPt ) == 0 ) ) )
                    {
                        m_LastPktSendTime = System.currentTimeMillis(); //Настраивать最后一个 number 据包的发送 Time 间。
                        Log.i( m_CurClsNameStrPt, "Send one  Have  voice sound live动的 Audio  enter  frame 包success. Audio  enter  frame  Time 间戳：" + m_LastSendAudioInputFrameTimeStamp + "，video enter  frame  Time 间戳：" + m_LastSendVideoInputFrameTimeStamp + "，总长度：" + p_FramePktLen + "。" );
                    }
                    else
                    {
                        String p_InfoStrPt = "Send one  Have  voice sound live动的 Audio  enter  frame The package failed. the reason：" + m_ErrInfoVarStrPt.GetStr();
                        Log.e( m_CurClsNameStrPt, p_InfoStrPt );
                        Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                        break out;
                    }

                    m_LastSendAudioInputFrameIsAct = 1; //Настраивать最后一个发送的 Audio  enter  frame  Have  voice sound live动。
                    m_LastSendAudioInputFrameIsRecv = 1; //Настраивать最后一个发送的 Audio  enter  frame 远端已经接收到。
                }
                else if( ( p_FramePktLen == 1 + 4 ) && ( m_LastSendAudioInputFrameIsAct != 0 ) ) //如果this  Audio  enter  frame  for 无  voice sound live动，但最后一个发送的 Audio  enter  frame  for  Have  voice sound live动，就发送。
                {
                    m_LastSendAudioInputFrameTimeStamp += 1; // Audio  enter  frame 的 Time 间戳递增一个步进。

                    //Настраивать number 据包 Types of  for  Audio  enter  frame 包。
                    m_TmpBytePt[0] = PKT_TYP_AFRAME;
                    //Настраивать Audio  enter  frame  Time 间戳。
                    m_TmpBytePt[1] = ( byte ) ( m_LastSendAudioInputFrameTimeStamp & 0xFF );
                    m_TmpBytePt[2] = ( byte ) ( ( m_LastSendAudioInputFrameTimeStamp & 0xFF00 ) >> 8 );
                    m_TmpBytePt[3] = ( byte ) ( ( m_LastSendAudioInputFrameTimeStamp & 0xFF0000 ) >> 16 );
                    m_TmpBytePt[4] = ( byte ) ( ( m_LastSendAudioInputFrameTimeStamp & 0xFF000000 ) >> 24 );

                    if( ( ( m_UseWhatXfrPrtcl == 0 ) && ( m_TcpClntSoktPt.SendPkt( m_TmpBytePt, p_FramePktLen, ( short ) 0, m_ErrInfoVarStrPt ) == 0 ) ) ||
                        ( ( m_UseWhatXfrPrtcl == 1 ) && ( m_UdpSoktPt.SendPkt( 4, null, null, m_TmpBytePt, p_FramePktLen, ( short ) 0, m_ErrInfoVarStrPt ) == 0 ) ) )
                    {
                        m_LastPktSendTime = System.currentTimeMillis(); //Настраивать最后一个 number 据包的发送 Time 间。
                        Log.i( m_CurClsNameStrPt, "Send one 无  voice sound live动的 Audio  enter  frame 包success. Audio  enter  frame  Time 间戳：" + m_LastSendAudioInputFrameTimeStamp + "，总长度：" + p_FramePktLen + "。" );
                    }
                    else
                    {
                        String p_InfoStrPt = "Send one 无  voice sound live动的 Audio  enter  frame The package failed. the reason：" + m_ErrInfoVarStrPt.GetStr();
                        Log.e( m_CurClsNameStrPt, p_InfoStrPt );
                        Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                        break out;
                    }

                    m_LastSendAudioInputFrameIsAct = 0; //Настраивать最后一个发送的 Audio  enter  frame 无  voice sound live动。
                    m_LastSendAudioInputFrameIsRecv = 0; //Настраивать最后一个发送的 Audio  enter  frame 远端没 Have接收到。
                }
                else //如果this  Audio  enter  frame  for 无  voice sound live动，且最后一个发送的 Audio  enter  frame  for 无  voice sound live动，无需发送。
                {
                    Log.i( m_CurClsNameStrPt, "this  Audio  enter  frame  for 无  voice sound live动，且最后一个发送的 Audio  enter  frame  for 无  voice sound live动，无需发送。" );

                    if( ( m_UseWhatXfrPrtcl == 1 ) && ( m_LastSendAudioInputFrameIsRecv == 0 ) ) //如果是 use UDPprotocol，且this  Audio  enter  frame  for 无  voice sound live动，且最后一个发送的 Audio  enter  frame  for 无  voice sound live动，且最后一个发送的 Audio  enter  frame 远端没 Have接收到。
                    {
                        //Настраивать Audio  enter  frame 包。
                        m_TmpBytePt[0] = PKT_TYP_AFRAME;
                        //Настраивать Audio  enter  frame  Time 间戳。
                        m_TmpBytePt[1] = ( byte ) ( m_LastSendAudioInputFrameTimeStamp & 0xFF );
                        m_TmpBytePt[2] = ( byte ) ( ( m_LastSendAudioInputFrameTimeStamp & 0xFF00 ) >> 8 );
                        m_TmpBytePt[3] = ( byte ) ( ( m_LastSendAudioInputFrameTimeStamp & 0xFF0000 ) >> 16 );
                        m_TmpBytePt[4] = ( byte ) ( ( m_LastSendAudioInputFrameTimeStamp & 0xFF000000 ) >> 24 );

                        if( m_UdpSoktPt.SendPkt( 4, null, null, m_TmpBytePt, p_FramePktLen, ( short ) 0, m_ErrInfoVarStrPt ) == 0 )
                        {
                            m_LastPktSendTime = System.currentTimeMillis(); //Настраивать最后一个 number 据包的发送 Time 间。
                            Log.i( m_CurClsNameStrPt, "Resend last一个无  voice sound live动的 Audio  enter  frame 包success. Audio  enter  frame  Time 间戳：" + m_LastSendAudioInputFrameTimeStamp + "，总长度：" + p_FramePktLen + "。" );
                        }
                        else
                        {
                            String p_InfoStrPt = "Resend last一个无  voice sound live动的 Audio  enter  frame The package failed. the reason：" + m_ErrInfoVarStrPt.GetStr();
                            Log.e( m_CurClsNameStrPt, p_InfoStrPt );
                            Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                            break out;
                        }
                    }
                }
            }

            //发送video enter  frame 。
            if( YU12VideoInputFramePt != null ) //如果要 use video enter 。
            {
                if( EncoderVideoInputFramePt != null ) //如果要 use 已编码格式video enter  frame 。
                {
                    if( EncoderVideoInputFrameLenPt.m_Val != 0 ) //如果this 次video enter  frame  for  Have图像 live动。
                    {
                        System.arraycopy( EncoderVideoInputFramePt, 0, m_TmpBytePt, 1 + 4 + 4, ( int ) EncoderVideoInputFrameLenPt.m_Val ); //Настраиватьvideo enter  Output  frame 。
                        p_FramePktLen = 1 + 4 + 4 + ( int ) EncoderVideoInputFrameLenPt.m_Val; // number 据包长度 =  number 据包 Types of  + video enter  frame  Time 间戳 +  Audio  enter  frame  Time 间戳 + 已编码格式video enter  frame 。
                    }
                    else
                    {
                        p_FramePktLen = 1 + 4; // number 据包长度 =  number 据包 Types of  + video enter  frame  Time 间戳。
                    }
                }
                else //如果要 use YU12格式video enter  frame 。
                {
                    System.arraycopy( YU12VideoInputFramePt, 0, m_TmpBytePt, 1 + 4 + 4, YU12VideoInputFramePt.length ); //Настраиватьvideo enter  Output  frame 。
                    p_FramePktLen = 1 + 4 + 4 + YU12VideoInputFramePt.length; // number 据包长度 =  number 据包 Types of  + video enter  frame  Time 间戳 +  Audio  enter  frame  Time 间戳 + YU12格式video enter  frame 。
                }

                //发送video enter  frame  number 据包。
                if( p_FramePktLen != 1 + 4 ) //如果this video enter  frame  for  Have图像 live动，就发送。
                {
                    m_LastSendVideoInputFrameTimeStamp += 1; //video enter  frame 的 Time 间戳递增一个步进。

                    //Настраивать number 据包 Types of  for video enter  frame 包。
                    m_TmpBytePt[0] = PKT_TYP_VFRAME;
                    //Настраиватьvideo enter  frame  Time 间戳。
                    m_TmpBytePt[1] = ( byte ) ( m_LastSendVideoInputFrameTimeStamp & 0xFF );
                    m_TmpBytePt[2] = ( byte ) ( ( m_LastSendVideoInputFrameTimeStamp & 0xFF00 ) >> 8 );
                    m_TmpBytePt[3] = ( byte ) ( ( m_LastSendVideoInputFrameTimeStamp & 0xFF0000 ) >> 16 );
                    m_TmpBytePt[4] = ( byte ) ( ( m_LastSendVideoInputFrameTimeStamp & 0xFF000000 ) >> 24 );
                    //Настраивать Audio  enter  frame  Time 间戳。
                    m_TmpBytePt[5] = ( byte ) ( m_LastSendAudioInputFrameTimeStamp & 0xFF );
                    m_TmpBytePt[6] = ( byte ) ( ( m_LastSendAudioInputFrameTimeStamp & 0xFF00 ) >> 8 );
                    m_TmpBytePt[7] = ( byte ) ( ( m_LastSendAudioInputFrameTimeStamp & 0xFF0000 ) >> 16 );
                    m_TmpBytePt[8] = ( byte ) ( ( m_LastSendAudioInputFrameTimeStamp & 0xFF000000 ) >> 24 );

                    if( ( ( m_UseWhatXfrPrtcl == 0 ) && ( m_TcpClntSoktPt.SendPkt( m_TmpBytePt, p_FramePktLen, ( short ) 0, m_ErrInfoVarStrPt ) == 0 ) ) ||
                        ( ( m_UseWhatXfrPrtcl == 1 ) && ( m_UdpSoktPt.SendPkt( 4, null, null, m_TmpBytePt, p_FramePktLen, ( short ) 0, m_ErrInfoVarStrPt ) == 0 ) ) )
                    {
                        m_LastPktSendTime = System.currentTimeMillis(); //Настраивать最后一个 number 据包的发送 Time 间。
                        Log.i( m_CurClsNameStrPt, "Send one  Have图像 live动的video enter  frame 包success.video enter  frame  Time 间戳：" + m_LastSendVideoInputFrameTimeStamp + "， Audio  enter  frame  Time 间戳：" + m_LastSendAudioInputFrameTimeStamp + "，总长度：" + p_FramePktLen + "， Types of ：" + ( m_TmpBytePt[13] & 0xff ) + "。" );
                    }
                    else
                    {
                        String p_InfoStrPt = "Send one  Have图像 live动的video enter  frame The package failed. the reason：" + m_ErrInfoVarStrPt.GetStr();
                        Log.e( m_CurClsNameStrPt, p_InfoStrPt );
                        Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
                        break out;
                    }
                }
                else //如果this video enter  frame  for 无图像 live动，无需发送。
                {
                    Log.i( m_CurClsNameStrPt, "this video enter  frame  for 无图像 live动，无需发送。" );
                }
            }

            p_Result = 0; //Настраиватьthis 函 number 执行success.
        }

        return p_Result;
    }

    // user 定义的写入 Audio  Output  frame 函 number ， в 需要写入一个 Audio  Output  frame  Time 回调一次。注意：this 函 number 不是 в 媒体处理线程in执行的，而是 в  Audio  Output 线程in执行的，所以this 函 number 应尽量 в 一瞬间完成执行，否则会导致 Audio  enter  Output  frame 不同步，从而导致 Acoustic echo sound消除失败。
    @Override public void UserWriteAudioOutputFrame( short PcmAudioOutputFramePt[], byte EncoderAudioOutputFramePt[], HTLong EncoderAudioOutputFrameLen )
    {
        int p_AudioOutputFrameTimeStamp = 0;
        byte p_AudioOutputFramePt[] = null;
        long p_AudioOutputFrameLen = 0;
        int p_TmpInt32;

        out:
        {
            //取出并写入 Audio  Output  frame 。
            {
                //从Linked list或Adaptive jitter buffer取出一个 Audio  Output  frame 。
                switch( m_UseWhatRecvOutputFrame ) // use 什么 Receive output  frame 。
                {
                    case 0: //如果 use Linked list。
                    {
                        if( m_RecvAudioOutputFrameLnkLstPt.size() != 0 ) //如果接收 Audio  Output  frame Linked list不 for 空。
                        {
                            synchronized( m_RecvAudioOutputFrameLnkLstPt )
                            {
                                p_AudioOutputFramePt = m_RecvAudioOutputFrameLnkLstPt.getFirst(); //获取接收 Audio  Output  frame Linked list的第一个 Audio  Output  frame 。
                                m_RecvAudioOutputFrameLnkLstPt.removeFirst(); // delete 接收 Audio  Output  frame Linked list的第一个 Audio  Output  frame 。
                            }
                            p_AudioOutputFrameLen = p_AudioOutputFramePt.length;
                        }

                        if( p_AudioOutputFrameLen != 0 ) //如果 Audio  Output  frame  for  Have  voice sound live动。
                        {
                            Log.i( m_CurClsNameStrPt, "Receive from  Audio  Output  frame Linked list取出一个 Have  voice sound live动的 Audio  Output  frame 。 number According to the length ：" + p_AudioOutputFrameLen + "。" );
                        }
                        else //如果 Audio  Output  frame  for 无  voice sound live动。
                        {
                            Log.i( m_CurClsNameStrPt, "Receive from  Audio  Output  frame Linked list取出一个无  voice sound live动的 Audio  Output  frame 。 number According to the length ：" + p_AudioOutputFrameLen + "。" );
                        }

                        break;
                    }
                    case 1: //如果 use Adaptive jitter buffer。
                    {
                        synchronized( m_AAjbPt )
                        {
                            //从 Audio Adaptive jitter buffer取出一个 Audio  Output  frame 。
                            m_AAjbPt.GetOneByteFrame( m_TmpHTInt2Pt, m_TmpByte2Pt, 0, m_TmpByte2Pt.length, m_TmpHTLong2Pt );
                            p_AudioOutputFrameTimeStamp = m_TmpHTInt2Pt.m_Val;
                            p_AudioOutputFramePt = m_TmpByte2Pt;
                            p_AudioOutputFrameLen = m_TmpHTLong2Pt.m_Val;

                            if( p_AudioOutputFrameLen > 0 ) //如果 Audio  Output  frame  for  Have  voice sound live动。
                            {
                                m_LastGetAudioOutputFrameVideoOutputFrameTimeStamp = ( p_AudioOutputFramePt[0] & 0xFF ) + ( ( p_AudioOutputFramePt[1] & 0xFF ) << 8 ) + ( ( p_AudioOutputFramePt[2] & 0xFF ) << 16 ) + ( ( p_AudioOutputFramePt[3] & 0xFF ) << 24 ); //Настраивать最后一个取出的 Audio  Output  frame 对应video Output  frame 的 Time 间戳。
                                m_LastGetAudioOutputFrameIsAct = 1; //Настраивать最后一个取出的 Audio  Output  frame  for  Have  voice sound live动。
                                Log.i( m_CurClsNameStrPt, "从 Audio Adaptive jitter buffer取出一个 Have  voice sound live动的 Audio  Output  frame 。 Audio  Output  frame  Time 间戳：" + p_AudioOutputFrameTimeStamp + "，video Output  frame  Time 间戳：" + m_LastGetAudioOutputFrameVideoOutputFrameTimeStamp + "， number According to the length ：" + p_AudioOutputFrameLen + "。" );
                            }
                            else if( p_AudioOutputFrameLen == 0 ) //如果 Audio  Output  frame  for 无  voice sound live动。
                            {
                                m_LastGetAudioOutputFrameIsAct = 0; //Настраивать最后一个取出的 Audio  Output  frame  for 无  voice sound live动。
                                Log.i( m_CurClsNameStrPt, "从 Audio Adaptive jitter buffer取出一个无  voice sound live动的 Audio  Output  frame 。 Audio  Output  frame  Time 间戳：" + p_AudioOutputFrameTimeStamp + "， number According to the length ：" + p_AudioOutputFrameLen + "。" );
                            }
                            else //如果 Audio  Output  frame  for 丢失。
                            {
                                m_LastGetAudioOutputFrameIsAct = 1; //Настраивать最后一个取出的 Audio  Output  frame  for  Have  voice sound live动。
                                Log.i( m_CurClsNameStrPt, "从 Audio Adaptive jitter buffer取出一个丢失的 Audio  Output  frame 。 Audio  Output  frame  Time 间戳：" + p_AudioOutputFrameTimeStamp + "，video Output  frame  Time 间戳：" + m_LastGetAudioOutputFrameVideoOutputFrameTimeStamp + "， number According to the length ：" + p_AudioOutputFrameLen + "。" );
                            }

                            HTInt p_CurHaveBufActFrameCntPt = new HTInt(); //存放当前已缓冲 Have live动 frame 的 number 量。
                            HTInt p_CurHaveBufInactFrameCntPt = new HTInt(); //存放当前已缓冲无 live动 frame 的 number 量。
                            HTInt p_CurHaveBufFrameCntPt = new HTInt(); //存放当前已缓冲 frame 的 number 量。
                            HTInt p_MinNeedBufFrameCntPt = new HTInt(); //存放Minimum number of frames to be buffered。
                            HTInt p_MaxNeedBufFrameCntPt = new HTInt(); //Maximum buffer required for storage  frame 的 number 量。
                            HTInt p_CurNeedBufFrameCntPt = new HTInt(); //存放当前需缓冲 frame 的 number 量。
                            m_AAjbPt.GetBufFrameCnt( p_CurHaveBufActFrameCntPt, p_CurHaveBufInactFrameCntPt, p_CurHaveBufFrameCntPt, p_MinNeedBufFrameCntPt, p_MaxNeedBufFrameCntPt, p_CurNeedBufFrameCntPt );
                            Log.i( m_CurClsNameStrPt, " Audio Adaptive jitter buffer： Have live动 frame ：" + p_CurHaveBufActFrameCntPt.m_Val + "，无 live动 frame ：" + p_CurHaveBufInactFrameCntPt.m_Val + "， frame ：" + p_CurHaveBufFrameCntPt.m_Val + "，最小需 frame ：" + p_MinNeedBufFrameCntPt.m_Val + "，最大需 frame ：" + p_MaxNeedBufFrameCntPt.m_Val + "，当前需 frame ：" + p_CurNeedBufFrameCntPt.m_Val + "。" );
                        }

                        break;
                    }
                }

                //写入 Audio  Output  frame 。
                if( p_AudioOutputFrameLen > 0 ) //如果 Audio  Output  frame  for  Have  voice sound live动。
                {
                    if( PcmAudioOutputFramePt != null ) //如果要 use PCM格式 Audio  Output  frame 。
                    {
                        if( p_AudioOutputFrameLen - 4 != PcmAudioOutputFramePt.length * 2 )
                        {
                            Arrays.fill( PcmAudioOutputFramePt, ( short ) 0 );
                            Log.e( m_CurClsNameStrPt, " Audio  Output  frame 的 number According to the length 不等于PCM格式的 number According to the length 。 Audio  Output  frame ：" + ( p_AudioOutputFrameLen - 4 ) + "，PCM格式：" + ( PcmAudioOutputFramePt.length * 2 ) + "。" );
                            break out;
                        }

                        //写入PCM格式 Audio  Output  frame 。
                        for( p_TmpInt32 = 0; p_TmpInt32 < PcmAudioOutputFramePt.length; p_TmpInt32++ )
                        {
                            PcmAudioOutputFramePt[p_TmpInt32] = ( short ) ( ( p_AudioOutputFramePt[4 + p_TmpInt32 * 2] & 0xFF ) | ( p_AudioOutputFramePt[4 + p_TmpInt32 * 2 + 1] << 8 ) );
                        }
                    }
                    else //如果要 use 已编码格式 Audio  Output  frame 。
                    {
                        if( p_AudioOutputFrameLen - 4 > EncoderAudioOutputFramePt.length )
                        {
                            EncoderAudioOutputFrameLen.m_Val = 0;
                            Log.e( m_CurClsNameStrPt, " Audio  Output  frame 的 number According to the length 已ultra过已编码格式的 number According to the length 。 Audio  Output  frame ：" + ( p_AudioOutputFrameLen - 4 ) + "，已编码格式：" + EncoderAudioOutputFramePt.length + "。" );
                            break out;
                        }

                        //写入已编码格式 Audio  Output  frame 。
                        System.arraycopy( p_AudioOutputFramePt, 4, EncoderAudioOutputFramePt, 0, ( int ) ( p_AudioOutputFrameLen - 4 ) );
                        EncoderAudioOutputFrameLen.m_Val = p_AudioOutputFrameLen - 4;
                    }
                }
                else if( p_AudioOutputFrameLen == 0 ) //如果 Audio  Output  frame  for 无  voice sound live动。
                {
                    if( PcmAudioOutputFramePt != null ) //如果要 use PCM格式 Audio  Output  frame 。
                    {
                        Arrays.fill( PcmAudioOutputFramePt, ( short ) 0 );
                    }
                    else //如果要 use 已编码格式 Audio  Output  frame 。
                    {
                        EncoderAudioOutputFrameLen.m_Val = 0;
                    }
                }
                else //如果 Audio  Output  frame  for 丢失。
                {
                    if( PcmAudioOutputFramePt != null ) //如果要 use PCM格式 Audio  Output  frame 。
                    {
                        Arrays.fill( PcmAudioOutputFramePt, ( short ) 0 );
                    }
                    else //如果要 use 已编码格式 Audio  Output  frame 。
                    {
                        EncoderAudioOutputFrameLen.m_Val = p_AudioOutputFrameLen;
                    }
                }
            }

            //取出并写入 Audio  Output  frame 。
            GetAndWriteVideoOutputFrame( m_TmpByte2Pt, m_TmpHTInt2Pt, m_TmpHTLong2Pt );
        }
    }

    // user 定义的获取PCM格式 Audio  Output  frame 函 number ， в 解码完一个已编码 Audio  Output  frame  Time 回调一次。注意：this 函 number 不是 в 媒体处理线程in执行的，而是 в  Audio  Output 线程in执行的，所以this 函 number 应尽量 в 一瞬间完成执行，否则会导致 Audio  enter  Output  frame 不同步，从而导致 Acoustic echo sound消除失败。
    @Override public void UserGetPcmAudioOutputFrame( short PcmOutputFramePt[] )
    {

    }

    //取出并写入video Output  frame 。
    void GetAndWriteVideoOutputFrame( byte TmpBytePt[], HTInt TmpHTIntPt, HTLong TmpHTLongPt )
    {
        int p_VideoOutputFrameTimeStamp = 0;
        int p_VideoOutputFrameAudioOutputFrameTimeStamp = 0;
        byte p_VideoOutputFramePt[] = null;
        long p_VideoOutputFrameLen = 0;

        //从Linked list或Adaptive jitter buffer取出一个video Output  frame 。
        switch( m_UseWhatRecvOutputFrame ) // use 什么 Receive output  frame 。
        {
            case 0: //如果 use Linked list。
            {
                if( m_RecvVideoOutputFrameLnkLstPt.size() != 0 ) //如果接收video Output  frame Linked list不 for 空。
                {
                    synchronized( m_RecvVideoOutputFrameLnkLstPt )
                    {
                        p_VideoOutputFramePt = m_RecvVideoOutputFrameLnkLstPt.getFirst(); //获取接收video Output  frame Linked list的第一个video Output  frame 。
                        m_RecvVideoOutputFrameLnkLstPt.removeFirst(); // delete 接收video Output  frame Linked list的第一个video Output  frame 。
                    }
                    p_VideoOutputFrameLen = p_VideoOutputFramePt.length;
                }

                if( p_VideoOutputFrameLen != 0 ) //如果video Output  frame  for  Have图像 live动。
                {
                    Log.i( m_CurClsNameStrPt, "Receive from video Output  frame Linked list取出一个 Have图像 live动的video Output  frame 。 number According to the length ：" + p_VideoOutputFrameLen + "。" );
                }
                else //如果video Output  frame  for 无图像 live动。
                {
                    Log.i( m_CurClsNameStrPt, "Receive from video Output  frame Linked list取出一个无图像 live动的video Output  frame 。 number According to the length ：" + p_VideoOutputFrameLen + "。" );
                }

                break;
            }
            case 1: //如果 use Adaptive jitter buffer。
            {
                synchronized( m_VAjbPt )
                {
                    //从videoAdaptive jitter buffer取出一个video Output  frame 。
                    if( m_AudioOutputPt.m_IsUseAudioOutput != 0 && m_LastGetAudioOutputFrameIsAct != 0 ) //如果要 use  Audio  Output ，且最后一个取出的 Audio  Output  frame  for  Have  voice sound live动，就根据最后一个取出的 Audio  Output  frame 对应video Output  frame 的 Time 间戳来取出。
                    {
                        m_VAjbPt.GetOneByteFrameWantTimeStamp( System.currentTimeMillis(), m_LastGetAudioOutputFrameVideoOutputFrameTimeStamp, TmpHTIntPt, TmpBytePt, 0, TmpBytePt.length, TmpHTLongPt );
                    }
                    else //如果最后一个取出的 Audio  Output  frame  for 无  voice sound live动，就根据直接取出。
                    {
                        m_VAjbPt.GetOneByteFrame( System.currentTimeMillis(), TmpHTIntPt, TmpBytePt, 0, TmpBytePt.length, TmpHTLongPt );
                    }
                    p_VideoOutputFrameTimeStamp = TmpHTIntPt.m_Val;
                    p_VideoOutputFramePt = TmpBytePt;
                    p_VideoOutputFrameLen = TmpHTLongPt.m_Val;

                    if( p_VideoOutputFrameLen > 0 ) //如果video Output  frame  for  Have图像 live动。
                    {
                        Log.i( m_CurClsNameStrPt, "从videoAdaptive jitter buffer取出一个 Have图像 live动的video Output  frame 。 Time 间戳：" + p_VideoOutputFrameTimeStamp + "， number According to the length ：" + p_VideoOutputFrameLen + "。" );
                    }
                    else //如果video Output  frame  for 无图像 live动。
                    {
                        Log.i( m_CurClsNameStrPt, "从videoAdaptive jitter buffer取出一个无图像 live动的video Output  frame 。 Time 间戳：" + p_VideoOutputFrameTimeStamp + "， number According to the length ：" + p_VideoOutputFrameLen + "。" );
                    }

                    HTInt p_CurHaveBufFrameCntPt = new HTInt(); //存放当前已缓冲 frame 的 number 量。
                    HTInt p_MinNeedBufFrameCntPt = new HTInt(); //存放Minimum number of frames to be buffered。
                    HTInt p_MaxNeedBufFrameCntPt = new HTInt(); //Maximum buffer required for storage  frame 的 number 量。
                    HTInt p_CurNeedBufFrameCntPt = new HTInt(); //存放当前需缓冲 frame 的 number 量。
                    m_VAjbPt.GetBufFrameCnt( p_CurHaveBufFrameCntPt, p_MinNeedBufFrameCntPt, p_MaxNeedBufFrameCntPt, p_CurNeedBufFrameCntPt );
                    Log.i( m_CurClsNameStrPt, "videoAdaptive jitter buffer： frame ：" + p_CurHaveBufFrameCntPt.m_Val + "，最小需 frame ：" + p_MinNeedBufFrameCntPt.m_Val + "，最大需 frame ：" + p_MaxNeedBufFrameCntPt.m_Val + "，当前需 frame ：" + p_CurNeedBufFrameCntPt.m_Val + "。" );
                }

                break;
            }
        }

        //写入video Output  frame 。
        if( p_VideoOutputFrameLen > 0 ) //如果video Output  frame  for  Have图像 live动。
        {
            //读取video Output  frame 对应 Audio  Output  frame 的 Time 间戳。
            p_VideoOutputFrameAudioOutputFrameTimeStamp = ( p_VideoOutputFramePt[0] & 0xFF ) + ( ( p_VideoOutputFramePt[1] & 0xFF ) << 8 ) + ( ( p_VideoOutputFramePt[2] & 0xFF ) << 16 ) + ( ( p_VideoOutputFramePt[3] & 0xFF ) << 24 );

            UserWriteVideoOutputFrame( p_VideoOutputFramePt, 4, p_VideoOutputFrameLen - 4 );
        }
        else if( p_VideoOutputFrameLen == 0 ) //如果video Output  frame  for 无图像 live动。
        {

        }
    }
}

public class MainActivity extends AppCompatActivity
{
    String m_CurClsNameStrPt = this.getClass().getSimpleName(); //存放当前类名称digit符串。

    View m_LyotActivityMainViewPt; //存放主界面布局控件的内存指针。
    View m_LyotActivitySettingViewPt; //存放Настраивать界面布局控件的内存指针。
    View m_LyotActivitySpeexAecViewPt; //存放Speex Acoustic echo sound Eliminator Настраивать布局控件的内存指针。
    View m_LyotActivityWebRtcAecmViewPt; //存放WebRtc Fixed-point version  Acoustic echo sound Eliminator Настраивать布局控件的内存指针。
    View m_LyotActivityWebRtcAecViewPt; //存放WebRtc Floating point version  Acoustic echo sound Eliminator Настраивать布局控件的内存指针。
    View m_LyotActivitySpeexWebRtcAecViewPt; //存放SpeexWebRtc triple  Acoustic echo sound Eliminator Настраивать布局控件的内存指针。
    View m_LyotActivitySpeexPprocNsViewPt; //存放Speex Preprocessor  noise sound торможение Настраивать布局控件的内存指针。
    View m_LyotActivityWebRtcNsxViewPt; //存放WebRtc定点 noise sound Suppressor Настраивать布局控件的内存指针。
    View m_LyotActivityWebRtcNsViewPt; //存放WebRtc浮点 noise sound Suppressor Настраивать布局控件的内存指针。
    View m_LyotActivitySpeexPprocOtherViewPt; //存放Speex Preprocessor  Other functions Настраивать布局控件的内存指针。
    View m_LyotActivitySpeexCodecViewPt; //存放Speex Codec Настраивать布局控件的内存指针。
    View m_LyotActivityOpenH264CodecViewPt; //存放OpenH264 Codec Настраивать布局控件的内存指针。
    View m_LyotActivityAjbViewPt; //存放 Audio Adaptive jitter bufferНастраивать布局控件的内存指针。
    View m_LyotActivityCurViewPt; //存放当前界面布局控件的内存指针。

    MainActivity m_MainActivityPt; //存放主界面类对象的内存指针。
    MyMediaProcThread m_MyMediaProcThreadPt; //存放媒体处理线程类对象的内存指针。
    MainActivityHandler m_MainActivityHandlerPt; //存放主界面消息处理类对象的内存指针。

    HTSurfaceView m_VideoInputPreviewSurfaceViewPt; //存放video enter 预览SurfaceView控件的内存指针。
    HTSurfaceView m_VideoOutputDisplaySurfaceViewPt; //存放video Output 显示SurfaceView控件的内存指针。

    String m_ExternalDirFullAbsPathStrPt; //存放 The full absolute path of the extension directory digit符串的内存指针。

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        LayoutInflater layoutInflater = LayoutInflater.from( this );
        m_LyotActivityMainViewPt = layoutInflater.inflate( R.layout.activity_main, null );
        m_LyotActivitySettingViewPt = layoutInflater.inflate( R.layout.activity_setting, null );
        m_LyotActivitySpeexAecViewPt = layoutInflater.inflate( R.layout.activity_speexaec, null );
        m_LyotActivityWebRtcAecmViewPt = layoutInflater.inflate( R.layout.activity_webrtcaecm, null );
        m_LyotActivityWebRtcAecViewPt = layoutInflater.inflate( R.layout.activity_webrtcaec, null );
        m_LyotActivitySpeexWebRtcAecViewPt = layoutInflater.inflate( R.layout.activity_speexwebrtcaec, null );
        m_LyotActivitySpeexPprocNsViewPt = layoutInflater.inflate( R.layout.activity_speexpprocns, null );
        m_LyotActivityWebRtcNsxViewPt = layoutInflater.inflate( R.layout.activity_webrtcnsx, null );
        m_LyotActivityWebRtcNsViewPt = layoutInflater.inflate( R.layout.activity_webrtcns, null );
        m_LyotActivitySpeexPprocOtherViewPt = layoutInflater.inflate( R.layout.activity_speexpprocother, null );
        m_LyotActivitySpeexCodecViewPt = layoutInflater.inflate( R.layout.activity_speexcodec, null );
        m_LyotActivityOpenH264CodecViewPt = layoutInflater.inflate( R.layout.activity_openh264codec, null );
        m_LyotActivityAjbViewPt = layoutInflater.inflate( R.layout.activity_ajb, null );

        setContentView( m_LyotActivityMainViewPt ); //Настраивать界面的内容 for 主界面。
        m_LyotActivityCurViewPt = m_LyotActivityMainViewPt;

        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseEffectSuperRadioBtn ) ).performClick(); //默认 Effect level ：ultra。
        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseBitrateSuperRadioBtn ) ).performClick(); //默认ratiospecial Rate grade ：ultra。

        //检测并请求录sound权限。
        if( ContextCompat.checkSelfPermission( this, Manifest.permission.RECORD_AUDIO ) != PackageManager.PERMISSION_GRANTED )
            ActivityCompat.requestPermissions( this, new String[] {Manifest.permission.RECORD_AUDIO}, 1 );

        //检测并请求修改 Audio Настраивать权限。
        if( ContextCompat.checkSelfPermission( this, Manifest.permission.MODIFY_AUDIO_SETTINGS ) != PackageManager.PERMISSION_GRANTED )
            ActivityCompat.requestPermissions( this, new String[] {Manifest.permission.MODIFY_AUDIO_SETTINGS}, 1 );

        //检测并请求摄像头权限。
        if( ContextCompat.checkSelfPermission( this, Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED )
            ActivityCompat.requestPermissions( this, new String[] {Manifest.permission.CAMERA}, 1 );

        //检测并请求网络权限。
        if( ContextCompat.checkSelfPermission( this, Manifest.permission.INTERNET ) != PackageManager.PERMISSION_GRANTED )
            ActivityCompat.requestPermissions( this, new String[] {Manifest.permission.INTERNET}, 1 );

        //检测并请求唤醒锁权限。
        if( ContextCompat.checkSelfPermission( this, Manifest.permission.WAKE_LOCK ) != PackageManager.PERMISSION_GRANTED )
            ActivityCompat.requestPermissions( this, new String[] {Manifest.permission.WAKE_LOCK}, 1 );

        //检测并请求 Reception 权限。
        if( ContextCompat.checkSelfPermission( this, Manifest.permission.FOREGROUND_SERVICE ) != PackageManager.PERMISSION_GRANTED )
            ActivityCompat.requestPermissions( this, new String[] {Manifest.permission.FOREGROUND_SERVICE}, 1 );

        //Настраивать主界面类对象。
        m_MainActivityPt = this;

        //初始化消息处理类对象。
        m_MainActivityHandlerPt = new MainActivityHandler();
        m_MainActivityHandlerPt.m_MainActivityPt = m_MainActivityPt;

        //获取this 机IP地址。
        String p_pclString = null;
        try
        {
            //遍历所 Have的网络接口设备。
            out:
            for( Enumeration< NetworkInterface > clEnumerationNetworkInterface = NetworkInterface.getNetworkInterfaces(); clEnumerationNetworkInterface.hasMoreElements(); )
            {
                NetworkInterface clNetworkInterface = clEnumerationNetworkInterface.nextElement();
                if( clNetworkInterface.getName().compareTo( "usbnet0" ) != 0 ) //如果该网络接口设备不是USB接口对应的网络接口设备。
                {
                    //遍历该网络接口设备所 Have的IP地址。
                    for( Enumeration< InetAddress > enumIpAddr = clNetworkInterface.getInetAddresses(); enumIpAddr.hasMoreElements(); )
                    {
                        InetAddress clInetAddress = enumIpAddr.nextElement();
                        if( ( !clInetAddress.isLoopbackAddress() ) && ( clInetAddress.getAddress().length == 4 ) ) //如果该IP地址不是回环地址，且是IPv4的。
                        {
                            p_pclString = clInetAddress.getHostAddress();
                            break out;
                        }
                    }
                }
            }
        }
        catch( SocketException e )
        {
        }
        if( p_pclString == null )
        {
            p_pclString = "127.0.0.1";
        }

        //НастраиватьIP地址控件的内容。
        ( ( EditText ) m_LyotActivityMainViewPt.findViewById( R.id.IPAddrEdit ) ).setText( p_pclString );

        //Настраиватьport控件的内容。
        ( ( EditText ) m_LyotActivityMainViewPt.findViewById( R.id.PortEdit ) ).setText( "12345" );

        //添加video enter 预览SurfaceView的回调函 number 。
        m_VideoInputPreviewSurfaceViewPt = ( ( HTSurfaceView )findViewById( R.id.VideoInputPreviewSurfaceView ) );
        m_VideoInputPreviewSurfaceViewPt.getHolder().setType( SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS );
        m_VideoInputPreviewSurfaceViewPt.getHolder().addCallback( new SurfaceHolder.Callback()
        {
            @Override
            public void surfaceCreated( SurfaceHolder holder )
            {
                Log.i( m_CurClsNameStrPt, "VideoInputPreviewSurfaceView Created" );
                if( m_MyMediaProcThreadPt != null && m_MyMediaProcThreadPt.m_VideoInputPt.m_IsUseVideoInput != 0 && m_MyMediaProcThreadPt.m_RunFlag == MediaProcThread.RUN_FLAG_PROC ) //如果SurfaceView已经重新创建，且媒体处理线程已经启动，且要 use video enter ，并处于初始化完毕正 в 循环处理 frame 。
                {
                    m_MyMediaProcThreadPt.RequireExit( 3, 1 ); //请求重启媒体处理线程，来保证正常的video enter ，否则video enter 会in断。
                }
            }

            @Override
            public void surfaceChanged( SurfaceHolder holder, int format, int width, int height )
            {
                Log.i( m_CurClsNameStrPt, "VideoInputPreviewSurfaceView Changed" );
            }

            @Override
            public void surfaceDestroyed( SurfaceHolder holder )
            {
                Log.i( m_CurClsNameStrPt, "VideoInputPreviewSurfaceView Destroyed" );
            }
        } );
        m_VideoOutputDisplaySurfaceViewPt = ( ( HTSurfaceView )findViewById( R.id.VideoOutputDisplaySurfaceView ) );
        m_VideoOutputDisplaySurfaceViewPt.getHolder().setType( SurfaceHolder.SURFACE_TYPE_NORMAL );

        //获取 The full absolute path of the extension directory digit符串。
        if( getExternalFilesDir( null ) != null )
        {
            m_ExternalDirFullAbsPathStrPt = getExternalFilesDir( null ).getPath();
        }
        else
        {
            m_ExternalDirFullAbsPathStrPt = Environment.getExternalStorageDirectory().getPath() + "/Android/data/" + getApplicationContext().getPackageName();
        }

        String p_InfoStrPt = " The full absolute path of the extension directory ：" + m_ExternalDirFullAbsPathStrPt;
        Log.i( m_CurClsNameStrPt, p_InfoStrPt );
        Message p_MessagePt = new Message();p_MessagePt.what = 3;p_MessagePt.obj = p_InfoStrPt;m_MainActivityHandlerPt.sendMessage( p_MessagePt );
    }

    //返回键。
    @Override
    public void onBackPressed()
    {
        if( m_LyotActivityCurViewPt == m_LyotActivityMainViewPt )
        {
            Log.i( m_CurClsNameStrPt, " user  в Press the return button on the main interface，this The software exits." );
            if( m_MyMediaProcThreadPt != null )
            {
                Log.i( m_CurClsNameStrPt, "Start the request and wait for the media processing thread to exit." );
                m_MyMediaProcThreadPt.RequireExit( 1, 1 );
                Log.i( m_CurClsNameStrPt, "End the request and wait for the media processing thread to exit." );
            }
            System.exit(0);
        }
        else if( m_LyotActivityCurViewPt == m_LyotActivitySettingViewPt )
        {
            this.OnClickSettingOk( null );
        }
        else if( ( m_LyotActivityCurViewPt == m_LyotActivitySpeexAecViewPt ) ||
                ( m_LyotActivityCurViewPt == m_LyotActivityWebRtcAecmViewPt ) ||
                ( m_LyotActivityCurViewPt == m_LyotActivityWebRtcAecViewPt ) ||
                ( m_LyotActivityCurViewPt == m_LyotActivitySpeexWebRtcAecViewPt ) ||
                ( m_LyotActivityCurViewPt == m_LyotActivitySpeexPprocNsViewPt ) ||
                ( m_LyotActivityCurViewPt == m_LyotActivityWebRtcNsxViewPt ) ||
                ( m_LyotActivityCurViewPt == m_LyotActivityWebRtcNsViewPt ) ||
                ( m_LyotActivityCurViewPt == m_LyotActivitySpeexPprocOtherViewPt ) ||
                ( m_LyotActivityCurViewPt == m_LyotActivitySpeexCodecViewPt ) ||
                ( m_LyotActivityCurViewPt == m_LyotActivityOpenH264CodecViewPt ) ||
                ( m_LyotActivityCurViewPt == m_LyotActivityAjbViewPt ) )
        {
            this.OnClickWebRtcAecSettingOk( null );
        }
    }

    // use  Audio 按钮。
    public void OnUseAudio( View BtnPt )
    {
        if( m_MyMediaProcThreadPt != null )
        {
            m_MyMediaProcThreadPt.m_AudioInputPt.m_IsUseAudioInput = 1;
            m_MyMediaProcThreadPt.m_AudioOutputPt.m_IsUseAudioOutput = 1;
            m_MyMediaProcThreadPt.m_VideoInputPt.m_IsUseVideoInput = 0;
            m_MyMediaProcThreadPt.m_VideoOutputPt.m_IsUseVideoOutput = 0;

            if( m_MyMediaProcThreadPt.m_RunFlag > MediaProcThread.RUN_FLAG_INIT ) //如果要 use  Audio  Output ，且媒体处理线程已经初始化完毕。
            {
                m_MyMediaProcThreadPt.RequireExit( 3, 1 ); //请求重启并阻塞等待。
            }
        }
    }

    // use video按钮。
    public void OnUseVideo( View BtnPt )
    {
        if( m_MyMediaProcThreadPt != null )
        {
            m_MyMediaProcThreadPt.m_AudioInputPt.m_IsUseAudioInput = 0;
            m_MyMediaProcThreadPt.m_AudioOutputPt.m_IsUseAudioOutput = 0;
            m_MyMediaProcThreadPt.m_VideoInputPt.m_IsUseVideoInput = 1;
            m_MyMediaProcThreadPt.m_VideoOutputPt.m_IsUseVideoOutput = 1;

            if( m_MyMediaProcThreadPt.m_RunFlag > MediaProcThread.RUN_FLAG_INIT ) //如果要 use  Audio  Output ，且媒体处理线程已经初始化完毕。
            {
                m_MyMediaProcThreadPt.RequireExit( 3, 1 ); //请求重启并阻塞等待。
            }
        }
    }

    // use soundvideo按钮。
    public void OnUseAudioVideo( View BtnPt )
    {
        if( m_MyMediaProcThreadPt != null )
        {
            m_MyMediaProcThreadPt.m_AudioInputPt.m_IsUseAudioInput = 1;
            m_MyMediaProcThreadPt.m_AudioOutputPt.m_IsUseAudioOutput = 1;
            m_MyMediaProcThreadPt.m_VideoInputPt.m_IsUseVideoInput = 1;
            m_MyMediaProcThreadPt.m_VideoOutputPt.m_IsUseVideoOutput = 1;

            if( m_MyMediaProcThreadPt.m_RunFlag > MediaProcThread.RUN_FLAG_INIT ) //如果要 use  Audio  Output ，且媒体处理线程已经初始化完毕。
            {
                m_MyMediaProcThreadPt.RequireExit( 3, 1 ); //请求重启并阻塞等待。
            }
        }
    }

    // use speaker按钮。
    public void OnUseSpeaker( View BtnPt )
    {
        if( m_MyMediaProcThreadPt != null )
        {
            m_MyMediaProcThreadPt.SetAudioOutputUseDevice( 0, 0 );

            if( m_MyMediaProcThreadPt.m_AudioOutputPt.m_IsUseAudioOutput != 0 && m_MyMediaProcThreadPt.m_RunFlag > MediaProcThread.RUN_FLAG_INIT ) //如果要 use  Audio  Output ，且媒体处理线程已经初始化完毕。
            {
                m_MyMediaProcThreadPt.RequireExit( 3, 1 ); //请求重启并阻塞等待。
            }
        }
    }

    // use earpiece按钮。
    public void OnUseHeadset( View BtnPt )
    {
        if( m_MyMediaProcThreadPt != null )
        {
            m_MyMediaProcThreadPt.SetAudioOutputUseDevice( 1, 0 );

            if( m_MyMediaProcThreadPt.m_AudioOutputPt.m_IsUseAudioOutput != 0 && m_MyMediaProcThreadPt.m_RunFlag > MediaProcThread.RUN_FLAG_INIT ) //如果要 use  Audio  Output ，且媒体处理线程已经初始化完毕。
            {
                m_MyMediaProcThreadPt.RequireExit( 3, 1 ); //请求重启并阻塞等待。
            }
        }
    }

    // use Front camera按钮。
    public void OnUseFrontCamere( View BtnPt )
    {
        if( m_MyMediaProcThreadPt != null )
        {
            m_MyMediaProcThreadPt.SetVideoInputUseDevice( 0 );

            if( m_MyMediaProcThreadPt.m_VideoInputPt.m_IsUseVideoInput != 0 && m_MyMediaProcThreadPt.m_RunFlag > MediaProcThread.RUN_FLAG_INIT ) //如果要 use  Audio  Output ，且媒体处理线程已经初始化完毕。
            {
                m_MyMediaProcThreadPt.RequireExit( 3, 1 ); //请求重启并阻塞等待。
            }
        }
    }

    // use rear camera按钮。
    public void OnUseBackCamere( View BtnPt )
    {
        if( m_MyMediaProcThreadPt != null )
        {
            m_MyMediaProcThreadPt.SetVideoInputUseDevice( 1 );

            if( m_MyMediaProcThreadPt.m_VideoInputPt.m_IsUseVideoInput != 0 && m_MyMediaProcThreadPt.m_RunFlag > MediaProcThread.RUN_FLAG_INIT ) //如果要 use  Audio  Output ，且媒体处理线程已经初始化完毕。
            {
                m_MyMediaProcThreadPt.RequireExit( 3, 1 ); //请求重启并阻塞等待。
            }
        }
    }

    // Audio input device Quiet sound按钮。
    public void OnAudioInputDeviceIsMute( View BtnPt )
    {
        if( m_MyMediaProcThreadPt != null )
        {
            m_MyMediaProcThreadPt.SetAudioInputDeviceIsMute( ( ( ( CheckBox ) m_LyotActivityMainViewPt.findViewById( R.id.AudioInputDeviceIsMuteCheckBox ) ).isChecked() ) ? 1 : 0 );
        }
    }

    // Audio  Output device  Quiet sound按钮。
    public void OnAudioOutputDeviceIsMute( View BtnPt )
    {
        if( m_MyMediaProcThreadPt != null )
        {
            m_MyMediaProcThreadPt.SetAudioOutputDeviceIsMute( ( ( ( CheckBox ) m_LyotActivityMainViewPt.findViewById( R.id.AudioOutputDeviceIsMuteCheckBox ) ).isChecked() ) ? 1 : 0 );
        }
    }

    //videoinput device Black screen按钮。
    public void OnVideoInputDeviceIsBlack( View BtnPt )
    {
        if( m_MyMediaProcThreadPt != null )
        {
            m_MyMediaProcThreadPt.SetVideoInputDeviceIsBlack( ( ( ( CheckBox ) m_LyotActivityMainViewPt.findViewById( R.id.VideoInputDeviceIsBlackCheckBox ) ).isChecked() ) ? 1 : 0 );
        }
    }

    // Time 频 Output device  Black screen按钮。
    public void OnVideoOutputDeviceIsBlack( View BtnPt )
    {
        if( m_MyMediaProcThreadPt != null )
        {
            m_MyMediaProcThreadPt.SetVideoOutputDeviceIsBlack( ( ( ( CheckBox ) m_LyotActivityMainViewPt.findViewById( R.id.VideoOutputDeviceIsBlackCheckBox ) ).isChecked() ) ? 1 : 0 );
        }
    }

    //创建服务器或连接服务器按钮。
    public void OnClickCreateSrvrAndConnectSrvr( View BtnPt )
    {
        int p_Result = -1; //存放this 函 number 执行 result 的值， for 0表示成功， for 非0表示失败。

        out:
        {
            if( m_MyMediaProcThreadPt == null ) //如果媒体处理线程还没 Have启动。
            {
                Log.i( m_CurClsNameStrPt, "Start the media processing thread." );

                //创建并初始化媒体处理线程类对象。
                {
                    m_MyMediaProcThreadPt = new MyMediaProcThread( getApplicationContext() ); //创建媒体处理线程类对象。

                    if( BtnPt.getId() == R.id.CreateSrvrBtn )
                    {
                        m_MyMediaProcThreadPt.m_IsCreateSrvrOrClnt = 1; //标记 Create server 接受客户端。
                    }
                    else if( BtnPt.getId() == R.id.ConnectSrvrBtn )
                    {
                        m_MyMediaProcThreadPt.m_IsCreateSrvrOrClnt = 0; //标记创建客户端 Connect to the server 。
                    }

                    m_MyMediaProcThreadPt.m_MainActivityHandlerPt = m_MainActivityHandlerPt; //Настраивать主界面消息处理类对象的内存指针。

                    //НастраиватьIP地址digit符串、port。
                    m_MyMediaProcThreadPt.m_IPAddrStrPt = ( ( EditText ) m_LyotActivityMainViewPt.findViewById( R.id.IPAddrEdit ) ).getText().toString();
                    m_MyMediaProcThreadPt.m_PortStrPt = ( ( EditText ) m_LyotActivityMainViewPt.findViewById( R.id.PortEdit ) ).getText().toString();

                    //判断是否 use 什么Transfer Protocol。
                    if( ( ( RadioButton ) m_LyotActivityMainViewPt.findViewById( R.id.UseTcpPrtclRadioBtn ) ).isChecked() )
                    {
                        m_MyMediaProcThreadPt.m_UseWhatXfrPrtcl = 0;
                    }
                    else
                    {
                        m_MyMediaProcThreadPt.m_UseWhatXfrPrtcl = 1;
                    }

                    //判断是否 use Linked list。
                    if( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseLnkLstRadioBtn ) ).isChecked() )
                    {
                        m_MyMediaProcThreadPt.m_UseWhatRecvOutputFrame = 0;
                    }

                    //判断是否 use  Self-designed  Audio Adaptive jitter buffer。
                    if( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseAjbRadioBtn ) ).isChecked() )
                    {
                        m_MyMediaProcThreadPt.m_UseWhatRecvOutputFrame = 1;

                        try
                        {
                            m_MyMediaProcThreadPt.m_AAjbMinNeedBufFrameCnt = Integer.parseInt( ( ( TextView ) m_LyotActivityAjbViewPt.findViewById( R.id.AAjbMinNeedBufFrameCnt ) ).getText().toString() );
                            m_MyMediaProcThreadPt.m_AAjbMaxNeedBufFrameCnt = Integer.parseInt( ( ( TextView ) m_LyotActivityAjbViewPt.findViewById( R.id.AAjbMaxNeedBufFrameCnt ) ).getText().toString() );
                            m_MyMediaProcThreadPt.m_AAjbAdaptSensitivity = Float.parseFloat( ( ( TextView ) m_LyotActivityAjbViewPt.findViewById( R.id.AAjbAdaptSensitivity ) ).getText().toString() );

                            m_MyMediaProcThreadPt.m_VAjbMinNeedBufFrameCnt = Integer.parseInt( ( ( TextView ) m_LyotActivityAjbViewPt.findViewById( R.id.VAjbMinNeedBufFrameCnt ) ).getText().toString() );
                            m_MyMediaProcThreadPt.m_VAjbMaxNeedBufFrameCnt = Integer.parseInt( ( ( TextView ) m_LyotActivityAjbViewPt.findViewById( R.id.VAjbMaxNeedBufFrameCnt ) ).getText().toString() );
                            m_MyMediaProcThreadPt.m_VAjbAdaptSensitivity = Float.parseFloat( ( ( TextView ) m_LyotActivityAjbViewPt.findViewById( R.id.VAjbAdaptSensitivity ) ).getText().toString() );
                        }
                        catch( NumberFormatException e )
                        {
                            Toast.makeText( this, " please enter  number digit", Toast.LENGTH_LONG ).show();
                            break out;
                        }
                    }

                    //判断是否 Save Настраивать到file。
                    if( ( ( CheckBox ) m_LyotActivitySettingViewPt.findViewById( R.id.IsSaveSettingToFileCheckBox ) ).isChecked() )
                    {
                        m_MyMediaProcThreadPt.SetIsSaveSettingToFile( 1, m_ExternalDirFullAbsPathStrPt + "/Setting.txt" );
                    }
                    else
                    {
                        m_MyMediaProcThreadPt.SetIsSaveSettingToFile( 0, null );
                    }

                    //判断是否 print LogcatLog。
                    if( ( ( CheckBox ) m_LyotActivitySettingViewPt.findViewById( R.id.IsPrintLogcatCheckBox ) ).isChecked() )
                    {
                        m_MyMediaProcThreadPt.SetIsPrintLogcat( 1 );
                    }
                    else
                    {
                        m_MyMediaProcThreadPt.SetIsPrintLogcat( 0 );
                    }

                    //判断是否 Use wake lock 。
                    if( ( ( CheckBox ) m_MainActivityPt.m_LyotActivitySettingViewPt.findViewById( R.id.IsUseWakeLockCheckBox ) ).isChecked() )
                    {
                        m_MyMediaProcThreadPt.SetIsUseWakeLock( 1 );
                    }
                    else
                    {
                        m_MyMediaProcThreadPt.SetIsUseWakeLock( 0 );
                    }

                    //判断是否 use  Audio  enter 。
                    m_MyMediaProcThreadPt.SetIsUseAudioInput(
                            ( ( ( RadioButton ) m_LyotActivityMainViewPt.findViewById( R.id.UseAudioTalkbackRadioBtn ) ).isChecked() ) ? 1 :
                                    ( ( ( RadioButton ) m_LyotActivityMainViewPt.findViewById( R.id.UseAudioVideoTalkbackRadioBtn ) ).isChecked() ) ? 1 : 0,
                            ( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseAudioSamplingRate8000RadioBtn ) ).isChecked() ) ? 8000 :
                                    ( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseAudioSamplingRate16000RadioBtn ) ).isChecked() ) ? 16000 :
                                            ( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseAudioSamplingRate32000RadioBtn ) ).isChecked() ) ? 32000 : 0,
                            ( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseAudioFrameLen10msRadioBtn ) ).isChecked() ) ? 10 :
                                    ( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseAudioFrameLen20msRadioBtn ) ).isChecked() ) ? 20 :
                                            ( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseAudioFrameLen30msRadioBtn ) ).isChecked() ) ? 30 : 0 );

                    //判断 Audio  enter 是否 Use the acoustic feedback that comes with the system sound Canceller, noise sound Suppressor and automatic gain controller 。
                    if( ( ( CheckBox ) m_LyotActivitySettingViewPt.findViewById( R.id.IsUseSystemAecNsAgcCheckBox ) ).isChecked() )
                    {
                        m_MyMediaProcThreadPt.SetAudioInputIsUseSystemAecNsAgc( 1 );
                    }
                    else
                    {
                        m_MyMediaProcThreadPt.SetAudioInputIsUseSystemAecNsAgc( 0 );
                    }

                    //判断 Audio  enter 是否 Do not use  Acoustic echo sound Eliminator 。
                    if( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseNoAecRadioBtn ) ).isChecked() )
                    {
                        m_MyMediaProcThreadPt.SetAudioInputUseNoAec();
                    }

                    //判断 Audio  enter 是否 use Speex Acoustic echo sound Eliminator 。
                    if( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseSpeexAecRadioBtn ) ).isChecked() )
                    {
                        try
                        {
                            m_MyMediaProcThreadPt.SetAudioInputUseSpeexAec(
                                    Integer.parseInt( ( ( TextView ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecFilterLenEdit ) ).getText().toString() ),
                                    ( ( ( CheckBox ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecIsUseRecCheckBox ) ).isChecked() ) ? 1 : 0,
                                    Float.parseFloat( ( ( TextView ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecEchoMultipleEdit ) ).getText().toString() ),
                                    Float.parseFloat( ( ( TextView ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecEchoContEdit ) ).getText().toString() ),
                                    Integer.parseInt( ( ( TextView ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecEchoSupesEdit ) ).getText().toString() ),
                                    Integer.parseInt( ( ( TextView ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecEchoSupesActEdit ) ).getText().toString() ),
                                    ( ( ( CheckBox ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecIsSaveMemFileCheckBox ) ).isChecked() ) ? 1 : 0,
                                    m_ExternalDirFullAbsPathStrPt + "/SpeexAecMem"
                            );
                        }
                        catch( NumberFormatException e )
                        {
                            Toast.makeText( this, " please enter  number digit", Toast.LENGTH_LONG ).show();
                            break out;
                        }
                    }

                    //判断 Audio  enter 是否 use WebRtc Fixed-point version  Acoustic echo sound Eliminator 。
                    if( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseWebRtcAecmRadioBtn ) ).isChecked() )
                    {
                        try
                        {
                            m_MyMediaProcThreadPt.SetAudioInputUseWebRtcAecm(
                                    ( ( ( CheckBox ) m_LyotActivityWebRtcAecmViewPt.findViewById( R.id.WebRtcAecmIsUseCNGModeCheckBox ) ).isChecked() ) ? 1 : 0,
                                    Integer.parseInt( ( ( TextView ) m_LyotActivityWebRtcAecmViewPt.findViewById( R.id.WebRtcAecmEchoModeEdit ) ).getText().toString() ),
                                    Integer.parseInt( ( ( TextView ) m_LyotActivityWebRtcAecmViewPt.findViewById( R.id.WebRtcAecmDelayEdit ) ).getText().toString() )
                            );
                        }
                        catch( NumberFormatException e )
                        {
                            Toast.makeText( this, " please enter  number digit", Toast.LENGTH_LONG ).show();
                            break out;
                        }
                    }

                    //判断 Audio  enter 是否 use WebRtc Floating point version  Acoustic echo sound Eliminator 。
                    if( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseWebRtcAecRadioBtn ) ).isChecked() )
                    {
                        try
                        {
                            m_MyMediaProcThreadPt.SetAudioInputUseWebRtcAec(
                                    Integer.parseInt( ( ( TextView ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecEchoModeEdit ) ).getText().toString() ),
                                    Integer.parseInt( ( ( TextView ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecDelayEdit ) ).getText().toString() ),
                                    ( ( ( CheckBox ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecIsUseDelayAgnosticModeCheckBox ) ).isChecked() ) ? 1 : 0,
                                    ( ( ( CheckBox ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecIsUseExtdFilterModeCheckBox ) ).isChecked() ) ? 1 : 0,
                                    ( ( ( CheckBox ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecIsUseRefinedFilterAdaptAecModeCheckBox ) ).isChecked() ) ? 1 : 0,
                                    ( ( ( CheckBox ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecIsUseAdaptAdjDelayCheckBox ) ).isChecked() ) ? 1 : 0,
                                    ( ( ( CheckBox ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecIsSaveMemFileCheckBox ) ).isChecked() ) ? 1 : 0,
                                    m_ExternalDirFullAbsPathStrPt + "/WebRtcAecMem"
                            );
                        }
                        catch( NumberFormatException e )
                        {
                            Toast.makeText( this, " please enter  number digit", Toast.LENGTH_LONG ).show();
                            break out;
                        }
                    }

                    //判断 Audio  enter 是否 use SpeexWebRtc triple  Acoustic echo sound Eliminator 。
                    if( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseSpeexWebRtcAecRadioBtn ) ).isChecked() )
                    {
                        try
                        {
                            m_MyMediaProcThreadPt.SetAudioInputUseSpeexWebRtcAec(
                                    ( ( RadioButton ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWorkModeSpeexAecWebRtcAecmRadioBtn ) ).isChecked() ? 1 :
                                            ( ( RadioButton ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWorkModeWebRtcAecmWebRtcAecRadioBtn ) ).isChecked() ? 2 :
                                                    ( ( RadioButton ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWorkModeSpeexAecWebRtcAecmWebRtcAecRadioBtn ) ).isChecked() ? 3 : 0,
                                    Integer.parseInt( ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSpeexAecFilterLenEdit ) ).getText().toString() ),
                                    ( ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSpeexAecIsUseRecCheckBox ) ).isChecked() ) ? 1 : 0,
                                    Float.parseFloat( ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSpeexAecEchoMultipleEdit ) ).getText().toString() ),
                                    Float.parseFloat( ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSpeexAecEchoContEdit ) ).getText().toString() ),
                                    Integer.parseInt( ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSpeexAecEchoSupesEdit ) ).getText().toString() ),
                                    Integer.parseInt( ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSpeexAecEchoSupesActEdit ) ).getText().toString() ),
                                    ( ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecmIsUseCNGModeCheckBox ) ).isChecked() ) ? 1 : 0,
                                    Integer.parseInt( ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecmEchoModeEdit ) ).getText().toString() ),
                                    Integer.parseInt( ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecmDelayEdit ) ).getText().toString() ),
                                    Integer.parseInt( ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecEchoModeEdit ) ).getText().toString() ),
                                    Integer.parseInt( ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecDelayEdit ) ).getText().toString() ),
                                    ( ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecIsUseDelayAgnosticModeCheckBox ) ).isChecked() ) ? 1 : 0,
                                    ( ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecIsUseExtdFilterModeCheckBox ) ).isChecked() ) ? 1 : 0,
                                    ( ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecIsUseRefinedFilterAdaptAecModeCheckBox ) ).isChecked() ) ? 1 : 0,
                                    ( ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecIsUseAdaptAdjDelayCheckBox ) ).isChecked() ) ? 1 : 0,
                                    ( ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecIsUseSameRoomAecCheckBox ) ).isChecked() ) ? 1 : 0,
                                    Integer.parseInt( ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSameRoomEchoMinDelayEdit ) ).getText().toString() )
                            );
                        }
                        catch( NumberFormatException e )
                        {
                            Toast.makeText( this, " please enter  number digit", Toast.LENGTH_LONG ).show();
                            break out;
                        }
                    }

                    //判断 Audio  enter 是否 Do not use  noise sound Suppressor 。
                    if( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseNoNsRadioBtn ) ).isChecked() )
                    {
                        m_MyMediaProcThreadPt.SetAudioInputUseNoNs();
                    }

                    //判断 Audio  enter 是否 use Speex Preprocessor  noise sound торможение 。
                    if( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseSpeexPprocNsRadioBtn ) ).isChecked() )
                    {
                        try
                        {
                            m_MyMediaProcThreadPt.SetAudioInputUseSpeexPprocNs(
                                    ( ( ( CheckBox ) m_LyotActivitySpeexPprocNsViewPt.findViewById( R.id.SpeexPprocIsUseNsCheckBox ) ).isChecked() ) ? 1 : 0,
                                    Integer.parseInt( ( ( TextView ) m_LyotActivitySpeexPprocNsViewPt.findViewById( R.id.SpeexPprocNoiseSupesEdit ) ).getText().toString() ),
                                    ( ( ( CheckBox ) m_LyotActivitySpeexPprocNsViewPt.findViewById( R.id.SpeexPprocIsUseDereverbCheckBox ) ).isChecked() ) ? 1 : 0
                            );
                        }
                        catch( NumberFormatException e )
                        {
                            Toast.makeText( this, " please enter  number digit", Toast.LENGTH_LONG ).show();
                            break out;
                        }
                    }

                    //判断 Audio  enter 是否 use WebRtc Fixed-point version  noise sound Suppressor 。
                    if( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseWebRtcNsxRadioBtn ) ).isChecked() )
                    {
                        try
                        {
                            m_MyMediaProcThreadPt.SetAudioInputUseWebRtcNsx(
                                    Integer.parseInt( ( ( TextView ) m_LyotActivityWebRtcNsxViewPt.findViewById( R.id.WebRtcNsxPolicyModeEdit ) ).getText().toString() )
                            );
                        }
                        catch( NumberFormatException e )
                        {
                            Toast.makeText( this, " please enter  number digit", Toast.LENGTH_LONG ).show();
                            break out;
                        }
                    }

                    //判断 Audio  enter 是否 use WebRtc Floating point version  noise sound Suppressor 。
                    if( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseWebRtcNsRadioBtn ) ).isChecked() )
                    {
                        try
                        {
                            m_MyMediaProcThreadPt.SetAudioInputUseWebRtcNs(
                                    Integer.parseInt( ( ( TextView ) m_LyotActivityWebRtcNsViewPt.findViewById( R.id.WebRtcNsPolicyModeEdit ) ).getText().toString() )
                            );
                        }
                        catch( NumberFormatException e )
                        {
                            Toast.makeText( this, " please enter  number digit", Toast.LENGTH_LONG ).show();
                            break out;
                        }
                    }

                    //判断 Audio  enter 是否 use RNNoise noise sound Suppressor 。
                    if( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseRNNoiseRadioBtn ) ).isChecked() )
                    {
                        try
                        {
                            m_MyMediaProcThreadPt.SetAudioInputUseRNNoise();
                        }
                        catch( NumberFormatException e )
                        {
                            Toast.makeText( this, " please enter  number digit", Toast.LENGTH_LONG ).show();
                            break out;
                        }
                    }

                    //判断 Audio  enter 是否 use Speex Preprocessor  Other functions 。
                    if( ( ( CheckBox ) m_LyotActivitySettingViewPt.findViewById( R.id.IsUseSpeexPprocOtherCheckBox ) ).isChecked() )
                    {
                        try
                        {
                            m_MyMediaProcThreadPt.SetAudioInputIsUseSpeexPprocOther(
                                    1,
                                    ( ( ( CheckBox ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocIsUseVadCheckBox ) ).isChecked() ) ? 1 : 0,
                                    Integer.parseInt( ( ( TextView ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocVadProbStartEdit ) ).getText().toString() ),
                                    Integer.parseInt( ( ( TextView ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocVadProbContEdit ) ).getText().toString() ),
                                    ( ( ( CheckBox ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocIsUseAgcCheckBox ) ).isChecked() ) ? 1 : 0,
                                    Integer.parseInt( ( ( TextView ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocAgcLevelEdit ) ).getText().toString() ),
                                    Integer.parseInt( ( ( TextView ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocAgcIncrementEdit ) ).getText().toString() ),
                                    Integer.parseInt( ( ( TextView ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocAgcDecrementEdit ) ).getText().toString() ),
                                    Integer.parseInt( ( ( TextView ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocAgcMaxGainEdit ) ).getText().toString() )
                            );
                        }
                        catch( NumberFormatException e )
                        {
                            Toast.makeText( this, " please enter  number digit", Toast.LENGTH_LONG ).show();
                            break out;
                        }
                    }
                    else
                    {
                        m_MyMediaProcThreadPt.SetAudioInputIsUseSpeexPprocOther( 0, 0, 0, 0, 0, 0, 0, 0, 0 );
                    }

                    //判断 Audio  enter 是否 use PCM Raw data 。
                    if( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UsePcmRadioBtn ) ).isChecked() )
                    {
                        m_MyMediaProcThreadPt.SetAudioInputUsePcm();
                    }

                    //判断 Audio  enter 是否 use Speex Encoder 。
                    if( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseSpeexCodecRadioBtn ) ).isChecked() )
                    {
                        try
                        {
                            m_MyMediaProcThreadPt.SetAudioInputUseSpeexEncoder(
                                    ( ( ( RadioButton ) m_LyotActivitySpeexCodecViewPt.findViewById( R.id.SpeexCodecEncoderUseCbrRadioBtn ) ).isChecked() ) ? 0 : 1,
                                    Integer.parseInt( ( ( TextView ) m_LyotActivitySpeexCodecViewPt.findViewById( R.id.SpeexCodecEncoderQualityEdit ) ).getText().toString() ),
                                    Integer.parseInt( ( ( TextView ) m_LyotActivitySpeexCodecViewPt.findViewById( R.id.SpeexCodecEncoderComplexityEdit ) ).getText().toString() ),
                                    Integer.parseInt( ( ( TextView ) m_LyotActivitySpeexCodecViewPt.findViewById( R.id.SpeexCodecEncoderPlcExpectedLossRateEdit ) ).getText().toString() )
                            );
                        }
                        catch( NumberFormatException e )
                        {
                            Toast.makeText( this, " please enter  number digit", Toast.LENGTH_LONG ).show();
                            break out;
                        }
                    }

                    //判断 Audio  enter 是否 use Opus Encoder 。
                    if( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseOpusCodecRadioBtn ) ).isChecked() )
                    {
                        m_MyMediaProcThreadPt.SetAudioInputUseOpusEncoder();
                    }

                    //判断 Audio  enter 是否 Save  Audio 到file。
                    if( ( ( CheckBox ) m_LyotActivitySettingViewPt.findViewById( R.id.IsSaveAudioToFileCheckBox ) ).isChecked() )
                    {
                        m_MyMediaProcThreadPt.SetAudioInputIsSaveAudioToFile(
                                1,
                                m_ExternalDirFullAbsPathStrPt + "/AudioInput.wav",
                                m_ExternalDirFullAbsPathStrPt + "/AudioResult.wav"
                        );
                    }
                    else
                    {
                        m_MyMediaProcThreadPt.SetAudioInputIsSaveAudioToFile( 0, null, null );
                    }

                    //判断 Audio input device是否 Quiet sound。
                    m_MyMediaProcThreadPt.SetAudioInputDeviceIsMute( ( ( ( CheckBox ) m_LyotActivityMainViewPt.findViewById( R.id.AudioInputDeviceIsMuteCheckBox ) ).isChecked() ) ? 1 : 0 );

                    //判断是否 use  Audio  Output 。
                    m_MyMediaProcThreadPt.SetIsUseAudioOutput(
                            ( ( ( RadioButton ) m_LyotActivityMainViewPt.findViewById( R.id.UseAudioTalkbackRadioBtn ) ).isChecked() ) ? 1 :
                                    ( ( ( RadioButton ) m_LyotActivityMainViewPt.findViewById( R.id.UseAudioVideoTalkbackRadioBtn ) ).isChecked() ) ? 1 : 0,
                            ( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseAudioSamplingRate8000RadioBtn ) ).isChecked() ) ? 8000 :
                                    ( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseAudioSamplingRate16000RadioBtn ) ).isChecked() ) ? 16000 :
                                            ( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseAudioSamplingRate32000RadioBtn ) ).isChecked() ) ? 32000 : 0,
                            ( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseAudioFrameLen10msRadioBtn ) ).isChecked() ) ? 10 :
                                    ( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseAudioFrameLen20msRadioBtn ) ).isChecked() ) ? 20 :
                                            ( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseAudioFrameLen30msRadioBtn ) ).isChecked() ) ? 30 : 0 );

                    //判断 Audio  Output 是否 use PCM Raw data 。
                    if( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UsePcmRadioBtn ) ).isChecked() )
                    {
                        m_MyMediaProcThreadPt.SetAudioOutputUsePcm();
                    }

                    //判断 Audio  Output 是否 use Speex解码器。
                    if( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseSpeexCodecRadioBtn ) ).isChecked() )
                    {
                        try
                        {
                            m_MyMediaProcThreadPt.SetAudioOutputUseSpeexDecoder(
                                    ( ( ( CheckBox ) m_LyotActivitySpeexCodecViewPt.findViewById( R.id.SpeexCodecIsUsePerceptualEnhancementCheckBox ) ).isChecked() ) ? 1 : 0
                            );
                        }
                        catch( NumberFormatException e )
                        {
                            Toast.makeText( this, " please enter  number digit", Toast.LENGTH_LONG ).show();
                            break out;
                        }
                    }

                    //判断 Audio  Output 是否 use Opus解码器。
                    if( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseOpusCodecRadioBtn ) ).isChecked() )
                    {
                        m_MyMediaProcThreadPt.SetAudioOutputUseOpusDecoder();
                    }

                    //判断 use 的 Audio  Output device 。
                    if( ( ( RadioButton ) m_LyotActivityMainViewPt.findViewById( R.id.UseSpeakerRadioBtn ) ).isChecked() )
                    {
                        m_MyMediaProcThreadPt.SetAudioOutputUseDevice( 0, 0 );
                    }
                    else
                    {
                        m_MyMediaProcThreadPt.SetAudioOutputUseDevice( 1, 0 );
                    }

                    //判断 Audio  Output device 是否 Quiet sound。
                    m_MyMediaProcThreadPt.SetAudioOutputDeviceIsMute( ( ( ( CheckBox ) m_LyotActivityMainViewPt.findViewById( R.id.AudioOutputDeviceIsMuteCheckBox ) ).isChecked() ) ? 1 : 0 );

                    //判断 Audio  Output 是否 Save  Audio 到file。
                    if( ( ( CheckBox ) m_LyotActivitySettingViewPt.findViewById( R.id.IsSaveAudioToFileCheckBox ) ).isChecked() )
                    {
                        m_MyMediaProcThreadPt.SetAudioOutputIsSaveAudioToFile(
                                1,
                                m_ExternalDirFullAbsPathStrPt + "/AudioOutput.wav"
                        );
                    }
                    else
                    {
                        m_MyMediaProcThreadPt.SetAudioOutputIsSaveAudioToFile( 0, null );
                    }

                    //判断是否 use video enter 。
                    m_MyMediaProcThreadPt.SetIsUseVideoInput(
                            ( ( ( RadioButton ) m_LyotActivityMainViewPt.findViewById( R.id.UseVideoTalkbackRadioBtn ) ).isChecked() ) ? 1 :
                                    ( ( ( RadioButton ) m_LyotActivityMainViewPt.findViewById( R.id.UseAudioVideoTalkbackRadioBtn ) ).isChecked() ) ? 1 : 0,
                            ( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseVideoSamplingRate12RadioBtn ) ).isChecked() ) ? 12 :
                                    ( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseVideoSamplingRate16RadioBtn ) ).isChecked() ) ? 16 :
                                            ( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseVideoSamplingRate24RadioBtn ) ).isChecked() ) ? 24 :
                                                    ( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseVideoSamplingRate30RadioBtn ) ).isChecked() ) ? 30 : 0,
                            ( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseVideoFrameSize144_176RadioBtn ) ).isChecked() ) ? 144 :
                                    ( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseVideoFrameSize240_320RadioBtn ) ).isChecked() ) ? 240 :
                                            ( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseVideoFrameSize480_640RadioBtn ) ).isChecked() ) ? 480 :
                                                    ( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseVideoFrameSize960_1280RadioBtn ) ).isChecked() ) ? 960 : 0,
                            ( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseVideoFrameSize144_176RadioBtn ) ).isChecked() ) ? 176 :
                                    ( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseVideoFrameSize240_320RadioBtn ) ).isChecked() ) ? 320 :
                                            ( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseVideoFrameSize480_640RadioBtn ) ).isChecked() ) ? 640 :
                                                    ( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseVideoFrameSize960_1280RadioBtn ) ).isChecked() ) ? 1280 : 0,
                            m_VideoInputPreviewSurfaceViewPt
                    );

                    //判断video enter 是否 use YU12 Raw data 。
                    if( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseYU12RadioBtn ) ).isChecked() )
                    {
                        m_MyMediaProcThreadPt.SetVideoInputUseYU12();
                    }

                    //判断video enter 是否 use OpenH264 Encoder 。
                    if( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseOpenH264CodecRadioBtn ) ).isChecked() )
                    {
                        m_MyMediaProcThreadPt.SetVideoInputUseOpenH264Encoder(
                                Integer.parseInt( ( ( TextView ) m_LyotActivityOpenH264CodecViewPt.findViewById( R.id.OpenH264EncoderVideoTypeEdit ) ).getText().toString() ),
                                Integer.parseInt( ( ( TextView ) m_LyotActivityOpenH264CodecViewPt.findViewById( R.id.OpenH264EncoderEncodedBitrateEdit ) ).getText().toString() ) * 1024 * 8,
                                Integer.parseInt( ( ( TextView ) m_LyotActivityOpenH264CodecViewPt.findViewById( R.id.OpenH264EncoderBitrateControlModeEdit ) ).getText().toString() ),
                                Integer.parseInt( ( ( TextView ) m_LyotActivityOpenH264CodecViewPt.findViewById( R.id.OpenH264EncoderIDRFrameIntvlEdit ) ).getText().toString() ),
                                Integer.parseInt( ( ( TextView ) m_LyotActivityOpenH264CodecViewPt.findViewById( R.id.OpenH264EncoderComplexityEdit ) ).getText().toString() )
                        );
                    }

                    //判断 use 的videoinput device。
                    m_MyMediaProcThreadPt.SetVideoInputUseDevice( ( ( ( RadioButton ) m_LyotActivityMainViewPt.findViewById( R.id.UseFrontCamereRadioBtn ) ).isChecked() ) ? 0 : 1 );

                    //判断videoinput device是否 Black screen。
                    m_MyMediaProcThreadPt.SetVideoInputDeviceIsBlack( ( ( ( CheckBox ) m_LyotActivityMainViewPt.findViewById( R.id.VideoInputDeviceIsBlackCheckBox ) ).isChecked() ) ? 1 : 0 );

                    //判断是否 use video Output 。
                    m_MyMediaProcThreadPt.SetIsUseVideoOutput(
                            ( ( ( RadioButton ) m_LyotActivityMainViewPt.findViewById( R.id.UseVideoTalkbackRadioBtn ) ).isChecked() ) ? 1 :
                                    ( ( ( RadioButton ) m_LyotActivityMainViewPt.findViewById( R.id.UseAudioVideoTalkbackRadioBtn ) ).isChecked() ) ? 1 : 0,
                            ( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseVideoFrameSize144_176RadioBtn ) ).isChecked() ) ? 144 :
                                    ( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseVideoFrameSize240_320RadioBtn ) ).isChecked() ) ? 240 :
                                            ( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseVideoFrameSize480_640RadioBtn ) ).isChecked() ) ? 480 :
                                                    ( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseVideoFrameSize960_1280RadioBtn ) ).isChecked() ) ? 960 : 0,
                            ( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseVideoFrameSize144_176RadioBtn ) ).isChecked() ) ? 176 :
                                    ( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseVideoFrameSize240_320RadioBtn ) ).isChecked() ) ? 320 :
                                            ( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseVideoFrameSize480_640RadioBtn ) ).isChecked() ) ? 640 :
                                                    ( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseVideoFrameSize960_1280RadioBtn ) ).isChecked() ) ? 1280 : 0,
                            m_VideoOutputDisplaySurfaceViewPt,
                            ( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseDisplayScale1_0RadioBtn ) ).isChecked() ) ? 1.0f :
                                    ( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseDisplayScale1_5RadioBtn ) ).isChecked() ) ? 1.5f :
                                            ( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseDisplayScale2_0RadioBtn ) ).isChecked() ) ? 2.0f :
                                                    ( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseDisplayScale3_0RadioBtn ) ).isChecked() ) ? 3.0f : 1.0f
                    );

                    //判断video Output 是否 use YU12 Raw data 。
                    if( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseYU12RadioBtn ) ).isChecked() )
                    {
                        m_MyMediaProcThreadPt.SetVideoOutputUseYU12();
                    }

                    //判断video Output 是否 use OpenH264 Encoder 。
                    if( ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseOpenH264CodecRadioBtn ) ).isChecked() )
                    {
                        m_MyMediaProcThreadPt.SetVideoOutputUseOpenH264Decoder( 0 );
                    }

                    //判断video Output device 是否 Black screen。
                    m_MyMediaProcThreadPt.SetVideoOutputDeviceIsBlack( ( ( ( CheckBox ) m_LyotActivityMainViewPt.findViewById( R.id.VideoOutputDeviceIsBlackCheckBox ) ).isChecked() ) ? 1 : 0 );
                }

                m_MyMediaProcThreadPt.start(); //启动媒体处理线程。

                Log.i( m_CurClsNameStrPt, "Finished starting the media processing thread." );
            }
            else
            {
                Log.i( m_CurClsNameStrPt, "Start the request and wait for the media processing thread to exit." );
                m_MyMediaProcThreadPt.RequireExit( 1, 1 );
                Log.i( m_CurClsNameStrPt, "End the request and wait for the media processing thread to exit." );
            }

            p_Result = 0;

            break out;
        }

        if( p_Result != 0 ) //如果媒体处理线程启动失败。
        {
            m_MyMediaProcThreadPt = null;
        }
    }

    //主界面video enter 预览SurfaceView按钮。
    public void onClickVideoSurfaceView( View BtnPt )
    {
        if( ( ( LinearLayout )BtnPt.getParent() ).getOrientation() == LinearLayout.HORIZONTAL )
        {
            ( ( LinearLayout )BtnPt.getParent() ).setOrientation( LinearLayout.VERTICAL );
        }
        else
        {
            ( ( LinearLayout )BtnPt.getParent() ).setOrientation( LinearLayout.HORIZONTAL );
        }
    }

    //主界面Настраивать按钮。
    public void OnClickSetting( View BtnPt )
    {
        setContentView( m_LyotActivitySettingViewPt );
        m_LyotActivityCurViewPt = m_LyotActivitySettingViewPt;
    }

    //主界面EmptyLog按钮。
    public void OnClickClearLog( View BtnPt )
    {
        ( ( LinearLayout ) m_LyotActivityMainViewPt.findViewById( R.id.LogLinearLyot ) ).removeAllViews();
    }

    //主界面RTFM按钮。
    public void OnClickReadMe( View BtnPt )
    {
        startActivity( new Intent( Intent.ACTION_VIEW, Uri.parse( "https://github.com/cyz7758520/Android_audio_talkback_demo_program" ) ) );
    }

    //Настраивать界面的 determine 按钮。
    public void OnClickSettingOk( View BtnPt )
    {
        setContentView( m_LyotActivityMainViewPt );
        m_LyotActivityCurViewPt = m_LyotActivityMainViewPt;
    }

    //Speex Acoustic echo sound Eliminator Настраивать按钮。
    public void OnClickSpeexAecSetting( View BtnPt )
    {
        setContentView( m_LyotActivitySpeexAecViewPt );
        m_LyotActivityCurViewPt = m_LyotActivitySpeexAecViewPt;
    }

    //Speex Acoustic echo sound Eliminator Настраивать界面的 Delete memory block file 按钮。
    public void OnClickSpeexAecDelMemFile( View BtnPt )
    {
        String p_pclSpeexAecMemoryFullPath = m_ExternalDirFullAbsPathStrPt + "/SpeexAecMemory";
        File file = new File( p_pclSpeexAecMemoryFullPath );
        if( file.exists() )
        {
            if( file.delete() )
            {
                Toast.makeText( this, " delete Speex Acoustic echo sound Eliminator Memory block file " + p_pclSpeexAecMemoryFullPath + " success.", Toast.LENGTH_LONG ).show();
            }
            else
            {
                Toast.makeText( this, " delete Speex Acoustic echo sound Eliminator Memory block file " + p_pclSpeexAecMemoryFullPath + " 失败。", Toast.LENGTH_LONG ).show();
            }
        }
        else
        {
            Toast.makeText( this, "Speex Acoustic echo sound Eliminator Memory block file " + p_pclSpeexAecMemoryFullPath + " 不存 в 。", Toast.LENGTH_LONG ).show();
        }
    }

    //Speex Acoustic echo sound Eliminator Настраивать界面的 determine 按钮。
    public void OnClickSpeexAecSettingOk( View BtnPt )
    {
        setContentView( m_LyotActivitySettingViewPt );
        m_LyotActivityCurViewPt = m_LyotActivitySettingViewPt;
    }

    //WebRtc Fixed-point version  Acoustic echo sound Eliminator Настраивать按钮。
    public void OnClickWebRtcAecmSetting( View BtnPt )
    {
        setContentView( m_LyotActivityWebRtcAecmViewPt );
        m_LyotActivityCurViewPt = m_LyotActivityWebRtcAecmViewPt;
    }

    //WebRtc Fixed-point version  Acoustic echo sound Eliminator Настраивать界面的 determine 按钮。
    public void OnClickWebRtcAecmSettingOk( View BtnPt )
    {
        setContentView( m_LyotActivitySettingViewPt );
        m_LyotActivityCurViewPt = m_LyotActivitySettingViewPt;
    }

    //WebRtc Floating point version  Acoustic echo sound Eliminator Настраивать按钮。
    public void OnClickWebRtcAecSetting( View BtnPt )
    {
        setContentView( m_LyotActivityWebRtcAecViewPt );
        m_LyotActivityCurViewPt = m_LyotActivityWebRtcAecViewPt;
    }

    //WebRtc Floating point version  Acoustic echo sound Eliminator Настраивать界面的 Delete memory block file 按钮。
    public void OnClickWebRtcAecDelMemFile( View BtnPt )
    {
        String p_pclWebRtcAecMemoryFullPath = m_ExternalDirFullAbsPathStrPt + "/WebRtcAecMemory";
        File file = new File( p_pclWebRtcAecMemoryFullPath );
        if( file.exists() )
        {
            if( file.delete() )
            {
                Toast.makeText( this, " delete WebRtc Floating point version  Acoustic echo sound Eliminator Memory block file " + p_pclWebRtcAecMemoryFullPath + " success.", Toast.LENGTH_LONG ).show();
            }
            else
            {
                Toast.makeText( this, " delete WebRtc Floating point version  Acoustic echo sound Eliminator Memory block file " + p_pclWebRtcAecMemoryFullPath + " 失败。", Toast.LENGTH_LONG ).show();
            }
        }
        else
        {
            Toast.makeText( this, "WebRtc Floating point version  Acoustic echo sound Eliminator Memory block file " + p_pclWebRtcAecMemoryFullPath + " 不存 в 。", Toast.LENGTH_LONG ).show();
        }
    }

    //WebRtc Floating point version  Acoustic echo sound Eliminator Настраивать界面的 determine 按钮。
    public void OnClickWebRtcAecSettingOk( View BtnPt )
    {
        setContentView( m_LyotActivitySettingViewPt );
        m_LyotActivityCurViewPt = m_LyotActivitySettingViewPt;
    }

    //SpeexWebRtc triple  Acoustic echo sound Eliminator Настраивать按钮。
    public void OnClickSpeexWebRtcAecSetting( View BtnPt )
    {
        setContentView( m_LyotActivitySpeexWebRtcAecViewPt );
        m_LyotActivityCurViewPt = m_LyotActivitySpeexWebRtcAecViewPt;
    }

    //SpeexWebRtc triple  Acoustic echo sound Eliminator Настраивать界面的 determine 按钮。
    public void OnClickSpeexWebRtcAecSettingOk( View BtnPt )
    {
        setContentView( m_LyotActivitySettingViewPt );
        m_LyotActivityCurViewPt = m_LyotActivitySettingViewPt;
    }

    //Speex Preprocessor  noise sound торможение Настраивать按钮。
    public void OnClickSpeexPprocNsSetting( View BtnPt )
    {
        setContentView( m_LyotActivitySpeexPprocNsViewPt );
        m_LyotActivityCurViewPt = m_LyotActivitySpeexPprocNsViewPt;
    }

    //Speex Preprocessor  noise sound торможение Настраивать界面的 determine 按钮。
    public void OnClickSpeexPprocNsSettingOk( View BtnPt )
    {
        setContentView( m_LyotActivitySettingViewPt );
        m_LyotActivityCurViewPt = m_LyotActivitySettingViewPt;
    }

    //WebRtc Fixed-point version  noise sound Suppressor Настраивать按钮。
    public void OnClickWebRtcNsxSetting( View BtnPt )
    {
        setContentView( m_LyotActivityWebRtcNsxViewPt );
        m_LyotActivityCurViewPt = m_LyotActivityWebRtcNsxViewPt;
    }

    //WebRtc Fixed-point version  noise sound Suppressor Настраивать界面的 determine 按钮。
    public void OnClickWebRtcNsxSettingOk( View BtnPt )
    {
        setContentView( m_LyotActivitySettingViewPt );
        m_LyotActivityCurViewPt = m_LyotActivitySettingViewPt;
    }

    //WebRtc Floating point version  noise sound Suppressor Настраивать按钮。
    public void OnClickWebRtcNsSetting( View BtnPt )
    {
        setContentView( m_LyotActivityWebRtcNsViewPt );
        m_LyotActivityCurViewPt = m_LyotActivityWebRtcNsViewPt;
    }

    //WebRtc Floating point version  noise sound Suppressor Настраивать界面的 determine 按钮。
    public void OnClickWebRtcNsSettingOk( View BtnPt )
    {
        setContentView( m_LyotActivitySettingViewPt );
        m_LyotActivityCurViewPt = m_LyotActivitySettingViewPt;
    }

    //Speex Preprocessor  Other functions Настраивать按钮。
    public void OnClickSpeexPprocOtherSetting( View BtnPt )
    {
        setContentView( m_LyotActivitySpeexPprocOtherViewPt );
        m_LyotActivityCurViewPt = m_LyotActivitySpeexPprocOtherViewPt;
    }

    //Speex Preprocessor  Other functions Настраивать界面的 determine 按钮。
    public void OnClickSpeexPprocOtherSettingOk( View BtnPt )
    {
        setContentView( m_LyotActivitySettingViewPt );
        m_LyotActivityCurViewPt = m_LyotActivitySettingViewPt;
    }

    //Speex Codec Настраивать按钮。
    public void OnClickSpeexCodecSetting( View BtnPt )
    {
        setContentView( m_LyotActivitySpeexCodecViewPt );
        m_LyotActivityCurViewPt = m_LyotActivitySpeexCodecViewPt;
    }

    //Speex Codec Настраивать界面的 determine 按钮。
    public void OnClickSpeexCodecSettingOk( View BtnPt )
    {
        setContentView( m_LyotActivitySettingViewPt );
        m_LyotActivityCurViewPt = m_LyotActivitySettingViewPt;
    }

    //Opus Codec Настраивать按钮。
    public void OnClickOpusCodecSetting( View BtnPt )
    {
        setContentView( m_LyotActivitySpeexCodecViewPt );
        m_LyotActivityCurViewPt = m_LyotActivitySpeexCodecViewPt;
    }

    //Opus Codec Настраивать界面的 determine 按钮。
    public void OnOpusCodecSettingOkClick( View BtnPt )
    {
        setContentView( m_LyotActivitySettingViewPt );
        m_LyotActivityCurViewPt = m_LyotActivitySettingViewPt;
    }

    //OpenH264 Codec Настраивать按钮。
    public void OnClickOpenH264CodecSetting( View BtnPt )
    {
        setContentView( m_LyotActivityOpenH264CodecViewPt );
        m_LyotActivityCurViewPt = m_LyotActivityOpenH264CodecViewPt;
    }

    //Opus Codec Настраивать界面的 determine 按钮。
    public void OnOpenH264CodecSettingOkClick( View BtnPt )
    {
        setContentView( m_LyotActivitySettingViewPt );
        m_LyotActivityCurViewPt = m_LyotActivitySettingViewPt;
    }

    // Audio Adaptive jitter bufferНастраивать按钮。
    public void OnClickAjbSetting( View BtnPt )
    {
        setContentView( m_LyotActivityAjbViewPt );
        m_LyotActivityCurViewPt = m_LyotActivityAjbViewPt;
    }

    // Audio Adaptive jitter bufferНастраивать界面的 determine 按钮。
    public void OnClickAjbSettingOk( View BtnPt )
    {
        setContentView( m_LyotActivitySettingViewPt );
        m_LyotActivityCurViewPt = m_LyotActivitySettingViewPt;
    }

    // Effect level ： low 。
    public void OnClickUseEffectLowRadioBtn( View BtnPt )
    {
        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseEffectLowRadioBtn ) ).setChecked( true );

        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseAudioSamplingRate8000RadioBtn ) ).setChecked( true );
        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseAudioFrameLen20msRadioBtn ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivitySettingViewPt.findViewById( R.id.IsUseSystemAecNsAgcCheckBox ) ).setChecked( true );

        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseWebRtcAecmRadioBtn ) ).setChecked( true );

        ( ( TextView ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecFilterLenEdit ) ).setText( "500" );
        ( ( CheckBox ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecIsUseRecCheckBox ) ).setChecked( true );
        ( ( TextView ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecEchoMultipleEdit ) ).setText( "3.0" );
        ( ( TextView ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecEchoContEdit ) ).setText( "0.65" );
        ( ( TextView ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecEchoSupesEdit ) ).setText( "-32768" );
        ( ( TextView ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecEchoSupesActEdit ) ).setText( "-32768" );
        ( ( CheckBox ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecIsSaveMemFileCheckBox ) ).setChecked( false );

        ( ( CheckBox ) m_LyotActivityWebRtcAecmViewPt.findViewById( R.id.WebRtcAecmIsUseCNGModeCheckBox ) ).setChecked( false );
        ( ( TextView ) m_LyotActivityWebRtcAecmViewPt.findViewById( R.id.WebRtcAecmEchoModeEdit ) ).setText( "4" );
        ( ( TextView ) m_LyotActivityWebRtcAecmViewPt.findViewById( R.id.WebRtcAecmDelayEdit ) ).setText( "0" );

        ( ( TextView ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecEchoModeEdit ) ).setText( "2" );
        ( ( TextView ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecDelayEdit ) ).setText( "0" );
        ( ( CheckBox ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecIsUseDelayAgnosticModeCheckBox ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecIsUseExtdFilterModeCheckBox ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecIsUseRefinedFilterAdaptAecModeCheckBox ) ).setChecked( false );
        ( ( CheckBox ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecIsUseAdaptAdjDelayCheckBox ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecIsSaveMemFileCheckBox ) ).setChecked( false );

        ( ( RadioButton ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWorkModeSpeexAecWebRtcAecmRadioBtn ) ).setChecked( true );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSpeexAecFilterLenEdit ) ).setText( "500" );
        ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSpeexAecIsUseRecCheckBox ) ).setChecked( true );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSpeexAecEchoMultipleEdit ) ).setText( "1.0" );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSpeexAecEchoContEdit ) ).setText( "0.6" );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSpeexAecEchoSupesEdit ) ).setText( "-32768" );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSpeexAecEchoSupesActEdit ) ).setText( "-32768" );
        ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecmIsUseCNGModeCheckBox ) ).setChecked( false );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecmEchoModeEdit ) ).setText( "4" );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecmDelayEdit ) ).setText( "0" );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecEchoModeEdit ) ).setText( "2" );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecDelayEdit ) ).setText( "0" );
        ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecIsUseDelayAgnosticModeCheckBox ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecIsUseExtdFilterModeCheckBox ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecIsUseRefinedFilterAdaptAecModeCheckBox ) ).setChecked( false );
        ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecIsUseAdaptAdjDelayCheckBox ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecIsUseSameRoomAecCheckBox ) ).setChecked( false );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSameRoomEchoMinDelayEdit ) ).setText( "420" );

        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseSpeexPprocNsRadioBtn ) ).setChecked( true );

        ( ( CheckBox ) m_LyotActivitySpeexPprocNsViewPt.findViewById( R.id.SpeexPprocIsUseNsCheckBox ) ).setChecked( true );
        ( ( TextView ) m_LyotActivitySpeexPprocNsViewPt.findViewById( R.id.SpeexPprocNoiseSupesEdit ) ).setText( "-32768" );
        ( ( CheckBox ) m_LyotActivitySpeexPprocNsViewPt.findViewById( R.id.SpeexPprocIsUseDereverbCheckBox ) ).setChecked( true );

        ( ( TextView ) m_LyotActivityWebRtcNsxViewPt.findViewById( R.id.WebRtcNsxPolicyModeEdit ) ).setText( "3" );

        ( ( TextView ) m_LyotActivityWebRtcNsViewPt.findViewById( R.id.WebRtcNsPolicyModeEdit ) ).setText( "3" );

        ( ( CheckBox ) m_LyotActivitySettingViewPt.findViewById( R.id.IsUseSpeexPprocOtherCheckBox ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocIsUseVadCheckBox ) ).setChecked( true );
        ( ( TextView ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocVadProbStartEdit ) ).setText( "95" );
        ( ( TextView ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocVadProbContEdit ) ).setText( "95" );
        ( ( CheckBox ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocIsUseAgcCheckBox ) ).setChecked( false );
        ( ( TextView ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocAgcLevelEdit ) ).setText( "30000" );
        ( ( TextView ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocAgcIncrementEdit ) ).setText( "10" );
        ( ( TextView ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocAgcDecrementEdit ) ).setText( "-30000" );
        ( ( TextView ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocAgcMaxGainEdit ) ).setText( "25" );

        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseSpeexCodecRadioBtn ) ).setChecked( true );

        ( ( RadioButton ) m_LyotActivitySpeexCodecViewPt.findViewById( R.id.SpeexCodecEncoderUseCbrRadioBtn ) ).setChecked( true );
        ( ( TextView ) m_LyotActivitySpeexCodecViewPt.findViewById( R.id.SpeexCodecEncoderComplexityEdit ) ).setText( "1" );
        ( ( CheckBox ) m_LyotActivitySpeexCodecViewPt.findViewById( R.id.SpeexCodecIsUsePerceptualEnhancementCheckBox ) ).setChecked( true );

        ( ( CheckBox ) m_LyotActivitySettingViewPt.findViewById( R.id.IsSaveAudioToFileCheckBox ) ).setChecked( false );

        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseVideoSamplingRate12RadioBtn ) ).setChecked( true );
        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseVideoFrameSize144_176RadioBtn ) ).setChecked( true );
        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseDisplayScale1_0RadioBtn ) ).setChecked( true );
        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseOpenH264CodecRadioBtn ) ).setChecked( true );

        ( ( TextView ) m_LyotActivityOpenH264CodecViewPt.findViewById( R.id.OpenH264EncoderVideoTypeEdit ) ).setText( "0" );
        ( ( TextView ) m_LyotActivityOpenH264CodecViewPt.findViewById( R.id.OpenH264EncoderBitrateControlModeEdit ) ).setText( "3" );
        ( ( TextView ) m_LyotActivityOpenH264CodecViewPt.findViewById( R.id.OpenH264EncoderIDRFrameIntvlEdit ) ).setText( "12" );
        ( ( TextView ) m_LyotActivityOpenH264CodecViewPt.findViewById( R.id.OpenH264EncoderComplexityEdit ) ).setText( "0" );
    }

    // Effect level ：in。
    public void OnClickUseEffectMidRadioBtn( View BtnPt )
    {
        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseEffectMidRadioBtn ) ).setChecked( true );

        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseAudioSamplingRate16000RadioBtn ) ).setChecked( true );
        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseAudioFrameLen20msRadioBtn ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivitySettingViewPt.findViewById( R.id.IsUseSystemAecNsAgcCheckBox ) ).setChecked( true );

        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseWebRtcAecRadioBtn ) ).setChecked( true );

        ( ( TextView ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecFilterLenEdit ) ).setText( "500" );
        ( ( CheckBox ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecIsUseRecCheckBox ) ).setChecked( true );
        ( ( TextView ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecEchoMultipleEdit ) ).setText( "3.0" );
        ( ( TextView ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecEchoContEdit ) ).setText( "0.65" );
        ( ( TextView ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecEchoSupesEdit ) ).setText( "-32768" );
        ( ( TextView ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecEchoSupesActEdit ) ).setText( "-32768" );
        ( ( CheckBox ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecIsSaveMemFileCheckBox ) ).setChecked( false );

        ( ( CheckBox ) m_LyotActivityWebRtcAecmViewPt.findViewById( R.id.WebRtcAecmIsUseCNGModeCheckBox ) ).setChecked( false );
        ( ( TextView ) m_LyotActivityWebRtcAecmViewPt.findViewById( R.id.WebRtcAecmEchoModeEdit ) ).setText( "4" );
        ( ( TextView ) m_LyotActivityWebRtcAecmViewPt.findViewById( R.id.WebRtcAecmDelayEdit ) ).setText( "0" );

        ( ( TextView ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecEchoModeEdit ) ).setText( "2" );
        ( ( TextView ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecDelayEdit ) ).setText( "0" );
        ( ( CheckBox ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecIsUseDelayAgnosticModeCheckBox ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecIsUseExtdFilterModeCheckBox ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecIsUseRefinedFilterAdaptAecModeCheckBox ) ).setChecked( false );
        ( ( CheckBox ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecIsUseAdaptAdjDelayCheckBox ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecIsSaveMemFileCheckBox ) ).setChecked( false );

        ( ( RadioButton ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWorkModeWebRtcAecmWebRtcAecRadioBtn ) ).setChecked( true );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSpeexAecFilterLenEdit ) ).setText( "500" );
        ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSpeexAecIsUseRecCheckBox ) ).setChecked( true );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSpeexAecEchoMultipleEdit ) ).setText( "1.0" );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSpeexAecEchoContEdit ) ).setText( "0.6" );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSpeexAecEchoSupesEdit ) ).setText( "-32768" );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSpeexAecEchoSupesActEdit ) ).setText( "-32768" );
        ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecmIsUseCNGModeCheckBox ) ).setChecked( false );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecmEchoModeEdit ) ).setText( "4" );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecmDelayEdit ) ).setText( "0" );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecEchoModeEdit ) ).setText( "2" );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecDelayEdit ) ).setText( "0" );
        ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecIsUseDelayAgnosticModeCheckBox ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecIsUseExtdFilterModeCheckBox ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecIsUseRefinedFilterAdaptAecModeCheckBox ) ).setChecked( false );
        ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecIsUseAdaptAdjDelayCheckBox ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecIsUseSameRoomAecCheckBox ) ).setChecked( false );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSameRoomEchoMinDelayEdit ) ).setText( "420" );

        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseWebRtcNsxRadioBtn ) ).setChecked( true );

        ( ( CheckBox ) m_LyotActivitySpeexPprocNsViewPt.findViewById( R.id.SpeexPprocIsUseNsCheckBox ) ).setChecked( true );
        ( ( TextView ) m_LyotActivitySpeexPprocNsViewPt.findViewById( R.id.SpeexPprocNoiseSupesEdit ) ).setText( "-32768" );
        ( ( CheckBox ) m_LyotActivitySpeexPprocNsViewPt.findViewById( R.id.SpeexPprocIsUseDereverbCheckBox ) ).setChecked( true );

        ( ( TextView ) m_LyotActivityWebRtcNsxViewPt.findViewById( R.id.WebRtcNsxPolicyModeEdit ) ).setText( "3" );

        ( ( TextView ) m_LyotActivityWebRtcNsViewPt.findViewById( R.id.WebRtcNsPolicyModeEdit ) ).setText( "3" );

        ( ( CheckBox ) m_LyotActivitySettingViewPt.findViewById( R.id.IsUseSpeexPprocOtherCheckBox ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocIsUseVadCheckBox ) ).setChecked( true );
        ( ( TextView ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocVadProbStartEdit ) ).setText( "95" );
        ( ( TextView ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocVadProbContEdit ) ).setText( "95" );
        ( ( CheckBox ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocIsUseAgcCheckBox ) ).setChecked( true );
        ( ( TextView ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocAgcLevelEdit ) ).setText( "30000" );
        ( ( TextView ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocAgcIncrementEdit ) ).setText( "10" );
        ( ( TextView ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocAgcDecrementEdit ) ).setText( "-30000" );
        ( ( TextView ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocAgcMaxGainEdit ) ).setText( "25" );

        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseSpeexCodecRadioBtn ) ).setChecked( true );

        ( ( RadioButton ) m_LyotActivitySpeexCodecViewPt.findViewById( R.id.SpeexCodecEncoderUseCbrRadioBtn ) ).setChecked( true );
        ( ( TextView ) m_LyotActivitySpeexCodecViewPt.findViewById( R.id.SpeexCodecEncoderComplexityEdit ) ).setText( "4" );
        ( ( CheckBox ) m_LyotActivitySpeexCodecViewPt.findViewById( R.id.SpeexCodecIsUsePerceptualEnhancementCheckBox ) ).setChecked( true );

        ( ( CheckBox ) m_LyotActivitySettingViewPt.findViewById( R.id.IsSaveAudioToFileCheckBox ) ).setChecked( false );

        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseVideoSamplingRate16RadioBtn ) ).setChecked( true );
        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseVideoFrameSize240_320RadioBtn ) ).setChecked( true );
        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseDisplayScale1_0RadioBtn ) ).setChecked( true );
        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseOpenH264CodecRadioBtn ) ).setChecked( true );

        ( ( TextView ) m_LyotActivityOpenH264CodecViewPt.findViewById( R.id.OpenH264EncoderVideoTypeEdit ) ).setText( "0" );
        ( ( TextView ) m_LyotActivityOpenH264CodecViewPt.findViewById( R.id.OpenH264EncoderBitrateControlModeEdit ) ).setText( "3" );
        ( ( TextView ) m_LyotActivityOpenH264CodecViewPt.findViewById( R.id.OpenH264EncoderIDRFrameIntvlEdit ) ).setText( "16" );
        ( ( TextView ) m_LyotActivityOpenH264CodecViewPt.findViewById( R.id.OpenH264EncoderComplexityEdit ) ).setText( "0" );
    }

    // Effect level ：high。
    public void OnClickUseEffectHighRadioBtn( View BtnPt )
    {
        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseEffectHighRadioBtn ) ).setChecked( true );

        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseAudioSamplingRate16000RadioBtn ) ).setChecked( true );
        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseAudioFrameLen20msRadioBtn ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivitySettingViewPt.findViewById( R.id.IsUseSystemAecNsAgcCheckBox ) ).setChecked( true );

        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseSpeexWebRtcAecRadioBtn ) ).setChecked( true );

        ( ( TextView ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecFilterLenEdit ) ).setText( "500" );
        ( ( CheckBox ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecIsUseRecCheckBox ) ).setChecked( true );
        ( ( TextView ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecEchoMultipleEdit ) ).setText( "3.0" );
        ( ( TextView ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecEchoContEdit ) ).setText( "0.65" );
        ( ( TextView ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecEchoSupesEdit ) ).setText( "-32768" );
        ( ( TextView ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecEchoSupesActEdit ) ).setText( "-32768" );
        ( ( CheckBox ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecIsSaveMemFileCheckBox ) ).setChecked( false );

        ( ( CheckBox ) m_LyotActivityWebRtcAecmViewPt.findViewById( R.id.WebRtcAecmIsUseCNGModeCheckBox ) ).setChecked( false );
        ( ( TextView ) m_LyotActivityWebRtcAecmViewPt.findViewById( R.id.WebRtcAecmEchoModeEdit ) ).setText( "4" );
        ( ( TextView ) m_LyotActivityWebRtcAecmViewPt.findViewById( R.id.WebRtcAecmDelayEdit ) ).setText( "0" );

        ( ( TextView ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecEchoModeEdit ) ).setText( "2" );
        ( ( TextView ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecDelayEdit ) ).setText( "0" );
        ( ( CheckBox ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecIsUseDelayAgnosticModeCheckBox ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecIsUseExtdFilterModeCheckBox ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecIsUseRefinedFilterAdaptAecModeCheckBox ) ).setChecked( false );
        ( ( CheckBox ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecIsUseAdaptAdjDelayCheckBox ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecIsSaveMemFileCheckBox ) ).setChecked( false );

        ( ( RadioButton ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWorkModeSpeexAecWebRtcAecmWebRtcAecRadioBtn ) ).setChecked( true );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSpeexAecFilterLenEdit ) ).setText( "500" );
        ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSpeexAecIsUseRecCheckBox ) ).setChecked( true );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSpeexAecEchoMultipleEdit ) ).setText( "1.0" );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSpeexAecEchoContEdit ) ).setText( "0.6" );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSpeexAecEchoSupesEdit ) ).setText( "-32768" );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSpeexAecEchoSupesActEdit ) ).setText( "-32768" );
        ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecmIsUseCNGModeCheckBox ) ).setChecked( false );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecmEchoModeEdit ) ).setText( "4" );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecmDelayEdit ) ).setText( "0" );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecEchoModeEdit ) ).setText( "2" );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecDelayEdit ) ).setText( "0" );
        ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecIsUseDelayAgnosticModeCheckBox ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecIsUseExtdFilterModeCheckBox ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecIsUseRefinedFilterAdaptAecModeCheckBox ) ).setChecked( false );
        ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecIsUseAdaptAdjDelayCheckBox ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecIsUseSameRoomAecCheckBox ) ).setChecked( false );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSameRoomEchoMinDelayEdit ) ).setText( "420" );

        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseWebRtcNsRadioBtn ) ).setChecked( true );

        ( ( CheckBox ) m_LyotActivitySpeexPprocNsViewPt.findViewById( R.id.SpeexPprocIsUseNsCheckBox ) ).setChecked( true );
        ( ( TextView ) m_LyotActivitySpeexPprocNsViewPt.findViewById( R.id.SpeexPprocNoiseSupesEdit ) ).setText( "-32768" );
        ( ( CheckBox ) m_LyotActivitySpeexPprocNsViewPt.findViewById( R.id.SpeexPprocIsUseDereverbCheckBox ) ).setChecked( true );

        ( ( TextView ) m_LyotActivityWebRtcNsxViewPt.findViewById( R.id.WebRtcNsxPolicyModeEdit ) ).setText( "3" );

        ( ( TextView ) m_LyotActivityWebRtcNsViewPt.findViewById( R.id.WebRtcNsPolicyModeEdit ) ).setText( "3" );

        ( ( CheckBox ) m_LyotActivitySettingViewPt.findViewById( R.id.IsUseSpeexPprocOtherCheckBox ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocIsUseVadCheckBox ) ).setChecked( true );
        ( ( TextView ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocVadProbStartEdit ) ).setText( "95" );
        ( ( TextView ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocVadProbContEdit ) ).setText( "95" );
        ( ( CheckBox ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocIsUseAgcCheckBox ) ).setChecked( true );
        ( ( TextView ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocAgcLevelEdit ) ).setText( "30000" );
        ( ( TextView ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocAgcIncrementEdit ) ).setText( "10" );
        ( ( TextView ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocAgcDecrementEdit ) ).setText( "-30000" );
        ( ( TextView ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocAgcMaxGainEdit ) ).setText( "25" );

        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseSpeexCodecRadioBtn ) ).setChecked( true );

        ( ( RadioButton ) m_LyotActivitySpeexCodecViewPt.findViewById( R.id.SpeexCodecEncoderUseVbrRadioBtn ) ).setChecked( true );
        ( ( TextView ) m_LyotActivitySpeexCodecViewPt.findViewById( R.id.SpeexCodecEncoderComplexityEdit ) ).setText( "8" );
        ( ( CheckBox ) m_LyotActivitySpeexCodecViewPt.findViewById( R.id.SpeexCodecIsUsePerceptualEnhancementCheckBox ) ).setChecked( true );

        ( ( CheckBox ) m_LyotActivitySettingViewPt.findViewById( R.id.IsSaveAudioToFileCheckBox ) ).setChecked( false );

        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseVideoSamplingRate16RadioBtn ) ).setChecked( true );
        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseVideoFrameSize480_640RadioBtn ) ).setChecked( true );
        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseDisplayScale1_0RadioBtn ) ).setChecked( true );
        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseOpenH264CodecRadioBtn ) ).setChecked( true );

        ( ( TextView ) m_LyotActivityOpenH264CodecViewPt.findViewById( R.id.OpenH264EncoderVideoTypeEdit ) ).setText( "0" );
        ( ( TextView ) m_LyotActivityOpenH264CodecViewPt.findViewById( R.id.OpenH264EncoderBitrateControlModeEdit ) ).setText( "3" );
        ( ( TextView ) m_LyotActivityOpenH264CodecViewPt.findViewById( R.id.OpenH264EncoderIDRFrameIntvlEdit ) ).setText( "16" );
        ( ( TextView ) m_LyotActivityOpenH264CodecViewPt.findViewById( R.id.OpenH264EncoderComplexityEdit ) ).setText( "0" );
    }

    // Effect level ：ultra。
    public void OnClickUseEffectSuperRadioBtn( View BtnPt )
    {
        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseEffectSuperRadioBtn ) ).setChecked( true );

        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseAudioSamplingRate16000RadioBtn ) ).setChecked( true );
        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseAudioFrameLen20msRadioBtn ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivitySettingViewPt.findViewById( R.id.IsUseSystemAecNsAgcCheckBox ) ).setChecked( true );

        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseSpeexWebRtcAecRadioBtn ) ).setChecked( true );

        ( ( TextView ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecFilterLenEdit ) ).setText( "500" );
        ( ( CheckBox ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecIsUseRecCheckBox ) ).setChecked( true );
        ( ( TextView ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecEchoMultipleEdit ) ).setText( "3.0" );
        ( ( TextView ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecEchoContEdit ) ).setText( "0.65" );
        ( ( TextView ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecEchoSupesEdit ) ).setText( "-32768" );
        ( ( TextView ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecEchoSupesActEdit ) ).setText( "-32768" );
        ( ( CheckBox ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecIsSaveMemFileCheckBox ) ).setChecked( false );

        ( ( CheckBox ) m_LyotActivityWebRtcAecmViewPt.findViewById( R.id.WebRtcAecmIsUseCNGModeCheckBox ) ).setChecked( false );
        ( ( TextView ) m_LyotActivityWebRtcAecmViewPt.findViewById( R.id.WebRtcAecmEchoModeEdit ) ).setText( "4" );
        ( ( TextView ) m_LyotActivityWebRtcAecmViewPt.findViewById( R.id.WebRtcAecmDelayEdit ) ).setText( "0" );

        ( ( TextView ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecEchoModeEdit ) ).setText( "2" );
        ( ( TextView ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecDelayEdit ) ).setText( "0" );
        ( ( CheckBox ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecIsUseDelayAgnosticModeCheckBox ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecIsUseExtdFilterModeCheckBox ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecIsUseRefinedFilterAdaptAecModeCheckBox ) ).setChecked( false );
        ( ( CheckBox ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecIsUseAdaptAdjDelayCheckBox ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecIsSaveMemFileCheckBox ) ).setChecked( false );

        ( ( RadioButton ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWorkModeSpeexAecWebRtcAecmWebRtcAecRadioBtn ) ).setChecked( true );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSpeexAecFilterLenEdit ) ).setText( "500" );
        ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSpeexAecIsUseRecCheckBox ) ).setChecked( true );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSpeexAecEchoMultipleEdit ) ).setText( "1.0" );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSpeexAecEchoContEdit ) ).setText( "0.6" );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSpeexAecEchoSupesEdit ) ).setText( "-32768" );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSpeexAecEchoSupesActEdit ) ).setText( "-32768" );
        ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecmIsUseCNGModeCheckBox ) ).setChecked( false );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecmEchoModeEdit ) ).setText( "4" );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecmDelayEdit ) ).setText( "0" );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecEchoModeEdit ) ).setText( "2" );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecDelayEdit ) ).setText( "0" );
        ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecIsUseDelayAgnosticModeCheckBox ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecIsUseExtdFilterModeCheckBox ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecIsUseRefinedFilterAdaptAecModeCheckBox ) ).setChecked( false );
        ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecIsUseAdaptAdjDelayCheckBox ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecIsUseSameRoomAecCheckBox ) ).setChecked( true );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSameRoomEchoMinDelayEdit ) ).setText( "420" );

        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseRNNoiseRadioBtn ) ).setChecked( true );

        ( ( CheckBox ) m_LyotActivitySpeexPprocNsViewPt.findViewById( R.id.SpeexPprocIsUseNsCheckBox ) ).setChecked( true );
        ( ( TextView ) m_LyotActivitySpeexPprocNsViewPt.findViewById( R.id.SpeexPprocNoiseSupesEdit ) ).setText( "-32768" );
        ( ( CheckBox ) m_LyotActivitySpeexPprocNsViewPt.findViewById( R.id.SpeexPprocIsUseDereverbCheckBox ) ).setChecked( true );

        ( ( TextView ) m_LyotActivityWebRtcNsxViewPt.findViewById( R.id.WebRtcNsxPolicyModeEdit ) ).setText( "3" );

        ( ( TextView ) m_LyotActivityWebRtcNsViewPt.findViewById( R.id.WebRtcNsPolicyModeEdit ) ).setText( "3" );

        ( ( CheckBox ) m_LyotActivitySettingViewPt.findViewById( R.id.IsUseSpeexPprocOtherCheckBox ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocIsUseVadCheckBox ) ).setChecked( true );
        ( ( TextView ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocVadProbStartEdit ) ).setText( "95" );
        ( ( TextView ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocVadProbContEdit ) ).setText( "95" );
        ( ( CheckBox ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocIsUseAgcCheckBox ) ).setChecked( true );
        ( ( TextView ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocAgcLevelEdit ) ).setText( "30000" );
        ( ( TextView ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocAgcIncrementEdit ) ).setText( "10" );
        ( ( TextView ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocAgcDecrementEdit ) ).setText( "-30000" );
        ( ( TextView ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocAgcMaxGainEdit ) ).setText( "25" );

        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseSpeexCodecRadioBtn ) ).setChecked( true );

        ( ( RadioButton ) m_LyotActivitySpeexCodecViewPt.findViewById( R.id.SpeexCodecEncoderUseVbrRadioBtn ) ).setChecked( true );
        ( ( TextView ) m_LyotActivitySpeexCodecViewPt.findViewById( R.id.SpeexCodecEncoderComplexityEdit ) ).setText( "10" );
        ( ( CheckBox ) m_LyotActivitySpeexCodecViewPt.findViewById( R.id.SpeexCodecIsUsePerceptualEnhancementCheckBox ) ).setChecked( true );

        ( ( CheckBox ) m_LyotActivitySettingViewPt.findViewById( R.id.IsSaveAudioToFileCheckBox ) ).setChecked( false );

        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseVideoSamplingRate24RadioBtn ) ).setChecked( true );
        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseVideoFrameSize480_640RadioBtn ) ).setChecked( true );
        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseDisplayScale1_0RadioBtn ) ).setChecked( true );
        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseOpenH264CodecRadioBtn ) ).setChecked( true );

        ( ( TextView ) m_LyotActivityOpenH264CodecViewPt.findViewById( R.id.OpenH264EncoderVideoTypeEdit ) ).setText( "0" );
        ( ( TextView ) m_LyotActivityOpenH264CodecViewPt.findViewById( R.id.OpenH264EncoderBitrateControlModeEdit ) ).setText( "3" );
        ( ( TextView ) m_LyotActivityOpenH264CodecViewPt.findViewById( R.id.OpenH264EncoderIDRFrameIntvlEdit ) ).setText( "24" );
        ( ( TextView ) m_LyotActivityOpenH264CodecViewPt.findViewById( R.id.OpenH264EncoderComplexityEdit ) ).setText( "1" );
    }

    // Effect level ：special。
    public void OnClickUseEffectPremiumRadioBtn( View BtnPt )
    {
        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseEffectPremiumRadioBtn ) ).setChecked( true );

        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseAudioSamplingRate32000RadioBtn ) ).setChecked( true );
        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseAudioFrameLen20msRadioBtn ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivitySettingViewPt.findViewById( R.id.IsUseSystemAecNsAgcCheckBox ) ).setChecked( true );

        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseSpeexWebRtcAecRadioBtn ) ).setChecked( true );

        ( ( TextView ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecFilterLenEdit ) ).setText( "500" );
        ( ( CheckBox ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecIsUseRecCheckBox ) ).setChecked( true );
        ( ( TextView ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecEchoMultipleEdit ) ).setText( "3.0" );
        ( ( TextView ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecEchoContEdit ) ).setText( "0.65" );
        ( ( TextView ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecEchoSupesEdit ) ).setText( "-32768" );
        ( ( TextView ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecEchoSupesActEdit ) ).setText( "-32768" );
        ( ( CheckBox ) m_LyotActivitySpeexAecViewPt.findViewById( R.id.SpeexAecIsSaveMemFileCheckBox ) ).setChecked( false );

        ( ( CheckBox ) m_LyotActivityWebRtcAecmViewPt.findViewById( R.id.WebRtcAecmIsUseCNGModeCheckBox ) ).setChecked( false );
        ( ( TextView ) m_LyotActivityWebRtcAecmViewPt.findViewById( R.id.WebRtcAecmEchoModeEdit ) ).setText( "4" );
        ( ( TextView ) m_LyotActivityWebRtcAecmViewPt.findViewById( R.id.WebRtcAecmDelayEdit ) ).setText( "0" );

        ( ( TextView ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecEchoModeEdit ) ).setText( "2" );
        ( ( TextView ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecDelayEdit ) ).setText( "0" );
        ( ( CheckBox ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecIsUseDelayAgnosticModeCheckBox ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecIsUseExtdFilterModeCheckBox ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecIsUseRefinedFilterAdaptAecModeCheckBox ) ).setChecked( false );
        ( ( CheckBox ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecIsUseAdaptAdjDelayCheckBox ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivityWebRtcAecViewPt.findViewById( R.id.WebRtcAecIsSaveMemFileCheckBox ) ).setChecked( false );

        ( ( RadioButton ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWorkModeSpeexAecWebRtcAecmWebRtcAecRadioBtn ) ).setChecked( true );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSpeexAecFilterLenEdit ) ).setText( "500" );
        ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSpeexAecIsUseRecCheckBox ) ).setChecked( true );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSpeexAecEchoMultipleEdit ) ).setText( "1.0" );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSpeexAecEchoContEdit ) ).setText( "0.6" );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSpeexAecEchoSupesEdit ) ).setText( "-32768" );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSpeexAecEchoSupesActEdit ) ).setText( "-32768" );
        ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecmIsUseCNGModeCheckBox ) ).setChecked( false );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecmEchoModeEdit ) ).setText( "4" );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecmDelayEdit ) ).setText( "0" );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecEchoModeEdit ) ).setText( "2" );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecDelayEdit ) ).setText( "0" );
        ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecIsUseDelayAgnosticModeCheckBox ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecIsUseExtdFilterModeCheckBox ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecIsUseRefinedFilterAdaptAecModeCheckBox ) ).setChecked( false );
        ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecIsUseAdaptAdjDelayCheckBox ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecWebRtcAecIsUseSameRoomAecCheckBox ) ).setChecked( true );
        ( ( TextView ) m_LyotActivitySpeexWebRtcAecViewPt.findViewById( R.id.SpeexWebRtcAecSameRoomEchoMinDelayEdit ) ).setText( "420" );

        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseRNNoiseRadioBtn ) ).setChecked( true );

        ( ( CheckBox ) m_LyotActivitySpeexPprocNsViewPt.findViewById( R.id.SpeexPprocIsUseNsCheckBox ) ).setChecked( true );
        ( ( TextView ) m_LyotActivitySpeexPprocNsViewPt.findViewById( R.id.SpeexPprocNoiseSupesEdit ) ).setText( "-32768" );
        ( ( CheckBox ) m_LyotActivitySpeexPprocNsViewPt.findViewById( R.id.SpeexPprocIsUseDereverbCheckBox ) ).setChecked( true );

        ( ( TextView ) m_LyotActivityWebRtcNsxViewPt.findViewById( R.id.WebRtcNsxPolicyModeEdit ) ).setText( "3" );

        ( ( TextView ) m_LyotActivityWebRtcNsViewPt.findViewById( R.id.WebRtcNsPolicyModeEdit ) ).setText( "3" );

        ( ( CheckBox ) m_LyotActivitySettingViewPt.findViewById( R.id.IsUseSpeexPprocOtherCheckBox ) ).setChecked( true );
        ( ( CheckBox ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocIsUseVadCheckBox ) ).setChecked( true );
        ( ( TextView ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocVadProbStartEdit ) ).setText( "95" );
        ( ( TextView ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocVadProbContEdit ) ).setText( "95" );
        ( ( CheckBox ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocIsUseAgcCheckBox ) ).setChecked( true );
        ( ( TextView ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocAgcLevelEdit ) ).setText( "30000" );
        ( ( TextView ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocAgcIncrementEdit ) ).setText( "10" );
        ( ( TextView ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocAgcDecrementEdit ) ).setText( "-30000" );
        ( ( TextView ) m_LyotActivitySpeexPprocOtherViewPt.findViewById( R.id.SpeexPprocAgcMaxGainEdit ) ).setText( "25" );

        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseSpeexCodecRadioBtn ) ).setChecked( true );

        ( ( RadioButton ) m_LyotActivitySpeexCodecViewPt.findViewById( R.id.SpeexCodecEncoderUseVbrRadioBtn ) ).setChecked( true );
        ( ( TextView ) m_LyotActivitySpeexCodecViewPt.findViewById( R.id.SpeexCodecEncoderComplexityEdit ) ).setText( "10" );
        ( ( CheckBox ) m_LyotActivitySpeexCodecViewPt.findViewById( R.id.SpeexCodecIsUsePerceptualEnhancementCheckBox ) ).setChecked( true );

        ( ( CheckBox ) m_LyotActivitySettingViewPt.findViewById( R.id.IsSaveAudioToFileCheckBox ) ).setChecked( false );

        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseVideoSamplingRate30RadioBtn ) ).setChecked( true );
        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseVideoFrameSize960_1280RadioBtn ) ).setChecked( true );
        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseDisplayScale1_0RadioBtn ) ).setChecked( true );
        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseOpenH264CodecRadioBtn ) ).setChecked( true );

        ( ( TextView ) m_LyotActivityOpenH264CodecViewPt.findViewById( R.id.OpenH264EncoderVideoTypeEdit ) ).setText( "0" );
        ( ( TextView ) m_LyotActivityOpenH264CodecViewPt.findViewById( R.id.OpenH264EncoderBitrateControlModeEdit ) ).setText( "3" );
        ( ( TextView ) m_LyotActivityOpenH264CodecViewPt.findViewById( R.id.OpenH264EncoderIDRFrameIntvlEdit ) ).setText( "30" );
        ( ( TextView ) m_LyotActivityOpenH264CodecViewPt.findViewById( R.id.OpenH264EncoderComplexityEdit ) ).setText( "2" );
    }

    //ratiospecial Rate grade ： low 。
    public void OnClickUseBitrateLowRadioBtn( View BtnPt )
    {
        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseBitrateLowRadioBtn ) ).setChecked( true );

        ( ( TextView ) m_LyotActivitySpeexCodecViewPt.findViewById( R.id.SpeexCodecEncoderQualityEdit ) ).setText( "1" );
        ( ( TextView ) m_LyotActivitySpeexCodecViewPt.findViewById( R.id.SpeexCodecEncoderPlcExpectedLossRateEdit ) ).setText( "1" );

        ( ( TextView ) m_LyotActivityOpenH264CodecViewPt.findViewById( R.id.OpenH264EncoderEncodedBitrateEdit ) ).setText( "1" );
    }

    //ratiospecial Rate grade ：in。
    public void OnClickUseBitrateMidRadioBtn( View BtnPt )
    {
        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseBitrateMidRadioBtn ) ).setChecked( true );

        ( ( TextView ) m_LyotActivitySpeexCodecViewPt.findViewById( R.id.SpeexCodecEncoderQualityEdit ) ).setText( "4" );
        ( ( TextView ) m_LyotActivitySpeexCodecViewPt.findViewById( R.id.SpeexCodecEncoderPlcExpectedLossRateEdit ) ).setText( "40" );

        ( ( TextView ) m_LyotActivityOpenH264CodecViewPt.findViewById( R.id.OpenH264EncoderEncodedBitrateEdit ) ).setText( "20" );
    }

    //ratiospecial Rate grade ：high。
    public void OnClickUseBitrateHighRadioBtn( View BtnPt )
    {
        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseBitrateHighRadioBtn ) ).setChecked( true );

        ( ( TextView ) m_LyotActivitySpeexCodecViewPt.findViewById( R.id.SpeexCodecEncoderQualityEdit ) ).setText( "8" );
        ( ( TextView ) m_LyotActivitySpeexCodecViewPt.findViewById( R.id.SpeexCodecEncoderPlcExpectedLossRateEdit ) ).setText( "80" );

        ( ( TextView ) m_LyotActivityOpenH264CodecViewPt.findViewById( R.id.OpenH264EncoderEncodedBitrateEdit ) ).setText( "40" );
    }

    //ratiospecial Rate grade ：ultra。
    public void OnClickUseBitrateSuperRadioBtn( View BtnPt )
    {
        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseBitrateSuperRadioBtn ) ).setChecked( true );

        ( ( TextView ) m_LyotActivitySpeexCodecViewPt.findViewById( R.id.SpeexCodecEncoderQualityEdit ) ).setText( "10" );
        ( ( TextView ) m_LyotActivitySpeexCodecViewPt.findViewById( R.id.SpeexCodecEncoderPlcExpectedLossRateEdit ) ).setText( "100" );

        ( ( TextView ) m_LyotActivityOpenH264CodecViewPt.findViewById( R.id.OpenH264EncoderEncodedBitrateEdit ) ).setText( "60" );
    }

    //ratiospecial Rate grade ：special。
    public void OnClickUseBitratePremiumRadioBtn( View BtnPt )
    {
        ( ( RadioButton ) m_LyotActivitySettingViewPt.findViewById( R.id.UseBitratePremiumRadioBtn ) ).setChecked( true );

        ( ( TextView ) m_LyotActivitySpeexCodecViewPt.findViewById( R.id.SpeexCodecEncoderQualityEdit ) ).setText( "10" );
        ( ( TextView ) m_LyotActivitySpeexCodecViewPt.findViewById( R.id.SpeexCodecEncoderPlcExpectedLossRateEdit ) ).setText( "100" );

        ( ( TextView ) m_LyotActivityOpenH264CodecViewPt.findViewById( R.id.OpenH264EncoderEncodedBitrateEdit ) ).setText( "80" );
    }
}