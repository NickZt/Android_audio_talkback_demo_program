package HeavenTao.Sokt;

import HeavenTao.Data.*;

//this 端UDPprotocol套接digit类。
public class UdpSokt
{
    private long m_UdpSoktPt; //存放this 端UDPprotocol套接digit的内存指针。

    static
    {
        System.loadLibrary( "Func" ); //加载libFunc.so。
        System.loadLibrary( "Sokt" ); //加载libSokt.so。
    }

    //构造函 number 。
    public UdpSokt()
    {
        m_UdpSoktPt = 0;
    }

    //析构函 number 。
    public void finalize()
    {
        Destroy( null );
    }

    //获取this 端UDPprotocol套接digit的内存指针。
    public long GetUdpSoktPt()
    {
        return m_UdpSoktPt;
    }

    //创建并初始化已监听的this 端UDPprotocol套接digit。
    public native int Init( int LclNodeAddrFmly, String LclNodeNamePt, String LclNodeSrvcPt, VarStr ErrInfoVarStrPt );

    //Use monitoredthis 端UDPprotocol套接digit连接已监听的远端UDPprotocol套接digit，已连接的this 端UDPprotocol套接digit只能接收连接的远端UDPprotocol套接digit发送的 number 据包。
    public native int Connect( int RmtNodeAddrFmly, String RmtNodeNamePt, String RmtNodeSrvcPt, VarStr ErrInfoVarStrPt );
    // will 已连接的this 端UDPprotocol套接digit断开连接的远端UDPprotocol套接digit，已连接的this 端UDPprotocol套接digit will 变成已监听的this 端UDPprotocol套接digit。
    public native int Disconnect( VarStr ErrInfoVarStrPt );

    //获取已监听的this 端UDPprotocol套接digit绑定的this 地节点地址和port。
    public native int GetLclAddr( HTInt LclNodeAddrFmlyPt, HTString LclNodeAddrPt, HTString LclNodePortPt, VarStr ErrInfoVarStrPt );
    //Get connected this 端UDPprotocol套接digit连接的远端UDPprotocol套接digit绑定的远程节点地址和port。
    public native int GetRmtAddr( HTInt RmtNodeAddrFmlyPt, HTString RmtNodeAddrPt, HTString RmtNodePortPt, VarStr ErrInfoVarStrPt );

    //用已监听或已连接的this 端UDPprotocol套接digitSend one  number 据包到指定的或连接的远端UDPprotocol套接digit。
    public native int SendPkt( int RmtNodeAddrFmly, String RmtNodeNamePt, String RmtNodeSrvcPt, byte DataBufPt[], long DataBufLen, short TimeOutMsec, VarStr ErrInfoVarStrPt );
    //用已监听或已连接的this 端UDPprotocol套接digit开始接收远端UDPprotocol套接digit发送的一个 number 据包。
    public native int RecvPkt( HTInt RmtNodeAddrFmlyPt, HTString RmtNodeAddrPt, HTString RmtNodePortPt, byte DataBufPt[], long DataBufSz, HTLong DataBufLenPt, short TimeOutMsec, VarStr ErrInfoVarStrPt );

    //Настраивать已监听或已连接的this 端UDPprotocol套接digit的多线程操作状态。
    public native int SetMultiThread( int IsMultiThread, VarStr ErrInfoVarStrPt );
    //获取已监听或已连接的this 端UDPprotocol套接digit的多线程操作状态。
    public native int GetMultiThread( HTInt IsMultiThreadPt, VarStr ErrInfoVarStrPt );

    //Настраивать已监听或已连接的this 端UDPprotocol套接digit的发送缓冲区内存大小。
    public native int SetSendBufSz( long SendBufSz, VarStr ErrInfoVarStrPt );
    //获取已监听或已连接的this 端UDPprotocol套接digit的发送缓冲区内存大小。
    public native int GetSendBufSz( HTLong SendBufSzPt, VarStr ErrInfoVarStrPt );

    //Настраивать已监听或已连接的this 端UDPprotocol套接digit的接收缓冲区内存大小。
    public native int SetRecvBufSz( long RecvBufSz, VarStr ErrInfoVarStrPt );
    //获取已监听或已连接的this 端UDPprotocol套接digit的接收缓冲区内存大小。
    public native int GetRecvBufSz( HTLong RecvBufSzPt, VarStr ErrInfoVarStrPt );
    //获取已监听或已连接的this 端UDPprotocol套接digit的接收缓冲区 number According to the length 。
    public native int GetRecvBufLen( HTLong RecvBufLenPt, VarStr ErrInfoVarStrPt );

    //关闭并destroy已创建的this 端UDPprotocol套接digit。
    public native int Destroy( VarStr ErrInfoVarStrPt );
}