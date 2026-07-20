# Bible Tool

A minimal offline Bible reader for the Light Phone III, built as a Light SDK tool. v1 does one job: pick a chapter, read it, come back to where you were.

## Language

**Tool**:
A LightOS application built with this SDK. The Bible reader is one Tool.
_Avoid_: app, applet

**Translation**:
A complete, self-contained edition of the Bible text (v1 ships exactly one: KJV). The unit by which text is added to or removed from the Tool.
_Avoid_: version, edition

**Book**:
One of the 66 named divisions of the Bible (e.g. Genesis, John). The top level of navigation.

**Chapter**:
A numbered division of a Book. The unit the user reads, and the unit of text loading.

**Verse**:
A numbered line of text within a Chapter. Individually addressable in data from day one; not a navigation target in v1.

**Reader**:
The screen where a Chapter is read. The Tool boots directly into it at the Reading Position.

**Picker**:
The two-screen navigation flow (Book list → Chapter grid) reached from the Reader, used to jump to a different Chapter.
_Avoid_: table of contents, index, menu

**Verse-style Format**:
The Reader's text layout: each Verse starts on its own numbered line, making the text easy to navigate.
_Avoid_: paragraph format, prose format

**Reading Position**:
The single remembered place the user left off, at Book + Chapter granularity in v1. There is exactly one per Tool, not one per Book.
_Avoid_: bookmark, history

**Verse Anchor** *(planned, not in v1)*:
A Reading Position refined to a specific Verse, allowing relaunch at the exact verse rather than the top of the Chapter.

**Verse Search** *(planned, not in v1)*:
Finding Verses by their text content across the whole Translation.
