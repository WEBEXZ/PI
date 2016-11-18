package om.qps.w.pimobile;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class UsuariosDB extends SQLiteOpenHelper {

    String[] datos = new String[1];

    public String[] datosRegistro(String[] data) {
        data[0] = "CREATE TABLE Registro(clave TEXT, status INTEGER)";
        return data;
    }

    UsuariosDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String[] array = datosRegistro(datos);
        for(int i = 0; i < array.length; i++ ) {
            db.execSQL(array[i]);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Registro");
        String[] array = datosRegistro(datos);
        for(int i = 0; i < array.length; i++ ) {
            db.execSQL(array[i]);
        }
    }
}
