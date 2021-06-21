/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.oracle.tce.minecrafteventplugin;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Jason
 */
public class MinecraftEventPlugin extends JavaPlugin implements Listener {

    private MinecraftEventHandler handler = null;

    @EventHandler
    public void unregisterHandlers(PluginDisableEvent event) {
        // Some code here
        getLogger().info("plugin has been disabled!");
        event.getHandlers().unregister((Listener)this);
        event.getHandlers().unregister((Listener)handler);
    }    

    @Override
    public void onEnable() {
        // TODO Insert logic to be performed when the plugin is enabled
        getLogger().info("onEnable has been invoked!");
        this.saveDefaultConfig(); 
        MinecraftEventHandler.register(this);
        getServer().getPluginManager().registerEvents(this, this);
    }
    
    @Override
    public void onDisable() {
        // TODO Insert logic to be performed when the plugin is disabled
        getLogger().info("onDisable has been invoked!");
    }

}
