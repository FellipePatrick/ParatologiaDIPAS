package cog.com.sic.frontend.core;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.view.RedirectView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public RedirectView handleNotFound(HttpServletRequest request, NoHandlerFoundException ex) {
        return new RedirectView("/");
    }

    @ExceptionHandler(Exception.class)
    public RedirectView handleGenericException(HttpServletRequest request, Exception ex) {
        return new RedirectView("/");
    }
}