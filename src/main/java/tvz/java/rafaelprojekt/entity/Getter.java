package tvz.java.rafaelprojekt.entity;

import java.util.List;
import java.util.Optional;

public interface Getter <T>{
    Optional<T> getById(Long id);



}
