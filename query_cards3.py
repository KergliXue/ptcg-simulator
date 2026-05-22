#!/usr/bin/env python3
"""Get ALL prints for each card from mik.moe."""
import json, time, requests

API = "https://tcg.mik.moe/api/v3/card/card-basic-search"
DETAIL = "https://tcg.mik.moe/api/v3/card/card-detail"
session = requests.Session()
session.headers.update({"Content-Type": "application/json"})

# Card name -> search text (Chinese name that works)
card_searches = [
    ("Munkidori", "愿增猿"),
    ("Ralts", "拉鲁拉丝"),
    ("Kirlia", "奇鲁莉安"),
    ("GardevoirEx", "沙奈朵ex"),
    ("UltraBall", "高级球"),
    ("NestBall", "巢穴球"),
    ("SuperRod", "厉害钓竿"),
    ("BuddyBuddyPoffin", "友好宝芬"),
    ("PokemonSwitch", "宝可梦交替"),
    ("PsychicEnergy", "基本超能量"),
    ("GrassEnergy", "基本草能量"),
    ("FireEnergy", "基本火能量"),
    ("WaterEnergy", "基本水能量"),
    ("LightingEnergy", "基本雷能量"),
    ("FightingEnergy", "基本斗能量"),
    ("DarknessEnergy", "基本恶能量"),
    ("JetEnergy", "喷射能量"),
]

all_results = {}

for our_name, search_text in card_searches:
    print(f"\n=== {our_name} (search: {search_text}) ===", flush=True)
    all_cards = []
    page = 1
    while True:
        try:
            resp = session.post(API, json={"page": page, "pageSize": 10, "searchText": search_text}, timeout=15)
            data = resp.json()
            if data.get("code") != 200:
                print(f"  API error: {data}", flush=True)
                break
            cards = data["data"]["list"]
            if not cards:
                break
            all_cards.extend(cards)
            print(f"  Page {page}: +{len(cards)} (total {len(all_cards)})", flush=True)
            if len(cards) < 10:
                break
            page += 1
            time.sleep(0.2)
        except Exception as e:
            print(f"  Error: {e}", flush=True)
            break

    all_results[our_name] = all_cards
    for c in all_cards:
        print(f"    {c['setCode']}/{c['cardIndex']} - {c['cardName']} - rarity: {c.get('rarity', '?')}",
              flush=True)
    time.sleep(0.3)

# Save to file
with open("/Users/kerglixue/dev/Code/PtcgCoach/mikmoe_search_results.json", "w") as f:
    json.dump(all_results, f, ensure_ascii=False, indent=2)
print(f"\n\nSaved to mikmoe_search_results.json", flush=True)
