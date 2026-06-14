package com.agrovet.pos.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.agrovet.pos.models.VentaItem;
import java.util.List;

@Dao
public interface VentaItemDao {
    @Insert
    void insertAll(List<VentaItem> items);

    @Insert
    void insert(VentaItem item);

    @Query("SELECT * FROM venta_items WHERE venta_id = :ventaId")
    List<VentaItem> getItemsByVenta(Integer ventaId);

    @Query("DELETE FROM venta_items WHERE venta_id = :ventaId")
    void deleteByVentaId(Integer ventaId);
}
