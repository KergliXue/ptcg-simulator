package com.ptcg.server.store;

import com.corundumstudio.socketio.SocketIOClient;
import com.ptcg.server.model.state.State;

public class GameSession {

    private final String gameId;
    private final SocketIOClient player1Client;
    private SocketIOClient player2Client;
    private final int player1Id;
    private final int player2Id;
    private State state;

    public GameSession(String gameId, SocketIOClient player1Client, SocketIOClient player2Client) {
        this.gameId = gameId;
        this.player1Client = player1Client;
        this.player2Client = player2Client;
        this.player1Id = 0;
        this.player2Id = 1;
    }

    public String getGameId() {
        return gameId;
    }

    public SocketIOClient getClientForPlayer(int playerId) {
        if (playerId == player1Id) return player1Client;
        if (playerId == player2Id) return player2Client;
        return null;
    }

    public int getPlayer1Id() {
        return player1Id;
    }

    public int getPlayer2Id() {
        return player2Id;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public SocketIOClient getPlayer1Client() {
        return player1Client;
    }

    public SocketIOClient getPlayer2Client() {
        return player2Client;
    }

    public void setPlayer2Client(SocketIOClient player2Client) {
        this.player2Client = player2Client;
    }
}
