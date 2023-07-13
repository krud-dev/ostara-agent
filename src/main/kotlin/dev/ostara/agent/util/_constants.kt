package dev.ostara.agent.util

const val API_PREFIX = "/api/v1"
const val AGENT_KEY_HEADER = "X-Ostara-Key"
const val CONFIGURATION_PREFIX = "ostara.agent"

const val PROXY_INSTANCE_ID_HEADER = "X-Ostara-InstanceId"
val IGNORED_REQUEST_HEADERS = listOf(
    AGENT_KEY_HEADER,
    PROXY_INSTANCE_ID_HEADER,
    "Host",
    "Connection",
    "Keep-Alive",
    "Proxy-Authenticate",
    "Proxy-Authorization",
    "TE",
    "Trailer",
    "Transfer-Encoding",
    "Upgrade"
)

val IGNORED_RESPONSE_HEADERS = listOf(
    "Content-Length",
    "Transfer-Encoding",
    "Connection",
)