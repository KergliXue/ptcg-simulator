#!/usr/bin/env python3
"""Query for cards that weren't found with initial search terms."""
import json, time, requests

API = "https://tcg.mik.moe/api/v3/card/card-basic-search"
session = requests.Session()
session.headers.update({"Content-Type": "application/json"})

# Try different search terms
searches = [
    ("钓竿", "SuperRod"),
    ("神奇糖果", "RareCandy"),
    ("宝芬", "BuddyBuddyPoffin"),
    ("朋友手册", "BuddyBuddyPoffin2"),
    ("能量回收", "EnergyRetrieval"),
    ("交替", "Switch"),
    ("巢穴", "NestBall2"),
]

for search_text, our_name in searches:
    print(f"\n=== {our_name} (search: {search_text}) ===")
    try:
        resp = session.post(API, json={"page": 1, "pageSize": 5, "searchText": search_text}, timeout=15)
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
