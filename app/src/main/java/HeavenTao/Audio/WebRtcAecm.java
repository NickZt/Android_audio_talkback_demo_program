package HeavenTao.Audio;

import HeavenTao.Data.*;

//WebRtc Fixed-point version  Acoustic echo sound Eliminator 类。
public class WebRtcAecm
{
    private long m_WebRtcAecmPt; //存放WebRtc Fixed-point version  Acoustic echo sound Eliminator 的内存指针。

    static
    {
        System.loadLibrary( "Func" ); //加载libFunc.so。
        System.loadLibrary( "c++_shared" ); //加载libc++_shared.so。
        System.loadLibrary( "WebRtc" ); //加载libWebRtc.so。
    }

    //构造函 number 。
    public WebRtcAecm()
    {
        m_WebRtcAecmPt = 0;
    }

    //析构函 number 。
    public void finalize()
    {
        Destroy();
    }

    //获取WebRtc Fixed-point version  Acoustic echo sound Eliminator 的内存指针。
    public long GetWebRtcAecmPt()
    {
        return m_WebRtcAecmPt;
    }

    //创建并初始化WebRtc Fixed-point version  Acoustic echo sound Eliminator 。
    public native int Init( int SamplingRate, int FrameLen, int IsUseCNGMode, int EchoMode, int Delay );

    //НастраиватьWebRtc Fixed-point version  Acoustic echo sound Eliminator 的回sound延迟。
    public native int SetDelay( int Delay );

    //获取WebRtc Fixed-point version  Acoustic echo sound Eliminator 的回sound延迟。
    public native int GetDelay( HTInt DelayPt );

    //用WebRtc Fixed-point version  Acoustic echo sound Eliminator 对单声道16位 Have符号整型PCM格式 enter  frame 进行WebRtc Fixed-point version  Acoustic echo sound消除。
    public native int Proc( short InputFramePt[], short OutputFramePt[], short ResultFramePt[] );

    //destroyWebRtc Fixed-point version  Acoustic echo sound Eliminator 。
    public native int Destroy();
}