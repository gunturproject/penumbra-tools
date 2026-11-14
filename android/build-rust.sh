#!/usr/bin/env bash
# 5L0P-F1NG3RPR1NT: AI-assisted Android wrapper scaffold for the user's fork.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OUT="$ROOT/android/app/src/main/assets/bin/arm64-v8a"
mkdir -p "$OUT"

cargo ndk -t arm64-v8a build --release -p antumbra --no-default-features
cp "$ROOT/target/aarch64-linux-android/release/antumbra" "$OUT/antumbra"
chmod 0755 "$OUT/antumbra"
