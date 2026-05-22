package com.ptcg.server.serializer;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ptcg.server.model.card.basic.Card;
import com.ptcg.server.model.card.basic.SpecialCondition;
import com.ptcg.server.model.prompt.*;
import com.ptcg.server.model.state.*;

import java.io.StringWriter;
import java.util.List;

/**
 * Serializes game State into the JSON format expected by the TypeScript
 * StateSerializer.deserialize() on the frontend.
 *
 * Key differences from default Jackson serialization:
 * - Every object gets a "_type" field (e.g. "State", "Player", "Card", "CardList")
 * - Cards are serialized as {"_type":"Card","index":N} (index only, not full object)
 * - CardList.cards is an array of card IDs, not full card objects
 * - Top-level format is [players_array, state_object]
 */
public class StateSerializer {

    private final JsonFactory factory;
    private final ObjectMapper codec;

    public StateSerializer() {
        this.factory = new JsonFactory();
        this.codec = new ObjectMapper();
        this.codec.configure(SerializationFeature.WRITE_ENUMS_USING_INDEX, true);
    }

    public String serialize(State state) {
        try {
            StringWriter sw = new StringWriter();
            JsonGenerator gen = factory.createGenerator(sw);
            gen.setCodec(codec);

            // Output: [players, state]
            gen.writeStartArray();

            // Players array
            gen.writeStartArray();
            for (Player player : state.getPlayers()) {
                writePlayer(gen, player);
            }
            gen.writeEndArray();

            // State object
            writeState(gen, state);

            gen.writeEndArray();
            gen.close();
            return sw.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize state", e);
        }
    }

    // =========================================================================
    // STATE
    // =========================================================================

    private void writeState(JsonGenerator gen, State state) throws Exception {
        gen.writeStartObject();
        gen.writeStringField("_type", "State");

        // Players (included for compatibility, even though also in top-level array)
        gen.writeArrayFieldStart("players");
        for (Player player : state.getPlayers()) {
            writePlayer(gen, player);
        }
        gen.writeEndArray();

        gen.writeNumberField("phase", state.getPhase().ordinal());
        gen.writeNumberField("turn", state.getTurn());
        gen.writeNumberField("activePlayer", state.getActivePlayerIndex());
        gen.writeObjectField("winner", state.getWinner().getValue());

        // Prompts
        gen.writeArrayFieldStart("prompts");
        for (GamePrompt<?> prompt : state.getPrompts()) {
            writePrompt(gen, prompt);
        }
        gen.writeEndArray();

        // Logs
        gen.writeArrayFieldStart("logs");
        for (StateLog log : state.getLogs()) {
            writeStateLog(gen, log);
        }
        gen.writeEndArray();

        // Rules
        gen.writeFieldName("rules");
        writeRules(gen, state.getRules());

        // Card names
        gen.writeArrayFieldStart("cardNames");
        for (String name : state.getCardNames()) {
            gen.writeString(name);
        }
        gen.writeEndArray();

        gen.writeEndObject();
    }

    // =========================================================================
    // PLAYER
    // =========================================================================

    private void writePlayer(JsonGenerator gen, Player player) throws Exception {
        gen.writeStartObject();
        gen.writeStringField("_type", "Player");

        gen.writeNumberField("id", player.getId());
        gen.writeNumberField("userId", player.getUserId());
        gen.writeStringField("name", player.getName());

        gen.writeFieldName("deck");
        writeCardList(gen, player.getDeck());

        gen.writeFieldName("hand");
        writeCardList(gen, player.getHand());

        gen.writeFieldName("discard");
        writeCardList(gen, player.getDiscard());

        gen.writeFieldName("stadium");
        writeCardList(gen, player.getStadium());

        gen.writeFieldName("supporter");
        writeCardList(gen, player.getSupporter());

        // Prizes
        gen.writeArrayFieldStart("prizes");
        for (CardList prize : player.getPrizes()) {
            writeCardList(gen, prize);
        }
        gen.writeEndArray();

        // Active
        gen.writeFieldName("active");
        writePokemonSlot(gen, player.getActive());

        // Bench
        gen.writeArrayFieldStart("bench");
        for (PokemonSlot bench : player.getBench()) {
            writePokemonSlot(gen, bench);
        }
        gen.writeEndArray();

        gen.writeNumberField("retreatedTurn", player.getRetreatedTurn());
        gen.writeNumberField("energyPlayedTurn", player.getEnergyPlayedTurn());
        gen.writeNumberField("stadiumPlayedTurn", player.getStadiumPlayedTurn());
        gen.writeNumberField("stadiumUsedTurn", player.getStadiumUsedTurn());

        // Marker
        gen.writeFieldName("marker");
        writePlayerMarker(gen, player.getMarker());

        gen.writeStringField("avatarName", player.getAvatarName());

        gen.writeEndObject();
    }

