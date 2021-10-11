package com.chalova.irina.myfinances.users_service.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    private Long id;

    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return getName().equals(role.getName());
    }

    public Role (String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}