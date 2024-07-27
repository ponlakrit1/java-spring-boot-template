package com.nctine.template.template.config;

import com.nctine.template.template.component.UserComponent;
import com.nctine.template.template.entity.UsersEntity;
import com.nctine.template.template.repository.UsersRepository;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${jwt.signing.key}")
    public String SIGNING_KEY;

    @Value("${jwt.refresh.signing.key}")
    public String REFRESH_SIGNING_KEY;

    @Value("${jwt.token.validity}")
    public long TOKEN_VALIDITY;

    @Value("${jwt.refresh.token.validity}")
    public long REFRESH_TOKEN_VALIDITY;

    @Value("${jwt.authorities.key}")
    public String AUTHORITIES_KEY;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private UserComponent userComponent;

    public String generateAccessToken(Authentication authentication) {
        return this.generateToken(authentication, TOKEN_VALIDITY, SIGNING_KEY);
    }

    public String generateRefreshToken(Authentication authentication) {
        return this.generateToken(authentication, REFRESH_TOKEN_VALIDITY, REFRESH_SIGNING_KEY);
    }

    private String generateToken(Authentication authentication, long expire, String secret) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        UsersEntity users = usersRepository.findByUsernameAndActiveIsTrue(authentication.getName());

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .claim("userid", users.getUserId())
                .claim("firstname", users.getFirstname())
                .claim("lastname", users.getLastname())
                .claim("email", users.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(this.getExpireDate(expire))
                .signWith(this.getSigningKey(), SignatureAlgorithm.HS256)
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
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        userComponent.setUserId((Integer) claims.get("userId"));
        userComponent.setUsername(userDetails.getUsername());
        userComponent.setEmail((String) claims.get("email"));
        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SIGNING_KEY.getBytes());
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
