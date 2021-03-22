/*
 * Copyright (c) 2010-2019. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.iamcyw.tower.commandhandling;


import io.iamcyw.tower.common.HandlerExecutionException;

/**
 * Indicates that an exception has occurred while handling a command. Typically, this class is used to wrap checked
 * exceptions that have been thrown from a Command Handler while processing an incoming command.
 *
 * @author Allard Buijze
 * @since 1.3
 */
public class CommandExecutionException extends HandlerExecutionException {

    private static final long serialVersionUID = -4864350962123378098L;

    /**
     * Initializes the exception with given {@code message} and {@code cause}.
     *
     * @param message The message describing the exception
     * @param cause   The cause of the exception
     */
    public CommandExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Initializes the exception with given {@code message}, {@code cause} and an object providing application-specific
     * {@code details}.
     *
     * @param message The message describing the exception
     * @param cause   The cause of the exception
     * @param details An object providing more error details (may be {@code null})
     */
    public CommandExecutionException(String message, Throwable cause, Object details) {
        super(message, cause, details);
    }
}
