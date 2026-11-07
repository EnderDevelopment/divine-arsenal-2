package com.enderdevelopment.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.Locale;

public final class Main extends JavaPlugin implements Listener {
    private NamespacedKey weaponKey;

    @Override
    public void onEnable() {
        weaponKey = new NamespacedKey(this, "god_weapon");
        Bukkit.getPluginManager().registerEvents(this, this);
        if (getCommand("godweapon") != null) {
            getCommand("godweapon").setExecutor((sender, command, label, args) -> handleCommand(sender, args));
        }
    }

    @Override
    public void onDisable() {
    }

    private boolean handleCommand(org.bukkit.command.CommandSender sender, String[] args) {
        if (!sender.hasPermission("godlyweapons.use")) {
            sender.sendMessage(Component.text("You do not have permission to use god weapons.", NamedTextColor.RED));
            return true;
        }
        if (args.length == 0 || args[0].equalsIgnoreCase("list")) {
            sender.sendMessage(Component.text("Godly weapons: Thor's Wrath, Poseidon's Trident, Aegis Blade", NamedTextColor.GOLD));
            sender.sendMessage(Component.text("Use /godweapon give <thor|poseidon|aegis> [player]", NamedTextColor.GRAY));
            return true;
        }
        if (!args[0].equalsIgnoreCase("give") || args.length < 2 || args.length > 3) {
            sender.sendMessage(Component.text("Usage: /godweapon give <thor|poseidon|aegis> [player]", NamedTextColor.RED));
            return true;
        }
        Player recipient;
        if (args.length == 3) {
            if (!sender.hasPermission("godlyweapons.admin")) {
                sender.sendMessage(Component.text("Only admins can give weapons to other players.", NamedTextColor.RED));
                return true;
            }
            recipient = Bukkit.getPlayerExact(args[2]);
        } else if (sender instanceof Player player) {
            recipient = player;
        } else {
            sender.sendMessage(Component.text("Specify an online player: /godweapon give <weapon> <player>", NamedTextColor.RED));
            return true;
        }
        if (recipient == null) {
            sender.sendMessage(Component.text("That player is not online.", NamedTextColor.RED));
            return true;
        }
        ItemStack weapon = createWeapon(args[1]);
        if (weapon == null) {
            sender.sendMessage(Component.text("Unknown weapon. Choose thor, poseidon, or aegis.", NamedTextColor.RED));
            return true;
        }
        recipient.getInventory().addItem(weapon).values().forEach(item -> recipient.getWorld().dropItemNaturally(recipient.getLocation(), item));
        recipient.sendMessage(Component.text("A divine weapon has been bestowed upon you.", NamedTextColor.AQUA));
        if (recipient != sender) sender.sendMessage(Component.text("Weapon given to " + recipient.getName() + ".", NamedTextColor.GREEN));
        return true;
    }

    private ItemStack createWeapon(String name) {
        String id = name.toLowerCase(Locale.ROOT);
        Material material;
        String title;
        NamedTextColor color;
        switch (id) {
            case "thor" -> { material = Material.NETHERITE_AXE; title = "Thor's Wrath"; color = NamedTextColor.YELLOW; }
            case "poseidon" -> { material = Material.TRIDENT; title = "Poseidon's Trident"; color = NamedTextColor.AQUA; }
            case "aegis" -> { material = Material.NETHERITE_SWORD; title = "Aegis Blade"; color = NamedTextColor.GOLD; }
            default -> { return null; }
        }
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        meta.displayName(Component.text(title, color));
        meta.lore(java.util.List.of(Component.text(switch (id) {
            case "thor" -> "Strike the ground to call a bolt of lightning.";
            case "poseidon" -> "Right-click to surge forward with ocean force.";
            default -> "A divine edge that empowers every strike.";
        }, NamedTextColor.GRAY)));
        meta.getPersistentDataContainer().set(weaponKey, PersistentDataType.STRING, id);
        Enchantment sharpness = Registry.ENCHANTMENT.get(NamespacedKey.minecraft("sharpness"));
        Enchantment unbreaking = Registry.ENCHANTMENT.get(NamespacedKey.minecraft("unbreaking"));
        if (sharpness != null) meta.addEnchant(sharpness, id.equals("aegis") ? 6 : 4, true);
        if (unbreaking != null) meta.addEnchant(unbreaking, 4, true);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onWeaponUse(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) return;
        String id = item.getItemMeta().getPersistentDataContainer().get(weaponKey, PersistentDataType.STRING);
        if (id == null || event.getAction().isLeftClick()) return;
        Player player = event.getPlayer();
        if (id.equals("thor")) {
            if (player.getTargetBlockExact(30) == null) return;
            player.getWorld().strikeLightning(player.getTargetBlockExact(30).getLocation());
            player.sendMessage(Component.text("The storm answers your call!", NamedTextColor.YELLOW));
        } else if (id.equals("poseidon")) {
            Vector direction = player.getLocation().getDirection().multiply(1.8).setY(0.45);
            player.setVelocity(direction);
            player.getWorld().spawnParticle(org.bukkit.Particle.SPLASH, player.getLocation(), 30, 0.6, 0.4, 0.6, 0.1);
        }
    }

    @EventHandler
    public void onDivineStrike(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!item.hasItemMeta()) return;
        String id = item.getItemMeta().getPersistentDataContainer().get(weaponKey, PersistentDataType.STRING);
        if ("aegis".equals(id)) {
            event.setDamage(event.getDamage() + 4.0);
            player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + 1.0));
        }
    }
}
