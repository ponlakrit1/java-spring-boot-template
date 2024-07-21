package com.nctine.template.template.config;

import com.nctine.template.template.component.UserComponent;
import com.nctine.template.template.repository.UsersRepository;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TokenProvider {

    private static final String SECRET = "secret-key";

    private static final String REFRESH_SECRET = "refresh-secret-key";

    private static final long EXPIRATION_TIME = 1000 * 60 * 30; // 30 minute

    private static final long REFRESH_EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 1 day

    private static final String AUTH_HEADER = "Authorization";

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private UserComponent userComponent;

    public String generateAccessToken(Authentication authentication) {
        return this.generateToken(authentication, EXPIRATION_TIME, SECRET);
    }

    public String generateRefreshToken(Authentication authentication) {
        return this.generateToken(authentication, REFRESH_EXPIRATION_TIME, REFRESH_SECRET);
    }

    private String generateToken(Authentication authentication, long expire, String secret) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("userid", 1)
                .claim("firstname", "nctine")
                .claim("lastname", "nctine")
                .claim("email", "nctine.tech@gmail.com")
                .setExpiration(this.getExpireDate(expire))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    private Date getExpireDate(long expire) {
        return new Date(System.currentTimeMillis() + expire);
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public UsernamePasswordAuthenticationToken getAuthenticationToken(final String token, final UserDetails userDetails) {
        final JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(getSigningKey()).build();

        final Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);

        final Claims claims = claimsJws.getBody();

        final Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTH_HEADER).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        userComponent.setUserId((Integer) claims.get("userId"));
        userComponent.setUsername(userDetails.getUsername());
        userComponent.setEmail((String) claims.get("email"));
        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(TokenProvider.SECRET.getBytes());
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
}
