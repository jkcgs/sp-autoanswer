package com.makzk.spigot.autoanswer;

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
    public void onDisable() {
        logger.info("Plugin disabled");
    }

    public AnswerConfig getAnswerConfig() {
        return answerConfig;
    }
}

