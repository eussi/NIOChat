package top.eussi.bootstrap;

import top.eussi.constants.Constants;
import top.eussi.nio.NIOServer;

import java.io.IOException;

/**
 * Created by xueming.wang on 2018/11/20.
 */
public class ServerStartup {
    public static void main(String[] args) {
        try {
            new NIOServer(Constants.SERVER_PORT).listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
