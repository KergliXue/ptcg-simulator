package com.ptcg.server.websocket;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class SocketIOConfig {

    @Bean
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration();
        // 绑定到所有接口，端口可配置，这里暂定 8081（通常 Spring Boot Web 是 8080，Socket.io 可以开 8081）
        config.setHostname("0.0.0.0");
        config.setPort(12021);
        
        // 允许跨域，兼容 ryuu 前端
        config.setOrigin("*");

        return new SocketIOServer(config);
    }

    @Bean
    public com.corundumstudio.socketio.annotation.SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketServer) {
        return new com.corundumstudio.socketio.annotation.SpringAnnotationScanner(socketServer);
    }
}
