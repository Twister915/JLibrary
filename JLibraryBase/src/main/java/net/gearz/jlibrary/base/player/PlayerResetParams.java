package net.gearz.jlibrary.base.player;

import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * A Data file containing the parameters ( and default values ) for resetting a player
 * <p>
 * <p>
 * Latest Change: Boxed the primatives
 * <p>
 *
 * @author @Twister915
 * @since 7/10/13
 */

@Data
public class PlayerResetParams {
    private TPlayer player;

    private List<ItemStack> doNotclear;

    private boolean clearXP = true;

    private boolean clearPotions = true;

    private boolean restoreHealth = true;

    private boolean restoreFood = true;

    private boolean resetFlight = true;

    private boolean movePlayerDown = true;

    private boolean restoreSpeeds = true;

    private boolean resetInventory = true;
}