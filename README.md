# Divine Arsenal Minecraft Plugin

Divine Arsenal is a Minecraft plugin that adds godly weapons with custom abilities, enchanted equipment, player permissions, and commands for Paper servers running Minecraft 1.21. The plugin provides Thor's Wrath, Poseidon's Trident, and the Aegis Blade.

## Install

1. Build the project with Maven using Java 21:

   `mvn package`

2. Copy the generated `plugin.jar` file from the `target` directory into your server's `plugins` directory.

3. Start or restart a Paper Minecraft server running version 1.21 or newer.

4. Confirm that the plugin loaded successfully, then use the `/godweapon` command in game.

The plugin defines the following permissions:

- `godlyweapons.use` allows access to the god weapon commands. It defaults to operators.
- `godlyweapons.admin` allows giving weapons to another online player. It defaults to operators.

## Use

View the available weapons:

`/godweapon list`

Give yourself a weapon:

`/godweapon give thor`

`/godweapon give poseidon`

`/godweapon give aegis`

Give a weapon to another online player:

`/godweapon give thor PlayerName`

Giving weapons to another player requires `godlyweapons.admin`.

Available weapons:

- Thor's Wrath is a netherite axe with Sharpness IV and Unbreaking IV. Right-click a target block within 30 blocks to summon lightning at that location.
- Poseidon's Trident is a trident with Sharpness IV and Unbreaking IV. Right-click to launch yourself forward with an upward boost and splash particles.
- Aegis Blade is a netherite sword with Sharpness VI and Unbreaking IV. Each successful attack deals 4 additional damage and restores 1 health point to the wielder.

---

More Minecraft plugin projects by EnderDevelopment: https://github.com/EnderDevelopment

Project page: https://dash.enderdevelopment.com/community/e23828a3-cb39-430b-9f36-9b43e2eb3c42?utm_source=github&utm_medium=readme&utm_campaign=auto_publish&utm_content=e23828a3-cb39-430b-9f36-9b43e2eb3c42
