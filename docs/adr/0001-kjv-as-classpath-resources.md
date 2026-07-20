# KJV text is bundled as classpath resources, not Android assets

Light SDK tools cannot reach an Android `Context` — `SealedLightContext` exposes only `dataStore`/`filesDir`/`fileShare`, and the lint rules ban `LocalContext.current` — so the standard `assets/` mechanism is unreachable from tool code. We bundle the KJV as per-chapter JSON files under `tool/src/main/resources/kjv/<book>/<chapter>.json` and read them with `ClassLoader.getResourceAsStream("kjv/<book>/<chapter>.json")`, which needs no `Context` and is packaged at the APK root (verified in a built APK and a passing unit test, 2026-07-18).

## Considered Options

- **Android `assets/`** — unreachable: no `Context` is exposed to tool code.
- **Generate Kotlin source from JSON at build time** — works, but ~4.5MB of string literals compiles slowly, bloats the DEX string pool, and pollutes review diffs.
- **Download on first run into `filesDir`** — keeps the APK and repo small but breaks "offline from first launch" and requires hosting the text. **Kept as the documented fallback** (see `docs/plans/runtime-download-alternative.md`) in case Light's review prefers tools not to bundle multi-MB texts.
- **Build-time fetch in a Gradle task** — smallest repo with an unchanged APK, but depends on Light's build server allowing network during builds (unverified) and means the archived source no longer contains the shipped text.
- **Classpath resources (chosen)** — offline, self-contained, sandbox-legal, no codegen, plain data files in the repo.

## Consequences

Chapter loads do blocking IO via the classloader, so reads happen on `Dispatchers.IO`. All text access goes through a **`ChapterRepository`** seam (`getChapter(book, chapter)`) — the Reader and future features never touch the classloader directly. This makes the storage decision reversible: swapping to the runtime-download fallback (or a future sanctioned asset API) replaces one implementation class and nothing else.
