package account.services;

import account.repo.RoleRepository;
import account.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataLoader {

    private final RoleRepository roleRepository;

    @Autowired
    public DataLoader(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
        createRoles();
    }

    private void createRoles() {
        if (roleRepository.count() == 0) {
            try {
                roleRepository.save(new Role("ROLE_ADMINISTRATOR"));
                roleRepository.save(new Role("ROLE_USER"));
                roleRepository.save(new Role("ROLE_ACCOUNTANT"));
                roleRepository.save(new Role("ROLE_AUDITOR"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Roles already exist");
        }

    }
}
