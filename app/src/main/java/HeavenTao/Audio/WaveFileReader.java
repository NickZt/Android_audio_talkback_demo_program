package HeavenTao.Audio;

import HeavenTao.Data.*;

//Wavefile读取器类。
public class WaveFileReader
{
    private long m_WaveFileReaderPt; //存放Wavefile读取器的内存指针。

    static
    {
        System.loadLibrary( "Func" ); //加载libFunc.so。
        System.loadLibrary( "WaveFile" ); //加载libWaveFile.so。
    }

    //构造函 number 。
    public WaveFileReader()
    {
        m_WaveFileReaderPt = 0;
    }

    //析构函 number 。
    public void finalize()
    {
        Destroy();
    }

    //获取Wavefile读取器的内存指针。
    public long GetWaveFileReaderPt()
    {
        return m_WaveFileReaderPt;
    }

    //创建并初始化Wavefile读取器。
    public native int Init( String WaveFileFullPathStrPt, HTShort NumChanlPt, HTInt SamplingRatePt, HTInt SamplingBitPt );

    //用Wavefile读取器读取 number 据。
    public native int ReadData( short DataPt[], long DataSz, HTLong DataLenPt );

    //destroyWavefile读取器。
    public native int Destroy();
}
