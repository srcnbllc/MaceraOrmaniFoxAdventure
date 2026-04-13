# Design System Strategy: The Forest Explorer’s Playbook

This document outlines the visual language and structural logic for our forest-themed adventure interface. As a junior designer, your goal is to move beyond "standard" game UI. We are not just placing buttons on a screen; we are crafting a tactile, immersive world that feels alive, premium, and intuitively navigable for a younger audience.

---

### 1. Overview & Creative North Star: "The Living Diorama"

The Creative North Star for this design system is **The Living Diorama**. 

Unlike flat, static interfaces, our UI should feel like a physical set of wooden blocks, polished river stones, and translucent leaves floating in a sun-drenched forest. We break the "template" look by avoiding rigid, full-width containers. Instead, we use **intentional asymmetry**—offsetting panels, overlapping organic shapes, and using varied typography scales to create a sense of discovery. The layout should breathe, using whitespace not as "empty" area, but as the "air" between the trees.

---

### 2. Colors: Tonal Depth & The Organic Palette

We utilize a sophisticated palette derived from forest elements, mapped to functional roles.

*   **Primary (Jungle Green - #006b1b):** Our "Life" color. Used for progress, success, and primary actions.
*   **Secondary (Safety Orange - #8c4a00):** Our "Adventure" color. High-contrast and energetic, reserved for critical calls to action.
*   **Tertiary (Golden Yellow - #6c5a00):** Our "Reward" color. Used for treasures, currency, and achievement highlights.
*   **Surface/Background (Soft Mint - #edfaee):** A gentle, eye-friendly base that prevents the visual fatigue associated with pure white.

#### The "No-Line" Rule
**Prohibit 1px solid borders for sectioning.** Boundaries must be defined solely through background color shifts or subtle tonal transitions. For example, a `surface-container-low` panel sitting on a `surface` background provides enough contrast to define a boundary without the "cheap" look of a black stroke.

#### Surface Hierarchy & Nesting
Treat the UI as a series of physical layers. 
- **Base:** `surface` (#edfaee).
- **Secondary Panels:** `surface-container` (#dcecdf).
- **Interactive Elements/Modals:** `surface-container-highest` (#cfe2d2).
This "stacking" creates a natural sense of depth, leading the child's eye to the most interactive "highest" elements.

#### The "Glass & Gradient" Rule
To elevate the "bubble" style, use **Glassmorphism** for floating HUD elements. Apply `surface-tint` at 40% opacity with a `backdrop-blur` of 12px. For buttons, use a subtle linear gradient from `primary` to `primary-container` to give them a 3D, "glossy berry" feel.

---

### 3. Typography: Playful Authority

We pair two distinct typefaces to balance whimsy with extreme legibility.

*   **Display & Headlines (Plus Jakarta Sans):** Used for titles and big celebratory moments. Its wide apertures and modern geometric curves feel friendly yet high-end. 
    *   *Scale Example:* `display-lg` (3.5rem) for Level Up screens.
*   **Body & Titles (Be Vietnam Pro):** A workhorse font with exceptional readability. It maintains the "friendly" vibe without becoming unreadable in long quest descriptions.
    *   *Scale Example:* `body-md` (0.875rem) for item descriptions.

**Hierarchy Note:** Always use high-contrast scales. If a title is `headline-lg`, the supporting text should skip a level down to `title-sm` to create clear visual "hooks" for the user.

---

### 4. Elevation & Depth: Tonal Layering

We reject traditional drop shadows in favor of **Ambient Tonal Depth**.

*   **The Layering Principle:** Place a `surface-container-lowest` (#ffffff) card on a `surface-container-low` (#e6f5e8) section. This creates a soft, tactile lift.
*   **Ambient Shadows:** For "floating" items (like the main character portrait), use a large 32px blur with only 6% opacity. The shadow color must be a tinted version of `on-surface` (#263129), never pure black. This mimics natural forest light filtered through leaves.
*   **The "Ghost Border" Fallback:** If a border is required for accessibility, use the `outline-variant` token at 15% opacity. **Never use 100% opaque borders.**
*   **Leaf Textures:** Overlay a subtle, low-opacity (5%) leaf-vein pattern on `surface-container` elements to add a signature premium texture.

---

### 5. Components: Tactile & Modular

All components are designed to be "chunky" and touch-friendly, following our **Rounding Scale** (Default: 1rem / `xl`: 3rem).

*   **Buttons (The "Bubble" Style):**
    *   **Primary:** `primary` background, `on-primary` text. Apply a 4px "bottom-heavy" shadow (Safety Orange at 20% opacity) to create a "pressable" button look.
    *   **Secondary:** `secondary-container` with a `secondary` label.
*   **Cards & Lists:** **Forbid divider lines.** Use vertical white space (Spacing `6`: 2rem) or a subtle shift from `surface-container` to `surface-container-high` to separate items.
*   **Input Fields:** Use `surface-container-lowest` with a "Ghost Border." When focused, transition the border to `primary` at 40% opacity and add a subtle inner glow.
*   **Tooltips:** Treat these as "Floating Leaves." Use `tertiary-container` (#ffd709) with a `full` (9999px) corner radius.
*   **Adventure Panels (Custom Component):** A wooden-textured container using `secondary-container`. Instead of a flat box, use an asymmetrical "hand-carved" shape with the `xl` (3rem) rounding scale on three corners and `sm` (0.5rem) on one.

---

### 6. Do’s and Don’ts

#### Do:
*   **Use the Spacing Scale:** Stick strictly to the increments (e.g., `spacing-4` for padding, `spacing-8` for section gaps) to ensure rhythmic harmony.
*   **Embrace Asymmetry:** Offset your panels by a few pixels or use varied corner radii to make the UI feel "hand-crafted."
*   **Layer for Importance:** The most important button should be the "highest" in the tonal stack (most contrast).

#### Don’t:
*   **No "Pure" Grays:** Every neutral should be tinted with a hint of green or blue to keep the forest atmosphere alive.
*   **No Sharp Corners:** This is a kid-friendly world. Even "square" panels must use at least the `md` (1.5rem) rounding token.
*   **No Crowding:** If a screen feels busy, increase the spacing from `4` to `6`. Avoid the temptation to shrink text to fit more "stuff."