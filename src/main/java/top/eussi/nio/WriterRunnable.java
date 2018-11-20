package top.eussi.nio;

import top.eussi.constants.Constants;

import java.nio.channels.SocketChannel;
import java.util.Scanner;

/**
 * Created by xueming.wang on 2018/11/20.
 */
public class WriterRunnable implements Runnable{
    private String nickName;
    private SocketChannel client;
    public WriterRunnable(SocketChannel client, String nickName) {
        this.nickName = nickName;
        this.client = client;
    }
    @Override
    public void run() {
        try{
            Scanner scan = new Scanner(System.in);
            while(scan.hasNextLine()){
                String line = scan.nextLine();
                if(Constants.BLANK.equals(line)) continue; //过滤空消息
                //第一次输入如果昵称为空，先给昵称赋值
                if(Constants.BLANK.equals(nickName)) {
                    nickName = line;
                    line = nickName + Constants.SEP;
                } else {
                    line = nickName + Constants.SEP + line;
                }

                //通过client写出去
                client.write(Constants.CHARSET.encode(line));
            }
            scan.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
