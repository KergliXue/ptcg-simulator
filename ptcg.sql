/*
 Navicat Premium Dump SQL

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 90500 (9.5.0)
 Source Host           : localhost:3306
 Source Schema         : ptcg

 Target Server Type    : MySQL
 Target Server Version : 90500 (9.5.0)
 File Encoding         : 65001

 Date: 22/05/2026 17:43:24
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for card
-- ----------------------------
DROP TABLE IF EXISTS `card`;
CREATE TABLE `card` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `card_key` varchar(100) NOT NULL,
  `name` varchar(100) NOT NULL,
  `full_name` varchar(200) NOT NULL,
  `set_name` varchar(50) NOT NULL,
  `super_type` tinyint NOT NULL DEFAULT '0',
  `card_data` json NOT NULL,
  `arts` json NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `card_key` (`card_key`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of card
-- ----------------------------
BEGIN;
INSERT INTO `card` (`id`, `card_key`, `name`, `full_name`, `set_name`, `super_type`, `card_data`, `arts`) VALUES (11, 'Base-JetEnergy', 'Jet Energy', 'Jet Energy CSV4C 129', 'CSV4C', 3, '{\"id\": -1, \"set\": \"CSV4C\", \"name\": \"Jet Energy\", \"tags\": [], \"text\": \"\", \"fullName\": \"Jet Energy CSV4C 129\", \"provides\": [1], \"superType\": 3, \"energyType\": 1}', '[{\"rarity\": \"U\", \"imageUrl\": \"https://tcg.mik.moe/static/img/CSV4C/129.png\"}, {\"rarity\": \"UR\", \"imageUrl\": \"https://tcg.mik.moe/static/img/CSV8C/263.png\"}]');
INSERT INTO `card` (`id`, `card_key`, `name`, `full_name`, `set_name`, `super_type`, `card_data`, `arts`) VALUES (12, 'CSV2C-Kirlia', 'Kirlia', 'Kirlia CSV2C 054', 'CSV2C', 1, '{\"hp\": 80, \"id\": -1, \"set\": \"CSV2C\", \"name\": \"Kirlia\", \"tags\": [], \"stage\": 3, \"powers\": [{\"name\": \"Refinement\", \"text\": \"You must discard a card from your hand in order to use this Ability. Once during your turn, you may draw 2 cards.\", \"powerType\": 2, \"useWhenInPlay\": true}], \"attacks\": [{\"cost\": [4, 1], \"name\": \"Slap\", \"text\": \"\", \"damage\": \"20\"}], \"retreat\": [1], \"fullName\": \"Kirlia CSV2C 054\", \"weakness\": [{\"type\": 7}], \"cardTypes\": [4], \"superType\": 1, \"resistance\": [], \"evolvesFrom\": \"Ralts\"}', '[{\"rarity\": \"C\", \"imageUrl\": \"https://tcg.mik.moe/static/img/CSV2C/054.png\"}, {\"rarity\": \"AR\", \"imageUrl\": \"https://tcg.mik.moe/static/img/CSV2C/133.png\"}]');
INSERT INTO `card` (`id`, `card_key`, `name`, `full_name`, `set_name`, `super_type`, `card_data`, `arts`) VALUES (13, 'Base-DarknessEnergy', 'Darkness Energy', 'Darkness Energy CSVSC DAR', 'CSVSC', 3, '{\"id\": -1, \"set\": \"CSVSC\", \"name\": \"Darkness Energy\", \"tags\": [], \"text\": \"\", \"fullName\": \"Darkness Energy CSVSC DAR\", \"provides\": [8], \"superType\": 3, \"energyType\": 0}', '[{\"rarity\": \"C\", \"imageUrl\": \"https://tcg.mik.moe/static/img/CSVSC/DAR.png\"}]');
INSERT INTO `card` (`id`, `card_key`, `name`, `full_name`, `set_name`, `super_type`, `card_data`, `arts`) VALUES (14, 'Base-FightingEnergy', 'Fighting Energy', 'Fighting Energy CSVSC FIG', 'CSVSC', 3, '{\"id\": -1, \"set\": \"CSVSC\", \"name\": \"Fighting Energy\", \"tags\": [], \"text\": \"\", \"fullName\": \"Fighting Energy CSVSC FIG\", \"provides\": [3], \"superType\": 3, \"energyType\": 0}', '[{\"rarity\": \"C\", \"imageUrl\": \"https://tcg.mik.moe/static/img/CSVSC/FIG.png\"}]');
INSERT INTO `card` (`id`, `card_key`, `name`, `full_name`, `set_name`, `super_type`, `card_data`, `arts`) VALUES (15, 'CSV2C-GardevoirEx', 'Gardevoir ex', 'Gardevoir ex CSV2C 055', 'CSV2C', 1, '{\"hp\": 310, \"id\": -1, \"set\": \"CSV2C\", \"name\": \"Gardevoir ex\", \"tags\": [], \"stage\": 4, \"powers\": [{\"name\": \"Psychic Embrace\", \"text\": \"As often as you like during your turn, you may attach a Basic Psychic Energy card from your discard pile to 1 of your Psychic Pokémon. If you attached Energy to a Pokémon in this way, put 2 damage counters on that Pokémon. You can\'t use this Ability on a Pokémon that would be Knocked Out.\", \"powerType\": 2, \"useWhenInPlay\": true}], \"attacks\": [{\"cost\": [4, 4, 1], \"name\": \"Miracle Force\", \"text\": \"This Pokémon recovers from all Special Conditions.\", \"damage\": \"190\"}], \"retreat\": [1, 1], \"fullName\": \"Gardevoir ex CSV2C 055\", \"weakness\": [{\"type\": 8}], \"cardTypes\": [4], \"superType\": 1, \"resistance\": [], \"evolvesFrom\": \"Kirlia\"}', '[{\"rarity\": \"RR\", \"imageUrl\": \"https://tcg.mik.moe/static/img/CSV2C/055.png\"}, {\"rarity\": \"SR\", \"imageUrl\": \"https://tcg.mik.moe/static/img/CSV2C/140.png\"}, {\"rarity\": \"SAR\", \"imageUrl\": \"https://tcg.mik.moe/static/img/CSV2C/155.png\"}]');
INSERT INTO `card` (`id`, `card_key`, `name`, `full_name`, `set_name`, `super_type`, `card_data`, `arts`) VALUES (16, 'Base-UltraBall', 'Ultra Ball', 'Ultra Ball CSV1C 112', 'CSV1C', 2, '{\"id\": -1, \"set\": \"CSV1C\", \"name\": \"Ultra Ball\", \"tags\": [], \"text\": \"You can play this card only if you discard 2 other cards from your hand. Search your deck for a Pokémon, reveal it, and put it into your hand. Then, shuffle your deck.\", \"fullName\": \"Ultra Ball CSV1C 112\", \"superType\": 2, \"trainerType\": 0, \"useWhenInPlay\": false}', '[{\"rarity\": \"U\", \"imageUrl\": \"https://tcg.mik.moe/static/img/CSV1C/112.png\"}]');
INSERT INTO `card` (`id`, `card_key`, `name`, `full_name`, `set_name`, `super_type`, `card_data`, `arts`) VALUES (17, 'Base-LightningEnergy', 'Lightning Energy', 'Lightning Energy CSVSC LIG', 'CSVSC', 3, '{\"id\": -1, \"set\": \"CSVSC\", \"name\": \"Lightning Energy\", \"tags\": [], \"text\": \"\", \"fullName\": \"Lightning Energy CSVSC LIG\", \"provides\": [6], \"superType\": 3, \"energyType\": 0}', '[{\"rarity\": \"C\", \"imageUrl\": \"https://tcg.mik.moe/static/img/CSVSC/LIG.png\"}]');
INSERT INTO `card` (`id`, `card_key`, `name`, `full_name`, `set_name`, `super_type`, `card_data`, `arts`) VALUES (18, 'Base-NestBall', 'Nest Ball', 'Nest Ball CSV2C 110', 'CSV2C', 2, '{\"id\": -1, \"set\": \"CSV2C\", \"name\": \"Nest Ball\", \"tags\": [], \"text\": \"Search your deck for a Basic Pokémon and put it onto your Bench. Then, shuffle your deck.\", \"fullName\": \"Nest Ball CSV2C 110\", \"superType\": 2, \"trainerType\": 0, \"useWhenInPlay\": false}', '[{\"rarity\": \"U\", \"imageUrl\": \"https://tcg.mik.moe/static/img/CSV2C/110.png\"}]');
INSERT INTO `card` (`id`, `card_key`, `name`, `full_name`, `set_name`, `super_type`, `card_data`, `arts`) VALUES (19, 'Base-GrassEnergy', 'Grass Energy', 'Grass Energy CSVSC GRA', 'CSVSC', 3, '{\"id\": -1, \"set\": \"CSVSC\", \"name\": \"Grass Energy\", \"tags\": [], \"text\": \"\", \"fullName\": \"Grass Energy CSVSC GRA\", \"provides\": [2], \"superType\": 3, \"energyType\": 0}', '[{\"rarity\": \"C\", \"imageUrl\": \"https://tcg.mik.moe/static/img/CSVSC/GRA.png\"}]');
INSERT INTO `card` (`id`, `card_key`, `name`, `full_name`, `set_name`, `super_type`, `card_data`, `arts`) VALUES (20, 'CSV8C-Munkidori', 'Munkidori', 'Munkidori CSV8C 094', 'CSV8C', 1, '{\"hp\": 110, \"id\": -1, \"set\": \"CSV8C\", \"name\": \"Munkidori\", \"tags\": [], \"stage\": 2, \"powers\": [{\"name\": \"Adrena Brain\", \"text\": \"Once during your turn, you may put 3 damage counters on 1 of your opponent\'s Pokémon. If you placed any damage counters in this way, your turn ends.\", \"powerType\": 2, \"useWhenInPlay\": true}], \"attacks\": [{\"cost\": [4], \"name\": \"Mind Bend\", \"text\": \"This attack does 10 damage for each damage counter on your opponent\'s Active Pokémon.\", \"damage\": \"\"}], \"retreat\": [1], \"fullName\": \"Munkidori CSV8C 094\", \"weakness\": [{\"type\": 8}], \"cardTypes\": [4], \"superType\": 1, \"resistance\": [{\"type\": 3, \"value\": 30}], \"evolvesFrom\": \"\"}', '[{\"rarity\": \"R\", \"imageUrl\": \"https://tcg.mik.moe/static/img/CSV8C/094.png\"}]');
INSERT INTO `card` (`id`, `card_key`, `name`, `full_name`, `set_name`, `super_type`, `card_data`, `arts`) VALUES (21, 'CSV2C-Ralts', 'Ralts', 'Ralts CSV2C 053', 'CSV2C', 1, '{\"hp\": 60, \"id\": -1, \"set\": \"CSV2C\", \"name\": \"Ralts\", \"tags\": [], \"stage\": 2, \"powers\": [], \"attacks\": [{\"cost\": [4], \"name\": \"Memory Skip\", \"text\": \"Choose 1 of the Defending Pokémon\'s attacks. During your opponent\'s next turn, that Pokémon can\'t use that attack.\", \"damage\": \"10\"}], \"retreat\": [1], \"fullName\": \"Ralts CSV2C 053\", \"weakness\": [{\"type\": 8}], \"cardTypes\": [4], \"superType\": 1, \"resistance\": [], \"evolvesFrom\": \"\"}', '[{\"rarity\": \"C\", \"imageUrl\": \"https://tcg.mik.moe/static/img/CSV2C/053.png\"}, {\"rarity\": \"AR\", \"imageUrl\": \"https://tcg.mik.moe/static/img/CSV2C/132.png\"}]');
INSERT INTO `card` (`id`, `card_key`, `name`, `full_name`, `set_name`, `super_type`, `card_data`, `arts`) VALUES (22, 'Base-SuperRod', 'Super Rod', 'Super Rod CSV1C 109', 'CSV1C', 2, '{\"id\": -1, \"set\": \"CSV1C\", \"name\": \"Super Rod\", \"tags\": [], \"text\": \"Shuffle up to 3 in any combination of Pokémon and Basic Energy cards from your discard pile back into your deck.\", \"fullName\": \"Super Rod CSV1C 109\", \"superType\": 2, \"trainerType\": 0, \"useWhenInPlay\": false}', '[{\"rarity\": \"U\", \"imageUrl\": \"https://tcg.mik.moe/static/img/CSV1C/109.png\"}, {\"rarity\": \"UR\", \"imageUrl\": \"https://tcg.mik.moe/static/img/CSV1C/166.png\"}]');
INSERT INTO `card` (`id`, `card_key`, `name`, `full_name`, `set_name`, `super_type`, `card_data`, `arts`) VALUES (23, 'Base-WaterEnergy', 'Water Energy', 'Water Energy CSVSC WAT', 'CSVSC', 3, '{\"id\": -1, \"set\": \"CSVSC\", \"name\": \"Water Energy\", \"tags\": [], \"text\": \"\", \"fullName\": \"Water Energy CSVSC WAT\", \"provides\": [5], \"superType\": 3, \"energyType\": 0}', '[{\"rarity\": \"C\", \"imageUrl\": \"https://tcg.mik.moe/static/img/CSVSC/WAT.png\"}]');
INSERT INTO `card` (`id`, `card_key`, `name`, `full_name`, `set_name`, `super_type`, `card_data`, `arts`) VALUES (24, 'Base-PsychicEnergy', 'Psychic Energy', 'Psychic Energy CSVSC PSY', 'CSVSC', 3, '{\"id\": -1, \"set\": \"CSVSC\", \"name\": \"Psychic Energy\", \"tags\": [], \"text\": \"\", \"fullName\": \"Psychic Energy CSVSC PSY\", \"provides\": [4], \"superType\": 3, \"energyType\": 0}', '[{\"rarity\": \"C\", \"imageUrl\": \"https://tcg.mik.moe/static/img/CSVSC/PSY.png\"}]');
INSERT INTO `card` (`id`, `card_key`, `name`, `full_name`, `set_name`, `super_type`, `card_data`, `arts`) VALUES (25, 'Base-FireEnergy', 'Fire Energy', 'Fire Energy CSVSC FIR', 'CSVSC', 3, '{\"id\": -1, \"set\": \"CSVSC\", \"name\": \"Fire Energy\", \"tags\": [], \"text\": \"\", \"fullName\": \"Fire Energy CSVSC FIR\", \"provides\": [9], \"superType\": 3, \"energyType\": 0}', '[{\"rarity\": \"C\", \"imageUrl\": \"https://tcg.mik.moe/static/img/CSVSC/FIR.png\"}]');
INSERT INTO `card` (`id`, `card_key`, `name`, `full_name`, `set_name`, `super_type`, `card_data`, `arts`) VALUES (26, 'Base-PokemonSwitch', 'Pokemon Switch', 'Pokemon Switch CSV1C 113', 'CSV1C', 2, '{\"id\": -1, \"set\": \"CSV1C\", \"name\": \"Pokemon Switch\", \"tags\": [], \"text\": \"Switch your Active Pokémon with 1 of your Benched Pokémon.\\n\", \"fullName\": \"Pokemon Switch CSV1C 113\", \"superType\": 2, \"trainerType\": 0, \"useWhenInPlay\": false}', '[{\"rarity\": \"U\", \"imageUrl\": \"https://tcg.mik.moe/static/img/CSV1C/113.png\"}, {\"rarity\": \"UR\", \"imageUrl\": \"https://tcg.mik.moe/static/img/CSV1C/167.png\"}]');
INSERT INTO `card` (`id`, `card_key`, `name`, `full_name`, `set_name`, `super_type`, `card_data`, `arts`) VALUES (27, 'Base-BuddyBuddyPoffin', 'Buddy-Buddy Poffin', 'Buddy-Buddy Poffin CSV7C 177', 'CSV7C', 2, '{\"id\": -1, \"set\": \"CSV7C\", \"name\": \"Buddy-Buddy Poffin\", \"tags\": [], \"text\": \"Search your deck for up to 2 Basic Pokémon with 70 HP or less and put them onto your Bench. Then, shuffle your deck.\", \"fullName\": \"Buddy-Buddy Poffin CSV7C 177\", \"superType\": 2, \"trainerType\": 0, \"useWhenInPlay\": false}', '[{\"rarity\": \"U\", \"imageUrl\": \"https://tcg.mik.moe/static/img/CSV7C/177.png\"}, {\"rarity\": \"UR\", \"imageUrl\": \"https://tcg.mik.moe/static/img/CSV7C/258.png\"}]');
COMMIT;

-- ----------------------------
-- Table structure for card_metadata
-- ----------------------------
DROP TABLE IF EXISTS `card_metadata`;
CREATE TABLE `card_metadata` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `card_key` varchar(100) NOT NULL COMMENT 'Links to Java card class, e.g. csv8c-Munkidori',
  `name` varchar(100) NOT NULL COMMENT 'Card display name',
  `set_code` varchar(50) NOT NULL COMMENT 'Set identifier',
  `set_number` varchar(20) NOT NULL COMMENT 'Card number within set',
  `rarity` varchar(10) NOT NULL DEFAULT 'R' COMMENT 'Rarity: C, U, R, RR, SR, UR, etc.',
  `image_url` varchar(500) DEFAULT '' COMMENT 'Card image URL',
  `description` text COMMENT 'Card text / description',
  `flavor_text` varchar(500) DEFAULT '' COMMENT 'Flavor text',
  `illustrator` varchar(100) DEFAULT '' COMMENT 'Illustrator name',
  PRIMARY KEY (`id`),
  KEY `idx_card_key` (`card_key`),
  KEY `idx_set_code` (`set_code`),
  KEY `idx_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='PTCG Card Metadata';

-- ----------------------------
-- Records of card_metadata
-- ----------------------------
BEGIN;
INSERT INTO `card_metadata` (`id`, `card_key`, `name`, `set_code`, `set_number`, `rarity`, `image_url`, `description`, `flavor_text`, `illustrator`) VALUES (37, 'CSV8C-Munkidori', 'Munkidori', 'CSV8C', '094', 'R', 'https://tcg.mik.moe/static/img/CSV8C/094.png', 'Ability: Adrena Brain — If this Pokemon has any Darkness Energy attached, once during your turn you may move up to 3 damage counters from 1 of your Pokemon to 1 of your opponent\'s Pokemon. [P] Mind Bend 60 — Flip a coin. If heads, the Defending Pokemon is now Confused.', '', '');
INSERT INTO `card_metadata` (`id`, `card_key`, `name`, `set_code`, `set_number`, `rarity`, `image_url`, `description`, `flavor_text`, `illustrator`) VALUES (38, 'CSV2C-Ralts', 'Ralts', 'CSV2C', '053', 'C', 'https://tcg.mik.moe/static/img/CSV2C/053.png', '[P] Psychic Shot 30', '', '');
INSERT INTO `card_metadata` (`id`, `card_key`, `name`, `set_code`, `set_number`, `rarity`, `image_url`, `description`, `flavor_text`, `illustrator`) VALUES (39, 'CSV2C-Ralts', 'Ralts', 'CSV2C', '132', 'AR', 'https://tcg.mik.moe/static/img/CSV2C/132.png', '[P] Psychic Shot 30', '', '');
INSERT INTO `card_metadata` (`id`, `card_key`, `name`, `set_code`, `set_number`, `rarity`, `image_url`, `description`, `flavor_text`, `illustrator`) VALUES (40, 'CSV2C-Kirlia', 'Kirlia', 'CSV2C', '054', 'C', 'https://tcg.mik.moe/static/img/CSV2C/054.png', '[P] Magical Shot 30 [P][P] Psychic 60+ — This attack does 20 more damage for each Energy attached to your opponent\'s Active Pokemon.', '', '');
INSERT INTO `card_metadata` (`id`, `card_key`, `name`, `set_code`, `set_number`, `rarity`, `image_url`, `description`, `flavor_text`, `illustrator`) VALUES (41, 'CSV2C-Kirlia', 'Kirlia', 'CSV2C', '133', 'AR', 'https://tcg.mik.moe/static/img/CSV2C/133.png', '[P] Magical Shot 30 [P][P] Psychic 60+ — This attack does 20 more damage for each Energy attached to your opponent\'s Active Pokemon.', '', '');
INSERT INTO `card_metadata` (`id`, `card_key`, `name`, `set_code`, `set_number`, `rarity`, `image_url`, `description`, `flavor_text`, `illustrator`) VALUES (42, 'CSV2C-GardevoirEx', 'Gardevoir ex', 'CSV2C', '055', 'RR', 'https://tcg.mik.moe/static/img/CSV2C/055.png', 'Ability: Psychic Embrace — As often as you like during your turn, you may attach a Basic Psychic Energy from your discard pile to 1 of your Psychic Pokemon. If you do, put 2 damage counters on that Pokemon. [P][P][C] Miracle Force 190 — This Pokemon recovers from all Special Conditions.', '', '');
INSERT INTO `card_metadata` (`id`, `card_key`, `name`, `set_code`, `set_number`, `rarity`, `image_url`, `description`, `flavor_text`, `illustrator`) VALUES (43, 'CSV2C-GardevoirEx', 'Gardevoir ex', 'CSV2C', '140', 'SR', 'https://tcg.mik.moe/static/img/CSV2C/140.png', 'Ability: Psychic Embrace — As often as you like during your turn, you may attach a Basic Psychic Energy from your discard pile to 1 of your Psychic Pokemon. If you do, put 2 damage counters on that Pokemon. [P][P][C] Miracle Force 190 — This Pokemon recovers from all Special Conditions.', '', '');
INSERT INTO `card_metadata` (`id`, `card_key`, `name`, `set_code`, `set_number`, `rarity`, `image_url`, `description`, `flavor_text`, `illustrator`) VALUES (44, 'CSV2C-GardevoirEx', 'Gardevoir ex', 'CSV2C', '155', 'SAR', 'https://tcg.mik.moe/static/img/CSV2C/155.png', 'Ability: Psychic Embrace — As often as you like during your turn, you may attach a Basic Psychic Energy from your discard pile to 1 of your Psychic Pokemon. If you do, put 2 damage counters on that Pokemon. [P][P][C] Miracle Force 190 — This Pokemon recovers from all Special Conditions.', '', '');
INSERT INTO `card_metadata` (`id`, `card_key`, `name`, `set_code`, `set_number`, `rarity`, `image_url`, `description`, `flavor_text`, `illustrator`) VALUES (45, 'Base-UltraBall', 'Ultra Ball', 'CSV1C', '112', 'U', 'https://tcg.mik.moe/static/img/CSV1C/112.png', 'You can play this card only if you discard 2 other cards from your hand. Search your deck for a Pokemon, reveal it, and put it into your hand. Then, shuffle your deck.', '', '');
INSERT INTO `card_metadata` (`id`, `card_key`, `name`, `set_code`, `set_number`, `rarity`, `image_url`, `description`, `flavor_text`, `illustrator`) VALUES (46, 'Base-NestBall', 'Nest Ball', 'CSV2C', '110', 'U', 'https://tcg.mik.moe/static/img/CSV2C/110.png', 'Search your deck for a Basic Pokemon and put it onto your Bench. Then, shuffle your deck.', '', '');
INSERT INTO `card_metadata` (`id`, `card_key`, `name`, `set_code`, `set_number`, `rarity`, `image_url`, `description`, `flavor_text`, `illustrator`) VALUES (47, 'Base-SuperRod', 'Super Rod', 'CSV1C', '109', 'U', 'https://tcg.mik.moe/static/img/CSV1C/109.png', 'Shuffle up to 3 in any combination of Pokemon and Basic Energy cards from your discard pile back into your deck.', '', '');
INSERT INTO `card_metadata` (`id`, `card_key`, `name`, `set_code`, `set_number`, `rarity`, `image_url`, `description`, `flavor_text`, `illustrator`) VALUES (48, 'Base-SuperRod', 'Super Rod', 'CSV1C', '166', 'UR', 'https://tcg.mik.moe/static/img/CSV1C/166.png', 'Shuffle up to 3 in any combination of Pokemon and Basic Energy cards from your discard pile back into your deck.', '', '');
INSERT INTO `card_metadata` (`id`, `card_key`, `name`, `set_code`, `set_number`, `rarity`, `image_url`, `description`, `flavor_text`, `illustrator`) VALUES (49, 'Base-BuddyBuddyPoffin', 'Buddy-Buddy Poffin', 'CSV7C', '177', 'U', 'https://tcg.mik.moe/static/img/CSV7C/177.png', 'Search your deck for up to 2 Basic Pokemon with 70 HP or less and put them onto your Bench. Then, shuffle your deck.', '', '');
INSERT INTO `card_metadata` (`id`, `card_key`, `name`, `set_code`, `set_number`, `rarity`, `image_url`, `description`, `flavor_text`, `illustrator`) VALUES (50, 'Base-BuddyBuddyPoffin', 'Buddy-Buddy Poffin', 'CSV7C', '258', 'UR', 'https://tcg.mik.moe/static/img/CSV7C/258.png', 'Search your deck for up to 2 Basic Pokemon with 70 HP or less and put them onto your Bench. Then, shuffle your deck.', '', '');
INSERT INTO `card_metadata` (`id`, `card_key`, `name`, `set_code`, `set_number`, `rarity`, `image_url`, `description`, `flavor_text`, `illustrator`) VALUES (51, 'Base-PokemonSwitch', 'Pokemon Switch', 'CSV1C', '113', 'U', 'https://tcg.mik.moe/static/img/CSV1C/113.png', 'Switch your Active Pokemon with 1 of your Benched Pokemon.', '', '');
INSERT INTO `card_metadata` (`id`, `card_key`, `name`, `set_code`, `set_number`, `rarity`, `image_url`, `description`, `flavor_text`, `illustrator`) VALUES (52, 'Base-PokemonSwitch', 'Pokemon Switch', 'CSV1C', '167', 'UR', 'https://tcg.mik.moe/static/img/CSV1C/167.png', 'Switch your Active Pokemon with 1 of your Benched Pokemon.', '', '');
INSERT INTO `card_metadata` (`id`, `card_key`, `name`, `set_code`, `set_number`, `rarity`, `image_url`, `description`, `flavor_text`, `illustrator`) VALUES (53, 'Base-PsychicEnergy', 'Psychic Energy', 'CSVSC', 'PSY', 'C', 'https://tcg.mik.moe/static/img/CSVSC/PSY.png', 'Basic Psychic Energy — Provides 1 Psychic Energy.', '', '');
INSERT INTO `card_metadata` (`id`, `card_key`, `name`, `set_code`, `set_number`, `rarity`, `image_url`, `description`, `flavor_text`, `illustrator`) VALUES (54, 'Base-GrassEnergy', 'Grass Energy', 'CSVSC', 'GRA', 'C', 'https://tcg.mik.moe/static/img/CSVSC/GRA.png', 'Basic Grass Energy — Provides 1 Grass Energy.', '', '');
INSERT INTO `card_metadata` (`id`, `card_key`, `name`, `set_code`, `set_number`, `rarity`, `image_url`, `description`, `flavor_text`, `illustrator`) VALUES (55, 'Base-FireEnergy', 'Fire Energy', 'CSVSC', 'FIR', 'C', 'https://tcg.mik.moe/static/img/CSVSC/FIR.png', 'Basic Fire Energy — Provides 1 Fire Energy.', '', '');
INSERT INTO `card_metadata` (`id`, `card_key`, `name`, `set_code`, `set_number`, `rarity`, `image_url`, `description`, `flavor_text`, `illustrator`) VALUES (56, 'Base-WaterEnergy', 'Water Energy', 'CSVSC', 'WAT', 'C', 'https://tcg.mik.moe/static/img/CSVSC/WAT.png', 'Basic Water Energy — Provides 1 Water Energy.', '', '');
INSERT INTO `card_metadata` (`id`, `card_key`, `name`, `set_code`, `set_number`, `rarity`, `image_url`, `description`, `flavor_text`, `illustrator`) VALUES (57, 'Base-LightningEnergy', 'Lightning Energy', 'CSVSC', 'LIG', 'C', 'https://tcg.mik.moe/static/img/CSVSC/LIG.png', 'Basic Lightning Energy — Provides 1 Lightning Energy.', '', '');
INSERT INTO `card_metadata` (`id`, `card_key`, `name`, `set_code`, `set_number`, `rarity`, `image_url`, `description`, `flavor_text`, `illustrator`) VALUES (58, 'Base-FightingEnergy', 'Fighting Energy', 'CSVSC', 'FIG', 'C', 'https://tcg.mik.moe/static/img/CSVSC/FIG.png', 'Basic Fighting Energy — Provides 1 Fighting Energy.', '', '');
INSERT INTO `card_metadata` (`id`, `card_key`, `name`, `set_code`, `set_number`, `rarity`, `image_url`, `description`, `flavor_text`, `illustrator`) VALUES (59, 'Base-DarknessEnergy', 'Darkness Energy', 'CSVSC', 'DAR', 'C', 'https://tcg.mik.moe/static/img/CSVSC/DAR.png', 'Basic Darkness Energy — Provides 1 Darkness Energy.', '', '');
INSERT INTO `card_metadata` (`id`, `card_key`, `name`, `set_code`, `set_number`, `rarity`, `image_url`, `description`, `flavor_text`, `illustrator`) VALUES (60, 'Base-JetEnergy', 'Jet Energy', 'CSV4C', '129', 'U', 'https://tcg.mik.moe/static/img/CSV4C/129.png', 'As long as this card is attached to a Pokemon, it provides 1 Colorless Energy. When you attach this card from your hand to 1 of your Benched Pokemon, switch that Pokemon with your Active Pokemon.', '', '');
INSERT INTO `card_metadata` (`id`, `card_key`, `name`, `set_code`, `set_number`, `rarity`, `image_url`, `description`, `flavor_text`, `illustrator`) VALUES (61, 'Base-JetEnergy', 'Jet Energy', 'CSV8C', '263', 'UR', 'https://tcg.mik.moe/static/img/CSV8C/263.png', 'As long as this card is attached to a Pokemon, it provides 1 Colorless Energy. When you attach this card from your hand to 1 of your Benched Pokemon, switch that Pokemon with your Active Pokemon.', '', '');
COMMIT;

-- ----------------------------
-- Table structure for deck
-- ----------------------------
DROP TABLE IF EXISTS `deck`;
CREATE TABLE `deck` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `name` varchar(100) NOT NULL,
  `cards` text NOT NULL,
  `is_valid` tinyint(1) DEFAULT '0',
  `format_names` varchar(500) DEFAULT '[]',
  `card_types` varchar(500) DEFAULT '[]',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_name` (`user_id`,`name`),
  CONSTRAINT `deck_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of deck
-- ----------------------------
BEGIN;
INSERT INTO `deck` (`id`, `user_id`, `name`, `cards`, `is_valid`, `format_names`, `card_types`) VALUES (2, 3, 'test', '[\"Munkidori CSV8C 094\",\"Munkidori CSV8C 094\",\"Munkidori CSV8C 094\",\"Munkidori CSV8C 094\",\"Ralts CSV2C 053\",\"Ralts CSV2C 053\",\"Ralts CSV2C 053\",\"Ralts CSV2C 053\",\"Kirlia CSV2C 054\",\"Kirlia CSV2C 054\",\"Kirlia CSV2C 054\",\"Kirlia CSV2C 054\",\"Nest Ball CSV2C 110\",\"Nest Ball CSV2C 110\",\"Nest Ball CSV2C 110\",\"Nest Ball CSV2C 110\",\"Pokemon Switch CSV1C 113\",\"Pokemon Switch CSV1C 113\",\"Pokemon Switch CSV1C 113\",\"Pokemon Switch CSV1C 113\",\"Gardevoir ex CSV2C 055\",\"Gardevoir ex CSV2C 055\",\"Gardevoir ex CSV2C 055\",\"Gardevoir ex CSV2C 055\",\"Buddy-Buddy Poffin CSV7C 177\",\"Buddy-Buddy Poffin CSV7C 177\",\"Buddy-Buddy Poffin CSV7C 177\",\"Buddy-Buddy Poffin CSV7C 177\",\"Super Rod CSV1C 109\",\"Super Rod CSV1C 109\",\"Super Rod CSV1C 109\",\"Super Rod CSV1C 109\",\"Ultra Ball CSV1C 112\",\"Ultra Ball CSV1C 112\",\"Ultra Ball CSV1C 112\",\"Ultra Ball CSV1C 112\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\"]', 1, '[]', '[]');
INSERT INTO `deck` (`id`, `user_id`, `name`, `cards`, `is_valid`, `format_names`, `card_types`) VALUES (4, 5, '123', '[\"Munkidori CSV8C 094\",\"Munkidori CSV8C 094\",\"Munkidori CSV8C 094\",\"Munkidori CSV8C 094\",\"Ralts CSV2C 053\",\"Ralts CSV2C 053\",\"Ralts CSV2C 053\",\"Ralts CSV2C 053\",\"Kirlia CSV2C 054\",\"Kirlia CSV2C 054\",\"Kirlia CSV2C 054\",\"Kirlia CSV2C 054\",\"Nest Ball CSV2C 110\",\"Nest Ball CSV2C 110\",\"Nest Ball CSV2C 110\",\"Nest Ball CSV2C 110\",\"Pokemon Switch CSV1C 113\",\"Pokemon Switch CSV1C 113\",\"Pokemon Switch CSV1C 113\",\"Pokemon Switch CSV1C 113\",\"Gardevoir ex CSV2C 055\",\"Gardevoir ex CSV2C 055\",\"Gardevoir ex CSV2C 055\",\"Gardevoir ex CSV2C 055\",\"Buddy-Buddy Poffin CSV7C 177\",\"Buddy-Buddy Poffin CSV7C 177\",\"Buddy-Buddy Poffin CSV7C 177\",\"Buddy-Buddy Poffin CSV7C 177\",\"Super Rod CSV1C 109\",\"Super Rod CSV1C 109\",\"Super Rod CSV1C 109\",\"Super Rod CSV1C 109\",\"Ultra Ball CSV1C 112\",\"Ultra Ball CSV1C 112\",\"Ultra Ball CSV1C 112\",\"Ultra Ball CSV1C 112\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\",\"Psychic Energy CSVSC PSY\"]', 1, '[]', '[]');
COMMIT;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL,
  `email` varchar(255) NOT NULL DEFAULT '',
  `password` varchar(255) NOT NULL,
  `ranking` int DEFAULT '0',
  `registered` bigint DEFAULT '0',
  `last_seen` bigint DEFAULT '0',
  `last_ranking_change` bigint DEFAULT '0',
  `avatar_file` varchar(255) DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of user
-- ----------------------------
BEGIN;
INSERT INTO `user` (`id`, `name`, `email`, `password`, `ranking`, `registered`, `last_seen`, `last_ranking_change`, `avatar_file`) VALUES (1, 'testuser', 'test@test.com', '$2a$10$LmrRcGREXwnOHZ82ddAJ0Od7JmTKFK6orPZ6yR15aNvjluW8oSDm2', 0, 1779167522057, 1779167522149, 0, '');
INSERT INTO `user` (`id`, `name`, `email`, `password`, `ranking`, `registered`, `last_seen`, `last_ranking_change`, `avatar_file`) VALUES (2, 'player1', 'player1@test.com', '$2a$10$lfyB.FyyPLf8kJkpFDIYSep2FIsMIfArQcMUrZSxmwBtj5FiuXd6q', 0, 1779167955058, 0, 0, '');
INSERT INTO `user` (`id`, `name`, `email`, `password`, `ranking`, `registered`, `last_seen`, `last_ranking_change`, `avatar_file`) VALUES (3, 'Kergli', '1111@gmail.com', '$2a$10$qoFXTn91VpWkshObyylo/.mcA5RoX5lvSNSMZRPfYBcGFQU6MWSny', 0, 1779168279527, 1779442200093, 0, '');
INSERT INTO `user` (`id`, `name`, `email`, `password`, `ranking`, `registered`, `last_seen`, `last_ranking_change`, `avatar_file`) VALUES (4, 'debuguser', 'debug@test.com', '$2a$10$I1Raufm3llWcNSvJ0tfFt.WWD6GBWO0EsJBLnQ6MUl5ot0slI6gN6', 0, 1779172343175, 1779175078655, 0, '');
INSERT INTO `user` (`id`, `name`, `email`, `password`, `ranking`, `registered`, `last_seen`, `last_ranking_change`, `avatar_file`) VALUES (5, 'TestPlayer', 'test123@test.com', '$2a$10$56/1VefxIcms2FAVBrH6hu0JmOrWjsVmVMuWmbR3qfnfc4hRMX8.u', 0, 1779179056620, 1779428236645, 0, '');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
