package account.services;

import account.dto.UserDTO;
import account.entity.EventLog;
import account.entity.Role;
import account.entity.User;
import account.errors.*;
import account.repo.EventLogRepository;
import account.repo.RoleRepository;
import account.repo.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    List<String> breachedPasswords = List.of("PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
            "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
            "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember");

    public static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_TIME_DURATION = 24 * 60 * 60 * 1000; // 24 hours
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final EventLogRepository eventLogRepo;
    private final EventLogService eventLogService;

    public UserDetailsServiceImpl(UserRepository userRepo, RoleRepository roleRepo, EventLogRepository eventLogRepo, EventLogService eventLogService) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.eventLogRepo = eventLogRepo;
        this.eventLogService = eventLogService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepo.findByEmail(username);
        System.out.println(user);
        return user.orElse(null);
    }

    //CODE FOR USER STUFFS

    public User getByEmail(String email) {
        return userRepo.findByEmail(email).orElseThrow(() -> new GenericException("No such user exists"));
    }

    public static User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }

    public void passwordBreached(String hashedPassword) {
        breachedPasswords.forEach(password -> {
            if (getEncoder().matches(password, hashedPassword)) {
                throw new PasswordBreachedException();
            }
        });
    }

    public Role defineRole(String role) {
        return roleRepo.findByRole(role).orElseThrow(() -> new GenericException("Role not found"));
    }

    public void saveUser(User user) {
        if (userRepo.findByEmail(user.getEmail()).isPresent()) { //checking if user with this email already exists
            throw new UserAlreadyExistsException();
        } else {
            if (userRepo.count() == 0) {
                user.setRoles(Set.of(defineRole("ROLE_ADMINISTRATOR")));
            } else {
                user.setRoles(Set.of(defineRole("ROLE_USER")));
            }
            String hashedPassword = getEncoder().encode(user.getPassword());
            passwordBreached(hashedPassword); //checking if password is breached
            user.setPassword(hashedPassword); //setting password to encoded password
            user.setEmail(user.getEmail().toLowerCase());
            userRepo.save(user);
            eventLogService.saveEvent(new EventLog (LocalDate.now(), "CREATE_USER", "Anonymous", user.getEmail(), "/api/auth/signup"));
            System.out.println("User registered");
        }
    }

    public void changepass(String newPassword) {
        User user = getCurrentUser();
        if (newPassword.length() < 12) {
            throw new PasswordValidationException();
        }
        if (getEncoder().matches(newPassword, user.getPassword())) {
            throw new SamePasswordException();
        }
        String hashedPassword = getEncoder().encode(newPassword);
        passwordBreached(hashedPassword); //checking if password is breached
        user.setPassword(hashedPassword); //setting password to encoded password
        userRepo.save(user);
        eventLogService.saveEvent(new EventLog(LocalDate.now(), "CHANGE_PASSWORD", user.getEmail(), user.getEmail(), "/api/auth/changepass"));

    }

    //ADMIN METHODS

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepo.findAll();
        List<UserDTO> userDTOs = new ArrayList<>();
        users.forEach(user -> userDTOs.add(UserDTO.mapToDTO(user)));
        return userDTOs;
    }

    public void deleteUser(String email) {
        userRepo.findByEmail(email.toLowerCase()).ifPresentOrElse(user -> {
            if (user.getRoles().contains(defineRole("ROLE_ADMINISTRATOR"))) {
                throw new GenericException("Can't remove ADMINISTRATOR role!");
            }
            userRepo.delete(user);


            eventLogService.saveEvent(new EventLog (LocalDate.now(), "DELETE_USER",
                    UserDetailsServiceImpl.getCurrentUser().getEmail(), email, "/api/admin/user"));

        }, () -> {
            throw new NotFoundException("User not found!");
        });
    }

    public UserDTO changeRole(Map<String, String> roleChange) {
        User user = userRepo.findByEmail(roleChange.get("user").toLowerCase()).orElseThrow(() -> new NotFoundException("User not found!"));
        Role role = roleRepo.findByRole("ROLE_"+roleChange.get("role").toUpperCase()).orElseThrow(() -> new NotFoundException("Role not found!"));
        if (roleChange.get("operation").equalsIgnoreCase("grant")) {

            if (user.getRoles().stream().anyMatch(r -> Objects.equals(r.getRole(), "ROLE_ADMINISTRATOR"))) {
                throw new GenericException("The user cannot combine administrative and business roles!");
            }

            if (user.getRoles().stream().anyMatch(r -> Objects.equals(r.getRole(), "ROLE_USER") || Objects.equals(r.getRole(), "ROLE_ACCOUNTANT") || Objects.equals(r.getRole(), "ROLE_AUDITOR"))) {
                if (role.getRole().equals("ROLE_ADMINISTRATOR")) {
                    throw new GenericException("The user cannot combine administrative and business roles!");
                }
            }

            user.getRoles().add(role);

            eventLogService.saveEvent(new EventLog (LocalDate.now(), "GRANT_ROLE",
                    UserDetailsServiceImpl.getCurrentUser().getEmail(),
                    "Grant role "+role.getRole().split("_")[1]+" to "+roleChange.get("user").toLowerCase(),
                    "/api/admin/user/role"));

        } else if (roleChange.get("operation").equalsIgnoreCase("remove")) {

            if (user.getRoles().stream().anyMatch(r -> Objects.equals(r.getRole(), role.getRole()))) {

                if (role.getRole().equals("ROLE_ADMINISTRATOR")) {
                    throw new GenericException("Can't remove ADMINISTRATOR role!");
                }

                if (user.getRoles().size() == 1) {
                    throw new GenericException("The user must have at least one role!");
                }

                user.getRoles().remove(role);

                eventLogService.saveEvent(new EventLog (LocalDate.now(), "REMOVE_ROLE",
                        UserDetailsServiceImpl.getCurrentUser().getEmail(),
                        "Remove role "+role.getRole().split("_")[1]+" from "+roleChange.get("user").toLowerCase(),
                        "/api/admin/user/role"));

            } else {
                throw new GenericException("The user does not have a role!");
            }
        } else {
            throw new NotFoundException("Role not found!");
        }
        userRepo.save(user);
        return UserDTO.mapToDTO(user);
    }

    public void changeUserAccess(Map<String, String> accessChange) {
        User user = userRepo.findByEmail(accessChange.get("user").toLowerCase()).orElseThrow(() -> new NotFoundException("User not found!"));
        if (accessChange.get("operation").equalsIgnoreCase("lock")) {
            lock(user, accessChange.get("user"));

        } else if (accessChange.get("operation").equalsIgnoreCase("unlock")) {
            unlock(user, accessChange);

        } else {
            throw new NotFoundException("Operation not found!");
        }

    }

    //CODE FOR AUDITOR
    public List<EventLog> getEventLogs() {
        return eventLogService.accessLogs();
    }

    //CODE FOR PAYROLLS
    public List<Map<String, String>> getUserPayrolls(User user) {
        System.out.println(user.getAuthorities());
        List<Map<String, String>> salaries = new ArrayList<>();
        user.getSalaries().forEach(salary -> {
            Map<String, String> salaryMap = new LinkedHashMap<>();
            String salaryString = (salary.getSalary()/100) + " dollar(s) " + (salary.getSalary()%100 + " cent(s)");
            salaryMap.put("name", user.getName());
            salaryMap.put("lastname", user.getLastname());
            salaryMap.put("period", salary.getPeriod());
            salaryMap.put("salary", salaryString);
            salaries.add(salaryMap);
        });
        salaries.sort((o1, o2) -> o2.get("period").compareTo(o1.get("period")));
        for (Map<String, String> salary : salaries) {  //switching month to name. couldn't find a way to sort by month name
            salary.put("period", switchMonth(salary.get("period")));
        }
        System.out.println(salaries);
        return salaries;
    }

    public Map<String, String> getSpecificUserPayroll(User user, String period) {
        Map<String, String> salaries = new LinkedHashMap<>();
        user.getSalaries().forEach(salary -> {
            System.out.println(Objects.equals(salary.getPeriod(), period));
            if (Objects.equals(salary.getPeriod(), period)) {
                String salaryString = (salary.getSalary()/100) + " dollar(s) " + (salary.getSalary()%100 + " cent(s)");
                salaries.put("name", user.getName());
                salaries.put("lastname", user.getLastname());
                salaries.put("period", switchMonth(period));
                salaries.put("salary", salaryString);
            }
        });
        user.getSalaries().stream().
                filter(salary -> Objects.equals(salary.getPeriod(), period))
                .findFirst().orElseThrow(() -> new GenericException("No such salary for this period exists"));
        return salaries;
    }

    public String switchMonth(String period) {
        String monthInPeriod = period.substring(0, 2);
        String yearInPeriod = period.substring(2);
        String month = null;
        switch (monthInPeriod) {
            case "01" -> month = "January"+yearInPeriod;
            case "02" -> month = "February"+yearInPeriod;
            case "03" -> month = "March"+yearInPeriod;
            case "04" -> month = "April"+yearInPeriod;
            case "05" -> month = "May"+yearInPeriod;
            case "06" -> month = "June"+yearInPeriod;
            case "07" -> month = "July"+yearInPeriod;
            case "08" -> month = "August"+yearInPeriod;
            case "09" -> month = "September"+yearInPeriod;
            case "10" -> month = "October"+yearInPeriod;
            case "11" -> month = "November"+yearInPeriod;
            case "12" -> month = "December"+yearInPeriod;
        }
        return month;
    }

    //CODE FOR SECURITY STUFFS
    public void increaseFailedAttempts(User user) {
        int newFailAttempts = user.getFailedAttempt() + 1;
        userRepo.updateFailedAttempts(newFailAttempts, user.getEmail());
    }

    public void resetFailedAttempts(String email) {
        userRepo.updateFailedAttempts(0, email);
    }

    public void lock(User user, String email) {
        if (user.getRoles().stream().anyMatch(r -> Objects.equals(r.getRole(), "ROLE_ADMINISTRATOR"))) {
            throw new GenericException("Can't lock the ADMINISTRATOR!");
        }
        eventLogService.saveEvent(new EventLog (LocalDate.now(), "LOCK_USER",
                UserDetailsServiceImpl.getCurrentUser().getEmail(),
                "Lock user "+email.toLowerCase(),
                "/api/admin/user/access"));

        user.setAccountNonLocked(false);
        user.setLockTime(new Date());

        userRepo.save(user);
    }

    public void automaticLock(User user, String email) {
        eventLogRepo.save(new EventLog (LocalDate.now(), "LOCK_USER",
                user.getEmail(),
                "Lock user "+email.toLowerCase(),
                "/api/admin/user/access"));


        user.setAccountNonLocked(false);
        user.setLockTime(new Date());
        userRepo.save(user);
    }
//TODO: figure out the difference between automatic lock and lock and also what to do with automatic lock
// when the admin is the one being automatically locked
    public void unlock(User user, Map<String, String> accessChange) {
        user.setAccountNonLocked(true);

        eventLogService.saveEvent(new EventLog (LocalDate.now(), "UNLOCK_USER",
                UserDetailsServiceImpl.getCurrentUser().getEmail(),
                "Unlock user "+accessChange.get("user").toLowerCase(),
                "/api/admin/user/access"));

        userRepo.save(user);
        resetFailedAttempts(accessChange.get("user"));
    }

    public void unlockWhenTimeExpired(User user) {
        long lockTimeInMilliSecs = user.getLockTime().getTime();
        long currentTimeInMilliSecs = System.currentTimeMillis();

        if (lockTimeInMilliSecs + LOCK_TIME_DURATION < currentTimeInMilliSecs) {
            user.setAccountNonLocked(true);
            user.setLockTime(null);
            user.setFailedAttempt(0);

            userRepo.save(user);
        }
    }

    //PASSWORD ENCODER
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }

}
