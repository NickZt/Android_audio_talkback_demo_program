package HeavenTao.Ado;

import HeavenTao.Data.*;

//WebRtc浮点版噪音抑制器类。
public class WebRtcNs
{
    static
    {
        System.loadLibrary( "Func" ); //加载libFunc.so。
        System.loadLibrary( "c++_shared" ); //加载libc++_shared.so。
        System.loadLibrary( "WebRtc" ); //加载libWebRtc.so。
    }

    public long m_WebRtcNsPt; //WebRtc浮点版噪音抑制器的指针。

    //构造函数。
    public WebRtcNs()
    {
        m_WebRtcNsPt = 0;
    }

    //析构函数。
    protected void finalize()
    {
        Dstoy();
    }

    //创建并初始化WebRtc浮点版噪音抑制器。
    public int Init( int SmplRate, int FrmLen, int PolicyMode, Vstr ErrInfoVstrPt )
    {
        if( m_WebRtcNsPt == 0 )
        {
            HTLong p_WebRtcNsPt = new HTLong();
            if( WebRtcNsInit( p_WebRtcNsPt, SmplRate, FrmLen, PolicyMode, ( ErrInfoVstrPt != null ) ? ErrInfoVstrPt.m_VstrPt : 0 ) == 0 )
            {
                m_WebRtcNsPt = p_WebRtcNsPt.m_Val;
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

    //用WebRtc浮点版噪音抑制器对单声道16位有符号整型PCM格式帧进行WebRtc浮点版噪音抑制。
    public int Pocs( short FrmObj[], short RsltFrmObj[] )
    {
        return WebRtcNsPocs( m_WebRtcNsPt, FrmObj, RsltFrmObj );
    }

    //销毁WebRtc浮点版噪音抑制器。
    public int Dstoy()
    {
        if( m_WebRtcNsPt != 0 )
        {
            if( WebRtcNsDstoy( m_WebRtcNsPt ) == 0 )
            {
                m_WebRtcNsPt = 0;
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

    //创建并初始化WebRtc浮点版噪音抑制器。
    public native int WebRtcNsInit( HTLong WebRtcNsPt, int SmplRate, int FrmLen, int PolicyMode, long ErrInfoVstrPt );

    //用WebRtc浮点版噪音抑制器对单声道16位有符号整型PCM格式帧进行WebRtc浮点版噪音抑制。
    public native int WebRtcNsPocs( long WebRtcNsPt, short FrmObj[], short RsltFrmObj[] );

    //销毁WebRtc浮点版噪音抑制器。
    public native int WebRtcNsDstoy( long WebRtcNsPt );
}
