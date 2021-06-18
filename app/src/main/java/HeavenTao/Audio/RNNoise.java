package HeavenTao.Audio;

import HeavenTao.Data.*;

//RNNoise noise sound Suppressor 类。
public class RNNoise
{
    private long m_RNNoisePt; //RNNoise noise sound Suppressor 的内存指针。

    static
    {
        System.loadLibrary( "Func" ); //加载libFunc.so。
        System.loadLibrary( "c++_shared" ); //加载libc++_shared.so。
        System.loadLibrary( "WebRtc" ); //加载libWebRtc.so。
        System.loadLibrary( "RNNoise" ); //加载libRNNoise.so。
    }

    //构造函 number 。
    public RNNoise()
    {
        m_RNNoisePt = 0;
    }

    //析构函 number 。
    public void finalize()
    {
        Destroy();
    }

    //获取RNNoise noise sound Suppressor 的内存指针。
    public long GetRNNoisePt()
    {
        return m_RNNoisePt;
    }

    //创建并初始化RNNoise noise sound Suppressor 。
    public native int Init( int SamplingRate, int FrameLen );

    //用RNNoise noise sound Suppressor 对单声道16位 Have符号整型PCM格式 frame 进行RNNoise noise sound торможение 。
    public native int Proc( short FrameObj[], short ResultFrameObj[] );

    //destroyRNNoise noise sound Suppressor 。
    public native int Destroy();
}
