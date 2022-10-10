package pene.gc.nuzlocke;

import com.mongodb.client.result.DeleteResult;
import emu.grasscutter.Grasscutter;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.command.commands.TeamCommand;
import emu.grasscutter.database.DatabaseManager;
import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.player.Player;
import pene.gc.nuzlocke.proto.PacketAvatarDelNotify;

import java.util.ArrayList;
import java.util.List;



public class Nuzlocke {
    public static void NuzlockeFunction(Player targetPlayer, Avatar deadAvatar) {
        try {
            long avatarGuid = targetPlayer.getTeamManager().getCurrentCharacterGuid(); //I can't lose it later
            if(targetPlayer.getTeamManager().getCurrentTeamInfo().size() == 1){
                //The game is handling it some other way but I'll keep it as a second barrier
                Grasscutter.getLogger().info("你输了，但我不能这么对你因为 YuiServer 可能会在此期间崩溃:)");
                return;
            }
            List<String> args = new ArrayList<>();
            args.add("remove");
            args.add(String.valueOf(targetPlayer.getTeamManager().getCurrentCharacterIndex()+1));

            TeamCommand Tc = new TeamCommand();
            Tc.execute(targetPlayer, targetPlayer, args);

            DeleteResult result = DatabaseManager.getGameDatastore().delete(deadAvatar);
            targetPlayer.getScene().broadcastPacket(new PacketAvatarDelNotify(avatarGuid));

            if(result.wasAcknowledged()){
                Grasscutter.getLogger().info(String.format("%s 已经飞升", deadAvatar.getAvatarData().getName()));
                CommandHandler.sendMessage(targetPlayer, String.format("你的朋友已经离开了你： %s", deadAvatar.getAvatarData().getName()));
            } else{
                Grasscutter.getLogger().error("出现了数据库错误");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
