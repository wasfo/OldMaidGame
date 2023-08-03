package org.example.serial_solution;

import java.util.Objects;

public class Player {

    private final int id;

    public Player(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id == player.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
