package com.agrovet.pos.database;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.agrovet.pos.models.Abono;
import java.util.List;

@Dao
public interface AbonoDao {
    @Query("SELECT * FROM abonos ORDER BY id DESC")
    LiveData<List<Abono>> getAllAbonos();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Abono abono);

    @Delete
    void delete(Abono abono);
}
