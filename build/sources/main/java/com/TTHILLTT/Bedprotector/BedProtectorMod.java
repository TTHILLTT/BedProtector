import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.block.BlockBed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
@Mod(modid = "BedProtector", version = "1.0")
public class BedProtectorMod {

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new BedEventHandler());
    }

    public static class BedEventHandler {
        private int tickCounter = 0;

        @SubscribeEvent
        public void onPlayerTick(PlayerTickEvent event) {
            if (event.phase != TickEvent.Phase.START) return;

            tickCounter++;
            if (tickCounter % 20 != 0) return; // 每秒检测一次

            EntityPlayer player = event.player;
            World world = player.worldObj;

            // 检测周围10格内的床
            boolean foundBed = false;
            BlockPos playerPos = new BlockPos(player.posX, player.posY, player.posZ);

            for (int x = -10; x <= 10; x++) {
                for (int y = -3; y <= 3; y++) {
                    for (int z = -10; z <= 10; z++) {
                        BlockPos checkPos = playerPos.add(x, y, z);
                        if (world.getBlockState(checkPos).getBlock() instanceof BlockBed) {
                            foundBed = true;
                            break;
                        }
                    }
                    if (foundBed) break;
                }
                if (foundBed) break;
            }

            // 找到床时创建保护墙
            if (foundBed) {
                BlockPos center = new BlockPos(player.posX, player.posY, player.posZ);

                // 在玩家周围创建3x3的围墙
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x == 0 && z == 0) continue; // 跳过玩家位置

                        // 地面层
                        world.setBlockState(center.add(x, 0, z),
                                Blocks.obsidian.getDefaultState(), 3);

                        // 第二层
                        world.setBlockState(center.add(x, 1, z),
                                Blocks.obsidian.getDefaultState(), 3);
                    }
                }
            }
        }
    }
}