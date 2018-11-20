package top.eussi.nio;

import top.eussi.constants.Constants;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by xueming.wang on 2018/11/20.
 */
public class ReaderRunnable implements Runnable {
    private Selector selector;
    private String nickName;
    public ReaderRunnable(Selector selector, String nickName) {
        this.selector = selector;
        this.nickName = nickName;
    }
    @Override
    public void run() {
        try {
            //轮询
            while(true) {
                int readyChannels = selector.select();
                //如果当前无可用通道，继续轮询
                if(readyChannels == 0) continue;
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
                while(keyIterator.hasNext()) {
                    SelectionKey key = (SelectionKey) keyIterator.next();
                    keyIterator.remove();
                    deal(key);
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    private void deal(SelectionKey key) throws IOException {
        if(key.isReadable()){
            //使用 NIO 读取 Channel中的数据
            SocketChannel sc = (SocketChannel)key.channel();

            ByteBuffer buff = ByteBuffer.allocate(1024);
            String content = "";
            while(sc.read(buff) > 0)
            {
                buff.flip();
                content += Constants.CHARSET.decode(buff);
            }
            //若系统发送通知名字已经存在，则需要换个昵称
            if(Constants.USER_EXIST.equals(content)) {
                nickName = Constants.BLANK;
            }
            System.out.println(content);
            key.interestOps(SelectionKey.OP_READ);
        }
    }
}
