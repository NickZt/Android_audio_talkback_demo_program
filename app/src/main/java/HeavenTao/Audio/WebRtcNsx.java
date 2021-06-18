package HeavenTao.Audio;

import HeavenTao.Data.*;

//WebRtc Fixed-point version  noise sound Suppressor 类。
public class WebRtcNsx
{
    private long m_WebRtcNsxPt; //WebRtc Fixed-point version  noise sound Suppressor 的内存指针。

    static
    {
        System.loadLibrary( "Func" ); //加载libFunc.so。
        System.loadLibrary( "c++_shared" ); //加载libc++_shared.so。
        System.loadLibrary( "WebRtc" ); //加载libWebRtc.so。
    }

    //构造函 number 。
    public WebRtcNsx()
    {
        m_WebRtcNsxPt = 0;
    }

    //析构函 number 。
    public void finalize()
    {
        Destroy();
    }

    //获取WebRtc Fixed-point version  noise sound Suppressor 的内存指针。
    public long GetWebRtcNsxPt()
    {
        return m_WebRtcNsxPt;
    }

    //创建并初始化WebRtc Fixed-point version  noise sound Suppressor 。
    public native int Init( int SamplingRate, int FrameLen, int PolicyMode );

    //用WebRtc Fixed-point version  noise sound Suppressor 对单声道16位 Have符号整型PCM格式 frame 进行WebRtc Fixed-point version  noise sound торможение 。
    public native int Proc( short FramePt[], short ResultFramePt[] );

    //destroyWebRtc Fixed-point version  noise sound Suppressor 。
    public native int Destroy();
}
