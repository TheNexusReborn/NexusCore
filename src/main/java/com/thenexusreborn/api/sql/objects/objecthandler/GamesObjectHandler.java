package com.thenexusreborn.api.sql.objects.objecthandler;

import com.thenexusreborn.api.gamearchive.*;
import com.thenexusreborn.api.sql.objects.ObjectHandler;
import com.thenexusreborn.api.sql.objects.SQLDatabase;
import com.thenexusreborn.api.sql.objects.Table;

import java.sql.SQLException;
import java.util.List;

public class GamesObjectHandler extends ObjectHandler {
    public GamesObjectHandler(Object object, SQLDatabase database, Table table) {
        super(object, database, table);
    }
    
    @Override
    public void afterLoad() {
        GameInfo gameInfo = (GameInfo) object;
        try {
            List<GameAction> gameActions = database.get(GameAction.class, "gameId", gameInfo.getId());
            gameInfo.getActions().addAll(gameActions);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void afterSave() {
        GameInfo gameInfo = (GameInfo) object;
        for (GameAction action : gameInfo.getActions()) {
            action.setGameId(gameInfo.getId());
            database.saveSilent(action);
        }
    }
}
