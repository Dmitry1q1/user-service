package com.mit.user.userservice.component;

import com.mit.user.userservice.model.Role;
import io.jsonwebtoken.*;
//import io.jsonwebtoken.impl.crypto.MacProvider;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {
    @Value("${security.jwt.token.secret-key:secretKey}")
//    private String secretKey = "KCkXtDAQa5wd+ztPksKizISaF4sW4YzCbFaPLfmv1U8=";
    private String secretKey;
    @Value("${security.jwt.token.expire-length:600000}")
    private long validityInMilliseconds; // 1h
    @Autowired
    private UserDetailsService userDetailsService;

    private String base64EncodedSecretKey;
    private Key key;

    @PostConstruct
    protected void init() {
        base64EncodedSecretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
//        byte[] keyBytes = Decoders.BASE64.decode(base64EncodedSecretKey);
//        key = Keys.hmacShaKeyFor(keyBytes);
        key = Keys.hmacShaKeyFor(secretKey.getBytes());
//        key = Keys.secretKeyFor(SignatureAlgorithm.HS256); //or HS384 or HS512
//        key = MacProvider.generateKey(SignatureAlgorithm.HS256);
//        key = Keys.hmacShaKeyFor(base64EncodedSecretKey.getBytes());
//        base64EncodedSecretKey = Encoders.BASE64.encode(key.getEncoded());
    }

    public String createToken(String username, List<Role> roles) {
        List<String> roleList = roles.stream().map(Role::getName).collect(Collectors.toList());
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", roleList);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        return Jwts.builder()//
                .setClaims(claims)//
                .setIssuedAt(now)//
                .setExpiration(validity)//
//                .signWith(SignatureAlgorithm.HS256, secretKey)//
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
//        return Jwts.parser().setSigningKey(base64EncodedSecretKey).parseClaimsJws(token).getBody().getSubject();
        return Jwts.parserBuilder().setSigningKey(base64EncodedSecretKey).build().parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
//            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(base64EncodedSecretKey)
                    .build()
                    .parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
//            throw new InvalidJwtAuthenticationException("Expired or invalid JWT token");
        }
    }
}
