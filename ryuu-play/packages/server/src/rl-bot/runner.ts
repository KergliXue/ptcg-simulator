import { CardManager, GameSettings, Rules } from '@ptcg/common';
import { baseSets } from '@ptcg/sets';

import { RlBot } from './rl-bot';
import { SimpleBot } from '../simple-bot/simple-bot';
import { Core } from '../game/core/core';
import { Storage } from '../storage';
import { config } from '../config';
import * as readline from 'readline';

async function bootstrap() {
  // 1. Initialize Cards (Mandatory before creating decks or games)
  const cardManager = CardManager.getInstance();
  cardManager.defineFormat('Base Sets', [
    baseSets.setBase,
    baseSets.setJungle,
    baseSets.setFossil
  ]);

  // 2. Configure Storage to use an in-memory SQLite database
  config.storage.type = 'sqlite';
  config.storage.database = ':memory:';

  const storage = new Storage();
  await storage.connect();

  const core = new Core(storage);

  const rlBot = new RlBot('RLAgent');
  const simpleBot = new SimpleBot('SimpleBot');

  rlBot.loadDeck = async () => rlDeck;
  simpleBot.loadDeck = async () => simpleDeck;

  core.connect(rlBot);
  core.connect(simpleBot);

  // 3. Create a basic valid deck (60 cards)
  const basicDeck: string[] = [];
  // Add 4 Pikachu
  for (let i = 0; i < 4; i++) basicDeck.push('Pikachu BS');
  // Add 2 Raichu
  for (let i = 0; i < 2; i++) basicDeck.push('Raichu BS');
  // Add 54 Lightning Energy
  for (let i = 0; i < 54; i++) basicDeck.push('Lightning Energy BS');

  const rlDeck: string[] = [...basicDeck];
  const simpleDeck: string[] = [...basicDeck];

  const rules = new Rules();
  rules.formatName = 'Base Sets';
  const gameSettings = new GameSettings();
  gameSettings.rules = rules;

  console.log('--- Starting PTCG Headless Environment ---');

  // 4. Start game
  core.createGame(rlBot, rlDeck, gameSettings, simpleBot);
  // Keep simpleDeck referenced to avoid TS unused variable warning
  console.log(`Initialized with simple deck size: ${simpleDeck.length}`);

  // 5. Setup stdin to read actions from python
  const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout,
    terminal: false
  });

  rl.on('line', (line) => {
    try {
      const data = JSON.parse(line);
      if (data.type === 'ACTION') {
        const clientId = data.clientId;
        const callback = (global as any).rlActionCallbacks?.[clientId];
        if (callback) {
          callback(data.action);
          delete (global as any).rlActionCallbacks[clientId];
        } else {
          console.error(`No pending callback for client ${clientId}`);
        }
      }
    } catch (e) {
      console.error("Invalid input from Python", e);
    }
  });

  // Check if games finished
  setInterval(() => {
    if (core.games.length === 0) {
      console.log('Game finished. Exiting...');
      process.exit(0);
    }
  }, 1000);
}

bootstrap().catch(err => {
  console.error('Error bootstrapping game runner:', err);
  process.exit(1);
});
