# heve

A bread-baking companion for Android. *Heve* is Norwegian for "to rise".

## What it does

Heve walks you through a bake in three steps:

1. **Recipe** - set a target dough weight and dial in hydration, salt, and yeast as baker's percentages. Ingredient weights update live.
2. **Journey** - build your bake sequence by adding step blocks (knead, proof, fold, preshape, shape, bake) and dragging them into order, each with its own duration.
3. **Bake** - follow each step in real time with a live countdown and progress through the sequence.

Recipes are saved to a library and can be edited or resumed at any time. If a bake is already in progress, the home screen shows a resume card.

## Tech

- Kotlin + Jetpack Compose (Material 3, custom theme)
- Single-activity with a manual sealed-class back stack
- SharedPreferences + org.json for persistence
- Custom fonts: Bricolage Grotesque (display), Hanken Grotesk (body)

## Status

- [x] Theme + shared UI components
- [x] Recipe screen: baker's percentage calculator
- [x] Journey builder: drag-to-reorder step sequence
- [x] Live bake guide
- [x] Recipe library with save, edit, and delete
- [x] Active bake persistence and resume
- [x] App icon
- [x] Step timers and background notifications
- [x] Flour type selector with dynamic hydration guidance
