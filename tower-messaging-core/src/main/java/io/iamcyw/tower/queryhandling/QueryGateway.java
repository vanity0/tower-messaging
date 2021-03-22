/*
 * Copyright (c) 2010-2020. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.iamcyw.tower.queryhandling;


import io.iamcyw.tower.messaging.MessageDispatchInterceptorSupport;
import io.iamcyw.tower.messaging.responsetypes.ResponseType;
import io.iamcyw.tower.messaging.responsetypes.ResponseTypes;
import reactor.util.concurrent.Queues;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static io.iamcyw.tower.queryhandling.QueryMessage.queryName;

/**
 * Interface towards the Query Handling components of an application. This interface provides a friendlier API toward
 * the query bus.
 */
public interface QueryGateway extends MessageDispatchInterceptorSupport<QueryMessage<?, ?>> {

    /**
     * Sends given {@code query} over the {@link QueryBus}, expecting a response with
     * the given {@code responseType} from a single source. The query name will be derived from the provided {@code
     * query}. Execution may be asynchronous, depending on the QueryBus implementation.
     *
     * @param query        The {@code query} to be sent
     * @param responseType A {@link Class} describing the desired response type
     * @param <R>          The response class contained in the given {@code responseType}
     * @param <Q>          The query class
     * @return A {@link CompletableFuture} containing the query result as dictated by the given
     * {@code responseType}
     */
    default <R, Q> CompletableFuture<R> query(Q query, Class<R> responseType) {
        return query(queryName(query), query, responseType);
    }

    /**
     * Sends given {@code query} over the {@link QueryBus}, expecting a response with
     * the given {@code responseType} from a single source. Execution may be asynchronous, depending on the QueryBus
     * implementation.
     *
     * @param queryName    A {@link String} describing the query to be executed
     * @param query        The {@code query} to be sent
     * @param responseType The {@link ResponseType} used for this query
     * @param <R>          The response class contained in the given {@code responseType}
     * @param <Q>          The query class
     * @return A {@link CompletableFuture} containing the query result as dictated by the given
     * {@code responseType}
     */
    default <R, Q> CompletableFuture<R> query(String queryName, Q query, Class<R> responseType) {
        return query(queryName, query, ResponseTypes.instanceOf(responseType));
    }

    /**
     * Sends given {@code query} over the {@link QueryBus}, expecting a response in the
     * form of {@code responseType} from a single source. The query name will be derived from the provided {@code
     * query}. Execution may be asynchronous, depending on the QueryBus implementation.
     *
     * @param query        The {@code query} to be sent
     * @param responseType The {@link ResponseType} used for this query
     * @param <R>          The response class contained in the given {@code responseType}
     * @param <Q>          The query class
     * @return A {@link CompletableFuture} containing the query result as dictated by the given
     * {@code responseType}
     */
    default <R, Q> CompletableFuture<R> query(Q query, ResponseType<R> responseType) {
        return query(queryName(query), query, responseType);
    }

    /**
     * Sends given {@code query} over the {@link QueryBus}, expecting a response in the
     * form of {@code responseType} from a single source. Execution may be asynchronous, depending on the QueryBus
     * implementation.
     *
     * @param queryName    A {@link String} describing the query to be executed
     * @param query        The {@code query} to be sent
     * @param responseType The {@link ResponseType} used for this query
     * @param <R>          The response class contained in the given {@code responseType}
     * @param <Q>          The query class
     * @return A {@link CompletableFuture} containing the query result as dictated by the given
     * {@code responseType}
     */
    <R, Q> CompletableFuture<R> query(String queryName, Q query, ResponseType<R> responseType);

