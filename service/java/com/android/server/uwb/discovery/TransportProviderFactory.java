/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.server.uwb.discovery;

import android.content.AttributionSource;
import android.content.Context;

import com.android.server.uwb.discovery.TransportClientProvider.TransportClientCallback;
import com.android.server.uwb.discovery.TransportServerProvider.TransportServerCallback;
import com.android.server.uwb.discovery.ble.GattTransportClientProvider;
import com.android.server.uwb.discovery.ble.GattTransportServerProvider;
import com.android.server.uwb.discovery.info.DiscoveryInfo;

import java.util.concurrent.Executor;

/** Factory for creating TransportProvider. */
public class TransportProviderFactory {
    private static final String TAG = TransportProviderFactory.class.getSimpleName();

    /**
     * Create a TransportServerProvider.
     *
     * @param attributionSource Attribution Source.
     * @param context Context.
     * @param discoveryInfo Info of the discovery request.
     * @param transportServerCallback callback for transport server events.
     */
    public static TransportServerProvider createServer(
            AttributionSource attributionSource,
            Context context,
            DiscoveryInfo discoveryInfo,
            TransportServerCallback transportServerCallback)
            throws AssertionError {

        switch (discoveryInfo.transportType) {
            case BLE:
                return new GattTransportServerProvider(
                        attributionSource, context, transportServerCallback);
            default:
                throw new AssertionError(
                        "Failed to create TransportServerProvider due to invalid transport type:"
                                + " "
                                + discoveryInfo.transportType);
        }
    }

    /**
     * Create a TransportClientProvider.
     *
     * @param attributionSource Attribution Source.
     * @param context Context.
     * @param executor Executor.
     * @param discoveryInfo Info of the discovery request.
     * @param transportClientCallback callback for transport client events.
     */
    public static TransportClientProvider createClient(
            AttributionSource attributionSource,
            Context context,
            Executor executor,
            DiscoveryInfo discoveryInfo,
            TransportClientCallback transportClientCallback)
            throws IllegalArgumentException, AssertionError {

        switch (discoveryInfo.transportType) {
            case BLE:
                if (!discoveryInfo.transportClientInfo.isPresent()) {
                    throw new IllegalArgumentException(
                            "Failed to create GattTransportClientProvider due to empty"
                                    + " transportClientInfo in discoveryInfo:"
                                    + discoveryInfo);
                }
                return new GattTransportClientProvider(
                        attributionSource,
                        context,
                        executor,
                        discoveryInfo.transportClientInfo.get(),
                        transportClientCallback);
            default:
                throw new AssertionError(
                        "Failed to create TransportClientProvider due to invalid transport type:"
                                + " "
                                + discoveryInfo.transportType);
        }
    }
}
