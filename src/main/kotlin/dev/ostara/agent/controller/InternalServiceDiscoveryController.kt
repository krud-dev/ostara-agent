package dev.ostara.agent.controller

import dev.ostara.agent.config.condition.ConditionalOnInternalEnabled
import dev.ostara.agent.model.RegistrationRequestDTO
import dev.ostara.agent.servicediscovery.InternalServiceDiscoveryHandlerImpl
import dev.ostara.agent.util.API_PREFIX
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("$API_PREFIX/internal/service-discovery")
@ConditionalOnInternalEnabled
class InternalServiceDiscoveryController(
  private val internalServiceDiscoveryHandlerImpl: InternalServiceDiscoveryHandlerImpl
) {
  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  fun register(@RequestBody request: RegistrationRequestDTO): ResponseEntity<Unit> {
    val instanceExisted = internalServiceDiscoveryHandlerImpl.doRegister(request)
    return if (instanceExisted) {
      ResponseEntity.noContent().build()
    } else {
      ResponseEntity.status(HttpStatus.CREATED).build()
    }
  }

  @PostMapping("/deregister")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  fun deregister(@RequestBody request: RegistrationRequestDTO) {
    internalServiceDiscoveryHandlerImpl.doUnregister(request)
  }
}
