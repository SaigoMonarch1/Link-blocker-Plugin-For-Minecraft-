package com.kurai.linkblocker;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkBlocker extends JavaPlugin implements Listener {

    // Regex pattern jo har tarah ki links detect karta hai
    private final Pattern URL_PATTERN = Pattern.compile(
            "(https?://)?(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)",
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("LinkBlocker (Kurai) plugin enabled successfully.");
    }

    // Players ke chat ko intercept karna
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (containsBlockedLink(event.getMessage())) {
            event.setCancelled(true); // Bina msg bheje silent block
        }
    }

    // Console commands intercept karna (/say, /broadcast etc.)
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onConsoleCommand(ServerCommandEvent event) {
        if (containsBlockedLink(event.getCommand())) {
            event.setCancelled(true); // Console se bhi link block
            getLogger().warning("Blocked an unauthorized link from console.");
        }
    }

    // Check karna ki link config me allow hai ya nahi
    private boolean containsBlockedLink(String message) {
        Matcher matcher = URL_PATTERN.matcher(message);
        List<String> allowedLinks = getConfig().getStringList("allowed-links");

        while (matcher.find()) {
            String foundUrl = matcher.group().toLowerCase();
            boolean isAllowed = false;

            for (String allowed : allowedLinks) {
                if (foundUrl.contains(allowed.toLowerCase())) {
                    isAllowed = true;
                    break;
                }
            }

            if (!isAllowed) {
                return true; // Link allow nahi hai, isliye block karo
            }
        }
        return false;
    }
                                }
