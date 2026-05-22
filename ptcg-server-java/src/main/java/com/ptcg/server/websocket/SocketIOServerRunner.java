package com.ptcg.server.websocket;

import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

@Component
public class SocketIOServerRunner implements CommandLineRunner, DisposableBean {

    private final SocketIOServer server;

    public SocketIOServerRunner(SocketIOServer server) {
        this.server = server;
    }

    @Override
    public void run(String... args) throws Exception {
        server.start();
        System.out.println("🚀 Socket.IO Server 已启动！");
    }

    @Override
    public void destroy() {
        if (server != null) {
            server.stop();
            System.out.println("🛑 Socket.IO Server 已停止。");
        }
    }
}
