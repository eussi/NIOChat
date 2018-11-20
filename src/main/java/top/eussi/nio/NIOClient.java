package top.eussi.nio;

import top.eussi.constants.Constants;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * Created by xueming.wang on 2018/11/20.
 */
public class NIOClient {
    private final InetSocketAddress serverAddress = new InetSocketAddress("localhost", Constants.SERVER_PORT);
    private SocketChannel client;
    private Selector selector;
    public NIOClient() throws IOException {
        //打开渠道
        client = SocketChannel.open(serverAddress);
        client.configureBlocking(false);//默认情况下channels是阻塞的.通过此行可以设置一个channel为nonblock的
        //并非所有的channel都可以设置为非阻塞的，比如file channels不能设置为nonblock.所有提供nonblocking I/O的类都是SelectableChannel的子类.

        selector = Selector.open();
        client.register(selector, SelectionKey.OP_READ);
    }

    public void start() {
        //从服务器端读数据
        new Thread(new ReaderRunnable(selector, Constants.BLANK)).start();
        //从服务器端写数据
        new Thread(new WriterRunnable(client, Constants.BLANK)).start();
    }
}
