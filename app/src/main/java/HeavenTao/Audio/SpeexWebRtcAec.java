package HeavenTao.Audio;

import HeavenTao.Data.*;

//SpeexWebRtc triple  Acoustic echo sound Eliminator 类。
public class SpeexWebRtcAec
{
    private long m_SpeexWebRtcAecPt; //存放SpeexWebRtc triple  Acoustic echo sound Eliminator 的内存指针。

    static
    {
        System.loadLibrary( "Func" ); //加载libFunc.so。
        System.loadLibrary( "SpeexDsp" ); //加载libSpeex.so。
        System.loadLibrary( "c++_shared" ); //加载libc++_shared.so。
        System.loadLibrary( "WebRtc" ); //加载libWebRtc.so。
        System.loadLibrary( "SpeexWebRtcAec" ); //加载libSpeexWebRtcAec.so。
    }

    //构造函 number 。
    public SpeexWebRtcAec()
    {
        m_SpeexWebRtcAecPt = 0;
    }

    //析构函 number 。
    public void finalize()
    {
        Destroy();
    }

    //获取SpeexWebRtc triple  Acoustic echo sound Eliminator 的内存指针。
    public long GetSpeexWebRtcAecPt()
    {
        return m_SpeexWebRtcAecPt;
    }

    //创建并初始化SpeexWebRtc triple  Acoustic echo sound Eliminator 。
    public native int Init( int SamplingRate, int FrameLen, int WorkMode, int SpeexAecFilterLen, int SpeexAecIsUseRec, float SpeexAecEchoMultiple, float SpeexAecEchoCont, int SpeexAecEchoSupes, int SpeexAecEchoSupesAct, int WebRtcAecmIsUseCNGMode, int WebRtcAecmEchoMode, int WebRtcAecmDelay, int WebRtcAecEchoMode, int WebRtcAecDelay, int WebRtcAecIsUseDelayAgnosticMode, int WebRtcAecIsUseExtdFilterMode, int WebRtcAecIsUseRefinedFilterAdaptAecMode, int WebRtcAecIsUseAdaptAdjDelay, int IsUseSameRoomAec, int SameRoomEchoMinDelay );

    //НастраиватьSpeexWebRtc triple  Acoustic echo sound Eliminator 的WebRtc Fixed-point version  Acoustic echo sound Eliminator 的回sound延迟。
    public native int SetWebRtcAecmDelay( int WebRtcAecmDelay );

    //获取SpeexWebRtc triple  Acoustic echo sound Eliminator 的WebRtc Fixed-point version  Acoustic echo sound Eliminator 的回sound延迟。
    public native int GetWebRtcAecmDelay( HTInt WebRtcAecmDelayPt );

    //НастраиватьSpeexWebRtc triple  Acoustic echo sound Eliminator 的WebRtc Floating point version  Acoustic echo sound Eliminator 的回sound延迟。
    public native int SetWebRtcAecDelay( int WebRtcAecDelay );

    //获取SpeexWebRtc triple  Acoustic echo sound Eliminator 的WebRtc Floating point version  Acoustic echo sound Eliminator 的回sound延迟。
    public native int GetWebRtcAecDelay( HTInt WebRtcAecDelayPt );

    //用SpeexWebRtc triple  Acoustic echo sound Eliminator 对单声道16位 Have符号整型PCM格式 enter  frame 进行SpeexWebRtc triple  Acoustic echo sound消除。
    public native int Proc( short InputFramePt[], short OutputFramePt[], short ResultFramePt[] );

    //destroySpeexWebRtc triple  Acoustic echo sound Eliminator 。
    public native int Destroy();
}