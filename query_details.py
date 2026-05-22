#!/usr/bin/env python3
"""Fetch card details for all our primary cards."""
import json, time, requests

DETAIL = "https://tcg.mik.moe/api/v3/card/card-detail"
session = requests.Session()
session.headers.update({"Content-Type": "application/json"})

# Primary cards with their set_code and card_index
primary_cards = [
    ("Munkidori", "CSV8C", "094"),
    ("Munkidori", "CSV8C", "134"),
    ("Munkidori", "CSV8C", "225"),
    ("Munkidori", "CSV8C", "248"),
    ("Ralts", "CSV2C", "053"),
    ("Ralts", "CSV2C", "132"),
    ("Kirlia", "CSV2C", "054"),
    ("Kirlia", "CSV2C", "133"),
    ("GardevoirEx", "CSV2C", "055"),
    ("GardevoirEx", "CSV2C", "140"),
    ("GardevoirEx", "CSV2C", "155"),
    ("UltraBall", "CSV1C", "112"),
    ("NestBall", "CSV2C", "110"),
    ("SuperRod", "CSV1C", "109"),
    ("SuperRod", "CSV1C", "166"),
    ("BuddyBuddyPoffin", "CSV7C", "177"),
    ("BuddyBuddyPoffin", "CSV7C", "258"),
    ("PokemonSwitch", "CSV1C", "113"),
    ("PokemonSwitch", "CSV1C", "167"),
    ("PsychicEnergy", "CSVSC", "PSY"),
    ("GrassEnergy", "CSVSC", "GRA"),
    ("FireEnergy", "CSVSC", "FIR"),
    ("WaterEnergy", "CSVSC", "WAT"),
    ("LightingEnergy", "CSVSC", "LIG"),
    ("FightingEnergy", "CSVSC", "FIG"),
    ("DarknessEnergy", "CSVSC", "DAR"),
    ("JetEnergy", "CSV4C", "129"),
    ("JetEnergy", "CSV8C", "263"),
]

all_details = {}

for name, set_code, card_index in primary_cards:
    key = f"{set_code}-{card_index}"
    print(f"Fetching {name} {key}...", flush=True)
    try:
        resp = session.post(DETAIL, json={"setCode": set_code, "cardIndex": card_index}, timeout=15)
        data = resp.json()
        if data.get("code") == 200:
            detail = data["data"]
            all_details[key] = detail
            print(f"  OK: {detail.get('name', '?')} | rarity={detail.get('rarity', '?')} | illustrator={detail.get('illustrator', '?')}", flush=True)
            desc = detail.get('description', '')
            if desc:
                print(f"  Description: {desc[:100]}...", flush=True)
        else:
            print(f"  API error: {data}", flush=True)
    except Exception as e:
        print(f"  Error: {e}", flush=True)
    time.sleep(0.1)

with open("/Users/kerglixue/dev/Code/PtcgCoach/mikmoe_card_details.json", "w") as f:
    json.dump(all_details, f, ensure_ascii=False, indent=2)
print(f"\nSaved {len(all_details)} card details to mikmoe_card_details.json", flush=True)
