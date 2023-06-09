package eom.demo.ejh_board.util

import eom.demo.ejh_board.exception.InvalidInputException
import eom.demo.ejh_board.exception.NotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ServerWebExchange

@RestControllerAdvice
class GlobalExceptionHandler {
    private val logger: Logger= LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException::class)
    @ResponseBody
    fun handleNotFoundException(exchange: ServerWebExchange, ex: Exception): HttpError =
        createHttpError(HttpStatus.NOT_FOUND, exchange.request, exchange.response, ex)

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidInputException::class)
    @ResponseBody
    fun handleInvalidInputException(exchange: ServerWebExchange, ex: Exception): HttpError =
        createHttpError(HttpStatus.UNPROCESSABLE_ENTITY, exchange.request, exchange.response, ex)

    private fun createHttpError(status: HttpStatus, request: ServerHttpRequest, response: ServerHttpResponse, ex: Exception): HttpError {
        val path = request.path.pathWithinApplication().value()
        var message = ex.localizedMessage

        logger.debug("Returning HTTP status: {} for path: {}, message: {}, cause:{}", status, path, message,ex.cause)
        response.statusCode = status
        return HttpError(path, status, message)
    }
}
