package dev.ostara.agent.servicediscovery

import dev.ostara.agent.config.ServiceDiscoveryProperties
import dev.ostara.agent.config.condition.ConditionalOnInternalEnabled
import dev.ostara.agent.model.DiscoveredInstanceDTO
import dev.ostara.agent.model.RegistrationRequestDTO
import dev.ostara.agent.service.TimeService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@ConditionalOnInternalEnabled
class InternalServiceDiscoveryHandlerImpl(
  private val timeService: TimeService,
  private val serviceDiscoveryProperties: ServiceDiscoveryProperties
) : ServiceDiscoveryHandler<ServiceDiscoveryProperties.ServiceDiscovery.Internal> {
  private val instanceStore = mutableMapOf<Pair<String, String>, Pair<RegistrationRequestDTO, Long>>()

  override fun supports(config: ServiceDiscoveryProperties.ServiceDiscovery): Boolean {
    return config is ServiceDiscoveryProperties.ServiceDiscovery.Internal
  }

  override fun discoverInstances(config: ServiceDiscoveryProperties.ServiceDiscovery.Internal): List<DiscoveredInstanceDTO> {
    return instanceStore.values.map { (request, _) ->
      request.toDiscoveredInstance()
    } + serviceDiscoveryProperties.internal.instances.toDiscoveredInstances()
  }

  @Scheduled(fixedDelay = 30_000)
  fun evictStale() {
    val now = timeService.currentTimeMillis()
    instanceStore.entries.removeIf {
      val (_, registeredAt) = it.value
      val diff = now - registeredAt
      diff > 60_000
    }
  }

  /**
   * Registers an instance.
   * Returns [true] if the instance already existed and was updated, and [false] if it didn't exist
   */
  fun doRegister(request: RegistrationRequestDTO): Boolean {
    val id = request.appName to request.host
    val instanceExists = instanceStore.containsKey(id)
    instanceStore[id] = request to timeService.currentTimeMillis()
    return instanceExists
  }

  fun doUnregister(request: RegistrationRequestDTO) {
    instanceStore.remove(request.appName to request.host)
  }

  companion object {
    private fun RegistrationRequestDTO.toDiscoveredInstance(): DiscoveredInstanceDTO {
      return DiscoveredInstanceDTO(
        appName = appName,
        id = "${appName}-${host}",
        name = host,
        url = managementUrl
      )
    }

    private fun Collection<RegistrationRequestDTO>.toDiscoveredInstances(): List<DiscoveredInstanceDTO> {
      return map { it.toDiscoveredInstance() }
    }
  }
}
