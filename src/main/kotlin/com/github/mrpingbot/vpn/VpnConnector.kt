package com.github.mrpingbot.vpn

interface VpnConnector {
    fun start();

    fun close();
}