package sync.api;

import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.event.entity.player.PlayerEvent;

import cpw.mods.fml.common.eventhandler.Cancelable;

@Cancelable
public class RequestSyncEvent extends PlayerEvent {
    /**
     * Event is triggered when the player requests to sync to a shell.
     * Event is cancelable to stop a player from syncing.
     *
     * @param x next sync x position
     * @param y next sync y position
     * @param z next sync z position
     * @param dimension id of the dimension of the next sync
     */

    private final int x;
    private final int y;
    private final int z;
    private final int dimension;

    public RequestSyncEvent(EntityPlayer player, int xCoord, int yCoord, int zCoord, int dimension) {
        super(player);
        this.x = xCoord;
        this.y = yCoord;
        this.z = zCoord;
        this.dimension = dimension;
    }
}
