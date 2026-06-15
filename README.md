# heve

A bread-baking companion for Android. *Heve* is Norwegian for "to rise".

## 🚧 Coming soon

Heve does three things in one flow:

1. **Recipe** - calculate a dough by baker's percentages. Set a target
   weight and dial in hydration, salt, and yeast against guideline ranges;
   the ingredient weights update live.
2. **Dough journey** - compose a bake by dragging step blocks (knead, proof,
   fold, preshape, shape, bake) into any order, each with its own timing. A
   four-proof sourdough is as easy to build as a weeknight loaf.
3. **Live bake guide** - walk through each step in real time, with timers and
   notifications so you never miss a fold.

Plus a library of saved recipes and reusable journeys, with one-tap resume
for a bake already in progress.

## Tech

- Kotlin + Jetpack Compose (Material 3, custom theme)
- Single-activity, Compose Navigation
- Room for saved recipes, journeys, and in-progress bake state
- WorkManager + notifications for bake-step timers

## Status

Early development - building it out screen by screen.

- [x] Theme foundation
- [x] Shared UI components
- [ ] Recipe screen + baker's percentage calculator
- [ ] Dough journey builder (drag to reorder)
- [ ] Library
- [ ] Live bake guide
- [ ] Persistence + timers
