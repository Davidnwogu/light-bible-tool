# Alternative: runtime-downloaded text (not currently chosen)

The documented fallback to ADR-0001, held in reserve in case Light's review prefers tools not to bundle the ~4.5MB KJV, or repo size becomes a real constraint. **Do not build this unless that happens.** Because all text access in v1 goes through `ChapterRepository`, adopting this plan replaces one implementation class; the Reader, Picker, persistence, and deferred-feature plans are untouched.

## Architecture

- **Ship no text.** The APK contains only code and the books manifest.
- **`ChapterRepository` implementation**: `getChapter(book, chapter)` checks `filesDir/kjv/<book>/<n>.json`; on miss, fetches that single chapter over Ktor (sanctioned — see the weather example), verifies its checksum against the manifest, writes it to `filesDir`, returns it.
- **Lazy-first**: first launch fetches only the Reading Position's chapter (a few KB). The Reader is usable immediately given any connectivity.
- **Prefetch-behind**: a `@LightJob("sync-kjv")` background job downloads the remaining chapters when the system allows (Wi-Fi/idle). Once complete — flagged in `dataStore` — the tool is permanently offline. Transfer may be batched (per book, or one archive) behind the same per-chapter interface.
- **Hosting**: versioned release artifacts of this public repo (per-chapter JSON + a checksum manifest), pinned by tag. Immutable, free, consistent with Light's open-source requirement.

## Costs this introduces (why it isn't the default)

- A first-run **network requirement** and a failure state in the core screen ("Connect to download this chapter") that cannot exist in the bundled design.
- A permanent **hosting dependency** and integrity/checksum handling.
- More moving parts to save a one-time ~4.5MB commit of a text that never changes.

## Trigger to adopt

Light's tool review objects to the bundled text, or Light confirms a preferred download mechanism for large static assets. Revisit ADR-0001 at that point and mark it superseded.
