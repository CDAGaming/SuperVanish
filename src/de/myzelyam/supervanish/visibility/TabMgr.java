/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *   License, v. 2.0. If a copy of the MPL was not distributed with this
 *   file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.myzelyam.supervanish.visibility;

import de.myzelyam.supervanish.SuperVanish;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class TabMgr {

    private final SuperVanish plugin;

    private FileConfiguration playerData;

    public TabMgr(SuperVanish plugin) {
        this.plugin = plugin;
        playerData = plugin.playerData;
        enabled = plugin.settings.getBoolean("Configuration.Tablist.ChangeTabNames");
    }

    private boolean enabled;

    public void adjustTabName(Player p, TabAction action) {
        if (action == TabAction.RESTORE_NORMAL_TAB_NAME)
            restoreNormalTabName(p);
        else
            setCustomTabName(p);
    }

    private void restoreNormalTabName(Player p) {
        String ntn = loadData(p);
        if (ntn == null)
            return;
        p.setPlayerListName(plugin.convertString(ntn, p));
    }

    private void setCustomTabName(Player p) {
        if (!enabled)
            return;
        String tn = plugin.getMsg("TabName");
        if (tn != null) {
            storeData(p);
            StringBuilder sb = new StringBuilder(plugin.convertString(tn, p));
            if (plugin.convertString(tn, p).length() > 16)
                sb.setLength(16);
            else
                sb.setLength(plugin.convertString(tn, p).length());
            p.setPlayerListName(sb.toString());
        }
    }

    public enum TabAction {
        RESTORE_NORMAL_TAB_NAME, SET_CUSTOM_TAB_NAME
    }

    public void storeData(Player p) {
        playerData.set("PlayerData." + p.getUniqueId().toString()
                + ".normalTabName", p.getPlayerListName());
        plugin.savePlayerData();
    }

    public String loadData(Player p) {
        return playerData.getString("PlayerData." + p.getUniqueId().toString()
                + ".normalTabName");
    }
}