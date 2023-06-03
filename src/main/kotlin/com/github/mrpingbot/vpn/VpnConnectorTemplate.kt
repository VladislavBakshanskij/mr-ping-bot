package com.github.mrpingbot.vpn

interface VpnConnectorTemplate : VpnConnector {
    fun <T> execute(action: () -> T): T {
        start()
        try {
            return action()
        } catch (e: Exception) {
            throw e;
        } finally {
            close()
        }
    }
}
