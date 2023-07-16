package dev.ostara.agent.controller

import dev.ostara.agent.model.RegistrationRequestDTO
import dev.ostara.agent.servicediscovery.InternalServiceDiscoveryHandlerImpl
import dev.ostara.agent.service.ServiceDiscoveryService
import dev.ostara.agent.test.IntegrationTest
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.web.client.RestTemplate

@IntegrationTest
class ProxyControllerIntegrationTest {

  @Autowired
  private lateinit var serviceDiscoveryService: ServiceDiscoveryService

  @Autowired
  private lateinit var internalServiceDiscoveryHandlerImpl: InternalServiceDiscoveryHandlerImpl

  @Autowired
  private lateinit var mockMvc: MockMvc

  @MockBean
  private lateinit var restTemplate: RestTemplate

  @Test
  fun `doCall should return 404 if instance id isn't found`() {
    mockMvc.get(
      "/api/v1/proxy/someApp-someHost/health"
    ).andExpect {
      status { isNotFound() }
    }
  }

  @Test
  @DirtiesContext
  fun `doCall should relay call if instance exists`() {
    val registration = RegistrationRequestDTO(
      "someApp",
      "someHost",
      "http://localhost:13333/actuator"
    )
    internalServiceDiscoveryHandlerImpl.doRegister(
      registration
    )
    serviceDiscoveryService.runDiscovery()

    whenever(
      restTemplate.exchange(
        any(),
        eq(String::class.java)
      )
    ).thenReturn(ResponseEntity.ok("OK"))

    mockMvc.get(
      "/api/v1/proxy/someApp-someHost/health"
    ).andExpect {
      status { isOk() }
      content { string("OK") }
    }
  }
}
