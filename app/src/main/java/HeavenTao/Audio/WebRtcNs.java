package HeavenTao.Audio;

import HeavenTao.Data.*;

//WebRtc Floating point version  noise sound Suppressor 类。
public class WebRtcNs
{
    private long m_WebRtcNsPt; //WebRtc Floating point version  noise sound Suppressor 的内存指针。

    static
    {
        System.loadLibrary( "Func" ); //加载libFunc.so。
        System.loadLibrary( "c++_shared" ); //加载libc++_shared.so。
        System.loadLibrary( "WebRtc" ); //加载libWebRtc.so。
    }

    //构造函 number 。
    public WebRtcNs()
    {
        m_WebRtcNsPt = 0;
    }

    //析构函 number 。
    public void finalize()
    {
        Destroy();
    }

    //获取WebRtc Floating point version  noise sound Suppressor 的内存指针。
    public long GetWebRtcNsPt()
    {
        return m_WebRtcNsPt;
    }

    //创建并初始化WebRtc Floating point version  noise sound Suppressor 。
    public native int Init( int SamplingRate, int FrameLen, int PolicyMode );

    //用WebRtc Floating point version  noise sound Suppressor 对单声道16位 Have符号整型PCM格式 frame 进行WebRtc Floating point version  noise sound торможение 。
    public native int Proc( short FrameObj[], short ResultFrameObj[] );

    //destroyWebRtc Floating point version  noise sound Suppressor 。
    public native int Destroy();
}
