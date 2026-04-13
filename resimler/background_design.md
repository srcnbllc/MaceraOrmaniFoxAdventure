# Design System: Atmospheric Immersion

## 1. Overview & Creative North Star
The Creative North Star for this system is **"The Luminous Sanctuary."** This design system moves away from flat, functional layouts toward a high-fidelity, editorial experience that feels like a window into another world. We are not just building backgrounds; we are constructing "living atmospheres" that breathe through parallax depth, intentional asymmetry, and a sophisticated interplay of shadow and neon luminescence.

By utilizing extreme corner radiuses (**3rem+ / xl**) and eliminating traditional structural lines, the system creates an organic, premium aesthetic. The goal is to make the user feel like a curator of an digital ecosystem, where every transition is a shift in weather and every surface is a layer of fog or crystalline rock.

---

## 2. Colors: The Crystal Cave Palette
The palette is rooted in a "Crystal Cave" high-contrast philosophy. It utilizes deep, abyssal shadows to make neon accents pop with an otherworldly glow.

### Tonal Foundation
- **Primary & Neon Accents:** Use `primary` (#b5ffc2) and `primary_container` (#3fff8b) for life-giving forest glows. For magical elements, deploy the `tertiary` (#f8acff) and `secondary` (#ffb778) tokens to represent violet flora and embers.
- **Deep Abyss (Neutrals):** The `surface` (#031013) and `surface_container` tiers are your void. They provide the "canvas" for the glowing elements to sit upon.

### The "No-Line" Rule
**Explicit Instruction:** Do not use 1px solid borders to define sections. Sectioning is achieved through color-blocking or tonal shifts. For example, a `surface_container_low` section should sit directly on a `surface` background. The change in hex value is the boundary.

### The "Glass & Gradient" Rule
Floating elements must utilize the **Glassmorphism** principle.
- **Backdrop:** Use `surface_variant` at 40-60% opacity with a `backdrop-blur` of 20px-40px.
- **Gradients:** Main interactive surfaces should use subtle linear gradients (e.g., `primary` to `primary_dim`) to provide a "liquid" soul that flat fills lack.

---

## 3. Typography: Editorial Clarity
We use **Plus Jakarta Sans** as our structural backbone. It is a clean, modern sans-serif that balances the "wild" nature of the forest with professional precision.

- **Display Scale:** `display-lg` (3.5rem) and `display-md` (2.75rem) are reserved for environmental titles. These should have generous letter-spacing (-0.02em) to feel premium.
- **Headline & Title:** `headline-lg` (2rem) and `title-lg` (1.375rem) provide hierarchy. Use these for section labels within the environment.
- **Body & Labels:** `body-lg` (1rem) is the workhorse for readability. `label-sm` (0.6875rem) is used for technical metadata or coordinate references.

---

## 4. Elevation & Depth: The Layering Principle
Standard drop shadows are prohibited. Depth in this system is a physical property of light and atmosphere.

- **Tonal Stacking:** Create depth by stacking surface tiers. A `surface_container_highest` element naturally feels "closer" to the viewer than a `surface_container_lowest` base.
- **Ambient Shadows:** For floating elements, use extra-diffused shadows.
    - **Blur:** 60px to 100px.
    - **Opacity:** 4% to 8%.
    - **Color:** Use a tinted version of `on_surface` (#cdebf2) to simulate light scattering rather than a "dirty" grey shadow.
- **The "Ghost Border" Fallback:** If containment is visually necessary, use `outline_variant` (#314c52) at **15% opacity**. It should be felt, not seen.
- **Parallax Mapping:** Environments must be designed in at least four layers:
    1. **Foreground (Blur):** Elements like large leaves or glowing crystals using `primary_fixed` with heavy blur.
    2. **Active Plane:** The sharpest layer for primary focus.
    3. **Mid-ground:** Atmospheric fog and silhouettes using `surface_bright`.
    4. **Deep Background:** The "Skybox" or "Cavern Roof" using `surface_container_lowest`.

---

## 5. Components: Integrated Organics
Components should feel like they were carved out of the environment, not placed on top of it.

*   **Buttons:**
    *   **Primary:** A gradient fill from `primary` to `primary_container`. No border. Corner radius: `full`.
    *   **Secondary:** Glassmorphic fill (semi-transparent `surface_variant`) with a "Ghost Border."
*   **Chips:** Use `secondary_container` for a warm, amber-like selection state. Roundness must be `full`.
*   **Input Fields:** Utilize `surface_container_highest` for the field background. Labels (`label-md`) should be placed with generous top-padding (`spacing-4`) to ensure editorial breathing room.
*   **Cards:** Forbid divider lines. Separate content using the **Spacing Scale** (e.g., `spacing-8` or `spacing-10`) or subtle background shifts between `surface_container_low` and `surface_container_high`.
*   **Environmental Orbs (Custom Component):** Circular indicators using `tertiary_fixed` with an outer glow (box-shadow) that pulses to indicate "magical" points of interest within the forest.

---

## 6. Do's and Don'ts

### Do:
*   **Use Asymmetry:** Place glowing elements off-center to create a natural, "wild" feel.
*   **Lean into 3rem+ Roundness:** The `xl` roundness token is the signature of this system. Use it on all major containers.
*   **Respect the "No-Line" Rule:** Rely on the `surface` hierarchy tokens for separation.
*   **Maximize Spacing:** Use `spacing-16` and `spacing-20` to give the environment "air."

### Don't:
*   **Don't use 100% Black:** Always use the `surface` token (#031013) to maintain depth and allow the "Crystal Cave" colors to glow.
*   **Don't use 1px Solid Borders:** These break the immersion of an organic forest.
*   **Don't use Harsh Shadows:** If a shadow looks like a "drop shadow," it is too dark and too small.
*   **Don't Over-Clutter:** The background is the hero. Ensure that any overlaid elements (when used in-app) do not obscure the glowing focal points.

---

## 7. Environmental Themes Reference
Designers should apply the color tokens differently based on the theme while maintaining the same structural rules:
- **Lava Island:** High use of `secondary_dim` (#fd9000) and `error_container`.
- **Ice Temple:** Heavily utilize `on_surface` (#cdebf2) and `surface_bright` with high transparency.
- **Cosmic Planet:** Deepest use of `surface_container_lowest` with `tertiary` (#f8acff) neon accents.