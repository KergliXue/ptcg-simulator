#!/usr/bin/env python3
"""
Download PTCG card images from tcg.mik.moe to assets/{setCode}/{number}.png

Each set has cards numbered 001-NNN. This script tries to download cards
for each known set code, stopping when it hits 404 for that set.
"""

import os
import requests
from concurrent.futures import ThreadPoolExecutor, as_completed
from pathlib import Path

PROJECT_ROOT = Path(__file__).resolve().parent
ASSETS_DIR = PROJECT_ROOT / "assets"
MIKMOE_IMG = "https://tcg.mik.moe/static/img"

# Known Chinese SV sets on mik.moe
SETS = [
    "CSVSC",
]

session = requests.Session()


def try_download(set_code: str, number: int) -> bool:
    """Try to download one card. Returns True if downloaded (or already exists)."""
    num_str = f"{number:03d}"
    url = f"{MIKMOE_IMG}/{set_code}/{num_str}.png"

    local_dir = ASSETS_DIR / set_code
    local_dir.mkdir(parents=True, exist_ok=True)
    local_path = local_dir / f"{num_str}.png"
    if local_path.exists():
        return True

    try:
        resp = session.get(url, timeout=10)
        if resp.status_code == 200:
            local_path.write_bytes(resp.content)
            return True
    except Exception:
        pass
    return False


def download_set(set_code: str, max_workers: int = 10):
    """Download all cards for a set. Try numbers 1-300 (most sets have <200 cards)."""
    print(f"\n📦 {set_code}...")

    # First, quickly find the max card number by probing
    max_num = 0
    for probe in [100, 150, 200, 250]:
        padded = f"{probe:03d}"
        url = f"{MIKMOE_IMG}/{set_code}/{padded}.png"
        try:
            if session.head(url, timeout=5).status_code == 200:
                max_num = probe
        except Exception:
            pass

    if max_num == 0:
        # Try from 1 upward to find max
        for n in range(1, 300):
            padded = f"{n:03d}"
            url = f"{MIKMOE_IMG}/{set_code}/{padded}.png"
            try:
                if session.head(url, timeout=5).status_code == 200:
                    max_num = max(max_num, n)
            except Exception:
                pass
            if n > 10 and max_num == 0:
                print(f"  No cards found, skipping")
                return 0, 0

    # Now download all cards 1..max_num (with some buffer)
    end = max(10, max_num + 10)
    tasks = list(range(1, end + 1))

    ok = 0
    miss = 0

    with ThreadPoolExecutor(max_workers=max_workers) as pool:
        futures = {pool.submit(try_download, set_code, n): n for n in tasks}
        for f in as_completed(futures):
            if f.result():
                ok += 1
            else:
                miss += 1

    # Print progress
    total = ok + miss
    has = ok + sum(
        1 for n in range(1, end + 1)
        if (ASSETS_DIR / set_code / f"{n:03d}.png").exists()
    )
    print(f"  {ok} downloaded, {miss} not found (have {has}/{end} files)")

    # Clean up: remove files that are too small (error pages)
    for f in (ASSETS_DIR / set_code).glob("*.png"):
        if f.stat().st_size < 1000:
            f.unlink()
            print(f"  Removed tiny file: {f.name}")

    return ok, miss


def main():
    import argparse
    parser = argparse.ArgumentParser()
    parser.add_argument("--set", help="Download only specific set")
    parser.add_argument("--workers", type=int, default=10, help="Parallel downloads")
    args = parser.parse_args()

    sets_to_dl = [args.set] if args.set else SETS

    total_ok = 0
    total_miss = 0
    for set_code in sets_to_dl:
        ok, miss = download_set(set_code, args.workers)
        total_ok += ok
        total_miss += miss

    print(f"\n{'=' * 50}")
    print(f"Done: {total_ok} downloaded, {total_miss} not found")
    print(f"Images in: {ASSETS_DIR}")


if __name__ == "__main__":
    main()
