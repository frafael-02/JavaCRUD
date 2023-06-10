package tvz.java.rafaelprojekt.entity;

import tvz.java.rafaelprojekt.main.MainApplication;
import tvz.java.rafaelprojekt.exceptions.NoActivityException;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

public class ProfileGetter implements Getter<Profile>, Serializable {

    @Override
    public Optional<Profile> getById(Long id)
    {

        return MainApplication.profileList.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    public static List<Profile> getByServer(String code)
    {

        List<Profile> profileList = new ArrayList<>();
        for(Profile p : MainApplication.profileList)
            if(p.getServer().getCode().equals(code))
                profileList.add(p);
    return profileList;
    }

    public static LocalDate lastActivity(Set<Match> matchSet) throws NoActivityException
    {
        if(matchSet.isEmpty())
            throw new NoActivityException();
        return matchSet.stream().map(Match::getMatchDate).max(LocalDate::compareTo).get();
    }

    public static List<Profile> sortPlayers(List<Profile> profileList)
    {
        return profileList.stream().sorted().toList();
    }

    public static String mostPlayedChampions(Profile profile) {
        StringBuilder stringBuilder = new StringBuilder();
        Map<Champion, Integer> map = new HashMap<>();
        for (Match m : profile.getMatchSet()) {
            if(m.getBlueSideId().equals(profile.getId()))
            {
                if(map.containsKey(m.getBlueChampion()))
                {
                    map.put(m.getBlueChampion(), map.get(m.getBlueChampion())+1);

                }
                else{
                    map.put(m.getBlueChampion(), 1);
                }
            }
            else {
                if(map.containsKey(m.getRedChampion()))
                {
                    map.put(m.getRedChampion(), map.get(m.getRedChampion())+1);
                }
                else{
                    map.put(m.getRedChampion(),1);
                }
            }

        }
        int br=0;
        while(map.size() > 0)
        {

                Integer max=0;
                Champion maxChampion = null;
                maxChampion = getChampion(map, max, maxChampion);
                map.remove(maxChampion);
                stringBuilder.append(maxChampion.getName() + "\n");
                br++;
                if(br == 3)
                    break;
        }


        if(stringBuilder.length()>0)
            return stringBuilder.toString();
        else
            return "No champions played";


    }

    private static Champion getChampion(Map<Champion, Integer> map, Integer max, Champion maxChampion) {
        for(Map.Entry<Champion, Integer> s : map.entrySet())
        {
            if(s.getValue()> max) {
                max = s.getValue();
                maxChampion =s.getKey();

            }
        }


        return maxChampion;
    }

    public static void updateWinsLosses(Long winner, Long loser, boolean add)
    {
        for(Profile p : MainApplication.profileList)
        {
            if(add)
            {
                if(winner != null){if(p.getId().equals(winner))
                    p.setWins(p.getWins()+1);}

                if(loser !=null){if(p.getId().equals(loser))
                    p.setLoses(p.getLoses()+1);}

            }
            else{
                if(winner != null)
                {if(p.getId().equals(winner))
                {   if(p.getWins().compareTo(0) > 0)
                    p.setWins(p.getWins()-1);
                else p.setWins(p.getWins());
                }}


                if(loser!=null)
                {if(p.getId().equals(loser))
                {
                    if(p.getLoses().compareTo(0) > 0)
                        p.setLoses(p.getLoses()-1);
                    else p.setLoses(p.getLoses());
                }}


            }

        }
    }

    public static boolean usernameUsed(String username)
    {
        return MainApplication.profileList.stream().anyMatch(profile -> profile.getUsername().equals(username));
    }



    }