    /**
     * Sends given {@code query} over the {@link QueryBus}, expecting a response in the
     * form of {@code responseType} from several sources. The stream is completed when a {@code timeout} occurs or when
     * all results are received. The query name will be derived from the provided {@code query}. Execution may be
     * asynchronous, depending on the QueryBus implementation.
     *
     * @param query        The {@code query} to be sent
     * @param responseType The {@link ResponseType} used for this query
     * @param timeout      A timeout of {@code long} for the query
     * @param timeUnit     The selected {@link TimeUnit} for the given {@code timeout}
     * @param <R>          The response class contained in the given {@code responseType}
     * @param <Q>          The query class
     * @return A stream of results.
     */
    default <R, Q> Stream<R> scatterGather(Q query, ResponseType<R> responseType, long timeout, TimeUnit timeUnit) {
        return scatterGather(queryName(query), query, responseType, timeout, timeUnit);
    }

    /**
     * Sends given {@code query} over the {@link QueryBus}, expecting a response in the
     * form of {@code responseType} from several sources. The stream is completed when a {@code timeout} occurs or when
     * all results are received. Execution may be asynchronous, depending on the QueryBus implementation.
     *
     * @param queryName    A {@link String} describing the query to be executed
     * @param query        The {@code query} to be sent
     * @param responseType The {@link ResponseType} used for this query
     * @param timeout      A timeout of {@code long} for the query
     * @param timeUnit     The selected {@link TimeUnit} for the given {@code timeout}
     * @param <R>          The response class contained in the given {@code responseType}
     * @param <Q>          The query class
     * @return A stream of results.
     */
    <R, Q> Stream<R> scatterGather(String queryName, Q query, ResponseType<R> responseType, long timeout, TimeUnit timeUnit);

    /**
     * Sends given {@code query} over the {@link QueryBus} and returns result containing initial response and
     * incremental updates (received at the moment the query is sent, until it is cancelled by the caller or closed by
     * the emitting side).
     * <p>
     * <b>Note</b>: Any {@code null} results, on the initial result or the updates, wil lbe filtered out by the
     * QueryGateway. If you require the {@code null} to be returned for the initial and update results, we suggest using
     * the {@link QueryBus} instead.
     *
     * @param query               The {@code query} to be sent
     * @param initialResponseType The initial response type used for this query
     * @param updateResponseType  The update response type used for this query
     * @param <Q>                 The type of the query
     * @param <I>                 The type of the initial response
     * @param <U>                 The type of the incremental update
     * @return registration which can be used to cancel receiving updates
     * @see QueryBus#subscriptionQuery(SubscriptionQueryMessage)
     * @see QueryBus#subscriptionQuery(SubscriptionQueryMessage, int)
     */
    default <Q, I, U> SubscriptionQueryResult<I, U> subscriptionQuery(Q query, Class<I> initialResponseType, Class<U> updateResponseType) {
        return subscriptionQuery(queryName(query), query, initialResponseType, updateResponseType);
    }

    /**
     * Sends given {@code query} over the {@link QueryBus} and returns result containing initial response and
     * incremental updates (received at the moment the query is sent, until it is cancelled by the caller or closed by
     * the emitting side).
     * <p>
     * <b>Note</b>: Any {@code null} results, on the initial result or the updates, wil lbe filtered out by the
     * QueryGateway. If you require the {@code null} to be returned for the initial and update results, we suggest using
     * the {@link QueryBus} instead.
     *
     * @param queryName           A {@link String} describing query to be executed
     * @param query               The {@code query} to be sent
     * @param initialResponseType The initial response type used for this query
     * @param updateResponseType  The update response type used for this query
     * @param <Q>                 The type of the query
     * @param <I>                 The type of the initial response
     * @param <U>                 The type of the incremental update
     * @return registration which can be used to cancel receiving updates
     * @see QueryBus#subscriptionQuery(SubscriptionQueryMessage)
     * @see QueryBus#subscriptionQuery(SubscriptionQueryMessage, int)
     */
    default <Q, I, U> SubscriptionQueryResult<I, U> subscriptionQuery(String queryName, Q query, Class<I> initialResponseType, Class<U> updateResponseType) {
        return subscriptionQuery(queryName, query, ResponseTypes.instanceOf(initialResponseType),
                                 ResponseTypes.instanceOf(updateResponseType));
    }

