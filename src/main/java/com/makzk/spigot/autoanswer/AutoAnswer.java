package com.makzk.spigot.autoanswer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class AutoAnswer extends JavaPlugin {
    private AnswerConfig answerConfig;
    private Logger logger;

    @Override
    public void onEnable() {
        logger = getLogger();

        // Load configuration
        getConfig().options().copyDefaults(true);
        saveConfig();

        // Register messages listener
        getServer().getPluginManager().registerEvents(new QuestionListener(this), this);

        answerConfig = new AnswerConfig(this);
        answerConfig.reload();

        logger.info("Plugin loaded");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(cmd.getName().equalsIgnoreCase("aa-reload")){
            if(!sender.hasPermission("autoanswer.reload")) return false;

            answerConfig.reload();
            logger.info("Settings reloaded!");

            if(sender instanceof Player) {
                sender.sendMessage("Settings reloaded!");
            }

            return true;
        }

        return false;
    }

    @Override
    public void onDisable() {
        saveConfig();
        logger.info("Plugin disabled");
    }

    public AnswerConfig getAnswerConfig() {
        return answerConfig;
    }
}

