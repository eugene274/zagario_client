package zagar.network.handlers;

import org.jetbrains.annotations.NotNull;
import protocol.CommandCellNames;
import protocol.model.CellsName;
import zagar.Game;
import zagar.util.JSONDeserializationException;
import zagar.util.JSONHelper;

/**
 * Created by eugene on 12/17/16.
 */
public class PacketHandlerCellNames {
    public PacketHandlerCellNames(@NotNull String json) {
        CommandCellNames command;
        try {
            command = JSONHelper.fromJSON(json, CommandCellNames.class);
        } catch (JSONDeserializationException e) {
            e.printStackTrace();
            return;
        }

        for(CellsName name : command.getCellsNames()){
            Game.cellNames.put(name.getCellId(), name.getName());
        }

    }
}
