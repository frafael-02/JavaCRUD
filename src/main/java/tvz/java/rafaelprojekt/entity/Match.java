package tvz.java.rafaelprojekt.entity;

import tvz.java.rafaelprojekt.main.MainApplication;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Optional;

public class Match extends Entity implements Serializable, Getter<Match> {
private Long redSideId;
private Long blueSideId;
private Long winnerId;
private int redKills;
private int blueKills;
private Champion redChampion;
private Champion blueChampion;
private LocalDate matchDate;

    public Match(Long id, Long redSideId, Long blueSideId, Long winnerId, int redKills, int blueKills, Champion redChampion, Champion blueChampion, LocalDate matchDate) {
        super(id);
        this.redSideId = redSideId;
        this.blueSideId = blueSideId;
        this.winnerId = winnerId;
        this.redKills = redKills;
        this.blueKills = blueKills;
        this.redChampion = redChampion;
        this.blueChampion = blueChampion;
        this.matchDate = matchDate;



    }

    public Long getRedSideId() {
        return redSideId;
    }

    public void setRedSideId(Long redSideId) {
        this.redSideId = redSideId;
    }

    public Long getBlueSideId() {
        return blueSideId;
    }

    public void setBlueSideId(Long blueSideId) {
        this.blueSideId = blueSideId;
    }

    public Long getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(Long winnerId) {
        this.winnerId = winnerId;
    }

    public int getRedKills() {
        return redKills;
    }

    public void setRedKills(int redKills) {
        this.redKills = redKills;
    }

    public int getBlueKills() {
        return blueKills;
    }

    public void setBlueKills(int blueKills) {
        this.blueKills = blueKills;
    }

    public Champion getRedChampion() {
        return redChampion;
    }

    public void setRedChampion(Champion redChampion) {
        this.redChampion = redChampion;
    }

    public Champion getBlueChampion() {
        return blueChampion;
    }

    public void setBlueChampion(Champion blueChampion) {
        this.blueChampion = blueChampion;
    }

    public LocalDate getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(LocalDate matchDate) {
        this.matchDate = matchDate;
    }

    public Profile getRedSide()
    {
        ProfileGetter profileGetter = new ProfileGetter();
        return profileGetter.getById(redSideId).get();
    }

    public Profile getBlueSide()
    {
        ProfileGetter profileGetter = new ProfileGetter();

        return profileGetter.getById(blueSideId).get();
    }

    public Profile getWinner()
    {
        ProfileGetter profileGetter = new ProfileGetter();

        return profileGetter.getById(winnerId).get();
    }


    public static void removeById(Long id)
    {
        for(int i=0;i< MainApplication.matchList.size();i++)
        {
            if(MainApplication.matchList.get(i).getId().equals(id))
                MainApplication.matchList.remove(i);
        }
    }

    @Override
    public boolean equals(Object m)
    {
        if(m instanceof Match)
        {
            int result =0;
            if(!winnerId.equals(((Match) m).getWinnerId()))
                result++;
            else if(!redSideId.equals(((Match) m).getRedSideId()))
                result++;
            else if(!blueSideId.equals(((Match) m).getBlueSideId()))
                result++;
            else if(!matchDate.equals(((Match) m).getMatchDate()))
                result++;
            else if(redKills != ((Match) m).getRedKills())
                result++;
            else if(blueKills != ((Match) m).getBlueKills())
                result++;
            else if (!blueChampion.getName().equals(((Match) m).getBlueChampion().getName()))
                result++;
            else if(!redChampion.getName().equals(((Match) m).getRedChampion().getName()))
                result++;
            return result == 0;
        }
        else return false;

    }


    public Optional<Match> getById(Long id)
    {

        return MainApplication.matchList.stream().filter(p -> p.getId().equals(id)).findFirst();
    }




}
