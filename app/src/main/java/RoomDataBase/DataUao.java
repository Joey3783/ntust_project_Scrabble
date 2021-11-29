package RoomDataBase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;
@Dao               // Data Access Object 用這個物件定義的function存取data
public interface DataUao {
//    String tableName = "PlayerInfo";
    /**=======================================================================================*/
//    @Insert(onConflict = OnConflictStrategy.REPLACE)//預設萬一執行出錯怎麼辦，REPLACE為覆蓋
//    void insertPlayerInfo(PlayerInfo playerInfo);
//    @Insert(onConflict = OnConflictStrategy.REPLACE)//預設萬一執行出錯怎麼辦，REPLACE為覆蓋
//    void insertHistoryWords(List<HistoryWords> historyWords);

    /**新增所有資料*/
//    @Query("INSERT INTO "+tableName+"(PlayerName,Level) VALUES(:PlayerName,:Level)")
//    void insertPlayerInfo(String PlayerName, String Level);

    /**=======================================================================================*/
    /**撈取全部資料*/
    @Query("SELECT * FROM playerinfo")
    List<PlayerInfo> displayAll();

    @Query("SELECT * FROM historywords where playerId = :id")
    List<HistoryWords> getHistoryWordsById(int id);

    /**撈取某個玩家的相關資料*/
//    @Query("SELECT * FROM " + tableName +" WHERE PlayerName = :PlayerName")
//    List<PlayerInfo> findDataByName(String PlayerName);

   // @Query("SELECT " +  +" FROM " + tableName)

    /**=======================================================================================*/
//    @Update
//    void updateData(PlayerInfo playerInfo);
//    /**更新資料*/
    @Query("UPDATE "+"PlayerInfo"+" SET PlayerName=:playername,Level=:Level  WHERE id = :id")
    void updateinfo(int id,String playername,String Level);


    /**=======================================================================================*/


//    /**刪除資料*/
//    @Query("DELETE  FROM " + tableName + " WHERE id = :id")
//    void deleteData(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long savePlayerInfo(PlayerInfo playerInfo); //會回饋一個long值，要用它來對應到historywords

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void savePlayerInfo(List<PlayerInfo> playerInfos);

    @Query("SELECT * FROM playerinfo WHERE id = :id")
    LiveData<PlayerInfo> getPlayerInfo(int id);



    //HistoryWords
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long saveHistoryWord(HistoryWords historyWords); //會insert，也會回饋long，然後就可以去對應到historywords


    //delete
    @Transaction
    @Query("DELETE FROM playerinfo  WHERE id = :id")
    void deletePlayerInfo(long id);

    /**
     * using {@link androidx.room.Relation} to query for pojo
     */
    @Transaction
    @Query("SELECT * FROM PlayerInfo where id= :playerId")
    LiveData<List<PlayerWithHistoryWords>> loadPersonHistoryWords(int playerId);



}
