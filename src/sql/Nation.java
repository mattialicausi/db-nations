package sql;

import java.sql.*;

public class Nation {

    //dati per la connessione
    private final static String URL = System.getenv("URL");
    private final static String USER = System.getenv("USER");
    private final static String PASSWORD = System.getenv("PASSWORD");

    public static void main(String[] args) {

        //apro una connessione
        try(Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {

            String query = """
                    
                    select cr.name, cr.country_id, r.name, r.continent_id
                    from regions r
                                        
                    join continents c
                    on r.continent_id  = c.continent_id
                                        
                    join countries cr
                    on r.region_id = cr.region_id
                    order by cr.name
                    
                    """;

            // creo il PreparedStatement per avviare la query
            try(PreparedStatement ps = conn.prepareStatement(query)) {

                // eseguo la query e metto il risultato nel ResultSet
               try(ResultSet rs = ps.executeQuery()) {

                   //parso il ResultSet
                   while(rs.next()) {

                       String nationName = rs.getString("cr.name");
                       int nationId = rs.getInt("cr.country_id");

                       String regionName = rs.getString("r.name");
                       int regionId = rs.getInt("r.continent_id");

                        //stampo nel terminale i dati del db
                       System.out.printf("%40s%15d%50s%15d", nationName, nationId, regionName, regionId);
                       System.out.println();


                   }
               }

            }

        }catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
