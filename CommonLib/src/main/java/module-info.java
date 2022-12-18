/*
 * Copyright 2022 Free Internals.
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

/**
 * @author Amos SHI
 */
module org.freeinternals.commonlib {
    requires java.base;
    requires transitive java.desktop;
    requires transitive java.logging;
    requires static com.github.spotbugs.annotations;
    
    exports org.freeinternals.commonlib.core;
    exports org.freeinternals.commonlib.ui;
}
