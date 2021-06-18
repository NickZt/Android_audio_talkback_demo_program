package HeavenTao.Audio;

import HeavenTao.Data.*;

//Speex Encoder 类。
public class SpeexEncoder
{
    private long m_SpeexEncoderPt; //存放Speex Encoder 的内存指针。

    static
    {
        System.loadLibrary( "Func" ); //加载libFunc.so。
        System.loadLibrary( "Speex" ); //加载libSpeex.so。
    }

    //构造函 number 。
    public SpeexEncoder()
    {
        m_SpeexEncoderPt = 0;
    }

    //析构函 number 。
    public void finalize()
    {
        Destroy();
    }

    //获取Speex Encoder 的内存指针。
    public long GetSpeexEncoderPt()
    {
        return m_SpeexEncoderPt;
    }

    //创建并初始化Speex Encoder 。
    public native int Init( int SamplingRate, int UseCbrOrVbr, int Quality, int Complexity, int PlcExpectedLossRate );

    //用Speex Encoder 对单声道16位 Have符号整型20 millisecondPCM格式 frame 进行Speex格式编码。
    public native int Proc( short PcmFramePt[], byte SpeexFramePt[], long SpeexFrameSz, HTLong SpeexFrameLenPt, HTInt IsNeedTransPt );

    //destroySpeex Encoder 。
    public native int Destroy();
}