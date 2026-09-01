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

    private String deathMessageFormat;
    private boolean debug;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadSettings();
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("HideKiller enabled — all death messages will be replaced.");
    }

    private void loadSettings() {
        reloadConfig();
        deathMessageFormat = getConfig().getString("death-message", "<white>Player</white> has died");
        debug = getConfig().getBoolean("debug", false);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();

        // Every death, regardless of cause or killer, gets replaced with
        // the exact same fixed message from config — no placeholders,
        // no exceptions.
        Component deathMessage = MiniMessage.miniMessage().deserialize(deathMessageFormat);
        event.deathMessage(deathMessage);

        if (debug) {
            getLogger().info("Replaced death message for " + victim.getName());
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
