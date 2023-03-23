package sql;

import java.sql.*;
import java.util.Scanner;

public class Nation {

    //dati per la connessione
    private final static String URL = System.getenv("URL");
    private final static String USER = System.getenv("USER");
    private final static String PASSWORD = System.getenv("PASSWORD");

    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);

        //apro una connessione
        try(Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {

            String query = """
                    
                    select cr.name, cr.country_id, r.name, r.continent_id
                    from regions r
                                        
                    join continents c
                    on r.continent_id  = c.continent_id
                                        
                    join countries cr
                    on r.region_id = cr.region_id
                    
                    where cr.name like ?
                    
                    order by cr.name
                    
                    """;

            //chiedo all'utente di inserire una stringa per filtrare la ricerca
            System.out.print("Insert a nation: ");
            String wordForFilter = scan.nextLine();


            // creo il PreparedStatement per avviare la query
            try(PreparedStatement ps = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {

                ps.setString(1, "%" + wordForFilter + "%");

                // eseguo la query e metto il risultato nel ResultSet
               try(ResultSet rs = ps.executeQuery()) {

                   if (!rs.next()) {
                       System.out.println("No nation with this name ");
                   } else {
                       // devo tornare indietro per recuperare la prima riga
                       rs.beforeFirst();
                   }

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

            System.out.println("Insert id ");
            int nationId = Integer.parseInt(scan.nextLine());

            String queryId = """
                                        
                    select l.language, cs.year, cs.population, cs.gdp
                    from countries c\s
                                        
                    join country_languages cl on c.country_id = cl.country_id\s
                    join languages l on l.language_id = cl.language_id\s
                                        
                    join country_stats cs on cs.country_id = c.country_id
                                        
                    where c.country_id = ?
                    
                    order by cs.year desc
                    
                    limit 1
                    
                                        
                    """;

            try(PreparedStatement ps = conn.prepareStatement(queryId, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {

                ps.setInt(1, nationId);

                try(ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        System.out.println("No nation with this id ");
                    } else {
                        // devo tornare indietro per recuperare la prima riga
                        rs.beforeFirst();
                    }

                    //parso il ResultSet
                    while(rs.next()) {

                        String language = rs.getString("l.language");
                        int year = rs.getInt("cs.year");
                        int population = rs.getInt("cs.population");
                        String gdp = rs.getString("cs.gdp");

                        //stampo nel terminale i dati del db

                        System.out.printf("%40s%15d%50d%15s", language, year, population, gdp);
                        System.out.println();

                    }

                }


            }

        }catch (SQLException e) {
            e.printStackTrace();
        }

        scan.close();

    }
}
