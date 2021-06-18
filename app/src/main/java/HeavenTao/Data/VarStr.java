package HeavenTao.Data;

// dynamic digit符串类。
public class VarStr
{
    private long m_VarStrPt; //存放 dynamic digit符串的内存指针。

    static
    {
        System.loadLibrary( "Func" ); //加载libFunc.so。
    }

    //构造函 number 。
    public VarStr()
    {
        m_VarStrPt = 0;
    }

    //析构函 number 。
    public void finalize()
    {
        Destroy();
    }

    //获取 dynamic digit符串的内存指针。
    public long GetVarStrPt()
    {
        return m_VarStrPt;
    }

    //创建并初始化 dynamic digit符串。
    public native int Init();

    //复制digit符串到 dynamic digit符串。
    public native int Cpy( String StrPt );

    //插入digit符串到 dynamic digit符串的指定位置。
    public native int Ins( long Pos, String StrPt );

    //追加digit符串到 dynamic digit符串的末尾。
    public native int Cat( String StrPt );

    //Empty dynamic digit符串的digit符串。
    public native int SetEmpty();

    //Настраивать dynamic digit符串的digit符串内存大小。
    public native int SetSz( long StrSz );

    //获取 dynamic digit符串的digit符串内存大小。
    public native int GetSz( HTLong StrSzPt );

    //获取 dynamic digit符串的digit符串。
    public native String GetStr();

    //destroy dynamic digit符串。
    public native int Destroy();
}