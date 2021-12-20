package xyz.vergoclient.modules.impl.movement;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Particle;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import xyz.vergoclient.Vergo;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventTick;
import xyz.vergoclient.event.impl.EventUpdate;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.util.ChatUtils;
import xyz.vergoclient.util.MovementUtils;
import xyz.vergoclient.util.TimerUtil;

import java.util.Arrays;

public class ModLongJump extends Module implements OnEventInterface {

    public ModLongJump() {
        super("LongJump", Category.MOVEMENT);
    }

    public ModeSetting mode = new ModeSetting("Mode", "Hypixel Bow", "Hypixel Bow");
    public static transient TimerUtil hypixelTimer = new TimerUtil();

    @Override
    public void loadSettings() {
        mode.modes.clear();
        mode.modes.addAll(Arrays.asList("Hypixel Bow"));

        addSettings(mode);
    }

    public int i;
    public int slotId;
    public int ticks;
    public boolean hasHurt = false;

    public ItemStack itemStack = null;

    @Override
    public void onEnable() {
        if (mode.is("Hypixel Bow")) {
            setInfo("HYPICKLE BOW POG");
        }

        if(MovementUtils.isMoving()) {
            mc.thePlayer.movementInput.moveForward = 0.0f;
            mc.thePlayer.movementInput.moveStrafe = 0.0f;
            MovementUtils.setMotion(0.0f);
        }

        for (i = 0; i < 9; i++) {
            itemStack = mc.thePlayer.inventory.mainInventory[i];
            if (itemStack != null && itemStack.getItem() instanceof ItemBow)
                break;
        }
        if (i == 9) {
            ChatUtils.addChatMessage("Did not find a bow in your hotbar.");
            toggle();
            return;
        } else {
            slotId = mc.thePlayer.inventory.currentItem;
            if (i != slotId) {
                ChatUtils.addChatMessage("Switching slot from " + slotId + " to " + i);
                mc.getNetHandler().getNetworkManager().sendPacket(new C09PacketHeldItemChange(i));
            }
            ticks = mc.thePlayer.ticksExisted;
            mc.getNetHandler().getNetworkManager().sendPacket(new C08PacketPlayerBlockPlacement(itemStack));
        }
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onEvent(Event e) {

        if (e instanceof EventUpdate && e.isPre()) {
            //Aim up and shoot shoot shoot!
            if (mode.is("Hypixel Bow")) {
                if (!hasHurt) {
                    if (mc.thePlayer.ticksExisted - ticks == 3) {
                        mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, -89.5f, true));
                        mc.getNetHandler().getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), EnumFacing.DOWN));

                        // Switch back to original slot
                        if (i != slotId) {
                            //ChatUtils.addChatMessage("Switching back from " + i + " to " + slotId);
                            mc.getNetHandler().getNetworkManager().sendPacket(new C09PacketHeldItemChange(slotId));
                        }

                    }
                }

                if(mc.thePlayer.hurtTime == 9) {
                    hasHurt = true;
                }

                if(hasHurt) {

                    mc.thePlayer.motionX *= 1.3D;
                    mc.thePlayer.motionZ *= 1.3D;

                    hypixelTimer.reset();
                    if(hypixelTimer.hasTimeElapsed(1000, true)) {
                        mc.thePlayer.motionX = 0f;
                        mc.thePlayer.motionY = 0f;
                    }
                    hasHurt = false;
                    toggle();
                }

            }
        }
    }

}