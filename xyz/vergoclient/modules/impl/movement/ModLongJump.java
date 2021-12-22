package xyz.vergoclient.modules.impl.movement;

import com.sun.org.apache.xpath.internal.operations.Bool;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import xyz.vergoclient.event.Event;
import xyz.vergoclient.event.impl.EventReceivePacket;
import xyz.vergoclient.event.impl.EventUpdate;
import xyz.vergoclient.modules.Module;
import xyz.vergoclient.modules.OnEventInterface;
import xyz.vergoclient.settings.BooleanSetting;
import xyz.vergoclient.settings.ModeSetting;
import xyz.vergoclient.settings.NumberSetting;
import xyz.vergoclient.util.ChatUtils;
import xyz.vergoclient.util.MovementUtils;
import xyz.vergoclient.util.TimerUtil;

import java.util.Arrays;

public class ModLongJump extends Module implements OnEventInterface {

    public ModLongJump() {
        super("LongJump", Category.MOVEMENT);
    }

    public ModeSetting mode = new ModeSetting("Mode", "Hypixel Bow", "Hypixel Bow", "Velocity Test");

    public NumberSetting veloTestX = new NumberSetting("Velo X", -10, -200, 200, 1), veloTestY = new NumberSetting("Velo Y", -10, -200, 200, 1);

    public BooleanSetting automated = new BooleanSetting("Automated", false), autoLook = new BooleanSetting("Auto Look", false),//, autoMove = new BooleanSetting("Auto Move", false),
                          //autoJump = new BooleanSetting("Auto Jump", false);
                          movementUtilMove = new BooleanSetting("Movement Utils Method", true),
                          otherMethod = new BooleanSetting("Other Method", false);

    public static transient TimerUtil hypixelTimer = new TimerUtil();

    @Override
    public void loadSettings() {
        mode.modes.clear();
        mode.modes.addAll(Arrays.asList("Hypixel Bow", "Velocity Test"));

        addSettings(mode, automated, autoLook, movementUtilMove, otherMethod);//, autoMove, autoJump);
    }

    public int i;
    public int slotId;
    public int ticks;
    public boolean hasHurt = false;

    public ItemStack itemStack = null;

    @Override
    public void onEnable() {
        if (mode.is("Hypixel Bow")) {
            setInfo("hypickle bow");
        }

        if(movementUtilMove.isEnabled()) {
            if(otherMethod.isDisabled()) {
                otherMethod.setEnabled(false);
            }
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
                //ChatUtils.addChatMessage("Switching slot from " + slotId + " to " + i);
                mc.getNetHandler().getNetworkManager().sendPacket(new C09PacketHeldItemChange(i));
            }
            ticks = mc.thePlayer.ticksExisted;
            mc.getNetHandler().getNetworkManager().sendPacket(new C08PacketPlayerBlockPlacement(itemStack));
        }
    }

    @Override
    public void onDisable() {
    }

    public boolean veloWasOn = false;

    @Override
    public void onEvent(Event e) {

        if (e instanceof EventUpdate && e.isPre()) {
            //Aim up and shoot shoot shoot!
            if (mode.is("Hypixel Bow")) {
                if (!hasHurt) {
                    if (mc.thePlayer.ticksExisted - ticks == 3) {
                        mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, -89.5f, true));
                        mc.getNetHandler().getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), EnumFacing.DOWN));
                        MovementUtils.setMotion(0);

                        // Switch back to original slot
                        if (i != slotId) {
                            //ChatUtils.addChatMessage("Switching back from " + i + " to " + slotId);
                            mc.getNetHandler().getNetworkManager().sendPacket(new C09PacketHeldItemChange(slotId));
                        }

                    }
                }

                if(mc.thePlayer.hurtTime == 9) {
                    hasHurt = true;
                } else if (mc.thePlayer.hurtTime > 9) {
                    toggle();
                }

                if(hasHurt) {

                    if(automated.isEnabled()) {
                        if(autoLook.isEnabled()) {
                            if (mc.thePlayer.ticksExisted - ticks == 3) {
                                mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, -0.954367f, true));
                            }
                        }
                        /*if(autoMove.isEnabled()) {
                            mc.thePlayer.setSprinting(true);
                            MovementUtils.forward(1);
                        }
                        if(autoJump.isEnabled()) {
                            mc.thePlayer.jump();
                            mc.thePlayer.setJumping(true);
                        }*/
                    }

                    mc.thePlayer.motionY *= 2;
                    if(movementUtilMove.isEnabled()) {
                        MovementUtils.setMotion(MovementUtils.getSpeed() * 3.5);
                    } else {
                        mc.thePlayer.motionZ *= 2;
                    }

                    hasHurt = false;
                    toggle();
                }

            } else if(mode.is("Velocity Test")) {
                EventReceivePacket event = (EventReceivePacket)e;
                if (event.packet instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.packet;
                    if (mc.theWorld.getEntityByID(packet.getEntityID()) != null && mc.theWorld.getEntityByID(packet.getEntityID()) instanceof EntityPlayer && ((EntityPlayer) mc.theWorld.getEntityByID(packet.getEntityID())).isUser()) {
                        if (veloTestX.getValueAsDouble() <= 0 && veloTestY.getValueAsDouble() <= 0) {
                            e.setCanceled(true);
                        } else {
                            packet.setMotionX((int) ((((double) packet.getMotionX()) / 100) * veloTestX.getValueAsDouble()));
                            packet.setMotionY((int) ((((double) packet.getMotionY()) / 100) * veloTestY.getValueAsDouble()));
                            packet.setMotionZ((int) ((((double) packet.getMotionZ()) / 100) * veloTestX.getValueAsDouble()));
                        }
                    }
                }
            }
        }
    }

}