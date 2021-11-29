package RoomDataBase;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class PlayerWithHistoryWords {

    // relation是一個查詢的工具，方便我們把資料調閱出來，但就是要額外設定playerId才能把playerInfo historywords搭建起來！
    @Embedded
    public  PlayerInfo playerInfo;

    @Relation(
            parentColumn = "id",
            entityColumn = "playerId",
            entity = HistoryWords.class
    )
    private List<HistoryWords> historyWords;

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }

    public void setPlayerInfo(PlayerInfo playerInfo) {
        this.playerInfo = playerInfo;
    }

    public List<HistoryWords> getHistoryWords() {
        return historyWords;
    }

    public void setHistoryWords(List<HistoryWords> historyWords) {
        this.historyWords = historyWords;
    }
}
