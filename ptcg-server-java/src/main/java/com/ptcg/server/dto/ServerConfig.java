package com.ptcg.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServerConfig {
    private int apiVersion;
    private int defaultPageSize;
    private String scansUrl;
    private String avatarsUrl;
    private int avatarFileSize;
    private int avatarMinSize;
    private int avatarMaxSize;
    private int replayFileSize;
}
