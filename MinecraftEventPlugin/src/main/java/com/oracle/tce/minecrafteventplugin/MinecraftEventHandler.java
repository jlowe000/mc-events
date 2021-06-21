/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.oracle.tce.minecrafteventplugin;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.net.*;
import java.net.http.*;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

/**
 *
 * @author Jason
 */
public final class MinecraftEventHandler implements Listener {

    private static Logger logger;
    private static String host;
    private static String eventType;
    private long session = System.currentTimeMillis();
    private long game_id = 6;

    @EventHandler
    public void login(PlayerLoginEvent event) {
        // Some code here
        logger.info("login has been invoked!");
    }

    @EventHandler (priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        logger.info("join has been invoked!");
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event)
    {
        Location fromLoc = event.getFrom();
        Location toLoc = event.getTo();
        Player player = event.getPlayer();
        if (!fromLoc.getBlock().equals(toLoc.getBlock())) {
            logger.info("move has been invoked!");
            StringBuilder sb = new StringBuilder();
            sb.append("{ \"game_id\": "+game_id+", ");
            sb.append("\"instance_id\": \""+session+"\", ");
            sb.append("\"user_id\": \""+player.getDisplayName()+"\", ");
            sb.append("\"score\": "+player.getExp()+", ");
            sb.append("\"level\": "+player.getLevel()+", ");
            Location loc = player.getLocation();
            sb.append("\"x\": "+loc.getBlockX()+", ");
            sb.append("\"y\": "+loc.getBlockY()+", ");
            sb.append("\"z\": "+loc.getBlockZ()+", ");
            sb.append("\"state\": \"PLAYER_MOVE\" }");
            logger.info(sb.toString());
/*
            try {
                HttpRequest request = HttpRequest.newBuilder()
                  .uri(new URI(host+"/event/"+eventType))
                  .headers("Content-Type", "application/json;charset=UTF-8")
                  .POST(HttpRequest.BodyPublishers.ofString(sb.toString()))
                  .build();
                HttpResponse<String> response = HttpClient.newBuilder()
                  .build()
                  .send(request, HttpResponse.BodyHandlers.ofString());
                logger.info(response.body());
            } catch (Exception ex) {
                logger.warning(ex.toString());
            }
*/
            ConnectionFactory factory = new ConnectionFactory();
            Connection connection = null;
            Channel channel = null;
            try {
                connection = factory.newConnection();
                channel = connection.createChannel();
                channel.queueDeclare("hello", false, false, false, null);
                channel.basicPublish("", "hello", null, sb.toString().getBytes());
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Error", ex);
            } finally {
                if (channel != null) { try { channel.close(); } catch (Exception iex) {} }
                if (connection != null) { try { connection.close(); } catch (Exception iex) {} }
            }
        }
    }

    public static void register(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new MinecraftEventHandler(), plugin);
        logger = plugin.getLogger();
        logger.info("handler has been registered!");
        String _host = plugin.getConfig().getString("host");
        String _port = plugin.getConfig().getString("port","");
        boolean _isSecured = plugin.getConfig().getBoolean("is-secured");
        eventType = plugin.getConfig().getString("event-type");
        host = new StringBuilder().append(_isSecured ? "https://" : "http://")
                                  .append(_host)
                                  .append("".equals(_port) ? "" : ":"+_port).toString();
        logger.info("handler will send messages to: "+host+"/event/"+eventType);
    }
}
