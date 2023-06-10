package tvz.java.rafaelprojekt.database;

import javafx.scene.image.Image;
import tvz.java.rafaelprojekt.main.MainApplication;
import tvz.java.rafaelprojekt.entity.*;
import java.io.FileReader;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tvz.java.rafaelprojekt.exceptions.DatabaseQueryException;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

import java.sql.*;


public class DatabaseQueries {
    private static Logger logger = LoggerFactory.getLogger(DatabaseQueries.class);
    private static final String DB_URL = "dat//server.properties";


    public static Connection getConnection(){
        Properties properties = new Properties();
        try
        {
           FileReader fr = new FileReader(DB_URL);
            properties.load(fr);
            fr.close();



        }
        catch (IOException e)
        {
            logger.debug("Error while loading DB properties");
            logger.debug(e.getMessage());
            throw new DatabaseQueryException("Error while loading DB properties");
        }
        String dbUrl =properties.getProperty("url");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(dbUrl, username, password);
        }


        catch(SQLException e)
        {
            logger.debug("Error while loading database.", e);

            throw new DatabaseQueryException("Error while loading database");
        }

        return connection;
    }

    public static List<Rank> getRanks()
    {
        List<Rank> rankList = new ArrayList<>();
        try(Connection connection = getConnection())
        {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM PROFILES_RANK");
            while(resultSet.next())
            {
                Long id = resultSet.getLong("ID");
                String name = resultSet.getString("NAME");
                int value = resultSet.getInt("VALUE_NUM");
                Rank rank = new Rank(id, name, value);
                rankList.add(rank);
            }
            resultSet.close();
            statement.close();
        }
        catch (SQLException e)
        {

            logger.debug("Error while getting ranks from the database", e);
            throw new DatabaseQueryException("Error while getting ranks from the database");
        }
        return rankList;
    }

    public static List<Champion> getChampions()
    {
        List<Champion> championList = new ArrayList<>();
        try(Connection connection = getConnection())
        {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM CHAMPIONS");
            while(resultSet.next())
            {
                Long id = resultSet.getLong("ID");
                String name = resultSet.getString("NAME");
                Champion champion = new Champion(id, name);
                championList.add(champion);
            }
            resultSet.close();
            statement.close();

        }
        catch(SQLException e)
        {
            logger.debug("Error while getting champions from the database", e);

            throw new DatabaseQueryException("Error while getting champions from the database");
        }
        return championList;
    }

    public static Champion getChampionById(Long id)
    {
        Champion result = null;
        for(Champion c : MainApplication.championList)
        {
            if(c.getId().equals(id))
                result=c;

        }
        if(result == null)
        {

            logger.debug("Error while getting the champion by ID");
            throw new DatabaseQueryException("Error while getting the champion by ID");
        }
        return result;
    }





    public static List<Match> getAllMatches()
    {
        List<Match> matchList = new ArrayList<>();
        try(Connection connection = getConnection())
        {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM MATCHES");
            while(resultSet.next())
            {

                Long id = resultSet.getLong("ID");
                Long redId = resultSet.getLong("RED_ID");
                Long blueId = resultSet.getLong("BLUE_ID");
                Long winnerId = resultSet.getLong("WINNER_ID");
                int redKills = resultSet.getInt("RED_KILLS");
                int blueKills = resultSet.getInt("BLUE_KILLS");
                Long REDChampionId = resultSet.getLong("RED_CHAMPION_ID");
                Long blueChampionId = resultSet.getLong("BLUE_CHAMPION_ID");
                Champion red = getChampionById(REDChampionId);
                Champion blue = getChampionById(blueChampionId);
                LocalDate matchDate = resultSet.getDate("MATCH_DATE").toLocalDate();
                Match match = new Match(id,redId, blueId, winnerId, redKills, blueKills, red, blue, matchDate);
                matchList.add(match);
            }

            resultSet.close();
            statement.close();
        }
        catch(SQLException e)
        {
            logger.debug("Error while getting matches from the database.", e);
            throw new DatabaseQueryException("Error while getting matches from the database.");


        }
        return matchList;
    }

    public static List<Match> getMatchByProfile(Long profileId)
    {
        List<Match> matchList = new ArrayList<>();
        for(Match m : MainApplication.matchList)
        {
            if((m.getRedSideId().equals(profileId)) || (m.getBlueSideId().equals(profileId)))
            {
                matchList.add(m);
            }
        }

        return matchList;
    }



    public static List<Profile> getProfiles()
    {
        List<Profile> profileList = new ArrayList<>();
        try(Connection connection = getConnection())
        {
            Statement statement = connection.createStatement();
            ResultSet resultSet =statement.executeQuery("SELECT * FROM PROFILES");
            while(resultSet.next())
            {
                Long id = resultSet.getLong("ID");
                String name = resultSet.getString("NAME");
                int level = resultSet.getInt("LEVEL");
                Long rankId = resultSet.getLong("RANK_ID");
                Long serverId = resultSet.getLong("SERVER_ID");
                int wins = resultSet.getInt("WINS_NUMBER");
                int loses = resultSet.getInt("LOSS_NUMBER");
                Optional<Rank> rank;
                rank = MainApplication.rankList.stream().filter(r -> r.id().equals(rankId)).findFirst();
                Server server = null;
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM SERVERS WHERE ID = ?");
                preparedStatement.setLong(1,serverId);
                ResultSet resultSet1 = preparedStatement.executeQuery();
                while(resultSet1.next()){
                    for(Server s: Server.values())
                    {
                        if(s.getCode().equals(resultSet1.getString("CODE")))
                        {
                            server = s;
                        }
                    }
                }
                resultSet1.close();
                List<Match> matchList = getMatchByProfile(id);
                Set<Match> matchSet = new HashSet<>(matchList);

                Profile profile = new Profile(id, name, new Image("C:\\Users\\Rafael\\IdeaProjects\\Rafael-Projekt\\src\\main\\resources\\tvz\\java\\rafaelprojekt\\main\\images\\profilePicture.jpg"), level, rank.get(), server, wins, loses, matchSet);
                profileList.add(profile);
                preparedStatement.close();
            }
            resultSet.close();
            statement.close();
        }
        catch (SQLException e)
        {
            logger.debug("Error while getting profiles from the database", e);
            throw new DatabaseQueryException("Error while getting profiles from the database.");
        }
        return profileList;
    }

    public static void editProfile(Profile profile)
    {
        try(Connection connection = getConnection())
        {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE PROFILES SET NAME = ?, LEVEL = ?, RANK_ID = ?, SERVER_ID = ?, WINS_NUMBER=?, LOSS_NUMBER=? WHERE ID=?");
            preparedStatement.setString(1,profile.getUsername());
            preparedStatement.setInt(2,profile.getLevel());
            preparedStatement.setLong(3, profile.getRank().id());
            preparedStatement.setLong(4, (profile.getServer().ordinal()+1));
            preparedStatement.setInt(5, profile.getWins());
            preparedStatement.setInt(6, profile.getLoses());
            preparedStatement.setLong(7,profile.getId());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        }
        catch(SQLException e)
        {
            logger.debug("Error while editing a profile.", e);
            throw new DatabaseQueryException("Error while editing a profile.");
        }
        for(Profile p:MainApplication.profileList)
        {
            if(p.getId().equals(profile.getId()))
            {
                p.setUsername(profile.getUsername());
                p.setWins(profile.getWins());
                p.setLoses(profile.getLoses());
                p.setLevel(profile.getLevel());
                p.setProfilePicture(profile.getProfilePicture());
                p.setRank(profile.getRank());
                p.setServer(profile.getServer());
            }


        }
    }

    public static void addProfile(Profile profile)
    {
        try(Connection connection = getConnection())
        {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO PROFILES(NAME,LEVEL,RANK_ID, SERVER_ID, WINS_NUMBER, LOSS_NUMBER) VALUES(?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, profile.getUsername());
            preparedStatement.setInt(2, profile.getLevel());
            preparedStatement.setLong(3, profile.getRank().id());
            preparedStatement.setLong(4, profile.getServer().ordinal()+1);
            preparedStatement.setInt(5, profile.getWins());
            preparedStatement.setInt(6, profile.getLevel());
            preparedStatement.executeUpdate();
            Long profileId=0L;
            try(ResultSet generatedKeys = preparedStatement.getGeneratedKeys())
            {
                if(generatedKeys.next())
                {
                    profileId=generatedKeys.getLong(1);
                    profile.setId(profileId);

                    MainApplication.profileList.add(profile);
                }
            }
        preparedStatement.close();
        }
        catch (SQLException e)
        {
                logger.debug("Error while adding a profile", e);
                throw new DatabaseQueryException("Error while adding a profile.");
        }
    }

    public static void deleteProfile(Long id)
    {

        try(Connection connection = getConnection())
        {
            List<Long> idList = new ArrayList<>();
            PreparedStatement preparedStatement=connection.prepareStatement("SELECT ID FROM MATCHES WHERE RED_ID = ? OR BLUE_ID = ?");
            preparedStatement.setLong(1, id);
            preparedStatement.setLong(2, id);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next())
            {
               idList.add(rs.getLong("ID"));
            }
            rs.close();
          preparedStatement = connection.prepareStatement("DELETE FROM MATCHES WHERE RED_ID = ? OR BLUE_ID = ?");
            preparedStatement.setLong(1, id);
            preparedStatement.setLong(2, id);
            preparedStatement.executeUpdate();
            preparedStatement=connection.prepareStatement("DELETE FROM PROFILES WHERE ID = ?");
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();

            Profile.removeById(id);

            for(Long l : idList)
                Match.removeById(l);

            preparedStatement.close();
        }
        catch (SQLException e)
        {
            logger.debug("Error while deleting a profile", e);
            throw new DatabaseQueryException("Error while deleting a profile.");
        }
    }

    public static void editChampion(String name, Long id)
    {
        try(Connection connection = getConnection())
        {
            PreparedStatement preparedStatement=connection.prepareStatement("UPDATE CHAMPIONS SET NAME = ? WHERE ID = ? ");
            preparedStatement.setString(1,name);
            preparedStatement.setLong(2, id);
            preparedStatement.executeUpdate();

            preparedStatement.close();
        }

        catch(SQLException e)
        {
            logger.debug("Error while editing a champion", e);
            throw new DatabaseQueryException("Error while adding a champion.");
        }
    }

    public static void deleteChampion(Long id)
    {

        try(Connection connection = getConnection())
        {
            PreparedStatement preparedStatement=connection.prepareStatement("DELETE FROM CHAMPIONS WHERE ID = ?");
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();


            preparedStatement.close();

            MainApplication.championList.remove(Champion.getById(id));
        }
        catch(SQLException e)
        {
            logger.debug("Error while deleting a champion", e);
            throw new DatabaseQueryException("Error while deleting a champion.");
        }
    }

    public static void addChampion(String name)
    {
        try(Connection connection = getConnection())
        {
            PreparedStatement preparedStatement=connection.prepareStatement("INSERT INTO CHAMPIONS(NAME) VALUES(?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();
            Long championId=0L;
            try(ResultSet generatedKeys = preparedStatement.getGeneratedKeys())
            {
                if(generatedKeys.next())
                {
                    championId=generatedKeys.getLong(1);

                    MainApplication.championList.add(new Champion(championId, name));

                }
            }
            preparedStatement.close();
        }
        catch(SQLException e)
        {
            logger.debug("Error while adding a champion.", e);
            throw new DatabaseQueryException("Error while adding a champion.");
        }

    }


    public static void addNewMatch(Match match)
    {
        try(Connection connection = getConnection())
        {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO MATCHES(RED_ID, BLUE_ID, WINNER_ID, RED_KILLS, BLUE_KILLS, RED_CHAMPION_ID, BLUE_CHAMPION_ID, MATCH_DATE) VALUES(?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1,match.getRedSideId());
            preparedStatement.setLong(2, match.getBlueSideId());
            preparedStatement.setLong(3, match.getWinnerId());
            preparedStatement.setInt(4, match.getRedKills());
            preparedStatement.setInt(5, match.getBlueKills());
            preparedStatement.setLong(6, match.getRedChampion().getId());
            preparedStatement.setLong(7, match.getBlueChampion().getId());
            Date date = Date.valueOf(match.getMatchDate());
            preparedStatement.setDate(8, date);
            preparedStatement.executeUpdate();
            Long matchId=0L;
            try(ResultSet generatedKeys = preparedStatement.getGeneratedKeys())
            {
                if(generatedKeys.next())
                {
                    matchId=generatedKeys.getLong(1);
                    match.setId(matchId);
                    MainApplication.matchList.add(match);
                    for(Profile p : MainApplication.profileList)
                    {
                        if(p.getId().equals(match.getBlueSideId()) || p.getId().equals(match.getRedSideId()))
                            p.getMatchSet().add(match);
                    }
                }
            }

            if(match.getRedSideId().equals(match.getWinnerId()))
            {
                ProfileGetter.updateWinsLosses(match.getRedSideId(), match.getBlueSideId(), true);
                updateDatabaseWinsLosses(match.getRedSideId(), match.getRedSide().getWins()+1, match.getBlueSideId(), match.getBlueSide().getLoses()+1);
            }
            else {ProfileGetter.updateWinsLosses(match.getBlueSideId(), match.getRedSideId(), true);
                updateDatabaseWinsLosses(match.getBlueSideId(), match.getBlueSide().getWins()+1,  match.getRedSideId(), match.getRedSide().getLoses()+1);


            }

            preparedStatement.close();

        }
        catch (SQLException e)
        {
            logger.debug("Error while adding a new match.", e);
            throw new DatabaseQueryException("Error while adding a new match.");
        }
    }

    public static void updateDatabaseWinsLosses(Long winner, int wins, Long loser, int losses)
    {
        try(Connection connection = getConnection())
        {

            if(winner != null)
            {PreparedStatement preparedStatement = connection.prepareStatement("UPDATE PROFILES SET WINS_NUMBER = ? WHERE ID = ?");
                preparedStatement.setInt(1, wins);
                preparedStatement.setLong(2, winner);
                preparedStatement.executeUpdate();
            preparedStatement.close();}
            if(loser !=null)
            {
                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE PROFILES SET LOSS_NUMBER = ? WHERE ID = ?");
                preparedStatement.setInt(1, losses);
                preparedStatement.setLong(2, loser);
                preparedStatement.executeUpdate();

                preparedStatement.close();
            }


        }
        catch (SQLException e)
        {
            logger.debug("Error while updating wins/losses in the database", e);
            throw new DatabaseQueryException("Error while updating wins/losses in the database");
        }
    }

    public static void deleteMatch(Match match)
    {
        try(Connection connection = getConnection())
        {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM MATCHES WHERE ID = ?");
            preparedStatement.setLong(1, match.getId());
            preparedStatement.executeUpdate();


            if(match.getRedSideId().equals(match.getWinnerId()))
            {

                ProfileGetter.updateWinsLosses(match.getRedSideId(), match.getBlueSideId(), false);

                if(match.getRedSide().getWins().compareTo(0) > 0 && match.getBlueSide().getWins().compareTo(0) > 0)
                    updateDatabaseWinsLosses(match.getRedSideId(), match.getRedSide().getWins()-1, match.getBlueSideId(), match.getBlueSide().getLoses()-1);
                else if (match.getRedSide().getWins().compareTo(0) > 0 && match.getBlueSide().getWins().compareTo(0) == 0)
                    updateDatabaseWinsLosses(match.getRedSideId(), match.getRedSide().getWins()-1, match.getBlueSideId(), match.getBlueSide().getLoses());
                else if (match.getRedSide().getWins().compareTo(0) == 0 && match.getBlueSide().getWins().compareTo(0) == 0)
                    updateDatabaseWinsLosses(match.getRedSideId(), match.getRedSide().getWins(), match.getBlueSideId(), match.getBlueSide().getLoses()-1);

            }
            else {
                ProfileGetter.updateWinsLosses(match.getBlueSideId(), match.getRedSideId(), false);
                if(match.getRedSide().getWins().compareTo(0) > 0 && match.getBlueSide().getWins().compareTo(0) > 0)
                    updateDatabaseWinsLosses(match.getBlueSideId(), match.getBlueSide().getWins()-1,  match.getRedSideId(), match.getRedSide().getLoses()-1);
                else if(match.getRedSide().getWins().compareTo(0) > 0 && match.getBlueSide().getWins().compareTo(0) == 0)
                    updateDatabaseWinsLosses(match.getBlueSideId(), match.getBlueSide().getWins()-1,  match.getRedSideId(), match.getRedSide().getLoses());
                else if(match.getRedSide().getWins().compareTo(0) ==0 && match.getBlueSide().getWins().compareTo(0) > 0)
                    updateDatabaseWinsLosses(match.getBlueSideId(), match.getBlueSide().getWins(),  match.getRedSideId(), match.getRedSide().getLoses()-1);




            }


        }
        catch (SQLException e)
        {
            logger.debug("Error while deleting a match.", e);
            throw new DatabaseQueryException("Error while deleting a match");
        }
    }

    public static void editMatch(Match m, boolean winnerChanged, boolean loserChanged)
    {
        try(Connection connection = getConnection())
        {
            if(winnerChanged == false && loserChanged == false)
            {
                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE MATCHES SET RED_KILLS = ?, BLUE_KILLS = ?, RED_CHAMPION_ID = ?, BLUE_CHAMPION_ID = ?, MATCH_DATE = ? WHERE ID = ?");
                preparedStatement.setInt(1,m.getRedKills());
                preparedStatement.setInt(2,m.getBlueKills());
                preparedStatement.setLong(3, m.getRedChampion().getId());
                preparedStatement.setLong(4, m.getBlueChampion().getId());
                Date date = Date.valueOf(m.getMatchDate());
                preparedStatement.setDate(5, date);
                preparedStatement.setLong(6, m.getId());
                preparedStatement.executeUpdate();


            }
            else if(winnerChanged == false && loserChanged == true)
            {
                Long redId=null;
                Long blueId=null;
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT RED_ID, BLUE_ID FROM MATCHES WHERE ID=?");
                preparedStatement.setLong(1, m.getId());
                ResultSet rs = preparedStatement.executeQuery();
                while(rs.next())
                {
                    redId=rs.getLong("RED_ID");
                    blueId=rs.getLong("BLUE_ID");
                }
                rs.close();
                preparedStatement = connection.prepareStatement("UPDATE MATCHES SET RED_ID =?, BLUE_ID = ?, RED_KILLS = ?, BLUE_KILLS = ?, RED_CHAMPION_ID = ?, BLUE_CHAMPION_ID = ?, MATCH_DATE = ? WHERE ID = ?");
                preparedStatement.setLong(1, m.getRedSideId());
                preparedStatement.setLong(2, m.getBlueSideId());
                preparedStatement.setInt(3,m.getRedKills());
                preparedStatement.setInt(4,m.getBlueKills());
                preparedStatement.setLong(5, m.getRedChampion().getId());
                preparedStatement.setLong(6, m.getBlueChampion().getId());
                Date date = Date.valueOf(m.getMatchDate());
                preparedStatement.setDate(7, date);
                preparedStatement.setLong(8, m.getId());
                preparedStatement.executeUpdate();

                if(redId.equals(m.getWinnerId()))
                {
                    for(Profile p: MainApplication.profileList)
                    {
                        if(p.getId().equals(blueId))
                        {
                            Set<Match> tempSet = new HashSet<>(p.getMatchSet());

                            for(Match match:p.getMatchSet())
                            {
                                if(match.getBlueSide().equals(blueId) && match.getId().equals(m.getId()))
                                   tempSet.remove(match);
                            }
                            p.setMatchSet(tempSet);
                            p.setLoses(p.getLoses()-1);
                            updateDatabaseWinsLosses(null, 0, p.getId(), p.getLoses() );
                        }
                    }
               ProfileGetter.updateWinsLosses(null, m.getBlueSideId(), true);
                    updateDatabaseWinsLosses(null, 0, m.getBlueSideId(), m.getBlueSide().getLoses()+1 );
                }
                else{
                    for(Profile p: MainApplication.profileList)
                    {
                        if(p.getId().equals(redId))
                        {Set<Match> tempSet = new HashSet<>(p.getMatchSet());
                            for(Match match:p.getMatchSet())
                            {
                                if(match.getRedSide().equals(redId) && match.getId().equals(m.getId()))
                                    tempSet.remove(match);
                            }
                            p.setMatchSet(tempSet);
                            p.setLoses(p.getLoses()-1);
                            updateDatabaseWinsLosses(null, 0, p.getId(), p.getLoses());
                        }
                    }

                    ProfileGetter.updateWinsLosses(null, m.getRedSideId(), true);
                    updateDatabaseWinsLosses(null, 0, m.getRedSideId(), m.getRedSide().getLoses()+1);
                }



            }
            else if(winnerChanged && !loserChanged)
            {
                Long redId=null;
                Long blueId=null;
                Long winnerId = null;
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT RED_ID, BLUE_ID, WINNER_ID FROM MATCHES WHERE ID=?");
                preparedStatement.setLong(1, m.getId());
                ResultSet rs = preparedStatement.executeQuery();
                while(rs.next())
                {
                    redId=rs.getLong("RED_ID");
                    blueId=rs.getLong("BLUE_ID");
                    winnerId = rs.getLong("WINNER_ID");
                }
                rs.close();
                preparedStatement = connection.prepareStatement("UPDATE MATCHES SET RED_ID =?, BLUE_ID = ?, WINNER_ID=?, RED_KILLS = ?, BLUE_KILLS = ?, RED_CHAMPION_ID = ?, BLUE_CHAMPION_ID = ?, MATCH_DATE = ? WHERE ID = ?");
                preparedStatement.setLong(1, m.getRedSideId());
                preparedStatement.setLong(2, m.getBlueSideId());
                preparedStatement.setLong(3, m.getWinnerId());
                preparedStatement.setInt(4,m.getRedKills());
                preparedStatement.setInt(5,m.getBlueKills());
                preparedStatement.setLong(6, m.getRedChampion().getId());
                preparedStatement.setLong(7, m.getBlueChampion().getId());
                Date date = Date.valueOf(m.getMatchDate());
                preparedStatement.setDate(8, date);
                preparedStatement.setLong(9, m.getId());
                preparedStatement.executeUpdate();
                if(redId.equals(winnerId))
                {
                    for(Profile p: MainApplication.profileList)
                    {
                        if(p.getId().equals(redId))
                        {
                            Set<Match> tempSet = new HashSet<>(p.getMatchSet());

                            for(Match match:p.getMatchSet())
                            {
                                if(match.getRedSide().equals(redId) && match.getId().equals(m.getId()))
                                    tempSet.remove(match);
                            }
                            p.setMatchSet(tempSet);
                            p.setWins(p.getWins()-1);
                            updateDatabaseWinsLosses(p.getId(), p.getWins(), null, 0 );
                        }
                    }
                    ProfileGetter.updateWinsLosses(m.getRedSideId(), null, true);
                    updateDatabaseWinsLosses(m.getRedSideId(),m.getRedSide().getWins()+1 , null, 0 );

                }
                else{
                    for(Profile p: MainApplication.profileList)
                    {
                        if(p.getId().equals(blueId))
                        {
                            Set<Match> tempSet = new HashSet<>(p.getMatchSet());

                            for(Match match:p.getMatchSet())
                            {
                                if(match.getBlueSide().equals(blueId) && match.getId().equals(m.getId()))
                                    tempSet.remove(match);
                            }
                            p.setMatchSet(tempSet);
                            p.setWins(p.getWins()-1);
                            updateDatabaseWinsLosses(p.getId(), p.getWins(), null, 0);
                        }
                    }

                    ProfileGetter.updateWinsLosses(m.getBlueSideId(), null, true);
                    updateDatabaseWinsLosses(m.getBlueSideId(), m.getBlueSide().getWins()+1, null, 0);



                }
            }

            else if(winnerChanged && loserChanged)
            {

                Long redId=null;
                Long blueId=null;
                Long winnerId=null;
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT RED_ID, BLUE_ID , WINNER_ID FROM MATCHES WHERE ID=?");
                preparedStatement.setLong(1, m.getId());
                ResultSet rs = preparedStatement.executeQuery();
                while(rs.next())
                {
                    redId=rs.getLong("RED_ID");
                    blueId=rs.getLong("BLUE_ID");
                    winnerId=rs.getLong("WINNER_ID");
                }
                rs.close();
                preparedStatement = connection.prepareStatement("UPDATE MATCHES SET RED_ID =?, BLUE_ID = ?, WINNER_ID=?, RED_KILLS = ?, BLUE_KILLS = ?, RED_CHAMPION_ID = ?, BLUE_CHAMPION_ID = ?, MATCH_DATE = ? WHERE ID = ?");
                preparedStatement.setLong(1, m.getRedSideId());
                preparedStatement.setLong(2, m.getBlueSideId());
                preparedStatement.setLong(3, m.getWinnerId());
                preparedStatement.setInt(4,m.getRedKills());
                preparedStatement.setInt(5,m.getBlueKills());
                preparedStatement.setLong(6, m.getRedChampion().getId());
                preparedStatement.setLong(7, m.getBlueChampion().getId());
                Date date = Date.valueOf(m.getMatchDate());
                preparedStatement.setDate(8, date);
                preparedStatement.setLong(9, m.getId());
                preparedStatement.executeUpdate();
                preparedStatement.close();

                if(redId.equals(winnerId))
                {
                    for(Profile p:MainApplication.profileList)
                    {
                        if(p.getId().equals(redId))
                        {
                            Set<Match> tempSet = new HashSet<>(p.getMatchSet());

                            for(Match match : p.getMatchSet())
                            {
                                if(match.getRedSideId().equals(p.getId()) && match.getId().equals(m.getId()))
                                {
                                    tempSet.remove(match);
                                }
                            }
                            p.setMatchSet(tempSet);
                           p.setWins(p.getWins()-1);
                            updateDatabaseWinsLosses(p.getId(), p.getWins(), null, 0 );
                        }
                        if(p.getId().equals(blueId))
                        {
                            Set<Match> tempSet = new HashSet<>(p.getMatchSet());

                            for(Match match:p.getMatchSet())
                            {
                                if(match.getBlueSideId().equals(p.getId()) && match.getId().equals(m.getId()))
                                {
                                    tempSet.remove(match);
                                }
                            }
                            p.setLoses(p.getLoses()-1);
                            p.setMatchSet(tempSet);
                            updateDatabaseWinsLosses(null, 0, p.getId(), p.getLoses() );
                        }

                    }
                }
                else{

                    for(Profile p:MainApplication.profileList)
                    {
                        if(p.getId().equals(blueId)) //winner
                        {
                            Set<Match> tempSet = new HashSet<>(p.getMatchSet());

                            for( Match match : p.getMatchSet())
                            {
                                if(match.getBlueSideId().equals(p.getId()) && match.getId().equals(m.getId()))
                                {
                                    tempSet.remove(match);

                                }

                            }
                            p.setMatchSet(tempSet);
                            p.setWins(p.getWins()-1);
                            updateDatabaseWinsLosses(p.getId(), p.getWins(), null, 0 );
                        }
                        if(p.getId().equals(redId)) //loser
                        {
                            Set<Match> tempSet = new HashSet<>(p.getMatchSet());


                            for(Match match:p.getMatchSet())
                            {
                                if(match.getRedSideId().equals(p.getId()) && match.getId().equals(m.getId()))
                                {
                                   tempSet.remove(match);
                                }
                            }
                            p.setLoses(p.getLoses()-1);
                            p.setMatchSet(tempSet);
                            updateDatabaseWinsLosses(null, 0, p.getId(), p.getLoses() );
                        }

                    }
                }
                if(m.getWinnerId().equals(m.getRedSideId()))
                {
                    ProfileGetter.updateWinsLosses(m.getWinnerId(), m.getBlueSideId(), true);
                    updateDatabaseWinsLosses(m.getWinnerId(), m.getRedSide().getWins()+1, m.getBlueSideId(), m.getBlueSide().getLoses()+1);
                }
                else{
                    ProfileGetter.updateWinsLosses(m.getWinnerId(), m.getRedSideId(), true);
                    updateDatabaseWinsLosses(m.getWinnerId(), m.getBlueSide().getWins()+1, m.getRedSideId(), m.getBlueSide().getLoses()+1);
                }

            }

            MainApplication.matchList=DatabaseQueries.getAllMatches();
            MainApplication.profileList=DatabaseQueries.getProfiles();

        }

        catch (SQLException e)
        {


            logger.debug("Error while editing a match.", e);
            throw new DatabaseQueryException("Error while editing a match");
        }
    }


}
