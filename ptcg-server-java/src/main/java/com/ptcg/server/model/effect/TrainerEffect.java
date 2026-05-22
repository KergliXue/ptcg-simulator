package com.ptcg.server.model.effect;

import com.ptcg.server.model.card.basic.TrainerCard;
import com.ptcg.server.model.state.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainerEffect {
    private Player player;
    private TrainerCard trainerCard;
}
