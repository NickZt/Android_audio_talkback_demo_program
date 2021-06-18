package HeavenTao.Audio;

import HeavenTao.Data.*;

// Audio Adaptive jitter buffer类。
public class AAjb
{
    private long m_AAjbPt; //存放 Audio Adaptive jitter buffer的内存指针。

    static
    {
        System.loadLibrary( "Func" ); //加载libFunc.so。
        System.loadLibrary( "c++_shared" ); //加载libc++_shared.so。
        System.loadLibrary( "Ajb" ); //加载libAjb.so。
    }

    //构造函 number 。
    public AAjb()
    {
        m_AAjbPt = 0;
    }

    //析构函 number 。
    public void finalize()
    {
        Destroy();
    }

    //获取 Audio Adaptive jitter buffer的内存指针。
    public long GetAAjbPt()
    {
        return m_AAjbPt;
    }

    //创建并初始化 Audio Adaptive jitter buffer。
    public native int Init( int SamplingRate, int FrameLen, int IsHaveTimeStamp, int TimeStampStep, int InactIsContPut, int MinNeedBufFrameCnt, int MaxNeedBufFrameCnt, float AdaptSensitivity, int IsUseMutexLock );

    //放入一个digit节型 frame 到 Audio Adaptive jitter buffer。
    public native int PutOneByteFrame( int TimeStamp, byte ByteFramePt[], long FrameStart, long FrameLen );

    //放入一个短整型 frame 到 Audio Adaptive jitter buffer。
    public native int PutOneShortFrame( int TimeStamp, short ShortFramePt[], long FrameStart, long FrameLen );

    //从 Audio Adaptive jitter buffer取出一个digit节型 frame 。
    public native int GetOneByteFrame( HTInt TimeStampPt, byte ByteFramePt[], long FrameStart, long FrameSz, HTLong FrameLenPt );

    //从 Audio Adaptive jitter buffer取出一个短整型 frame 。
    public native int GetOneShortFrame( HTInt TimeStampPt, short ShortFramePt[], long FrameStart, long FrameSz, HTLong FrameLenPt );

    //获取缓冲 frame 的 number 量。
    public native int GetBufFrameCnt( HTInt CurHaveBufActFrameCntPt, HTInt CurHaveBufInactFrameCntPt, HTInt CurHaveBufFrameCntPt, HTInt MinNeedBufFrameCntPt, HTInt MaxNeedBufFrameCntPt, HTInt CurNeedBufFrameCntPt );

    //Empty Audio Adaptive jitter buffer。
    public native int Clear();

    //destroy Audio Adaptive jitter buffer。
    public native int Destroy();
}