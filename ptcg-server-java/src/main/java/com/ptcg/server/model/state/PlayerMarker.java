package com.ptcg.server.model.state;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PlayerMarker {
    private List<CardMarker> markers = new ArrayList<>();
}
