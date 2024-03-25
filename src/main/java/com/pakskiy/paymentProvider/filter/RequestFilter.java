package com.pakskiy.paymentProvider.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletRequestWrapper;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

//https://gist.github.com/kad9/b595910e1a659f91d199b910f699a2d2

@Component
public class RequestFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // Check if the request is HTTP
        if (request instanceof HttpServletRequest httpServletRequest) {

            // Get the request body
            String body = httpServletRequest.getReader().lines()
                    .reduce("", (accumulator, actual) -> accumulator + actual);

            // Modify the request body
            body = modifyRequestBody(body);

            // Create a new request wrapper with the modified body
            String finalBody = body;
            ServletRequestWrapper wrappedRequest = new ServletRequestWrapper(httpServletRequest) {
                @Override
                public ServletInputStream getInputStream() {
                    final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(finalBody.getBytes());
                    return new ServletInputStream() {
                        @Override
                        public boolean isFinished() {
                            return false;
                        }

                        @Override
                        public boolean isReady() {
                            return false;
                        }

                        @Override
                        public void setReadListener(ReadListener readListener) {

                        }

                        @Override
                        public int read() {
                            return byteArrayInputStream.read();
                        }
                    };
                }

                @Override
                public BufferedReader getReader() {
                    return new BufferedReader(new InputStreamReader(this.getInputStream(), StandardCharsets.UTF_8));
                }
            };

            // Continue the filter chain with the wrapped request
            chain.doFilter(wrappedRequest, response);
        } else {
            // Not an HTTP request, proceed with the original request
            chain.doFilter(request, response);
        }
    }

    // Method to modify the request body
    private String modifyRequestBody(String body) {
        // Example: Convert all text to uppercase
        return body.toUpperCase();
    }

    @Override
    public void init(FilterConfig filterConfig) {
        // Initialization logic
    }

    @Override
    public void destroy() {
        // Cleanup logic
    }
}
