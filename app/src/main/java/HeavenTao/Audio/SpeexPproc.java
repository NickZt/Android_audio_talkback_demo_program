package HeavenTao.Audio;

import HeavenTao.Data.*;

//Speex预处理器类。
public class SpeexPproc
{
    private long m_SpeexPprocPt; //存放Speex Preprocessor 内存指针。

    static
    {
        System.loadLibrary( "Func" ); //加载libFunc.so。
        System.loadLibrary( "SpeexDsp" ); //加载libSpeexDsp.so。
    }

    //构造函 number 。
    public SpeexPproc()
    {
        m_SpeexPprocPt = 0;
    }

    //析构函 number 。
    public void finalize()
    {
        Destroy();
    }

    //获取Speex Preprocessor 内存指针。
    public long GetSpeexPprocPt()
    {
        return m_SpeexPprocPt;
    }

    //创建并初始化Speex预处理器。
    public native int Init( int SamplingRate, int FrameLen, int IsUseNs, int NoiseSupes, int IsUseDereverb, int IsUseVad, int VadProbStart, int VadProbCont, int IsUseAgc, int AgcLevel, int AgcIncrement, int AgcDecrement, int AgcMaxGain );

    //用Speex预处理器对单声道16位 Have符号整型PCM格式 frame 进行Speex预处理。
    public native int Proc( short FramePt[], short ResultFramePt[], HTInt VoiceActStsPt );

    //destroySpeex预处理器。
    public native int Destroy();
}