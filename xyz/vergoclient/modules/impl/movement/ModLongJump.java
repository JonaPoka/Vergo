package xyz.vergoclient.modules.impl.movement;

import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import xyz.vergoclient.event.Event;
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

    public NumberSetting speedSlider = new NumberSetting("SpeedSlider", 2.2, 0.3, 3.0, 0.01), heightSlider = new NumberSetting("Height Slider", 2.34, 1.0, 3.0, 0.01);

    public BooleanSetting automated = new BooleanSetting("Automated", false), autoLook = new BooleanSetting("Auto Look", false),//, autoMove = new BooleanSetting("Auto Move", false),
                          //autoJump = new BooleanSetting("Auto Jump", false);
                          movementUtilMove = new BooleanSetting("Movement Utils Method", true),
                          otherMethod = new BooleanSetting("Other Method", false);

    public static transient TimerUtil hypixelTimer = new TimerUtil();

    @Override
    public void loadSettings() {
        mode.modes.clear();
        mode.modes.addAll(Arrays.asList("Hypixel Bow", "Velocity Test"));

        addSettings(mode, speedSlider, heightSlider, automated, autoLook);//, autoMove, autoJump);
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

        mc.timer.timerSpeed = 1.0f;

    }

    public boolean veloWasOn = false;

    public float oldPitch;

    @Override
    public void onEvent(Event e) {

        if (e instanceof EventUpdate && e.isPre()) {
            //Aim up and shoot shoot shoot!
            if (mode.is("Hypixel Bow")) {
                if (!hasHurt) {
                    if (mc.thePlayer.ticksExisted - ticks == 3) {
                        //oldPitch = mc.thePlayer.rotationPitch;
                        mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, -89.5f, true));
                        mc.getNetHandler().getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), EnumFacing.DOWN));
                        //mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, oldPitch, true));
                        if(automated.isEnabled()) {
                            if (autoLook.isEnabled()) {
                                mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, -4.954367f, true));
                            }
                        }

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
                                mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, -4.954367f, true));
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

                    mc.thePlayer.motionY *= heightSlider.getValueAsDouble();
                    MovementUtils.setMotion(MovementUtils.getSpeed() * speedSlider.getValueAsDouble());

                    hasHurt = false;
                    toggle();
                }

            }
        }
    }

}