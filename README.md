# Mob Highlighter

A client-side Fabric mod for Minecraft 26.1.2 that highlights a selected mob type using the vanilla glow outline, so you can spot all nearby mobs of that type at a glance — even through walls.

![Mob Highlighter preview](docs/preview.png)
<!-- Replace docs/preview.png with a screenshot of the glow effect in action -->

---

## Features

- Press **H** while looking at any mob to start tracking that mob type
- All mobs of that type within **64 blocks** glow with the vanilla outline effect (same as the Glowing status effect)
- Visible through walls and terrain
- Compatible with Iris shaders
- HUD counter shows the tracked mob name and nearby count
- Press **H** again (or while not looking at a mob) to clear the highlight

---

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 26.1.2
2. Install [Fabric API](https://modrinth.com/mod/fabric-api) 0.148.0+26.1.2 or later
3. Download `mob-highlighter-1.0.0.jar` from the [Releases](https://github.com/Hackatoan/mob-highlighter/releases) page
4. Drop the JAR into your `mods/` folder
5. Launch Minecraft

**Requirements:**
| Dependency | Version |
|---|---|
| Minecraft | 26.1.2 |
| Fabric Loader | 0.19.2+ |
| Fabric API | 0.148.0+26.1.2+ |
| Java | 25+ |

---

## Usage

| Action | Result |
|---|---|
| Look at a mob + press **H** | Track that mob type — all nearby mobs glow |
| Press **H** again on the same mob type | Clear the highlight |
| Press **H** while looking at nothing | Clear the highlight |

The keybind can be changed in **Options → Controls → Mob Highlighter**.

---

## Building from Source

```bash
git clone https://github.com/Hackatoan/mob-highlighter.git
cd mob-highlighter
./gradlew build
```

The output JAR will be at `build/libs/mob-highlighter-1.0.0.jar`.
