package com.github.mrpingbot.vpn

import com.github.mrpingbot.utils.VPN_CONNECTOR_CONFIG_PROPERTIES_PREFIX
import com.github.mrpingbot.utils.VPN_CONNECTOR_CONFIG_PROPERTIES_TYPE_NAME
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    prefix = VPN_CONNECTOR_CONFIG_PROPERTIES_PREFIX,
    name = [VPN_CONNECTOR_CONFIG_PROPERTIES_TYPE_NAME],
    havingValue = "mock",
    matchIfMissing = true
)
class MockVpnConnectorTemplate : VpnConnectorTemplate {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(MockVpnConnectorTemplate::class.java)
    }

    override fun start() {
        logger.info("Mock vpn started")
    }

    override fun close() {
        logger.info("Mock vpn stopped")
    }
}