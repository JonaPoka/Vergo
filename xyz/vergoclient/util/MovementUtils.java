package xyz.vergoclient.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;

public class MovementUtils {
	
	public static Minecraft mc = Minecraft.getMinecraft();
	
	public static double getBlocksPerSecond() {
		
		if (mc.thePlayer == null || mc.thePlayer.ticksExisted < 1) {
			return 0;
		}
		
		return mc.thePlayer.getDistance(mc.thePlayer.lastTickPosX, mc.thePlayer.lastTickPosY, mc.thePlayer.lastTickPosZ) * (Minecraft.getMinecraft().timer.ticksPerSecond * Minecraft.getMinecraft().timer.timerSpeed);
		
	}
	
	public static float getSpeed() {
        return (float) Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
    }

    public static void strafe() {
        strafe(getSpeed());
    }

    public static boolean isMoving() {
        return mc.thePlayer != null && (mc.thePlayer.movementInput.moveForward != 0F || mc.thePlayer.movementInput.moveStrafe != 0F);
    }

    public static boolean hasMotion() {
        return mc.thePlayer.motionX != 0D && mc.thePlayer.motionZ != 0D && mc.thePlayer.motionY != 0D;
    }

    public static void strafe(double speed) {
        if(!isMoving())
            return;
        
        setMotion(speed);
    }

    public static void forward(final double length) {
        final double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
        mc.thePlayer.setPosition(mc.thePlayer.posX + (-Math.sin(yaw) * length), mc.thePlayer.posY, mc.thePlayer.posZ + (Math.cos(yaw) * length));
    }
    
    // Found on github
    public static void setMotion(double speed) {
        double forward = mc.thePlayer.movementInput.moveForward;
        double strafe = mc.thePlayer.movementInput.moveStrafe;
        float yaw = mc.thePlayer.rotationYaw;
        if ((forward == 0.0D) && (strafe == 0.0D)) {
        	mc.thePlayer.motionX = 0;
        	mc.thePlayer.motionZ = 0;
        } else {
            if (forward != 0.0D) {
                if (strafe > 0.0D) {
                    yaw += (forward > 0.0D ? -45 : 45);
                } else if (strafe < 0.0D) {
                    yaw += (forward > 0.0D ? 45 : -45);
                }
                strafe = 0.0D;
                if (forward > 0.0D) {
                    forward = 1;
                } else if (forward < 0.0D) {
                    forward = -1;
                }
            }
            mc.thePlayer.motionX = forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F)); 
            mc.thePlayer.motionZ = forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F));
        }
    }
    
    public static void setMotion(double speed, float directionInYaw) {
        double forward = mc.thePlayer.movementInput.moveForward;
        double strafe = mc.thePlayer.movementInput.moveStrafe;
        float yaw = directionInYaw;
        if ((forward == 0.0D) && (strafe == 0.0D)) {
        	mc.thePlayer.motionX = 0;
        	mc.thePlayer.motionZ = 0;
        } else {
            if (forward != 0.0D) {
                if (strafe > 0.0D) {
                    yaw += (forward > 0.0D ? -45 : 45);
                } else if (strafe < 0.0D) {
                    yaw += (forward > 0.0D ? 45 : -45);
                }
                strafe = 0.0D;
                if (forward > 0.0D) {
                    forward = 1;
                } else if (forward < 0.0D) {
                    forward = -1;
                }
            }
            mc.thePlayer.motionX = forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F)); 
            mc.thePlayer.motionZ = forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F));
        }
    }
    
    public static boolean isOnGround(double height) {
        if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty()) {
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean isOnGround(double height, EntityPlayer player) {
        if (!mc.theWorld.getCollidingBoundingBoxes(player, player.getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty()) {
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean canStep(double height) {
    	
    	if (!mc.thePlayer.isCollidedHorizontally)
    		return false;
    	
		if ((!mc.theWorld
				.getCollidingBoundingBoxes(mc.thePlayer,
						mc.thePlayer.getEntityBoundingBox().expand(0.1, 0, 0).offset(0.0D, height - 0.1, 0.0D))
				.isEmpty()
				&& mc.theWorld
						.getCollidingBoundingBoxes(mc.thePlayer,
								mc.thePlayer.getEntityBoundingBox().expand(0.1, 0, 0).offset(0.0D, height + 0.1, 0.0D))
						.isEmpty())
				|| (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer,
						mc.thePlayer.getEntityBoundingBox().expand(-0.1, 0, 0).offset(0.0D, height - 0.1, 0.0D))
						.isEmpty()
						&& mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer,
								mc.thePlayer.getEntityBoundingBox().expand(-0.1, 0, 0).offset(0.0D, height + 0.1,
										0.0D))
								.isEmpty())
				|| (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer,
						mc.thePlayer.getEntityBoundingBox().expand(0, 0, 0.1).offset(0.0D, height - 0.1, 0.0D))
						.isEmpty()
						&& mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer,
								mc.thePlayer.getEntityBoundingBox().expand(0, 0, 0.1).offset(0.0D, height + 0.1, 0.0D))
								.isEmpty())
				|| (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer,
						mc.thePlayer.getEntityBoundingBox().expand(0, 0, -0.1).offset(0.0D, height - 0.1, 0.0D))
						.isEmpty()
						&& mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox()
								.expand(0, 0, -0.1).offset(0.0D, height + 0.1, 0.0D)).isEmpty())) {
			return true;
		} else {
			return false;
		}
    }

	public static double getBaseMoveSpeed() {
		double baseSpeed = 0.2873D;
		if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
			int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
			baseSpeed *= 1.0D + 0.2D * (double) (amplifier + 1);
		}

		return baseSpeed;
	}
	
	public static boolean isOverVoid() {
		
		boolean isOverVoid = true;
		BlockPos block = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
		
		for (double i = mc.thePlayer.posY + 1; i > 0; i -= 0.5) {
			
			if (isOverVoid) {
				
				try {
					if (mc.theWorld.getBlockState(block).getBlock() != Blocks.air) {
						
						isOverVoid = false;
						break;
						
					}
				} catch (Exception e) {
					
				}
				
			}
			
			block = block.add(0, -1, 0);
			
		}
		
		for (double i = 0; i < 10; i += 0.1) {
			if (MovementUtils.isOnGround(i) && isOverVoid) {
				isOverVoid = false;
				break;
			}
		}
		
		return isOverVoid;
	}
	
	// When in the air you will slow down by 9% every tick
	public static double getAirTickSpeed(double currentSpeed) {
		return currentSpeed * 0.91f;
	}
	
	public static float getYawFromMovement() {
		double forward = mc.thePlayer.movementInput.moveForward;
		double strafe = mc.thePlayer.movementInput.moveStrafe;
		float yaw = mc.thePlayer.rotationYaw;
		if ((forward == 0.0D) && (strafe == 0.0D)) {

		} else if (forward != 0.0D) {
//			strafe = 0.0D;
			if (forward > 0.0D) {
				forward = 1;
			} else if (forward < 0.0D) {
				forward = -1;
			}
			if (strafe > 0.0D) {
				yaw += (forward > 0.0D ? -45 : 45);
			} else if (strafe < 0.0D) {
				yaw += (forward > 0.0D ? 45 : -45);
			}
		}
		
		return yaw + 90.0F;
		
	}
	
}
