# Spec: Bible Tool v1 — minimal offline Reader

The agreed, grilled-and-confirmed scope for the first version. Terms are defined in `CONTEXT.md`; the data-layer rationale is `docs/adr/0001-kjv-as-classpath-resources.md`. Deferred features have their own plans (`verse-anchor.md`, `verse-search.md`) — v1 must not preclude them but builds none of them.

## Scope

A minimal offline reader: pick a chapter, read it, resume where you left off. No search, no bookmarks, no settings, no notifications, no network.

## Text

- **KJV only**, bundled as per-chapter JSON: `tool/src/main/resources/kjv/<book>/<chapter>.json`, each file holding the verse array for one Chapter.
- Read at runtime with `ClassLoader.getResourceAsStream(...)` on `Dispatchers.IO` — verified working in the built APK (see ADR-0001). Do **not** use Android `assets/` or `LocalContext` (lint-banned).
- A translation is a self-contained directory (`kjv/`), so adding another later is additive.
- First implementation step: source a public-domain KJV dataset and generate the 1,189 chapter files plus a books manifest (order, chapter counts, OT/NT grouping).
- **All text access goes through a `ChapterRepository`** (`suspend getChapter(book, chapter): Chapter`) — the only class that knows text comes from the classloader. Screens/ViewModels depend on the interface. This keeps the storage decision reversible (see `docs/plans/runtime-download-alternative.md` for the held-in-reserve runtime-download implementation). The books manifest is parsed once into a small catalog object, separate from the repository.

## Screens (exactly three, all SDK-native `LightScreen`/`LightScreenViewModel` + `navigateTo`)

1. **Reader** — the `@InitialScreen`. Boots at the Reading Position; Genesis 1 on first launch (never opens to a decision). Vertical scroll (`LazyColumn`). Verse-style Format: one verse per item, small leading verse number. Fixed `LightTheme` typography (`paragraph` for verse text) — no font-size controls. Previous/Next chapter controls at the chapter ends; sequential advance is continuous across book boundaries (Malachi 4 → Matthew 1). A control (e.g. chapter title) opens the Picker.
2. **Book list** — 66 books with OT/NT section headers; the book containing the current Reading Position is marked. Tapping a book pushes the Chapter grid.
3. **Chapter grid** — `LightGrid` of chapter numbers. Picking one returns to the Reader at that chapter (bubble the choice back via `navigateTo` result callbacks). SDK back bar handles all back behavior.

## Persistence

One `dataStore` (`DEFAULT_DATASTORE`) record: the Reading Position — `position.book` + `position.chapter` — written on chapter change. Chapter-top restore only; exact-verse restore is the Verse Anchor plan.

## Where it lives

In this repo's `tool/` module, replacing the sample `HomeScreen`/`DetailScreen`, per the SDK README workflow.

## Build environment notes

- Build with **JDK 17** (`/opt/homebrew/Cellar/openjdk@17/...` on this machine). JDK 26 breaks the Android `JdkImageTransform`/jlink step. Pass `-Dorg.gradle.java.home=...` or set `JAVA_HOME`.
- GitHub Packages token required for SDK artifacts (see root README quickstart).
- Test on the LightOS emulator profile: 1080×1240, 3.92", API 34, no Play Services.
