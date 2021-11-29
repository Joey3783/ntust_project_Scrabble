package RoomDataBase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "HistoryWords"
        ,foreignKeys = {
        @ForeignKey(        // 用foreignkey是因為要自動把id綁住
        entity = PlayerInfo.class,
        parentColumns = "id",
        childColumns = "playerId",
        onDelete = CASCADE, // 遇到刪除，就直接刪
        onUpdate = CASCADE)}
        , indices = {@Index(value = {"playerId"})}
        )
public class HistoryWords {
    @PrimaryKey(autoGenerate = true)
    private long id;             // 自己的id
    @ColumnInfo(name = "Date")
    private String Date;         // 日期、時間
    @ColumnInfo(name = "Vocabulary")
    private String Vocabulary;   // 歷史單字 (save by json form)
    @ColumnInfo(name = "playerId")
    private long playerId;       // 跟PlayerInfo連結的ID


    public HistoryWords(){

    }
    @Ignore
    public HistoryWords(String Date, String Vocabulary, long playerId){
        this.Date = Date;
        this.Vocabulary = Vocabulary;
        this.playerId = playerId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getVocabulary() {
        return Vocabulary;
    }

    public void setVocabulary(String vocabulary) {
        Vocabulary = vocabulary;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }
}
