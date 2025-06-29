package com.skillswap.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handle404(NoHandlerFoundException ex, Model model) {
        model.addAttribute("message", "Страница не найдена");
        return "404";
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public String handle403(Exception ex, Model model) {
        model.addAttribute("message", "Доступ запрещён");
        return "403";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model, HttpServletRequest request) {
        model.addAttribute("message", ex.getMessage());
        return "error";
    }

    // REST API: JSON-ответы
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleApiException(RuntimeException ex, HttpServletRequest request) {
        if (request.getRequestURI().startsWith("/api/")) {
            return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
        }
        return null;
    }

    public static class ApiError {
        public String error;
        public ApiError(String error) { this.error = error; }
        public String getError() { return error; }
    }
}