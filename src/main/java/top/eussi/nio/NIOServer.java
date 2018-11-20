package top.eussi.nio;

import top.eussi.constants.Constants;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by xueming.wang on 2018/11/20.
 */
public class NIOServer {
    private int port = 8080;//默认端口
    //在线人数
    private static HashSet<String> users = new HashSet<String>();
    private Selector selector = null;
    public NIOServer(int port) throws IOException {
        this.port = port;

        //打开通道
        ServerSocketChannel server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(this.port));
        server.configureBlocking(false);

        //
        selector = Selector.open();
        server.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Service launched，The listening port is ：" + this.port);
    }

    public void listen() throws IOException{
        //死循环，这里不会阻塞
        //CPU工作频率可控，是可控的固定值
        while(true) {
            //轮询到有几人排队
            int wait = selector.select();
            //如果当前无可用通道，继续轮询
            if(wait == 0) continue;

            //获取可用通道的集合
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            while(iterator.hasNext()) {
                SelectionKey key = (SelectionKey) iterator.next();
                //处理后需要移除，否则会下一次会继续处理
                iterator.remove();
                //处理逻辑
                deal(key);
            }
        }
    }

    /**
     * 处理key
     * @param key
     * @throws IOException
     */
    private void deal(SelectionKey key) throws IOException {
        //获取到链接
        if(key.isAcceptable()){
            ServerSocketChannel server = (ServerSocketChannel)key.channel();
            SocketChannel client = server.accept();
            //非阻塞模式,与Selector一起使用时，Channel必须处于非阻塞模式下
            client.configureBlocking(false);
            //注册选择器，并设置为读取模式，收到一个连接请求，然后起一个SocketChannel，并注册到selector上，之后这个连接的数据，就由这个SocketChannel处理
            client.register(selector, SelectionKey.OP_READ);

            //将此对应的channel设置为准备接受其他客户端请求
            key.interestOps(SelectionKey.OP_ACCEPT);
            System.out.println("There is a client connection，IP :" + client.getRemoteAddress());
            client.write(Constants.CHARSET.encode(Constants.PROMPT));
        }
        //读取客户端发送的消息
        if(key.isReadable()){
            //返回该SelectionKey对应的 Channel，其中有数据需要读取
            SocketChannel client = (SocketChannel)key.channel();

            //往缓冲区读数据
            ByteBuffer buff = ByteBuffer.allocate(1024);
            StringBuilder content = new StringBuilder();
            try{
                while(client.read(buff) > 0)
                {
                    buff.flip();
                    content.append(Constants.CHARSET.decode(buff));
                }
                System.out.println("message from " + client.getRemoteAddress() + " is : " + content);
                //将此对应的channel设置为准备下一次接受数据
                key.interestOps(SelectionKey.OP_READ);
            }catch (IOException io){
                key.cancel();
                if(key.channel() != null)
                {
                    key.channel().close();
                }
            }
            if(content.length() > 0) {
                String[] arrayContent = content.toString().split(Constants.SEP);
                //如果不包含内容，则是第一次输入消息，即注册用户
                if(arrayContent != null && arrayContent.length == 1) {
                    String nickName = arrayContent[0];
                    if(users.contains(nickName)) {
                        client.write(Constants.CHARSET.encode(Constants.USER_EXIST));
                    } else {
                        users.add(nickName);
                        int onlineCount = onlineCount();
                        String message = "welcome" + nickName + " enter the chat room! Current number of people online :" + onlineCount;
                        broadCast(null, message);
                    }
                } else if (arrayContent != null && arrayContent.length > 1) {
                    String nickName = arrayContent[0];
                    String message = content.substring(nickName.length() + Constants.SEP.length());
                    message = nickName + Constants.SEP + message;
                    if(users.contains(nickName)) {
                        //不回发给发送此内容的客户端
                        broadCast(client, message);
                    }
                }
            }
        }
    }

    /**
     * 统计在线人数
     * @return
     */
    public int onlineCount() {
        int res = 0;
        for(SelectionKey key : selector.keys()){
            Channel target = key.channel();
            if(target instanceof SocketChannel){
                res++;
            }
        }
        return res;
    }

    /**
     * 广播数据
     * @param client
     * @param content
     * @throws IOException
     */
    public void broadCast(SocketChannel client, String content) throws IOException {
        //广播数据到所有的SocketChannel中
        for(SelectionKey key : selector.keys()) {
            Channel targetchannel = key.channel();
            //如果client不为空，不回发给发送此内容的客户端
            if(targetchannel instanceof SocketChannel && targetchannel != client) {
                SocketChannel target = (SocketChannel)targetchannel;
                target.write(Constants.CHARSET.encode(content));
            }
        }
    }
}
