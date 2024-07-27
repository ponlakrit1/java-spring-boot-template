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
        return this.generateToken(authentication, SIGNING_KEY, new Date(System.currentTimeMillis() + TOKEN_VALIDITY * 1000));
    }

    public String generateRefreshToken(Authentication authentication) {
        return this.generateToken(authentication, REFRESH_SIGNING_KEY, new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY * 1000));
    }

    private String generateToken(Authentication authentication, String signingKey, Date expirationDate) {
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
                .setExpiration(expirationDate)
                .signWith(this.getSigningKey(signingKey), SignatureAlgorithm.HS256)
                .compact();
    }

    private Date getExpireDate(long expire) {
        return new Date(System.currentTimeMillis() + expire);
    }

    public Boolean validateToken(String token, UserDetails userDetails, boolean isRenew) {
        final String username = getUsernameFromToken(token, isRenew);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token, isRenew));
    }

    public UsernamePasswordAuthenticationToken getAuthenticationToken(final String token, final UserDetails userDetails, boolean isRenew) {
        String signingKey = isRenew ? REFRESH_SIGNING_KEY : SIGNING_KEY;
        final JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(getSigningKey(signingKey)).build();

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

    private Key getSigningKey(String signingKey) {
        return Keys.hmacShaKeyFor(signingKey.getBytes());
    }

    public String getUsernameFromToken(String token, boolean isRenew) {
        return getClaimFromToken(token, Claims::getSubject, isRenew);
    }

    public Date getExpirationDateFromToken(String token, boolean isRenew) {
        return getClaimFromToken(token, Claims::getExpiration, isRenew);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver, boolean isRenew) {
        String signingKey = isRenew ? REFRESH_SIGNING_KEY : SIGNING_KEY;

        final Claims claims = getAllClaimsFromToken(token, signingKey);
        return claimsResolver.apply(claims);
    }

    public Claims getAllClaimsFromToken(String token, String signingKey) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(signingKey))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token, boolean isRenew) {
        final Date expiration = getExpirationDateFromToken(token, isRenew);
        return expiration.before(new Date());
    }
}
