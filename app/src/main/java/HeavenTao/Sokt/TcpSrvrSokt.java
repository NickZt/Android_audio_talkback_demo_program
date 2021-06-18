package HeavenTao.Sokt;

import HeavenTao.Data.*;

//this 端TCPprotocol服务端套接digit类。
public class TcpSrvrSokt
{
    private long m_TcpSrvrSoktPt; //存放this 端TCPprotocol服务端套接digit的内存指针。

    static
    {
        System.loadLibrary( "Func" ); //加载libFunc.so。
        System.loadLibrary( "Sokt" ); //加载libSokt.so。
    }

    //构造函 number 。
    public TcpSrvrSokt()
    {
        m_TcpSrvrSoktPt = 0;
    }

    //析构函 number 。
    public void finalize()
    {
        Destroy( null );
    }

    //获取this 端TCPprotocol服务端套接digit的内存指针。
    public long GetTcpSrvrSoktPt()
    {
        return m_TcpSrvrSoktPt;
    }

    //创建并初始化已监听的this 端TCPprotocol服务端套接digit。
    public native int Init( int LclNodeAddrFmly, String LclNodeNamePt, String LclNodeSrvcPt, int MaxWait, int IsReuseAddr, VarStr ErrInfoVarStrPt );

    //获取已监听的this 端TCPprotocol服务端套接digit绑定的this 地节点地址和port。
    public native int GetLclAddr( HTInt LclNodeAddrFmlyPt, HTString LclNodeAddrPt, HTString LclNodePortPt, VarStr ErrInfoVarStrPt );

    //Use monitoredthis 端TCPprotocol服务端套接digit开始接受远端TCPprotocol客户端套接digit的连接。
    public native int Accept( HTInt RmtNodeAddrFmlyPt, HTString RmtNodeAddrPt, HTString RmtNodePortPt, short TimeOutMsec, TcpClntSokt TcpClntSoktPt, VarStr ErrInfoVarStrPt );

    //关闭并destroy已创建的this 端TCPprotocol服务端套接digit。
    public native int Destroy( VarStr ErrInfoVarStrPt );
}