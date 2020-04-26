---
layout: default
title: drawingboard
nav_order: 4
parent: Scarpet Apps
---

# drawingboard
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

[\[View source on GitHub\]](https://github.com/ashutoshbsathe/scarpet-nn/blob/master/scarpet-apps/drawingboard.sc)

## Global variables

### `global_pencils`
List of items that should be recognized as pencils. 

---

### `global_pos1`
First corner of bounding box of the drawingboard

---

### `global_pos2`
Second corner of bounding box of the drawingboard

---

### `global_draw_block`
Block that should be recognized as drawn on the drawingboard

---

### `global_empty_block`
Block that should be recognized as empty on the drawingboard

---

### `global_reach`
Integer that specifies the maximum distance between player and drawingboard

## Functions
### `is_between`
Takes in 3 integers `a`, `b` and `c`. Returns if `b` lies in between `a` and `c`.

---

### `within_drawing_board`
Takes a block position as argument (`b_pos`). Returns if `b_pos` lies within boundingbox of the drawingboard.

---

### `clear_board`
Clears the drawingboard by replacing all "drawn" blocks by "empty" blocks

---

## Overridden events
### `__on_player_uses_item`
Checks following things
1. The item used by player belongs to mainhand
2. The item used by player is one of the `global_pencils`
3. If the player is looking at drawingboard within proper reach

If all of the checks are successful, only then it will turn the block player is looking at to `global_draw_block`
