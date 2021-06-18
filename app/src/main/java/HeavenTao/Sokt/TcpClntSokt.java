package HeavenTao.Sokt;

import HeavenTao.Data.*;

//this 端TCPprotocol客户端套接digit类。
public class TcpClntSokt
{
    private long m_TcpClntSoktPt; //存放this 端TCPprotocol客户端套接digit的内存指针。

    static
    {
        System.loadLibrary( "Func" ); //加载libFunc.so。
        System.loadLibrary( "Sokt" ); //加载libSokt.so。
    }

    //构造函 number 。
    public TcpClntSokt()
    {
        m_TcpClntSoktPt = 0;
    }

    //析构函 number 。
    public void finalize()
    {
        Destroy( ( short )-1, null );
    }

    //获取this 端TCPprotocol客户端套接digit的内存指针。
    public long GetTcpClntSoktPt()
    {
        return m_TcpClntSoktPt;
    }

    //创建并初始化this 端TCPprotocol客户端套接digit，并连接已监听的远端TCPprotocol服务端套接digit。
    public native int Init( int RmtLclNodeAddrFmly, String RmtNodeNamePt, String RmtNodeSrvcPt, String LclNodeNamePt, String LclNodeSrvcPt, short TimeOutMsec, VarStr ErrInfoVarStrPt );

    //Get connected this 端TCPprotocol客户端套接digit绑定的this 地节点地址和port。
    public native int GetLclAddr( HTInt LclNodeAddrFmlyPt, HTString LclNodeAddrPt, HTString LclNodePortPt, VarStr ErrInfoVarStrPt );
    //Get connected this 端TCPprotocol客户端套接digit连接的远端TCPprotocol客户端套接digit绑定的远程节点地址和port。
    public native int GetRmtAddr( HTInt RmtNodeAddrFmlyPt, HTString RmtNodeAddrPt, HTString RmtNodePortPt, VarStr ErrInfoVarStrPt );

    //Use connected this  端TCPprotocol客户端套接digitSend one  number 据包到连接的远端TCPprotocol客户端套接digit。
    public native int SendPkt( byte DataBufPt[], long DataBufLen, short TimeOutMsec, VarStr ErrInfoVarStrPt );
    //Use connected this  端TCPprotocol客户端套接digit开始接收连接的远端TCPprotocol客户端套接digit发送的一个 number 据包。
    public native int RecvPkt( byte DataBufPt[], long DataBufSz, HTLong DataBufLenPt, short TimeOutMsec, VarStr ErrInfoVarStrPt );

    //Настраивать已连接的this 端TCPprotocol客户端套接digit的多线程操作状态。
    public native int SetMultiThread( int IsMultiThread, VarStr ErrInfoVarStrPt );
    //Get connected this 端TCPprotocol客户端套接digit的多线程操作状态。
    public native int GetMultiThread( HTInt IsMultiThreadPt, VarStr ErrInfoVarStrPt );

    //Настраивать已连接的this 端TCPprotocol客户端套接digit的Nagle延迟算法状态。
    public native int SetNoDelay( int IsNoDelay, VarStr ErrInfoVarStrPt );
    //Get connected this 端TCPprotocol客户端套接digit的Nagle延迟算法状态。
    public native int GetNoDelay( HTInt IsNoDelayPt, VarStr ErrInfoVarStrPt );

    //Настраиватьthis 端TCPprotocol客户端套接digit的发送缓冲区内存大小。
    public native int SetSendBufSz( long SendBufSz, VarStr ErrInfoVarStrPt );
    //获取this 端TCPprotocol客户端套接digit的发送缓冲区内存大小。
    public native int GetSendBufSz( HTLong SendBufSzPt, VarStr ErrInfoVarStrPt );

    //Настраиватьthis 端TCPprotocol客户端套接digit的接收缓冲区内存大小。
    public native int SetRecvBufSz( long RecvBufSz, VarStr ErrInfoVarStrPt );
    //获取this 端TCPprotocol客户端套接digit的接收缓冲区内存大小。
    public native int GetRecvBufSz( HTLong RecvBufSzPt, VarStr ErrInfoVarStrPt );
    //获取this 端TCPprotocol客户端套接digit的接收缓冲区 number According to the length 。
    public native int GetRecvBufLen( HTLong RecvBufLenPt, VarStr ErrInfoVarStrPt );

    //关闭并destroy已创建的this 端TCPprotocol客户端套接digit。
    public native int Destroy( short TimeOutSec, VarStr ErrInfoVarStrPt );
}