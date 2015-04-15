package ru.kpfu.itis.cll;

import java.sql.*;

public class DBUtils {
    private static Connection cn;
    private static Statement st;

    // запись события в БД, возвращает id события
    public static int insertEvent(String company, boolean type) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        cn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/event_extraction", "postgres", "postgres");
        st = cn.createStatement();

        // type -> type_id   :   false - > 0 (announcement), true -> 1 (release)
        int typeID;
        if (!type) {
            typeID = 0;
        }
        else {
            typeID = 1;
        }
        ResultSet rsID = st.executeQuery("insert into events (company, type_id) values ('" + company + "', '" + typeID + "') returning id;");
        rsID.next();
        return rsID.getInt("id");
    }
}
