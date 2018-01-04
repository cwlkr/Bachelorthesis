package com.pubnub.api.managers;

import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.ReconnectionCallback;
import com.pubnub.api.enums.PNReconnectionPolicy;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.PNTimeResult;
import lombok.extern.slf4j.Slf4j;

import java.util.Timer;
import java.util.TimerTask;


@Slf4j
public class ReconnectionManager {

    private static final int INTERVAL = 3000;

    private ReconnectionCallback callback;
    private PubNub pubnub;

    /**
     * Timer for heartbeat operations.
     */
    private Timer timer;

    public ReconnectionManager(PubNub pubnubInstance) {
        this.pubnub = pubnubInstance;
    }

    public ReconnectionManager setReconnectionListener(ReconnectionCallback reconnectionCallback) {
        this.callback = reconnectionCallback;
        return this;
    }

    public void startPolling() {
        if (this.pubnub.getConfiguration().getReconnectionPolicy() == PNReconnectionPolicy.NONE) {
            log.warn("reconnection policy is disabled, please handle reconnection manually.");
            return;
