package HeavenTao.Audio;

import HeavenTao.Data.*;

//Speex Acoustic echo sound Eliminator 类。
public class SpeexAec
{
    private long m_SpeexAecPt; //存放Speex Acoustic echo sound Eliminator 的内存指针。

    static
    {
        System.loadLibrary( "Func" ); //加载libFunc.so。
        System.loadLibrary( "SpeexDsp" ); //加载libSpeexDsp.so。
    }

    //构造函 number 。
    public SpeexAec()
    {
        m_SpeexAecPt = 0;
    }

    //析构函 number 。
    public void finalize()
    {
        Destroy();
    }

    //获取Speex Acoustic echo sound Eliminator 的内存指针。
    public long GetSpeexAecPt()
    {
        return m_SpeexAecPt;
    }

    //创建并初始化Speex Acoustic echo sound Eliminator 。
    public native int Init( int SamplingRate, int FrameLen, int FilterLen, int IsUseRec, float EchoMultiple, float EchoCont, int EchoSupes, int EchoSupesAct );

    //根据Speex Acoustic echo sound Eliminator 内存块来创建并初始化Speex Acoustic echo sound Eliminator 。
    public native int InitByMem( int SamplingRate, int FrameLen, int FilterLen, int IsUseRec, float EchoMultiple, float EchoCont, int EchoSupes, int EchoSupesAct, byte SpeexAecMemPt[], long SpeexAecMemLen );

    //根据Speex Acoustic echo sound Eliminator 内存块file来创建并初始化Speex Acoustic echo sound Eliminator 。
    public native int InitByMemFile( int SamplingRate, int FrameLen, int FilterLen, int IsUseRec, float EchoMultiple, float EchoCont, int EchoSupes, int EchoSupesAct, String SpeexAecMemFileFullPathStrPt, VarStr ErrInfoVarStrPt );

    //获取Speex Acoustic echo sound Eliminator 内存块的 number According to the length 。
    public native int GetMemLen( int SamplingRate, int FrameLen, int FilterLen, int IsUseRec, float EchoMultiple, float EchoCont, int EchoSupes, int EchoSupesAct, HTLong SpeexAecMemLenPt );

    //获取Speex Acoustic echo sound Eliminator Memory block 。
    public native int GetMem( int SamplingRate, int FrameLen, int FilterLen, int IsUseRec, float EchoMultiple, float EchoCont, int EchoSupes, int EchoSupesAct, byte SpeexAecMemPt[], long SpeexAecMemSz );

    // will Speex Acoustic echo sound Eliminator 内存块 Save 到指定的file。
    public native int SaveMemFile( int SamplingRate, int FrameLen, int FilterLen, int IsUseRec, float EchoMultiple, float EchoCont, int EchoSupes, int EchoSupesAct, String SpeexAecMemFileFullPathStrPt, VarStr ErrInfoVarStrPt );

    //用Speex Acoustic echo sound Eliminator 对单声道16位 Have符号整型PCM格式 enter  frame 进行Speex Acoustic echo sound消除。
    public native int Proc( short InputFramePt[], short OutputFramePt[], short ResultFramePt[] );

    //destroySpeex Acoustic echo sound Eliminator 。
    public native int Destroy();
}