package iuh.fit.se.tramcamxuc.configuration;

import iuh.fit.se.tramcamxuc.modules.user.entity.User;
import iuh.fit.se.tramcamxuc.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class ApplicationAuditAware implements AuditorAware<UUID> {

    private final UserRepository userRepository;

    @Override
    public Optional<UUID> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }

        String email = authentication.getName();

        //muốn tối ưu thì lưu ID vào trong Token hoặc CustomUserDetails ngay từ đầu
        return userRepository.findByEmail(email).map(User::getId);
    }
}