#!/usr/bin/env python3
"""
Fetch the full PTCG Chinese card database from tcg.mik.moe API and
export all card data + download card images.

Usage:
  python3 fetch_mikmoe.py              # Just fetch card list to JSON
  python3 fetch_mikmoe.py --download   # Fetch + download all images
"""

import os
import sys
import json
import time
import requests
from pathlib import Path
from collections import defaultdict

PROJECT_ROOT = Path(__file__).resolve().parent
ASSETS_DIR = PROJECT_ROOT / "assets"
CARD_DATA_FILE = PROJECT_ROOT / "mikmoe_cards.json"

MIKMOE_API = "https://tcg.mik.moe/api/v3/card/card-advance-search"
MIKMOE_IMG = "https://tcg.mik.moe/static/img"

# All known Chinese SV set codes on mik.moe
SET_CODES = [
    "CSV1C", "CSV2C", "CSV3C", "CSV4C", "CSV5C",
    "CSV6C", "CSV7C", "CSV8C", "CSV9C",
    "CS1aC", "CS2aC", "CS3aC", "CS4aC", "CS5aC",
    "CS6aC", "CS7aC", "CS8aC", "CS9aC",
    "CS1bC", "CS2bC", "CS3bC", "CS4bC", "CS5bC",
    "CSPC", "CSVNC",
]

session = requests.Session()
session.headers.update({"Content-Type": "application/json"})


def fetch_cards_for_set(set_code: str) -> list[dict]:
    """Fetch all cards for a given set code, paginating as needed."""
    cards = []
    page = 1
    page_size = 200

    while True:
        payload = {
            "unique": False,
            "page": page,
            "pageSize": page_size,
            "m": [],
            "s": [set_code],
            "label": [],
            "series": [],
        }
        try:
            resp = session.post(MIKMOE_API, json=payload, timeout=30)
            data = resp.json()
            if data.get("code") != 200:
                print(f"  API error for {set_code} page {page}: {data}")
                break

            batch = data["data"]["list"]
            if not batch:
                break

            cards.extend(batch)
            print(f"  Page {page}: +{len(batch)} cards (total {len(cards)})")

            if len(batch) < page_size:
                break

            page += 1
            time.sleep(0.2)  # Be polite to the API
        except Exception as e:
            print(f"  Error fetching {set_code} page {page}: {e}")
            break

    return cards


def download_image(set_code: str, card_index: str) -> bool:
    """Download a single card image to assets/{set_code}/{card_index}.png"""
    # Pad to 3 digits
    padded = card_index.zfill(3)
    url = f"{MIKMOE_IMG}/{set_code}/{padded}.png"

    local_dir = ASSETS_DIR / set_code
    local_dir.mkdir(parents=True, exist_ok=True)
    local_path = local_dir / f"{padded}.png"

    if local_path.exists():
        return True  # already downloaded

    try:
        resp = requests.get(url, timeout=10)
        if resp.status_code == 200:
            local_path.write_bytes(resp.content)
            return True
        else:
            return False
    except Exception:
        return False


def main():
    import argparse
    parser = argparse.ArgumentParser()
    parser.add_argument("--download", action="store_true", help="Download card images")
    args = parser.parse_args()

    all_cards = []

    print("Fetching card database from tcg.mik.moe...")
    for set_code in SET_CODES:
        print(f"\n📦 Set: {set_code}")
        cards = fetch_cards_for_set(set_code)
        print(f"  Total: {len(cards)} cards")
        all_cards.extend(cards)
        time.sleep(0.3)

    # Save card data to JSON
    print(f"\n{'='*50}")
    print(f"Total cards: {len(all_cards)} across {len(SET_CODES)} sets")

    # Group by set for easier viewing
    by_set = defaultdict(list)
    for c in all_cards:
        by_set[c["setCode"]].append(c)

    export = {
        "total": len(all_cards),
        "sets": {sc: len(cards) for sc, cards in sorted(by_set.items())},
        "cards": all_cards,
    }

    CARD_DATA_FILE.write_text(json.dumps(export, ensure_ascii=False, indent=2))
    print(f"Card data saved to: {CARD_DATA_FILE}")

    # Download images
    if args.download:
        print(f"\n{'='*50}")
        print("Downloading card images...")
        success = 0
        fail = 0
        for i, card in enumerate(all_cards):
            sc = card["setCode"]
            ci = card["cardIndex"]
            if (i + 1) % 100 == 0:
                print(f"  Progress: {i+1}/{len(all_cards)} (ok:{success} fail:{fail})")

            if download_image(sc, ci):
                success += 1
            else:
                fail += 1

            time.sleep(0.05)  # Rate limiting

        print(f"\nDone: {success} downloaded, {fail} failed")
        print(f"Images saved to: {ASSETS_DIR}")

    # Print set summary
    print(f"\n{'='*50}")
    print("Set summary:")
    for sc in SET_CODES:
        if sc in by_set:
            cards = by_set[sc]
            sample = cards[0]
            print(f"  {sc:8s}: {len(cards):4d} cards  (e.g. {sample['cardIndex']} - {sample['cardName']})")


if __name__ == "__main__":
    main()
