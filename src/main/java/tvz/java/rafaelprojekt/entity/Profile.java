package tvz.java.rafaelprojekt.entity;

import javafx.scene.image.Image;
import tvz.java.rafaelprojekt.main.MainApplication;

import java.io.Serializable;
import java.util.Set;

import static eu.hansolo.tilesfx.tools.Helper.round;


public class Profile extends Entity implements Comparable<Profile>, Serializable {
    private String username;
    private Image profilePicture;
    private Integer level;
    private Rank rank;
    private Server server;
    private Integer wins;
    private Integer loses;
    private Set<Match> matchSet;

    public Profile(Long id, String username, Image profilePicture, Integer level, Rank rank, Server server, Integer wins, Integer loses, Set<Match> matchSet) {
        super(id);
        this.username = username;
        this.profilePicture = profilePicture;
        this.level = level;
        this.rank = rank;
        this.server = server;
        this.wins = wins;
        this.loses = loses;
        this.matchSet=matchSet;
    }

    public Profile(Profile profile)
    {

        super(profile.getId());
        this.username= profile.getUsername();
        this.profilePicture=profile.getProfilePicture();
        this.level= profile.getLevel();
        this.rank=profile.getRank();
        this.server=profile.getServer();
        this.wins=profile.getWins();
        this.loses=profile.getLoses();
        this.matchSet=profile.getMatchSet();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Image getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(Image profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public Integer getWins() {
        return wins;
    }

    public void setWins(Integer wins) {
        this.wins = wins;
    }

    public Integer getLoses() {
        return loses;
    }

    public void setLoses(Integer loses) {
        this.loses = loses;
    }

    public Set<Match> getMatchSet() {
        return matchSet;
    }

    public void setMatchSet(Set<Match> matchSet) {
        this.matchSet=matchSet;
    }

   public int getWinRate()
   {

       return  (int) (round(((double)wins / (loses+wins)), 2) * 100);
   }

   public String toString()
   {
       return username;
   }

   public int compareTo(Profile p2)
   {
       if(rank.value() > p2.getRank().value())
           return -1;
       else if(rank.value() == p2.getRank().value())
       {
           if(getWinRate() > p2.getWinRate())
               return -1;
           else if(getWinRate() == p2.getWinRate())
           {
               if(wins > p2.getWins())
                   return -1;
               else if(p2.getWins() > wins)
                   return 1;
               else return 0;
           }
           else return 1;
       }
       else return 1;
   }
   public static void removeById(Long id)
   {
       for(int i=0;i< MainApplication.profileList.size();i++)
       {
           if(MainApplication.profileList.get(i).getId().equals(id)) {
               MainApplication.profileList.remove(i);
           }
       }
   }

   @Override
   public boolean equals(Object o)
   {
       int result =0;
       if(o instanceof Profile)
       {
           if(!username.equals(((Profile) o).getUsername()))
               return false;
          else if(!wins.equals(((Profile) o).getWins()))
               return false;
           else if(!loses.equals(((Profile) o).getLoses()))
               return false;
          else if(!profilePicture.equals(((Profile) o).getProfilePicture()))
               return false;
           else if(!server.equals(((Profile) o).server))
               return false;
           else if(!level.equals(((Profile) o).getLevel()))
               return false;
           else return rank.equals(((Profile) o).getRank());

       }
       else
           return false;
   }







}