    // =========================================================================
    // POKEMON SLOT
    // =========================================================================

    private void writePokemonSlot(JsonGenerator gen, PokemonSlot slot) throws Exception {
        gen.writeStartObject();
        gen.writeStringField("_type", "PokemonSlot");

        // Pokemons — CardList format
        gen.writeFieldName("pokemons");
        writeCardListFromList(gen, slot.getPokemons());

        // Energies
        gen.writeFieldName("energies");
        writeCardListFromList(gen, slot.getEnergies());

        // Trainers/tools
        gen.writeFieldName("trainers");
        writeCardListFromList(gen, slot.getTrainers());

        gen.writeNumberField("damage", slot.getDamage());
        gen.writeNumberField("pokemonPlayedTurn", slot.getPokemonPlayedTurn());
        gen.writeNumberField("poisonDamage", slot.getPoisonDamage());
        gen.writeNumberField("burnDamage", slot.getBurnDamage());

        // Special conditions
        gen.writeArrayFieldStart("specialConditions");
        for (SpecialCondition sc : slot.getSpecialConditions()) {
            gen.writeNumber(sc.ordinal());
        }
        gen.writeEndArray();

        // Marker (TypeScript: single Marker object with "markers" array inside)
        gen.writeFieldName("marker");
        gen.writeStartObject();
        gen.writeStringField("_type", "Marker");
        gen.writeArrayFieldStart("markers");
        for (CardMarker m : slot.getMarker()) {
            writeMarkerItem(gen, m);
        }
        gen.writeEndArray();
        gen.writeEndObject();

        gen.writeEndObject();
    }

    // =========================================================================
    // CARD LIST
    // =========================================================================

    private void writeCardList(JsonGenerator gen, CardList cardList) throws Exception {
        gen.writeStartObject();
        gen.writeStringField("_type", "CardList");

        // Cards as array of IDs
        gen.writeArrayFieldStart("cards");
        for (Card card : cardList.getCards()) {
            gen.writeNumber(card.getId());
        }
        gen.writeEndArray();

        gen.writeBooleanField("isPublic", cardList.isPublic());
        gen.writeBooleanField("isSecret", cardList.isSecret());

        gen.writeEndObject();
    }

    // =========================================================================
    // CARD (index reference)
    // =========================================================================

    private void writeCardListFromList(JsonGenerator gen, List<? extends Card> cards) throws Exception {
        writeCardListFromList(gen, cards, true, false);
    }

    private void writeCardListFromList(JsonGenerator gen, List<? extends Card> cards,
                                        boolean isPublic, boolean isSecret) throws Exception {
        gen.writeStartObject();
        gen.writeStringField("_type", "CardList");

        gen.writeArrayFieldStart("cards");
        for (Card card : cards) {
            gen.writeNumber(card.getId());
        }
        gen.writeEndArray();

        gen.writeBooleanField("isPublic", isPublic);
        gen.writeBooleanField("isSecret", isSecret);

        gen.writeEndObject();
    }

    private void writeCardRef(JsonGenerator gen, Card card) throws Exception {
        gen.writeStartObject();
        gen.writeStringField("_type", "Card");
        gen.writeNumberField("index", card.getId());
        gen.writeEndObject();
    }

    // =========================================================================
    // RULES
    // =========================================================================

    private void writeRules(JsonGenerator gen, GameRules rules) throws Exception {
        gen.writeStartObject();
        gen.writeStringField("_type", "Rules");

        gen.writeStringField("formatName", rules.getFormatName());
        gen.writeBooleanField("firstTurnDrawCard", rules.isFirstTurnDrawCard());
        gen.writeBooleanField("firstTurnUseSupporter", rules.isFirstTurnUseSupporter());
        gen.writeBooleanField("noPrizeForFossil", rules.isNoPrizeForFossil());

        gen.writeEndObject();
    }

    // =========================================================================
    // PLAYER MARKER
    // =========================================================================

    private void writePlayerMarker(JsonGenerator gen, PlayerMarker marker) throws Exception {
        gen.writeStartObject();
        gen.writeStringField("_type", "Marker");

        gen.writeArrayFieldStart("markers");
        for (CardMarker cm : marker.getMarkers()) {
            writeCardMarker(gen, cm);
        }
        gen.writeEndArray();

        gen.writeEndObject();
    }

    // =========================================================================
    // CARD MARKER
    // =========================================================================

    // Plain MarkerItem (no _type) — used inside PokemonSlot.marker.markers
    private void writeMarkerItem(JsonGenerator gen, CardMarker marker) throws Exception {
        gen.writeStartObject();
        gen.writeStringField("name", marker.getName());
        gen.writeStringField("param", marker.getParam());
        gen.writeNumberField("duration", marker.getDuration());
        if (marker.getSource() != null) {
            gen.writeFieldName("source");
            writeCardRef(gen, marker.getSource());
        } else {
            gen.writeNullField("source");
        }
        gen.writeEndObject();
    }

