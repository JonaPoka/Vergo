package xyz.vergoclient.util.pathfinding;

import java.util.ArrayList;
import java.util.Collections;

import xyz.vergoclient.util.RenderUtils;
import xyz.vergoclient.util.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public class AStarPathFinder {
	
	public AStarPathFinder(long timeout, boolean goThoughBlocks) {
		this.timeout = timeout;
		this.goThoughBlocks = goThoughBlocks;
	}
	
	public long timeout;
	public boolean goThoughBlocks;
	
	// A* pathfinding basically works like this
	// Step 1: Get list of nodes with lowest f value
	// Step 2: if you have more than one node then select the one with the lowest distanceToEnd
	// Step 3: if you still have more pick one of the remaining ones
	// Step 4: check nodes around the selected node
	// Step 5: repeat until you reach the end
	
	// Node
	public static class Node{
		
		public Node(BlockPos pos, double distanceToStart, double distanceToEnd, Node previousNode) {
			this.pos = pos;
			this.distanceToStart = distanceToStart;
			this.distanceToEnd = distanceToEnd;
			this.previousNode = previousNode;
		}
		
		public double distanceToStart, distanceToEnd;
		public Node previousNode;
		public BlockPos pos;
		public boolean hasChecked = false;
		
		public double getFvalue() {
			return distanceToStart + distanceToEnd;
		}
		
	}
	
	public ArrayList<BlockPos> path = new ArrayList<>();
	
	public ArrayList<BlockPos> createPath(BlockPos start, BlockPos end, double distanceApart) {
		
		if (!Minecraft.getMinecraft().theWorld.getBlockState(start).getBlock().equals(Blocks.air) && !goThoughBlocks) {
//			NotificationManager.getNotificationManager().createNotification("Pathfinder", "Start point is inside of block, selecting point next to it", true, 1000, Type.WARNING, Color.RED);
			for (int x = -2; x < 2; x++)
				for (int y = -2; y < 2; y++)
					for (int z = -2; z < 2; z++)
						if (Minecraft.getMinecraft().theWorld.getBlockState(start.add(x, y, z)).getBlock().equals(Blocks.air)) {
							start = start.add(x, y, z);
							break;
						}
			if (!Minecraft.getMinecraft().theWorld.getBlockState(start).getBlock().equals(Blocks.air)) {
//				NotificationManager.getNotificationManager().createNotification("Pathfinder", "Failed to find air point next to the original start point", true, 1000, Type.WARNING, Color.RED);
				return new ArrayList<>();
			}
		}
		
		if (!Minecraft.getMinecraft().theWorld.getBlockState(end).getBlock().equals(Blocks.air) && !goThoughBlocks) {
//			NotificationManager.getNotificationManager().createNotification("Pathfinder", "End point is inside of block, selecting point next to it", true, 1000, Type.WARNING, Color.RED);
			for (int x = -2; x < 2; x++)
				for (int y = -2; y < 2; y++)
					for (int z = -2; z < 2; z++)
						if (Minecraft.getMinecraft().theWorld.getBlockState(end.add(x, y, z)).getBlock().equals(Blocks.air)) {
							end = end.add(x, y, z);
							break;
						}
			if (!Minecraft.getMinecraft().theWorld.getBlockState(end).getBlock().equals(Blocks.air)) {
//				NotificationManager.getNotificationManager().createNotification("Pathfinder", "Failed to find air point next to the original end point", true, 1000, Type.WARNING, Color.RED);
				return new ArrayList<>();
			}
		}
		
		boolean flipAfter = false;
		
		if (getBlockCountAroundPos(start) < getBlockCountAroundPos(end)) {
			BlockPos temp = start;
			start = end;
			end = temp;
			flipAfter = true;
		}
		
		path.clear();
		
		ArrayList<Node> nodes = new ArrayList<>();
		nodes.add(new Node(start, 0, WorldUtils.getDistance(start, end), null));
		
		// Prevents the program from freezing
		long antiFreeze = System.currentTimeMillis() + timeout;
		while (System.currentTimeMillis() < antiFreeze) {
			
			// Prevents it from getting stuck if there is no path the to end
			boolean breakHere = true;
			for (Node n : nodes) {
				if (!n.hasChecked) {
					breakHere = false;
				}
			}
			if (breakHere)
				break;
			
			// Finds nodes that are best to check
			Node nodeToCheck = null;
			ArrayList<Node> temp = new ArrayList<>();
			for (Node n : nodes) {
				if (temp.isEmpty() || (n.getFvalue() < temp.get(0).getFvalue() && !n.hasChecked)) {
					if (!n.hasChecked) {
						temp.clear();
						temp.add(n);
					}
				}
				else if (!temp.isEmpty() && temp.get(0).getFvalue() == n.getFvalue() && !n.hasChecked) {
					if (!n.hasChecked) {
						temp.add(n);
					}
				}
			}
			for (Node n : temp) {
				if (nodeToCheck == null || n.distanceToEnd < nodeToCheck.distanceToEnd) {
					nodeToCheck = n;
				}
			}
			
			// If it reached the end then return
			if (nodeToCheck.pos.equals(end)) {
				path.clear();
				Node backtrack = nodeToCheck;
				path.add(backtrack.pos);
				try {
					while ((backtrack = backtrack.previousNode) != null) {
						path.add(backtrack.pos);
//						backtrack = backtrack.previousNode;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				if (flipAfter)
					Collections.reverse(path);
				
				spacePath(distanceApart);
				
				
				return path;
			}
			
			// Debug lines
//			path.add(nodeToCheck.pos);
			
			// Recreates arraylist with added values
			nodeToCheck.hasChecked = true;
			nodes = reCreateNodeArrayList(nodeToCheck, end, nodes);
			
		}
		
		return new ArrayList<>();
		
	}
	
	public void createPath(BlockPos start, BlockPos end) {
		createPath(start, end, 1);
	}
	
	public void spacePath(double spacing) {
		
		if (path == null || path.isEmpty())
			return;
		
		ArrayList<BlockPos> newPath = new ArrayList<>();
		BlockPos lastPos = path.get(0);
		newPath.add(lastPos);
		if (spacing > 1) {
			for (BlockPos pos : path) {
				if ((!pos.equals(lastPos) && WorldUtils.getDistance(pos, lastPos) >= spacing) || path.indexOf(pos) == path.size()) {
					newPath.add(pos);
				}
			}
		}else if (spacing < 1) {
			if (spacing == 0) {
				spacing = 0.05;
//				spacing = 9.0E-4D;
			}
			for (BlockPos pos : path) {
				if ((!pos.equals(lastPos) && WorldUtils.getDistance(pos, lastPos) >= spacing) || path.indexOf(pos) == path.size()) {
					for (double x = 0; x < (lastPos.x - pos.x) / spacing; x += spacing)
						for (double y = 0; y < (lastPos.y - pos.y) / spacing; y += spacing)
							for (double z = 0; z < (lastPos.z - pos.z) / spacing; z += spacing)
								newPath.add(new BlockPos(pos.x + x, pos.y + y, pos.z + z));
					newPath.add(pos);
				}
			}
		}else {
			newPath = path;
		}
		path = newPath;
		
	}
	
	private int getBlockCountAroundPos(BlockPos pos) {
		int blockCount = 0;
		
		for (int x = -10; x < 10; x++)
			for (int y = -10; y < 10; y++)
				for (int z = -10; z < 10; z++)
					if (!Minecraft.getMinecraft().theWorld.getBlockState(pos.add(x, y, z)).getBlock().equals(Blocks.air))
						blockCount++;
		
		return blockCount;
	}
	
	private ArrayList<Node> reCreateNodeArrayList(Node nodeToCheck, BlockPos end, ArrayList<Node> existingNodes){
		
		nodeToCheck.hasChecked = true;
		
		// Creates new arraylist
		ArrayList<Node> nodes = new ArrayList<>();
		// Adds existing nodes
		nodes.addAll(existingNodes);
		
		ArrayList<Node> nodesToCheck = new ArrayList<AStarPathFinder.Node>();
		
		// Checks area around the node
		for (EnumFacing face : EnumFacing.VALUES) {
			// Creates new node
//			Node newNode = new Node(nodeToCheck.pos.offset(face), nodeToCheck.distanceToStart + 1, WorldUtils.getDistance(nodeToCheck.pos.offset(face), end), nodeToCheck);
			nodesToCheck.add(new Node(nodeToCheck.pos.offset(face), nodeToCheck.distanceToStart + 1, WorldUtils.getDistance(nodeToCheck.pos.offset(face), end), nodeToCheck));
			
		}
		
		nodesToCheck.add(new Node(nodeToCheck.pos.add(1, 0, 1), nodeToCheck.distanceToStart + 1,
				WorldUtils.getDistance(nodeToCheck.pos.add(1, 0, 1), end), nodeToCheck));
		nodesToCheck.add(new Node(nodeToCheck.pos.add(1, 0, -1), nodeToCheck.distanceToStart + 1,
				WorldUtils.getDistance(nodeToCheck.pos.add(1, 0, -1), end), nodeToCheck));
		nodesToCheck.add(new Node(nodeToCheck.pos.add(-1, 0, -1), nodeToCheck.distanceToStart + 1,
				WorldUtils.getDistance(nodeToCheck.pos.add(-1, 0, -1), end), nodeToCheck));
		nodesToCheck.add(new Node(nodeToCheck.pos.add(-1, 0, 1), nodeToCheck.distanceToStart + 1,
				WorldUtils.getDistance(nodeToCheck.pos.add(-1, 0, 1), end), nodeToCheck));
		
		nodesToCheck.add(new Node(nodeToCheck.pos.add(1, 1, 1), nodeToCheck.distanceToStart + 1,
				WorldUtils.getDistance(nodeToCheck.pos.add(1, 1, 1), end), nodeToCheck));
		nodesToCheck.add(new Node(nodeToCheck.pos.add(1, 1, -1), nodeToCheck.distanceToStart + 1,
				WorldUtils.getDistance(nodeToCheck.pos.add(1, 1, -1), end), nodeToCheck));
		nodesToCheck.add(new Node(nodeToCheck.pos.add(-1, 1, -1), nodeToCheck.distanceToStart + 1,
				WorldUtils.getDistance(nodeToCheck.pos.add(-1, 1, -1), end), nodeToCheck));
		nodesToCheck.add(new Node(nodeToCheck.pos.add(-1, 1, 1), nodeToCheck.distanceToStart + 1,
				WorldUtils.getDistance(nodeToCheck.pos.add(-1, 1, 1), end), nodeToCheck));
		
		nodesToCheck.add(new Node(nodeToCheck.pos.add(1, -1, 1), nodeToCheck.distanceToStart + 1,
				WorldUtils.getDistance(nodeToCheck.pos.add(1, -1, 1), end), nodeToCheck));
		nodesToCheck.add(new Node(nodeToCheck.pos.add(1, -1, -1), nodeToCheck.distanceToStart + 1,
				WorldUtils.getDistance(nodeToCheck.pos.add(1, -1, -1), end), nodeToCheck));
		nodesToCheck.add(new Node(nodeToCheck.pos.add(-1, -1, -1), nodeToCheck.distanceToStart + 1,
				WorldUtils.getDistance(nodeToCheck.pos.add(-1, -1, -1), end), nodeToCheck));
		nodesToCheck.add(new Node(nodeToCheck.pos.add(-1, -1, 1), nodeToCheck.distanceToStart + 1,
				WorldUtils.getDistance(nodeToCheck.pos.add(-1, -1, 1), end), nodeToCheck));
		
		nodesToCheck.add(new Node(nodeToCheck.pos.add(1, -1, 0), nodeToCheck.distanceToStart + 1,
				WorldUtils.getDistance(nodeToCheck.pos.add(1, -1, 0), end), nodeToCheck));
		nodesToCheck.add(new Node(nodeToCheck.pos.add(-1, -1, 0), nodeToCheck.distanceToStart + 1,
				WorldUtils.getDistance(nodeToCheck.pos.add(-1, -1, 0), end), nodeToCheck));
		nodesToCheck.add(new Node(nodeToCheck.pos.add(0, -1, -1), nodeToCheck.distanceToStart + 1,
				WorldUtils.getDistance(nodeToCheck.pos.add(0, -1, -1), end), nodeToCheck));
		nodesToCheck.add(new Node(nodeToCheck.pos.add(0, -1, 1), nodeToCheck.distanceToStart + 1,
				WorldUtils.getDistance(nodeToCheck.pos.add(0, -1, 1), end), nodeToCheck));
		
		nodesToCheck.add(new Node(nodeToCheck.pos.add(1, 1, 0), nodeToCheck.distanceToStart + 1,
				WorldUtils.getDistance(nodeToCheck.pos.add(1, 1, 0), end), nodeToCheck));
		nodesToCheck.add(new Node(nodeToCheck.pos.add(-1, 1, 0), nodeToCheck.distanceToStart + 1,
				WorldUtils.getDistance(nodeToCheck.pos.add(-1, 1, 0), end), nodeToCheck));
		nodesToCheck.add(new Node(nodeToCheck.pos.add(0, 1, -1), nodeToCheck.distanceToStart + 1,
				WorldUtils.getDistance(nodeToCheck.pos.add(0, 1, -1), end), nodeToCheck));
		nodesToCheck.add(new Node(nodeToCheck.pos.add(0, 1, 1), nodeToCheck.distanceToStart + 1,
				WorldUtils.getDistance(nodeToCheck.pos.add(0, 1, 1), end), nodeToCheck));
		
		for (Node newNode : nodesToCheck) {
			// Checks to see if it should actually add the node
			boolean add = true;
			for (Node n : nodes) {
				if (n.pos.equals(newNode.pos)) {
//								n.previousNode = nodeToCheck;
					add = false;
				}
			}

			if (add && !goThoughBlocks) {
				try {
					if (!Minecraft.getMinecraft().theWorld.getBlockState(newNode.pos).getBlock().equals(Blocks.air))
						add = false;
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			// Adds node if it fits the criteria
			if (add) {
				nodes.add(newNode);
			}
		}
		
		// Return the new arraylist
		return nodes;
		
	}
	
	public void renderPath() {
		
		try {
			ArrayList<Vec3> trailList = new ArrayList<Vec3>();
			for (BlockPos pos : path) {
				trailList.add(new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
			}
			
			Vec3 lastLoc = null;
			
			for (Vec3 loc: trailList) {
				
				if (lastLoc == null) {
					lastLoc = loc;
				}else {
					
					if (Minecraft.getMinecraft().thePlayer.getDistance(loc.xCoord, loc.yCoord, loc.zCoord) > 100) {
						
					}else {
						
						RenderUtils.drawLine(lastLoc.xCoord, lastLoc.yCoord, lastLoc.zCoord, loc.xCoord, loc.yCoord, loc.zCoord);
						
					}
					
					lastLoc = loc;
				}
				
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
}
