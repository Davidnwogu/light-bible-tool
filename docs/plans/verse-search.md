# Plan: Verse Search (post-v1)

Find Verses by their text content across the whole Translation, fully offline. Results jump into the Reader at the matched verse. See `CONTEXT.md` for terms.

**Depends on: Verse Anchor** (`docs/plans/verse-anchor.md`) — jumping to a result *is* opening the Reader at a Verse Anchor. Ship that first.

## Search strategy: staged, measure before indexing

The KJV is static, ~31k verses / ~4.5MB. Two candidate engines, in order:

### Stage 1 (build this): linear scan over the chapter resources

- Normalize the query (lowercase, collapse whitespace); scan every chapter JSON via the existing classloader loader, matching case-insensitive substring per verse.
- Run on `Dispatchers.IO`, streaming results to the UI as they're found (Genesis-first order), cancellable when the query changes.
- No new dependencies, no index build, no schema. The corpus never changes, so correctness is trivial.
- **Measure on real LPIII hardware.** If a full scan returns in well under ~2s, stop here — this is the whole feature.

### Stage 2 (only if Stage 1 is too slow): SQLite FTS via Room

- Room is already a sanctioned dependency (`tool/build.gradle.kts` ships `ksp(room.compiler)`).
- On first search (not first launch — don't tax boot), import all verses from the classpath resources into a Room FTS4 table (`book`, `chapter`, `verse`, `text`), flagging completion in `dataStore`. Query with `MATCH` thereafter.
- Costs: one-time import pause, a second copy of the text on disk, word-boundary semantics instead of substring. Only pay this if measurement says so.

## UI

- **Search screen**: a new `LightScreen` reached from the Reader (entry point alongside the Picker control). `LightTextField` for input; results as a scrolling list.
- **Result row**: reference (`John 3:16`) + verse text, query match emphasized. Verse-style Format keeps rows uniform.
- **Tap a result**: `goBack` to the Reader with the target as the result payload; Reader loads that chapter and applies the verse as its scroll target (same mechanism as Verse Anchor restore). The Reading Position updates to the destination — search *moves* you; back-bar returns through the stack as usual.
- Empty query shows nothing; no results shows a plain "No verses found." No search history in this version.

## Persistence

None. Search is stateless; only the (existing) Reading Position changes when a result is opened. (Stage 2 adds the import-complete flag.)

## Glossary

Promote **Verse Search** in `CONTEXT.md` from *(planned)* to a live term when built.

## Explicitly out of scope

- Reference parsing ("John 3:16" as a query) — worth considering, but it's *navigation*, not search; if wanted, it becomes its own small feature on top of the Picker.
- Ranking/relevance — results stay in canonical book order.
- Highlighting/bookmarking results.

## Test plan

- Unit: normalization, cancellation on query change, reference formatting, result → anchor payload mapping.
- Perf: scripted full-corpus scan timing on device; this measurement decides Stage 2.
- Manual: search "for God so loved", tap the John 3:16 result, land in Reader at that verse; back returns to results.