    private void writeCardMarker(JsonGenerator gen, CardMarker marker) throws Exception {
        gen.writeStartObject();
        gen.writeStringField("_type", "Marker");

        gen.writeStringField("name", marker.getName());
        gen.writeStringField("param", marker.getParam());
        gen.writeNumberField("duration", marker.getDuration());

        if (marker.getSource() != null) {
            gen.writeFieldName("source");
            writeCardRef(gen, marker.getSource());
        } else {
            gen.writeNullField("source");
        }

        gen.writeEndObject();
    }

    // =========================================================================
    // STATE LOG
    // =========================================================================

    private void writeStateLog(JsonGenerator gen, StateLog log) throws Exception {
        gen.writeStartObject();
        gen.writeStringField("_type", "StateLog");

        gen.writeNumberField("id", log.getId());
        gen.writeStringField("message", log.getMessage());
        if (log.getParams() != null) {
            gen.writeStringField("params", log.getParams());
        } else {
            gen.writeNullField("params");
        }
        gen.writeNumberField("client", log.getClient());

        gen.writeEndObject();
    }

    // =========================================================================
    // PROMPTS
    // =========================================================================

    private void writePrompt(JsonGenerator gen, GamePrompt<?> prompt) throws Exception {
        String typeName = prompt.getClass().getSimpleName();

        gen.writeStartObject();
        gen.writeStringField("_type", typeName);

        gen.writeNumberField("id", prompt.getId());
        gen.writeNumberField("playerId", prompt.getPlayerId());
        gen.writeStringField("message", prompt.getMessage());
        gen.writeStringField("type", prompt.getType());

        // result — only write if non-null (TypeScript omits undefined, so we omit null too)
        if (prompt.getResult() != null) {
            if (prompt.getResult() instanceof List<?> list) {
                gen.writeArrayFieldStart("result");
                for (Object item : list) {
                    if (item instanceof Card card) {
                        writeCardRef(gen, card);
                    } else {
                        gen.writeObject(item);
                    }
                }
                gen.writeEndArray();
            } else {
                gen.writeObjectField("result", prompt.getResult());
            }
        }

        // Prompt-specific fields
        if (prompt instanceof ChooseCardsPrompt cp) {
            gen.writeFieldName("cards");
            writeCardList(gen, cp.getCards());
            if (cp.getFilter() != null) {
                gen.writeObjectField("filter", cp.getFilter());
            } else {
                gen.writeNullField("filter");
            }
            gen.writeFieldName("options");
            writeChooseCardsOptions(gen, cp.getOptions());
        } else if (prompt instanceof ChooseEnergyPrompt ce) {
            gen.writeFieldName("cards");
            writeCardList(gen, ce.getCards());
            if (ce.getFilter() != null) {
                gen.writeObjectField("filter", ce.getFilter());
            } else {
                gen.writeNullField("filter");
            }
        } else if (prompt instanceof ChoosePrizePrompt cp) {
            gen.writeFieldName("prizes");
            writeCardList(gen, cp.getPrizes());
            gen.writeNumberField("min", cp.getMin());
            gen.writeNumberField("max", cp.getMax());
        } else if (prompt instanceof PutDamagePrompt pd) {
            gen.writeFieldName("targets");
            writeCardList(gen, pd.getTargets());
            gen.writeNumberField("totalDamage", pd.getTotalDamage());
        } else if (prompt instanceof ChooseAttackPrompt ca) {
            gen.writeBooleanField("allowPass", ca.isAllowPass());
        }

        gen.writeEndObject();
    }

    private void writeChooseCardsOptions(JsonGenerator gen,
                                          ChooseCardsPrompt.Options opts) throws Exception {
        gen.writeStartObject();
        gen.writeNumberField("min", opts.getMin());
        gen.writeNumberField("max", opts.getMax());
        gen.writeBooleanField("allowCancel", opts.isAllowCancel());
        gen.writeBooleanField("isSecret", opts.isSecret());
        gen.writeBooleanField("differentTypes", opts.isDifferentTypes());

        gen.writeArrayFieldStart("blocked");
        for (Integer b : opts.getBlocked()) {
            gen.writeNumber(b);
        }
        gen.writeEndArray();

        if (opts.getMaxPokemons() != null) {
            gen.writeNumberField("maxPokemons", opts.getMaxPokemons());
        }
        if (opts.getMaxEnergies() != null) {
            gen.writeNumberField("maxEnergies", opts.getMaxEnergies());
        }
        if (opts.getMaxTrainers() != null) {
            gen.writeNumberField("maxTrainers", opts.getMaxTrainers());
        }

        gen.writeEndObject();
    }
}
