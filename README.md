[![Buy Me A Coffee](https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png)](https://buymeacoffee.com/hackatoa)

# Mob Highlighter

A client-side Fabric mod for Minecraft 26.1.2 that highlights a selected mob type using the vanilla glow outline — spot all nearby mobs of that type at a glance, even through walls.

## Features

- Press **H** while looking at a mob to start tracking that mob type
- All mobs of that type within 64 blocks glow with the vanilla outline effect
- Visible through walls and terrain; compatible with Iris shaders
- HUD counter shows the tracked mob name and nearby count
- Press **H** again (or while not looking at a mob) to clear the highlight

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 26.1.2
2. Install [Fabric API](https://modrinth.com/mod/fabric-api) 0.148.0+26.1.2+
3. Download `mob-highlighter-1.0.0.jar` from [Releases](../../releases/latest)
4. Drop the JAR into your `mods/` folder and launch Minecraft

**Requirements:**

| Dependency | Version |
|---|---|
| Minecraft | 26.1.2 |
| Fabric Loader | 0.19.2+ |
| Fabric API | 0.148.0+26.1.2+ |
| Java | 25+ |

## Usage

| Action | Result |
|---|---|
| Look at mob + press **H** | Track that mob type — all nearby glow |
| Press **H** on same type | Clear the highlight |
| Press **H** while looking at nothing | Clear the highlight |

Keybind configurable in **Options → Controls → Mob Highlighter**.

## Build from source

```bash
git clone https://github.com/Hackatoan/mob-highlighter
cd mob-highlighter
./gradlew build
# output: build/libs/mob-highlighter-1.0.0.jar
```

---

[hackatoa.com](https://hackatoa.com) · [GitHub](https://github.com/Hackatoan) · [Buy Me A Coffee](https://buymeacoffee.com/hackatoa)
