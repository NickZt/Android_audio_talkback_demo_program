package HeavenTao.Audio;

import HeavenTao.Data.*;

//Speex解码器类。
public class SpeexDecoder
{
    private long m_SpeexDecoderPt; //存放Speex解码器的内存指针。

    static
    {
        System.loadLibrary( "Func" ); //加载libFunc.so。
        System.loadLibrary( "Speex" ); //加载libSpeex.so。
    }

    //构造函 number 。
    public SpeexDecoder()
    {
        m_SpeexDecoderPt = 0;
    }

    //析构函 number 。
    public void finalize()
    {
        Destroy();
    }

    //获取Speex解码器的内存指针。
    public long GetSpeexDecoderPt()
    {
        return m_SpeexDecoderPt;
    }

    //创建并初始化Speex解码器。
    public native int Init( int SamplingRate, int IsUsePerceptualEnhancement );

    //用Speex解码器对单声道16位 Have符号整型20 millisecondSpeex格式 frame 进行PCM格式解码。
    public native int Proc( byte SpeexFramePt[], long SpeexFrameLen, short PcmFramePt[] );

    //destroySpeex解码器。
    public native int Destroy();
}