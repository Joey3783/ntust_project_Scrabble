package RoomDataBase;

import android.content.Context;
import android.provider.ContactsContract;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.ArrayList;

//Database 真正實體，{資料綁定的MyData,資料庫版本,是否將資料導出至文件}
@Database(entities = {PlayerInfo.class,HistoryWords.class},version = 1,exportSchema = true)
public abstract class DataBase extends RoomDatabase {
    public static final String DB_NAME = "RecordData.db";
    private static volatile DataBase instance;
    public abstract DataUao getDataUao();
//
    public static synchronized DataBase getInstance(Context context){
        if(instance == null){
            instance = create(context);//創建新的資料庫
        }
        return instance;
    }

    private static DataBase create(final Context context){
        return Room.databaseBuilder(
                context,
                DataBase.class,
                DB_NAME).build();
//        return Room.databaseBuilder(context.getApplicationContext(), DataBase.class,DB_NAME )
//                .addMigrations(MIGRATION_1_2).build();
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS " + "playerInfo" + "( " +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "PlayerName TEXT, " +
                    "Level TEXT" +
                    ");");
//            database.execSQL("create unique index index_MyTable_PlayerName on MyTable(PlayerName)");
        }
    };







}
