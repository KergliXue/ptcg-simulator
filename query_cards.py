#!/usr/bin/env python3
"""Query mik.moe basic-search API for each card we need."""
import json, time, requests

API = "https://tcg.mik.moe/api/v3/card/card-basic-search"
session = requests.Session()
session.headers.update({"Content-Type": "application/json"})

cards_to_find = [
    # Pokemon (Chinese names from mik.moe)
    ("愿增猿", "Munkidori"),
    ("拉鲁拉丝", "Ralts"),
    ("奇鲁莉安", "Kirlia"),
    ("沙奈朵ex", "GardevoirEx"),
    # Trainers (Chinese names)
    ("高级球", "UltraBall"),
    ("巢穴球", "NestBall"),
    ("超级钓竿", "SuperRod"),
    ("朋友宝芬", "BuddyBuddyPoffin"),
    ("宝可梦交替", "PokemonSwitch"),
    # Energies (Chinese names)
    ("基本超能量", "PsychicEnergy"),
    ("基本草能量", "GrassEnergy"),
    ("基本火能量", "FireEnergy"),
    ("基本水能量", "WaterEnergy"),
    ("基本雷能量", "LightingEnergy"),
    ("基本斗能量", "FightingEnergy"),
    ("基本恶能量", "DarknessEnergy"),
    ("喷射能量", "JetEnergy"),
]

for search_text, our_name in cards_to_find:
    print(f"\n=== {our_name} (search: {search_text}) ===")
    try:
        resp = session.post(API, json={"page": 1, "pageSize": 10, "searchText": search_text}, timeout=15)
        data = resp.json()
        if data.get("code") != 200:
            print(f"  API error: {data}")
            continue
        cards = data["data"]["list"]
        print(f"  Found {len(cards)} results")
        for c in cards[:5]:
            print(f"  {c['setCode']}/{c['cardIndex']} - {c['cardName']} - rarity: {c.get('rarity', '?')}")
    except Exception as e:
        print(f"  Error: {e}")
    time.sleep(0.2)
