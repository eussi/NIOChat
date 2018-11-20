package top.eussi.bootstrap;

import top.eussi.nio.NIOClient;

import java.io.IOException;

/**
 * Created by xueming.wang on 2018/11/21.
 */
public class ClientStartup {
    public static void main(String[] args) {
        try {
            new NIOClient().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
