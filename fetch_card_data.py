#!/usr/bin/env python3
"""
Fetch card data from tcg.mik.moe API for all known sets.
Generates card_metadata SQL insert statements.

Usage: python3 fetch_card_data.py > card_data.sql
"""

import json
import time
import requests
from collections import defaultdict

MIKMOE_DETAIL = "https://tcg.mik.moe/api/v3/card/card-detail"
MIKMOE_IMG = "https://tcg.mik.moe/static/img"

session = requests.Session()
session.headers.update({"Content-Type": "application/json"})


def fetch_card_detail(set_code: str, card_index: str) -> dict | None:
    """Fetch detail for a single card."""
    try:
        resp = session.post(MIKMOE_DETAIL, json={
            "setCode": set_code,
            "cardIndex": card_index,
        }, timeout=15)
        data = resp.json()
        if data.get("code") == 200:
            return data["data"]
    except Exception as e:
        print(f"    Error: {e}")
    return None


def probe_set_cards(set_code: str) -> list[str]:
    """Probe image URLs to find all card numbers in a set. Returns list of card indices."""
    cards = []
    # Check if first card exists
    url = f"{MIKMOE_IMG}/{set_code}/001.png"
    try:
        if session.head(url, timeout=5).status_code != 200:
            return []
    except Exception:
        return []

    # Binary search to find max card number
    lo, hi = 1, 300
    while lo < hi:
        mid = (lo + hi + 1) // 2
        padded = f"{mid:03d}"
        url = f"{MIKMOE_IMG}/{set_code}/{padded}.png"
        try:
            if session.head(url, timeout=5).status_code == 200:
                lo = mid
            else:
                hi = mid - 1
        except Exception:
            hi = mid - 1
        time.sleep(0.02)

    max_num = lo
    for n in range(1, max_num + 1):
        padded = f"{n:03d}"
        url = f"{MIKMOE_IMG}/{set_code}/{padded}.png"
        try:
            if session.head(url, timeout=5).status_code == 200:
                cards.append(padded)
        except Exception:
            pass

    return cards


def main():
    # Sets we care about (Chinese SV)
    sets = [
        "CSV1C", "CSV2C", "CSV3C", "CSV4C", "CSV5C",
        "CSV6C", "CSV7C", "CSV8C", "CSV9C",
        "CS1aC", "CS2aC", "CS3aC", "CS4aC", "CS5aC",
        "CS6aC", "CS7aC", "CS8aC", "CS9aC",
        "CS1bC", "CS2bC", "CS3bC", "CS4bC", "CS5bC",
        "CSPC", "CSVNC",
    ]

    all_cards = {}

    for set_code in sets:
        print(f"\n📦 Probing set: {set_code}...", file=__import__('sys').stderr)
        indices = probe_set_cards(set_code)
        if not indices:
            print(f"  No cards found, skipping", file=__import__('sys').stderr)
            continue

        print(f"  Found {len(indices)} cards, fetching details...", file=__import__('sys').stderr)
        set_cards = []
        for i, card_index in enumerate(indices):
            if (i + 1) % 50 == 0:
                print(f"    Progress: {i+1}/{len(indices)}", file=__import__('sys').stderr)

            detail = fetch_card_detail(set_code, card_index)
            if detail:
                set_cards.append(detail)
            time.sleep(0.05)

        all_cards[set_code] = set_cards
        print(f"  Done: {len(set_cards)} cards", file=__import__('sys').stderr)

    # Output SQL
    print("-- PTCG Card Metadata - fetched from tcg.mik.moe")
    print("-- Auto-generated, do not edit manually")
    print()
    print("DELETE FROM card_metadata WHERE card_key LIKE 'CSV%' OR card_key LIKE 'CS%aC' OR card_key LIKE 'CS%bC' OR card_key LIKE 'CSPC%' OR card_key LIKE 'CSVNC%';")
    print()

    for set_code, cards in sorted(all_cards.items()):
        if not cards:
            continue
        print(f"-- Set: {set_code} ({len(cards)} cards)")
        print()

        for c in cards:
            card_key = f"{c['setCode']}-{c['cardIndex']}"
            name = c['name']
            rarity = c.get('rarity', 'C')
            image_url = f"https://tcg.mik.moe/static/img/{c['setCode']}/{c['cardIndex']}.png"
            desc = c.get('description', '')
            desc = desc.replace("'", "''")

            print(f"INSERT INTO card_metadata (card_key, name, set_code, set_number, rarity, image_url, description) VALUES (")
            print(f"  '{card_key}', '{name}', '{c['setCode']}', '{c['cardIndex']}', '{rarity}', '{image_url}', '{desc}'")
            print(f");")

    # Also output JSON for reference
    import json as j
    with open("mikmoe_card_data.json", "w") as f:
        j.dump(all_cards, f, ensure_ascii=False, indent=2)
    print(f"\n-- JSON data saved to mikmoe_card_data.json", file=__import__('sys').stderr)


if __name__ == "__main__":
    main()
