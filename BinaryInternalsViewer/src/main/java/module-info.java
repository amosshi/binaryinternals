/*
 * Copyright 2022 Binary Internals.
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

module org.binaryinternals.app {
    requires static com.github.spotbugs.annotations;
    requires static org.binaryinternals.commonlib;

    exports org.binaryinternals.app;
    exports org.binaryinternals.binaryviewer;
    exports org.binaryinternals.plugin;
}
