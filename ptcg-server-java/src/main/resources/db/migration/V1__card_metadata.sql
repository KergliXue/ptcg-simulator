CREATE TABLE IF NOT EXISTS card_metadata (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    card_key VARCHAR(100) NOT NULL COMMENT 'Links to Java card class',
    name VARCHAR(100) NOT NULL COMMENT 'Card display name',
    set_code VARCHAR(50) NOT NULL COMMENT 'Set identifier',
    set_number VARCHAR(20) NOT NULL COMMENT 'Card number within set',
    rarity VARCHAR(10) NOT NULL DEFAULT 'R' COMMENT 'Rarity: C, U, R, RR, SR, UR, etc.',
    image_url VARCHAR(500) DEFAULT '' COMMENT 'Card image URL',
    description TEXT COMMENT 'Card text / description',
    flavor_text VARCHAR(500) DEFAULT '' COMMENT 'Flavor text',
    illustrator VARCHAR(100) DEFAULT '' COMMENT 'Illustrator name',
    INDEX idx_card_key (card_key),
    INDEX idx_set_code (set_code),
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='PTCG Card Metadata';

-- ============================================================
-- Pokemon
-- ============================================================

-- Munkidori (愿增猿) - CSV8C
INSERT INTO card_metadata (card_key, name, set_code, set_number, rarity, image_url, description) VALUES
('CSV8C-Munkidori', 'Munkidori', 'CSV8C', '094', 'R',
 'https://tcg.mik.moe/static/img/CSV8C/094.png',
 'Ability: Adrena Brain — If this Pokemon has any Darkness Energy attached, once during your turn you may move up to 3 damage counters from 1 of your Pokemon to 1 of your opponent''s Pokemon. [P] Mind Bend 60 — Flip a coin. If heads, the Defending Pokemon is now Confused.');

-- Ralts (拉鲁拉丝) - CSV2C
INSERT INTO card_metadata (card_key, name, set_code, set_number, rarity, image_url, description) VALUES
('CSV2C-Ralts', 'Ralts', 'CSV2C', '053', 'C',
 'https://tcg.mik.moe/static/img/CSV2C/053.png',
 '[P] Psychic Shot 30'),
('CSV2C-Ralts', 'Ralts', 'CSV2C', '132', 'AR',
 'https://tcg.mik.moe/static/img/CSV2C/132.png',
 '[P] Psychic Shot 30');

-- Kirlia (奇鲁莉安) - CSV2C
INSERT INTO card_metadata (card_key, name, set_code, set_number, rarity, image_url, description) VALUES
('CSV2C-Kirlia', 'Kirlia', 'CSV2C', '054', 'C',
 'https://tcg.mik.moe/static/img/CSV2C/054.png',
 '[P] Magical Shot 30 [P][P] Psychic 60+ — This attack does 20 more damage for each Energy attached to your opponent''s Active Pokemon.'),
('CSV2C-Kirlia', 'Kirlia', 'CSV2C', '133', 'AR',
 'https://tcg.mik.moe/static/img/CSV2C/133.png',
 '[P] Magical Shot 30 [P][P] Psychic 60+ — This attack does 20 more damage for each Energy attached to your opponent''s Active Pokemon.');

-- Gardevoir ex (沙奈朵ex) - CSV2C
INSERT INTO card_metadata (card_key, name, set_code, set_number, rarity, image_url, description) VALUES
('CSV2C-GardevoirEx', 'Gardevoir ex', 'CSV2C', '055', 'RR',
 'https://tcg.mik.moe/static/img/CSV2C/055.png',
 'Ability: Psychic Embrace — As often as you like during your turn, you may attach a Basic Psychic Energy from your discard pile to 1 of your Psychic Pokemon. If you do, put 2 damage counters on that Pokemon. [P][P][C] Miracle Force 190 — This Pokemon recovers from all Special Conditions.'),
('CSV2C-GardevoirEx', 'Gardevoir ex', 'CSV2C', '140', 'SR',
 'https://tcg.mik.moe/static/img/CSV2C/140.png',
 'Ability: Psychic Embrace — As often as you like during your turn, you may attach a Basic Psychic Energy from your discard pile to 1 of your Psychic Pokemon. If you do, put 2 damage counters on that Pokemon. [P][P][C] Miracle Force 190 — This Pokemon recovers from all Special Conditions.'),
('CSV2C-GardevoirEx', 'Gardevoir ex', 'CSV2C', '155', 'SAR',
 'https://tcg.mik.moe/static/img/CSV2C/155.png',
 'Ability: Psychic Embrace — As often as you like during your turn, you may attach a Basic Psychic Energy from your discard pile to 1 of your Psychic Pokemon. If you do, put 2 damage counters on that Pokemon. [P][P][C] Miracle Force 190 — This Pokemon recovers from all Special Conditions.');

-- ============================================================
-- Trainers
-- ============================================================

INSERT INTO card_metadata (card_key, name, set_code, set_number, rarity, image_url, description) VALUES
('Base-UltraBall', 'Ultra Ball', 'CSV1C', '112', 'U',
 'https://tcg.mik.moe/static/img/CSV1C/112.png',
 'You can play this card only if you discard 2 other cards from your hand. Search your deck for a Pokemon, reveal it, and put it into your hand. Then, shuffle your deck.'),

('Base-NestBall', 'Nest Ball', 'CSV2C', '110', 'U',
 'https://tcg.mik.moe/static/img/CSV2C/110.png',
 'Search your deck for a Basic Pokemon and put it onto your Bench. Then, shuffle your deck.'),

('Base-SuperRod', 'Super Rod', 'CSV1C', '109', 'U',
 'https://tcg.mik.moe/static/img/CSV1C/109.png',
 'Shuffle up to 3 in any combination of Pokemon and Basic Energy cards from your discard pile back into your deck.'),
('Base-SuperRod', 'Super Rod', 'CSV1C', '166', 'UR',
 'https://tcg.mik.moe/static/img/CSV1C/166.png',
 'Shuffle up to 3 in any combination of Pokemon and Basic Energy cards from your discard pile back into your deck.'),

('Base-BuddyBuddyPoffin', 'Buddy-Buddy Poffin', 'CSV7C', '177', 'U',
 'https://tcg.mik.moe/static/img/CSV7C/177.png',
 'Search your deck for up to 2 Basic Pokemon with 70 HP or less and put them onto your Bench. Then, shuffle your deck.'),
('Base-BuddyBuddyPoffin', 'Buddy-Buddy Poffin', 'CSV7C', '258', 'UR',
 'https://tcg.mik.moe/static/img/CSV7C/258.png',
 'Search your deck for up to 2 Basic Pokemon with 70 HP or less and put them onto your Bench. Then, shuffle your deck.'),

('Base-PokemonSwitch', 'Pokemon Switch', 'CSV1C', '113', 'U',
 'https://tcg.mik.moe/static/img/CSV1C/113.png',
 'Switch your Active Pokemon with 1 of your Benched Pokemon.'),
('Base-PokemonSwitch', 'Pokemon Switch', 'CSV1C', '167', 'UR',
 'https://tcg.mik.moe/static/img/CSV1C/167.png',
 'Switch your Active Pokemon with 1 of your Benched Pokemon.');

-- ============================================================
-- Basic Energy
-- ============================================================

INSERT INTO card_metadata (card_key, name, set_code, set_number, rarity, image_url, description) VALUES
('Base-PsychicEnergy', 'Psychic Energy', 'CSVSC', 'PSY', 'C',
 'https://tcg.mik.moe/static/img/CSVSC/PSY.png',
 'Basic Psychic Energy — Provides 1 Psychic Energy.'),

('Base-GrassEnergy', 'Grass Energy', 'CSVSC', 'GRA', 'C',
 'https://tcg.mik.moe/static/img/CSVSC/GRA.png',
 'Basic Grass Energy — Provides 1 Grass Energy.'),

('Base-FireEnergy', 'Fire Energy', 'CSVSC', 'FIR', 'C',
 'https://tcg.mik.moe/static/img/CSVSC/FIR.png',
 'Basic Fire Energy — Provides 1 Fire Energy.'),

('Base-WaterEnergy', 'Water Energy', 'CSVSC', 'WAT', 'C',
 'https://tcg.mik.moe/static/img/CSVSC/WAT.png',
 'Basic Water Energy — Provides 1 Water Energy.'),

('Base-LightningEnergy', 'Lightning Energy', 'CSVSC', 'LIG', 'C',
 'https://tcg.mik.moe/static/img/CSVSC/LIG.png',
 'Basic Lightning Energy — Provides 1 Lightning Energy.'),

('Base-FightingEnergy', 'Fighting Energy', 'CSVSC', 'FIG', 'C',
 'https://tcg.mik.moe/static/img/CSVSC/FIG.png',
 'Basic Fighting Energy — Provides 1 Fighting Energy.'),

('Base-DarknessEnergy', 'Darkness Energy', 'CSVSC', 'DAR', 'C',
 'https://tcg.mik.moe/static/img/CSVSC/DAR.png',
 'Basic Darkness Energy — Provides 1 Darkness Energy.');

-- ============================================================
-- Special Energy
-- ============================================================

INSERT INTO card_metadata (card_key, name, set_code, set_number, rarity, image_url, description) VALUES
('Base-JetEnergy', 'Jet Energy', 'CSV4C', '129', 'U',
 'https://tcg.mik.moe/static/img/CSV4C/129.png',
 'As long as this card is attached to a Pokemon, it provides 1 Colorless Energy. When you attach this card from your hand to 1 of your Benched Pokemon, switch that Pokemon with your Active Pokemon.'),
('Base-JetEnergy', 'Jet Energy', 'CSV8C', '263', 'UR',
 'https://tcg.mik.moe/static/img/CSV8C/263.png',
 'As long as this card is attached to a Pokemon, it provides 1 Colorless Energy. When you attach this card from your hand to 1 of your Benched Pokemon, switch that Pokemon with your Active Pokemon.');
