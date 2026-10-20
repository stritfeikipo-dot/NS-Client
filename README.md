# NS Client

Client-side PvP visuals mod for Minecraft Java 1.21.11 (Fabric), built for the
Holyworld server. Theme: Modern Purple.

- Mod ID: nsclient
- Package: com.nsclient
- Build artifact: NS-Client.jar
- Config: config/nsclient/config.json (persists across restarts)
- Open menu: Right Shift (rebindable)
- Open HUD editor: Right Ctrl (rebindable)

No aimbot, no auto-attack, no combat automation - everything here is visual only.

## Known limitations

- Hitmarker / combo / hit particles trigger on attack attempt, not a
  confirmed server-side hit.
- "Hide vanilla crosshair" is currently a stub (not wired up yet).
- Kill-effect detection uses a simple "entity dead or removed by next tick" check.
- No mod icon included yet.

## Build it yourself

gradle build

Jar will be at build/libs/NS-Client-1.0.0.jar
