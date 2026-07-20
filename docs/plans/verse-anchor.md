# Plan: Verse Anchor (post-v1)

Refine the Reading Position from Book + Chapter to Book + Chapter + Verse, so relaunching the Tool lands on the exact verse the user was reading instead of the top of the chapter. See `CONTEXT.md` for the terms used here.

## Why this is cheap after v1

v1's design already does the hard part:

- **Verse-style Format** renders one verse per `LazyColumn` item, so "the verse the user is looking at" is just `LazyListState.firstVisibleItemIndex` — no text measurement or pixel offsets.
- Verses are already an addressable array in each chapter's JSON; no data change at all.

## Changes

### 1. Persistence (`dataStore`)

Add an optional verse number alongside the existing Reading Position keys:

- v1 stores `position.book` + `position.chapter`.
- Add `position.verse: Int?`. **Absent/null means "top of chapter"** — this makes the change backward-compatible with positions written by v1; no migration needed.

### 2. Capturing the anchor (Reader ViewModel)

- Observe the Reader's `LazyListState`. The anchor is the verse index of the first *fully* visible item (skip any non-verse header items — subtract the header offset).
- Write on two triggers:
  - `willHide` / `onScreenDestroy` lifecycle hooks (the must-not-miss write).
  - Scroll settle, debounced (~1s after `isScrollInProgress` goes false), so a crash/kill still loses at most the last scroll.
- Chapter changes keep their v1 write and reset `position.verse` to null.

### 3. Restoring the anchor (Reader)

- On boot, after the chapter loads, if `position.verse` is set, initialize the list with `initialFirstVisibleItemIndex = verseIndex + headerOffset` (or `scrollToItem` post-composition). No animation — the user should simply *be* there.
- If the stored verse exceeds the chapter's verse count (corrupt/stale data), clamp to top of chapter.

### 4. Glossary

Promote **Verse Anchor** in `CONTEXT.md` from *(planned)* to a live term; fold its meaning into **Reading Position** ("refined to a Verse when available").

## Explicitly out of scope

- Multiple saved positions / bookmarks — Reading Position stays singular.
- Anchoring UI (e.g. "jump to verse" input) — this feature only changes what is *remembered*, not navigation. Jump-to-verse arrives with Verse Search.

## Test plan

- Unit: anchor write on hide; debounced write on scroll settle; null-verse fallback; clamp on out-of-range verse.
- Manual on device/emulator: read to Psalm 119:100, kill the tool, relaunch → land at v100.
