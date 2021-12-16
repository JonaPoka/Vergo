package xyz.vergoclient.util.pathfinding;

import java.util.ArrayList;
import java.util.Collections;

import xyz.vergoclient.util.RenderUtils;
import xyz.vergoclient.util.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class PathFinder {
	
	public PathFinder(int maxDistanceApart, boolean raytrace, boolean dontPathIntoBlocks) {
		this.maxDistanceApart = maxDistanceApart;
		this.raytrace = raytrace;
		this.dontPathIntoBlocks = dontPathIntoBlocks;
	}
	
	public static Minecraft mc = Minecraft.getMinecraft();
	
	public int maxDistanceApart = 0;
	public ArrayList<BlockPos> path = new ArrayList<>();
	public boolean raytrace = false, dontPathIntoBlocks = false;
	
	// Returns true if it was able to reach the destination
	public boolean createPath(BlockPos current, BlockPos target) {
		
		path.clear();
		path.add(current);
		
		boolean success = false;
		long cancel = System.currentTimeMillis() + 3000;
		
		for (int i = 0; i < 7; i++) {
			success = createPath(current, target, System.currentTimeMillis());
			if (success)
				break;
			if (System.currentTimeMillis() > cancel)
				return false;
		}
		
		return success;
		
	}
	
	// Returns true if it was able to reach the destination
	boolean createPath(BlockPos current, BlockPos target, long startTime) {
		
		if (System.currentTimeMillis() > startTime + 2500) {
			path.clear();
			return false;
		}
		
		ArrayList<BlockPos> availableBlocks = new ArrayList<>();
		
		for (int x = current.getX() - maxDistanceApart; x < current.getX() + maxDistanceApart; x++) {
			for (int y = current.getY() - maxDistanceApart; y < current.getY() + maxDistanceApart; y++) {
				for (int z = current.getZ() - maxDistanceApart; z < current.getZ() + maxDistanceApart; z++) {
					BlockPos pos = new BlockPos(x, y, z);
					if (!path.contains(pos)) {
						
						boolean eligible = true;
						
						if (dontPathIntoBlocks) {
							if (!mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.air)) {
								eligible = false;
							}
						}
						
						if (raytrace) {
							
							double startX = current.getX(),
									endX = pos.getX(),
									startY = current.getY(),
									endY = pos.getY(),
									startZ = current.getZ(),
									endZ = pos.getZ();
							
							if (startX > endX) {
								double tempX = startX;
								startX = endX;
								endX = tempX;
							}
							
							if (startY > endY) {
								double tempY = startY;
								startY = endY;
								endY = tempY;
							}
							
							if (startZ > endZ) {
								double tempZ = startZ;
								startZ = endZ;
								endZ = tempZ;
							}
							
							for (double percent = 0; percent < 100; percent += 0.15) {
								double rayX = startX + (((endX - startX) / 100) * percent),
										rayY = startY + (((endY - startY) / 100) * percent),
										rayZ = startZ + (((endZ - startZ) / 100) * percent);
								
								if (!mc.theWorld.getBlockState(new BlockPos(rayX, rayY, rayZ)).getBlock().equals(Blocks.air)) {
									eligible = false;
								}
								
							}
							
						}
						
						if (eligible)
							availableBlocks.add(pos);
						
						// Raytrace
//						MovingObjectPosition raytrace = mc.theWorld.rayTraceBlocks(new Vec3(x, y, z), new Vec3(current.getX(), current.getY(), current.getZ()), false);
//						if (raytrace != null && raytrace.typeOfHit == MovingObjectType.MISS) {
//							availableBlocks.add(pos);
//						}
					}
				}
			}
		}
		
		Collections.shuffle(availableBlocks);
		
		BlockPos newPos = BlockPos.ORIGIN;
		for (BlockPos pos : availableBlocks) {
			if (WorldUtils.getDistance(newPos.getX(), newPos.getY(), newPos.getZ(), target.getX(), target.getY(), target.getZ()) > WorldUtils.getDistance(pos.getX(), pos.getY(), pos.getZ(), target.getX(), target.getY(), target.getZ())
					|| pos.equals(target)) {
				newPos = pos;
				if (pos.equals(target)) {
					break;
				}
			}
		}
		
		if (newPos == BlockPos.ORIGIN) {
			path.clear();
			return false;
		}
		
		path.add(newPos);
		
		if (newPos.equals(target)) {
			return true;
		}
		
		return createPath(newPos, target, startTime);
		
	}
	
	public void renderPath() {
		
		ArrayList<Vec3> trailList = new ArrayList<Vec3>();
		for (BlockPos pos : path) {
			trailList.add(new Vec3(pos.getX(), pos.getY(), pos.getZ()));
		}
		
		Vec3 lastLoc = null;
		
		for (Vec3 loc: trailList) {
			
			if (lastLoc == null) {
				lastLoc = loc;
			}else {
				
				if (mc.thePlayer.getDistance(loc.xCoord, loc.yCoord, loc.zCoord) > 150) {
					
				}else {
					
					RenderUtils.drawLine(lastLoc.xCoord, lastLoc.yCoord, lastLoc.zCoord, loc.xCoord, loc.yCoord, loc.zCoord);
					
				}
				
				lastLoc = loc;
			}
			
		}
		
	}
	
}
