package HeavenTao.Vdo;

import HeavenTao.Data.*;

//系统自带H264编码器类。
public class SystemH264Decd
{
    static
    {
        if( android.os.Build.VERSION.SDK_INT >= 21 )
        {
            System.loadLibrary( "Func" ); //加载libFunc.so。
            System.loadLibrary( "SystemH264" ); //加载libSystemH264.so。
        }
    }

    public long m_SystemH264DecdPt; //存放系统自带H264解码器的指针。

    //构造函数。
    public SystemH264Decd()
    {
        m_SystemH264DecdPt = 0;
    }

    //析构函数。
    protected void finalize()
    {
        Dstoy( null );
    }

    //创建并初始化系统自带H264解码器。
    public int Init( Vstr ErrInfoVstrPt )
    {
        if( m_SystemH264DecdPt == 0 )
        {
            if( android.os.Build.VERSION.SDK_INT < 21 )
            {
                ErrInfoVstrPt.Cpy( "当前系统不自带H264编码器。" );
                return -1;
            }

            HTLong p_WebRtcNsPt = new HTLong();
            if( SystemH264DecdInit( p_WebRtcNsPt, ( ErrInfoVstrPt != null ) ? ErrInfoVstrPt.m_VstrPt : 0 ) == 0 )
            {
                m_SystemH264DecdPt = p_WebRtcNsPt.m_Val;
                return 0;
            }
            else
            {
                return -1;
            }
        }
        else
        {
            return 0;
        }
    }

    //用系统自带H264解码器对H264格式进行8位无符号整型YU12格式帧解码。
    public int Pocs( byte H264FrmPt[], long H264FrmLen, byte YU12FrmPt[], long YU12FrmSz, HTInt YU12FrmWidth, HTInt YU12FrmHeight, long TimeOutMsec, Vstr ErrInfoVstrPt )
    {
        return SystemH264DecdPocs( m_SystemH264DecdPt, H264FrmPt, H264FrmLen, YU12FrmPt, YU12FrmSz, YU12FrmWidth, YU12FrmHeight, TimeOutMsec, ( ErrInfoVstrPt != null ) ? ErrInfoVstrPt.m_VstrPt : 0 );
    }

    //销毁系统自带H264解码器。
    public int Dstoy( Vstr ErrInfoVstrPt )
    {
        if( m_SystemH264DecdPt != 0 )
        {
            if( SystemH264DecdDstoy( m_SystemH264DecdPt, ( ErrInfoVstrPt != null ) ? ErrInfoVstrPt.m_VstrPt : 0 ) == 0 )
            {
                m_SystemH264DecdPt = 0;
                return 0;
            }
            else
            {
                return -1;
            }
        }
        else
        {
            return 0;
        }
    }

    //创建并初始化系统自带H264解码器。
    public native int SystemH264DecdInit( HTLong SystemH264DecdPt, long ErrInfoVstrPt );

    //用系统自带H264解码器对H264格式进行8位无符号整型YU12格式帧解码。
    public native int SystemH264DecdPocs( long SystemH264DecdPt, byte H264FrmPt[], long H264FrmLen, byte YU12FrmPt[], long YU12FrmSz, HTInt YU12FrmWidth, HTInt YU12FrmHeight, long TimeOutMsec, long ErrInfoVstrPt );

    //销毁系统自带H264解码器。
    public native int SystemH264DecdDstoy( long SystemH264DecdPt, long ErrInfoVstrPt );
}