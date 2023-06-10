package tvz.java.rafaelprojekt.entity;

import tvz.java.rafaelprojekt.main.MainApplication;

import java.io.Serializable;

import static eu.hansolo.tilesfx.tools.Helper.round;

public class Champion extends Entity implements Serializable {
    String name;

    public Champion(Long id, String name) {
        super(id);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString()
    {
        return name;
    }

    public int getWins()
    {
        int wins =0;
        for(Match m : MainApplication.matchList)
        {
            if(m.getBlueChampion().getId().equals(getId()))
            {
                if(m.getWinnerId().equals(m.getBlueSideId()))
                    wins++;
            }
            if(m.getRedChampion().getId().equals(getId()))
            {
                if(m.getWinnerId().equals(m.getRedSideId()))
                    wins++;
            }
        }

        return wins;

    }

    public int getLoses()
    {
        int loses=0;
        for(Match m : MainApplication.matchList)
        {
            if(m.getBlueChampion().getId().equals(getId()))
            {
                if(m.getWinnerId().equals(m.getRedSideId()))
                    loses++;
            }
            else if(m.getRedChampion().getId().equals(getId()))
            {
                if(m.getWinnerId().equals(m.getBlueSideId()))
                    loses++;
            }
        }


        return loses;
    }

    public int getWinRate()
    {
        int wins = getWins();
        int loses=getLoses();
        return  (int) (round(((double)getWins() / (loses+wins)), 2) * 100);
    }

    public int getPlayedTimes()
    {
        int played = 0;
        for(Match m : MainApplication.matchList)
        {
            if(m.getRedChampion().getId().equals(getId()) || m.getBlueChampion().getId().equals(getId()))
                played++;
        }

        return played;
    }

    public boolean wasPlayedBy(String username)
    {
        for(Match m : MainApplication.matchList)
        {
            if(m.getBlueChampion().getId().equals(getId()))
            {
                if(m.getBlueSide().getUsername().equals(username))
                    return true;
            }
            else if(m.getRedChampion().getId().equals(getId()))
            {
                if(m.getRedSide().getUsername().equals(username))
                    return true;
            }
        }

        return false;
    }

    public static boolean isUsed(Long id)
    {
        for(Match m : MainApplication.matchList)
        {
            if(m.getRedChampion().getId().equals(id) || m.getBlueChampion().getId().equals(id))
                return true;
        }
        return false;
    }

    public static Champion getById(Long id)
    {
        return MainApplication.championList.stream().filter(champ -> champ.getId().equals(id)).findFirst().get();
    }

    public static boolean nameUsed(String name)
    {
        return MainApplication.championList.stream().anyMatch(champion -> champion.getName().equals(name));

    }


}
