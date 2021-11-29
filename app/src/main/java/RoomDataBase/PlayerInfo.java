package RoomDataBase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "PlayerInfo") // 我們的資料在java這層以物件形式存在
//,indices = {@Index(value = {"PlayerName"},
//        unique = true)}

public class PlayerInfo {

    @PrimaryKey(autoGenerate = true)//設置是否使ＩＤ自動累加
    private long id;
    @ColumnInfo(name = "PlayerName")
    private String PlayerName;
    @ColumnInfo(name = "Level")
    private String Level;


    public PlayerInfo() {
    }

    public PlayerInfo(String playerName, String level) {
        this.PlayerName = playerName;
        this.Level = level;
    }


    public PlayerInfo(int id, String playerName, String level) {
        this.id = id;
        this.PlayerName = playerName;
        this.Level = level;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPlayerName() {
        return PlayerName;
    }

    public void setPlayerName(String playerName) {
        this.PlayerName = playerName;
    }

    public String getLevel() {
        return Level;
    }

    public void setLevel(String level) {
        this.Level = level;
    }


}



