package HeavenTao.Video;

import HeavenTao.Data.*;

//videoAdaptive jitter buffer类。
public class VAjb
{
    private long m_VAjbPt; //存放videoAdaptive jitter buffer的内存指针。

    static
    {
        System.loadLibrary( "Func" ); //加载libFunc.so。
        System.loadLibrary( "c++_shared" ); //加载libc++_shared.so。
        System.loadLibrary( "Ajb" ); //加载libAjb.so。
    }

    //构造函 number 。
    public VAjb()
    {
        m_VAjbPt = 0;
    }

    //析构函 number 。
    public void finalize()
    {
        Destroy();
    }

    //获取videoAdaptive jitter buffer的内存指针。
    public long GetVAjbPt()
    {
        return m_VAjbPt;
    }

    //创建并初始化videoAdaptive jitter buffer。
    public native int Init( int IsHaveTimeStamp, int MinNeedBufFrameCnt, int MaxNeedBufFrameCnt, float AdaptSensitivity, int IsUseMutexLock );

    //放入一个digit节型 frame 到videoAdaptive jitter buffer。
    public native int PutOneByteFrame( long CurTime, int TimeStamp, byte ByteFramePt[], long FrameStart, long FrameLen );

    //放入一个短整型 frame 到videoAdaptive jitter buffer。
    public native int PutOneShortFrame( long CurTime, int TimeStamp, short ShortFramePt[], long FrameStart, long FrameLen );

    //从videoAdaptive jitter buffer取出一个digit节型 frame 。
    public native int GetOneByteFrame( long CurTime, HTInt TimeStampPt, byte ByteFramePt[], long FrameStart, long FrameStartSz, HTLong FrameLenPt );

    //从videoAdaptive jitter buffer取出一个短整型 frame 。
    public native int GetOneShortFrame( long CurTime, HTInt TimeStampPt, short ShortFramePt[], long FrameStart, long FrameStartSz, HTLong FrameLenPt );

    //从videoAdaptive jitter buffer取出一个digit节型 frame 。
    public native int GetOneByteFrameWantTimeStamp( long CurTime, int WantTimeStamp, HTInt TimeStampPt, byte ByteFramePt[], long FrameStart, long FrameStartSz, HTLong FrameLenPt );

    //从videoAdaptive jitter buffer取出一个短整型 frame 。
    public native int GetOneShortFrameWantTimeStamp( long CurTime, int WantTimeStamp, HTInt TimeStampPt, short ShortFramePt[], long FrameStart, long FrameStartSz, HTLong FrameLenPt );

    //获取缓冲 frame 的 number 量。
    public native int GetBufFrameCnt( HTInt CurHaveBufFrameCntPt, HTInt MinNeedBufFrameCntPt, HTInt MaxNeedBufFrameCntPt, HTInt CurNeedBufFrameCntPt );

    //EmptyvideoAdaptive jitter buffer。
    public native int Clear();

    //destroyvideoAdaptive jitter buffer。
    public native int Destroy();
}