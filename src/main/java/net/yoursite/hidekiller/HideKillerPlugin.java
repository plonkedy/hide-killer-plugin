package net.yoursite.hidekiller;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class HideKillerPlugin extends JavaPlugin implements Listener {

    private String replacementName;
    private String messageFormat;
    private boolean hideForPlayerKillsOnly;
    private boolean debug;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadSettings();
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("HideKiller enabled — killer names will be hidden in death messages.");
    }

    private void loadSettings() {
        reloadConfig();
        replacementName = getConfig().getString("replacement-name", "a mysterious figure");
        messageFormat = getConfig().getString(
                "message-format",
                "<red>%victim% has died"
        );
        hideForPlayerKillsOnly = getConfig().getBoolean("hide-for-player-kills-only", true);
        debug = getConfig().getBoolean("debug", false);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        // If we're only anonymizing player-vs-player kills and there's no
        // player killer (mob, fall damage, lava, etc.), leave the vanilla
        // death message untouched.
        if (hideForPlayerKillsOnly && killer == null) {
            return;
        }

        // If there IS no identifiable killer at all (e.g. plain
        // environmental death) there's nothing to hide, so skip too —
        // unless you want every single death message customized, in
        // which case remove this block.
        if (killer == null) {
            return;
        }

        String formatted = messageFormat
                .replace("%victim%", victim.getName())
                .replace("%killer%", replacementName);

        Component deathMessage = MiniMessage.miniMessage().deserialize(formatted);
        event.deathMessage(deathMessage);

        if (debug) {
            getLogger().info("Rewrote death message for " + victim.getName()
                    + " (real killer: " + killer.getName() + ")");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("hidekiller")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                loadSettings();
                sender.sendMessage("§aHideKiller config reloaded.");
                return true;
            }
            sender.sendMessage("§eUsage: /hidekiller reload");
            return true;
        }
        return false;
    }
}
