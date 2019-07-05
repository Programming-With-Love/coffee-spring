/*
 * Copyright 2004, 2005, 2006 Acegi Technology Pty Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package site.zido.coffee.auth.exceptions;

/**
 * 认证错误
 *
 * @author zido
 */
public abstract class AbstractAuthenticationException extends RuntimeException {

    public AbstractAuthenticationException(String msg, Throwable t) {
        super(msg, t);
    }

    public AbstractAuthenticationException(String msg) {
        super(msg);
    }

}
