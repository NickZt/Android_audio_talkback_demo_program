package HeavenTao.Audio;

import HeavenTao.Data.*;

//Wavefile写入器类。
public class WaveFileWriter
{
    private long m_WaveFileWriterPt; //存放Wavefile写入器的内存指针。

    static
    {
        System.loadLibrary( "Func" ); //加载libFunc.so。
        System.loadLibrary( "WaveFile" ); //加载libWaveFile.so。
    }

    //构造函 number 。
    public WaveFileWriter()
    {
        m_WaveFileWriterPt = 0;
    }

    //析构函 number 。
    public void finalize()
    {
        Destroy();
    }

    //获取Wavefile写入器的内存指针。
    public long GetWaveFileWriterPt()
    {
        return m_WaveFileWriterPt;
    }

    //创建并初始化Wavefile写入器。
    public native int Init( String WaveFileFullPathStrPt, short NumChanl, int SamplingRate, int SamplingBit );

    //用Wavefile写入器写入 number 据。
    public native int WriteData( short DataPt[], long DataLen );

    //destroyWavefile写入器。
    public native int Destroy();
}
