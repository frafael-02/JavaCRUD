package tvz.java.rafaelprojekt.entity;

import java.io.Serializable;

public record Rank(Long id, String name, Integer value) implements Serializable {
    @Override
    public String toString()
    {
        return name;
    }
}
