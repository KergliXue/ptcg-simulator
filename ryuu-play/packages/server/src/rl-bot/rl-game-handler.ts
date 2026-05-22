import { Action, Player, Prompt, State, InvitePlayerPrompt, ResolvePromptAction, GamePhase } from '@ptcg/common';
import { Client } from '../game/client/client.interface';
import { Game } from '../game/core/game';

export class RlGameHandler {

  private state: State | undefined;
  private changeInProgress: boolean = false;
  private deckPromise: Promise<string[]>;
  private deck: string[] | null = null;

  constructor(
    private client: Client,
    public game: Game,
    deckPromise: Promise<string[]>
  ) {
    this.deckPromise = deckPromise;
    this.waitForDeck();
  }

  public async onStateChange(state: State): Promise<void> {
    if (this.changeInProgress) {
      this.state = state;
      return;
    }

    this.state = undefined;
    this.changeInProgress = true;

    await this.processState(state);

    this.changeInProgress = false;
    // A state change was ignored, because we were processing
    if (this.state) {
      this.onStateChange(this.state);
    }
  }

  private async waitForDeck(): Promise<void> {
    try {
      this.deck = await this.deckPromise;
    } catch (error) {
      // continue regardless of error
    }

    if (this.state) {
      this.onStateChange(this.state);
    }
  }

  private async processState(state: State): Promise<void> {
    let player: Player | undefined;
    for (let i = 0; i < state.players.length; i++) {
      if (state.players[i].id === this.client.id) {
        player = state.players[i];
      }
    }

    if (player === undefined) {
      return;
    }

    // Check for Prompts
    if (state.prompts.length > 0) {
      const prompt = state.prompts.find(p => p.playerId === player!.id && p.result === undefined);
      if (prompt !== undefined) {
        if (prompt instanceof InvitePlayerPrompt) {
          const action = new ResolvePromptAction(prompt.id, this.deck);
          this.game.dispatch(this.client, action);
          return;
        }

        // It's a prompt for our RL agent to resolve.
        await this.requestActionFromPython(state, prompt);
        return;
      }
    }

    // Wait for other players to resolve the prompts.
    if (state.prompts.filter(p => p.result === undefined).length > 0) {
      return;
    }

    const activePlayer = state.players[state.activePlayer];
    const isMyTurn = activePlayer.id === this.client.id;
    if (state.phase === GamePhase.PLAYER_TURN && isMyTurn) {
      // It's our turn to act
      await this.requestActionFromPython(state);
    }
  }

  private requestActionFromPython(state: State, prompt?: Prompt<any>): Promise<void> {
    return new Promise((resolve) => {
      // Send the state to stdout
      const message = {
        type: 'STATE_UPDATE',
        clientId: this.client.id,
        gameId: this.game.id,
        state: state,
        prompt: prompt
      };
      
      console.log(JSON.stringify(message));

      // We need a global listener to resolve this promise when python sends an action
      // For now, we will use a global event emitter or similar.
      // Easiest is to add a callback to a global registry.
      const callback = (action: Action) => {
        try {
          this.game.dispatch(this.client, action);
        } catch (error) {
          // Send error back or ignore
          console.error("Action error", error);
        }
        resolve();
      };
      
      (global as any).rlActionCallbacks = (global as any).rlActionCallbacks || {};
      (global as any).rlActionCallbacks[this.client.id] = callback;
    });
  }

}