    /**
     * Sends given {@code query} over the {@link QueryBus} and returns result containing initial response and
     * incremental updates (received at the moment the query is sent, until it is cancelled by the caller or closed by
     * the emitting side).
     * <p>
     * <b>Note</b>: Any {@code null} results, on the initial result or the updates, wil lbe filtered out by the
     * QueryGateway. If you require the {@code null} to be returned for the initial and update results, we suggest using
     * the {@link QueryBus} instead.
     *
     * @param query               The {@code query} to be sent
     * @param initialResponseType The initial response type used for this query
     * @param updateResponseType  The update response type used for this query
     * @param <Q>                 The type of the query
     * @param <I>                 The type of the initial response
     * @param <U>                 The type of the incremental update
     * @return registration which can be used to cancel receiving updates
     * @see QueryBus#subscriptionQuery(SubscriptionQueryMessage)
     * @see QueryBus#subscriptionQuery(SubscriptionQueryMessage, int)
     */
    default <Q, I, U> SubscriptionQueryResult<I, U> subscriptionQuery(Q query, ResponseType<I> initialResponseType, ResponseType<U> updateResponseType) {
        return subscriptionQuery(queryName(query), query, initialResponseType, updateResponseType);
    }

    /**
     * Sends given {@code query} over the {@link QueryBus} and returns result containing initial response and
     * incremental updates (received at the moment the query is sent, until it is cancelled by the caller or closed by
     * the emitting side).
     * <p>
     * <b>Note</b>: Any {@code null} results, on the initial result or the updates, will be filtered out by the
     * QueryGateway. If you require the {@code null} to be returned for the initial and update results, we suggest using
     * the {@link QueryBus} instead.
     *
     * @param queryName           a {@link String} describing query to be executed
     * @param query               the {@code query} to be sent
     * @param initialResponseType the initial response type used for this query
     * @param updateResponseType  the update response type used for this query
     * @param <Q>                 the type of the query
     * @param <I>                 the type of the initial response
     * @param <U>                 the type of the incremental update
     * @return registration which can be used to cancel receiving updates
     * @see QueryBus#subscriptionQuery(SubscriptionQueryMessage)
     * @see QueryBus#subscriptionQuery(SubscriptionQueryMessage, int)
     */
    default <Q, I, U> SubscriptionQueryResult<I, U> subscriptionQuery(String queryName, Q query, ResponseType<I> initialResponseType, ResponseType<U> updateResponseType) {
        return subscriptionQuery(queryName, query, initialResponseType, updateResponseType, Queues.SMALL_BUFFER_SIZE);
    }

    /**
     * Sends given {@code query} over the {@link QueryBus} and returns result containing initial response and
     * incremental updates (received at the moment the query is sent, until it is cancelled by the caller or closed by
     * the emitting side).
     * <p>
     * <b>Note</b>: Any {@code null} results, on the initial result or the updates, wil lbe filtered out by the
     * QueryGateway. If you require the {@code null} to be returned for the initial and update results, we suggest using
     * the {@link QueryBus} instead.
     *
     * @param queryName           a {@link String} describing query to be executed
     * @param query               the {@code query} to be sent
     * @param initialResponseType the initial response type used for this query
     * @param updateResponseType  the update response type used for this query
     * @param updateBufferSize    the size of buffer which accumulates updates before subscription to the flux
     *                            is made
     * @param <Q>                 the type of the query
     * @param <I>                 the type of the initial response
     * @param <U>                 the type of the incremental update
     * @return registration which can be used to cancel receiving updates
     * @see QueryBus#subscriptionQuery(SubscriptionQueryMessage)
     * @see QueryBus#subscriptionQuery(SubscriptionQueryMessage, int)
     */
    <Q, I, U> SubscriptionQueryResult<I, U> subscriptionQuery(String queryName, Q query, ResponseType<I> initialResponseType, ResponseType<U> updateResponseType, int updateBufferSize);

}
