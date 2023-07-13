package dev.ostara.agent.controller

import dev.ostara.agent.service.ServiceDiscoveryService
import dev.ostara.agent.util.API_PREFIX
import dev.ostara.agent.util.IGNORED_REQUEST_HEADERS
import dev.ostara.agent.util.IGNORED_RESPONSE_HEADERS
import dev.ostara.agent.util.ensureSuffix
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import java.net.URI
import java.util.stream.Collectors

@RestController
@RequestMapping(ProxyController.PATH)
class ProxyController(
  private val restTemplate: RestTemplate,
  private val serviceDiscoveryService: ServiceDiscoveryService
) {
  @RequestMapping("/{instanceId}/**")
  fun doCall(
    @PathVariable instanceId: String,
    request: HttpServletRequest,
    response: HttpServletResponse
  ) {
    val instance = serviceDiscoveryService.getDiscoveredInstanceById(instanceId)
    if (instance == null) {
      response.status = HttpStatus.NOT_FOUND.value()
      return
    }

    val instanceUrl = instance.url!!
    val url =
      (instanceUrl.ensureSuffix("/") + request.requestURI.replace("$PATH/$instanceId", "")
        .removePrefix("/"))
        .removeSuffix("/")
    val body = if (request.method in listOf(HttpMethod.POST.name(), HttpMethod.PUT.name())) {
      request.reader.lines()
        .collect(Collectors.joining(System.lineSeparator()))
    } else {
      null
    }

    val headers = HttpHeaders()
    request.headerNames
      .toList()
      .filter {
        it !in IGNORED_REQUEST_HEADERS
      }
      .toSet()
      .forEach {
        headers.addAll(it, request.getHeaders(it).toList())
      }

    val requestEntity = RequestEntity(
      body,
      headers,
      HttpMethod.valueOf(request.method),
      URI(url),
    )
    val instanceResponse = restTemplate.exchange(requestEntity, String::class.java)
    response.status = instanceResponse.statusCode.value()
    if (instanceResponse.hasBody()) {
      response
        .writer
        .write(instanceResponse.body)
    }
    instanceResponse.headers.filter { (key, _) ->
      key !in IGNORED_RESPONSE_HEADERS
    }
      .forEach {(key, values) ->
        values.forEach { value ->
          response.addHeader(key, value)
        }

      }
  }

  companion object {
    const val PATH = "$API_PREFIX/proxy"
  }
}
