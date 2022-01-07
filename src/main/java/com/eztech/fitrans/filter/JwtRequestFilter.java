package com.eztech.fitrans.filter;

import com.eztech.fitrans.dto.response.ErrorMessageDTO;
import com.eztech.fitrans.model.ActionLog;
import com.eztech.fitrans.repo.ActionLogRepository;
import com.eztech.fitrans.service.UserDetailsServiceImpl;
import com.eztech.fitrans.util.DataUtils;
import com.eztech.fitrans.util.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Component
//@Profile(Profiles.JWT_AUTH)
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
    private final UserDetailsServiceImpl jwtUserDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final ActionLogRepository actionLogRepository;

    @Autowired
    @Qualifier("ayncTaskExecutor")
    private TaskExecutor taskExecutor;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("Authorization");
        long startTime = System.currentTimeMillis();
        String username = null;
        String jwtToken = null;
        // JWT Token is in the form "Bearer token". Remove Bearer word and get
        // only the Token
        if (requestTokenHeader != null) {
            if (requestTokenHeader.startsWith("Bearer ")) {
                jwtToken = requestTokenHeader.substring(7);
                try {
                    username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                } catch (IllegalArgumentException e) {
                    logger.warn("Unable to get JWT Token");
                } catch (ExpiredJwtException e) {
                    logger.warn("JWT Token has expired");
                }
            } else {
                logger.warn("JWT Token does not begin with Bearer String");
            }
        }

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetails) {
                logger.info("===SecurityContextHolder getPrincipal UserDetails: " + ((UserDetails) principal).getUsername());
            } else {
                logger.info("===SecurityContextHolder getPrincipal: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            }

        }
        // Once we get the token validate it.
        if (username != null
                && (SecurityContextHolder.getContext().getAuthentication() == null
                || "anonymousUser".equalsIgnoreCase((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal()))) {
            UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);
            // if token is valid configure Spring Security to manually set
            // authentication
            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // After setting the Authentication in the context, we specify
                // that the current user is authenticated. So it passes the
                // Spring Security Configurations successfully.
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        //Lưu action log
        ContentCachingResponseWrapper responseCacheWrapperObject = new ContentCachingResponseWrapper((HttpServletResponse) response);
        chain.doFilter(request, responseCacheWrapperObject);
        responseCacheWrapperObject.copyBodyToResponse();
        String method = request.getMethod();
        if (!"GET".equalsIgnoreCase(method)) {
            String requestUri = request.getRequestURI();
            int httpStatus = response.getStatus();

            byte[] responseArray = responseCacheWrapperObject.getContentAsByteArray();
            String responseStr = new String(responseArray, "UTF-8");

            ErrorMessageDTO errorMessageDTO = DataUtils.jsonToObject(responseStr, ErrorMessageDTO.class);

            ActionLog actionLog = ActionLog.builder()
                    .username(username)
                    .url(requestUri)
                    .method(method)
                    .description("")
                    .httpStatus(httpStatus)
                    .responseContent((responseStr != null && responseStr.length() <= 4000) ? responseStr : "")
                    .responseCode(errorMessageDTO != null && errorMessageDTO.getCode() != null ? errorMessageDTO.getCode().name() : "00")
                    .processTime(LocalDateTime.now())
                    .duration(System.currentTimeMillis() - startTime)
                    .build();

            taskExecutor.execute(() -> actionLogRepository.save(actionLog));
        }
    }
}