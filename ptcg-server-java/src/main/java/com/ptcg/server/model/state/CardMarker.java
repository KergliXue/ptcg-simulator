package com.ptcg.server.model.state;

import com.ptcg.server.model.card.basic.Card;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardMarker {
    
    private String name = "";
    private Card source;
    private String param = ""; // Custom parameters (e.g. name of the blocked attack)
    private int duration = 1;  // Remaining duration in turns
}
