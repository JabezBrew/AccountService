package account.dto;

import account.entity.User;

import java.util.*;


public record UserDTO(
        Long id,
        String name,
        String lastname,
        String email,
        List<String> roles
) {
    public static UserDTO mapToDTO(User user) {
        List<String> roles = new ArrayList<>();
        user.getRoles().forEach(role -> roles.add(role.getRole()));
        roles.sort(Comparator.naturalOrder());
        return new UserDTO(user.getId(), user.getName(), user.getLastname(), user.getEmail(), roles);
    }
}
