package zagar.network.handlers;

import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.CommandReplicate;
import zagar.util.Colors;
import zagar.util.JSONDeserializationException;
import zagar.util.JSONHelper;
import zagar.view.Cell;
import zagar.Game;
import org.jetbrains.annotations.NotNull;

public class PacketHandlerReplicate {
  @NotNull
  private static final Logger log = LogManager.getLogger(PacketHandlerReplicate.class);

  public PacketHandlerReplicate(@NotNull String json) {
    CommandReplicate commandReplicate;
    try {
      commandReplicate = JSONHelper.fromJSON(json, CommandReplicate.class);
    } catch (JSONDeserializationException e) {
      e.printStackTrace();
      return;
    }

    Cell[] gameCells = new Cell[commandReplicate.getCells().length];
    Game.playerCells.clear();
    for (int i = 0; i < commandReplicate.getCells().length; i++) {
      protocol.model.Cell c = commandReplicate.getCells()[i];
      gameCells[i] = new Cell(c.getX(), c.getY(), c.getSize(), c.getCellId(), c.isVirus());
      // ROTATION FIX
      for (Cell c2: Game.cells){
        if(c2 != null && c2.id == c.getCellId()){
          gameCells[i].setRotationAngle(c2.getRotationAngle());
          break;
        }
      }

      // COLORIZATION
      Colors color = Colors.DEEP_PURPLE;
      if (c.getPlayerId() != -1) {
        Game.playerColors.putIfAbsent(c.getPlayerId(), Colors.getRandom());
        color = Game.playerColors.get(c.getPlayerId());
      }
      gameCells[i].setColor(color.getR(), color.getG(), color.getB());

      if(c.getPlayerId() == Game.playerID){
        log.debug("Player cell added");
        Game.playerCIDs.add(c.getCellId());
      }
    }

    if(Game.playerID == 0 && Game.playerCIDs.size() == 0){
      log.warn("playerID might be uninitialized");
    }

    Game.cells = gameCells;

    Cell[] foods = new Cell[commandReplicate.getFood().length];
    for(int i = 0; i < commandReplicate.getFood().length; i++){
      protocol.model.Food f = commandReplicate.getFood()[i];
      foods[i] = new Cell(f.getX(), f.getY(), 10f, -1, false);
      foods[i].setRotating(false);
      foods[i].setStaticVerges(20);
      foods[i].setColor(Colors.CYAN.getR()/2, Colors.CYAN.getG()/2, Colors.CYAN.getB()/2);
    }

    Game.foods = foods;


    //TODO
/*    if (b == null) return;
    b.order(ByteOrder.LITTLE_ENDIAN);
    short destroy = b.getShort(1);
    int offset = 3;
    for (int i = 0; i < destroy; i++) {
      for (int i2 = 0; i2 < Game.cellsNumber; i2++) {
        Cell c = Game.cells[i2];
        if (c != null) {
          if (c.id == b.getInt(offset + 4)) {
            Game.cells[i2] = null;
            if (Game.playerCells.contains(c)) {
              Game.playerCells.remove(c);
            }
            System.out.println("Removing " + c.id + " <" + c.name + ">");
            break;
          }
        }
      }
      offset += 8;
    }

    offset = addCell(offset, b);

    offset += 4;

    int destroyCells = b.getInt(offset);

    offset += 4;

    for (int i = 0; i < destroyCells; i++) {
      for (int i2 = 0; i2 < Game.cellsNumber; i2++) {
        Cell c = Game.cells[i2];
        if (c != null) {
          if (c.id == b.getInt(offset)) {
            Game.cells[i2] = null;
            if (Game.playerCells.contains(c)) {
              Game.playerCells.remove(c);
            }
            System.out.println("Removing(2) " + c.id + " <" + c.name + ">");
            break;
          }
        }
      }
      offset += 4;
    }*/
  }

  /*private int addCell(int offset, @NotNull ByteBuffer b) {
    int cellID = b.getInt(offset);
    if (cellID == 0) return offset;
    int x = b.getInt(offset + 4);
    int y = b.getInt(offset + 8);
    short size = b.getShort(offset + 12);

    byte red = b.get(offset + 14);
    byte green = b.get(offset + 15);
    byte blue = b.get(offset + 16);

    boolean flag = false;

    for (int i = 0; i < Game.cellsNumber; i++) {
      Cell c = Game.cells[i];
      if (c != null) {
        if (c.id == cellID) {
          flag = true;
        }
      }
    }

    byte flags = b.get(offset + 17);
    boolean virus = (flags & 1) == 1;

    if ((flags & 2) == 1) {
      offset += 4;
    }
    if ((flags & 4) == 1) {
      offset += 8;
    }
    if ((flags & 8) == 1) {
      offset += 16;
    }

    offset += 18;
    String name = "";
    while (b.getShort(offset) != 0) {
      name += b.getChar(offset);
      offset += 2;
    }

    if (!flag) {
      log.info("Adding new cell " + cellID + " <" + name + ">" + " /" + Game.cellsNumber + "/");
      Cell cell = new Cell(x, y, size, cellID, virus);
      if (name.length() > 0) {
        Game.cellNames.put(cellID, name);
      }
      cell.setColor(red, green, blue);
      Game.addCell(cell);
      cell.tick();
    } else {
      for (Cell cell : Game.cells) {
        if (cell != null) {
          if (cell.id == cellID) {
            cell.x = x;
            cell.y = y;
            cell.size = size;
            if (name.length() > 0) {
              cell.name = name;
              Game.cellNames.put(cellID, name);
            }
            cell.setColor(red, green, blue);
          }
        }
      }
    }

    offset += 2;
    if (b.getInt(offset) != 0) {
      offset = addCell(offset, b);
    }
    return offset;
  }*/
}
