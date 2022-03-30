package xyz.vergoclient.modules.impl.movement.scaffold;

import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.potion.Potion;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventMove;
import xyz.vergoclient.event.impl.EventReceivePacket;
import xyz.vergoclient.event.impl.EventSendPacket;
import xyz.vergoclient.event.impl.EventUpdate;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;

public class NewScaffold extends Module implements OnEventInterface {

    public NewScaffold() {
        super("Scaffold", Category.MOVEMENT);
    }

    // Variables
    private float rotations[];
    private ScaffoldUtils.BlockCache blockCache, lastBlockCache;

    public static transient int lastSlot = -1;

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

        if (lastSlot != mc.thePlayer.inventory.currentItem) {
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        }
        lastSlot = -1;

    }

    @Override
    public void onEvent(Event e) {

        if (e instanceof EventReceivePacket && e.isPre()) {

            if (((EventReceivePacket)e).packet instanceof S2FPacketSetSlot) {
                lastSlot = ((S2FPacketSetSlot)((EventReceivePacket)e).packet).slot;
            }

        }

        if (e instanceof EventSendPacket & e.isPre()) {
            if (((EventSendPacket)e).packet instanceof C09PacketHeldItemChange) {
                lastSlot = ((C09PacketHeldItemChange)((EventSendPacket)e).packet).getSlotId();
            }

        }

        if(e instanceof EventMove && e.isPre()) {

            // Setting Block Cache
            blockCache = ScaffoldUtils.grab();
            if (blockCache != null) {
                lastBlockCache = ScaffoldUtils.grab();
            }else{
                return;
            }

            int slot = ScaffoldUtils.grabBlockSlot();
            if(slot == -1) return;


            // Setting Slot
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));


            // Placing Blocks
            if (blockCache == null) return;

            mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getStackInSlot(slot), lastBlockCache.position, lastBlockCache.facing, ScaffoldUtils.getHypixelVec3(lastBlockCache));

            mc.thePlayer.swingItem();

            mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());

            blockCache = null;


            // Slowdown if speed pot is active.
            if(mc.thePlayer.isPotionActive(Potion.moveSpeed.id)){
                mc.thePlayer.motionX *= 0.66;
                mc.thePlayer.motionZ *= 0.66;
            }
        }

        if(e instanceof EventUpdate) {
            EventUpdate event = (EventUpdate) e;
            rotationInformation(event);
        }

    }

    private void rotationInformation(EventUpdate e) {
        if(lastBlockCache != null) {
            rotations = ScaffoldUtils.getFacingRotations2(lastBlockCache.getPosition().getX(), lastBlockCache.getPosition().getY(), lastBlockCache.getPosition().getZ());
            mc.thePlayer.renderYawOffset = rotations[0];
            mc.thePlayer.rotationYawHead = rotations[0];
            e.setYaw(rotations[0]);
            e.setPitch(81);
        } else {
            e.setPitch(81);
            e.setYaw(mc.thePlayer.rotationYaw + 180);
            mc.thePlayer.renderYawOffset = mc.thePlayer.rotationYaw + 180;
            mc.thePlayer.rotationYawHead = mc.thePlayer.rotationYaw + 180;
        }
    }

}
