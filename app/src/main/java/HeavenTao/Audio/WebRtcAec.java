package HeavenTao.Audio;

import HeavenTao.Data.*;

//WebRtc Floating point version  Acoustic echo sound Eliminator 类。
public class WebRtcAec
{
    private long m_WebRtcAecPt; //存放WebRtc Floating point version  Acoustic echo sound Eliminator 的内存指针。

    static
    {
        System.loadLibrary( "Func" ); //加载libFunc.so。
        System.loadLibrary( "c++_shared" ); //加载libc++_shared.so。
        System.loadLibrary( "WebRtc" ); //加载libWebRtc.so。
    }

    //构造函 number 。
    public WebRtcAec()
    {
        m_WebRtcAecPt = 0;
    }

    //析构函 number 。
    public void finalize()
    {
        Destroy();
    }

    //获取WebRtc Floating point version  Acoustic echo sound Eliminator 的内存指针。
    public long GetWebRtcAecPt()
    {
        return m_WebRtcAecPt;
    }

    //创建并初始化WebRtc Floating point version  Acoustic echo sound Eliminator 。
    public native int Init( int SamplingRate, int FrameLen, int EchoMode, int Delay, int IsUseDelayAgnosticMode, int IsUseExtdFilterMode, int IsUseRefinedFilterAdaptAecMode, int IsUseAdaptAdjDelay );

    //根据WebRtc Floating point version  Acoustic echo sound Eliminator 内存块来创建并初始化WebRtc Floating point version  Acoustic echo sound Eliminator 。
    public native int InitByMem( int SamplingRate, int FrameLen, int EchoMode, int Delay, int IsUseDelayAgnosticMode, int IsUseExtdFilterMode, int IsUseRefinedFilterAdaptAecMode, int IsUseAdaptAdjDelay, byte WebRtcAecMemPt[], long WebRtcAecMemLen );

    //根据WebRtc Floating point version  Acoustic echo sound Eliminator 内存块file来创建并初始化WebRtc Floating point version  Acoustic echo sound Eliminator 。
    public native int InitByMemFile( int SamplingRate, int FrameLen, int EchoMode, int Delay, int IsUseDelayAgnosticMode, int IsUseExtdFilterMode, int IsUseRefinedFilterAdaptAecMode, int IsUseAdaptAdjDelay, String WebRtcAecMemFileFullPathStrPt, VarStr ErrInfoVarStrPt );

    //获取WebRtc Floating point version  Acoustic echo sound Eliminator 内存块的 number According to the length 。
    public native int GetMemLen( int SamplingRate, int FrameLen, int EchoMode, int Delay, int IsUseDelayAgnosticMode, int IsUseExtdFilterMode, int IsUseRefinedFilterAdaptAecMode, int IsUseAdaptAdjDelay, HTLong WebrtcAecMemLenPt );

    //获取WebRtc Floating point version  Acoustic echo sound Eliminator Memory block 。
    public native int GetMem( int SamplingRate, int FrameLen, int EchoMode, int Delay, int IsUseDelayAgnosticMode, int IsUseExtdFilterMode, int IsUseRefinedFilterAdaptAecMode, int IsUseAdaptAdjDelay, byte WebRtcAecMemPt[], long WebRtcAecMemSz );

    // will WebRtc Floating point version  Acoustic echo sound Eliminator 内存块 Save 到指定的file。
    public native int SaveMemFile( int SamplingRate, int FrameLen, int EchoMode, int Delay, int IsUseDelayAgnosticMode, int IsUseExtdFilterMode, int IsUseRefinedFilterAdaptAecMode, int IsUseAdaptAdjDelay, String WebRtcAecMemFileFullPathStrPt, VarStr ErrInfoVarStrPt );

    //НастраиватьWebRtc Floating point version  Acoustic echo sound Eliminator 的回sound延迟。
    public native int SetDelay( int Delay );

    //获取WebRtc Floating point version  Acoustic echo sound Eliminator 的回sound延迟。
    public native int GetDelay( HTInt DelayPt );

    //用WebRtc Floating point version  Acoustic echo sound Eliminator 对单声道16位 Have符号整型PCM格式 enter  frame 进行WebRtc Floating point version  Acoustic echo sound消除。
    public native int Proc( short InputFramePt[], short OutputFramePt[], short ResultFramePt[] );

    //destroyWebRtc Floating point version  Acoustic echo sound Eliminator 。
    public native int Destroy();
}