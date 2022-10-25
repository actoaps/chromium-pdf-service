package dk.acto.web.pdf.security;

import dk.acto.web.pdf.dto.ActoConf;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
@AllArgsConstructor
public class SecretValidator extends GenericFilterBean {
    private static final Pattern auth = Pattern.compile("^([Bb]earer\\s+)?(.+)$");
    private final ActoConf actoConf;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        var cast = (HttpServletRequest) request;

        Optional.ofNullable(cast.getHeader("Authorization"))
                .map(this::validateAuth)
                .ifPresent(x -> SecurityContextHolder.getContext().
                        setAuthentication(new AnonymousAuth()));

        chain.doFilter(request, response);
    }

    private boolean validateAuth(String authString) {
        return Try.of(() -> auth.matcher(authString))
                .map(x -> x.group(2))
                .filter(x -> x.equals(actoConf.getSecret()))
                .isSuccess();
    }
}
